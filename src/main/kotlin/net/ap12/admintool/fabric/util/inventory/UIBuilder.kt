package net.ap12.admintool.fabric.util.inventory

import net.minecraft.network.chat.Component

interface UIBuilder<R> {
    fun build(): R

    fun getType(): UIType

    fun getTitle(): Component
}
