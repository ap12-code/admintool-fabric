package net.ap12.admintool.fabric.config

import kotlinx.serialization.KSerializer
import net.ap12.admintool.fabric.AdminToolMod
import net.mamoe.yamlkt.Yaml
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DataLoader<T>(
    private val serializer: KSerializer<T>,
    private val plugin: AdminToolMod,
    private val fileName: String,
) {
    private val file = File(plugin.dataFolder, fileName)

    fun load(): T {
        if (!file.exists()) createFile()
        return Yaml.decodeFromString(serializer, file.readText())
    }

    fun save(data: T) {
        if (!file.exists()) createFile()
        file.writeText(Yaml.encodeToString(serializer, data))
    }

    private fun createFile() {
        file.parentFile.mkdirs()
        plugin.javaClass.classLoader.getResourceAsStream(fileName).use {
            if (it != null) {
                Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}
