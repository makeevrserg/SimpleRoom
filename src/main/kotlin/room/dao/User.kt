package com.astrainteractive.astratemplate.auto_module.dao

import room.ColumnInfo
import room.Entity
import room.PrimaryKey

@Entity(User.TABLE)
data class User(
    @PrimaryKey(autoIncrement = true)
    @ColumnInfo(name = "id", field = "id")
    val id: Long,
    @ColumnInfo(name = "discord_id", field = "discordId")
    val discordId: String,
    @ColumnInfo(name = "minecraft_uuid", field = "minecraftUuid")
    val minecraftUuid: String,
) {
    companion object {
        const val TABLE: String = "users"
    }
}