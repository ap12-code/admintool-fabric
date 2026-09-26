package net.ap12.admintool.fabric.ui.impl.item.builder.enchantment

import net.ap12.admintool.fabric.i18n.t
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items

fun getEnchantableSameAs(enchantable: Int): Component? =
    when (enchantable) {
        1 -> t(Items.BOOK.descriptionId)
        5 -> t("admintool.tool_level.stone")
        8 -> t("admintool.tool_level.copper_armor")
        9 -> t("admintool.tool_level.iron_armor")
        10 -> t("admintool.tool_level.diamond")
        12 -> t("admintool.tool_level.chainmail")
        13 -> t("admintool.tool_level.copper")
        14 -> t("admintool.tool_level.iron")
        15 -> t("admintool.tool_level.netherite")
        22 -> t("admintool.tool_level.gold")
        25 -> t("admintool.tool_level.gold_armor")
        else -> null
    }
