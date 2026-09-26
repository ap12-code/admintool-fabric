package net.ap12.admintool.fabric.ui.impl.item.builder.tool

import java.util.*
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.component.Tool
import net.minecraft.world.level.block.Block

class UIItemBuilderToolRule(
    private val oldRule: Tool.Rule? = null,
    private val index: Int? = null,
) : UI {
    override val id: Key = AdminToolMod.key("item.builder.tool.rules.dialog")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val blocks = holder.createStringKey()
            val speed = holder.createStringKey()
            val correctForDrops = holder.createStringKey()

            base {
                if (index != null) {
                    title(
                        t(
                            "admintool.ui.item.builder.tool.rules.edit.dialog",
                            (index + 1).toComponent(),
                        )
                    )
                } else {
                    title("admintool.ui.item.builder.tool.rules.add.dialog")
                }
                textInput(blocks) {
                    label("admintool.ui.item.builder.tool.rules.entry.blocks")
                    maxLength(Int.MAX_VALUE)
                    initial(oldRule?.blocks()?.joinToString(", ") { it.registeredName })
                }
                textInput(speed) {
                    label("admintool.ui.item.builder.tool.rules.entry.speed")
                    initial(oldRule?.speed()?.toString() ?: "")
                }
                singleOption(correctForDrops) {
                    label("admintool.ui.item.builder.tool.rules.entry.correct_for_drops")
                    initial(oldRule?.correctForDrops()?.get().toString())

                    option("not_set", t("admintool.value.not_set"))
                    option("true", t("admintool.value.true"))
                    option("false", t("admintool.value.false"))
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action(holder.createStringKey()) { response ->
                            val blockTagKey =
                                TagKey.create(
                                    Registries.BLOCK,
                                    Identifier.parse(response.getString(blocks)),
                                )
                            val responseBlocks: HolderSet<Block> =
                                HolderSet.direct(
                                    BuiltInRegistries.BLOCK.getTagOrEmpty(blockTagKey).toList()
                                )

                            val responseSpeed = response.getString(speed).toFloatOrNull()

                            val newComponent =
                                Tool.Rule(
                                    responseBlocks,
                                    Optional.ofNullable(responseSpeed),
                                    Optional.ofNullable(response.getBooleanOrNull(correctForDrops)),
                                )

                            response.holder.back(newComponent)
                        }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action(holder.createStringKey()) { response ->
                            response.holder.back(oldRule)
                        }
                    }
                }
            }
        }
}
