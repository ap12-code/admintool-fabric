package net.ap12.admintool.fabric.ui.module.impl

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.module.UIModule
import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items

class QuickGateModule(val world: ServerLevel) : UIModule {
    override val id: Key = AdminToolMod.key("quick_gate")

    private fun getMaterial(world: ServerLevel) =
        when (world.dimension()) {
            ServerLevel.OVERWORLD -> Items.STONE
            ServerLevel.NETHER -> Items.NETHERRACK
            ServerLevel.END -> Items.END_STONE
            else -> Items.GLASS
        }

    override fun create(holder: AdminToolUIHolder): ItemBuilder = item(getMaterial(world)) {}

    private fun executeTeleport(player: ServerPlayer, world: ServerLevel) =
        when (world.dimension()) {
            ServerLevel.OVERWORLD -> Items.STONE
            ServerLevel.NETHER -> Items.NETHERRACK
            ServerLevel.END -> Items.END_STONE
            else -> Items.GLASS
        }

    private fun getRespawnLocation(player: ServerPlayer, world: ServerLevel): Location {
        val playerRespawnLocation = player.respawnConfig?.respawnData
        if (playerRespawnLocation?.dimension() == world.dimension()) {
            return Location.of(playerRespawnLocation)
        }

        return Location.of(world.respawnData)
    }

    private fun calculateNetherLocation(
        player: ServerPlayer,
        fromWorld: ServerLevel,
        toWorld: ServerLevel,
    ) {
        require(fromWorld.dimension() != toWorld.dimension())
    }

    private fun executeTeleportNormal(player: ServerLevel, world: ServerLevel) {}
}
