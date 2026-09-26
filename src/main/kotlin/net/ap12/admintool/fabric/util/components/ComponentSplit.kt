package net.ap12.admintool.fabric.util.components

import net.ap12.admintool.fabric.AdminToolMod
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

private val miniMessage = MiniMessage.miniMessage()
private val PATTERN_RGB_TAG = Regex("#[0-9a-fA-F]{6}")
private val PATTERN_RGBA_TAG = Regex("#[0-9a-fA-F]{8}")

private fun isColor(str: String, allowAlpha: Boolean = false): Boolean {
    val replaced = str.replace("grey", "gray")
    if (replaced.startsWith("#")) {
        if (str.matches(PATTERN_RGB_TAG)) return true
        if (allowAlpha && str.matches(PATTERN_RGBA_TAG)) return true
    }
    return false
}

private fun decoration(str: String): ChatFormatting? =
    when (str) {
        "bold",
        "b" -> ChatFormatting.BOLD
        "italic",
        "em",
        "i" -> ChatFormatting.ITALIC
        "underlined",
        "u" -> ChatFormatting.UNDERLINE
        "strikethrough",
        "st" -> ChatFormatting.STRIKETHROUGH
        "obfuscated",
        "obf" -> ChatFormatting.OBFUSCATED
        else -> null
    }

private fun isStyleTagName(str: String): Boolean {
    val tag = str.removePrefix("/").split(":").firstOrNull() ?: return false

    if (isColor(tag)) return true
    if (decoration(tag) != null) return true
    if (decoration(tag.removePrefix("!")) != null) return true

    val args = tag.split(":").drop(1)
    if (args.isNotEmpty()) {
        if (listOf("color", "colour", "c").contains(tag) && isColor(args[0])) return true
        if (tag == "shadow" && isColor(args[0], true)) return true // <shadow:color>
        if (tag == "shadow" && isColor(args[0]) && args.getOrNull(1)?.toFloatOrNull() != null)
            return true // <shadow:{color}:[float]>
        if (tag == "font" && args[0] != "") return true
    } else {
        if (tag == "!shadow") return true
    }
    if (tag == "reset") return true

    return false
}

private fun isCloseableTag(tag: String): Boolean {
    return tag != "reset"
}

private fun processCharacter(character: Char, tags: Iterable<String>): String {
    return "${tags.joinToString("") { "<${it}>" }}${character}"
}

private fun Component.serialize(): String {
    return miniMessage
        .serialize(AdminToolMod.getInstance().adventure.asAdventure(this))
        .replace("<br>", "\n")
}

private fun splitComponent(component: Component): List<String> {
    if (component.plainCopy().string.isEmpty()) return listOf()

    val serialized = component.copy().serialize()
    val plain =
        plainTextComponentSerializer.serialize(
            AdminToolMod.getInstance().adventure.asAdventure(component)
        )
    val buf = ArrayList<String>(plain.length)
    val tags = mutableSetOf<String>()
    val tagBuffer = StringBuilder()
    var isTag = false
    var escaped = false

    for (cursor in serialized.indices) {
        val peek = serialized[cursor]
        if (peek == '\\') {
            escaped = true
        } else if (peek == '<' && !escaped) {
            tagBuffer.clear()
            isTag = true
        } else if (peek == '>' && !escaped) {
            val tagName = tagBuffer.toString()
            if (tagName == "reset") {
                tags.clear()
            }
            if (isStyleTagName(tagName)) {
                if (tagName.startsWith("/") && isCloseableTag(tagName.removePrefix("/"))) {
                    tags.remove(tagName.removePrefix("/"))
                } else {
                    tags.add(tagName)
                }
            }
            isTag = false
            tagBuffer.clear()
        } else {
            if (isTag) {
                tagBuffer.append(peek)
            } else {
                buf.add(processCharacter(peek, tags))
            }
            escaped = false
        }
    }

    return buf
}

fun Component.split(): List<Component> {
    return splitComponent(this).map(miniMessage::deserialize).map { it.toNative() }
}

fun Component.split(separator: Component): List<Component> {
    if (separator.string.isEmpty()) return split()
    val style = this.style
    return this.serialize().split(separator.serialize()).map(miniMessage::deserialize).map {
        it.toNative().withStyle(style)
    }
}

fun Component.splitToString(): List<String> {
    return splitComponent(this)
}

fun Component.chunked(size: Int): List<Component> {
    return this.windowed(size, size)
}

fun Component.windowed(size: Int, step: Int = 1): List<Component> {
    val thisSize = legacyTextComponentSerializer.serialize(this.toAdventure()).length
    val resultCapacity = thisSize / step + if (thisSize % step == 0) 0 else 1
    val result = ArrayList<Component>(resultCapacity)
    var index = 0
    while (index in 0 until thisSize) {
        val end = index + size
        val coercedEnd = end.coerceIn(0, thisSize)
        result.add(this.string.substring(index, coercedEnd).toComponent())
        index += step
    }
    return result
}

fun Component.wrapText(limit: Int): Component {
    val words = this.split(Component.literal(" "))
    val result = mutableListOf<MutableComponent>()

    var lineLength = 0
    for (word in words) {
        if (lineLength + word.string.length > limit) {
            result.add(Component.literal("\n"))
            lineLength = 0
        }
        result.add(word.copy().append(" "))
        lineLength += word.string.length + 1
    }
    return result.reduce { acc, component -> acc.append(component) }
}
