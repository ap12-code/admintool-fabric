package net.ap12.admintool.fabric.ui

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class AdminToolSlot(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {
    override fun mayPickup(player: Player): Boolean {
        return false
    }

    override fun allowModification(player: Player): Boolean {
        return false
    }

    override fun safeTake(amount: Int, maxAmount: Int, player: Player): ItemStack {
        return ItemStack.EMPTY
    }
}
