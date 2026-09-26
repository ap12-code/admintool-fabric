package net.ap12.admintool.fabric.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

val ADMINTOOL_PREFIX = Component.translatable("admintool.prefix")

fun Component.prefixed(): Component = ADMINTOOL_PREFIX.copy().append(this).withColor(TextColor.GOLD)

fun Component.symbolPrefixed(color: TextColor = TextColor.WHITE, applyColor: Boolean = false) =
    Component.literal("» ")
        .copy()
        .withColor(color)
        .append(this.copy().withColor(if (applyColor) color else TextColor.WHITE))
