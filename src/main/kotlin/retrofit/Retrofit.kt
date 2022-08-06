package com.astrainteractive.astratemplate.auto_module.retrofit

import com.astrainteractive.astratemplate.auto_module.api.IProxyTask
import java.lang.reflect.Proxy

class Retrofit(private val configuration: Configuration.() -> Unit) {
    data class Configuration(
        var baseUrl: String = "",
        var headers: () -> Map<String, String> = { emptyMap() },
        var converterFactory: (String?, Class<*>) -> Any? = { _, _ -> },
        var decoderFactory: (Any?) -> String = { "" },
    )

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(clazz: Class<T>): T {
        val configuration = Configuration().apply { configuration() }
        return Proxy.newProxyInstance(
            clazz.classLoader, arrayOf(clazz),
            RetrofitProxyInvocationHandler(configuration)
        ) as T
    }
}

