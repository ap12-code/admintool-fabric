package net.ap12.admintool.fabric.ui.impl.home

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.impl.item.builder.UIItemBuilder
import net.ap12.admintool.fabric.ui.module.impl.*
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

class UIHome : Tab {
    override val id: Key = AdminToolMod.key("home")
    override val size: Int = 54

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            listOf(GameType.CREATIVE, GameType.SPECTATOR, GameType.SURVIVAL, GameType.ADVENTURE)
                .forEachIndexed { index, mode -> index to GamemodeModule(mode) }

            8 to HealModule()
            9 to VanishModule()
            10 to FlyModule()

            MenuModule.SUPPORTED_MENU_TYPES.forEachIndexed { index, menu ->
                (36 + index) to MenuModule(menu)
            }

            44 to
                item(Items.IRON_PICKAXE) {
                    name("admintool.ui.item.builder")

                    onClick("admintool.ui.home.item.builder") { context ->
                        context.go<Nothing>(UIItemBuilder("main"))
                    }
                }
        }
}
