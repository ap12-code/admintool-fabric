package net.ap12.admintool.fabric.ui.impl.player.details

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.compat.impl.ViaHook
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.openinv.OfflinePlayer
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.*
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.kyori.adventure.util.Ticks
import net.minecraft.network.chat.TextColor
import net.minecraft.stats.Stats
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import kotlin.time.Clock
import kotlin.time.toKotlinDuration

class UIPlayerDetails(private val target: OfflinePlayer) : UI {
    override val id: Key = AdminToolMod.key("player.details")

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            holder.plugin.heads.createHead(target.getUUID()) { headItem, profile ->
                0 to
                    item(headItem.copy()) {
                        name(profile.name.toComponent())

                        if (target.isOnline()) {
                            val via = holder.plugin.hooks.get<ViaHook>("ViaVersion")?.getInstance()
                            val clientVersion =
                                via?.getPlayerProtocolVersion(target.serverPlayer.getUUID())
                                    ?.name
                                    ?.plus(" &7(Via)") ?: holder.plugin.server.serverVersion
                            val clientBrandName = "Unknown"
                            val locale = target.serverPlayer.locale().toString()
                            val ping = "${target.serverPlayer.connection.latency()}ms"

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
                                +t("admintool.ui.player.details.offline")
                                    .symbolPrefixed(TextColor.RED)
                            }
                        }
                    }
            }

            4 to
                item(Items.APPLE) {
                    name("admintool.ui.player.details.health")

                    val currentHealth = target.serverPlayer.health
                    val maxHealth =
                        target.serverPlayer.getAttribute(Attributes.MAX_HEALTH)?.value ?: 20.0
                    val health = "%.2f&7/&f%.2f".format(currentHealth, maxHealth)

                    val foodLevel = "%d&7/&f20".format(target.serverPlayer.foodData.foodLevel)
                    val saturation =
                        "%.2f&7/&f20.00".format(target.serverPlayer.foodData.saturationLevel)

                    lore {
                        +""
                        +field("admintool.ui.player.details.health.health", health)
                        +field("admintool.ui.player.details.health.food_level", foodLevel)
                        +field("admintool.ui.player.details.health.saturation", saturation)
                        +""
                        +action("admintool.ui.player.details.health.heal.health", Keys.MOUSE_LEFT)
                        +action("admintool.ui.player.details.health.heal.food", Keys.MOUSE_RIGHT)
                        +action(
                            "admintool.ui.player.details.health.heal.saturation",
                            Keys.MOUSE_MIDDLE,
                        )
                        +action(
                            "admintool.ui.player.details.health.heal.all",
                            Keys.KEY_SHIFT,
                            Keys.CLICK,
                        )
                    }

                    onClick("admintool.ui.player.details.health") { context ->
                        val isBoth = context.isShift
                        if (isBoth || context.isMiddleClick)
                            target.serverPlayer.foodData.setSaturation(20f)
                        if (isBoth || context.isRightClick)
                            target.serverPlayer.foodData.foodLevel = 20
                        if (isBoth || context.isLeftClick)
                            target.serverPlayer.health = maxHealth.toFloat()

                        if (target.isOnline()) target.serverPlayer.hurtMarked = true
                        context.holder.plugin.offlinePlayerManager.flush(target.serverPlayer.uuid)
                        context.holder.update()
                    }
                }

            5 to
                item(Items.COMPASS) {
                    name("admintool.ui.player.details.location")

                    lore {
                        +""
                        +"&7» &cX: &f${"%.2f".format(target.serverPlayer.position().x)}"
                        +"&7» &aY: &f${"%.2f".format(target.serverPlayer.position().y)}"
                        +"&7» &9Z: &f${"%.2f".format(target.serverPlayer.position().z)}"
                        +"&7» &6World: &f${target.serverPlayer.level().dimension()}"
                        +""
                        +"&7» &eYaw: &f${"%.2f".format(target.serverPlayer.yRot)}"
                        +"&7» &bPitch: &f${"%.2f".format(target.serverPlayer.xRot)}"
                        +""
                        +action("admintool.ui.player.details.location.edit")
                    }

                    onClick("admintool.ui.player.details.location") { context ->
                        context.go<Nothing>(UIPlayerLocation(target))
                    }
                }

            6 to
                item(Items.BED.red) {
                    name("admintool.ui.player.details.respawn_location")

                    val respawnLocation = target.serverPlayer.respawnConfig?.respawnData
                    if (respawnLocation != null) {
                        val respawnLocation = Location.of(respawnLocation)
                        lore {
                            +""
                            +"&7» &cX: &f${"%.2f".format(respawnLocation.x)}"
                            +"&7» &aY: &f${"%.2f".format(respawnLocation.y)}"
                            +"&7» &9Z: &f${"%.2f".format(respawnLocation.z)}"
                            +"&7» &6World: &f${respawnLocation.level}"
                            +""
                            +"&7» &eYaw: &f${"%.2f".format(respawnLocation.yaw)}"
                            +"&7» &bPitch: &f${"%.2f".format(respawnLocation.pitch)}"
                            +""
                            +action("admintool.ui.player.teleport.location", Keys.MOUSE_LEFT)
                        }
                    } else {
                        lore {
                            +""
                            +t("admintool.ui.player.details.respawn_location.empty")
                                .symbolPrefixed(TextColor.GRAY)
                        }
                    }

                    onClick("admintool.ui.player.details.respawn_location") { context ->
                        if (respawnLocation != null) {
                            val teleportMode = holder.playerStore.options.teleportSpectatorMode
                            teleportMode.teleport(
                                context.player,
                                target.isOnline(),
                                Location.of(respawnLocation),
                            )
                        }
                    }
                }

            7 to
                item(Items.CLOCK) {
                    name("admintool.ui.player.details.time")

                    val firstPlayed = t("admintool.ui.player.details.time.unavailable")

                    val lastPlayed =
                        if (target.isOnline())
                            Clock.System.now()
                                .toLocalDateTime(holder.plugin.config.timezone)
                                .format(datetimeFormatter)
                                .toComponent()
                        else t("admintool.ui.player.details.time.unavailable")

                    val playTime =
                        Ticks.duration(
                                target.serverPlayer.stats
                                    .getValue(Stats.CUSTOM, Stats.PLAY_TIME)
                                    .toLong()
                            )
                            .toKotlinDuration()
                            .format(durationFormatter)

                    lore {
                        +""
                        +field("admintool.ui.player.details.time.first_played", firstPlayed)
                        +field("admintool.ui.player.details.time.last_played", lastPlayed)
                        +field("admintool.ui.player.details.time.play_time", playTime)
                    }
                }

            8 to
                item(Items.EXPERIENCE_BOTTLE) {
                    name("admintool.ui.player.details.experience")

                    val level = "%d".format(target.serverPlayer.experienceLevel)
                    val progress =
                        "%d &7/&f %d (%.2f%%)"
                            .format(
                                (target.serverPlayer.experienceProgress *
                                        target.serverPlayer.xpNeededForNextLevel)
                                    .toInt(),
                                target.serverPlayer.xpNeededForNextLevel,
                                (target.serverPlayer.experienceProgress * 100f),
                            )
                    val total = target.serverPlayer.totalExperience.toString()

                    lore {
                        +""
                        +field("admintool.ui.player.details.experience.level", level)
                        +field("admintool.ui.player.details.experience.progress", progress)
                        +field("admintool.ui.player.details.experience.total", total)
                        +""
                        +action("admintool.ui.player.details.experience.change")
                    }
                }

            9..<18 to Items.STAINED_GLASS_PANE.black

            PlayerDetailModules(target, holder).apply(this, 18)
        }

    fun update(holder: AdminToolUIHolder) {
        if (!target.isOnline()) return
        val created = create(holder).build()
        for (i in 0..9) {
            holder.setItem(i, holder.incrementStateId(), created.getItem(i))
            holder.broadcastChanges()
        }
    }

    override fun onBack(holder: AdminToolUIHolder, next: Runnable) {
        holder.plugin.offlinePlayerManager.flush(target, next)
    }

    override fun onClose(holder: AdminToolUIHolder) {
        holder.plugin.offlinePlayerManager.flush(target)
    }

    companion object {

        @OptIn(FormatStringsInDatetimeFormats::class)
        private val datetimeFormatter = LocalDateTime.Format {
            byUnicodePattern("yyyy/MM/dd HH:mm:ss")
        }

        private val durationFormatter = DurationFormatter()
    }
}
