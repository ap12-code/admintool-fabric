package net.ap12.admintool.fabric.util.dialog.builder.base.inputs

import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.locale
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.Input

@Suppress("unused")
abstract class AbstractDialogInputBuilder<V>(private val holder: AdminToolUIHolder) {
    protected var label: Component = Component.empty()

    fun label(label: Component) {
        this.label = label.translate(holder.player.locale())
    }

    fun label(label: String) = label(label.toComponent())

    protected var initial: V? = null

    fun initial(initial: V?) {
        this.initial = initial
    }

    abstract fun build(): Input
}
