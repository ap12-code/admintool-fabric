package net.ap12.admintool.fabric.ui.impl.player

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key

class UIPlayerSearch : UI {
    override val id: Key = AdminToolMod.key("player.search")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val keyword = holder.createStringKey()
            base {
                title("admintool.ui.player.search.dialog")
                textInput(keyword) {
                    label("admintool.ui.player.search.dialog.name")
                    maxLength(32)
                }
            }
            type {
                notice {
                    action {
                        label("admintool.ui.player.search.dialog.search")
                        action { response ->
                            response.holder.back(response.getStringOrNull(keyword))
                        }
                    }
                }
            }
        }
}
