package net.ap12.admintool.ui.impl.player.details.attributes

import java.text.DecimalFormat
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.ui.impl.player.details.attributes.attributeModifierValidator
import net.ap12.admintool.fabric.ui.impl.player.details.attributes.key
import net.ap12.admintool.fabric.ui.impl.player.details.attributes.toComponent
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.join
import net.ap12.admintool.fabric.util.toComponent
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier

class UIPlayerAttributeModifierValue(
    private val mode: String,
    private val attribute: Holder<Attribute>,
    private val modifier: AttributeModifier? = null,
) : UI {
    override val id =
        AdminToolMod.key("player.details.attribute.modifier.${attribute.value().descriptionId}")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val key = holder.createStringKey()
            val amount = holder.createStringKey()
            val operation = holder.createStringKey()

            base {
                title(
                    t(
                        "admintool.ui.player.details.attribute.modifier.${mode}.dialog",
                        if (modifier != null) Component.literal(modifier.id.asString())
                        else Component.translatable(attribute.value().descriptionId),
                    )
                )
                textInput(key) {
                    label("admintool.ui.player.details.attribute.modifier.key")
                    initial(modifier?.id?.asString() ?: "")
                }
                textInput(amount) {
                    label("admintool.ui.player.details.attribute.modifier.amount")
                    initial(DECIMAL_FORMAT.format(modifier?.amount ?: 0.0))
                }
                singleOption(operation) {
                    label("admintool.ui.player.details.attribute.modifier.operation")

                    AttributeModifier.Operation.entries.forEach {
                        option(it.key(), it.toComponent())
                    }

                    initial(
                        modifier?.operation?.key() ?: AttributeModifier.Operation.ADD_VALUE.key()
                    )
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
                                attributeModifierValidator.validate(
                                    response.getStringOrNull(key),
                                    response.getStringOrNull(amount),
                                    response.getStringOrNull(operation),
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
