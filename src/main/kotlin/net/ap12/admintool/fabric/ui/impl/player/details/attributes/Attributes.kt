package net.ap12.admintool.fabric.ui.impl.player.details.attributes

import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier

fun calculateAmount(instance: AttributeInstance): Double {
    var base = instance.baseValue
    instance.modifiers
        .filter { it.operation == AttributeModifier.Operation.ADD_VALUE }
        .forEach { base += it.amount }

    var d = base

    instance.modifiers
        .filter { it.operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE }
        .forEach { d += base * it.amount }
    instance.modifiers
        .filter { it.operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL }
        .forEach { d *= 1.0 + it.amount }

    return d
}
