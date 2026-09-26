package net.ap12.admintool.fabric.ui.impl.effect

import java.util.*
import kotlin.jvm.optionals.getOrDefault
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.paginator.UIPaginator
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.inventory.*
import net.ap12.admintool.fabric.util.symbolPrefixed
import net.kyori.adventure.key.Key
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.TooltipDisplay

class UIEffect(private val showAll: Boolean = false) : UIPaginator<Holder<MobEffect>>(), Tab {
    override val id: Key = AdminToolMod.key("effect")

    override fun getContent(holder: AdminToolUIHolder): List<Holder<MobEffect>> =
        BuiltInRegistries.MOB_EFFECT.asHolderIdMap().toList()

    override fun buildItem(holder: AdminToolUIHolder, element: Holder<MobEffect>): ItemBuilder {
        val state = holder.player.hasEffect(element)
        val defaultAmplifier =
            element
                .unwrapKey()
                .map {
                    holder.plugin.config.alwaysShownEffects.getOrDefault(
                        it.identifier().toString(),
                        1,
                    ) - 1
                }
                .getOrDefault(0)

        return item(Items.POTION) {
            val oldEffect = holder.player.getEffect(element)
            val effect =
                MobEffectInstance(
                    element,
                    MobEffectInstance.INFINITE_DURATION,
                    defaultAmplifier,
                    true,
                    false,
                    true,
                )

            name(element.value().descriptionId)

            stack.also { meta ->
                val color = effect.effect.value().color
                meta.set(
                    DataComponents.POTION_CONTENTS,
                    PotionContents(
                        Optional.empty(),
                        Optional.of(color),
                        emptyList(),
                        Optional.empty(),
                    ),
                )
                meta.set(
                    DataComponents.CUSTOM_NAME,
                    effect.effect
                        .value()
                        .displayName
                        .copy()
                        .withStyle(Style.EMPTY.withItalic(false)),
                )
                meta.set(
                    DataComponents.TOOLTIP_DISPLAY,
                    TooltipDisplay(false, linkedSetOf(DataComponents.POTION_CONTENTS)),
                )
            }

            val stateComp =
                if (state) {
                    t("admintool.ui.effect.active").symbolPrefixed(TextColor.GREEN)
                } else {
                    t("admintool.ui.effect.inactive").symbolPrefixed()
                }

            val leftActionKey =
                if (state) "admintool.ui.effect.increase" else "admintool.ui.effect.activate"

            lore {
                +stateComp
                if (oldEffect != null) {
                    +field("admintool.ui.effect.level", (oldEffect.amplifier + 1).toString())
                }
                +""
                +action(leftActionKey, Keys.MOUSE_LEFT)
                if (state) {
                    +action("admintool.ui.effect.decrease", Keys.MOUSE_RIGHT)
                    +action("admintool.ui.effect.deactivate", Keys.KEY_SHIFT, Keys.MOUSE_RIGHT)
                }
            }

            onClick("admintool.ui.effect.${element.unwrapKey().get().identifier().path}") { context
                ->
                if (context.isShift) {
                    context.player.removeEffect(element)
                } else if (context.isLeftClick) {
                    if (oldEffect == null) {
                        context.player.addEffect(effect)
                    } else {
                        val newEffect =
                            MobEffectInstance(
                                element,
                                oldEffect.duration,
                                oldEffect.amplifier + 1,
                                oldEffect.isAmbient,
                                oldEffect.isVisible,
                                oldEffect.showIcon(),
                            )
                        context.player.removeEffect(element)
                        context.player.addEffect(newEffect)
                    }
                } else if (context.isRightClick) {
                    if (oldEffect != null) {
                        val newEffect =
                            MobEffectInstance(
                                element,
                                oldEffect.duration,
                                oldEffect.amplifier - 1,
                                oldEffect.isAmbient,
                                oldEffect.isVisible,
                                oldEffect.showIcon(),
                            )
                        context.player.removeEffect(element)
                        if ((oldEffect.amplifier - 1) >= 0 && !context.isShift) {
                            context.player.addEffect(newEffect)
                        }
                    }
                }
                context.holder.update()
            }
        }
    }

    override fun create(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            super.create(holder).merge()

            36 to
                item(Items.MILK_BUCKET) {
                    name("admintool.ui.effect.clear")

                    onClick("admintool.ui.effect.clear") { context ->
                        context.playSound(Sounds.click3)
                        context.player.removeAllEffects()
                        context.holder.update()
                    }
                }

            44 to
                item(Items.NETHER_STAR) {
                    name(
                        if (showAll) t("admintool.ui.effect.show_part")
                        else t("admintool.ui.effect.show_all")
                    )

                    onClick("admintool.ui.effect.show_all") { context ->
                        context.playSound(Sounds.click2)
                        context.holder.changeTab(UIEffect(!showAll))
                    }
                }
        }
}
