package com.qiuyue.goetyominous.client.events;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class PiglinShamanClientEvents {

    private PiglinShamanClientEvents() {
    }

    @SubscribeEvent
    public static void playBossMusic(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof PiglinShaman shaman)) {
            return;
        }
        if (!(shaman.level() instanceof ClientLevel)) {
            return;
        }
        if (shaman.isNoAi() || !(MiscCapHelper.getMobTarget(shaman) instanceof Player)) {
            return;
        }
        ClientEvents.playBossMusic(ModSounds.SHAMAN_BOSS.get(), shaman, 1.0F, 1.0F);
    }
}
