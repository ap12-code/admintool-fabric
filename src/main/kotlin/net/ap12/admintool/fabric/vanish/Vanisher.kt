package net.ap12.admintool.fabric.vanish

import net.minecraft.server.level.ServerPlayer

interface Vanisher {
    fun vanish(player: ServerPlayer)

    fun appear(player: ServerPlayer)

    fun isVanished(player: ServerPlayer): Boolean
}
