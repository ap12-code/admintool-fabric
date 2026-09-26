package net.ap12.admintool.fabric.vanish

import net.ap12.admintool.fabric.AdminToolMod
import net.minecraft.server.level.ServerPlayer
import org.slf4j.LoggerFactory

class VanishManager(private val plugin: AdminToolMod) : Vanisher {
    private val vanisher = AdminToolVanisher(plugin)
    private val logger = LoggerFactory.getLogger("AdminTool/Vanish")

    fun init() {
        logger.info("Initializing vanishing module.")
        vanisher.init()
    }

    fun unload() {
        logger.info("Unloading vanishing module.")
        vanisher.save()
    }

    override fun vanish(player: ServerPlayer) {
        vanisher.vanish(player)
    }

    override fun appear(player: ServerPlayer) {
        vanisher.appear(player)
    }

    override fun isVanished(player: ServerPlayer): Boolean {
        return vanisher.isVanished(player)
    }

    fun toggle(player: ServerPlayer): Boolean {
        if (isVanished(player)) appear(player) else vanish(player)
        return isVanished(player)
    }

    companion object {
        val PERMISSION_VANISH_USE = AdminToolMod.permission("vanish.use")
    }
}
