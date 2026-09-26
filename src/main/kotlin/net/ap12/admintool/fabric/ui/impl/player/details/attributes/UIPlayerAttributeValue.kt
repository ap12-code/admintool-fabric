package net.ap12.admintool.ui.impl.player.details.attributes

import java.text.DecimalFormat
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Validators
import net.ap12.admintool.fabric.util.components.join
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.toComponent
import net.kyori.adventure.key.Key
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.ai.attributes.Attribute

class UIPlayerAttributeValue(
    private val attribute: Holder<Attribute>,
    private val oldValue: Double,
) : UI {
    override val id: Key =
        AdminToolMod.key("player.details.attribute.${attribute.unwrapKey().get().key()}")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val baseValue = holder.createStringKey()

            base {
                title(
                    t(
                        "admintool.ui.player.details.attribute.base.dialog",
                        Component.translatable(attribute.value().descriptionId),
                    )
                )
                textInput(baseValue) {
                    label("admintool.ui.player.details.attribute.base")
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
                                    baseValue,
                                    Validators.double(attribute.value().descriptionId),
                                )
                            if (result.isValid) {
                                response.holder.back(result.value())
                            } else {
                                response.validationFailed(result.errors().toComponent().join())
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
