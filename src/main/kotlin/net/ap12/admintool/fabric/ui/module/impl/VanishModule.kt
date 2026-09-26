package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.vanish.VanishManager
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Items

class VanishModule : UIModule {
    override val id: Key = AdminToolMod.key("vanish")

    private fun getVanisher(holder: AdminToolUIHolder): VanishManager {
        return holder.plugin.vanisher
    }

    private fun getState(holder: AdminToolUIHolder): Boolean {
        return getVanisher(holder).isVanished(holder.player)
    }

    override fun create(holder: AdminToolUIHolder): ItemBuilder =
        item(if (getState(holder)) Items.STAINED_GLASS.lime else Items.GLASS) {
            name("admintool.modules.vanish")

            lore {
                if (hasPermission(holder)) {
                    if (getState(holder)) {
                        +field(t("admintool.value.state.on"), TextColor.GREEN)
                        +""
                        +action("admintool.modules.vanish.disable", Keys.CLICK)
                    } else {
                        +field(t("admintool.value.state.off"), TextColor.GRAY)
                        +""
                        +action("admintool.modules.vanish.enable", Keys.CLICK)
                    }
                } else {
                    +field("admintool.modules.unavaiable.permission")
                }
            }

            onClick("admintool.modules.vanish") { context ->
                if (hasPermission(context.holder)) {
                    val result = getVanisher(context.holder).toggle(context.player)
                    if (result) {
                        context.feedback("admintool.modules.vanish.feedback.hide")
                    } else {
                        context.feedback("admintool.modules.vanish.feedback.show")
                    }
                    context.close()
                }
            }
        }
}
