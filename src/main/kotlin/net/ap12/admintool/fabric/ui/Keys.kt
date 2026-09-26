package net.ap12.admintool.fabric.ui

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.util.components.orEmpty
import net.minecraft.network.chat.Component

object Keys {
    val CLICK = t("admintool.ui.keybind.click")
    val MOUSE_LEFT = t("admintool.ui.keybind.click.left")
    val MOUSE_RIGHT = t("admintool.ui.keybind.click.right")
    val MOUSE_MIDDLE = t("admintool.ui.keybind.click.middle")
    val KEY_SHIFT = t("admintool.ui.keybind.shift")

    private val all = listOf(CLICK, MOUSE_LEFT, MOUSE_RIGHT, MOUSE_MIDDLE, KEY_SHIFT)

    fun concat(vararg keys: Component): Array<Component> {
        return arrayOf(
            keys
                .map { it.copy() }
                .takeIf { it.isNotEmpty() }
                ?.reduce { acc, component ->
                    acc.append(component).append(Component.literal(" + "))
                }
                .orEmpty(),
            *keys.filterNot { all.contains(it) }.toTypedArray(),
        )
    }
}
