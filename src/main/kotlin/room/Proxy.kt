package room

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

class SocketProxy<V : InvocationHandler> private constructor(private val invocationHandlerBuilder: () -> V) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader, arrayOf(clazz),
            invocationHandlerBuilder()
        ) as T
    }

    class Builder<V : InvocationHandler>(private val invocationHandlerBuilder: () -> V) {
        fun build(): SocketProxy<V> = SocketProxy(invocationHandlerBuilder)
    }
}

