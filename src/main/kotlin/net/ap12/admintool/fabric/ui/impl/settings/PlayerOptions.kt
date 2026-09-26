package net.ap12.admintool.fabric.ui.impl.settings

import kotlinx.serialization.Serializable

@Serializable
data class PlayerOptions(
    var teleportSpectatorMode: TeleportSpectatorMode = TeleportSpectatorMode.ALWAYS
)
