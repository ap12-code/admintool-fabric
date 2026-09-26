package net.ap12.admintool.fabric.util

import net.minecraft.server.level.ServerPlayer
import java.util.*

fun ServerPlayer.locale(): Locale {
    return Locale.of(this.clientInformation().language)
}
