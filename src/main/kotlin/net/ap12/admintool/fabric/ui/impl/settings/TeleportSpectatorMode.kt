package net.ap12.admintool.fabric.ui.impl.settings

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import net.ap12.admintool.fabric.config.ext.NbtEnumSerializer
import net.ap12.admintool.fabric.openinv.OfflinePlayer
import net.ap12.admintool.fabric.util.Location
import net.kyori.adventure.translation.Translatable
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameType

@Serializable(TeleportSpectatorMode.Serializer::class)
enum class TeleportSpectatorMode(val code: Byte) : Translatable {
    ALWAYS(1),
    ONLINE_ONLY(2),
    DISABLED(3);

    object Serializer :
        NbtEnumSerializer<TeleportSpectatorMode, Byte>(
            "TeleportSpectatorMode",
            PrimitiveKind.BYTE,
            { it.code },
            ::fromCode,
        )

    override fun translationKey(): String {
        return "admintool.ui.settings.teleport_spectator.${this.name.lowercase()}"
    }

    fun next(): TeleportSpectatorMode =
        when (this) {
            DISABLED -> ALWAYS
            ALWAYS -> ONLINE_ONLY
            ONLINE_ONLY -> DISABLED
        }

    companion object {
        fun fromCode(code: Byte): TeleportSpectatorMode =
            entries.find { it.code == code } ?: DISABLED
    }

    fun teleport(player: ServerPlayer, targetOnline: Boolean, targetLocation: Location) {
        if (this == ALWAYS || (targetOnline && this == ONLINE_ONLY)) {
            player.setGameMode(GameType.SPECTATOR)
        }
        targetLocation.moveHere(player)
    }

    fun teleport(player: ServerPlayer, target: OfflinePlayer) =
        teleport(player, target.isOnline(), Location.of(target.serverPlayer).clone())
}
