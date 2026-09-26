package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items

class HealModule : UIModule {
    override val id: Key = AdminToolMod.key("heal")

    override fun create(holder: AdminToolUIHolder): ItemBuilder =
        item(Items.GOLDEN_APPLE) {
            name("admintool.modules.heal")

            lore {
                +""
                +action("admintool.modules.heal.heal", Keys.CLICK)
            }

            onClick("admintool.modules.heal") { context ->
                val maxHealth = context.player.getAttribute(Attributes.MAX_HEALTH)?.value ?: 20.0
                context.player.health = maxHealth.toFloat()
                context.player.foodData.foodLevel = 20
                context.player.foodData.setSaturation(20f)
                context.feedback("admintool.modules.heal.feedback")
                if (!context.isShift) context.close()
            }
        }
}
