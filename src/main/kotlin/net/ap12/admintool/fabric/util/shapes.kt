package net.ap12.admintool.fabric.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity

fun canStandAt(level: ServerLevel, entity: Entity, pos: Position): Boolean {
    val isSpaceEmpty = level.noCollision(entity)

    val belowPos = BlockPos.containing(pos).below()
    val belowState = level.getBlockState(belowPos)
    val hasSupport = belowState.isFaceSturdy(level, belowPos, Direction.UP)

    return isSpaceEmpty && hasSupport
}
