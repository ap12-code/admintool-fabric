package net.ap12.admintool.fabric.config.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class NbtEnumSerializer<E : Enum<E>, K>(
    serialName: String,
    private val kind: PrimitiveKind,
    private val encode: (E) -> K,
    private val decode: (K) -> E,
) : KSerializer<E> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, kind)

    override fun serialize(encoder: Encoder, value: E) {
        when (val key = encode(value)) {
            is Int -> encoder.encodeInt(key)
            is String -> encoder.encodeString(key)
            is Byte -> encoder.encodeByte(key)
            else -> throw SerializationException("Unsupported primitive type: ${key!!::class}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): E =
        decode(
            when (kind) {
                PrimitiveKind.INT -> decoder.decodeInt()
                PrimitiveKind.STRING -> decoder.decodeString()
                PrimitiveKind.BYTE -> decoder.decodeByte()
                else -> throw SerializationException("Unsupported primitive type: $kind")
            }
                as K
        )
}
