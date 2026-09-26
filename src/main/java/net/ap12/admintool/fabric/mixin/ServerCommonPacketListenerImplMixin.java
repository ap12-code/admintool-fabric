package net.ap12.admintool.fabric.mixin;

import net.ap12.admintool.fabric.AdminToolMod;
import net.ap12.admintool.fabric.ui.AdminToolUIHolder;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
    private void admintool$handleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        ServerCommonPacketListenerImpl self = (ServerCommonPacketListenerImpl) (Object) this;
        if (self instanceof ServerGamePacketListenerImpl gamePacketListener) {
            ServerPlayer player = gamePacketListener.getPlayer();
            AdminToolUIHolder holder = AdminToolMod.Companion.getInstance().getUi().getHolder(player);

            if (holder != null) {
                holder.handleDialogAction(packet);
            }
        }
    }

}
