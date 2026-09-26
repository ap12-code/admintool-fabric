package net.ap12.admintool.fabric.util.dialog

import am.ik.yavi.arguments.StringValidator
import am.ik.yavi.core.Validated
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.Validators
import net.ap12.admintool.fabric.util.components.toAdventure
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket
import kotlin.jvm.optionals.getOrNull

class DialogResponse(
    val holder: AdminToolUIHolder,
    private val packet: ServerboundCustomClickActionPacket,
) {
    fun getBoolean(key: String): Boolean {
        return requireNotNull(getBooleanOrNull(key))
    }

    fun getBooleanOrNull(key: String): Boolean? {
        return packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getBoolean(key)?.getOrNull()
    }

    fun getFloat(key: String): Float {
        return requireNotNull(getFloatOrNull(key))
    }

    fun getFloatOrNull(key: String): Float? {
        return packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getFloat(key)?.getOrNull()
    }

    fun getInt(key: String): Int {
        return requireNotNull(getIntOrNull(key))
    }

    fun getIntOrNull(key: String): Int? {
        return packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getInt(key)?.getOrNull()
    }

    fun getString(key: String): String {
        return requireNotNull(getStringOrNull(key))
    }

    fun getStringOrNull(key: String): String? {
        return packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getString(key)?.getOrNull()
    }

    fun <U> validateString(key: String, validator: StringValidator<U>): Validated<U> {
        val text =
            packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getString(key)?.getOrNull()
        return validator.validate(text)
    }

    private val miniMessage = MiniMessage.miniMessage()

    fun validateString(
        key: String,
        defaultMessageFormat: String,
        predicate: (String) -> Boolean,
    ): Validated<String> {
        val name = requireNotNull(holder.elementNameStore[key])
        val messageKey = "string." + requireNotNull(holder.getCurrent()).id.value()

        val validator =
            Validators.predicate(
                miniMessage.serialize(name.toAdventure()),
                messageKey,
                defaultMessageFormat,
                predicate,
            )

        val text =
            packet.payload.getOrNull()?.asCompound()?.getOrNull()?.getString(key)?.getOrNull()
        return validator.validate(text)
    }

    fun validationFailed(error: Component) {
        holder.error = error
        holder.update()
    }
}
