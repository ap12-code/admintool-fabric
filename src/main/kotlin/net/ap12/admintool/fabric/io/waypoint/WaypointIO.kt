package net.ap12.admintool.fabric.io.waypoint

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.config.DataLoader
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.io.waypoint.Waypoints
import java.util.*

class WaypointIO(val plugin: AdminToolMod) {
    var loaded = false
    val waypoints: MutableList<Waypoint> = Collections.synchronizedList(mutableListOf<Waypoint>())
    private val loader = DataLoader(Waypoints.serializer(), plugin, "waypoints.yml")

    fun load(next: Runnable = {}) {
        plugin.server.execute {
            val values = loader.load().waypoints

            waypoints.clear()
            waypoints.addAll(values)
            loaded = true
            next()
        }
    }

    fun save() {
        if (!loaded) return
        loader.save(Waypoints(waypoints))
    }

    fun saveAsync(next: Runnable = {}) =
        plugin.server.execute {
            save()
            next()
        }
}
