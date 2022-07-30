package api

import api.*
import com.astrainteractive.astratemplate.auto_module.api.*
import org.jetbrains.kotlin.com.google.common.util.concurrent.Futures.catching
import java.lang.reflect.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement



class SocketProxyInvocationHandler(private val lazyConnection: () -> Connection?) :
    InvocationHandler {
    private inline fun <reified T> T?.notNull(msg: String): T = checkNotNull(this) {
        msg
    }

    private fun parseQuery(method: Method, args: Array<out Any>?): String? {
        val queryAnnotation: Query? =
            method.annotations.firstNotNullOfOrNull { it as? Query }
        var query = queryAnnotation?.query
        val annotations = method.parameterAnnotations
        val annotationWithValue = annotations.mapIndexedNotNull { i, annotations ->
            annotations.firstNotNullOfOrNull { it as? Param }?.name
        }.mapIndexed { i, name ->
            name to args?.getOrNull(i)
        }.filter { it.second != null }.forEach {
            query = query?.replace(
                ":${it.first}",
                it.second?.toString() ?: throw Exception("Values notated with Param should contain value")
            )
        }
        return query
    }

    fun resolveReturnedClass(method: Method): Class<*> {
        val outerType: Type = method.genericReturnType //<ProxyTask>
        if (outerType !is ParameterizedType) throw Exception("Returned objects should be wrapped with IProxyTask")
        var isList = false

        val innerType = when (val innerOuterType = outerType.actualTypeArguments[0]) {
            /** It probably is a [List] or [IProxyTask] **/
            /** It is a [List] **/
            is ParameterizedType -> {
                isList = true
                innerOuterType.actualTypeArguments[0]
            }
            else -> {
                innerOuterType
            }
        }
        val packageName = innerType.fullPackageName
        val clazz =
            catching { Class.forName(packageName) } ?: throw Exception("Class ${innerType.fullPackageName} not found")
        return clazz
    }

    /**
     * [proxy] - наш прокси
     * [method] - наша функция
     * [args] - аргументы функции
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val connection = lazyConnection()
        val query = parseQuery(method, args)
        val clazz = resolveReturnedClass(method)

        if (query != null) return queryTask(clazz, query, lazyConnection())

        val insertAnnotation: Insert? =
            method.annotations.firstNotNullOfOrNull { it as? Insert }
        if (insertAnnotation != null) return ProxyTask { insertTask(args, lazyConnection()) }

        return null
    }



    private fun insertTask(args: Array<out Any>?, connection: Connection?): Long? {
        if (args?.size != 1) throw Exception("Functions notated with Insert should contain only one argument")
        val entity = args.first()

        val oah = ObjectAnnotationHolder(entity::class.java.fullPackageName)
        val fields = oah.annotatedFields(true)
        val keys = fields.map { it.annotations.firstNotNullOf { it as? ColumnInfo }.name }.joinToString(",", "(", ")")

        val values = fields.map {
            val field = it.annotations.firstNotNullOf { it as? ColumnInfo }.field
            var value = oah.getDeclaredFieldValue(field, entity)
            if (value is String) "\"$value\""
            else value
        }.joinToString(",", "(", ")")

        val table = oah.entity?.tableName
        val query = "INSERT INTO $table $keys VALUES $values "
        println(query)
        val prepared =
            connection?.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).apply { this?.executeUpdate() }
        return prepared?.generatedKeys?.getLong(1)
    }

    private fun queryTask(clazz: Class<*>, query: String, connection: Connection?): ProxyTask<Any> {
        clazz.annotations.firstNotNullOfOrNull { it as? Entity }
            ?: throw Exception("You should provide Entity annotation")


        val constructor = clazz.constructors[0]
        val constructorParameters = constructor.parameters
        val fieldWithInfo =
            constructorParameters.map { it to it.annotations.firstNotNullOfOrNull { it as? ColumnInfo } }
        println(query)
        val resultSet = connection?.createStatement()?.executeQuery(query)
        val res = resultSet?.mapNotNull { rs ->
            val _constructor = fieldWithInfo.map { (field, columnInfo) ->
                columnInfo?.let { rs.getAs(field, it.name) }
            }
            val varargs = _constructor.toTypedArray()
            constructor.newInstance(*varargs)
        }
        return ProxyTask { res?.toList() }
    }

}


