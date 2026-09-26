package net.ap12.admintool.fabric.ui.impl.waypoint.editor

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.paginator.UIPaginator
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

class UIWaypointEditorIcon : UIPaginator<Item>() {
    override val id: Key = AdminToolMod.key("waypoint.editor.icon")

    override fun buildItem(holder: AdminToolUIHolder, element: Item): ItemBuilder =
        item(element) {
            lore {
                +""
                +action("admintool.ui.waypoint.editor.icon.select")
            }

            onClick("admintool.ui.waypoint.editor.icon.select.${element.descriptionId}") { context
                ->
                context.back(element)
            }
        }

    override fun getContent(holder: AdminToolUIHolder): List<Item> =
        BuiltInRegistries.ITEM.entrySet()
            .sortedBy { it.key.identifier().toString() }
            .map { it.value }
}
