package com.github.bryanser.dungeonpoint.table

import com.github.bryanser.common.coroutines.BukkitDispatchers
import com.github.bryanser.common.database.database
import com.github.bryanser.common.database.uuid
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.varchar
import java.util.*

object DungeonKeyTable : Table<DungeonKeyRecord>("dungeon_keys") {
    val playerUUID = uuid("playerUUID").primaryKey().bindTo {
        it.playerUUID
    }
    val dungeonName = varchar("dungeonName")
        .bindTo { it.dungeonName }
        .primaryKey()
        .references(DungeonInfoTable) { it.dungeonInfo }
    val key = varchar("key").bindTo { it.key }.primaryKey()

    @Language("MySQL")
    const val TABLE_CREATE = """
        CREATE TABLE IF NOT EXISTS dungeon_keys (
            playerUUID VARCHAR(64),
            dungeonName VARCHAR(255)  ,
            `key` VARCHAR(255)  ,
            PRIMARY KEY (playerUUID, dungeonName,`key`),
            FOREIGN KEY (dungeonName) REFERENCES dungeon_info(dungeonName)
        )
    """

    suspend fun queryPlayerKeyExists(
        uuid: UUID,
        dunName: String,
        key:String
    ):Boolean{
        return withContext(BukkitDispatchers.IO){
           database.sequenceOf(DungeonKeyTable)
               .any {
                   (it.key eq key) and (it.dungeonName eq dunName) and (it.playerUUID eq uuid)
               }

        }
    }

    suspend fun addPlayerKey(
        uuid: UUID,
        dunName: String,
        key:String
    ){
        withContext(BukkitDispatchers.IO){
            val record = DungeonKeyRecord{
                playerUUID = uuid
                dungeonName = dunName
                this.key = key
            }
            database.sequenceOf(
                DungeonKeyTable
            ).add(record)
        }
    }

}

