package net.ap12.admintool.fabric.util.dialog.builder.base.inputs

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.input.BooleanInput

@DialogDSL
@Suppress("unused")
class DialogBoolInputBuilder(private val key: String, private val holder: AdminToolUIHolder) :
    AbstractDialogInputBuilder<Boolean>(holder) {
    private var onTrue: String? = null
    private var onFalse: String? = null

    fun onTrue(value: String) {
        this.onTrue = value
    }

    fun onFalse(value: String) {
        this.onFalse = value
    }

    override fun build(): Input {
        holder.elementNameStore[key] = label
        return Input(key, BooleanInput(label, initial == true, onTrue ?: "", onFalse ?: ""))
    }
}
