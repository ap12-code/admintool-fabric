package net.ap12.admintool.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.ap12.admintool.fabric.ui.AdminToolUIHolder;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"), cancellable = true)
    private void admintool$handleContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = self.player;
        if (player.containerMenu instanceof AdminToolUIHolder holder) {
            player.containerMenu.suppressRemoteUpdates();
            boolean needsSync = packet.stateId() != player.containerMenu.getStateId();

            for (var entry : Int2ObjectMaps.fastIterable(packet.changedSlots())) {
                player.containerMenu.setRemoteSlotUnsafe(entry.getIntKey(), entry.getValue());
            }
            player.containerMenu.setRemoteCarried(packet.carriedItem());

            boolean allow = holder.click(packet.slotNum(), packet.buttonNum(), packet.containerInput(), player);

            player.containerMenu.resumeRemoteUpdates();
            if (allow) {
                if (needsSync) {
                    player.containerMenu.broadcastFullState();
                } else {
                    player.containerMenu.broadcastChanges();
                }
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleContainerClose", at = @At("HEAD"))
    private void admintool$handleContainerClose(ServerboundContainerClosePacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = self.player;
        if (player.containerMenu instanceof AdminToolUIHolder holder) {
            holder.onClose();
        }
    }
}
