package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.server.level.ServerPlayer

class IOEventListener(private val plugin: AdminToolMod) {
    init {
        ServerPlayerEvents.LEAVE.register(::onPlayerQuit)
    }

    private fun onPlayerQuit(player: ServerPlayer) {
        plugin.dataStore.queue.flush(player.uuid)
    }
}
