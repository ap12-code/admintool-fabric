package net.ap12.admintool.ui.impl.item

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.impl.item.RegisteredItemStack
import net.ap12.admintool.fabric.ui.impl.item.builder.UIItemBuilder
import net.ap12.admintool.fabric.ui.impl.item.manager.UIItemManager
import net.ap12.admintool.fabric.ui.impl.item.storage.UIStorage
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.inventory.*
import net.kyori.adventure.key.Key
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class UIItem : Tab {
    override val id: Key = AdminToolMod.key("item")

    private fun buildItem(element: RegisteredItemStack) =
        item(element.item.copy()) {
            lore {
                +""
                +action("admintool.ui.item.get", Keys.CLICK)
            }

            onClick("admintool.ui.item.${element.id}") { context ->
                val stack = element.item.copy().removeActionData()
                if (!context.isShift) {
                    context.player.inventory.add(stack.copy())
                } else {
                    val amount = if (context.isMiddleClick) stack.maxStackSize else 1
                    context.player.inventory.add(stack.copyWithCount(amount))
                }
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

    private fun getPage(holder: AdminToolUIHolder): Int {
        val visibility = holder.playerStore.itemUIData.mode
        val key = Key.key(this.id.namespace(), "${this.id.value()}_${visibility.name.lowercase()}")
        return holder.pageStore.getOrDefault(key, 1)
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val page = getPage(holder)
            val content = getContent(holder, page)
            val maxPage = holder.playerStore.itemUIData.stacks.maxOfOrNull { it.page } ?: 1
            for (element in content) {
                element.index to buildItem(element)
            }

            36..44 to Items.STAINED_GLASS_PANE.black
            36 to
                item(Items.ANVIL) {
                    name("admintool.ui.item.builder")

                    onClick("admintool.ui.item.builder") { context ->
                        context.go<Nothing>(UIItemBuilder("main"))
                    }
                }

            37 to
                item(Items.CHEST) {
                    name("admintool.ui.item.storage")

                    onClick("admintool.ui.item.storage") { context ->
                        context.go<Nothing>(UIStorage())
                    }
                }

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
            40 to item(Items.BOOK) { name("admintool.ui.page", "%d/%d".format(page, maxPage)) }
            41 to
                item(holder.plugin.heads.get("next")) {
                    name("admintool.ui.page.next")

                    onClick("admintool.ui.page.${key().value()}.next") { context ->
                        context.holder.pageStore.compute(key()) { _, _ ->
                            (page + 1).coerceAtMost(maxPage)
                        }
                        context.playSound(Sounds.next)
                        context.holder.update()
                    }
                }

            43 to
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

            44 to
                item(Items.WRITABLE_BOOK) {
                    name("admintool.ui.item.manager")

                    onClick("admintool.ui.item.manager") { context ->
                        context.go<Nothing>(UIItemManager())
                    }
                }
        }

    override fun bottomClick(context: ItemClickContext): Boolean {
        if (context.isShift) {
            context.holder.setItem(context.slot, context.holder.incrementStateId(), ItemStack.EMPTY)
            context.holder.broadcastFullState()
        }
        return super.bottomClick(context)
    }

    @Serializable
    data class Data(
        val stacks: MutableList<RegisteredItemStack> = mutableListOf(),
        var mode: ItemVisibility = ItemVisibility.PRIVATE,
    )
}
