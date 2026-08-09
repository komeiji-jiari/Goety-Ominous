package com.qiuyue.goetyominous.client.events;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ArchGeomancerEntity;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModBossMusicHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player == null || !player.level().isClientSide) return;

        for (ArchGeomancerEntity geomancer : player.level().getEntitiesOfClass(
                ArchGeomancerEntity.class, player.getBoundingBox().inflate(32))) {
            if (geomancer.isAlive() && geomancer.getTarget() instanceof Player) {
                ClientEvents.playBossMusic(ModSounds.ARCHGEOMANCER_MUSIC.get(), geomancer);
            }
        }
    }
}
