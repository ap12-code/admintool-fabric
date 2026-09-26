package net.ap12.admintool.util

import net.ap12.admintool.fabric.util.Placeholder
import net.minecraft.world.item.ItemStack

fun ItemStack?.isEmptyOrNull(): Boolean {
    return this == null || this.isEmpty
}

fun ItemStack?.isBlankOrNull(): Boolean {
    return this == null || this.isEmpty || Placeholder.isPlaceholder(this)
}
