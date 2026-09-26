package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.item.builder.enchantment.UIItemBuilderEnchant
import net.ap12.admintool.fabric.ui.impl.item.builder.tool.UIItemBuilderTool
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.ap12.admintool.util.isEmptyOrNull
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemLore
import kotlin.math.log2
import kotlin.math.pow

class UIItemBuilder(private val dataKey: String) : AbstractUIItemBuilder(dataKey, "") {

    override fun createChild(holder: AdminToolUIHolder, data: ItemBuilderPlayerData) =
        inventory(holder) {
            val newStack = data.stack?.copy() ?: ItemStack.EMPTY
            val oldStack = newStack.copy()

            if (!data.stack.isEmptyOrNull()) {
                var i = 17

                ++i to
                    item(Items.COMPASS) {
                        name("admintool.ui.item.builder.amount")
                        stack.set(DataComponents.MAX_STACK_SIZE, oldStack.maxStackSize)
                        stack.count = oldStack.count

                        lore {
                            +field(
                                "admintool.ui.item.builder.amount.current",
                                Component.literal(stack.count.toString()),
                            )
                            +""
                            +action("admintool.ui.item.builder.amount.edit", Keys.MOUSE_LEFT)
                            +action("admintool.ui.item.builder.amount.set_one", Keys.MOUSE_RIGHT)
                            +action("admintool.ui.item.builder.amount.set_max", Keys.MOUSE_MIDDLE)
                        }

                        onClick("admintool.ui.item.builder.amount") { context ->
                            if (context.isLeftClick) {
                                context.go<Int?>(
                                    UIItemBuilderAmount(
                                        "amount",
                                        oldStack.count,
                                        oldStack.maxStackSize,
                                    )
                                ) { result ->
                                    if (result != null) {
                                        newStack.count = result
                                    }
                                    updateStack(context.holder, oldStack, newStack)
                                }
                            } else if (context.isRightClick) {
                                newStack.count = 1
                            } else if (context.isMiddleClick) {
                                newStack.count = oldStack.maxStackSize
                            }
                            updateStack(context.holder, oldStack, newStack)
                        }
                    }

                val defaultMaxStackSize = oldStack.item.defaultMaxStackSize
                ++i to
                    item(Items.COMPASS) {
                        name("admintool.ui.item.builder.max_stack_size")
                        stack.set(DataComponents.MAX_STACK_SIZE, oldStack.maxStackSize)
                        stack.count = oldStack.maxStackSize
                        glint(true)

                        lore {
                            +field(
                                "admintool.ui.item.builder.max_stack_size.current",
                                Component.literal("${oldStack.maxStackSize}").let { component ->
                                    if (oldStack.maxStackSize == defaultMaxStackSize) {
                                        return@let component
                                            .append(" ")
                                            .append(
                                                t(
                                                    "admintool.ui.item.builder.max_stack_size.default"
                                                )
                                            )
                                    }
                                    component
                                },
                            )
                            +""
                            +action(
                                "admintool.ui.item.builder.max_stack_size.edit",
                                Keys.MOUSE_LEFT,
                            )
                            +action(
                                "admintool.ui.item.builder.max_stack_size.reset",
                                Keys.MOUSE_RIGHT,
                            )
                        }

                        onClick("admintool.ui.item.builder.max_stack_size") { context ->
                            if (context.isLeftClick) {
                                context.go<Int?>(
                                    UIItemBuilderAmount("max_stack_size", oldStack.maxStackSize, 99)
                                ) { result ->
                                    if (result != null) {
                                        newStack.set(DataComponents.MAX_STACK_SIZE, result)
                                        newStack.count = newStack.count.coerceAtMost(result)
                                        updateStack(context.holder, oldStack, newStack)
                                    }
                                }
                            } else if (context.isRightClick) {
                                newStack.set(DataComponents.MAX_STACK_SIZE, defaultMaxStackSize)
                                newStack.count = newStack.count.coerceAtMost(defaultMaxStackSize)
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.NAME_TAG) {
                        name("admintool.ui.item.builder.name")

                        lore {
                            if (newStack.hasNonDefault(DataComponents.ITEM_NAME)) {
                                +newStack.itemName.symbolPrefixed(TextColor.GRAY)
                            } else {
                                +t("admintool.ui.item.builder.name.empty")
                                    .symbolPrefixed(TextColor.GRAY)
                            }
                            +""
                            +action("admintool.ui.item.builder.name.edit", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.name") { context ->
                            context.go<Component?>(
                                UIItemBuilderName(
                                    "",
                                    newStack.getOrDefault(
                                        DataComponents.ITEM_NAME,
                                        Component.empty(),
                                    ),
                                )
                            ) {
                                if (it != null) {
                                    newStack.set(DataComponents.ITEM_NAME, it)
                                } else {
                                    newStack.remove(DataComponents.ITEM_NAME)
                                }
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.NAME_TAG) {
                        name("admintool.ui.item.builder.custom_name")
                        glint(true)

                        lore {
                            +newStack
                                .getOrDefault(
                                    DataComponents.CUSTOM_NAME,
                                    t("admintool.ui.item.builder.custom_name.empty"),
                                )
                                .symbolPrefixed(TextColor.GRAY)
                            +""
                            +action("admintool.ui.item.builder.custom_name.edit", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.custom_name") { context ->
                            context.go<Component?>(
                                UIItemBuilderName(
                                    "custom_name",
                                    newStack.getOrDefault(
                                        DataComponents.CUSTOM_NAME,
                                        Component.empty(),
                                    ),
                                )
                            ) {
                                if (it != null) {
                                    newStack.set(DataComponents.CUSTOM_NAME, it)
                                } else {
                                    newStack.remove(DataComponents.CUSTOM_NAME)
                                }
                                updateStack(context.holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.SUNFLOWER) {
                        name("admintool.ui.item.builder.lore")

                        lore {
                            if (newStack.hasNonDefault(DataComponents.LORE)) {
                                +t("admintool.ui.item.builder.lore.current").symbolPrefixed()
                                newStack
                                    .getOrDefault(DataComponents.LORE, ItemLore.EMPTY)
                                    .lines()
                                    .forEach { +it }
                            } else {
                                +t("admintool.ui.item.builder.lore.empty")
                                    .symbolPrefixed(TextColor.GRAY)
                            }
                            +""
                            +action("admintool.ui.item.builder.lore.edit", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.lore") { context ->
                            val oldLore =
                                if (newStack.hasNonDefault(DataComponents.LORE))
                                    newStack
                                        .getOrDefault(DataComponents.LORE, ItemLore.EMPTY)
                                        .lines()
                                else emptyList()

                            context.go<List<Component>?>(UIItemBuilderLore(oldLore)) {
                                if (it != null) {
                                    if (
                                        it.isEmpty() ||
                                            it.all { component -> component.string.isEmpty() }
                                    ) {
                                        newStack.remove(DataComponents.LORE)
                                    } else {
                                        newStack.set(DataComponents.LORE, ItemLore(emptyList(), it))
                                    }
                                }
                                updateStack(holder, oldStack, newStack)
                            }
                        }
                    }

                ++i to
                    item(Items.NETHER_STAR) {
                        name("admintool.ui.item.builder.rarity")
                        val defaultRarity =
                            oldStack.item.components().get(DataComponents.RARITY) ?: Rarity.COMMON
                        val currentRarity =
                            oldStack.getOrDefault(DataComponents.RARITY, defaultRarity)

                        lore {
                            Rarity.entries.forEach {
                                +t("admintool.ui.item.builder.rarity.${it.name.lowercase()}")
                                    .let { component ->
                                        if (it == defaultRarity) {
                                            return@let component
                                                .append(" ")
                                                .append(
                                                    t("admintool.ui.item.builder.rarity.default")
                                                        .withColor(TextColor.GRAY)
                                                )
                                        }
                                        component
                                    }
                                    .symbolPrefixed(
                                        if (currentRarity == it) TextColor.YELLOW
                                        else TextColor.GRAY,
                                        true,
                                    )
                            }
                            +""
                            +action("admintool.ui.item.builder.rarity.switch", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.rarity") { context ->
                            val nextRarity =
                                when (currentRarity) {
                                    Rarity.COMMON -> Rarity.UNCOMMON
                                    Rarity.UNCOMMON -> Rarity.RARE
                                    Rarity.RARE -> Rarity.EPIC
                                    Rarity.EPIC -> null
                                }

                            newStack.set(DataComponents.RARITY, nextRarity)
                            updateStack(context.holder, oldStack, newStack)
                        }
                    }

                ++i to
                    item(Items.ENCHANTED_BOOK) {
                        name("admintool.ui.item.builder.enchant")

                        lore {
                            newStack.enchantments.entrySet().forEach { (enchantment, level) ->
                                +t(enchantment.value().description.string)
                                    .append(" ")
                                    .append(Component.literal(level.toString()))
                            }
                            +""
                            +action("admintool.ui.item.builder.enchant.edit", Keys.MOUSE_LEFT)
                        }

                        onClick("admintool.ui.item.builder.enchant") { context ->
                            context.go<Nothing>(UIItemBuilderEnchant(dataKey))
                        }
                    }

                val isDamageable = oldStack.has(DataComponents.DAMAGE)
                val isRepairable = oldStack.has(DataComponents.REPAIRABLE)

                val damage = oldStack.getOrDefault(DataComponents.DAMAGE, 0)
                val maxDamage = oldStack.getOrDefault(DataComponents.MAX_DAMAGE, 0)
                val defaultMaxDamage = oldStack.item.components().get(DataComponents.MAX_DAMAGE)
                val oldRepairCost = oldStack.getOrDefault(DataComponents.REPAIR_COST, 0)
                val anvilUseCount = log2(oldRepairCost + 1.0).toInt()
                val newRepairCost = (2.0.pow(anvilUseCount + 1) - 1).toInt()

                ++i to
                    item(Items.ANVIL) {
                        name("admintool.ui.item.builder.durability")

                        lore {
                            if (isRepairable || isDamageable) {
                                +field(
                                    "admintool.ui.item.builder.durability.current",
                                    "${maxDamage - damage}/$maxDamage",
                                )
                                +field(
                                    t(
                                        "admintool.ui.item.builder.durability.cost",
                                        "$oldRepairCost",
                                        "$newRepairCost",
                                        "$anvilUseCount",
                                    )
                                )
                            } else {
                                +(t("admintool.ui.item.builder.durability.unavailable")
                                    .symbolPrefixed(TextColor.RED))
                            }
                            +""
                            +action("admintool.ui.item.builder.durability.edit", Keys.MOUSE_LEFT)
                            +action("admintool.ui.item.builder.durability.repair", Keys.MOUSE_RIGHT)
                            +action("admintool.ui.item.builder.durability.reset", Keys.MOUSE_MIDDLE)
                            +action(
                                "admintool.ui.item.builder.durability.edit.max",
                                Keys.KEY_SHIFT,
                                Keys.MOUSE_LEFT,
                            )
                            +action(
                                "admintool.ui.item.builder.durability.reset.cost",
                                Keys.KEY_SHIFT,
                                Keys.MOUSE_RIGHT,
                            )
                        }

                        onClick("admintool.ui.item.builder.durability") { context ->
                            if (context.isLeftClick) {
                                if (!context.isShift) {
                                    context.go<Int?>(
                                        UIItemBuilderAmount(
                                            "durability.edit",
                                            (maxDamage - damage),
                                            maxDamage,
                                            0,
                                        )
                                    ) { result ->
                                        if (result != null) {
                                            if (result <= 0) {
                                                newStack.set(
                                                    DataComponents.MAX_DAMAGE,
                                                    newStack.item
                                                        .components()
                                                        .get(DataComponents.MAX_DAMAGE),
                                                )
                                                newStack.set(DataComponents.DAMAGE, 0)
                                            } else {
                                                newStack.set(
                                                    DataComponents.DAMAGE,
                                                    maxDamage - result,
                                                )
                                            }
                                            updateStack(context.holder, oldStack, newStack)
                                        }
                                    }
                                } else {
                                    context.go<Int?>(
                                        UIItemBuilderAmount("durability.edit.max", maxDamage, 99999)
                                    ) { result ->
                                        if (result != null) {
                                            newStack.set(
                                                DataComponents.DAMAGE,
                                                newStack
                                                    .getOrDefault(DataComponents.DAMAGE, 0)
                                                    .coerceAtMost(result),
                                            )
                                            newStack.set(DataComponents.MAX_DAMAGE, result)
                                            updateStack(context.holder, oldStack, newStack)
                                        }
                                    }
                                }
                                return@onClick
                            } else if (context.isRightClick) {
                                if (!context.isShift) {
                                    newStack.set(DataComponents.DAMAGE, 0)
                                } else {
                                    newStack.set(DataComponents.REPAIR_COST, 0)
                                }
                            } else if (context.isMiddleClick) {
                                newStack.set(DataComponents.MAX_DAMAGE, defaultMaxDamage)
                                newStack.set(DataComponents.DAMAGE, 0)
                            }
                            updateStack(context.holder, oldStack, newStack)
                        }
                    }

                ++i to
                    item(Items.DIAMOND_SHOVEL) {
                        name("admintool.ui.item.builder.tool")

                        val oldComponent = oldStack.get(DataComponents.TOOL)
                        lore {
                            if (oldComponent != null) {
                                +field("admintool.ui.item.builder.tool.state.enabled")
                                +""
                                +field(
                                    "admintool.ui.item.builder.tool.default_mining_speed",
                                    "%.2f".format(oldComponent.defaultMiningSpeed()),
                                )
                                +field(
                                    "admintool.ui.item.builder.tool.damage_per_block",
                                    "%d".format(oldComponent.damagePerBlock()),
                                )
                                +field(
                                    "admintool.ui.item.builder.tool.can_destroy_blocks_in_creative",
                                    "%b".format(oldComponent.canDestroyBlocksInCreative()),
                                )
                                +field(
                                    "admintool.ui.item.builder.tool.rules",
                                    t(
                                        "admintool.ui.item.builder.tool.rules.entries",
                                        "%d".format(oldComponent.rules().size),
                                    ),
                                )
                            } else {
                                +field("admintool.ui.item.builder.tool.state.disabled")
                            }
                            +""
                            +action("admintool.ui.item.builder.tool.edit", Keys.CLICK)
                        }

                        onClick("admintool.ui.item.builder.tool") { context ->
                            context.go<Nothing>(UIItemBuilderTool(dataKey))
                        }
                    }
            }
        }
}
