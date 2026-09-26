package net.ap12.admintool.fabric.ui

import net.ap12.admintool.fabric.ui.impl.settings.UISettings
import net.ap12.admintool.fabric.util.Placeholder
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.hasPermission
import net.ap12.admintool.fabric.util.inventory.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class AdminToolUI(val baseUI: UI, val showTabs: Boolean = true) : UI {
    override val id: Key = baseUI.key()
    override val title = baseUI.title

    private fun mergeInventory(
        holder: AdminToolUIHolder,
        base: UIBuilder<ContainerWithTitle>,
        other: UIBuilder<ContainerWithTitle>,
    ) =
        inventory(holder) {
            val createdBase = base.build()
            val createdOther = other.build()

            for (i in 0..<createdBase.containerSize) createdBase.getSlot(i)?.get()?.let {
                setItem(i, it)
            }
            for (i in 0..<createdBase.containerSize) createdOther.getSlot(i)?.get()?.let {
                if (!createdOther.getItem(i).isEmpty) {
                    setItem(i, it)
                }
            }
        }

    private fun createBase(holder: AdminToolUIHolder) =
        inventory(holder) {
            if (showTabs) {
                baseUI.placeholderRange() to Placeholder.placeholder()

                for ((i, element) in holder.plugin.ui.tabs.withIndex()) {
                    val tab = element
                    if (holder.player.hasPermission(tab.permission, true)) {
                        45 + i to
                            item(
                                ItemStack(
                                    (if (tab.key() == baseUI.key()) Items.STAINED_GLASS_PANE.red
                                    else Items.STAINED_GLASS_PANE.lightBlue),
                                    i + 1,
                                )
                            ) {
                                name(tab.translationKey())
                                onClick("tab_${tab.id.value()}") { context ->
                                    context.holder.changeTab(tab)
                                    context.holder.plugin.dataStore.edit(
                                        holder.player.nameAndId()
                                    ) {
                                        lastTab = element.key().value()
                                    }
                                    context.playSound(Sounds.click2)
                                }
                            }
                    } else {
                        45 + i to
                            item(Items.STAINED_GLASS_PANE.lightGray()) {
                                name(tab.translationKey())
                                this.lore { Component.translatable("") }
                            }
                    }
                }
                52 to
                    item(Items.MUSIC_DISC_13) {
                        name("admintool.ui.settings")

                        onClick("admintool.ui.settings") { context ->
                            context.go<Nothing>(UISettings())
                        }
                    }
            }
            53 to
                item(Items.BARRIER) {
                    name("admintool.ui.${if (holder.hasPrevious) "back" else "close"}")
                    onClick("close") { context -> context.back(null) }
                }
        }

    @Suppress("UNCHECKED_CAST")
    override fun create(holder: AdminToolUIHolder): UIBuilder<*> {
        val tabBuilder = baseUI.create(holder)
        if (tabBuilder.getType() == UIType.DIALOG) return tabBuilder

        return mergeInventory(
            holder,
            createBase(holder),
            tabBuilder as UIBuilder<ContainerWithTitle>,
        )
    }

    override fun onClose(holder: AdminToolUIHolder) = baseUI.onClose(holder)

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) = baseUI.onBack(holder, next)

    override fun onClick(context: ItemClickContext): Boolean = baseUI.onClick(context)

    override fun bottomClick(context: ItemClickContext): Boolean = baseUI.bottomClick(context)

    override fun <U : UI> isInstance(ui: Class<U>): Boolean {
        return ui.isInstance(this.baseUI)
    }

    companion object {
        fun createUI(baseUI: UI, showTabs: Boolean): AdminToolUI = AdminToolUI(baseUI, showTabs)
    }
}
