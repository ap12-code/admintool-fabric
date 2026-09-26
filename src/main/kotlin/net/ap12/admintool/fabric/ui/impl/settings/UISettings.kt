package net.ap12.admintool.fabric.ui.impl.settings

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Items

class UISettings : UI {
    override val id: Key = AdminToolMod.key("settings")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        inventory(holder) {
            val options = holder.playerStore.options
            0 to
                item(Items.ENDER_EYE) {
                    name("admintool.ui.settings.teleport_spectator")

                    lore {
                        +t("admintool.ui.settings.teleport_spectator.description")
                            .symbolPrefixed(TextColor.WHITE)
                        +""
                        TeleportSpectatorMode.entries.forEach {
                            +field(it, options.teleportSpectatorMode == it)
                        }
                        +""
                        +action("admintool.ui.settings.teleport_spectator.switch", Keys.CLICK)
                    }

                    onClick("admintool.ui.settings.teleport_spectator") { context ->
                        context.holder.editStore {
                            context.holder.playerStore.options.teleportSpectatorMode =
                                options.teleportSpectatorMode.next()
                        }
                        context.holder.update()
                    }
                }
        }
}
