package com.astrainteractive.astratemplate.auto_module

import room.EOND
import com.astrainteractive.astratemplate.auto_module.dao.AnnotationDatabaseDAO
import com.astrainteractive.astratemplate.auto_module.dao.User
import com.astrainteractive.astratemplate.auto_module.retrofit.Retrofit
import com.astrainteractive.astratemplate.auto_module.retrofit.RetrofitDAO
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.com.google.gson.Gson

fun main() {

//    val api = EOND.databaseBuilder("test", clazz = AnnotationDatabaseDAO::class.java, User::class.java)
//    println(runBlocking { api.getUsers().await() })
//    println(runBlocking { api.insertUser(User(-1,"discordid","minecraftid")).await() })
//    println(runBlocking { api.insertUser(listOf(User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"),User(-1,"discordid","minecraftid"))).await() })
//    println(runBlocking { api.getUsers().await() })

    val retrofit = Retrofit {
        this.baseUrl = "https://rickandmortyapi.com/"
        this.converterFactory = { json, clazz ->
            json?.let { Gson().fromJson(json, clazz) }
        }
    }.create(RetrofitDAO::class.java)
    runBlocking { println(retrofit.getApi().await()) }
    runBlocking { println(retrofit.getCharacters().await()) }
}