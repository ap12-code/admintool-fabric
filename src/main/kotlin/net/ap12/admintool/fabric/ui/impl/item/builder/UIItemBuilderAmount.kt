package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.minecraft.server.dialog.Dialog

class UIItemBuilderAmount(
    private val childKey: String,
    private val oldValue: Int,
    private val maxValue: Int,
    private val minValue: Int = 1,
) : UI {
    override val id: Key = AdminToolMod.key("item.builder.$childKey")

    override fun create(holder: AdminToolUIHolder): UIBuilder<Dialog> =
        dialog(holder) {
            val key = holder.createStringKey()
            base {
                title("admintool.ui.item.builder.$childKey.dialog", "$oldValue", "$maxValue")
                numberRange(key) {
                    label("admintool.ui.item.builder.$childKey.dialog.value")
                    range(minValue..maxValue)
                    step(1f)
                    initial(oldValue.coerceIn(minValue, maxValue).toFloat())
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.item.builder.$childKey.dialog.confirm")
                        action(holder.createStringKey()) { context ->
                            context.holder.back(context.getInt(key))
                        }
                    }
                    no {
                        label("admintool.ui.item.builder.$childKey.dialog.cancel")
                        action(holder.createStringKey()) { context -> context.holder.back(null) }
                    }
                }
            }
        }
}
