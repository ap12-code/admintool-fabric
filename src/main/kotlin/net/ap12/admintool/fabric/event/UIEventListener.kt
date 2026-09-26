package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUI
import net.ap12.admintool.fabric.ui.impl.player.details.UIPlayerDetails
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer

class UIEventListener(private val plugin: AdminToolMod) : ServerTickEvents.StartTick {
    override fun onStartTick(server: MinecraftServer) {
        if (server.tickCount % 20 != 0) return
        plugin.ui.getInstances<AdminToolUI>().forEach { (holder, ui) ->
            (ui.baseUI as? UIPlayerDetails)?.update(holder)
        }
    }
}
