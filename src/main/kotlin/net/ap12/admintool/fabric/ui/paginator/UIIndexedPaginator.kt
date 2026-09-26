package net.ap12.admintool.fabric.ui.paginator

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.world.item.Items

abstract class UIIndexedPaginator<E>(
    startRow: Int = 1,
    endRow: Int = 4,
    private val controlOffset: Int = 0,
    private val initialPage: Int = 1,
) : UI {
    val startIndex = (startRow - 1) * 9
    val endIndex = (endRow - 1) * 9 + 9

    open fun filterContent(element: E): Boolean = true

    abstract fun buildItem(holder: AdminToolUIHolder, index: Int, element: E): ItemBuilder

    abstract fun getContent(holder: AdminToolUIHolder, page: Int): List<E>

    protected open fun beforeNavigate(holder: AdminToolUIHolder, beforePage: Int) {}

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            val page = holder.pageStore.getOrDefault(this@UIIndexedPaginator.key(), initialPage)
            val content = getContent(holder, page).filter { filterContent(it) }

            for ((i, element) in content.withIndex()) {
                (i + startIndex) to buildItem(holder, i, element)
            }

            val controlStartIndex = endIndex
            var offset = 8
            if (controlStartIndex + 8 == 53) offset = 7
            controlStartIndex..(controlStartIndex + offset) to Items.STAINED_GLASS_PANE.black
            (controlStartIndex + controlOffset + 3) to
                item(holder.plugin.heads.get("prev")) {
                    name("admintool.ui.page.prev")

                    onClick("admintool.ui.page.${this@UIIndexedPaginator.key().value()}.prev") {
                        context ->
                        beforeNavigate(context.holder, page)
                        context.holder.pageStore.compute(this@UIIndexedPaginator.key()) { _, _ ->
                            (page - 1).coerceAtLeast(1)
                        }
                        context.playSound(Sounds.previous)
                        context.holder.update()
                    }
                }
            (controlStartIndex + controlOffset + 4) to
                item(Items.BOOK) { name("admintool.ui.page", page.toString()) }
            (controlStartIndex + controlOffset + 5) to
                item(holder.plugin.heads.get("next")) {
                    name("admintool.ui.page.next")

                    onClick("admintool.ui.page.${this@UIIndexedPaginator.key().value()}.next") {
                        context ->
                        beforeNavigate(context.holder, page)
                        context.holder.pageStore.compute(this@UIIndexedPaginator.key()) { _, _ ->
                            (page + 1)
                        }
                        context.playSound(Sounds.next)
                        context.holder.update()
                    }
                }
        }
}
