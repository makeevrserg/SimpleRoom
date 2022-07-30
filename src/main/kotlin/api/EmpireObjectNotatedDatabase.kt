package api

import com.astrainteractive.astratemplate.auto_module.api.ObjectAnnotationHolder
import com.astrainteractive.astratemplate.auto_module.api.IProxyTask
import com.astrainteractive.astratemplate.auto_module.api.ProxyTask
import com.astrainteractive.astratemplate.auto_module.api.fullPackageName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Parameter
import java.sql.Connection
import java.sql.DriverManager

/**
 * Empire Object Notated Database
 */
object EOND {

    private fun createTable(connection: Connection?, clazz: Class<*>): IProxyTask<Boolean> {
        fun resolveType(parameter: Parameter): String {
            return when (parameter.type) {
                Int::class.java -> "INTEGER"
                String::class.java -> "TEXT"
                Double::class.java -> "REAL"
                Float::class.java -> "REAL"
                ByteArray::class.java -> "VARBINARY"
                Byte::class.java -> "BIT"
                Long::class.java -> "INTEGER"
                Boolean::class.java -> "BIT"
                else -> throw Exception("Type could not be resolved")
            }
        }

        println(clazz.fullPackageName)
        val oah = ObjectAnnotationHolder(clazz.fullPackageName)

        val fields = oah.annotatedFields()
        val keys = fields.map {
            val annotation = it.annotations.firstNotNullOf { it as? ColumnInfo }
            val primaryKey = it.annotations.firstNotNullOfOrNull { it as? PrimaryKey }
            buildList {
                add("${annotation.name} ${resolveType(it)}")

                if (primaryKey != null) add("PRIMARY KEY")
                if (primaryKey?.autoIncrement == true) add("AUTOINCREMENT")
                if (!annotation.nullable) add("NOT NULL")
                if (annotation.unique) add("UNIQUE")
            }.joinToString(" ")
        }.joinToString(",", "(", ")")
        val query = "CREATE TABLE IF NOT EXISTS ${oah.entity?.tableName} $keys"
        println(query)
        return ProxyTask { connection?.prepareStatement(query)?.execute() }
    }

    fun <T : Any> databaseBuilder(
        databaseName: String,
        clazz: Class<T>,
        vararg entities: Class<*>,
    ): T {
        Class.forName("org.sqlite.JDBC");
        val connection = DriverManager.getConnection(("jdbc:sqlite:$databaseName.db"))
        val lazyConnection = { connection }
        val lazyInvocation = { SocketProxyInvocationHandler(lazyConnection) }
        val proxy = SocketProxy.Builder(lazyInvocation).build()
        val api = proxy.create(clazz)
        runBlocking {
            entities.map {
                async { createTable(connection, it).await() }
            }.awaitAll()
        }
        return api
    }
}