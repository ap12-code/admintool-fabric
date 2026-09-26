package net.ap12.admintool.fabric.util.inventory

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.kyori.adventure.text.JoinConfiguration
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

@Suppress("unused")
class LoreBuilder {
    val divider =
        Component.literal(" ".repeat(40))
            .withColor(TextColor.DARK_GRAY)
            .withStyle(ChatFormatting.STRIKETHROUGH)

    private val components = mutableListOf<MutableComponent>()

    operator fun String.plus(component: Component) = this.toComponent().append(component)

    operator fun MutableComponent.plus(other: String): Component =
        this.append(other.toComponent().withStyle(this.style))

    operator fun MutableComponent.plus(other: MutableComponent): Component =
        this.append(other.withStyle(this.style))

    operator fun String.unaryPlus(): MutableComponent {
        components.add(this.toComponent())
        return this.toComponent()
    }

    operator fun Component.unaryPlus() {
        components.add(this.copy())
    }

    fun field(translationKey: String, active: Boolean = false) = field(t(translationKey), active)

    fun field(value: Any, active: Boolean = false) =
        field(Component.literal(value.toString()), active)

    fun field(value: MutableComponent, active: Boolean = false) =
        value.symbolPrefixed(if (active) TextColor.YELLOW else TextColor.GRAY, true)

    fun field(value: MutableComponent, color: TextColor) =
        value.withColor(color).symbolPrefixed(color)

    fun field(translationKey: String, value: MutableComponent): Component =
        field(translationKey, value, TextColor.WHITE)

    fun field(translationKey: String, value: String): Component =
        field(translationKey, value.toComponent())

    fun field(
        translationKey: String,
        value: Component,
        color: TextColor? = null,
    ): MutableComponent =
        t(translationKey)
            .withStyle(Style.EMPTY.withColor(color))
            .append(Component.literal(": "))
            .append(value.copy().withColor(TextColor.WHITE))
            .symbolPrefixed(TextColor.GRAY)

    fun action(translationKey: String, vararg args: Component): Component =
        t(translationKey, *Keys.concat(*args)).symbolPrefixed(TextColor.AQUA)

    private val fallbackStyle = Style.EMPTY.withColor(TextColor.WHITE).withItalic(false)

    fun build() =
        components.toList().map { if (it.style.isEmpty) it.withStyle(fallbackStyle) else it }
}

fun lore(builder: LoreBuilder.() -> Unit): List<Component> {
    return LoreBuilder().apply(builder).build()
}

fun List<Component>.join(separator: String) =
    this.map { it.copy() }
        .reduce { acc, component -> acc.append(component).append(Component.literal(separator)) }

fun List<net.kyori.adventure.text.Component>.join(separator: String) =
    net.kyori.adventure.text.Component.join(
        JoinConfiguration.separator(net.kyori.adventure.text.Component.text(separator)),
        this,
    )
