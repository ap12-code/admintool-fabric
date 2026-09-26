package net.ap12.admintool.fabric.ui.impl.item.builder

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.ItemStackSerializer
import net.minecraft.world.item.ItemStack

@Serializable
data class ItemBuilderPlayerData(
    @Serializable(ItemStackSerializer::class) var stack: ItemStack? = null,
    val history: MutableList<HistoryEntry> = ArrayDeque(),
) {
    @Serializable
    data class HistoryEntry(
        @Serializable(ItemStackSerializer::class) val stack: ItemStack,
        val recordAt: LocalDateTime,
        var undo: Boolean = false,
    )
}
