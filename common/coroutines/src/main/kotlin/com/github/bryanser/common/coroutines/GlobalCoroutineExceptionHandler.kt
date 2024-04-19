package com.github.bryanser.common.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import org.bukkit.Bukkit
import java.util.logging.Level
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler:CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    val successThrowable by lazy{
        kotlin.runCatching {
            val clazz = Class.forName(" kotlinx.coroutines.CoroutineExceptionHandler.internal.ExceptionSuccessfullyProcessed")
            clazz.getDeclaredField("INSTANCE").get(null) as? Throwable
        }.getOrNull()
    }

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Bukkit.getLogger().log(
            Level.WARNING,
            "Coroutine exception",
            exception
        )

        throw successThrowable ?: return
    }
}