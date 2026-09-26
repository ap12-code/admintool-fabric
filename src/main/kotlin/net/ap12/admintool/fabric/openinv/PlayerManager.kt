package net.ap12.admintool.fabric.openinv

import com.mojang.authlib.GameProfile
import com.mojang.logging.LogUtils
import net.ap12.admintool.fabric.AdminToolMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.network.Connection
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.server.players.NameAndId
import net.minecraft.util.ProblemReporter
import net.minecraft.util.Util
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

open class PlayerManager(val mod: AdminToolMod) {
    protected val loadedPlayers = ConcurrentHashMap<UUID, ServerPlayer>()
    val offlinePlayers = mutableListOf<NameAndId>()

    private fun resolveProfile(id: UUID): GameProfile? {
        val services = mod.server.services()
        return services.profileResolver().fetchById(id).getOrNull()
    }

    private fun readCache() {}

    fun get(uuid: UUID): OfflinePlayer? {
        // If player online, use current player instance
        val onlinePlayer = mod.server.playerList.getPlayer(uuid)
        if (onlinePlayer != null) return OfflinePlayer(onlinePlayer)

        // If player loaded, use loaded player instance
        if (loadedPlayers.containsKey(uuid)) return OfflinePlayer(loadedPlayers[uuid]!!)

        // If player offline and not loaded, force load player
        val profile = resolveProfile(uuid) ?: return null
        val serverPlayer = load(profile)
        loadedPlayers[uuid] = serverPlayer
        return OfflinePlayer(serverPlayer)
    }

    fun get(player: OfflinePlayer) = get(player.getUUID())

    fun get(player: NameAndId) = get(requireNotNull(player.id))

    open fun load(profile: GameProfile): ServerPlayer {
        val server = mod.server
        val level = server.overworld()

        val cookie = CommonListenerCookie.createInitial(profile, false)

        val serverPlayer = ServerPlayer(server, level, cookie.gameProfile, cookie.clientInformation)
        val connection = Connection(PacketFlow.SERVERBOUND)
        val packetListener = ServerGamePacketListenerImpl(server, connection, serverPlayer, cookie)

        val problemReporter = ProblemReporter.DISCARDING

        val data =
            server.playerList.loadPlayerData(serverPlayer.nameAndId()).map {
                TagValueInput.create(problemReporter, server.registryAccess(), it)
            }

        serverPlayer.connection = packetListener

        data.ifPresent(serverPlayer::load)
        serverPlayer.advancements.clearTriggers()

        serverPlayer.setServerLevel(level)
        serverPlayer.initInventoryMenu()
        serverPlayer.inventoryMenu.transferState(serverPlayer.containerMenu)
        serverPlayer.stats.markAllDirty()

        return serverPlayer
    }

    fun flush(uuid: UUID, callback: () -> Unit = {}) {
        val serverPlayer = loadedPlayers[uuid] ?: return callback()
        mod.server.executeBlocking { save(serverPlayer, callback) }
    }

    fun flush(player: OfflinePlayer, callback: () -> Unit = {}) {
        mod.server.executeBlocking { save(player.serverPlayer, callback) }
    }

    open fun createWritable(oldData: CompoundTag?): CompoundTag {
        if (oldData == null) return CompoundTag()

        val copied = oldData.copy()
        copied.keySet().removeIf {
            RESET_TAGS.contains(it) ||
                it.startsWith("Bukkit") ||
                (it.startsWith("Paper") && it.length > 5)
        }

        return copied
    }

    open fun restoreHistoricalData(player: ServerPlayer, output: TagValueOutput) {
        val data = output.child("admintool")
        data.putString("lastKnownName", player.scoreboardName)
        // data.putLong("LastLogin", player.loginTime)
        // data.putLong("LastSeen", bukkitPlayer.lastPlayed)
    }

    open fun save(player: ServerPlayer, callback: () -> Unit = {}) {
        val logger = LogUtils.getLogger()
        val server = mod.server

        try {
            val scopedController = ProblemReporter.ScopedCollector(player.problemPath(), logger)
            val playerDir = server.getWorldPath(LevelResource.PLAYER_DATA_DIR)

            val output = TagValueOutput.createWithContext(scopedController, player.registryAccess())
            player.saveWithoutId(output)
            restoreHistoricalData(player, output)

            val tempFile = Files.createTempFile(playerDir, "${player.stringUUID}-", ".dat")
            NbtIo.writeCompressed(output.buildResult(), tempFile)
            val dataFile = playerDir.resolve("${player.stringUUID}.dat")
            val backupFile = playerDir.resolve("${player.stringUUID}.dat_old")
            safeReplaceFile(dataFile, tempFile, backupFile)
            callback()
        } catch (e: Exception) {
            LogUtils.getLogger().warn("Failed to save player data for ${player.scoreboardName}: $e")
        }
    }

    open fun safeReplaceFile(dataFile: Path, tempFile: Path, backupFile: Path) =
        Util.safeReplaceFile(dataFile, tempFile, backupFile)

    fun unloadPlayer(uuid: UUID) {
        if (loadedPlayers.containsKey(uuid)) {
            flush(uuid) { loadedPlayers.remove(uuid) }
        }
    }

    fun loadPlayer(uuid: UUID) {
        val profile = resolveProfile(uuid) ?: return
        loadedPlayers[uuid] = load(profile)
    }

    fun unloadAllPlayers(save: Boolean) {
        for (entry in loadedPlayers.toMap()) {
            if (save) flush(entry.key)
            loadedPlayers.remove(entry.key)
        }
    }

    fun getLoadedPlayers(): List<OfflinePlayer> {
        return loadedPlayers.values.map(::OfflinePlayer).toList()
    }

    companion object {
        private val RESET_TAGS =
            setOf(
                // Entity#saveWithoutId(CompoundTag)
                "CustomName",
                "CustomNameVisible",
                "Silent",
                "NoGravity",
                "Glowing",
                "TicksFrozen",
                "HasVisualFire",
                "Tags",
                "data",
                "Passengers",
                // ServerPlayer#addAdditionalSaveData(CompoundTag)
                // Intentional omissions to prevent mount loss: Attach, Entity, and RootVehicle
                "warden_spawn_tracker", // No longer needed as of 1.21.11
                "entered_nether_pos", // Replaces enteredNetherPosition as of 1.21.6
                "enteredNetherPosition",
                "respawn", // Replaces SpawnXyz fields as of 1.21.6
                "SpawnX",
                "SpawnY",
                "SpawnZ",
                "SpawnForced",
                "SpawnAngle",
                "SpawnDimension",
                "raid_omen_position",
                "ender_pearls",
                "ShoulderEntityLeft",
                "ShoulderEntityRight",
                // Player#addAdditionalSaveData(CompoundTag)
                "LastDeathLocation",
                "current_explosion_impact_pos",
                // LivingEntity#addAdditionalSaveData(CompoundTag)
                "active_effects",
                "sleeping_pos", // Replaces SleepingXyz fields as of 1.21.6
                "SleepingX",
                "SleepingY",
                "SleepingZ",
                "Brain",
                "last_hurt_by_player",
                "last_hurt_by_player_memory_time",
                "last_hurt_by_mob",
                "ticks_since_last_hurt_by_mob",
                "equipment",
                "locator_bar_icon",
            )
    }
}
