package net.ap12.admintool.fabric.util

import net.ap12.admintool.fabric.i18n.t
import net.kyori.adventure.util.TriState
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

fun TriState.toComponent(): Component =
    when (this) {
        TriState.FALSE -> t("admintool.value.false").withColor(TextColor.RED)
        TriState.TRUE -> t("admintool.value.true").withColor(TextColor.GREEN)
        TriState.NOT_SET -> t("admintool.value.default").withColor(TextColor.GRAY)
    }

fun TriState.nextValue(): TriState =
    when (this) {
        TriState.NOT_SET -> TriState.TRUE
        TriState.TRUE -> TriState.FALSE
        TriState.FALSE -> TriState.NOT_SET
    }
