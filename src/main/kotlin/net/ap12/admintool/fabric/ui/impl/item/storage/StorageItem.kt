package net.ap12.admintool.fabric.ui.impl.item.storage

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.minecraft.world.item.ItemStack

@Serializable
data class StorageItem(
    val page: Int,
    val index: Int,
    @Serializable(ItemStackSerializer::class) val item: ItemStack,
)
