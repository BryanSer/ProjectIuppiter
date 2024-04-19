package com.github.bryanser.dungeonpoint.table

import com.github.bryanser.common.database.database

fun createTable(){
    database.useConnection {
        val statement = it.createStatement()
        statement.execute(
            DungeonInfoTable.TABLE_CREATE
        )
        statement.execute(
            DungeonPointRecordTable.TABLE_CREATE
        )
        statement.execute(
            DungeonKeyTable.TABLE_CREATE
        )
        statement.close()
    }
}