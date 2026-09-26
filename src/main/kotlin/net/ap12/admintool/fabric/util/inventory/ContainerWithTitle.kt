package net.ap12.admintool.fabric.util.inventory

import eu.pb4.sgui.api.gui.SimpleGui
import net.ap12.admintool.fabric.util.Placeholder
import net.ap12.admintool.util.isEmptyOrNull
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class ContainerWithTitle(val title: Component, val items: SimpleContainer = SimpleContainer(54)) :
    Container {
    val slots = mutableMapOf<Int, Slot>()

    init {
        for (i in 0..<54) {
            slots[i] = Slot(items, i, i, 0)
        }
    }

    override fun setItem(index: Int, stack: ItemStack) {
        items.setItem(index, stack)
    }

    override fun setChanged() {
        items.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    override fun getContainerSize(): Int {
        return 54
    }

    override fun isEmpty(): Boolean {
        return items.all { it.isEmptyOrNull() }
    }

    override fun getItem(index: Int): ItemStack {
        return items.getSlot(index)?.get() ?: ItemStack.EMPTY
    }

    override fun removeItem(slot: Int, count: Int): ItemStack {
        return items.removeItem(slot, count)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return items.removeItemNoUpdate(slot)
    }

    fun setSlot(index: Int, slot: Slot) {
        slots[index] = slot
    }

    fun applyTo(ui: SimpleGui) {
        for (i in 0..<54) {
            if (items.getItem(i).isEmptyOrNull()) {
                ui.setSlot(i, Placeholder.placeholder())
            }
            ui.setSlot(i, slots[i])
            ui.title = title
        }
    }

    override fun clearContent() {}

    companion object {
        fun empty(): ContainerWithTitle {
            return ContainerWithTitle(Component.empty())
        }
    }
}
