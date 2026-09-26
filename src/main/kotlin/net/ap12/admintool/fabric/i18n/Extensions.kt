package net.ap12.admintool.fabric.i18n

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.node.TextNode
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.server.level.ServerPlayer
import xyz.nucleoid.server.translations.api.Localization
import xyz.nucleoid.server.translations.api.language.ServerLanguage
import java.util.*

fun t(translationKey: String, vararg args: Any): MutableComponent {
    return Component.translatable(translationKey, *args)
}

fun Component.translate(locale: Locale): Component {
    return translate(ParserContext.of(), locale)
}

private fun resolveTranslate(player: ServerPlayer, original: Component): Component {
    val language = ServerLanguage.getLanguage(player.clientInformation().language())
    val context = PlaceholderContext.of(player)
    val resolved = Component.empty()
    val contents = original.contents
    if (contents is TranslatableContents) {
        var raw = Localization.raw(contents.key, language).orEmpty()
        for ((i, element) in contents.args.withIndex()) {
            val translated =
                when (element) {
                    is String -> element
                    is Component -> {
                        val innerContents = element.contents
                        if (innerContents is TranslatableContents)
                            Localization.raw(innerContents.key, language)
                        else element.string
                    }
                    else -> element.toString()
                }
            raw = raw.replace("<arg:${i}>", translated.orEmpty())
        }
        resolved.append(
            ComponentUtils.mergeStyles(
                I18n.NODE_PARSER.parseNode(raw).toComponent(context),
                original.style,
            )
        )
    } else if (contents is PlainTextContents.LiteralContents) {
        resolved.append(
            ComponentUtils.mergeStyles(
                I18n.NODE_PARSER.parseNode(TextNode.of(contents.text)).toComponent(context),
                original.style,
            )
        )
    }
    for (children in original.siblings) {
        resolved.append(resolveTranslate(player, children))
    }
    return resolved.withStyle(original.style)
}

fun Component.translate(player: ServerPlayer): Component {
    return resolveTranslate(player, this)
}

fun Component.translate(context: ParserContext, locale: Locale): Component {
    return I18n.NODE_PARSER.parseNode(TextNode.convert(this)).toComponent(context)
}
