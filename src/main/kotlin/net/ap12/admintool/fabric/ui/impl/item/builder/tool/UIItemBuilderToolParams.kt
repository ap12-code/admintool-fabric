package net.ap12.admintool.fabric.ui.impl.item.builder.tool

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.minecraft.world.item.component.Tool

class UIItemBuilderToolParams(private val viewContent: Int, private val oldComponent: Tool) : UI {
    override val id: Key = AdminToolMod.key("item.builder.tool.params")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val defaultMiningSpeed = holder.createStringKey()
            val damagePerBlock = holder.createStringKey()
            val canDestroyBlocksInCreative = holder.createStringKey()

            base {
                title("admintool.ui.item.builder.tool.dialog")
                when (viewContent) {
                    1 ->
                        numberRange(defaultMiningSpeed) {
                            label("admintool.ui.item.builder.tool.default_mining_speed")
                            step(0.1f)
                            initial(oldComponent.defaultMiningSpeed())
                            range(0..10)
                        }
                    2 ->
                        numberRange(damagePerBlock) {
                            label("admintool.ui.item.builder.tool.damage_per_block")
                            step(1.0f)
                            initial(oldComponent.damagePerBlock().toFloat())
                            range(0..10)
                        }
                    3 ->
                        bool(canDestroyBlocksInCreative) {
                            label(
                                t("admintool.ui.item.builder.tool.can_destroy_blocks_in_creative")
                            )
                            initial(oldComponent.canDestroyBlocksInCreative())
                        }
                }
            }

            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action(holder.createStringKey()) { response ->
                            val defaultMiningSpeedValue = response.getFloat(defaultMiningSpeed)
                            val damagePerBlockValue = response.getInt(damagePerBlock)
                            val canDestroyBlocksInCreativeValue =
                                response.getBoolean(canDestroyBlocksInCreative)
                            val result =
                                Tool(
                                    oldComponent.rules,
                                    defaultMiningSpeedValue,
                                    damagePerBlockValue,
                                    canDestroyBlocksInCreativeValue,
                                )

                            response.holder.back(result)
                        }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action(holder.createStringKey()) { response ->
                            response.holder.back(oldComponent)
                        }
                    }
                }
            }
        }
}
