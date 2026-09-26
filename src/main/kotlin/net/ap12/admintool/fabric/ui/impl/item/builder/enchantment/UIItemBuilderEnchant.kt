package net.ap12.admintool.fabric.ui.impl.item.builder.enchantment

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.item.builder.AbstractUIItemBuilder
import net.ap12.admintool.fabric.ui.impl.item.builder.ItemBuilderPlayerData
import net.ap12.admintool.fabric.ui.paginator.UISimplePaginator
import net.ap12.admintool.fabric.util.inventory.*
import net.ap12.admintool.ui.impl.item.builder.enchantment.UIItemBuilderEnchantRandom
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.util.RandomSource
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantable
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments

class UIItemBuilderEnchant(dataKey: String) : AbstractUIItemBuilder(dataKey, "enchant") {
    private fun buildItem(holder: AdminToolUIHolder, element: Holder<Enchantment>): ItemBuilder =
        item(Items.ENCHANTED_BOOK) {
            name(element.value().description)
            val oldStack = getData(holder).stack!!
            val newStack = oldStack.copy()

            lore {
                +""
                if (oldStack.enchantments.keySet().contains(element)) {
                    +field(
                        "admintool.ui.item.builder.enchant.level",
                        Component.literal(oldStack.enchantments.getLevel(element).toString()),
                    )
                    +""
                    +action("admintool.ui.item.builder.enchant.level.edit", Keys.MOUSE_LEFT)
                    +action("admintool.ui.item.builder.enchant.remove", Keys.MOUSE_RIGHT)
                } else {
                    +action("admintool.ui.item.builder.enchant.add", Keys.MOUSE_LEFT)
                }
            }

            onClick("admintool.ui.item.builder.enchant.${element.value().description}") { context ->
                if (context.isLeftClick || !oldStack.enchantments.keySet().contains(element)) {
                    context.go<Int?>(
                        UIItemBuilderEnchantLevel(element, oldStack.enchantments.getLevel(element))
                    ) { result ->
                        if (result != null) {
                            val map = ItemEnchantments.Mutable(oldStack.enchantments)
                            map.set(element, result)
                            newStack.set(DataComponents.ENCHANTMENTS, map.toImmutable())
                            updateStack(context.holder, oldStack, newStack)
                        }
                    }
                } else if (context.isRightClick) {
                    val map = ItemEnchantments.Mutable(oldStack.enchantments)
                    map.removeIf { it == element }
                    updateStack(context.holder, oldStack, newStack)
                }
            }
        }

    private fun getContent(holder: AdminToolUIHolder): List<Holder<Enchantment>> {
        val oldStack = getData(holder).stack!!
        val registry = holder.access.lookupOrThrow(Registries.ENCHANTMENT)
        return registry
            .map { registry.wrapAsHolder(it) }
            .sortedBy { oldStack.enchantments.keySet().contains(it) }
    }

    override fun createChild(
        holder: AdminToolUIHolder,
        data: ItemBuilderPlayerData,
    ): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val oldStack = data.stack!!
            val newStack = oldStack.copy()

            UISimplePaginator(this@UIItemBuilderEnchant.key(), 3, 5, ::getContent, ::buildItem, 2)
                .create(holder)
                .merge()

            9..17 to Items.STAINED_GLASS_PANE.black

            45 to
                item(Items.BRUSH) {
                    name("admintool.ui.item.builder.enchant.clear")

                    onClick("admintool.ui.item.builder.enchant.clear") { context ->
                        newStack.remove(DataComponents.ENCHANTMENTS)
                        updateStack(context.holder, oldStack, newStack)
                    }
                }

            46 to
                item(Items.ENCHANTING_TABLE) {
                    name("admintool.ui.item.builder.enchant.random")

                    lore {
                        +""
                        +t("admintool.ui.item.builder.enchant.random.description")
                    }

                    onClick("admintool.ui.item.builder.enchant.random") { context ->
                        context.go<UIItemBuilderEnchantRandom.Result?>(
                            UIItemBuilderEnchantRandom()
                        ) { result ->
                            if (result != null) {
                                val registry =
                                    holder.access.lookup(Registries.ENCHANTMENT).orElseThrow()
                                newStack.remove(DataComponents.ENCHANTMENTS)
                                val resultStack =
                                    EnchantmentHelper.enchantItem(
                                        RandomSource.create(result.seed),
                                        newStack,
                                        result.cost,
                                        registry.stream().map(registry::wrapAsHolder),
                                    )
                                updateStack(context.holder, oldStack, resultStack)
                            }
                        }
                    }
                }

            val defaultEnchantable =
                oldStack.item.components().get(DataComponents.ENCHANTABLE)?.value

            val oldEnchantable =
                oldStack.get(DataComponents.ENCHANTABLE)?.value ?: defaultEnchantable

            47 to
                item(Items.LECTERN) {
                    name("admintool.ui.item.builder.enchant.enchantable")

                    lore {
                        if (oldEnchantable != null) {
                            +field(
                                "admintool.ui.item.builder.enchant.enchantable.current",
                                Component.literal(oldEnchantable.toString()).let { component ->
                                    if (oldEnchantable == defaultEnchantable) {
                                        return@let component
                                            .append(" ")
                                            .append(
                                                t(
                                                        "admintool.ui.item.builder.enchant.enchantable.default"
                                                    )
                                                    .withColor(TextColor.GRAY)
                                            )
                                    }
                                    component
                                },
                            )
                        } else {
                            +field("admintool.ui.item.builder.enchant.enchantable.empty")
                        }
                        if (oldEnchantable != null) {
                            val sameAs = getEnchantableSameAs(oldEnchantable)
                            if (sameAs != null && oldEnchantable != defaultEnchantable) {
                                +Component.literal("   ")
                                    .append(
                                        t(
                                            "admintool.ui.item.builder.enchant.enchantable.same",
                                            sameAs,
                                        )
                                    )
                            }
                        }
                        +""
                        +t("admintool.ui.item.builder.enchant.enchantable.description")
                        +""
                        if (oldEnchantable != null) {
                            +action(
                                "admintool.ui.item.builder.enchant.enchantable.increase",
                                Keys.MOUSE_LEFT,
                            )
                            +action(
                                "admintool.ui.item.builder.enchant.enchantable.decrease",
                                Keys.MOUSE_RIGHT,
                            )
                            +action(
                                "admintool.ui.item.builder.enchant.enchantable.reset",
                                Keys.KEY_SHIFT,
                                Keys.MOUSE_RIGHT,
                            )
                        } else {
                            +action(
                                "admintool.ui.item.builder.enchant.enchantable.activate",
                                Keys.MOUSE_LEFT,
                            )
                        }
                    }

                    onClick("admintool.ui.item.builder.enchant.enchantable") { context ->
                        if (oldEnchantable != null) {
                            if (!context.isShift) {
                                if (context.isLeftClick) {
                                    newStack.set(
                                        DataComponents.ENCHANTABLE,
                                        Enchantable(oldEnchantable + 1),
                                    )
                                } else if (context.isRightClick) {
                                    if (oldEnchantable > 1) {
                                        newStack.set(
                                            DataComponents.ENCHANTABLE,
                                            Enchantable(oldEnchantable - 1),
                                        )
                                    } else {
                                        newStack.remove(DataComponents.ENCHANTABLE)
                                    }
                                }
                            } else {
                                newStack.remove(DataComponents.ENCHANTABLE)
                            }
                        } else {
                            newStack.set(DataComponents.ENCHANTABLE, Enchantable(1))
                        }
                        updateStack(context.holder, oldStack, newStack)
                    }
                }

            val oldGlintOverride = oldStack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)
            48 to
                item(Items.BOOKSHELF) {
                    name("admintool.ui.item.builder.enchant.glint_override")

                    glint(oldGlintOverride ?: false)
                    lore {
                        +field(
                            "admintool.ui.item.builder.enchant.glint_override.default",
                            oldGlintOverride == null,
                        )
                        +field(
                            "admintool.ui.item.builder.enchant.glint_override.on",
                            oldGlintOverride == true,
                        )
                        +field(
                            "admintool.ui.item.builder.enchant.glint_override.off",
                            oldGlintOverride == false,
                        )
                        +""
                        +t("admintool.ui.item.builder.enchant.glint_override.description")
                    }

                    onClick("admintool.ui.item.builder.enchant.glint_override") { context ->
                        val nextState =
                            when (oldGlintOverride) {
                                null -> true
                                true -> false
                                false -> null
                            }

                        if (nextState != null) {
                            newStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, nextState)
                        } else {
                            newStack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)
                        }
                        updateStack(context.holder, oldStack, newStack)
                    }
                }
        }
}
