package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.util.Location
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.NameAndId
import net.minecraft.world.entity.player.Abilities
import java.util.*

class OfflinePlayer(val serverPlayer: ServerPlayer) {

    fun isOnline(): Boolean =
        serverPlayer.level().server.playerList.playersByUUID.contains(serverPlayer.uuid)

    fun getName(): String = serverPlayer.scoreboardName

    fun getUUID(): UUID = serverPlayer.uuid

    fun setLocation(location: Location) {
        location.moveHere(this.serverPlayer)
    }

    fun nameAndId(): NameAndId {
        return NameAndId(this.getUUID(), this.getName())
    }

    fun getAbilities(): Abilities {
        return serverPlayer.abilities
    }
}
