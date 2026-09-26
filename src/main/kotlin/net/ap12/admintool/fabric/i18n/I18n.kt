package net.ap12.admintool.fabric.i18n

import am.ik.yavi.core.ViolationMessage
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.placeholders.api.parsers.NodeParser
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.util.locale
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import xyz.nucleoid.server.translations.api.Localization
import xyz.nucleoid.server.translations.api.language.ServerLanguage
import xyz.nucleoid.server.translations.impl.ServerTranslations
import xyz.nucleoid.server.translations.impl.language.TranslationMap

class I18n(private val plugin: AdminToolMod) {
    private var initialized: Boolean = false

    fun init() {
        load()
        plugin.slogger.info("Successfully initialized translations.")
    }

    fun reload() {
        load()
    }

    fun parse(translationKey: String, player: ServerPlayer): Component {
        val localized =
            Localization.raw(translationKey, ServerLanguage.getLanguage(player.locale().toString()))

        return NODE_PARSER.parseNode(TextNode.of(localized))
            .toComponent(PlaceholderContext.of(player))
    }

    private fun load() {
        initialized = false

        for (lang in languages) {
            val store = TranslationMap()
            ViolationMessage.Default.entries.forEach {
                store.put(
                    "admintool.validation.${it.messageKey()}",
                    it.defaultMessageFormat().replace(Regex("\\{([0-9]+)}"), "%s\\$$1"),
                )
            }
            ServerTranslations.INSTANCE.addTranslations(lang, { store })
        }

        initialized = true
    }

    companion object {
        private val languages = arrayOf("en_us", "ja_jp")
        val NODE_PARSER: NodeParser =
            NodeParser.builder().simplifiedTextFormat().serverPlaceholders().build()
    }
}
