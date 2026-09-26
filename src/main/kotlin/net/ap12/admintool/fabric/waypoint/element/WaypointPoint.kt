package net.ap12.admintool.fabric.waypoint.element

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.ap12.admintool.fabric.config.ext.LocationSerializer
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.waypoint.WaypointVisibility
import net.minecraft.world.item.ItemStack
import java.util.*

@Serializable
class WaypointPoint(
    @Serializable(UUIDSerializer::class) override val id: UUID,
    override val index: Int,
    override val name: String,
    override val visibility: WaypointVisibility,
    @Serializable(UUIDSerializer::class) override val parent: UUID?,
    @Serializable(ItemStackSerializer::class) override val icon: ItemStack,
    @Serializable(LocationSerializer::class) val location: Location,
    @Serializable(UUIDSerializer::class) override val creator: UUID,
    override val created: LocalDateTime,
    override val updated: LocalDateTime,
) : Waypoint {

    override fun equals(other: Any?): Boolean {
        if (other !is WaypointPoint) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + location.hashCode()
        return result
    }
}
