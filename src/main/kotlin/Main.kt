package com.astrainteractive.astratemplate.auto_module

import api.EOND
import com.astrainteractive.astratemplate.auto_module.dao.AnnotationDatabaseDAO
import com.astrainteractive.astratemplate.auto_module.dao.User
import kotlinx.coroutines.runBlocking

fun main() {

    val api = EOND.databaseBuilder("test", clazz = AnnotationDatabaseDAO::class.java, User::class.java)
    println(runBlocking { api.getUsers().await() })
    println(runBlocking { api.insertUser(User(-1,"discordid","minecraftid")).await() })
    println(runBlocking { api.insertUser(listOf(User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"))).await() })
    println(runBlocking { api.getUsers().await() })

}