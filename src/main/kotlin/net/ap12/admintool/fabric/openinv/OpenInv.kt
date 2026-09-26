package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder

open class OpenInv(val mod: AdminToolMod) {
    private val playerManager = PlayerManager(mod)

    fun getPlayerManager(): PlayerManager = playerManager

    fun createInventory(target: OfflinePlayer, holder: AdminToolUIHolder) {
        val offlineTarget = playerManager.get(target) ?: return
        val uiBuilder = holder.currentUI?.create(holder) ?: return
        holder.player.openMenu(
            SyncedInventory.Provider(holder, offlineTarget, uiBuilder, ::SyncedPlayerInventory)
        )
    }

    fun createEnderChest(target: OfflinePlayer, holder: AdminToolUIHolder) {
        val uiBuilder = holder.currentUI?.create(holder) ?: return
        val offlineTarget = playerManager.get(target) ?: return
        holder.player.openMenu(
            SyncedInventory.Provider(holder, offlineTarget, uiBuilder, ::SyncedEnderChestInventory)
        )
    }

    fun getMenu(holder: AdminToolUIHolder): SyncedInventory? {
        val menu = holder.player.containerMenu
        if (menu is SyncedInventory) {
            return menu
        }
        return null
    }

    fun discardMenu(holder: AdminToolUIHolder) {
        holder.player.closeContainer()
    }
}
