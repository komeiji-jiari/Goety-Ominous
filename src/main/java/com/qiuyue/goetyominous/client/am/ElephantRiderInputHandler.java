package com.qiuyue.goetyominous.client.am;

import com.qiuyue.goetyominous.client.ModKeyBindings;
import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.am.ElephantChargePacket;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class ElephantRiderInputHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.xxa == 0.0f) {
            return;
        }
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }
        if (player.getVehicle() instanceof IllagerElephantServant) {
            player.setYRot(player.getYRot() + player.xxa * IllagerElephantServant.RIDER_TURN_SPEED);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }
        if (!(player.getVehicle() instanceof IllagerElephantServant elephant)) {
            return;
        }
        if (ModKeyBindings.RIDER_CHARGE_KEY.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new ElephantChargePacket(elephant.getId()));
            elephant.triggerCharge();
        }
    }

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
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }
        Entity entity = player.getVehicle();
        if (!(entity instanceof IllagerElephantServant)) {
            return;
        }
        event.setCanceled(true);
    }
}
