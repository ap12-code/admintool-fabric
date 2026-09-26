package net.ap12.admintool.io.waypoint

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.io.waypoint.WaypointIO
import net.ap12.admintool.fabric.ui.impl.waypoint.editor.WaypointEditorPlayerData
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import net.ap12.admintool.waypoint.IWaypointManager
import org.slf4j.LoggerFactory
import java.util.*

class WaypointManager(plugin: AdminToolMod) : IWaypointManager<WaypointEditorPlayerData> {
    private val logger = LoggerFactory.getLogger("AdminTool/Waypoint")
    val io = WaypointIO(plugin)

    fun init() {
        try {
            logger.info("Loading waypoints...")
            io.load { logger.info("Successfully loaded ${io.waypoints.size} waypoints.") }
        } catch (e: Exception) {
            logger.error("Failed to load waypoints.")
            logger.error(" -> ${e.message}")
        }
    }

    fun get(viewer: UUID?, id: UUID): Waypoint? =
        (listGroups(viewer) + listPoints(viewer)).find { it.id == id }

    override fun getPoint(viewer: UUID?, id: UUID): WaypointPoint? =
        listPoints(viewer).find { it.id == id }

    override fun getGroup(viewer: UUID?, id: UUID): WaypointGroup? =
        listGroups(viewer).find { it.id == id }

    fun remove(id: UUID, creator: UUID?, next: Runnable = {}) {
        val before = requireNotNull(get(creator, id))
        val parentGroup = before.parent?.let { getGroup(creator, it)?.children } ?: io.waypoints
        val index = parentGroup.indexOfFirst { it.id == id }
        parentGroup.removeAt(index)
        io.saveAsync { next() }
    }

    fun update(id: UUID, creator: UUID?, point: Waypoint, next: Runnable = {}) {
        check(point.id == id)
        val before = requireNotNull(getPoint(creator, id))
        val parentGroup = before.parent?.let { getGroup(creator, it)?.children } ?: io.waypoints
        val index = parentGroup.indexOfFirst { it.id == id }
        parentGroup[index] = point
        io.saveAsync { next() }
    }

    override fun create(creator: UUID?, point: Waypoint, next: Runnable) {
        if (point.isRoot()) {
            io.waypoints.add(point)
        } else {
            val parent =
                getGroup(creator, point.parent!!)
                    ?: throw IllegalStateException("Can't find group: ${point.parent}")
            parent.children.add(point)
        }
        logger.info("Created waypoint: ${point.name}")
        io.saveAsync { next() }
    }

    private fun listGroups(viewer: UUID?, parent: WaypointGroup): List<WaypointGroup> {
        val groups = parent.getChildGroups()
        val result = mutableListOf(parent)
        for (group in groups) {
            result.addAll(listGroups(viewer, group))
        }
        return result
    }

    override fun listGroups(viewer: UUID?): List<WaypointGroup> =
        io.waypoints
            .filterIsInstance<WaypointGroup>()
            .flatMap { listGroups(viewer, it) }
            .filter { it.isVisible(viewer) }

    override fun listPoints(viewer: UUID?): List<WaypointPoint> =
        listGroups(viewer)
            .flatMap { it.getChildPoints() }
            .filter { it.isVisible(viewer) }
            .union(listRootPoints(viewer))
            .toList()

    override fun listRootGroups(viewer: UUID?) =
        io.waypoints.filterIsInstance<WaypointGroup>().toList().filter { it.isVisible(viewer) }

    override fun listRootPoints(viewer: UUID?) =
        io.waypoints.filterIsInstance<WaypointPoint>().toList().filter { it.isVisible(viewer) }

    override fun listRoot(viewer: UUID?) = io.waypoints.toList().filter { it.isVisible(viewer) }

    override fun save() = io.save()
}
