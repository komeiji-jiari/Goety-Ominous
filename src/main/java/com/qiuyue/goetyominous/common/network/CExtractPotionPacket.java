package com.qiuyue.goetyominous.common.network;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.qiuyue.goetyominous.common.items.curios.CroneRobeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CExtractPotionPacket {

    public CExtractPotionPacket() {}

    public static void encode(CExtractPotionPacket msg, FriendlyByteBuf buf) {}

    public static CExtractPotionPacket decode(FriendlyByteBuf buf) {
        return new CExtractPotionPacket();
    }

    public static void handle(CExtractPotionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity,
                        (itemStack) -> itemStack.getItem() instanceof CroneRobeItem);
                if (stack != null && !stack.isEmpty()) {
                    WitchRobeInventory inventory = ModSaveInventory.getInstance()
                            .getWitchRobeInventory(stack.getTag().getInt(CroneRobeItem.INVENTORY), playerEntity);
                    inventory.extractPotions();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
