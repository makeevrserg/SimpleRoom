package com.astrainteractive.astratemplate.auto_module.dao

import room.Insert
import room.Param
import room.Query
import com.astrainteractive.astratemplate.auto_module.api.IProxyTask

interface AnnotationDatabaseDAO {
    @Query("SELECT * FROM :table")
    fun getUsers(@Param("table") table: String = User.TABLE): IProxyTask<List<User>>

    @Query("SELECT * FROM :table")
    fun createTable()

    @Insert
    fun insertUser(user: User): IProxyTask<Long>


    @Insert
    fun insertUser(user: List<User>): IProxyTask<Long>
}

