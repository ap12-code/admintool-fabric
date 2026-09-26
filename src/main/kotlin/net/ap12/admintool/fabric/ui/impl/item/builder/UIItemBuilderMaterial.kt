package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.paginator.UIPaginator
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.kyori.adventure.key.Key
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.AirItem
import net.minecraft.world.item.Item

class UIItemBuilderMaterial : UIPaginator<ResourceKey<Item>>() {
    override val id: Key = AdminToolMod.key("item.builder.material")

    override fun buildItem(holder: AdminToolUIHolder, element: ResourceKey<Item>): ItemBuilder =
        item(BuiltInRegistries.ITEM.getOrThrow(element).value()) {
            lore {
                +""
                +action("admintool.ui.item.builder.material.select")
            }

            onClick(
                "admintool.ui.item.builder.material.select.${element.identifier().toLanguageKey()}"
            ) { context ->
                context.back(element)
            }
        }

    override fun getContent(holder: AdminToolUIHolder): List<ResourceKey<Item>> =
        BuiltInRegistries.ITEM.entrySet()
            .filter { (_, value) -> value !is AirItem }
            .sortedBy { (key, _) -> key.identifier() }
            .map { (key, _) -> key }
}
