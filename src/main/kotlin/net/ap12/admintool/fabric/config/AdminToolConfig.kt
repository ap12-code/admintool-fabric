package net.ap12.admintool.fabric.config

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ExtendedTimeZoneSerializer
import net.ap12.admintool.fabric.util.permission.PermissionState

@Serializable
data class AdminToolConfig(
    @Serializable(ExtendedTimeZoneSerializer::class)
    val timezone: TimeZone = TimeZone.currentSystemDefault(),
    val defaultTab: String = "admintool:home",
    val directories: Directories = Directories(),
    val alwaysShownEffects: Map<String, Int> = mapOf(),
    val permissions: Permissions = Permissions(),
) {
    @Serializable data class Directories(val playerdata: String = "auto")

    @Serializable
    data class Permissions(
        val ui: Map<String, PermissionState> = mapOf(),
        val modules: Map<String, PermissionState> = mapOf(),
    )
}
