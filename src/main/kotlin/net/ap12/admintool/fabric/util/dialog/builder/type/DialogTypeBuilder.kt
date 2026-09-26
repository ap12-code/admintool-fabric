package net.ap12.admintool.fabric.util.dialog.builder.type

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog

@Suppress("unused")
@DialogDSL
class DialogTypeBuilder(
    private val holder: AdminToolUIHolder,
    private val common: CommonDialogData,
) {
    private var dialogType: Dialog? = null

    fun notice(builder: DialogNoticeTypeBuilder.() -> Unit) {
        dialogType = DialogNoticeTypeBuilder(holder).apply(builder).build(common)
    }

    fun confirmation(builder: DialogConfirmationTypeBuilder.() -> Unit) {
        dialogType = DialogConfirmationTypeBuilder(holder).apply(builder).build(common)
    }

    fun multiAction(builder: DialogConfirmationTypeBuilder.() -> Unit) {
        dialogType = DialogConfirmationTypeBuilder(holder).apply(builder).build(common)
    }

    fun build(): Dialog {
        return dialogType!!
    }
}
