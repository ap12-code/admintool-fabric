package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key

class UIWaypointEditorName(private val data: WaypointEditorPlayerData) : UI {
    override val id: Key = AdminToolMod.key("waypoint.editor.name")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val key = holder.createStringKey()
            base {
                title("admintool.ui.waypoint.editor.name.set")
                textInput(key) {
                    label("admintool.ui.waypoint.editor.name")
                    initial(data.name ?: "")
                    labelVisible(false)
                }
            }
            type {
                confirmation {
                    yes {
                        action { event -> event.holder.back(event.getStringOrNull(key)) }
                        label("admintool.ui.waypoint.editor.name.confirm")
                    }
                    no {
                        label("admintool.ui.waypoint.editor.name.cancel")
                        action { event -> event.holder.back(null) }
                    }
                }
            }
        }
}
