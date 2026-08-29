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

public class CCroneRobePacket {

    public CCroneRobePacket() {}

    public static void encode(CCroneRobePacket msg, FriendlyByteBuf buf) {}

    public static CCroneRobePacket decode(FriendlyByteBuf buf) {
        return new CCroneRobePacket();
    }

    public static void handle(CCroneRobePacket msg, Supplier<NetworkEvent.Context> ctx) {
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
                            player.openMenu(inventory);
                        }
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}
