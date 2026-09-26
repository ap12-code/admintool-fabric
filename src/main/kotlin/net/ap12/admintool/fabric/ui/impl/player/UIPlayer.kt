package net.ap12.admintool.fabric.ui.impl.player

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUI
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.impl.player.details.UIPlayerDetails
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.Callback
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.TooltipDisplay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.math.min

class UIPlayer : Tab {
    override val id = AdminToolMod.key("player")

    companion object {
        const val PLAYERS_PER_PAGE = 36
    }

    fun fetchPlayerHeads(holder: AdminToolUIHolder, page: Int, callback: Callback<ItemStack>) {
        val data = holder.playerStore.playerUIData
        holder.plugin.taskScheduler.async {
            val online = holder.plugin.server.playerList.players.map { it.nameAndId() }

            val allPlayers = online.toList()

            val exactMatchPlayer = allPlayers.find { it.name == data.filter.keyword }
            if (exactMatchPlayer != null) {
                val offlinePlayer =
                    holder.plugin.offlinePlayerManager.get(exactMatchPlayer) ?: return@async
                holder.plugin.taskScheduler.async {
                    holder.editStore { playerUIData.filter.keyword = "" }
                    holder.go<Nothing>(AdminToolUI(UIPlayerDetails(offlinePlayer)), true)
                }
                return@async
            }

            val startIndex = min((page - 1) * PLAYERS_PER_PAGE, allPlayers.size)
            val endIndex = min(startIndex + PLAYERS_PER_PAGE, allPlayers.size)

            val players = data.filter.apply(allPlayers.subList(startIndex, endIndex), holder)

            players.forEach { profile ->
                val player = holder.plugin.offlinePlayerManager.get(profile) ?: return@forEach
                val builtItem =
                    item(holder.plugin.heads.createHead(profile)) {
                        name(Component.literal(profile.name))

                        stack.also {
                            it.set(
                                DataComponents.TOOLTIP_DISPLAY,
                                TooltipDisplay(false, linkedSetOf(DataComponents.PROFILE)),
                            )
                        }

                        lore {
                            if (player.isOnline()) {
                                +field(t("admintool.ui.player.online"), TextColor.GREEN)
                            } else {
                                +field(t("admintool.ui.player.offline"), TextColor.RED)
                            }
                            +""
                            +action("admintool.ui.player.open", Keys.MOUSE_LEFT)
                            if (player.isOnline()) {
                                +action("admintool.ui.player.teleport", Keys.MOUSE_RIGHT)
                            }
                        }

                        onClick("admintool.ui.player.${profile.name}") { context ->
                            if (context.isRightClick) {
                                if (player.isOnline()) {
                                    val teleportMode =
                                        holder.playerStore.options.teleportSpectatorMode
                                    teleportMode.teleport(context.player, player)
                                    context.close()
                                }
                            } else {
                                context.go<Nothing>(UIPlayerDetails(player))
                            }
                        }
                    }
                callback(builtItem.toItemStack(holder))
            }
        }
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> {
        val page = holder.pageStore.getOrDefault(this.key(), 1)
        val allPlayers = buildList {
            addAll(holder.plugin.server.playerList.players.map { it.nameAndId() })
            addAll(holder.plugin.offlinePlayerManager.offlinePlayers)
        }
        val maxPage = ceil(allPlayers.size / PLAYERS_PER_PAGE.toDouble()).toInt()

        val i = AtomicInteger(0)
        fetchPlayerHeads(holder, page) { stack ->
            if (holder.key == key())
                holder.plugin.taskScheduler.sync {
                    holder.setItem(i.getAndIncrement(), holder.incrementStateId(), stack)
                }
        }

        return inventory(holder) {
            36..44 to Items.STAINED_GLASS_PANE.black

            val filter = holder.playerStore.playerUIData.filter
            36 to
                item(Items.HOPPER) {
                    name("admintool.ui.player.filter")

                    lore {
                        +field(
                            "admintool.ui.player.filter.online",
                            filter.online.toComponent(),
                            TextColor.GRAY,
                        )
                        +field(
                            "admintool.ui.player.filter.offline",
                            filter.offline.toComponent(),
                            TextColor.GRAY,
                        )
                        +field(
                            "admintool.ui.player.filter.op",
                            filter.op.toComponent(),
                            TextColor.GRAY,
                        )
                    }

                    onClick("admintool.ui.player.filter") { context ->
                        context.go<Nothing>(UIPlayerFilter(), showTabs = false)
                    }
                }

            val keyword = filter.keyword
            37 to
                item(Items.COMPASS) {
                    name("admintool.ui.player.search")

                    lore {
                        if (keyword.isNullOrEmpty()) {
                            +field("admintool.value.empty").withColor(TextColor.GRAY)
                        } else {
                            +field(Component.literal(keyword))
                        }
                        +""
                        +action("admintool.ui.player.search.open", Keys.MOUSE_LEFT)
                        if (!keyword.isNullOrEmpty()) {
                            +action("admintool.ui.player.search.clear", Keys.MOUSE_RIGHT)
                        }
                    }

                    onClick("admintool.ui.player.search") { context ->
                        when {
                            context.isLeftClick ||
                                (context.isRightClick && keyword.isNullOrBlank()) ->
                                context.go<String?>(UIPlayerSearch()) { result ->
                                    context.holder.editStore {
                                        playerUIData.filter.keyword = result.orEmpty()
                                    }
                                    context.holder.update()
                                }

                            context.isRightClick && !keyword.isNullOrBlank() -> {
                                context.holder.editStore { playerUIData.filter.keyword = "" }
                                context.holder.update()
                            }
                        }
                    }
                }

            39 to
                item(holder.plugin.heads.get("prev")) {
                    name("admintool.ui.page.prev")

                    onClick("admintool.ui.page.player.prev") { context ->
                        context.holder.pageStore.compute(this@UIPlayer.key()) { _, _ ->
                            (page - 1).coerceAtLeast(1)
                        }
                        context.playSound(Sounds.previous)
                        context.holder.update()
                    }
                }
            40 to item(Items.BOOK) { name("admintool.ui.page", "%d/%d".format(page, maxPage)) }
            41 to
                item(holder.plugin.heads.get("next")) {
                    name("admintool.ui.page.next")

                    onClick("admintool.ui.page.player.next") { context ->
                        context.holder.pageStore.compute(this@UIPlayer.key()) { _, _ ->
                            (page + 1).coerceAtMost(maxPage)
                        }
                        context.playSound(Sounds.next)
                        context.holder.update()
                    }
                }

            44 to
                item(Items.LODESTONE) {
                    name("admintool.ui.player.reload")

                    onClick("admintool.ui.player.reload") { context -> context.holder.update() }
                }
        }
    }

    @Serializable data class Data(var filter: PlayerFilter = PlayerFilter())
}
