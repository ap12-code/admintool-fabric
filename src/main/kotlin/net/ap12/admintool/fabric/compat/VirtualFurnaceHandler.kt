package net.ap12.admintool.fabric.compat

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.minecraft.world.inventory.FurnaceMenu

class VirtualFurnaceHandler {
    companion object {
        fun open(holder: AdminToolUIHolder) {
            val menu =
                FurnaceMenu(holder.player.containerMenu.incrementStateId(), holder.player.inventory)
            menu.broadcastChanges()
        }
    }
}
