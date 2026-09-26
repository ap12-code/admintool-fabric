package net.ap12.admintool.fabric.ui.impl.player.details.attributes

import am.ik.yavi.arguments.Arguments3Validator
import am.ik.yavi.arguments.ArgumentsValidators
import net.ap12.admintool.fabric.util.Validators
import net.minecraft.world.entity.ai.attributes.AttributeModifier

val attributeModifierValidator: Arguments3Validator<String, String, String, AttributeModifier> =
    ArgumentsValidators.split(
            Validators.namespacedKey("key"),
            Validators.double("amount"),
            operationValidator,
        )
        .apply(::AttributeModifier)
