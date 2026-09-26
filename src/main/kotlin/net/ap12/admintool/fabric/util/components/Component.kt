package net.ap12.admintool.fabric.util.components

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.network.chat.Component

val plainTextComponentSerializer = PlainTextComponentSerializer.plainText()
val legacyTextComponentSerializer = LegacyComponentSerializer.legacyAmpersand()

fun Component.length(): Int {
    return this.string.length
}

fun Component?.isNullOrBlank(): Boolean {
    return plainTextComponentSerializer.serializeOrNull(this?.toAdventure()).isNullOrBlank()
}

fun Component?.isNullOrEmpty(): Boolean {
    return plainTextComponentSerializer.serializeOrNull(this?.toAdventure()).isNullOrEmpty()
}

fun Component.isEmpty(): Boolean {
    return plainTextComponentSerializer.serialize(this.toAdventure()).isEmpty()
}

fun Component.isBlank(): Boolean {
    return plainTextComponentSerializer.serialize(this.toAdventure()).isBlank()
}

fun Component?.orEmpty(): Component {
    return this ?: Component.empty()
}

operator fun Component.get(i: Int): Component {
    return this.split()[i]
}

fun Component.equalContent(other: Component): Boolean {
    return this.string == other.string
}

fun Component.indexOf(element: Component): Int {
    return this.indexOf(element, 0)
}

fun Component.indexOf(element: Component, startIndex: Int = 0, ignoreCase: Boolean = false): Int {
    return this.string.indexOf(element.string, startIndex, ignoreCase)
}

fun Component.substring(start: Int, end: Int = this.length()): Component {
    return this.split().subList(start, end).join(Component.empty())
}
