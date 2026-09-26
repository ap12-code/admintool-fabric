package net.ap12.admintool.fabric.util

import net.ap12.admintool.fabric.AdminToolMod
import net.kyori.adventure.util.Ticks
import net.minecraft.server.TickTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TaskScheduler(private val plugin: AdminToolMod) {
    private val scheduledAsyncExecutor = Executors.newScheduledThreadPool(1)
    private val asyncExecutor = Executors.newFixedThreadPool(1)

    fun sync(task: Runnable) {
        plugin.server.executeBlocking(task)
    }

    fun syncDelayed(delay: Int, task: Runnable) {
        plugin.server.schedule(TickTask(delay, task))
    }

    fun async(task: Runnable) {
        asyncExecutor.execute(task)
    }

    fun asyncRepeat(period: Long, delay: Long = 0, task: Runnable) {
        val periodMills = period * Ticks.SINGLE_TICK_DURATION_MS
        val delayMills = delay * Ticks.SINGLE_TICK_DURATION_MS

        scheduledAsyncExecutor.scheduleAtFixedRate(
            task,
            delayMills,
            periodMills,
            TimeUnit.MILLISECONDS,
        )
    }

    fun asyncDelayed(delay: Long, task: Runnable) {
        val delayMills = delay * Ticks.SINGLE_TICK_DURATION_MS

        scheduledAsyncExecutor.schedule(task, delayMills, TimeUnit.MILLISECONDS)
    }

    fun <T> scheduleAsync(block: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        async {
            val result = block()
            future.complete(result)
        }
        return future
    }
}
