package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.util.Location
import net.minecraft.server.level.ServerPlayer
import java.util.*

class TeleportRecorder(private val plugin: AdminToolMod) {
    private val recodedLocations = mutableMapOf<UUID, Location>()

    fun onTeleport(player: ServerPlayer, srcLocation: Location) {
        recodedLocations[player.uuid] = srcLocation
    }

    fun back(player: ServerPlayer): Boolean {
        val location = recodedLocations[player.uuid]
        if (location != null) {
            player.setPos(location.position)
            player.xRot = location.pitch
            player.yRot = location.yaw
            player.setYBodyRot(location.yaw)

            return true
        }
        return false
    }
}
