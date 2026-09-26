package net.ap12.admintool.fabric.util

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.permissions.PermissionLevel
import net.minecraft.world.entity.player.Player

fun Player.hasPermission(node: String, op: Boolean = true): Boolean {
    return Permissions.check(
        this,
        node,
        if (op) PermissionLevel.GAMEMASTERS else PermissionLevel.ALL,
    )
}
