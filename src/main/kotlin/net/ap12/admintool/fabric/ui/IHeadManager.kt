package net.ap12.admintool.fabric.ui

import net.minecraft.world.item.ItemStack

interface IHeadManager {
    fun get(name: String): ItemStack

    fun rebuild()
}
