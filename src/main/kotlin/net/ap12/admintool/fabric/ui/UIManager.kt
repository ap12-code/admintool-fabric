package net.ap12.admintool.fabric.ui

import java.util.*
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.module.UIModules
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.ui.tab.Tabs
import net.kyori.adventure.key.Key
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

class UIManager(private val plugin: AdminToolMod) : IUIManager {
    override val tabs: List<Tab> = Tabs.entries.map { it.creator() }
    override val wrapperCreator: (baseUI: UI, showTabs: Boolean) -> UI = AdminToolUI::createUI

    private val holders = mutableMapOf<UUID, AdminToolUIHolder>()

    val openItem
        get() = createItem()

    val modules = UIModules()

    private fun createItem(): ItemStack {
        return ItemStack(Items.MUSIC_DISC_13).also {
            val tag = CompoundTag()
            tag.putBoolean("admintool", true)

            val patch = DataComponentPatch.builder()
            patch.set(DataComponents.ITEM_NAME, t("admintool.name"))
            patch.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            patch.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
            patch.remove(DataComponents.JUKEBOX_PLAYABLE)

            it.applyComponents(patch.build())
        }
    }

    fun giveItem(player: ServerPlayer) {
        val stack = openItem.copy()
        stack.set(DataComponents.ITEM_NAME, t("admintool.name").translate(player))
        player.addItem(stack)
    }

    override fun open(player: ServerPlayer) {
        val data = plugin.dataStore.get(player.nameAndId())
        val defaultTab =
            tabs.find { it.key() == Key.key(plugin.config.defaultTab) } ?: Tabs.HOME.creator()

        val lastTab = tabs.find { it.key().value() == data.lastTab } ?: defaultTab
        open(AdminToolUI(lastTab), player)
    }

    fun open(ui: UI, player: ServerPlayer) {
        player.openMenu(AdminToolUIHolder.Provider(plugin, ui))
        holders[player.uuid] = player.containerMenu as AdminToolUIHolder
    }

    fun getHolders(): List<AdminToolUIHolder> {
        return holders.values.toList()
    }

    typealias Condition<T> = (holder: AdminToolUIHolder, ui: T) -> Boolean

    fun getHolder(player: ServerPlayer): AdminToolUIHolder? {
        return holders[player.uuid]
    }

    inline fun <reified T : UI> getInstances(
        noinline condition: Condition<T>? = null
    ): Map<AdminToolUIHolder, T> {
        return getHolders()
            .filter {
                it.currentUI?.isInstance(T::class.java) == true &&
                    (condition?.invoke(it, it.currentUI as T) ?: true)
            }
            .toList()
            .associateWith { it.currentUI as T }
    }

    inline fun <reified T : UI> updateIf(noinline condition: Condition<T>?) {
        getInstances<T>(condition).forEach { (holder, _) -> holder.update() }
    }
}
