package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.Slot

class SyncedEnderChestInventory(
    containerId: Int,
    viewer: ServerPlayer,
    target: OfflinePlayer,
    uiBuilder: UIBuilder<*>,
    holder: AdminToolUIHolder,
) :
    SyncedInventory(
        containerId,
        viewer,
        target,
        target.serverPlayer.enderChestInventory,
        uiBuilder,
        holder,
    ) {
    override fun getTopSize(): Int = 27

    override fun setupSlot() {
        val topSize = getTopSize()
        val middleSize = (rowSize * colSize) - (topSize + bottomSize)
        val dummyContainer = SimpleContainer(middleSize)
        val emptyStack = createEmptyItem().toItemStack(holder)

        if (baseInventory is ContainerWithTitle) {
            for (i in 0..<dummyContainer.containerSize) {
                val stack = baseInventory.getItem(topSize + i)
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
                    in 0..<topSize -> addSlot(Slot(targetInventory, i, x(col), y(row)))

                    in topSize..<(topSize + middleSize) ->
                        addSlot(Slot(dummyContainer, i - topSize, x(col), y(row)))

                    in (topSize + middleSize)..<(rowSize * colSize) ->
                        addSlot(
                            Slot(
                                viewerInventory,
                                fixHotbar(i - (topSize + middleSize)),
                                x(col),
                                y(row),
                            )
                        )
                }
            }
        }
    }
}
