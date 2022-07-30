package com.astrainteractive.astratemplate.auto_module.dao

import api.Insert
import api.Param
import api.Query
import com.astrainteractive.astratemplate.auto_module.api.IProxyTask

interface AnnotationDatabaseDAO {
    @Query("SELECT * FROM :table")
    fun getUsers(@Param("table") table: String = User.TABLE): IProxyTask<List<User>>

    @Query("SELECT * FROM :table")
    fun createTable()
    @Insert
    fun insertUser(user: User): IProxyTask<Long>
}

