package com.github.bryanser.dungeonpoint.table

import org.ktorm.entity.Entity

interface DungeonInfo : Entity<DungeonInfo> {
    companion object: Entity.Factory<DungeonInfo>()
    var dungeonName:String
    var maxPoint:Int
    var permissions:List<String>


}