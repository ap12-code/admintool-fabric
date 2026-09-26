package net.ap12.admintool.fabric.util.dialog.builder.type

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.ConfirmationDialog
import net.minecraft.server.dialog.Dialog

@DialogDSL
@Suppress("unused")
class DialogConfirmationTypeBuilder(private val holder: AdminToolUIHolder) {
    private var yes: ActionButton? = null

    fun yes(builder: ActionButtonBuilder.() -> Unit) {
        yes = ActionButtonBuilder(holder).apply(builder).build()
    }

    private var no: ActionButton? = null

    fun no(builder: ActionButtonBuilder.() -> Unit) {
        no = ActionButtonBuilder(holder).apply(builder).build()
    }

    fun build(data: CommonDialogData): Dialog {
        return ConfirmationDialog(data, requireNotNull(yes), requireNotNull(no))
    }
}
