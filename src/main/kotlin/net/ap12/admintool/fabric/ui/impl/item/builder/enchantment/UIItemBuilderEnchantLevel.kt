package net.ap12.admintool.fabric.ui.impl.item.builder.enchantment

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment

class UIItemBuilderEnchantLevel(
    private val enchantment: Holder<Enchantment>,
    private val oldLevel: Int? = null,
) : UI {
    override val id: Key = AdminToolMod.key("item.builder.enchant.level")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val key = holder.createStringKey()
            base {
                title(
                    t(
                        "admintool.ui.item.builder.enchant.level.dialog",
                        enchantment.value().description,
                    )
                )
                numberRange(key) {
                    label("admintool.ui.item.builder.enchant.level")
                    range(1..255)
                    step(1f)
                    initial(oldLevel?.toFloat() ?: 1f)
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action { context -> context.holder.back(context.getInt(key)) }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action { context -> context.holder.back(null) }
                    }
                }
            }
        }
}
