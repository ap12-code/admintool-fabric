package net.ap12.admintool.fabric.vanish

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.config.DataLoader
import net.ap12.admintool.fabric.config.ext.UUIDSerializer
import net.ap12.admintool.fabric.i18n.t
import net.kyori.adventure.text.Component
import net.minecraft.server.level.ServerPlayer
import java.util.*

class AdminToolVanisher(private val plugin: AdminToolMod) : Vanisher {
    private val vanished = mutableSetOf<UUID>()
    private val dataLoader = DataLoader<Data>(Data.serializer(), plugin, "vanish.yml")

    fun init() {
        vanished.clear()
        vanished.addAll(dataLoader.load().players)
        plugin.taskScheduler.asyncRepeat(500, 0, { sendActionBar() })
    }

    fun sendActionBar() {
        plugin.server.playerList.players
            .filter { vanished.contains(it.uuid) }
            .forEach { player -> player.sendSystemMessage(t("admintool.vanish.vanished"), true) }
    }

    fun save() {
        dataLoader.save(Data(vanished.toList()))
    }

    override fun vanish(player: ServerPlayer) {
        vanished.add(player.uuid)
        for (target in plugin.server.playerList.players) {
            if (target == player) continue
            // target.hidePlayer(plugin, player)
        }
        sendActionBar()
    }

    override fun appear(player: ServerPlayer) {
        vanished.remove(player.uuid)
        for (target in plugin.server.playerList.players) {
            if (target == player) continue
            // target.showPlayer(plugin, player)
        }
        player.sendActionBar(Component.empty())
    }

    override fun isVanished(player: ServerPlayer): Boolean {
        return vanished.contains(player.uuid)
    }

    @Serializable data class Data(var players: List<@Serializable(UUIDSerializer::class) UUID>)
}
