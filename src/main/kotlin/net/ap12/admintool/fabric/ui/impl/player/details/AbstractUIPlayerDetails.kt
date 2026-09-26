package net.ap12.admintool.fabric.ui.impl.player.details

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.compat.impl.ViaHook
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.mixin.PlayerListAccessor
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import java.util.*

abstract class AbstractUIPlayerDetails(private val key: String, private val target: UUID) : UI {
    override val id: Key = AdminToolMod.key("player.details.$key")

    protected fun getPlayer(holder: AdminToolUIHolder): ServerPlayer {
        return requireNotNull(holder.plugin.server.playerList.getPlayer(target))
    }

    abstract fun createChild(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle>

    private fun createBase(holder: AdminToolUIHolder) =
        inventory(holder) {
            val player = getPlayer(holder)
            val headItem = holder.plugin.heads.createHead(player.gameProfile)
            0 to
                item(headItem.copy()) {
                    name(player.name)

                    if (holder.plugin.server.playerList.playersByUUID.containsKey(player.uuid)) {
                        val via = holder.plugin.hooks.get<ViaHook>("ViaVersion")?.getInstance()
                        val clientVersion =
                            via?.getPlayerProtocolVersion(player.uuid)?.name?.plus(" &7(Via)")
                                ?: holder.plugin.server.serverVersion
                        val clientBrandName = "Unknown"
                        val locale = player.clientInformation().language
                        val ping = "${player.connection.latency()}ms"

                        lore {
                            +""
                            +field("admintool.ui.player.details.locale", locale)
                            +field("admintool.ui.player.details.version", clientVersion)
                            +field("admintool.ui.player.details.brand", clientBrandName)
                            +field("admintool.ui.player.details.ping", ping)
                        }
                    } else {
                        lore {
                            +""
                            +t("admintool.ui.player.details.offline").symbolPrefixed(TextColor.RED)
                        }
                    }
                }

            9..17 to Items.STAINED_GLASS_PANE.black
        }

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        val player = holder.plugin.offlinePlayerManager.get(target)
        if (player != null) {
            (holder.plugin.server.playerList as PlayerListAccessor)
                .playerIo()
                .save(player.serverPlayer)
            next()
        }
    }

    override fun onClose(holder: AdminToolUIHolder) {
        val player = holder.plugin.offlinePlayerManager.get(target)
        if (player != null) {
            (holder.plugin.server.playerList as PlayerListAccessor)
                .playerIo()
                .save(player.serverPlayer)
        }
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        inventory(holder) {
            createBase(holder).merge()
            createChild(holder).merge()
        }
}
