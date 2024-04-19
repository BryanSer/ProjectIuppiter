package com.github.bryanser.dungeonpoint.table

import com.github.bryanser.common.coroutines.BukkitDispatchers
import com.github.bryanser.common.database.database
import com.github.bryanser.common.database.uuid
import com.github.bryanser.dungeonpoint.Main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.*
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import org.ktorm.schema.Table
import org.ktorm.schema.*
import java.util.UUID


object DungeonPointRecordTable : Table<DungeonPointRecord>("dungeon_point_record") {
    val playerUUID = uuid("playerUUID").bindTo { it.playerUUID }.primaryKey()
    val dungeonName = varchar("dungeonName")
        .bindTo { it.dungeonName }
        .primaryKey()
        .references(DungeonInfoTable) { it.dungeonInfo }

    val point = int("point").bindTo { it.point }

    @Language("MySQL")
    const val TABLE_CREATE = """
        CREATE TABLE IF NOT EXISTS dungeon_point_record (
            playerUUID VARCHAR(64),
            dungeonName VARCHAR(255)  ,
            point INT,
            PRIMARY KEY (playerUUID, dungeonName),
            FOREIGN KEY (dungeonName) REFERENCES dungeon_info(dungeonName)
        )
    """

    suspend fun queryPlayerPointRecord(
        uuid: UUID,
        dunName: String,
    ): Int {
        return withContext(BukkitDispatchers.IO) {
            val result = database.from(DungeonPointRecordTable)
                .select(
                    point
                ).where {
                    (playerUUID eq uuid) and (dungeonName eq dunName)
                }.limit(1)
                .map {
                    it[point]
                }
            val point = result.firstOrNull() ?: 0
            point
        }
    }

    suspend fun updatePlayerPointRecord(
        uuid: UUID,
        dunName: String,
        point: Int
    ) {
        withContext(BukkitDispatchers.IO) {
            val record = DungeonPointRecord {
                playerUUID = uuid
                dungeonName = dunName
                this.point = point
            }
            val update = database.sequenceOf(
                DungeonPointRecordTable
            ).update(record)
            if (update == 0) {
                database.sequenceOf(
                    DungeonPointRecordTable
                ).add(record)
            }
        }
    }

}

