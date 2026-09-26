package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.location
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.ap12.admintool.fabric.waypoint.element.Waypoint
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.ap12.admintool.waypoint.WaypointVisibility
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.floor

class UIWaypointEditor(private val plugin: AdminToolMod, private val target: Waypoint? = null) :
    UI {
    override val id: Key = AdminToolMod.key("waypoint.editor")

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            var data = holder.playerStore.waypointEditor.copy()
            val isEdit = target != null

            if (isEdit && data.id == null) {
                data = WaypointEditorPlayerData.fromWaypoint(target)
            }

            if (data.type == WaypointType.POINT && data.location == null) {
                data = data.copy(location = holder.player.location())
            } else if (data.type == WaypointType.GROUP) {
                data = data.copy(location = null)
            }

            val iconStack =
                if (data.icon.isEmpty) ItemStack(Items.STONE) else data.icon.copyWithCount(1)
            0 to
                item(iconStack) {
                    name(Component.literal(data.name ?: ">> INVALID <<"))

                    lore {
                        +t("admintool.ui.waypoint.editor.preview")
                        +""
                        +action("admintool.ui.tp.teleport", Keys.MOUSE_LEFT)
                        +action("admintool.ui.tp.teleport.spectator", Keys.MOUSE_RIGHT)
                        +action("admintool.ui.tp.edit", Keys.KEY_SHIFT, Keys.MOUSE_LEFT)
                    }
                }

            9..17 to Items.STAINED_GLASS_PANE.black

            18 to
                item(Items.NAME_TAG) {
                    name("admintool.ui.waypoint.editor.name")

                    lore {
                        if (!data.name.isNullOrEmpty()) {
                            +Component.literal(data.name!!).symbolPrefixed(TextColor.GRAY)
                        } else {
                            +t("admintool.ui.waypoint.editor.name.empty")
                                .symbolPrefixed(TextColor.RED, true)
                        }
                    }

                    onClick("admintool.ui.waypoint.editor.name") { context ->
                        context.go<String?>(UIWaypointEditorName(data)) {
                            if (it == null) return@go
                            context.holder.editStore { waypointEditor.name = it }
                            context.holder.update()
                        }
                    }
                }

            19 to
                item(if (data.type == WaypointType.GROUP) Items.WOOL.green else Items.WOOL.yellow) {
                    name("admintool.ui.waypoint.editor.type")
                    lore {
                        WaypointType.entries.forEach {
                            +t(it.translationKey())
                                .symbolPrefixed(
                                    if (data.type == it) TextColor.YELLOW else TextColor.WHITE,
                                    true,
                                )
                        }
                        +""
                        +action("admintool.ui.waypoint.editor.type.change", Keys.MOUSE_LEFT)
                    }

                    onClick("admintool.ui.waypoint.editor.type") { context ->
                        val nextType =
                            if (data.type == WaypointType.GROUP) {
                                WaypointType.POINT
                            } else {
                                WaypointType.GROUP
                            }

                        context.holder.editStore { waypointEditor.type = nextType }
                        context.holder.update()
                    }
                }

            20 to
                item(
                    if (data.visibility == WaypointVisibility.PUBLIC) Items.ENDER_EYE
                    else Items.ENDER_PEARL
                ) {
                    name("admintool.ui.waypoint.editor.visibility")
                    lore {
                        WaypointVisibility.entries.forEach {
                            +t(it.translationKey())
                                .symbolPrefixed(
                                    if (data.visibility == it) TextColor.YELLOW
                                    else TextColor.WHITE,
                                    true,
                                )
                        }
                        +""
                        +action("admintool.ui.waypoint.editor.visibility.change", Keys.MOUSE_LEFT)
                    }

                    onClick("admintool.ui.waypoint.editor.visibility") { context ->
                        val nextVisibility =
                            if (data.visibility == WaypointVisibility.PUBLIC) {
                                WaypointVisibility.PRIVATE
                            } else {
                                WaypointVisibility.PUBLIC
                            }

                        context.holder.editStore { waypointEditor.visibility = nextVisibility }
                        context.holder.update()
                    }
                }

            21 to
                item(Items.WOOL.blue) {
                    name("admintool.ui.waypoint.editor.parent")

                    val parentGroup =
                        plugin.waypointManager.listGroups(holder.player.uuid).find {
                            it.id == data.parent
                        }
                    lore {
                        if (parentGroup != null) {
                            +field(parentGroup.name)
                        } else {
                            +field("admintool.ui.waypoint.editor.parent.empty")
                        }
                        +""
                        +action("admintool.ui.waypoint.editor.parent.change", Keys.MOUSE_LEFT)
                    }

                    onClick("admintool.ui.waypoint.editor.parent") { context ->
                        context.go<WaypointGroup?>(UIWaypointEditorParent()) { group ->
                            context.holder.editStore { waypointEditor.parent = group?.id }
                            context.holder.update()
                        }
                    }
                }

            22 to
                item(iconStack) {
                    name("admintool.ui.waypoint.editor.icon")

                    lore {
                        +field(iconStack.item.descriptionId)
                        +""
                        +action("admintool.ui.waypoint.editor.icon.change", Keys.MOUSE_LEFT)
                    }

                    onClick("admintool.ui.waypoint.editor.icon") { context ->
                        context.go<Item>(UIWaypointEditorIcon()) { material ->
                            context.holder.editStore { waypointEditor.icon = ItemStack(material) }
                            context.holder.update()
                        }
                    }
                }

            val location = (data.location ?: holder.player.location())
            if (data.type == WaypointType.POINT) {
                23 to
                    item(Items.COMPASS) {
                        name("admintool.ui.waypoint.editor.location")

                        lore {
                            +""
                            +field(
                                "admintool.ui.waypoint.editor.location.x",
                                "%.2f".format(location.x),
                            )
                            +field(
                                "admintool.ui.waypoint.editor.location.y",
                                "%.2f".format(location.y),
                            )
                            +field(
                                "admintool.ui.waypoint.editor.location.z",
                                "%.2f".format(location.z),
                            )
                            +field(
                                "admintool.ui.waypoint.editor.location.world",
                                location.level.identifier().toString(),
                            )
                            +divider
                            +field(
                                "admintool.ui.waypoint.editor.location.yaw",
                                "%.2f".format(location.yaw),
                            )
                            +field(
                                "admintool.ui.waypoint.editor.location.pitch",
                                "%.2f".format(location.pitch),
                            )
                            +divider
                            +""
                            +action("admintool.ui.waypoint.editor.location.change", Keys.MOUSE_LEFT)
                            +action(
                                "admintool.ui.waypoint.editor.location.change.center",
                                Keys.MOUSE_RIGHT,
                            )
                            +action(
                                "admintool.ui.waypoint.editor.location.change.here",
                                Keys.MOUSE_MIDDLE,
                            )
                        }

                        onClick("admintool.ui.waypoint.editor.location") { context ->
                            var newLocation = location.clone()
                            val roundYaw = { yaw: Float ->
                                (floor(yaw / 90.0) * 90).toInt().toFloat()
                            }
                            if (context.isLeftClick) {
                                // TODO: goto location ui
                            } else if (context.isRightClick) {
                                // center
                                newLocation = newLocation.toCenterLocation()
                                newLocation.setRotation(
                                    0f,
                                    if (context.isShift) roundYaw(location.yaw) else 0f,
                                )
                            } else if (context.isMiddleClick) {
                                // set here
                                newLocation = context.player.location().clone()
                                if (context.isShift)
                                    newLocation.setRotation(0f, roundYaw(newLocation.yaw))
                            }

                            context.holder.editStore { waypointEditor.location = newLocation }
                            context.holder.update()
                        }
                    }
            }

            if (isEdit) {
                7 to
                    item(plugin.heads.get("trash")) {
                        name("admintool.ui.waypoint.editor.remove")

                        lore {
                            +""
                            +action(
                                "admintool.ui.waypoint.editor.remove.description",
                                Keys.KEY_SHIFT,
                                Keys.MOUSE_LEFT,
                            )
                        }

                        onClick("admintool.ui.waypoint.editor.remove") { context ->
                            if (context.isShift) {
                                plugin.waypointManager.remove(target.id, context.player.uuid) {
                                    context.holder.editStore {
                                        waypointEditor = WaypointEditorPlayerData()
                                    }
                                    context.back(null)
                                }
                            }
                        }
                    }
            }

            8 to
                if (data.isValid()) {
                    item(plugin.heads.get("checkmark")) {
                        name(
                            if (isEdit) t("admintool.ui.waypoint.editor.apply")
                            else t("admintool.ui.waypoint.editor.create")
                        )

                        val parentName =
                            data.parent?.let {
                                plugin.waypointManager
                                    .getGroup(holder.player.uuid, it)
                                    ?.name
                                    .toComponent()
                            } ?: t("admintool.ui.waypoint.editor.parent.empty")

                        lore {
                            +field("admintool.ui.waypoint.editor.name", data.name!!)
                            +field("admintool.ui.waypoint.editor.type", data.type.translationKey())
                            +field(
                                "admintool.ui.waypoint.editor.visibility",
                                data.visibility.translationKey(),
                            )
                            +field("admintool.ui.waypoint.editor.parent", parentName)
                            +field(
                                "admintool.ui.waypoint.editor.icon",
                                data.icon.item.descriptionId,
                            )
                            if (data.type == WaypointType.POINT) {
                                +field(
                                    "admintool.ui.waypoint.editor.location",
                                    "&c%.2f &a%.2f &9%.2f &6%s &7(&e%.2f &b%.2f&7)"
                                        .format(
                                            location.x,
                                            location.y,
                                            location.z,
                                            location.level.identifier().toString(),
                                            location.yaw,
                                            location.pitch,
                                        )
                                        .toComponent(),
                                )
                            }
                        }

                        onClick("admintool.ui.waypoint.editor.create") { context ->
                            val waypoint = data.toWaypoint(plugin, holder.player)
                            require(waypoint != null)
                            val next: Runnable = {
                                context.holder.editStore {
                                    waypointEditor = WaypointEditorPlayerData()
                                }
                                context.back(null)
                            }

                            if (isEdit) {
                                plugin.waypointManager.update(
                                    target.id,
                                    context.player.uuid,
                                    waypoint,
                                    next,
                                )
                            } else {
                                plugin.waypointManager.create(context.player.uuid, waypoint, next)
                            }
                        }
                    }
                } else {
                    item(Items.STRUCTURE_VOID) {
                        name("admintool.ui.waypoint.editor.invalid")
                        lore {
                            +""
                            +t("admintool.ui.waypoint.editor.invalid.description")
                            if (data.name.isNullOrEmpty())
                                +t("admintool.ui.waypoint.editor.invalid.name")
                            if (data.type == WaypointType.POINT && data.location == null)
                                +t("admintool.ui.waypoint.editor.invalid.location")
                        }
                    }
                }
        }
}
