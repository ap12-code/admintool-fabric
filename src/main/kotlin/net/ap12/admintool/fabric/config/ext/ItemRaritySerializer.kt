package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.world.item.Rarity

object ItemRaritySerializer : KSerializer<Rarity> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ItemRarity", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Rarity) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): Rarity = Rarity.valueOf(decoder.decodeString())
}
