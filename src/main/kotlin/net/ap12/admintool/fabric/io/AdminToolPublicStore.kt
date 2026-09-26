package net.ap12.admintool.fabric.io

import kotlinx.serialization.KSerializer
import net.ap12.admintool.fabric.AdminToolMod
import net.benwoodworth.knbt.decodeFromStream
import net.benwoodworth.knbt.encodeToStream
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class AdminToolPublicStore<T>(
    private val serializer: KSerializer<T>,
    private val initialValue: T,
    private val plugin: AdminToolMod,
) {
    companion object {
        private val logger = LoggerFactory.getLogger("AdminTool/PublicDataLoader")
    }

    private val dataDir = plugin.dataFolder.resolve("data")
    private var initialized = false

    private var data: T? = initialValue

    fun init() {
        if (!dataDir.parentFile.canRead()) {
            throw IOException("Can't create data directory: no read permission")
        }
        if (!dataDir.parentFile.canWrite()) {
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

    private fun getFile(): File {
        require(initialized)

        val file = dataDir.resolve("public.dat")
        this.checkDataDirectory()
        if (!file.exists()) {
            file.createNewFile()
            file.outputStream().use { plugin.nbt.encodeToStream(serializer, initialValue, it) }
        }
        if (!file.isFile || !file.canRead() || !file.canWrite())
            throw IOException("Can't open public data file")
        return file
    }

    fun preload() {
        require(initialized)

        logger.info("Pre-loading public data...")
        data =
            getFile().inputStream().use { stream ->
                plugin.nbt.decodeFromStream(serializer, stream)
            }

        logger.info("Successfully loaded public data.")
    }

    fun get(force: Boolean = false): T {
        require(initialized)
        if (data != null && !force) return data!!

        data = getFile().inputStream().use { plugin.nbt.decodeFromStream(serializer, it) }
        return data!!
    }

    fun edit(editor: T.() -> Unit) {
        require(initialized)
        data = get().apply(editor)
        if (data != null) {
            plugin.server.execute {
                data?.let { data1 ->
                    getFile().outputStream().use {
                        plugin.nbt.encodeToStream(serializer, data1, it)
                    }
                }
            }
        }
    }

    fun delete() {
        val file = dataDir.resolve("public.dat")
        if (file.exists()) file.delete()
        data = null
    }
}
