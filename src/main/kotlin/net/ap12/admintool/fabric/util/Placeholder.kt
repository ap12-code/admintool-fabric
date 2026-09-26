package net.ap12.admintool.fabric.util

import net.ap12.admintool.fabric.util.inventory.ItemClickContext.Companion.CANCEL_KEY
import net.ap12.admintool.util.isEmptyOrNull
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.TooltipDisplay

class Placeholder {
    companion object {
        const val PLACEHOLDER_KEY = "placeholder"

        fun placeholder(): ItemStack {
            return ItemStack(Items.STAINED_GLASS_PANE.lightGray).also {
                it.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay(true, linkedSetOf()))
                val tag = CompoundTag()
                tag.putBoolean(PLACEHOLDER_KEY, true)
                tag.putBoolean(CANCEL_KEY, true)

                it.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
            }
        }

        fun isPlaceholder(stack: ItemStack?): Boolean {
            if (stack.isEmptyOrNull()) return false
            return stack!!
                .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getBooleanOr(PLACEHOLDER_KEY, false)
        }
    }
}
