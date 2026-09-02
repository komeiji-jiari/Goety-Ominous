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

public class CCroneRobePacket {

    public CCroneRobePacket() {}

    public static void encode(CCroneRobePacket msg, FriendlyByteBuf buf) {}

    public static CCroneRobePacket decode(FriendlyByteBuf buf) {
        return new CCroneRobePacket();
    }

    public static void handle(CCroneRobePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity,
                        (itemStack) -> itemStack.getItem() instanceof CroneRobeItem);
                if (stack != null && !stack.isEmpty()) {
                    WitchRobeInventory inventory = ModSaveInventory.getInstance()
                            .getWitchRobeInventory(stack.getTag().getInt(CroneRobeItem.INVENTORY), playerEntity);
                    playerEntity.openMenu(inventory);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
