package net.ap12.admintool.fabric.ui

import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.inventory.ItemClickContext
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.translation.Translatable
import net.minecraft.network.chat.Component

interface UI : Translatable, Keyed {
    val id: Key

    val size: Int
        get() = 54

    val title: Component
        get() = Component.translatable(this.translationKey())

    override fun key(): Key = id

    override fun translationKey(): String = "admintool.ui.${id.value()}"

    fun create(holder: AdminToolUIHolder): UIBuilder<*>

    fun onClose(holder: AdminToolUIHolder) {}

    fun onClick(context: ItemClickContext): Boolean = true

    fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        next()
    }

    fun bottomClick(context: ItemClickContext): Boolean = false

    fun <U : UI> isInstance(ui: Class<U>): Boolean = ui.isInstance(this)

    fun placeholderRange(): IntRange = 0..<size
}
