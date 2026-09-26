package net.ap12.admintool.fabric.ui.impl.item

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.minecraft.world.item.ItemStack
import java.util.*

@Serializable
data class RegisteredItemStack(
    @Serializable(UUIDSerializer::class) val id: UUID,
    val index: Int,
    @Serializable(ItemStackSerializer::class) val item: ItemStack,
    val page: Int,
) {

    companion object {
        fun create(id: UUID = UUID.randomUUID(), stack: ItemStack, page: Int): RegisteredItemStack =
            RegisteredItemStack(id, 0, stack, page)
    }
}
