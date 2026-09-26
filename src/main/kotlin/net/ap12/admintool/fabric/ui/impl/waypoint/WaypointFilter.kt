package net.ap12.admintool.fabric.ui.impl.waypoint

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.ap12.admintool.fabric.ui.impl.waypoint.editor.WaypointType
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import net.ap12.admintool.waypoint.WaypointVisibility
import java.util.*

@Serializable
data class WaypointFilter(
    val keyword: String? = null,
    val type: WaypointType? = null,
    val visibility: WaypointVisibility? = null,
    @Serializable(UUIDSerializer::class) val creator: UUID? = null,
    val dimension: String? = null,
) {
    fun apply(waypoints: List<Waypoint>): List<Waypoint> {
        return waypoints.filter {
            val keywordMatch = keyword.let { s -> s.isNullOrBlank() || it.name.startsWith(s) }
            val typeMatch = type == null || WaypointType.getType(it) == type
            val visibilityMatch = visibility == null || it.visibility == visibility
            val creatorMatch = creator == null || it.creator == creator
            val dimensionMatch =
                dimension == null ||
                    it !is WaypointPoint ||
                    it.location.level.identifier().value() == dimension

            keywordMatch && typeMatch && visibilityMatch && creatorMatch && dimensionMatch
        }
    }
}
