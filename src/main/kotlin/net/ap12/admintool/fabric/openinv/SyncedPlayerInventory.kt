package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.Slot

class SyncedPlayerInventory(
    containerId: Int,
    viewer: ServerPlayer,
    target: OfflinePlayer,
    uiBuilder: UIBuilder<*>,
    holder: AdminToolUIHolder,
) : SyncedInventory(containerId, viewer, target, target.serverPlayer.inventory, uiBuilder, holder) {
    override fun getTopSize(): Int = 36

    override fun setupSlot() {
        val topSize = getTopSize()
        val armorSize = 5
        val middleSize = (rowSize * colSize) - (topSize + armorSize + bottomSize)
        val dummyContainer = SimpleContainer(middleSize)
        val emptyStack = createEmptyItem().toItemStack(holder).copy()

        if (baseInventory is ContainerWithTitle) {
            for (i in 0..<dummyContainer.containerSize) {
                val stack = baseInventory.getItem(topSize + armorSize + i).copy()
                if (stack.isEmpty) {
                    dummyContainer.setItem(i, emptyStack.copy())
                } else {
                    dummyContainer.setItem(i, stack)
                }
            }
        }

        for (row in 0..<rowSize) {
            for (col in 0..<colSize) {
                when (val i = (row * colSize) + col) {
                    in 0..<36 -> addSlot(Slot(targetInventory, fixHotbar(i), x(col), y(row)))
                    36 -> addSlot(Slot(dummyContainer, 0, x(col), y(row)))
                    in 37..41 -> addSlot(Slot(targetInventory, i - 1, x(col), y(row)))
                    in 42..<(topSize + armorSize + middleSize) ->
                        addSlot(Slot(dummyContainer, i - (topSize + armorSize), x(col), y(row)))

                    in (topSize + armorSize + middleSize)..<(rowSize * colSize) ->
                        addSlot(
                            Slot(
                                viewerInventory,
                                fixHotbar(i - (topSize + armorSize + middleSize)),
                                x(col),
                                y(row),
                            )
                        )
                }
            }
        }
    }
}
