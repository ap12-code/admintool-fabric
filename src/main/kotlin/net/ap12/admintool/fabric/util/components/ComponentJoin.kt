package net.ap12.admintool.fabric.util.components

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component

private val miniMessage = MiniMessage.miniMessage()

fun List<Component>.join(separator: String = ""): Component {
    return join(miniMessage.deserialize(separator).toNative())
}

fun List<Component>.join(separator: Component): Component {
    return this.map { it.copy() }
        .reduce { acc, component -> acc.copy().append(component).append(separator) }
}
