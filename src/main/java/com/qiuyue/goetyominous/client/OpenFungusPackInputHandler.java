package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.OpenFungusPackPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class OpenFungusPackInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ModKeyBindings.OPEN_FUNGUS_PACK_KEY.consumeClick()) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null) return;

            if (player.getMainHandItem().is(ModTags.FUNGUS_PACKS)
                    || player.getOffhandItem().is(ModTags.FUNGUS_PACKS)) {
                ModNetwork.CHANNEL.sendToServer(new OpenFungusPackPacket());
                return;
            }

            if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModTags.FUNGUS_PACKS)) {
                ModNetwork.CHANNEL.sendToServer(new OpenFungusPackPacket());
                return;
            }

            CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(s -> s.is(ModTags.FUNGUS_PACKS)))
                    .orElse(java.util.Optional.empty())
                    .ifPresent(slot -> ModNetwork.CHANNEL.sendToServer(new OpenFungusPackPacket()));
        }
    }
}