package net.ap12.admintool.fabric.openinv

import net.ap12.admintool.fabric.util.Placeholder
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.world.item.Items

fun createOnlineItem() = item(Items.WOOL.lime) { name("admintool.ui.player.details.online") }

fun createOfflineItem() = item(Items.WOOL.red) { name("admintool.ui.player.details.offline") }

fun createEmptyItem() = item(Placeholder.placeholder()) {}
