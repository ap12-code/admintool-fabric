package net.ap12.admintool.fabric.util.dialog.builder.type

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.dialog.MultiActionDialog
import java.util.*

@DialogDSL
@Suppress("unused")
class DialogMultiActionTypeBuilder(private val holder: AdminToolUIHolder) {
    private var actions = mutableListOf<ActionButton>()
    private var exitAction: ActionButton? = null
    private var columns: Int = 2

    fun action(builder: ActionButtonBuilder.() -> Unit) {
        actions.add(ActionButtonBuilder(holder).apply(builder).build())
    }

    fun exit(builder: ActionButtonBuilder.() -> Unit) {
        this.exitAction = ActionButtonBuilder(holder).apply(builder).build()
    }

    fun columns(value: Int) {
        this.columns = value
    }

    fun build(common: CommonDialogData): Dialog {
        return MultiActionDialog(common, actions, Optional.ofNullable(exitAction), columns)
    }
}
