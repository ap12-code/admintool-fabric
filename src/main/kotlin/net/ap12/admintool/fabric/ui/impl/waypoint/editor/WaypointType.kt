package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import net.ap12.admintool.fabric.config.ext.NbtEnumSerializer
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import net.kyori.adventure.translation.Translatable

@Serializable(WaypointType.Serializer::class)
enum class WaypointType(val code: Byte) : Translatable {
    @SerialName("group") GROUP(0),
    @SerialName("point") POINT(1);

    override fun translationKey(): String =
        "admintool.ui.waypoint.editor.type.${this.name.lowercase()}"

    object Serializer :
        NbtEnumSerializer<WaypointType, Byte>(
            "WaypointType",
            PrimitiveKind.BYTE,
            { it.code },
            ::fromCode,
        )

    companion object {
        fun fromCode(code: Byte): WaypointType = entries.find { it.code == code } ?: GROUP

        fun getType(waypoint: Waypoint): WaypointType =
            when (waypoint) {
                is WaypointGroup -> GROUP
                is WaypointPoint -> POINT
            }
    }
}
