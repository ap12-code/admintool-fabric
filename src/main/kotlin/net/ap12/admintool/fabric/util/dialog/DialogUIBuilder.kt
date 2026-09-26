package net.ap12.admintool.fabric.util.dialog

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogBuilder
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.UIType
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.Dialog

class DialogUIBuilder(private val dialog: DialogBuilder) : UIBuilder<Dialog> {

    override fun build(): Dialog = dialog.build()

    override fun getType() = UIType.DIALOG

    override fun getTitle(): Component = Component.empty()
}

fun dialog(holder: AdminToolUIHolder, builder: DialogBuilder.() -> Unit): UIBuilder<Dialog> =
    DialogUIBuilder(DialogBuilder(holder).apply(builder))
