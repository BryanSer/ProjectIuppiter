package com.github.bryanser.dungeonpoint.table

import com.github.bryanser.common.coroutines.BukkitDispatchers
import com.github.bryanser.common.database.database
import com.github.bryanser.common.database.listString
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.entity.add
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import org.ktorm.schema.Table
import org.ktorm.schema.*
import java.util.*

object DungeonInfoTable : Table<DungeonInfo>("dungeon_info") {
    val dungeonName = varchar("dungeonName").bindTo { it.dungeonName }.primaryKey()
    val maxPoint = int("maxPoint").bindTo { it.maxPoint }
    val permissions = listString("permissions").bindTo { it.permissions }

    @Language("MySQL")
    const val TABLE_CREATE = """
        CREATE TABLE IF NOT EXISTS dungeon_info (
            dungeonName VARCHAR(255) primary key ,
            maxPoint INT,
            permissions TEXT
        )
    """


    suspend fun queryDungeonInfo(
        name: String
    ): DungeonInfo? {
        return withContext(BukkitDispatchers.IO) {
            database.sequenceOf(DungeonInfoTable)
                .firstOrNull {
                    it.dungeonName eq name
                }
        }
    }


    suspend fun updatePlayerPointRecord(
        record:DungeonInfo
    ) {
        withContext(BukkitDispatchers.IO) {
            val update = database.sequenceOf(
                DungeonInfoTable
            ).update(record)
            if (update == 0) {
                database.sequenceOf(
                    DungeonInfoTable
                ).add(record)
            }
        }
    }
}

