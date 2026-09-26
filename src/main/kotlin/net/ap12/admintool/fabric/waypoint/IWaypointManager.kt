package net.ap12.admintool.waypoint

import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import java.util.*

interface IWaypointManager<T> {

    fun getPoint(viewer: UUID?, id: UUID): WaypointPoint?

    fun getGroup(viewer: UUID?, id: UUID): WaypointGroup?

    fun create(creator: UUID?, point: Waypoint, next: Runnable = {})

    fun listGroups(viewer: UUID? = null): List<WaypointGroup>

    fun listPoints(viewer: UUID? = null): List<WaypointPoint>

    fun listRootGroups(viewer: UUID? = null): List<WaypointGroup>

    fun listRootPoints(viewer: UUID? = null): List<WaypointPoint>

    fun listRoot(viewer: UUID? = null): List<Waypoint>

    fun save()
}
