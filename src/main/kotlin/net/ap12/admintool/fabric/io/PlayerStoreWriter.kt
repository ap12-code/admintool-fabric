package net.ap12.admintool.fabric.io

import java.io.File
import java.util.*
import kotlinx.serialization.KSerializer
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.io.IAdminToolPlayerData
import net.benwoodworth.knbt.encodeToStream
import org.slf4j.Logger

class PlayerStoreWriter<T : IAdminToolPlayerData>(
    private val dataDir: File,
    private val dataStore: AdminToolPlayerStore<T>,
    private val logger: Logger,
    private val plugin: AdminToolMod,
    private val serializer: KSerializer<T>,
) {
    val deferred = mutableSetOf<UUID>()

    private fun checkData(data: T): Result<Boolean> {
        try {
            plugin.nbt.encodeToNbtTag(serializer, data)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun writeToFile(player: UUID) {
        val file = dataDir.resolve("$player.dat")
        if (!file.exists()) file.createNewFile()
        val data = dataStore.get(player)
        val checkResult = checkData(data)
        if (checkResult.isFailure) {
            logger.error("AdminTool detected invalid user data!")
            logger.error("terminated write user data for $player.")
            logger.error("-> ${checkResult.exceptionOrNull()?.stackTraceToString()}")
            return
        }
        file.outputStream().use { plugin.nbt.encodeToStream(serializer, data, it) }
    }

    fun writeNow(player: UUID) {
        plugin.server.execute { writeToFile(player) }
    }

    fun writeDefer(player: UUID) {
        deferred.add(player)
    }

    fun flush(player: UUID) {
        deferred.filter { it == player }.forEach { writeNow(it) }
    }

    fun flush() {
        deferred.forEach { writeToFile(it) }
    }
}
