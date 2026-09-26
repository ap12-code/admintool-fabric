package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.components.mixedSerializer
import net.ap12.admintool.fabric.util.components.toAdventure
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.Dialog

class UIItemBuilderName(private val key: String, private val initial: Component?) : UI {
    override val id: Key = AdminToolMod.key("item.builder.$key")
    private val miniMessage = MiniMessage.miniMessage()

    override fun create(holder: AdminToolUIHolder): UIBuilder<Dialog> =
        dialog(holder) {
            val textInputKey = holder.createStringKey()
            val initialStr = miniMessage.serializeOrNull(initial?.toAdventure()) ?: ""

            base {
                title("admintool.ui.item.builder.name.dialog")
                textInput(textInputKey) {
                    labelVisible(false)
                    initial(initialStr)
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action(holder.createStringKey()) { event ->
                            event.holder.back(
                                mixedSerializer.deserializeOrNull(
                                    event.getStringOrNull(textInputKey)
                                )
                            )
                        }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action(holder.createStringKey()) { event -> event.holder.back(null) }
                    }
                }
            }
        }
}
