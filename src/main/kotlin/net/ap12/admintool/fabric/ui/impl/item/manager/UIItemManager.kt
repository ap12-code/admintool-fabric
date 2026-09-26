package net.ap12.admintool.fabric.ui.impl.item.manager

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.ui.impl.item.RegisteredItemStack
import net.ap12.admintool.fabric.util.Placeholder
import net.ap12.admintool.fabric.util.inventory.*
import net.ap12.admintool.ui.impl.item.ItemVisibility
import net.ap12.admintool.util.isEmptyOrNull
import net.kyori.adventure.key.Key
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

class UIItemManager(private val initialPage: Int = 1) : UI {
    override val id: Key = AdminToolMod.key("item.manager")

    private fun getPage(holder: AdminToolUIHolder): Int {
        val visibility = holder.playerStore.itemUIData.mode
        val key = Key.key(this.id.namespace(), "${this.id.value()}_${visibility.name.lowercase()}")
        return holder.pageStore.getOrDefault(key, initialPage)
    }

    private fun buildItem(element: RegisteredItemStack): ItemBuilder =
        item(element.item.copy()) {
            lore {
                +""
                +action("admintool.ui.item.manager.unregister")
            }
        }

    private fun getContent(holder: AdminToolUIHolder, page: Int): List<RegisteredItemStack> {
        val mode = holder.playerStore.itemUIData.mode
        val data =
            when (mode) {
                ItemVisibility.PRIVATE -> holder.playerStore.itemUIData
                ItemVisibility.PUBLIC -> holder.publicStore.itemUIData
            }

        return data.stacks.filter { it.page == page }
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val page = getPage(holder)
            val content = getContent(holder, page)
            for (i in 0..35) {
                i to Placeholder.placeholder()
            }
            for (element in content) {
                element.index to buildItem(element)
            }

            36..44 to Items.STAINED_GLASS_PANE.black

            39 to
                item(holder.plugin.heads.get("prev")) {
                    name("admintool.ui.page.prev")

                    onClick("admintool.ui.page.${key().value()}.prev") { context ->
                        context.holder.pageStore.compute(key()) { _, _ ->
                            (page - 1).coerceAtLeast(1)
                        }
                        context.playSound(Sounds.previous)
                        context.holder.update()
                    }
                }
            40 to item(Items.BOOK) { name("admintool.ui.page", "%d".format(page)) }
            41 to
                item(holder.plugin.heads.get("next")) {
                    name("admintool.ui.page.next")

                    onClick("admintool.ui.page.${key().value()}.next") { context ->
                        context.holder.pageStore.compute(key()) { _, _ -> (page + 1) }
                        context.playSound(Sounds.next)
                        context.holder.update()
                    }
                }

            44 to
                item(Items.HOPPER) {
                    name("admintool.ui.item.mode")

                    val current = holder.playerStore.itemUIData.mode
                    lore {
                        +""
                        +field(
                            "admintool.ui.item.visibility.public",
                            current == ItemVisibility.PUBLIC,
                        )
                        +field(
                            "admintool.ui.item.visibility.private",
                            current == ItemVisibility.PRIVATE,
                        )
                        +""
                        +action("admintool.ui.item.mode.switch", Keys.CLICK)
                    }

                    onClick("admintool.ui.item.mode") { context ->
                        context.holder.editStore { itemUIData.mode = itemUIData.mode.inverted() }
                        context.holder.update()
                    }
                }
        }

    override fun onClick(context: ItemClickContext): Boolean {
        if (context.slot in 0..35) {
            val oldItem = context.topInventory.getSlot(context.slot).item.copy()
            val page = getPage(context.holder)
            val mode = context.holder.playerStore.itemUIData.mode

            if (oldItem.isEmptyOrNull() || Placeholder.isPlaceholder(oldItem)) {
                if (!context.cursor.isEmptyOrNull()) {
                    // place
                    val stack = context.cursor.copy()
                    val state =
                        when (mode) {
                            ItemVisibility.PUBLIC -> context.holder.publicStore.itemUIData.stacks
                            ItemVisibility.PRIVATE -> context.holder.playerStore.itemUIData.stacks
                        }.any { it.index == context.slot && it.page == page }
                    if (state) return true

                    context.topInventory.getSlot(context.slot).set(stack)
                    context.cursor = ItemStack.EMPTY
                    val entry = RegisteredItemStack(UUID.randomUUID(), context.slot, stack, page)

                    when (mode) {
                        ItemVisibility.PRIVATE ->
                            context.holder.editStore { itemUIData.stacks.add(entry) }
                        ItemVisibility.PUBLIC ->
                            context.holder.editPublicStore { itemUIData.stacks.add(entry) }
                    }
                    context.holder.update()
                    return true
                }
            } else if (!Placeholder.isPlaceholder(oldItem)) {
                // pick
                val originalItem =
                    when (mode) {
                        ItemVisibility.PUBLIC -> context.holder.publicStore.itemUIData.stacks
                        ItemVisibility.PRIVATE -> context.holder.playerStore.itemUIData.stacks
                    }.singleOrNull { it.index == context.slot && it.page == page }

                if (originalItem != null && !originalItem.item.isEmptyOrNull()) {
                    context.cursor = originalItem.item.copy().removeActionData()
                    when (mode) {
                        ItemVisibility.PUBLIC ->
                            context.holder.editPublicStore {
                                itemUIData.stacks.removeIf {
                                    it.index == context.slot && it.page == page
                                }
                            }
                        ItemVisibility.PRIVATE ->
                            context.holder.editStore {
                                itemUIData.stacks.removeIf {
                                    it.index == context.slot && it.page == page
                                }
                            }
                    }
                    context.topInventory.getSlot(context.slot).set(ItemStack.EMPTY)
                    context.holder.update()
                }
                return true
            }
        }
        return super.onClick(context)
    }
}
