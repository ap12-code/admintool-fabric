package net.ap12.admintool.io.waypoint

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.waypoint.element.Waypoint

@Serializable data class Waypoints(val waypoints: List<Waypoint> = listOf())
