package com.github.bryanser.common.coroutines

import kotlinx.coroutines.Dispatchers
    import org.bukkit.plugin.Plugin

    object BukkitDispatchers {
        lateinit var mainDispatcher: BukkitCoroutineDispatcher
            private set
        val IO = Dispatchers.IO

        fun init(plugin: Plugin){
            mainDispatcher = BukkitCoroutineDispatcher(plugin)
        }


}