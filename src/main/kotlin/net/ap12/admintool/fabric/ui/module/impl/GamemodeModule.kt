package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

class GamemodeModule(private val mode: GameType) : UIModule {
    override val id: Key = AdminToolMod.key("gamemode.${mode.name.lowercase()}")

    private val material
        get() =
            when (mode) {
                GameType.CREATIVE -> Items.GRASS_BLOCK
                GameType.SURVIVAL -> Items.IRON_SWORD
                GameType.ADVENTURE -> Items.MAP
                GameType.SPECTATOR -> Items.ENDER_EYE
            }

    override fun create(holder: AdminToolUIHolder): ItemBuilder =
        item(material) {
            name(mode.shortDisplayName)

            lore {
                +""
                if (hasPermission(holder)) {
                    +action("admintool.modules.gamemode.switch", Keys.CLICK, mode.longDisplayName)
                } else {
                    +field("admintool.modules.unavailable.permission")
                }
            }

            onClick("admintool.modules.gamemode.${mode.toString().lowercase()}") { context ->
                if (hasPermission(context.holder)) {
                    context.player.setGameMode(mode)
                    context.playSound(Sounds.click3)
                    context.feedback(t("admintool.modules.gamemode.feedback", mode.longDisplayName))
                } else {
                    context.feedback("admintool.command.no_permission")
                }
                context.close()
            }
        }

    companion object {
        private const val PERMISSION = "minecraft.command.gamemode"

        fun createAllGameModes(): List<GamemodeModule> {
            return GameType.entries.map(::GamemodeModule)
        }
    }
}
