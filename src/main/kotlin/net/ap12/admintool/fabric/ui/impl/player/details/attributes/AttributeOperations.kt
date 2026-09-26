package net.ap12.admintool.fabric.ui.impl.player.details.attributes

import am.ik.yavi.arguments.StringValidator
import am.ik.yavi.builder.StringValidatorBuilder
import net.ap12.admintool.fabric.i18n.t
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.ai.attributes.AttributeModifier

fun AttributeModifier.Operation.toComponent(): Component {
    val key = this.key()
    val hoverEvent =
        HoverEvent.ShowText(
            t("admintool.ui.player.details.attribute.modifier.operation.${key}.description")
        )
    return t("admintool.ui.player.details.attribute.modifier.operation.${key}")
        .withStyle(Style.EMPTY.withHoverEvent(hoverEvent))
}

fun AttributeModifier.Operation.key(): String =
    when (this) {
        AttributeModifier.Operation.ADD_VALUE -> "add_value"
        AttributeModifier.Operation.ADD_MULTIPLIED_BASE -> "add_multiplied_base"
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL -> "add_multiplied_total"
    }

fun fromKey(key: String): AttributeModifier.Operation? =
    when (key) {
        "add_value" -> AttributeModifier.Operation.ADD_VALUE
        "add_multiplied_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        "add_multiplied_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        else -> null
    }

val operationValidator: StringValidator<AttributeModifier.Operation> =
    StringValidatorBuilder.of("operation") { constraint ->
            constraint.oneOf(AttributeModifier.Operation.entries.map { it.key() })
        }
        .build(::fromKey)
