package net.ap12.admintool.fabric.util

import net.minecraft.server.level.ServerPlayer

interface IStore<T> {
    fun read(player: ServerPlayer): T

    fun write(player: ServerPlayer, data: T, callback: Callback<T> = {})
}
