package net.ap12.admintool.fabric.util.inventory

import eu.pb4.sgui.api.ClickType
import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.IUIManager
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.prefixed
import net.kyori.adventure.sound.Sound
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

@Suppress("UNCHECKED_CAST", "unused")
class ItemClickContext(val holder: AdminToolUIHolder, val slot: Int, val click: ClickType) {
    val player: ServerPlayer
        get() = holder.player

    val item
        get() = holder.container.getItem(slot)

    val isLeftClick
        get() = click.isLeft

    val isRightClick
        get() = click.isRight

    val isMiddleClick
        get() = click.isMiddle

    val isShift
        get() = click.shift

    var cursor
        get() = holder.player.inventoryMenu.carried
        set(value) {
            holder.player.inventoryMenu.carried = value
            holder.player.inventoryMenu.broadcastChanges()
        }

    val topInventory = holder.player.containerMenu
    val bottomInventory = holder.player.inventoryMenu

    var soundPlayed = false

    fun hasAction(): Boolean {
        val action = !item.getAction().isNullOrBlank()
        val cancel = item.getCancel() ?: false
        return action || cancel
    }

    fun close() {
        holder.close()
    }

    fun feedback(text: Component) {
        player.sendSystemMessage(text.prefixed().translate(holder.player))
    }

    fun feedback(translationKey: String) {
        player.sendSystemMessage(holder.plugin.localization.parse(translationKey, holder.player))
    }

    fun <R> back(callbackValue: R) {
        holder.back(callbackValue)
    }

    fun <R> go(
        ui: UI,
        needsWrap: Boolean = true,
        showTabs: Boolean = true,
        callback: (R) -> Unit = {},
    ): UI {
        val wrappedUI = (holder.plugin.ui as IUIManager).wrapperCreator(ui, showTabs)
        holder.go(wrappedUI, true, callback)
        return wrappedUI
    }

    fun set(stack: ItemStack) {
        holder.container.setItem(slot, stack)
    }

    fun getActionKey(): String? = item.getAction()

    private var shouldCancel = item.getCancel() ?: true

    fun shouldCancelEvent(): Boolean = shouldCancel

    fun setEventCancel(cancel: Boolean) {
        shouldCancel = cancel
    }

    fun uncancelEvent() {
        shouldCancel = false
    }

    fun playSound(sound: Sound) {
        holder.playSound(sound)
        soundPlayed = true
    }

    companion object {
        const val ACTION_KEY = "action"
        const val CANCEL_KEY = "cancel"
    }
}
