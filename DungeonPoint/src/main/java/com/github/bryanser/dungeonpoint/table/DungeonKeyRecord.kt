package com.github.bryanser.dungeonpoint.table

import org.ktorm.entity.Entity
import java.util.*

interface DungeonKeyRecord : Entity<DungeonKeyRecord> {

    companion object : Entity.Factory<DungeonKeyRecord>()
    var playerUUID: UUID
    var dungeonName:String
    var key:String

    val dungeonInfo: DungeonInfo


}