package net.ap12.admintool.fabric.util.inventory

import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.Callback
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.TooltipDisplay
import kotlin.jvm.optionals.getOrNull

typealias ItemAction = (ItemClickContext) -> Unit

@Suppress("unused")
class ItemBuilder(val stack: ItemStack) {
    private var name: Component? = null
    private val patch = DataComponentPatch.builder()

    fun name(name: Component) {
        this.name = name
    }

    fun name(name: String, vararg args: String) {
        this.name = Component.translatable(name, *args)
    }

    private var lore: List<Component>? = null

    fun lore(builder: LoreBuilder.() -> Unit) {
        lore = LoreBuilder().apply(builder).build()
    }

    fun lore(vararg lines: Component) {
        lore = lines.toList()
    }

    fun List<Component>.lore() {
        lore = this
    }

    operator fun LoreBuilder.invoke() {
        lore = this.build()
    }

    var onClick: Callback<ItemClickContext>? = null
    var actionName: String = ""

    fun onClick(name: String, action: Callback<ItemClickContext>? = null) {
        actionName = name
        onClick = action
    }

    private var shouldCancel = true

    fun shouldCancel(value: Boolean) {
        shouldCancel = value
    }

    private var hasEnchantGlint: Boolean? = null

    fun glint(value: Boolean = true) {
        hasEnchantGlint = value
    }

    fun <T : Any> set(type: DataComponentType<T>, value: T) {
        patch.set(type, value)
    }

    fun toItemStack(holder: AdminToolUIHolder): ItemStack {
        stack.also {
            if (name != null) {
                it.set(DataComponents.RARITY, Rarity.COMMON)
                val baseTooltip =
                    it.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT)
                val tooltip =
                    TooltipDisplay(baseTooltip.hideTooltip, baseTooltip.hiddenComponents())

                it.set(DataComponents.TOOLTIP_DISPLAY, tooltip)
                it.set(DataComponents.ITEM_NAME, name!!.translate(holder.player))
                if (stack.`is`(Items.PLAYER_HEAD)) {
                    it.set(
                        DataComponents.CUSTOM_NAME,
                        name!!
                            .translate(holder.player)
                            .copy()
                            .withStyle(Style.EMPTY.withItalic(false)),
                    )
                }
            }
            if (lore != null) {
                val resolvedLore =
                    lore!!.map { component -> component.translate(holder.player) }.toList()
                it.set(
                    DataComponents.LORE,
                    ItemLore(
                        resolvedLore.map { component ->
                            ComponentUtils.mergeStyles(
                                component,
                                Style.EMPTY.withColor(TextColor.WHITE).withItalic(false),
                            )
                        }
                    ),
                )
            }
            if (hasEnchantGlint != null)
                it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, hasEnchantGlint)
        }
        val data = CompoundTag()
        data.putBoolean("cancel", shouldCancel)
        if (actionName != "") {
            data.putString("action", actionName)
            if (onClick != null) {
                holder.setAction(actionName, onClick!!)
            }
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data))
        stack.applyComponents(patch.build())
        return stack.copy()
    }
}

fun item(material: Item, block: ItemBuilder.() -> Unit): ItemBuilder =
    ItemBuilder(ItemStack(material)).apply(block)

fun item(stack: ItemStack, block: ItemBuilder.() -> Unit): ItemBuilder =
    ItemBuilder(stack).apply(block)

fun ItemStack.removeActionData(): ItemStack {
    this.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update {
        it.remove("cancel")
        it.remove("action")
    }
    return this
}

fun ItemStack.getAction(): String? {
    return this.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        .copyTag()
        .getString("action")
        .getOrNull()
}

fun ItemStack.getCancel(): Boolean? {
    return this.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        .copyTag()
        .getBoolean("cancel")
        .getOrNull()
}
