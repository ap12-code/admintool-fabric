package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.ap12.admintool.fabric.util.permission.PermissionParser
import net.ap12.admintool.fabric.util.permission.PermissionState

private val PARSER = PermissionParser()

object PermissionSerializer : KSerializer<PermissionState> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PermissionState", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PermissionState {
        return PARSER.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: PermissionState) {
        return encoder.encodeString(value.condition)
    }
}
