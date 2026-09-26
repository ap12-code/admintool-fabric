package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.impl.player.details.abilities.UIPlayerAbilities
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Abilities
import java.util.*

class PlayerAbilitiesFixer(private val plugin: AdminToolMod) {
    private val store = mutableMapOf<UUID, Entry>()
    private val playerManager
        get() = plugin.server.playerList

    fun onPlayerLogin(player: UUID) {
        val serverPlayer = playerManager.getPlayer(player) ?: return
        //        if (serverPlayer.abilities) {
        //            plugin.slogger.info(
        //                "Player '${serverPlayer.plainTextName}' abilities marked dirty. applying
        // changed abilities..."
        //            )
        //            store[player] = Entry(System.currentTimeMillis(), serverPlayer.abilities)
        //        }
    }

    fun onPlayerJoin(player: ServerPlayer) {
        val entry = store.remove(player.uuid)
        val abilities = player.abilities
        if (entry != null) {
            println("join ${player.name} -> mayFly: ${abilities.mayfly} to ${entry.data.mayfly}")
            abilities.apply(entry.data.pack())
        }
    }

    fun onPlayerGamemodeChange(player: ServerPlayer) {
        plugin.ui.updateIf<UIPlayerAbilities> { _, ui -> ui.player.getUUID() == player.uuid }
    }

    fun unload() {
        store.clear()
    }

    data class Entry(val addedAt: Long, val data: Abilities)
}
