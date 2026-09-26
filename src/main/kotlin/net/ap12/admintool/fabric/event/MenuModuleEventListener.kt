package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.EnchantmentMenu
import net.minecraft.world.inventory.MenuType

class MenuModuleEventListener(val plugin: AdminToolMod) {
    companion object {
        val views = mutableListOf<EnchantmentMenu>()
    }

    //
    //    fun onPrepareItemEnchant(event: PrepareItemEnchantEvent) {
    //        val player = event.enchanter
    //        val view = views.find { it.player == player }
    //        if (view == null) return
    //
    //        event.offers.getOrNull(0)?.cost = 1
    //        event.offers.getOrNull(1)?.cost = 15
    //        event.offers.getOrNull(2)?.cost = 30
    //    }

    fun onInventoryClose(player: ServerPlayer) {
        if (player.inventoryMenu.type == MenuType.ENCHANTMENT) {
            views.removeIf { it.stateId == player.inventoryMenu.stateId }
        }
    }
}
