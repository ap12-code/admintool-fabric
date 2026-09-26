package net.ap12.admintool.ui.impl.item.builder.enchantment

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.kyori.adventure.key.Key
import java.util.*

class UIItemBuilderEnchantRandom : UI {
    override val id: Key = AdminToolMod.key("item.builder.enchant.random")
    private val randomSeed = Random().nextLong()

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val costKey = holder.createStringKey()
            val allowTreasureKey = holder.createStringKey()
            val seedKey = holder.createStringKey()

            base {
                numberRange(costKey) {
                    label("admintool.ui.item.builder.enchant.random.cost")
                    range(1..30)
                    initial(30f)
                    step(1f)
                }
                bool(allowTreasureKey) {
                    label("admintool.ui.item.builder.enchant.random.allow_treasure")
                }
                textInput(seedKey) {
                    label("admintool.ui.item.builder.enchant.random.seed")
                    initial(randomSeed.toString())
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.item.builder.enchant.random.confirm")
                        action(holder.createStringKey()) { context ->
                            val seedStr = context.getStringOrNull(seedKey)
                            var seedResult = randomSeed
                            if (!seedStr.isNullOrBlank()) {
                                seedResult =
                                    if (seedStr.toLongOrNull() != null) {
                                        seedStr.toLong()
                                    } else {
                                        seedStr.hashCode().toLong()
                                    }
                            }

                            context.holder.back(
                                Result(
                                    context.getIntOrNull(costKey) ?: 1,
                                    context.getBooleanOrNull(allowTreasureKey) ?: false,
                                    seedResult,
                                )
                            )
                        }
                    }
                    no {
                        label("admintool.ui.item.builder.enchant.random.cancel")
                        action(holder.createStringKey()) { context -> context.holder.back(null) }
                    }
                }
            }
        }

    class Result(val cost: Int, val allowTreasure: Boolean, val seed: Long)
}
