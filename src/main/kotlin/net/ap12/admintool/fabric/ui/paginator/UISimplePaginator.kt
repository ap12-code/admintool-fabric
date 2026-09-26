package net.ap12.admintool.fabric.ui.paginator

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.kyori.adventure.key.Key

class UISimplePaginator<E>(
    override val id: Key,
    startRow: Int = 1,
    endRow: Int = 4,
    private val contentGetter: (holder: AdminToolUIHolder) -> List<E>,
    private val itemBuilder: (holder: AdminToolUIHolder, element: E) -> ItemBuilder,
    controlOffset: Int = 0,
) : UIPaginator<E>(startRow, endRow, controlOffset) {
    override fun buildItem(holder: AdminToolUIHolder, element: E): ItemBuilder =
        itemBuilder(holder, element)

    override fun getContent(holder: AdminToolUIHolder): List<E> = contentGetter(holder)
}
