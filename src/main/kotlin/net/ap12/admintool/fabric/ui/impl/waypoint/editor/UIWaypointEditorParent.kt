package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.paginator.UIPaginator
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.waypoint.element.WaypointGroup
import net.kyori.adventure.key.Key
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class UIWaypointEditorParent : UIPaginator<WaypointGroup?>() {
    override val id: Key = AdminToolMod.key("waypoint.editor.parent")

    override fun buildItem(holder: AdminToolUIHolder, element: WaypointGroup?): ItemBuilder =
        item(element?.icon ?: ItemStack(Items.BARRIER)) {
            name(element?.name?.toComponent() ?: t("admintool.ui.waypoint.editor.parent.empty"))

            lore {
                +""
                +action("admintool.ui.waypoint.editor.parent.select")
            }
            onClick("admintool.ui.waypoint.editor.parent.${element?.id ?: "empty"}") { context ->
                context.back(element)
            }
        }

    override fun getContent(holder: AdminToolUIHolder): List<WaypointGroup?> = buildList {
        addFirst(null)
        addAll(holder.plugin.waypointManager.listGroups(holder.player.uuid))
    }
}
