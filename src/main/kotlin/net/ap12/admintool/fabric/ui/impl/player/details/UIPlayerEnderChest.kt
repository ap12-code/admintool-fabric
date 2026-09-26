package net.ap12.admintool.fabric.ui.impl.player.details

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.inventory.ItemClickContext
import net.ap12.admintool.fabric.util.inventory.inventory
import net.kyori.adventure.key.Key
import net.minecraft.world.item.Items

class UIPlayerEnderChest : UI {
    override val id: Key = UI_PLAYER_ENDER_CHEST_KEY

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            27..35 to Items.STAINED_GLASS_PANE.white
            36..44 to Items.STAINED_GLASS_PANE.black
        }

    private fun flushAndDiscard(holder: AdminToolUIHolder, next: Runnable = {}) {
        val plugin = holder.plugin
        plugin.openInv.getMenu(holder)?.save(next)
        plugin.openInv.discardMenu(holder)
    }

    override fun onClick(context: ItemClickContext): Boolean = context.hasAction()

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        flushAndDiscard(holder, next)
    }

    override fun onClose(holder: AdminToolUIHolder) {
        flushAndDiscard(holder)
    }

    companion object {
        val UI_PLAYER_ENDER_CHEST_KEY = AdminToolMod.key("player.details.ender_chest")
    }
}
