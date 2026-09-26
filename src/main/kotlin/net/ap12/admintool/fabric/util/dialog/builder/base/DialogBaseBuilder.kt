package net.ap12.admintool.fabric.util.dialog.builder.base

import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.ap12.admintool.fabric.util.dialog.builder.base.body.DialogBodyBuilder
import net.ap12.admintool.fabric.util.dialog.builder.base.inputs.DialogBoolInputBuilder
import net.ap12.admintool.fabric.util.dialog.builder.base.inputs.DialogNumberRangeInputBuilder
import net.ap12.admintool.fabric.util.dialog.builder.base.inputs.DialogSingleOptionInputBuilder
import net.ap12.admintool.fabric.util.dialog.builder.base.inputs.DialogTextInputBuilder
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.DialogAction
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.body.DialogBody
import java.util.*

@Suppress("unused")
@DialogDSL
class DialogBaseBuilder(private val holder: AdminToolUIHolder) {
    private var title: Component = Component.empty()

    fun title(title: Component) {
        this.title = title.translate(holder.player)
    }

    fun title(title: String, vararg args: String) {
        this.title = Component.translatableEscape(title, *args).translate(holder.player)
    }

    private val body = mutableListOf<DialogBody>()

    fun body(builder: DialogBodyBuilder.() -> Unit) {
        body.addAll(DialogBodyBuilder(holder).apply(builder).build())
    }

    private val inputs = mutableListOf<Input>()

    fun textInput(key: String, builder: DialogTextInputBuilder.() -> Unit): String {
        inputs.add(DialogTextInputBuilder(key, holder).apply(builder).build())
        return key
    }

    fun bool(key: String, builder: DialogBoolInputBuilder.() -> Unit): String {
        inputs.add(DialogBoolInputBuilder(key, holder).apply(builder).build())
        return key
    }

    fun singleOption(key: String, builder: DialogSingleOptionInputBuilder.() -> Unit): String {
        inputs.add(DialogSingleOptionInputBuilder(key, holder).apply(builder).build())
        return key
    }

    fun numberRange(key: String, builder: DialogNumberRangeInputBuilder.() -> Unit): String {
        inputs.add(DialogNumberRangeInputBuilder(key, holder).apply(builder).build())
        return key
    }

    fun build(): CommonDialogData =
        CommonDialogData(title, Optional.empty(), false, false, DialogAction.CLOSE, body, inputs)
}
