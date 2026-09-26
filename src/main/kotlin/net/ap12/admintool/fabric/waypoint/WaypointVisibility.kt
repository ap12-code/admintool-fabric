package net.ap12.admintool.waypoint

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import net.ap12.admintool.fabric.config.ext.NbtEnumSerializer
import net.kyori.adventure.translation.Translatable

@Serializable(WaypointVisibility.Serializer::class)
enum class WaypointVisibility(val code: Byte) : Translatable {
    PUBLIC(0),
    PRIVATE(1);

    override fun translationKey(): String =
        "admintool.ui.waypoint.editor.visibility.${name.lowercase()}"

    object Serializer :
        NbtEnumSerializer<WaypointVisibility, Byte>(
            "WaypointType",
            PrimitiveKind.BYTE,
            { it.code },
            { fromCode(it) },
        )

    companion object {
        fun fromCode(code: Byte): WaypointVisibility = entries.find { it.code == code } ?: PRIVATE
    }
}
