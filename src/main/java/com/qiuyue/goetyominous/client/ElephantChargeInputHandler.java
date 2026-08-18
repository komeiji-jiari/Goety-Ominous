package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import com.qiuyue.goetyominous.common.network.ElephantChargePacket;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class ElephantChargeInputHandler {
    @SubscribeEvent
    public static void onInteractionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (event.getKeyMapping() != mc.options.keyUse || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (player.isShiftKeyDown()) {
            return;
        }
        if (!player.getMainHandItem().isEmpty()) {
            return;
        }
        // 未安装 Alex's Mobs 时大象仆从实体不存在,直接禁用充能(避免加载 Alex's Mobs 类导致 NoClassDefFoundError)
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }
        Entity entity = player.getVehicle();
        if (!(entity instanceof IllagerElephantServant elephant)) {
            return;
        }
        ModNetwork.CHANNEL.sendToServer(new ElephantChargePacket(elephant.getId()));
        elephant.triggerCharge();
        event.setSwingHand(true);
        event.setCanceled(true);
    }
}
