package net.ap12.admintool.fabric.ui.impl.player.details.abilities

import net.ap12.admintool.fabric.openinv.OfflinePlayer
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.player.details.AbstractUIPlayerDetails
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.world.entity.player.Abilities
import net.minecraft.world.item.Items
import java.util.*

class UIPlayerAbilities(val player: OfflinePlayer) :
    AbstractUIPlayerDetails("abilities", player.getUUID()) {
    private fun getOfflinePlayer(holder: AdminToolUIHolder, target: UUID): OfflinePlayer {
        return requireNotNull(holder.plugin.offlinePlayerManager.get(target))
    }

    private fun updateAbilities(
        holder: AdminToolUIHolder,
        target: UUID,
        updater: Abilities.() -> Unit,
    ) {
        val player = getOfflinePlayer(holder, target)
        player.getAbilities().updater()
        if (player.isOnline()) {
            player.serverPlayer.onUpdateAbilities()
        }
    }

    override fun createChild(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val player = getPlayer(holder)
            val nmsPlayer = getOfflinePlayer(holder, player.uuid)
            val abilities = nmsPlayer.getAbilities()

            18 to
                item(Items.FEATHER) {
                    name("admintool.ui.player.details.abilities.can_fly")

                    lore {
                        if (abilities.mayfly) {
                            +field("admintool.value.state.yes")
                        } else {
                            +field("admintool.value.state.no")
                        }
                        +""
                        +action("admintool.ui.player.details.abilities.can_fly.switch", Keys.CLICK)
                    }

                    onClick("admintool.ui.player.details.abilities.can_fly") { context ->
                        updateAbilities(context.holder, player.uuid) { mayfly = true }
                        context.holder.update()
                    }
                }

            19 to
                item(Items.DIAMOND_BOOTS) {
                    name("admintool.ui.player.details.abilities.fly_speed")

                    lore {
                        +field("admintool.ui.current", "%.2f".format(abilities.flyingSpeed))
                        +""
                        +action(
                            "admintool.ui.player.details.abilities.fly_speed.edit",
                            Keys.MOUSE_LEFT,
                        )
                        +action(
                            "admintool.ui.player.details.abilities.fly_speed.default",
                            Keys.MOUSE_RIGHT,
                        )
                    }

                    onClick("admintool.ui.player.details.abilities.fly_speed") { context ->
                        if (context.isRightClick) {
                            updateAbilities(context.holder, player.uuid) {
                                flyingSpeed = DEFAULT_FLY_SPEED
                            }
                        } else if (context.isLeftClick) {
                            context.go<Float?>(
                                UIPlayerAbilitiesSpeed("fly_speed", abilities.flyingSpeed)
                            ) { result ->
                                if (result != null) {
                                    updateAbilities(context.holder, player.uuid) {
                                        flyingSpeed = result
                                    }
                                }
                            }
                        }
                        context.holder.update()
                    }
                }

            21 to
                item(Items.LEATHER_BOOTS) {
                    name("admintool.ui.player.details.abilities.walk_speed")

                    lore {
                        +field("admintool.ui.current", "%.2f".format(abilities.walkingSpeed))
                        +""
                        +action(
                            "admintool.ui.player.details.abilities.walk_speed.edit",
                            Keys.MOUSE_LEFT,
                        )
                        +action(
                            "admintool.ui.player.details.abilities.walk_speed.default",
                            Keys.MOUSE_RIGHT,
                        )
                    }

                    onClick("admintool.ui.player.details.abilities.walk_speed") { context ->
                        if (context.isRightClick) {
                            updateAbilities(context.holder, player.uuid) {
                                walkingSpeed = DEFAULT_WALK_SPEED
                            }
                        } else if (context.isLeftClick) {
                            context.go<Float?>(
                                UIPlayerAbilitiesSpeed("walk_speed", abilities.walkingSpeed)
                            ) { result ->
                                if (result != null) {
                                    updateAbilities(context.holder, player.uuid) {
                                        walkingSpeed = result
                                    }
                                }
                            }
                        }
                        context.holder.update()
                    }
                }

            22 to
                item(Items.OAK_PLANKS) {
                    name("admintool.ui.player.details.abilities.can_build")

                    lore {
                        if (abilities.mayBuild) {
                            +field("admintool.value.state.yes")
                        } else {
                            +field("admintool.value.state.no")
                        }
                        +""
                        +action(
                            "admintool.ui.player.details.abilities.can_build.switch",
                            Keys.CLICK,
                        )
                    }

                    onClick("admintool.ui.player.details.abilities.can_build") { context ->
                        updateAbilities(context.holder, player.uuid) {
                            mayBuild = !abilities.mayBuild
                            if (!mayBuild) instabuild = false
                        }
                        context.holder.update()
                    }
                }

            23 to
                item(Items.GRASS_BLOCK) {
                    name("admintool.ui.player.details.abilities.insta_build")

                    lore {
                        if (abilities.instabuild) {
                            +field("admintool.value.state.active")
                        } else {
                            +field("admintool.value.state.inactive")
                        }
                        +""
                        +action(
                            "admintool.ui.player.details.abilities.insta_build.switch",
                            Keys.CLICK,
                        )
                    }

                    onClick("admintool.ui.player.details.abilities.insta_build") { context ->
                        updateAbilities(context.holder, player.uuid) {
                            instabuild = !abilities.instabuild
                        }
                        context.holder.update()
                    }
                }
        }

    companion object {
        private const val DEFAULT_FLY_SPEED = 0.05f
        private const val DEFAULT_WALK_SPEED = 0.1f
    }
}
