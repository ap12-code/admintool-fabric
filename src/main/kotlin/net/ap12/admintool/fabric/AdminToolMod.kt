package net.ap12.admintool.fabric

import kotlinx.serialization.SerializationException
import net.ap12.admintool.compat.HookManager
import net.ap12.admintool.fabric.command.AdminToolCommand
import net.ap12.admintool.fabric.config.AdminToolConfig
import net.ap12.admintool.fabric.config.DataLoader
import net.ap12.admintool.fabric.event.ItemEventListener
import net.ap12.admintool.fabric.event.PlayerAbilitiesFixer
import net.ap12.admintool.fabric.event.TeleportRecorder
import net.ap12.admintool.fabric.i18n.I18n
import net.ap12.admintool.fabric.io.AdminToolPlayerData
import net.ap12.admintool.fabric.io.AdminToolPlayerStore
import net.ap12.admintool.fabric.io.AdminToolPublicStore
import net.ap12.admintool.fabric.openinv.OpenInv
import net.ap12.admintool.fabric.openinv.PlayerManager
import net.ap12.admintool.fabric.ui.HeadManager
import net.ap12.admintool.fabric.ui.ReflectionUILoader
import net.ap12.admintool.fabric.ui.UIManager
import net.ap12.admintool.fabric.util.TaskScheduler
import net.ap12.admintool.fabric.vanish.VanishManager
import net.ap12.admintool.io.AdminToolPublicData
import net.ap12.admintool.io.waypoint.WaypointManager
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtVariant
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class AdminToolMod : ModInitializer {
    val dataFolder: File = FabricLoader.getInstance().configDir.resolve("admintool").toFile()

    // key-value datastore
    val dataStore =
        AdminToolPlayerStore(AdminToolPlayerData.serializer(), AdminToolPlayerData(), this)

    val publicDataStore =
        AdminToolPublicStore(AdminToolPublicData.serializer(), AdminToolPublicData(), this)

    val taskScheduler = TaskScheduler(this)
    lateinit var adventure: MinecraftServerAudiences

    // configuration and file
    val configLoader = DataLoader(AdminToolConfig.serializer(), this, "config.yml")
    val localization = I18n(this)

    // modules
    val offlinePlayerManager = PlayerManager(this)
    val openInv = OpenInv(this)
    val vanisher = VanishManager(this)
    val abilitiesFixer = PlayerAbilitiesFixer(this)
    val teleportRecorder = TeleportRecorder(this)

    val slogger: Logger = LoggerFactory.getLogger("AdminTool")
    val heads = HeadManager(this)
    val ui = UIManager(this)
    val waypointManager = WaypointManager(this)

    var config = AdminToolConfig()

    val hooks = HookManager(this)

    lateinit var server: MinecraftServer

    private val reflector = ReflectionUILoader(this)
    val nbt = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.Gzip
    }

    private var isLoaded = false

    init {
        instance = this
    }

    override fun onInitialize() {
        slogger.info("Registering events...")
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        try {
            slogger.info("Loading configuration...")
            config = configLoader.load()
            slogger.info("Successfully loaded plugin configuration.")
        } catch (e: SerializationException) {
            slogger.error("Failed to load configuration.")
            slogger.error(" -> Please check plugin configuration.")
            slogger.error("Cause: $e")
        }

        try {
            dataStore.init()
        } catch (e: IOException) {
            disable("Failed to initialize plugin player data store: $e")
            return
        }

        try {
            publicDataStore.init()
        } catch (e: IOException) {
            disable("Failed to initialize plugin public data store: $e")
            return
        }

        localization.init()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            AdminToolCommand(dispatcher, this)
        }

        reflector.loadAll()

        UseItemCallback.EVENT.register(ItemEventListener(this))
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            this.server = server
            this.adventure = MinecraftServerAudiences.of(server)
            vanisher.init()
            heads.init()

            dataStore.preload()
            publicDataStore.preload()
            waypointManager.init()

            isLoaded = true
        }
    }

    fun reloadConfig() {
        config = configLoader.load()
    }

    private fun disable(message: String?) {
        slogger.error(message)
    }

    fun onDisable() {
        if (!isLoaded) {
            slogger.warn(
                "Plugin was disabled before initialization was complete, saving will be skipped."
            )
            return
        }
        isLoaded = false

        slogger.info("Saving waypoint data...")
        waypointManager.save()

        slogger.info("Saving user data...")
        dataStore.queue.flush()
        vanisher.unload()

        abilitiesFixer.unload()
    }

    fun isDevelopment(): Boolean {
        return FabricLoader.getInstance().isDevelopmentEnvironment
    }

    fun key(path: String): Identifier = Identifier.fromNamespaceAndPath(NAMESPACE, path)

    companion object {
        private const val NAMESPACE = "admintool"

        private lateinit var instance: AdminToolMod

        fun id(path: String): Identifier =
            Identifier.fromNamespaceAndPath(NAMESPACE, path.replace(":", "."))

        fun key(path: String): Key = Key.key(NAMESPACE, path.replace(":", "."))

        fun permission(node: String) = "$NAMESPACE.$node"

        fun getInstance(): AdminToolMod {
            return instance
        }
    }
}
