package net.ap12.admintool.fabric.util.dialog.builder.type

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.dialog.NoticeDialog

@Suppress("unused")
@DialogDSL
class DialogNoticeTypeBuilder(private val holder: AdminToolUIHolder) {
    private var action: ActionButton? = null

    fun action(builder: ActionButtonBuilder.() -> Unit) {
        action = ActionButtonBuilder(holder).apply(builder).build()
    }

    fun build(common: CommonDialogData): Dialog {
        return NoticeDialog(common, requireNotNull(action))
    }
}
