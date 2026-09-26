package net.ap12.admintool.fabric.ui.impl.waypoint

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.waypoint.editor.UIWaypointEditor
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.location
import net.ap12.admintool.fabric.util.teleportTo
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.fabric.waypoint.element.WaypointPoint
import net.ap12.admintool.util.isEmptyOrNull
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

class UITeleport(private val parentGroup: WaypointGroup? = null) : Tab {
    override val id: Key = AdminToolMod.key("tp")
    override val size: Int = 54

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            val waypointManager = holder.plugin.waypointManager
            val waypoints =
                parentGroup?.children?.filter { it.isVisible(holder.player.uuid) }
                    ?: waypointManager.listRoot(holder.player.uuid)

            for ((i, waypoint) in waypoints.withIndex()) {
                val iconStack =
                    if (waypoint.icon.isEmptyOrNull()) ItemStack(Items.STONE)
                    else waypoint.icon.copyWithCount(1)
                i to
                    item(iconStack) {
                        name(Component.literal(waypoint.name))

                        lore {
                            +""
                            +action("admintool.ui.tp.teleport", Keys.MOUSE_LEFT)
                            +action("admintool.ui.tp.teleport.spectator", Keys.MOUSE_RIGHT)
                            +action("admintool.ui.tp.edit", Keys.KEY_SHIFT, Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.tp.${waypoint.id}") { context ->
                            if (waypoint is WaypointPoint && !context.isShift) {
                                if (context.isLeftClick) {
                                    // teleport
                                    waypoint.location.moveHere(context.player)
                                    context.close()
                                } else if (context.isRightClick) {
                                    // teleport
                                    context.player.setGameMode(GameType.SPECTATOR)
                                    waypoint.location.moveHere(context.player)
                                    context.close()
                                }
                            } else if (waypoint is WaypointGroup && !context.isShift) {
                                // group
                                context.go<Nothing>(UITeleport(waypoint))
                            } else if (context.isShift) {
                                // edit
                                context.go<Nothing>(
                                    UIWaypointEditor(context.holder.plugin, waypoint)
                                )
                            }
                        }
                    }
            }

            36..44 to Items.STAINED_GLASS_PANE.black
            36 to
                item(Items.SCAFFOLDING) {
                    name("admintool.ui.tp.unstuck")

                    lore {
                        +""
                        +action("admintool.ui.tp.unstuck.click", Keys.CLICK)
                    }

                    onClick("admintool.ui.tp.unstuck") { context ->
                        context.player.unstuck {
                            context.feedback("admintool.ui.tp.unstuck.feedback")
                            context.close()
                        }
                    }
                }
            37 to
                item(Items.OAK_BOAT) {
                    name("admintool.ui.tp.center")

                    lore {
                        +""
                        +action("admintool.ui.tp.center.teleport", Keys.MOUSE_LEFT)
                        +action("admintool.ui.tp.center.teleport.rotation", Keys.MOUSE_RIGHT)
                    }

                    onClick("admintool.ui.tp.center") { context ->
                        val centerLocation =
                            context.player.location().toCenterLocation().also {
                                if (context.isRightClick) it.setRotation(0f, 0f)
                            }

                        context.player.teleportTo(centerLocation)
                        context.feedback(
                            t(
                                "admintool.ui.tp.center.feedback${if (context.isRightClick) ".rotation" else ""}"
                            )
                        )
                        context.close()
                    }
                }
            38 to
                item(Items.SOUL_SAND) {
                    name("admintool.ui.tp.ascend")

                    lore {
                        +""
                        +action("admintool.ui.tp.ascend.click", Keys.CLICK)
                    }

                    onClick("admintool.ui.tp.ascend") { context ->
                        context.player.ascend {
                            context.feedback("admintool.ui.tp.ascend.feedback")
                            context.close()
                        }
                    }
                }
            39 to
                item(Items.MAGMA_BLOCK) {
                    name("admintool.ui.tp.descend")

                    lore {
                        +""
                        +action("admintool.ui.tp.descend.click", Keys.CLICK)
                    }

                    onClick("admintool.ui.tp.descend") { context ->
                        context.player.descend {
                            context.feedback("admintool.ui.tp.descend.feedback")
                            context.close()
                        }
                    }
                }
            44 to
                item(Items.NAME_TAG) {
                    name("admintool.ui.tp.create")
                    onClick("admintool.ui.tp.create") { context ->
                        context.go<Nothing>(UIWaypointEditor(holder.plugin))
                    }
                }
        }
}
