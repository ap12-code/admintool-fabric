package net.ap12.admintool.fabric.util.dialog.builder.base.inputs

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.minecraft.server.dialog.Input
import net.minecraft.server.dialog.input.NumberRangeInput
import org.jetbrains.annotations.Range
import java.util.*

@DialogDSL
@Suppress("unused")
class DialogNumberRangeInputBuilder(
    private val key: String,
    private val holder: AdminToolUIHolder,
) : AbstractDialogInputBuilder<Float>(holder) {
    private var max = 0f
    private var min = 0f

    fun <T> range(range: ClosedRange<T>) where T : Comparable<T>, T : Number {
        min = range.start.toFloat()
        max = range.endInclusive.toFloat()
    }

    fun min(value: Float) {
        this.min = value
    }

    fun max(value: Float) {
        this.max = value
    }

    private var width: @Range(from = 1, to = 1024) Int = 200

    fun width(width: @Range(from = 1, to = 1024) Int) {
        this.width = width
    }

    private var labelFormat: String = "options.generic_value"

    fun labelFormat(value: String) {
        this.labelFormat = value
    }

    private var step: Float? = null

    fun step(value: Float) {
        this.step = value
    }

    override fun build(): Input {
        holder.elementNameStore[key] = label
        return Input(
            key,
            NumberRangeInput(
                width,
                label,
                labelFormat,
                NumberRangeInput.RangeInfo(
                    min,
                    max,
                    Optional.ofNullable(initial),
                    Optional.ofNullable(step),
                ),
            ),
        )
    }
}
