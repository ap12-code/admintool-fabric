package net.ap12.admintool.fabric.ui.impl.waypoint

import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.checkCollision
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import kotlin.math.ceil

fun ServerPlayer.ascend(callback: Runnable) {
    val level = this.level()
    var boundingBox = this.boundingBox.move(0.0, boundingBox.ysize, 0.0)
    val maxY = level.maxY + ceil(boundingBox.ysize).toInt()

    while (boundingBox.minY <= maxY) {
        val pos = Location.of(level, boundingBox.minPosition.add(0.0, 0.5, 0.0))
        val blockPos = BlockPos.containing(pos)
        val solidStanding =
            !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty
        if (checkCollision(level, boundingBox, this) && solidStanding) {
            pos.moveHere(this)
            return
        }
        boundingBox = boundingBox.move(0.0, 1.0, 0.0)
    }
}

fun ServerPlayer.descend(callback: Runnable) {
    val level = this.level()
    var boundingBox = this.boundingBox
    val minY = level.minY + ceil(boundingBox.ysize).toInt()

    while (boundingBox.minY >= minY) {
        val pos = Location.of(level, boundingBox.minPosition)
        val blockPos = BlockPos.containing(pos)
        val solidStanding =
            !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty

        if (checkCollision(level, boundingBox, this) && solidStanding) {
            pos.moveHere(this)
            return
        }
        boundingBox = boundingBox.move(0.0, -1.0, 0.0)
    }
}
