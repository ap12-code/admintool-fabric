package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

class MenuModule<T : AbstractContainerMenu>(private val menuType: MenuType<T>) : UIModule {
    private val menuTypeKey = requireNotNull(BuiltInRegistries.MENU.getKey(menuType))

    override val id: Key = AdminToolMod.key("menu.${menuTypeKey.toLanguageKey()}")

    private val material
        get() = getMaterial(menuType)

    override fun create(holder: AdminToolUIHolder) =
        item(material) {
            name(material.descriptionId)

            lore {
                +""
                +action("admintool.modules.menu.open", Keys.CLICK, t(material.descriptionId))
            }

            onClick("admintool.modules.menu.${menuTypeKey}") { context ->
                if (menuType == MenuType.FURNACE) {
                    // context.holder.plugin.virtualFurnace.open(context.player)
                } else {
                    context.player.openMenu(
                        object : MenuProvider {
                            override fun getDisplayName(): Component {
                                return Component.translatable(
                                    menuTypeKey.toLanguageKey("menu_type")
                                )
                            }

                            override fun createMenu(
                                containerId: Int,
                                inventory: Inventory,
                                player: Player,
                            ): AbstractContainerMenu {
                                return menuType.create(containerId, inventory)
                            }
                        }
                    )
                }
            }
        }

    companion object {
        val SUPPORTED_MENU_TYPES =
            listOf(
                MenuType.CRAFTING,
                MenuType.FURNACE,
                MenuType.STONECUTTER,
                MenuType.LOOM,
                MenuType.ANVIL,
                MenuType.ENCHANTMENT,
                MenuType.GRINDSTONE,
            )

        private fun getMaterial(menu: MenuType<*>): Item =
            when (menu) {
                MenuType.ANVIL -> Items.ANVIL
                MenuType.ENCHANTMENT -> Items.ENCHANTING_TABLE
                MenuType.CRAFTING -> Items.CRAFTING_TABLE
                MenuType.STONECUTTER -> Items.STONECUTTER
                MenuType.FURNACE -> Items.FURNACE
                MenuType.LOOM -> Items.LOOM
                MenuType.GRINDSTONE -> Items.GRINDSTONE
                else -> throw IllegalArgumentException("Unsupported menu type: $menu")
            }

        fun createAllMenus(): List<MenuModule<*>> {
            return SUPPORTED_MENU_TYPES.map { MenuModule(it) }
        }
    }
}
