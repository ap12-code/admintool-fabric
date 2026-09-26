package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import java.util.*
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key

class UISimpleWaypointEditor(private val parent: UUID?) : UI {
    override val id: Key = AdminToolMod.key("waypoint.editor.quick")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val name = holder.createStringKey()
            val type = holder.createStringKey()
            base {
                title("admintool.ui.waypoint.editor.quick")
                textInput(name) { label("admintool.ui.waypoint.editor.name") }
                singleOption(type) {
                    option("waypoint", t("admintool.ui.waypoint.editor.type.point"))
                    option("group", t("admintool.ui.waypoint.editor.type.group"))
                }
            }
            type {
                confirmation {
                    yes {
                        action { response ->
                            response.validateString(
                                name,
                                "{0} must not be blank",
                                String::isNotBlank,
                            )
                        }
                    }
                    no { action { response -> response.holder.back(null) } }
                }
            }
        }
}
