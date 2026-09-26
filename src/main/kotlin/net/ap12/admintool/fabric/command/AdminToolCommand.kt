package net.ap12.admintool.fabric.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.json.Json
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolContext
import net.ap12.admintool.fabric.ui.AdminToolUI
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.impl.player.details.UIPlayerInventory
import net.ap12.admintool.fabric.util.components.split
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.components.toNative
import net.ap12.admintool.fabric.util.inventory.join
import net.ap12.admintool.fabric.util.inventory.lore
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.NameAndId
import net.minecraft.world.item.ItemStack

@Suppress("SameReturnValue")
class AdminToolCommand(
    registerer: CommandDispatcher<CommandSourceStack>,
    private val plugin: AdminToolMod,
) {
    private val command =
        Commands.literal("admintool")
            .then(
                Commands.literal("debug")
                    .then(Commands.literal("reload-lang").executes(::debugReloadLang))
                    .then(Commands.literal("rebuild-heads").executes(::debugRebuildHeadsCache))
                    .then(
                        Commands.literal("reset-data")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .executes(::debugResetData)
                            )
                    )
                    .then(Commands.literal("reload-data").executes(::debugReloadData))
                    .then(
                        Commands.literal("show-data")
                            .then(
                                Commands.argument("player", GameProfileArgument.gameProfile())
                                    .executes(::debugShowPlayerData)
                            )
                    )
                    .then(
                        Commands.literal("openinv")
                            .then(
                                Commands.argument("player", GameProfileArgument.gameProfile())
                                    .executes(::debugOpenInv)
                            )
                    )
                    .then(
                        Commands.literal("test-parse")
                            .then(
                                Commands.argument("str", StringArgumentType.greedyString())
                                    .executes(::debugParseTest)
                            )
                    )
                    .executes(::debugInfo)
            )
            .then(Commands.literal("get").executes(::getAdminTool))
            .then(Commands.literal("reload").executes(::reloadConfig))
            .executes(::openAdminTool)

    init {
        registerer.register(command)
    }

    private fun getPlayerProfile(context: CommandContext<CommandSourceStack>): NameAndId? {
        return GameProfileArgument.getGameProfiles(context, "player").firstOrNull()
    }

    private fun getPlayer(context: CommandContext<CommandSourceStack>): ServerPlayer {
        return EntityArgument.getPlayer(context, "player")
    }

    private fun openAdminTool(context: CommandContext<CommandSourceStack>): Int {
        plugin.ui.open(context.source.playerOrException)
        return 1
    }

    private fun reloadConfig(context: CommandContext<CommandSourceStack>): Int {
        try {
            plugin.reloadConfig()
            context.source.playerOrException.sendSystemMessage(
                "&aConfiguration reloaded".toComponent()
            )
        } catch (e: Exception) {
            context.source.playerOrException.sendSystemMessage(
                "&cFailed to reload configuration: ${e.message}".toComponent()
            )
        }
        return 1
    }

    private fun debugReloadLang(context: CommandContext<CommandSourceStack>): Int {
        plugin.localization.reload()
        context.source.playerOrException.sendSystemMessage("Reloaded localizations.".toComponent())

        return 1
    }

    private fun debugReloadData(context: CommandContext<CommandSourceStack>): Int {
        plugin.server
        plugin.dataStore.queue.deferred.clear()
        plugin.dataStore.preload()
        plugin.publicDataStore.preload()
        context.source.playerOrException.sendSystemMessage("Reloaded player data.".toComponent())

        return 1
    }

    private fun debugRebuildHeadsCache(context: CommandContext<CommandSourceStack>): Int {
        plugin.heads.rebuild()
        return 1
    }

    private fun debugResetData(context: CommandContext<CommandSourceStack>): Int {
        val player = getPlayer(context)

        plugin.dataStore.delete(player.uuid)
        context.source.sendSystemMessage(
            "Successfully removed user data for ${player.name} (${player.stringUUID})".toComponent()
        )
        return 1
    }

    private fun debugInfo(context: CommandContext<CommandSourceStack>): Int {

        val info = lore {
            +"&aDebug information:"
            +"&e -> &fScheduled data write tasks: ${plugin.dataStore.queue.deferred.size}\n"
            +"&e -> &fLoaded players: ${plugin.server.playerList.players.size}\n"
        }

        context.source.sendSystemMessage(info.join("\n"))

        return 1
    }

    private fun debugShowPlayerData(context: CommandContext<CommandSourceStack>): Int {
        val player =
            getPlayerProfile(context)
                ?: run {
                    context.source.sendSystemMessage(
                        t("argument.entity.notfound.player").withColor(TextColor.RED)
                    )
                    return 0
                }
        val data = plugin.dataStore.get(player.id)

        context.source.sendSystemMessage(Json.encodeToString(data).replace("\\", "").toComponent())
        return 1
    }

    private fun debugOpenInv(context: CommandContext<CommandSourceStack>): Int {
        val viewer = context.source.playerOrException
        val targetProfile =
            getPlayerProfile(context)
                ?: run {
                    context.source.sendSystemMessage(
                        t("argument.entity.notfound.player").withColor(TextColor.RED)
                    )
                    return 0
                }

        viewer.nextContainerCounter()
        val holder =
            AdminToolUIHolder(
                AdminToolContext(plugin, viewer, viewer.containerCounter),
                AdminToolUI(UIPlayerInventory()),
            )
        plugin.offlinePlayerManager.get(targetProfile)?.let {
            plugin.openInv.createInventory(it, holder)
        }

        return 1
    }

    private fun debugParseTest(context: CommandContext<CommandSourceStack>): Int {
        val text = StringArgumentType.getString(context, "str")
        val miniMessage = MiniMessage.miniMessage()

        context.source.sendSystemMessage(
            miniMessage.deserialize(text).toNative().split().join("\n")
        )

        return 1
    }

    private fun getAdminTool(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        if (!player.inventory.any(ItemStack::isEmpty)) {
            return 0
        }
        plugin.ui.giveItem(player)
        return 1
    }
}
