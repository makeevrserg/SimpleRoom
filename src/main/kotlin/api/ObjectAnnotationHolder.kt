package com.astrainteractive.astratemplate.auto_module.api

import api.ColumnInfo
import api.Entity
import api.PrimaryKey
import java.lang.reflect.Field
import java.lang.reflect.Parameter

class ObjectAnnotationHolder(val clazz:Class<*>) {

    constructor(fullPackageName: String):this(Class.forName(fullPackageName))

    val entity: Entity?
        get() = clazz.annotations.firstNotNullOfOrNull { it as? Entity }

    fun getDeclaredFieldValue(name: String, origin: Any): Any {
        val field = clazz.getDeclaredField(name)
        field.isAccessible = true
        val value = field.get(origin)
        field.isAccessible = false
        return value
    }

    fun <T> objectAnnotation(name: String): T? {
        val indexOf =
            clazz.constructors[0].parameters.indexOfFirst { it.annotations.firstNotNullOfOrNull { it as? ColumnInfo }?.name == name }
        return objectAnnotation(indexOf)
    }

    fun <T> objectAnnotation(i: Int) =
        clazz.constructors[0].parameters.getOrNull(i)?.annotations?.firstNotNullOfOrNull { it as? T }

    fun annotatedFields(unique: Boolean = false): List<Parameter> {
        return clazz.constructors[0].parameters.filter {
            val columnInfo = it.annotations.firstNotNullOfOrNull { it as? ColumnInfo }
            val primaryKey = it.annotations.firstNotNullOfOrNull { it as? PrimaryKey }
            columnInfo != null && ( if (unique) primaryKey == null else true)
        }
    }

    val declaredFields: List<Field>
        get() = clazz.declaredFields.toList()
}