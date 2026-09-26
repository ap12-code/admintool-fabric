package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.components.mixedSerializer
import net.ap12.admintool.fabric.util.components.toAdventure
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.join
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component

class UIItemBuilderLore(private val initial: List<Component>) : UI {
    override val id: Key = AdminToolMod.key("item.builder.lore")
    private val miniMessage = MiniMessage.miniMessage()

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val textInputKey = holder.createStringKey()
            val initialStr =
                (miniMessage.serializeOrNull(initial.join("\n").toAdventure()) ?: "")
                    .replace("<br>", "\n")
                    .replace("<newline>", "\n")

            base {
                title("admintool.ui.item.builder.lore.dialog")
                textInput(textInputKey) {
                    labelVisible(false)
                    initial(initialStr)
                    multiline { maxLines(50) }
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action(holder.createStringKey()) { event ->
                            event.holder.back(
                                event
                                    .getStringOrNull(textInputKey)
                                    ?.let { result ->
                                        result.split("\n").map { mixedSerializer.deserialize(it) }
                                    }
                                    .orEmpty()
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
