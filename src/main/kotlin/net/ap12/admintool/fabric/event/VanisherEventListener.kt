package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.minecraft.server.level.ServerPlayer

class VanisherEventListener(private val plugin: AdminToolMod) {
    fun onPlayerJoin(player: ServerPlayer) {
        if (plugin.vanisher.isVanished(player)) {
            plugin.vanisher.vanish(player)
        }
    }

    fun onPlayerQuit(player: ServerPlayer) {
        if (plugin.vanisher.isVanished(player)) {}
    }
}
