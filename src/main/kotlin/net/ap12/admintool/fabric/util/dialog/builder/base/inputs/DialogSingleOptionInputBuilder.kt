package net.ap12.admintool.fabric.util.dialog.builder.base.inputs

import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.ap12.admintool.fabric.util.locale
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.input.SingleOptionInput
import java.util.*

@DialogDSL
@Suppress("unused")
class DialogSingleOptionInputBuilder(
    private val key: String,
    private val holder: AdminToolUIHolder,
) : AbstractDialogInputBuilder<String>(holder) {

    class SingleOptionEntryBuilder(
        private val holder: AdminToolUIHolder,
        private val id: String,
        private val initial: Boolean,
    ) {
        private var display: Component? = null

        fun display(display: Component) {
            this.display = display.translate(holder.player.locale())
        }

        fun display(display: String) = display(display.toComponent())

        fun build(): SingleOptionInput.Entry {
            return SingleOptionInput.Entry(id, Optional.ofNullable(display), initial)
        }
    }

    private fun isInitial(key: String) = if (initial == null) options.isEmpty() else initial == key

    private val options = mutableListOf<SingleOptionInput.Entry>()

    fun option(key: String, builder: SingleOptionEntryBuilder.() -> Unit) {
        options.add(SingleOptionEntryBuilder(holder, key, isInitial(key)).apply(builder).build())
    }

    fun option(key: String, display: Component) {
        options.add(
            SingleOptionInput.Entry(
                key,
                Optional.ofNullable(display.translate(holder.player.locale())),
                isInitial(key),
            )
        )
    }

    fun option(key: String, display: String) {
        options.add(
            SingleOptionInput.Entry(
                key,
                Optional.ofNullable(display.toComponent().translate(holder.player.locale())),
                isInitial(key),
            )
        )
    }

    override fun build(): Input {
        holder.elementNameStore[key] = label
        return Input(key, SingleOptionInput(200, options, label, true))
    }
}
