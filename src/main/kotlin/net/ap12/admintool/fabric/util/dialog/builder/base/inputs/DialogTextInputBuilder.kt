package net.ap12.admintool.fabric.util.dialog.builder.base.inputs

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.input.TextInput
import org.jetbrains.annotations.Range
import java.util.*
import kotlin.math.max

@DialogDSL
@Suppress("unused")
class DialogTextInputBuilder(private val key: String, private val holder: AdminToolUIHolder) :
    AbstractDialogInputBuilder<String>(holder) {

    private var labelVisible: Boolean = true

    fun labelVisible(labelVisible: Boolean) {
        this.labelVisible = labelVisible
    }

    private var width: @Range(from = 1, to = 1024) Int? = null

    fun width(width: @Range(from = 1, to = 1024) Int) {
        this.width = width
    }

    private var maxLength: Int? = null

    fun maxLength(maxLength: Int) {
        this.maxLength = max(maxLength, 0)
    }

    class MultilineOptionsBuilder {
        private var maxLines: Int? = null
        private var height: @Range(from = 1, to = 512) Int? = null

        fun maxLines(maxLines: Int) {
            this.maxLines = maxLines
        }

        fun height(height: @Range(from = 1, to = 512) Int?) {
            this.height = height
        }

        fun build() =
            TextInput.MultilineOptions(Optional.ofNullable(maxLines), Optional.ofNullable(height))
    }

    private var multilineOptions: TextInput.MultilineOptions? = null

    fun multiline(builder: MultilineOptionsBuilder.() -> Unit) {
        this.multilineOptions = MultilineOptionsBuilder().apply(builder).build()
    }

    override fun build(): Input {
        holder.elementNameStore[key] = label

        return Input(
            key,
            TextInput(
                width ?: 200,
                label,
                labelVisible,
                initial ?: "",
                maxLength ?: 32,
                Optional.ofNullable(multilineOptions),
            ),
        )
    }
}
