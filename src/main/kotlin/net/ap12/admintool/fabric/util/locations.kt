package net.ap12.admintool.fabric.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

fun isPortalBlock(material: Block): Boolean {
    return material == Blocks.NETHER_PORTAL ||
        material == Blocks.END_PORTAL ||
        material == Blocks.END_GATEWAY
}

fun isFireOrLava(material: BlockState): Boolean {
    return material.`is`(BlockTags.FIRE) ||
        material.`is`(BlockTags.CAMPFIRES) ||
        material.`is`(Blocks.LAVA)
}

fun iterateBlocks(
    level: ServerLevel,
    minLocation: BlockPos,
    maxLocation: BlockPos,
): Sequence<BlockState> {
    check(level.isLoaded(minLocation) && level.isLoaded(maxLocation)) { "Location is not loaded" }

    return sequence {
        for (x in minLocation.x..maxLocation.x) {
            for (y in minLocation.y..maxLocation.y) {
                for (z in minLocation.z..maxLocation.z) {
                    yield(level.getBlockState(BlockPos(x, y, z)))
                }
            }
        }
    }
}

fun iterateBlocks(level: ServerLevel, boundingBox: AABB): Sequence<BlockState> {
    return iterateBlocks(
        level,
        BlockPos.containing(boundingBox.minPosition),
        BlockPos.containing(boundingBox.maxPosition),
    )
}

fun hasFireResistance(entity: LivingEntity): Boolean {
    if (entity.type.fireImmune()) return true
    return entity.hasEffect(MobEffects.FIRE_RESISTANCE)
}

fun checkLocation(level: ServerLevel, location: Vec3, entity: LivingEntity? = null): Boolean {
    val currentBlock = BlockPos.containing(location)
    val standingBlock = level.getBlockState(BlockPos.containing(location).below())

    if (
        entity != null &&
            hasFireResistance(entity) &&
            iterateBlocks(level, entity.boundingBox).find { isFireOrLava(it) } != null
    ) {
        return true
    }

    return true
}

fun checkCollision(world: ServerLevel, boundingBox: AABB, entity: LivingEntity? = null): Boolean {
    return world.noCollision(boundingBox) || !checkLocation(world, boundingBox.minPosition, entity)
}

private fun resolveScale(sourceLevel: ServerLevel, source: Vec3, destination: ServerLevel): Vec3 {
    val scale =
        sourceLevel.dimensionType().coordinateScale / destination.dimensionType().coordinateScale
    return source.multiply(scale, scale, scale)
}

private fun WorldBorder.toBoundingBox(level: ServerLevel): AABB {
    val center = Vec3(centerX, 0.0, centerZ)
    val size = this.size
    val min = center.subtract(Vec3(size, size, size)).with(Direction.Axis.Y, level.minY.toDouble())
    val max = center.add(Vec3(size, size, size)).with(Direction.Axis.Y, level.maxY.toDouble())
    return AABB(min, max)
}

private fun isValidLocation(world: ServerLevel, boundingBox: AABB): Boolean {
    return world.noBorderCollision(null, boundingBox)
}

fun findNearestSpace(player: ServerPlayer, destination: ServerLevel, limit: Int): Location? {
    val boundingBox = player.boundingBox
    var count = 0

    val scaledLocation = resolveScale(player.level(), player.position(), destination)
    var scaledBoundingBox =
        AABB.ofSize(scaledLocation, boundingBox.xsize, boundingBox.ysize, boundingBox.zsize)

    while (checkCollision(destination, boundingBox, player)) {
        if (count > limit) return null
        if (isValidLocation(destination, boundingBox)) break
        count++
        scaledBoundingBox = scaledBoundingBox.move(0.0, 1.0, 0.0)
    }

    if (boundingBox == player.boundingBox) {
        return null
    }

    return Location.of(player.level(), boundingBox.minPosition, player.rotationVector)
}

fun findNearestSpace(
    level: ServerLevel,
    origin: Vec3,
    boundingBox: AABB,
    direction: Vec3,
    entity: LivingEntity? = null,
    limit: Int? = null,
): Location? {
    var boundingBox = boundingBox
    val direction = direction
    var count = 0

    while (checkCollision(level, boundingBox, entity)) {
        if (limit != null && count > limit) return null
        if (isValidLocation(level, boundingBox)) break
        count++
        boundingBox = boundingBox.move(direction)
    }

    val location = Location.of(level, boundingBox.minPosition)
    if (origin == location) return null

    if (entity != null) location.setRotation(entity.xRot, entity.yRot)
    return location
}
