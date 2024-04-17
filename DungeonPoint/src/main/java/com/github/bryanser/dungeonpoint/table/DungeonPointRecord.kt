package com.github.bryanser.dungeonpoint.table

import org.ktorm.entity.Entity
import java.util.*

interface DungeonPointRecord : Entity<DungeonPointRecord> {

    companion object : Entity.Factory<DungeonPointRecord>()
    var playerUUID: UUID
    var dungeonName:String
    var point:Int

    val dungeonInfo:DungeonInfo


}