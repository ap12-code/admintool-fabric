package net.ap12.admintool.fabric.util.dialog.builder.base.body

import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.body.PlainMessage

@DialogDSL
@Suppress("unused")
class DialogPlainMessageBodyBuilder : AbstractDialogBodyBuilder(200) {
    private var contents: Component = Component.empty()

    fun contents(component: Component) {
        this.contents = component
    }

    override fun build(): PlainMessage {
        return PlainMessage(contents, width)
    }
}
