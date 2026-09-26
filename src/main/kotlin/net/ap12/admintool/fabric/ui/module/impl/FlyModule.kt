package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Items

class FlyModule : UIModule {
    override val id: Key = AdminToolMod.key("fly")

    override fun create(holder: AdminToolUIHolder): ItemBuilder =
        item(Items.FEATHER) {
            name("admintool.modules.fly")
            glint(holder.player.abilities.mayfly)

            lore {
                if (holder.player.abilities.mayfly) {
                    +field(t("admintool.value.state.on"), TextColor.GREEN)
                    +""
                    +action("admintool.modules.fly.disable", Keys.CLICK)
                } else {
                    +field(t("admintool.value.state.off"), TextColor.GRAY)
                    +""
                    +action("admintool.modules.fly.enable", Keys.CLICK)
                }
            }

            onClick("admintool.modules.fly") { context ->
                val value = !context.player.abilities.mayfly
                context.player.abilities.mayfly = value
                if (value) {
                    context.feedback("admintool.modules.fly.feedback.activate")
                } else {
                    context.feedback("admintool.modules.fly.feedback.deactivate")
                    context.player.abilities.flying = false
                }
                context.player.onUpdateAbilities()
                context.close()
            }
        }
}
