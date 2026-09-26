package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item

object ItemTypeSerializer : KSerializer<Item> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MaterialSerializer", PrimitiveKind.STRING)
    private val registry = BuiltInRegistries.ITEM

    override fun serialize(encoder: Encoder, value: Item) {
        encoder.encodeString(registry.getKey(value).asString())
    }

    override fun deserialize(decoder: Decoder): Item {
        val key = Identifier.tryParse(decoder.decodeString())
        return registry.getValue(key)
    }
}
