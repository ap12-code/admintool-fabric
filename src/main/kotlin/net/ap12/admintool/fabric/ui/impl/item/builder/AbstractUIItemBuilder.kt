package net.ap12.admintool.fabric.ui.impl.item.builder

import kotlin.time.Clock
import kotlinx.datetime.toLocalDateTime
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.components.split
import net.ap12.admintool.fabric.util.components.wrapText
import net.ap12.admintool.fabric.util.inventory.*
import net.ap12.admintool.util.isEmptyOrNull
import net.kyori.adventure.key.Key
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

abstract class AbstractUIItemBuilder(private val dataKey: String, private val key: String) : UI {
    override val id: Key = AdminToolMod.key("item.builder.$key")
    override val title: Component = t("admintool.ui.item.builder")

    abstract fun createChild(
        holder: AdminToolUIHolder,
        data: ItemBuilderPlayerData,
    ): UIBuilder<ContainerWithTitle>

    private fun canPickup(): Boolean {
        return key == ""
    }

    protected fun getData(holder: AdminToolUIHolder): ItemBuilderPlayerData {
        return holder.playerStore.itemBuilder.getOrPut(dataKey) { ItemBuilderPlayerData() }
    }

    protected fun undo(holder: AdminToolUIHolder) {
        val data = getData(holder)
        if (data.history.isEmpty()) return
        val undoIndex = data.history.indexOfLast { !it.undo }
        val updatedHistoryEntry = data.history[undoIndex].copy(undo = true)
        holder.editStore {
            itemBuilder[dataKey]!!.stack = data.stack
            itemBuilder[dataKey]!!.history[undoIndex] = updatedHistoryEntry
        }
        holder.update()
    }

    protected fun redo(holder: AdminToolUIHolder) {
        val data = getData(holder)
        if (data.history.isEmpty()) return
        val lastUndoIndex = data.history.indexOfFirst { it.undo }
        val updatedHistoryEntry = data.history[lastUndoIndex].copy(undo = true)
        holder.editStore {
            itemBuilder[dataKey]!!.stack = data.stack
            itemBuilder[dataKey]!!.history[lastUndoIndex] = updatedHistoryEntry
        }
        holder.update()
    }

    private fun createBase(holder: AdminToolUIHolder, data: ItemBuilderPlayerData) =
        inventory(holder) {
            if (data.stack.isEmptyOrNull()) {
                0 to
                    item(Items.STAINED_GLASS_PANE.lightGray) {
                        name("admintool.ui.item.builder.placeholder")

                        lore {
                            +""
                            +t("admintool.ui.item.builder.placeholder.description")
                        }

                        onClick("admintool.ui.item.builder.placeholder") { context ->
                            if (context.cursor.isEmptyOrNull() && data.stack.isEmptyOrNull()) {
                                // TODO: select material
                            } else {
                                if (data.stack.isEmptyOrNull()) {
                                    context.holder.editStore {
                                        itemBuilder[dataKey]!!.stack =
                                            context.player.inventoryMenu.carried
                                    }
                                    context.player.inventoryMenu.carried = ItemStack.EMPTY
                                    context.player.inventoryMenu.broadcastFullState()
                                }
                                context.holder.update()
                            }
                        }
                    }
            } else {
                0 to
                    item(data.stack!!) {
                        onClick("admintool.ui.item.builder.placeholder") { context ->
                            if (!this@AbstractUIItemBuilder.canPickup()) return@onClick
                            val resultStack = data.stack!!.removeActionData()
                            if (context.isShift) {
                                context.player.inventory.add(resultStack)
                            } else {
                                context.cursor
                            }
                            context.holder.editStore {
                                itemBuilder[dataKey] = ItemBuilderPlayerData()
                            }
                            context.holder.update()
                        }
                    }
                1 to
                    item(Items.BOOK) {
                        name("admintool.ui.item.builder.nbt")

                        val nbtStr =
                            NbtUtils.toPrettyComponent(
                                    ItemStack.CODEC.encodeStart(
                                            NbtOps.INSTANCE,
                                            data.stack!!.copy().removeActionData(),
                                        )
                                        .result()
                                        .get()
                                )
                                .copy()
                                .withStyle(Style.EMPTY.withItalic(false))
                                .split(Component.literal("\n"))
                                .map { if (it.string.length > 100) it.wrapText(100) else it }
                                .take(250)

                        lore(*nbtStr.toTypedArray())

                        onClick("admintool.ui.item.builder.nbt") { context ->
                            context.go<ItemStack?>(UIItemBuilderNBT(data.stack!!)) { result ->
                                if (result != null) {
                                    context.holder.editStore {
                                        itemBuilder[dataKey]?.stack = result
                                    }
                                }
                            }
                        }
                    }
            }

            9..17 to Items.STAINED_GLASS_PANE.black
        }

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            val data = getData(holder)
            45..52 to Items.STAINED_GLASS_PANE.black
            createChild(holder, data).merge()
            createBase(holder, data).merge()
        }

    protected open fun updateStack(
        holder: AdminToolUIHolder,
        oldStack: ItemStack,
        newStack: ItemStack,
    ) {
        getData(holder)
        val now = Clock.System.now().toLocalDateTime(holder.plugin.config.timezone)
        val historyEntry = ItemBuilderPlayerData.HistoryEntry(oldStack.copy(), now)
        holder.editStore {
            itemBuilder[dataKey]!!.stack = newStack.copy().removeActionData()
            if (oldStack != newStack) itemBuilder[dataKey]!!.history.addLast(historyEntry)
        }
        holder.update()
    }
}
