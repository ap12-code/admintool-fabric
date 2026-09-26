package net.ap12.admintool.fabric.ui

import net.ap12.admintool.fabric.ui.tab.Tab
import net.minecraft.server.level.ServerPlayer

interface IUIManager {
    val wrapperCreator: (baseUI: UI, showTabs: Boolean) -> UI
    val tabs: List<Tab>

    fun open(player: ServerPlayer)
}
