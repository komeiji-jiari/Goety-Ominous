package com.qiuyue.goetyominous.common.network;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.qiuyue.goetyominous.common.items.curios.CroneRobeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class CExtractPotionPacket {

    public CExtractPotionPacket() {}

    public static void encode(CExtractPotionPacket msg, FriendlyByteBuf buf) {}

    public static CExtractPotionPacket decode(FriendlyByteBuf buf) {
        return new CExtractPotionPacket();
    }

    public static void handle(CExtractPotionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(s -> s.getItem() instanceof CroneRobeItem))
                    .orElse(java.util.Optional.empty())
                    .ifPresent(slot -> {
                        ItemStack stack = slot.stack();
                        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(CroneRobeItem.INVENTORY)) {
                            WitchRobeInventory inventory = ModSaveInventory.getInstance()
                                    .getWitchRobeInventory(stack.getTag().getInt(CroneRobeItem.INVENTORY), player);
                            inventory.extractPotions();
                        }
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}
