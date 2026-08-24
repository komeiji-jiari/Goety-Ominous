/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.network.client.focus.CAddFocusToInventoryPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.network.client.focus.CAddFocusToInventoryPacket;
import com.vivideru.masteryofmagic.MasterStaffFocusTransfer;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CAddFocusToInventoryPacket.class})
public class CAddFocusToInventoryPacketMixin {
    @Inject(method={"consume"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void goetyMasteryOfMagic$transferFromMasterStaff(CAddFocusToInventoryPacket packet, Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player == null || !MasterStaffFocusTransfer.hasActiveMasterStaff((Player)player)) {
            return;
        }
        context.enqueueWork(() -> MasterStaffFocusTransfer.addFocusToInventory(player));
        context.setPacketHandled(true);
        ci.cancel();
    }
}

