package net.ap12.admintool.fabric.ui.impl.item.builder.tool

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.item.builder.AbstractUIItemBuilder
import net.ap12.admintool.fabric.ui.impl.item.builder.ItemBuilderPlayerData
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Tool

class UIItemBuilderTool(private val dataKey: String) : AbstractUIItemBuilder(dataKey, "tool") {
    override fun updateStack(holder: AdminToolUIHolder, oldStack: ItemStack, newStack: ItemStack) {
        // remove empty (default) tool component
        val newComponent = newStack.get(DataComponents.TOOL)
        val defaultComponent = oldStack.item.components().get(DataComponents.TOOL)

        if (newComponent != null && defaultComponent != null) {
            val isDefault =
                newComponent.defaultMiningSpeed() == defaultComponent.defaultMiningSpeed() &&
                    newComponent.damagePerBlock() == defaultComponent.damagePerBlock() &&
                    newComponent.canDestroyBlocksInCreative() ==
                        defaultComponent.canDestroyBlocksInCreative() &&
                    newComponent.rules().isEmpty()

            if (isDefault) newStack.set(DataComponents.TOOL, defaultComponent)
        }

        super.updateStack(holder, oldStack, newStack)
    }

    override fun createChild(
        holder: AdminToolUIHolder,
        data: ItemBuilderPlayerData,
    ): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val oldStack = requireNotNull(getData(holder).stack)
            val oldComponent = oldStack.get(DataComponents.TOOL)
            val defaultComponent = oldStack.item.components().get(DataComponents.TOOL)
            val newStack = oldStack.copy()

            var i = 17

            9..17 to Items.STAINED_GLASS_PANE.black
            ++i to
                item(if (oldComponent != null) Items.WOOL.lime else Items.WOOL.red) {
                    name("admintool.ui.item.builder.tool.state")

                    lore {
                        if (oldComponent != null) {
                            +(field("admintool.ui.item.builder.tool.state.enabled")
                                .withColor(TextColor.GREEN))
                            +""
                            +action("admintool.ui.item.builder.tool.state.disable", Keys.MOUSE_LEFT)
                        } else {
                            +(field("admintool.ui.item.builder.tool.state.disabled")
                                .withColor(TextColor.RED))
                            +""
                            +action("admintool.ui.item.builder.tool.state.enable", Keys.MOUSE_LEFT)
                        }
                    }

                    onClick("admintool.ui.item.builder.tool.state") { context ->
                        if (oldComponent == null) {
                            newStack.set(DataComponents.TOOL, defaultComponent)
                        } else {
                            newStack.remove(DataComponents.TOOL)
                        }
                        updateStack(context.holder, oldStack, newStack)
                    }
                }

            if (oldComponent != null) {
                ++i to
                    item(Items.IRON_SHOVEL) {
                        name("admintool.ui.item.builder.tool.default_mining_speed")

                        lore {
                            +field(
                                "admintool.ui.current",
                                Component.literal(oldComponent.defaultMiningSpeed().toString()),
                            )
                        }

                        onClick("admintool.ui.item.builder.tool.default_mining_speed") { context ->
                            context.go<Tool>(UIItemBuilderToolParams(1, oldComponent)) { result ->
                                newStack.set(DataComponents.TOOL, result)
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.COBBLESTONE) {
                        name("admintool.ui.item.builder.tool.damage_per_block")

                        lore {
                            +field(
                                "admintool.ui.current",
                                Component.literal(oldComponent.damagePerBlock().toString()),
                            )
                        }

                        onClick("admintool.ui.item.builder.tool.damage_per_block") { context ->
                            context.go<Tool>(UIItemBuilderToolParams(2, oldComponent)) { result ->
                                newStack.set(DataComponents.TOOL, result)
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.GRASS_BLOCK) {
                        name("admintool.ui.item.builder.tool.can_destroy_blocks_in_creative")

                        lore {
                            +field(
                                "admintool.ui.current",
                                Component.literal(
                                    oldComponent.canDestroyBlocksInCreative().toString()
                                ),
                            )
                        }

                        onClick("admintool.ui.item.builder.tool.can_destroy_blocks_in_creative") {
                            context ->
                            context.go<Tool>(UIItemBuilderToolParams(3, oldComponent)) { result ->
                                newStack.set(DataComponents.TOOL, result)
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.NAME_TAG) {
                        name("admintool.ui.item.builder.tool.rules")

                        lore {
                            +""
                            +action("admintool.ui.item.builder.tool.rules.edit", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.tool.rules") { context ->
                            context.go<Nothing>(UIItemBuilderToolRules(dataKey))
                        }
                    }
            }
        }
}
