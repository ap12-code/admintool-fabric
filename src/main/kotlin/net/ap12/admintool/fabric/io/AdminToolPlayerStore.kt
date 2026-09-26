package net.ap12.admintool.fabric.io

import java.io.File
import java.io.IOException
import java.util.*
import kotlinx.serialization.KSerializer
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.util.filterAndCollect
import net.ap12.admintool.fabric.util.mapAndCollect
import net.ap12.admintool.io.IAdminToolPlayerData
import net.ap12.admintool.util.isValidUUID
import net.benwoodworth.knbt.decodeFromStream
import net.benwoodworth.knbt.encodeToStream
import net.minecraft.server.players.NameAndId
import org.slf4j.LoggerFactory

class AdminToolPlayerStore<T : IAdminToolPlayerData>(
    private val serializer: KSerializer<T>,
    private val initialValue: T,
    private val plugin: AdminToolMod,
) {
    companion object {
        private val logger = LoggerFactory.getLogger("AdminTool/DataLoader")
    }

    private val dataDir = plugin.dataFolder.toPath().resolve("data").toFile()
    private var initialized = false

    val queue = PlayerStoreWriter(dataDir, this, logger, plugin, serializer)

    private val data = mutableMapOf<UUID, T>()

    fun init() {
        if (!plugin.dataFolder.canRead()) {
            throw IOException("Can't create data directory: no read permission")
        }
        if (!plugin.dataFolder.canWrite()) {
            throw IOException("Can't create data directory: no write permission")
        }
        if (!dataDir.exists()) {
            dataDir.mkdirs()
            plugin.slogger.info("Created data directory: ${dataDir.path}")
        }
        this.checkDataDirectory()
        initialized = true
    }

    private fun checkDataDirectory() {
        if (!dataDir.canRead()) {
            throw IOException("Can't open data directory: no read permission")
        }
        if (!dataDir.canWrite()) {
            throw IOException("Can't open data directory: no write permission")
        }
    }

    private fun getFile(uuid: UUID): File {
        require(initialized)

        val file = dataDir.resolve("$uuid.dat")
        this.checkDataDirectory()
        if (!file.exists()) {
            file.createNewFile()
            file.outputStream().use { plugin.nbt.encodeToStream(serializer, initialValue, it) }
        }
        return file
    }

    fun preload() {
        require(initialized)

        logger.info("Pre-loading user data...")
        val invalid = mutableListOf<File>()
        dataDir
            .listFiles()
            .toList()
            .filter { it.nameWithoutExtension != "public" }
            .filterAndCollect(invalid) { it.canRead() && it.canWrite() && it.isFile }
            .filterAndCollect(invalid) {
                isValidUUID(it.nameWithoutExtension) && it.extension == "dat"
            }
            .mapAndCollect(invalid) {
                UUID.fromString(it.nameWithoutExtension) to
                    it.inputStream().use { stream ->
                        plugin.nbt.decodeFromStream(serializer, stream)
                    }
            }
            .let { mapOf(*it.toTypedArray()) }
            .forEach { data[it.key] = it.value }

        logger.info("Successfully loaded user data for ${data.size} users.")
        if (invalid.isNotEmpty()) {
            logger.warn(
                "Invalid user data found: \n${invalid.joinToString("\n - ") { it.nameWithoutExtension }}"
            )
        }
    }

    fun get(player: UUID, force: Boolean = false): T {
        require(initialized)
        if (force) data.remove(player)
        if (data.containsKey(player)) return data[player]!!

        data[player] =
            getFile(player).inputStream().use { plugin.nbt.decodeFromStream(serializer, it) }
        return data[player]!!
    }

    fun get(player: NameAndId, force: Boolean = false) = get(player.id, force)

    fun edit(player: UUID, editor: T.() -> Unit) {
        require(initialized)
        data[player] = get(player).apply(editor)
        queue.writeDefer(player)
    }

    fun edit(player: NameAndId, editor: T.() -> Unit) = edit(player.id, editor)

    fun delete(player: UUID) {
        queue.deferred.removeIf { it == player }
        dataDir
            .listFiles { it.nameWithoutExtension == player.toString() && it.extension == "dat" }
            .forEach { it.delete() }
        data.remove(player)
    }

    fun deleteAll() {
        queue.deferred.clear()
        dataDir.listFiles { it.extension == "dat" }.forEach { it.delete() }
        data.clear()
    }
}
