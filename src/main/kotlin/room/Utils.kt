package com.astrainteractive.astratemplate.auto_module.api

import java.lang.reflect.Parameter
import java.lang.reflect.Type
import java.sql.ResultSet


inline fun <reified T> catching(crossinline block: () -> T): T? = try {
    block()
} catch (e: java.lang.Exception) {
    e.printStackTrace()
    null
}

public inline fun <R : Any, C : MutableCollection<in R>> ResultSet.mapNotNullTo(
    destination: C,
    rs: (ResultSet) -> R?,
): C {
    forEach { element -> rs(element)?.let { destination.add(it) } }
    return destination
}

public inline fun <R : Any> ResultSet.mapNotNull(rs: (ResultSet) -> R?): List<R> {
    return mapNotNullTo(ArrayList<R>(), rs)
}

inline fun ResultSet.forEach(rs: (ResultSet) -> Unit) {
    while (this.next()) {
        rs(this)
    }
}
val Type.fullPackageName: String
    get() = toString().replace("class ", "")

fun ResultSet.getAs(parameter: Parameter, name: String): Any {
    return when (parameter.type) {
        Int::class.java -> getInt(name)
        String::class.java -> getString(name)
        Double::class.java -> getDouble(name)
        Float::class.java -> getFloat(name)
        ByteArray::class.java -> getBytes(name)
        Byte::class.java -> getByte(name)
        Long::class.java -> getLong(name)
        Boolean::class.java -> getBoolean(name)
        else -> throw Exception("Type could not be resolved")
    }
}
