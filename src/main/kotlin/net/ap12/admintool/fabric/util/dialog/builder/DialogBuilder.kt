@file:Suppress("UnstableApiUsage")

package net.ap12.admintool.fabric.util.dialog.builder

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.DialogResponse
import net.ap12.admintool.fabric.util.dialog.builder.base.DialogBaseBuilder
import net.ap12.admintool.fabric.util.dialog.builder.type.DialogTypeBuilder
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog

typealias DialogCallback = (DialogResponse) -> Unit

@DialogDSL
class DialogBuilder(private val holder: AdminToolUIHolder) {
    private var base: CommonDialogData? = null
    private var type: DialogTypeBuilder.() -> Unit = {}

    fun base(builder: DialogBaseBuilder.() -> Unit) {
        this.base = DialogBaseBuilder(holder).apply(builder).build()
    }

    fun type(builder: DialogTypeBuilder.() -> Unit) {
        this.type = builder
    }

    fun build(): Dialog {
        return DialogTypeBuilder(holder, requireNotNull(base)).apply(type).build()
    }
}
