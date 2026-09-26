package net.ap12.admintool.fabric.ui.impl.player.details.abilities

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Validators
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.join
import net.ap12.admintool.fabric.util.toComponent
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import java.text.DecimalFormat

class UIPlayerAbilitiesSpeed(private val key: String, private val oldValue: Float) : UI {
    override val id: Key = AdminToolMod.key("player.details.abilities.$key")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val valueKey = holder.createStringKey()

            base {
                title("admintool.ui.player.details.abilities.$key.dialog")
                textInput(valueKey) {
                    label("admintool.ui.player.details.abilities.$key")
                    initial(DECIMAL_FORMAT.format(oldValue))
                }
                body {
                    holder.ifError { component -> text(component.copy().withColor(TextColor.RED)) }
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action { response ->
                            val result =
                                response.validateString(
                                    valueKey,
                                    Validators.float("admintool.ui.player.details.abilities.$key"),
                                )
                            if (result.isValid) {
                                response.holder.back(result.value())
                            } else {
                                response.validationFailed(result.errors().toComponent().join("\n"))
                            }
                        }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action { response -> response.holder.back(null) }
                    }
                }
            }
        }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("0.0##")
    }
}
