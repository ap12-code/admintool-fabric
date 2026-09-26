package net.ap12.admintool.fabric.config.ext

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.world.item.ItemStack
import org.apache.commons.lang3.SerializationException

object ItemStackSerializer : KSerializer<ItemStack> {
    private val gson = Gson()
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeString(
            gson.toJson(ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, value).orThrow)
        )
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val input = decoder.decodeString()
        if (input.isEmpty()) return ItemStack.EMPTY
        return ItemStack.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(input))
            .getOrThrow { SerializationException("Failed to parse item stack: $input") }
            .first
    }
}
