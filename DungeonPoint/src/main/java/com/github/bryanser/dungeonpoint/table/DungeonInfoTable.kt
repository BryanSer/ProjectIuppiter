package com.github.bryanser.dungeonpoint.table

import com.github.bryanser.common.database.listString
import org.ktorm.schema.Table
import org.ktorm.schema.*

object DungeonInfoTable : Table<DungeonInfo>("dungeon_info"){
    val dungeonName = varchar("dungeonName").bindTo { it.dungeonName }.primaryKey()
    val maxPoint = int("maxPoint").bindTo { it.maxPoint }
    val permissions = listString("permissions").bindTo { it.permissions }
}

