package com.astrainteractive.astratemplate.auto_module.api

import kotlinx.coroutines.CoroutineScope

/**
 * Wrapper to create async calls
 */
interface IProxyTask<out T> {
    val block: suspend () -> T?
    suspend fun await() = block()
}

class ProxyTask<T>(override val block: suspend () -> T?) : IProxyTask<T>