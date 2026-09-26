package net.ap12.admintool.fabric.util

import am.ik.yavi.arguments.StringValidator
import am.ik.yavi.builder.StringValidatorBuilder
import am.ik.yavi.core.ConstraintViolation
import am.ik.yavi.core.ConstraintViolations
import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.Translatable
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier

class Validators private constructor() {
    companion object {
        fun float(name: String): StringValidator<Float> =
            StringValidatorBuilder.of(name, { c -> c.notNull().notBlank().isFloat })
                .build(String::toFloat)

        fun float(name: Translatable) = float(name.translationKey())

        fun double(name: String): StringValidator<Double> =
            StringValidatorBuilder.of(name, { c -> c.notNull().notBlank().isDouble })
                .build(String::toDouble)

        fun key(name: String): StringValidator<Key> =
            StringValidatorBuilder.of(name) { c ->
                    c.notNull()
                        .notBlank()
                        .predicate(
                            Key::parseable,
                            "string.key",
                            "\"{0}\" must be \"namespace:value\" format.",
                        )
                }
                .build(Key::key)

        fun namespacedKey(name: String): StringValidator<Identifier> =
            StringValidatorBuilder.of(name) { c ->
                    c.notNull()
                        .notBlank()
                        .predicate(
                            Key::parseable,
                            "string.key",
                            "\"{0}\" must be \"namespace:value\" format.",
                        )
                }
                .build(Identifier::tryParse)

        fun predicate(
            name: String,
            messageKey: String,
            defaultMessageFormat: String,
            fn: (String) -> Boolean,
        ): StringValidator<String> =
            StringValidatorBuilder.of(name) { c ->
                    c.predicate(fn, messageKey, defaultMessageFormat)
                }
                .build()
    }
}

private fun convertTranslationKey(violation: ConstraintViolation): Component {
    val baseKey = "admintool.validation.${violation.messageKey()}"
    return Component.translatable(
        baseKey,
        violation.args().map {
            if (it is String) {
                Component.translatable(it, it)
            } else {
                Component.literal(it.toString())
            }
        },
    )
}

fun ConstraintViolations.toComponent(): List<Component> {
    return this.map { convertTranslationKey(it).copy().withColor(TextColor.RED) }
}
