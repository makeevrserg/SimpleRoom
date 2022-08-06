package com.astrainteractive.astratemplate.auto_module.retrofit

import com.astrainteractive.astratemplate.auto_module.api.*
import org.jetbrains.kotlin.com.google.gson.Gson
import java.io.OutputStreamWriter
import java.lang.reflect.*
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Connection
import java.sql.Statement
import java.util.Arrays


class RetrofitProxyInvocationHandler(private val configuration: Retrofit.Configuration) :
    InvocationHandler {
    private inline fun <reified T> T?.notNull(msg: String): T = checkNotNull(this) {
        msg
    }

    inline fun <reified T> getTransferAnnotation(method: Method): T? =
        method.annotations.firstNotNullOfOrNull { it as? T }

    inline fun <reified T> annotationWithIndex(parameterAnnotations: kotlin.Array<out Array<out Annotation>>): List<Pair<Int, T & Any>> {
        var i = -1
        return parameterAnnotations.mapNotNull {
            i++
            it.firstNotNullOfOrNull { it as? T }?.let { i to it }
        }
    }

    fun <T> annotationsToParam(indexed: List<Pair<Int, T>>, args: Array<out Any>?): List<Pair<Any, T>> {
        return indexed.mapNotNull { (i, annotation) ->
            args?.getOrNull(i)?.let { it to annotation }
        }
    }

    /**
     * [proxy] - наш прокси
     * [method] - наша функция
     * [args] - аргументы функции
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val get = getTransferAnnotation<Get>(method)
        val path = get?.path
        val query = annotationsToParam(annotationWithIndex<Query>(method.parameterAnnotations), args).joinToString(
            "&",
            prefix = "?"
        ) {
            "${it.second.field}=${it.first}"
        }
        val body = annotationsToParam(annotationWithIndex<Body>(method.parameterAnnotations), args).firstOrNull()

        return ProxyTask {
            val url = URL(configuration.baseUrl + path + query)
            val connection = url.openConnection() as HttpURLConnection
            if (get != null)
                connection.requestMethod = Get.METHOD_NAME
            configuration.headers().forEach(connection::setRequestProperty)
            body?.let {
                connection.doOutput = true
                connection.outputStream.also { os ->
                    OutputStreamWriter(os, "UTF-8").apply {
                        write(configuration.decoderFactory(it.first))
                        flush()
                        close()
                    }
                    os.close()
                }
            }

            val json = connection.run {
                connect()
                val json = this.url.readText()
                disconnect()
                json
            }
            val name =
                ((method.genericReturnType as ParameterizedType).actualTypeArguments[0] as ParameterizedType).actualTypeArguments[0].fullPackageName
            val clazz = Class.forName(name)
            val decoded = configuration.converterFactory.invoke(json, clazz)
            val code = connection.responseCode
            val message = connection.responseMessage
            Response(message, code, decoded)
        }
    }


}


