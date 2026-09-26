package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.ap12.admintool.fabric.config.ext.LocationSerializer
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import net.ap12.admintool.waypoint.WaypointVisibility
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.time.Clock

@Serializable
data class WaypointEditorPlayerData(
    @Serializable(UUIDSerializer::class) var id: UUID? = null,
    var name: String? = null,
    var type: WaypointType = WaypointType.GROUP,
    var visibility: WaypointVisibility = WaypointVisibility.PRIVATE,
    @Serializable(UUIDSerializer::class) var parent: UUID? = null,
    @Serializable(ItemStackSerializer::class) var icon: ItemStack = ItemStack.EMPTY,
    @Serializable(LocationSerializer::class) var location: Location? = null,
    @Serializable(UUIDSerializer::class) var creator: UUID? = null,
    var created: LocalDateTime? = null,
    var updated: LocalDateTime? = null,
    var children: MutableList<Waypoint>? = null,
) {
    fun isValid(): Boolean {
        if (name.isNullOrBlank()) return false
        if (type == WaypointType.POINT) {
            if (location == null) return false
        }

        return true
    }

    fun toWaypoint(access: AdminToolMod, creator: ServerPlayer): Waypoint? {
        if (!isValid()) return null
        val id = this.id ?: UUID.randomUUID()
        val index = access.waypointManager.listRoot().size
        val now = Clock.System.now().toLocalDateTime(access.config.timezone)

        val creator = this.creator ?: creator.uuid
        val created = this.created ?: now

        return if (type == WaypointType.POINT) {
            require(location != null)
            WaypointPoint(
                id,
                index,
                requireNotNull(name),
                visibility,
                parent,
                icon,
                requireNotNull(location),
                creator,
                created,
                now,
            )
        } else {
            val children = this.children ?: mutableListOf()
            WaypointGroup(
                id,
                index,
                requireNotNull(name),
                visibility,
                parent,
                icon,
                children,
                creator,
                created,
                now,
            )
        }
    }

    companion object {
        fun fromWaypoint(waypoint: Waypoint): WaypointEditorPlayerData {
            val targetType =
                when (waypoint) {
                    is WaypointPoint -> WaypointType.POINT
                    is WaypointGroup -> WaypointType.GROUP
                }
            val targetLocation = if (waypoint is WaypointPoint) waypoint.location else null
            val targetChildren = if (waypoint is WaypointGroup) waypoint.children else null
            return WaypointEditorPlayerData(
                waypoint.id,
                waypoint.name,
                targetType,
                waypoint.visibility,
                waypoint.parent,
                waypoint.icon,
                targetLocation,
                waypoint.creator,
                waypoint.created,
                waypoint.updated,
                targetChildren,
            )
        }
    }
}
