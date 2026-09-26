package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object AdventureComponentSerializer : KSerializer<Component> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AdventureComponentSerializer", PrimitiveKind.STRING)
    private val miniMessage = MiniMessage.miniMessage()

    override fun serialize(encoder: Encoder, value: Component) {
        encoder.encodeString(miniMessage.serialize(value))
    }

    override fun deserialize(decoder: Decoder): Component =
        miniMessage.deserialize(decoder.decodeString())
}
