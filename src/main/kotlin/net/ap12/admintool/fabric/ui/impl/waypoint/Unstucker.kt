package net.ap12.admintool.fabric.ui.impl.waypoint

import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.checkCollision
import net.ap12.admintool.fabric.util.teleportTo
import net.minecraft.server.level.ServerPlayer

fun resolveUnstuckLocation(player: ServerPlayer): Location? {
    var boundingBox = player.boundingBox
    val level = player.level()

    while (checkCollision(level, boundingBox, player)) {
        if (boundingBox.minY > level.maxY) break
        boundingBox = boundingBox.move(0.0, 1.0, 0.0)
    }

    if (boundingBox == player.boundingBox) {
        return null
    }

    val location = Location.of(level, boundingBox.minPosition, player.rotationVector)
    if (!level.worldBorder.isWithinBounds(boundingBox.minPosition)) return null

    return location
}

fun ServerPlayer.unstuck(callback: Runnable) {
    val location = resolveUnstuckLocation(this)
    if (location != null) {
        this.teleportTo(location)
        callback()
    }
}
