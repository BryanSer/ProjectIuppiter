package com.github.bryanser.common.coroutines

import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object BukkitDispatchers {
    lateinit var mainDispatcher: BukkitCoroutineDispatcher
        private set
    val IO = Dispatchers.IO

    fun init(plugin: Plugin) {
        mainDispatcher = BukkitCoroutineDispatcher(plugin)
    }


}

inline fun launchOnIO(
    context: CoroutineContext = BukkitDispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT, crossinline block: suspend () -> Unit
):Job{

    return CoroutineScope(BukkitDispatchers.IO).launch(context, start) {
        block()
    }
}