package com.qiuyue.goetyominus.common.network;

import com.qiuyue.goetyominus.common.init.ModTags;
import com.qiuyue.goetyominus.common.items.handler.FungusPackItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class OpenFungusPackPacket {

    public OpenFungusPackPacket() {}

    public static void encode(OpenFungusPackPacket msg, FriendlyByteBuf buf) {}

    public static OpenFungusPackPacket decode(FriendlyByteBuf buf) {
        return new OpenFungusPackPacket();
    }

    public static void handle(OpenFungusPackPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // 检查主手 / 副手
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.is(ModTags.FUNGUS_PACKS)) {
                    openGui(player, stack);
                    return;
                }
            }

            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.is(ModTags.FUNGUS_PACKS)) {
                openGui(player, chestStack);
                return;
            }

            CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(s -> s.is(ModTags.FUNGUS_PACKS)))
                    .orElse(java.util.Optional.empty())
                    .ifPresent(slot -> openGui(player, slot.stack()));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void openGui(ServerPlayer player, ItemStack stack) {
        net.minecraftforge.network.NetworkHooks.openScreen(
                player,
                new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new com.qiuyue.goetyominus.client.inventory.container.FungusPackContainer(
                                id, inv,
                                FungusPackItemHandler.get(stack),
                                stack),
                        stack.getHoverName()),
                buf -> {});
    }
}