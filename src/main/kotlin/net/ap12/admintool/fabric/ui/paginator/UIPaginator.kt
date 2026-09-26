package net.ap12.admintool.fabric.ui.paginator

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.world.item.Items
import kotlin.math.ceil

abstract class UIPaginator<E>(
    startRow: Int = 1,
    endRow: Int = 4,
    private val controlOffset: Int = 0,
) : UI {
    val startIndex = (startRow - 1) * 9
    val endIndex = (endRow - 1) * 9 + 9
    val pageSize = endIndex - startIndex

    open fun filterContent(element: E): Boolean = true

    abstract fun buildItem(holder: AdminToolUIHolder, element: E): ItemBuilder

    abstract fun getContent(holder: AdminToolUIHolder): List<E>

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            val contents = getContent(holder).filter { filterContent(it) }
            val maxPage = ceil(contents.size / pageSize.toDouble()).toInt().coerceAtLeast(1)
            val page = holder.pageStore.getOrDefault(this@UIPaginator.key(), 1).coerceIn(1, maxPage)
            val content = contents.chunked(pageSize).getOrNull(page - 1).orEmpty()
            for ((i, element) in content.withIndex()) {
                (i + startIndex) to buildItem(holder, element)
            }

            val controlStartIndex = endIndex
            var offset = 8
            if (controlStartIndex + 8 == 53) offset = 7
            controlStartIndex..(controlStartIndex + offset) to Items.STAINED_GLASS_PANE.black
            (controlStartIndex + controlOffset + 3) to
                item(holder.plugin.heads.get("prev")) {
                    name("admintool.ui.page.prev")

                    onClick("admintool.ui.page.${this@UIPaginator.key().value()}.prev") { context ->
                        context.holder.pageStore.compute(this@UIPaginator.key()) { _, _ ->
                            (page - 1).coerceAtLeast(1)
                        }
                        context.playSound(Sounds.previous)
                        context.holder.update()
                    }
                }
            (controlStartIndex + controlOffset + 4) to
                item(Items.BOOK) { name("admintool.ui.page", "%d/%d".format(page, maxPage)) }
            (controlStartIndex + controlOffset + 5) to
                item(holder.plugin.heads.get("next")) {
                    name("admintool.ui.page.next")

                    onClick("admintool.ui.page.${this@UIPaginator.key().value()}.next") { context ->
                        context.holder.pageStore.compute(this@UIPaginator.key()) { _, _ ->
                            (page + 1).coerceAtMost(maxPage)
                        }
                        context.playSound(Sounds.next)
                        context.holder.update()
                    }
                }
        }
}
