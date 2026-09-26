package net.ap12.admintool.fabric.util.dialog.builder.base.body

import net.minecraft.server.dialog.body.DialogBody

@Suppress("unused")
abstract class AbstractDialogBodyBuilder(defaultWidth: Int) {
    protected var width: Int = defaultWidth

    fun width(width: Int) {
        this.width = width
    }

    abstract fun build(): DialogBody
}
