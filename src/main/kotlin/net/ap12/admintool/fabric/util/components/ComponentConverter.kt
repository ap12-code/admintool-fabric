package net.ap12.admintool.fabric.util.components

import net.ap12.admintool.fabric.AdminToolMod
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

fun Float.toComponent(fix: Int = 2): Component {
    return Component.literal("%.${fix}f".format(this))
}

fun Double.toComponent(fix: Int = 2): Component {
    return Component.literal("%.${fix}f".format(this))
}

fun Int.toComponent(): Component {
    return Component.literal(this.toString())
}

fun Long.toComponent(): Component {
    return Component.literal(this.toString())
}

enum class BooleanComponentType(val on: String, val off: String) {
    ON_OFF("admintool.value.state.on", "admintool.value.state.off"),
    YES_NO("admintool.value.state.yes", "admintool.value.state.no"),
    TRUE_FALSE("admintool.value.true", "admintool.value.false"),
}

fun Boolean.toComponent(type: BooleanComponentType = BooleanComponentType.ON_OFF): Component {
    return Component.translatable(if (this) type.on else type.off)
}

private val legacy = LegacyComponentSerializer.legacyAmpersand()
private val miniMessage = MiniMessage.miniMessage()
val mixedSerializer =
    MiniMessage.builder().preProcessor { miniMessage.serialize(legacy.deserialize(it)) }.build()

fun String?.toComponent(): MutableComponent =
    Component.empty()
        .append(
            AdminToolMod.getInstance().adventure.asNative(mixedSerializer.deserialize(this ?: ""))
        )
