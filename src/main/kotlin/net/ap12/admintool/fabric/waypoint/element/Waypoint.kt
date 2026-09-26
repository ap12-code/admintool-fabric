package net.ap12.admintool.fabric.waypoint.element

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.ap12.admintool.waypoint.WaypointVisibility
import net.minecraft.world.item.ItemStack
import java.util.*

@Serializable
sealed interface Waypoint {
    @Serializable(UUIDSerializer::class) val id: UUID

    val index: Int

    val name: String

    val visibility: WaypointVisibility

    @Serializable(UUIDSerializer::class) val parent: UUID?

    @Serializable(ItemStackSerializer::class) val icon: ItemStack

    @Serializable(UUIDSerializer::class) val creator: UUID

    val created: LocalDateTime
    val updated: LocalDateTime

    fun isRoot(): Boolean = parent == null

    fun isVisible(viewer: UUID?): Boolean =
        visibility == WaypointVisibility.PUBLIC || creator == viewer
}
