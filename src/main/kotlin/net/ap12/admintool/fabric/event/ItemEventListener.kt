package net.ap12.admintool.fabric.event

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.util.hasPermission
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level

class ItemEventListener(private val plugin: AdminToolMod) : UseItemCallback {
    override fun interact(player: Player, level: Level, hand: InteractionHand): InteractionResult {
        if (!player.hasPermission("admintool.use", true)) return InteractionResult.PASS
        if (
            !player
                .getItemInHand(hand)
                .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getBooleanOr("admintool", false)
        ) {
            return InteractionResult.PASS
        }

        plugin.slogger.info("Open: {}", player.plainTextName)
        plugin.ui.open(player as ServerPlayer)
        return InteractionResult.SUCCESS
    }
}
