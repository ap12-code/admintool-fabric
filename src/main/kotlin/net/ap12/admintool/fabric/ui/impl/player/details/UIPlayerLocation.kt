package net.ap12.admintool.fabric.ui.impl.player.details

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.openinv.OfflinePlayer
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Location
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.inventory.ItemClickContext
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

class UIPlayerLocation(private val target: OfflinePlayer) : UI {
    override val id: Key = AdminToolMod.key("player.details.location")
    private var targetLocation = Location.of(target.serverPlayer)

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        inventory(holder) {
            0 to
                item(Items.COMPASS) {
                    name("admintool.ui.player.details.location")

                    lore {
                        +""
                        +"&7» &cX: &f${"%.2f".format(targetLocation.x)}"
                        +"&7» &aY: &f${"%.2f".format(targetLocation.y)}"
                        +"&7» &9Z: &f${"%.2f".format(targetLocation.z)}"
                        +"&7» &6World: &f${targetLocation.level}"
                        +divider
                        +"&7» &eYaw: &f${"%.2f".format(targetLocation.yaw)}"
                        +"&7» &bPitch: &f${"%.2f".format(targetLocation.pitch)}"
                    }

                    onClick("admintool.ui.player.details.location") {}
                }

            9..17 to Items.STAINED_GLASS_PANE.black

            18 to
                item(Items.ENDER_PEARL) {
                    name("admintool.ui.player.details.location.teleport")

                    lore {
                        +field("admintool.ui.player.details.location.teleport.description")
                        +""
                        +action("admintool.ui.player.details.location.teleport.click", Keys.CLICK)
                    }

                    onClick("admintool.ui.player.details.location.teleport") { context ->
                        val teleportMode = holder.playerStore.options.teleportSpectatorMode
                        teleportMode.teleport(context.player, target.isOnline(), targetLocation)
                        context.close()
                    }
                }
            19 to
                item(Items.BED.red) {
                    name("admintool.ui.player.details.location.set_here")

                    lore {
                        +field("admintool.ui.player.details.location.set_here.description")
                        +""
                        +action("admintool.ui.player.details.location.set_here.click", Keys.CLICK)
                    }

                    onClick("admintool.ui.player.details.location.set_here") { context ->
                        val newLocation = Location.of(context.player).clone()
                        if (target.isOnline()) {
                            context.player.setGameMode(GameType.SPECTATOR)
                            newLocation.moveHere(target.serverPlayer)
                        } else {
                            targetLocation = newLocation
                        }
                        context.holder.update()
                    }
                }
        }

    override fun onClose(holder: AdminToolUIHolder) {
        targetLocation.moveHere(target.serverPlayer)
        holder.plugin.offlinePlayerManager.flush(target.getUUID())
    }

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        val targetNMS = holder.plugin.offlinePlayerManager.get(target.getUUID())
        if (targetNMS != null) {
            targetNMS.setLocation(targetLocation)
            holder.plugin.offlinePlayerManager.flush(targetNMS, next)
        } else next()
    }

    override fun onClick(context: ItemClickContext): Boolean = context.hasAction()
}
