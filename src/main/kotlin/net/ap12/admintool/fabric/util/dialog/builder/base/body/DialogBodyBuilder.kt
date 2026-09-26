package net.ap12.admintool.fabric.util.dialog.builder.base.body

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.body.PlainMessage

@Suppress("UnstableApiUsage", "unused")
@DialogDSL
class DialogBodyBuilder(private val holder: AdminToolUIHolder) {
    private val elements = mutableListOf<DialogBody>()

    fun text(builder: DialogPlainMessageBodyBuilder.() -> Unit) {
        elements.add(DialogPlainMessageBodyBuilder().apply(builder).build())
    }

    fun text(component: Component, width: Int = 200) {
        elements.add(PlainMessage(component, width))
    }

    fun item(builder: DialogItemBodyBuilder.() -> Unit) {
        elements.add(DialogItemBodyBuilder(holder).apply(builder).build())
    }

    fun build(): List<DialogBody> {
        return elements.toList()
    }
}
