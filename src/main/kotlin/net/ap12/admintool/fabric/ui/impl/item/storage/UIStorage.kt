package net.ap12.admintool.fabric.ui.impl.item.storage

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.paginator.UIIndexedPaginator
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.inventory.*
import net.ap12.admintool.util.isBlankOrNull
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack

class UIStorage : UIIndexedPaginator<ItemStack>() {
    override val id = AdminToolMod.key("item.storage")

    override fun buildItem(holder: AdminToolUIHolder, index: Int, element: ItemStack): ItemBuilder =
        item(element) {}

    override fun getContent(holder: AdminToolUIHolder, page: Int): List<ItemStack> {
        val result = NonNullList.withSize(36, ItemStack.EMPTY)
        val content = holder.playerStore.storage.content.filter { it.page == page }
        for (entry in content) {
            result[entry.index] = entry.item
        }
        return result.toList()
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            super.create(holder).merge()
            for (i in 0..35) {
                if (get(i).isBlankOrNull()) {
                    setItem(i, ItemStack.EMPTY)
                }
            }
        }

    override fun placeholderRange(): IntRange = 36..53

    private fun save(holder: AdminToolUIHolder, content: List<ItemStack?>) {
        val page = holder.pageStore.getOrDefault(key(), 1)
        val result = mutableListOf<StorageItem>()
        for ((i, element) in content.withIndex()) {
            if (!element.isBlankOrNull()) {
                result.add(StorageItem(page, i, element!!))
            }
        }

        holder.editStore {
            storage.content.removeIf { it.page == page }
            storage.content.addAll(result)
        }
    }

    private fun getCurrentContent(holder: AdminToolUIHolder): List<ItemStack?> {
        require(holder.key == key())
        val result = mutableListOf<ItemStack>()
        for (i in 0..<36) {
            result.add(holder.container.getItem(i))
        }
        return result
    }

    override fun onClick(context: ItemClickContext): Boolean {
        if (context.slot in 0..35) return false
        return super.onClick(context)
    }

    override fun onClose(holder: AdminToolUIHolder) {
        save(holder, getCurrentContent(holder))
    }

    override fun beforeNavigate(holder: AdminToolUIHolder, beforePage: Int) {
        save(holder, getCurrentContent(holder))
    }

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        save(holder, getCurrentContent(holder))
        next()
    }

    @Serializable data class Data(val content: MutableList<StorageItem> = mutableListOf())
}
