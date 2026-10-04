package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CroneRobeClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && CroneCuriosUtil.hasCroneSet(player) && player.tickCount % 5 == 0) {
            player.level().addParticle(ParticleTypes.WITCH,
                    player.getX(), player.getY() + player.getBbHeight() + 0.3D, player.getZ(),
                    0.0D, 0.02D, 0.0D);
        }
    }
}
