package com.astrainteractive.astratemplate.auto_module.retrofit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Get(val path: String) {
    companion object {
        const val METHOD_NAME = "GET"
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Query(val field: String)