package net.ap12.admintool.fabric.ui.impl.player

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.minecraft.server.players.NameAndId

@Serializable
data class PlayerFilter(
    var keyword: String? = "",
    var offline: Boolean = false,
    var online: Boolean = false,
    var op: Boolean = false,
) {

    fun apply(players: List<NameAndId>, holder: AdminToolUIHolder): List<NameAndId> {
        val playerList = holder.plugin.server.playerList
        return players.filter {
            val moderator = !op || !playerList.isOp(it)
            val isOnline = playerList.playersByUUID.containsKey(it.id)

            val onlineFilter = online || offline
            val onlineMatch =
                when {
                    !onlineFilter -> true
                    online && !isOnline -> true
                    offline && isOnline -> true
                    else -> false
                }

            val keywordMatch = keyword.let { s -> s.isNullOrBlank() || it.name.startsWith(s) }

            moderator && onlineMatch && keywordMatch
        }
    }
}
