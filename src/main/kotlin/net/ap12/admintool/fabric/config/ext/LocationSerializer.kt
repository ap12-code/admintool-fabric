package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.ap12.admintool.fabric.util.Location
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeString("${value.x} ${value.y} ${value.z} ${value.level.identifier()}")
    }

    override fun deserialize(decoder: Decoder): Location {
        val input = decoder.decodeString()
        val split = input.split(" ")
        if (split.size != 4) throw SerializationException("Failed to decode Location: $input")

        val (x, y, z) =
            split.dropLast(1).map {
                it.toDoubleOrNull() ?: throw SerializationException("Failed to parse Double: $it")
            }

        val pos = Vec3(x, y, z)

        return Location(
            pos,
            Vec2.ZERO,
            ResourceKey.create(Registries.DIMENSION, Identifier.parse(split.last())),
        )
    }
}
