package net.ap12.admintool.fabric.ui.impl.item.builder.tool

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.item.builder.AbstractUIItemBuilder
import net.ap12.admintool.fabric.ui.impl.item.builder.ItemBuilderPlayerData
import net.ap12.admintool.fabric.ui.paginator.UISimplePaginator
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.format
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.util.chunkAt
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Tool
import kotlin.jvm.optionals.getOrNull

class UIItemBuilderToolRules(dataKey: String) : AbstractUIItemBuilder(dataKey, "tool.rules") {

    private fun getRules(holder: AdminToolUIHolder): List<Tool.Rule> {
        val oldStack = requireNotNull(getData(holder).stack)
        val oldComponent = oldStack.get(DataComponents.TOOL)
        return oldComponent?.rules().orEmpty()
    }

    private fun modifyRules(old: Tool, modifier: (MutableList<Tool.Rule>) -> Unit = {}): Tool {
        val newRules = old.rules.toMutableList()
        modifier(newRules)

        return Tool(
            newRules,
            old.defaultMiningSpeed,
            old.damagePerBlock,
            old.canDestroyBlocksInCreative,
        )
    }

    private fun buildItem(holder: AdminToolUIHolder, rule: Tool.Rule, oldComponent: Tool) =
        item(Items.BOOK) {
            val index = getRules(holder).indexOf(rule)
            name("admintool.ui.item.builder.tool.rules.entry", (index + 1).toString())

            val blocksComponent = rule.blocks().format()

            lore {
                +field(
                    "admintool.ui.item.builder.tool.rules.entry.blocks",
                    blocksComponent.toComponent(),
                )
                +field(
                    "admintool.ui.item.builder.tool.rules.entry.speed",
                    rule.speed().getOrNull()?.toComponent()
                        ?: t(
                            "admintool.value.default.valued",
                            oldComponent.defaultMiningSpeed().toComponent(),
                        ),
                )
                +field(
                    "admintool.ui.item.builder.tool.rules.entry.correct_for_drops",
                    rule.correctForDrops().getOrNull()?.toComponent()
                        ?: t("admintool.value.default"),
                )
                +""
                +action("admintool.ui.item.builder.tool.rules.entry.edit", Keys.CLICK)
            }

            onClick("admintool.ui.item.builder.tool.rules.entry.${index}") { context ->
                context.go<Tool.Rule?>(UIItemBuilderToolRule(rule, index)) { result ->
                    if (result != null) {
                        val (beforeRules, afterRules) = oldComponent.rules().toList().chunkAt(index)
                        val oldStack = requireNotNull(getData(context.holder).stack)
                        val newStack = oldStack.copy()
                        val newRules = buildList {
                            addAll(beforeRules)
                            add(rule)
                            addAll(afterRules)
                        }

                        val newComponent =
                            Tool(
                                newRules,
                                oldComponent.defaultMiningSpeed,
                                oldComponent.damagePerBlock,
                                oldComponent.canDestroyBlocksInCreative,
                            )

                        newStack.set(DataComponents.TOOL, newComponent)
                        updateStack(context.holder, oldStack, newStack)
                    }
                }
            }
        }

    override fun createChild(
        holder: AdminToolUIHolder,
        data: ItemBuilderPlayerData,
    ): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val oldStack = requireNotNull(getData(holder).stack)
            val oldComponent = requireNotNull(oldStack.get(DataComponents.TOOL))
            val newStack = oldStack.copy()

            val itemBuilder = { holder: AdminToolUIHolder, rule: Tool.Rule ->
                buildItem(holder, rule, oldComponent)
            }

            UISimplePaginator(this@UIItemBuilderToolRules.id, 3, 5, ::getRules, itemBuilder)
                .create(holder)
                .merge()

            45 to
                item(Items.WRITABLE_BOOK) {
                    name("admintool.ui.item.builder.tool.rules.add")

                    onClick("admintool.ui.item.builder.tool.rules.add") { context ->
                        context.go<Tool.Rule?>(UIItemBuilderToolRule()) { result ->
                            if (result != null) {
                                newStack.set(
                                    DataComponents.TOOL,
                                    modifyRules(oldComponent) { it.add(result) },
                                )
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }
                }
        }
}
