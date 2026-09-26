package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class SyncedInventory(
    containerId: Int,
    protected var viewer: ServerPlayer,
    protected var target: OfflinePlayer,
    protected var targetInventory: Container,
    uiBuilder: UIBuilder<*>,
    protected val holder: AdminToolUIHolder,
) : AbstractContainerMenu(MenuType.GENERIC_9x6, containerId) {
    protected val playerManager = holder.plugin.offlinePlayerManager
    protected val baseInventory = uiBuilder.build()
    protected val viewerInventory
        get() = viewer.inventory

    protected val baseSize = 54

    protected abstract fun getTopSize(): Int

    protected val bottomSize = 36

    protected val rowSize = 10
    protected val colSize = 9

    init {
        setupSlot()
        updateStateItem(target.isOnline())
        broadcastChanges()
    }

    protected abstract fun setupSlot()

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = slots[index]
        if (!slot.hasItem() || slot.isFake) return ItemStack.EMPTY

        val stack = slot.item
        val original = stack.copy()

        if (index < 36) {
            if (!this.moveItemStackTo(stack, baseSize, baseSize + bottomSize, true)) {
                return ItemStack.EMPTY
            }
        } else if (index in 36..<54) {
            return ItemStack.EMPTY
        } else {
            if (!this.moveItemStackTo(stack, 0, getTopSize(), false)) {
                return ItemStack.EMPTY
            }
        }

        if (stack.isEmpty) {
            slot.setByPlayer(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }

        if (stack.count == original.count) return ItemStack.EMPTY
        slot.onTake(player, stack)

        return stack
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeInventory(
        holder: AdminToolUIHolder,
        base: UIBuilder<ContainerWithTitle>,
        other: Container,
    ): ContainerWithTitle {
        val inventory = ContainerWithTitle.empty()
        val createdBase = base.build()

        for (i in 0..<baseSize) createdBase.getItem(i).let { inventory.setItem(i, it) }
        for (i in 0..<getTopSize()) other.getItem(i).let { inventory.setItem(i, it) }

        return inventory
    }

    fun updateTarget(newTarget: OfflinePlayer) {
        val newContainer = newTarget.serverPlayer.inventory
        target = newTarget
        targetInventory = newContainer

        for (i in 0..<getTopSize()) {
            val oldSlot = slots[i]
            slots[i] = Slot(targetInventory, oldSlot.index, oldSlot.x, oldSlot.y)
        }
        updateStateItem(target.isOnline())

        broadcastFullState()
    }

    private fun updateStateItem(online: Boolean) {
        slots[36].set((if (online) createOnlineItem() else createOfflineItem()).toItemStack(holder))
    }

    override fun broadcastChanges() {
        if (target.isOnline()) {
            super.broadcastChanges()
        }
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    fun save(callback: () -> Unit) {
        playerManager.flush(target, callback)
    }

    protected fun fixHotbar(i: Int): Int = if (i >= 27) i - 27 else i + 9

    fun setTitle(title: Component) {
        this.holder.title = title
    }

    class Provider(
        val holder: AdminToolUIHolder,
        val target: OfflinePlayer,
        val uiBuilder: UIBuilder<*>,
        val builder:
            (Int, ServerPlayer, OfflinePlayer, UIBuilder<*>, AdminToolUIHolder) -> SyncedInventory,
    ) : MenuProvider {
        override fun getDisplayName(): Component {
            return this.holder.title
        }

        override fun createMenu(
            containerId: Int,
            inventory: Inventory,
            player: Player,
        ): AbstractContainerMenu {
            return builder(containerId, player as ServerPlayer, target, uiBuilder, holder)
        }
    }

    companion object {
        internal fun y(row: Int): Int = 18 + row * 18

        internal fun x(col: Int): Int = 8 + col * 18
    }
}
