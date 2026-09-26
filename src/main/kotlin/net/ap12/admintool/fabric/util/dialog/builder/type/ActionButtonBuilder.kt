package net.ap12.admintool.fabric.util.dialog.builder.type

import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.dialog.builder.DialogCallback
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.ap12.admintool.fabric.util.locale
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonButtonData
import net.minecraft.server.dialog.action.Action
import net.minecraft.server.dialog.action.CustomAll
import org.jetbrains.annotations.Range
import java.util.*

@DialogDSL
@Suppress("unused")
class ActionButtonBuilder(private val holder: AdminToolUIHolder) {
    private var label: Component = Component.empty()

    fun label(label: Component) {
        this.label = label.translate(holder.player.locale())
    }

    fun label(label: String) {
        this.label = label.toComponent().translate(holder.player.locale())
    }

    private var tooltip: Component? = null

    fun tooltip(tooltip: Component) {
        this.tooltip = tooltip.translate(holder.player.locale())
    }

    fun tooltip(tooltip: String) {
        this.tooltip = tooltip.toComponent().translate(holder.player.locale())
    }

    private var width: @Range(from = 1, to = 1024) Int = 100

    fun width(width: @Range(from = 1, to = 1024) Int) {
        this.width = width
    }

    private var action: Action? = null

    fun action(key: String, callback: DialogCallback) {
        val actionKey = holder.plugin.key(key)
        holder.setDialogAction(actionKey, callback)
        this.action = CustomAll(actionKey, Optional.empty())
    }

    fun action(callback: DialogCallback) {
        val actionKey = holder.plugin.key(holder.createStringKey())
        holder.setDialogAction(actionKey, callback)
        this.action = CustomAll(actionKey, Optional.empty())
    }

    fun build(): ActionButton =
        ActionButton(
            CommonButtonData(label, Optional.ofNullable(tooltip), width),
            Optional.ofNullable(action),
        )
}
