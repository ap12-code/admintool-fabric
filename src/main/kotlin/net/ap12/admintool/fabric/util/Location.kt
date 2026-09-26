package net.ap12.admintool.fabric.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Location(val position: Vec3, val rotation: Vec2, val level: ResourceKey<Level>) :
    Cloneable, Position {
    var x = position.x
    var y = position.y
    var z = position.z
    var yaw = rotation.x
    var pitch = rotation.y

    fun getBlockPos(): BlockPos {
        return BlockPos.containing(this)
    }

    fun getBlock(blockGetter: BlockGetter): BlockState {
        return blockGetter.getBlockState(this.getBlockPos())
    }

    fun relative(x: Double, y: Double, z: Double): Location {
        return Location(position.add(x, y, z), rotation, level)
    }

    fun relative(pos: Position) = relative(pos.x(), pos.y(), pos.z())

    fun get(): Vec3 = Vec3(x, y, z)

    override fun equals(other: Any?): Boolean {
        if (other is Vec3) return get() == other
        return false
    }

    fun toCenterLocation(): Location {
        val x = Mth.floor(x) + 0.5
        val y = Mth.floor(y) + 0.5
        val z = Mth.floor(z) + 0.5

        return create(x, y, z, level, yaw, pitch)
    }

    fun moveHere(entity: Entity) {
        entity.snapTo(this.x, this.y, this.z, this.pitch, this.yaw)
        if (entity is ServerPlayer) {
            entity.connection.teleport(this.x, this.y, this.z, this.pitch, this.yaw)
            entity.connection.resetPosition()
        }
    }

    public override fun clone(): Location = create(x, y, z, level, yaw, pitch)

    fun setRotation(pitch: Float, yaw: Float) {
        this.pitch = pitch
        this.yaw = yaw
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        result = 31 * result + level.hashCode()
        result = 31 * result + level.hashCode()
        return result
    }

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    companion object {
        fun wrap(location: Vec3, world: ResourceKey<Level>) = Location(location, Vec2.ZERO, world)

        fun of(entity: Entity): Location {
            return Location(entity.position(), entity.rotationVector, entity.level().dimension())
        }

        fun of(level: ServerLevel, position: Vec3, rotation: Vec2 = Vec2.ZERO): Location {
            return Location(position, rotation, level.dimension())
        }

        fun of(respawnData: LevelData.RespawnData): Location {
            return Location(
                Vec3.atBottomCenterOf(respawnData.pos()),
                Vec2(respawnData.pitch, respawnData.yaw),
                respawnData.dimension(),
            )
        }

        fun create(
            x: Double,
            y: Double,
            z: Double,
            world: ResourceKey<Level>,
            yaw: Float,
            pitch: Float,
        ) = Location(Vec3(x, y, z), Vec2(yaw, pitch), world)
    }
}

fun Entity.location(): Location {
    return Location.of(this)
}

fun Entity.teleportTo(loc: Location) {
    loc.moveHere(this)
}
