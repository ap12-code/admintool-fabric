package net.ap12.admintool.fabric.ui.tab

import net.ap12.admintool.fabric.ui.UI

interface Tab : UI {
    val permission: String
        get() = "admintool.tabs.${id.value()}"
}
