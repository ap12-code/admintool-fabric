package net.ap12.admintool.fabric.util.components

import net.ap12.admintool.fabric.AdminToolMod

fun net.kyori.adventure.text.Component.toNative(): net.minecraft.network.chat.MutableComponent {
    return AdminToolMod.getInstance().adventure.asNative(this).copy()
}

fun net.minecraft.network.chat.Component.toAdventure(): net.kyori.adventure.text.Component {
    return AdminToolMod.getInstance().adventure.asAdventure(this)
}
