package com.github.bryanser.dungeonpoint

import com.github.bryanser.common.coroutines.BukkitDispatchers
import com.github.bryanser.common.database.initDatabase
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object{
        lateinit var plugin: Main
    }

    override fun onLoad() {
        super.onLoad()
        plugin = this
    }

    override fun onEnable() {
        BukkitDispatchers.init(this)
        initDatabase(this)
    }

    override fun onDisable() {
    }
}
