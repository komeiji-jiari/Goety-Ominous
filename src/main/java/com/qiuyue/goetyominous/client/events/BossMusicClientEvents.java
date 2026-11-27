package com.qiuyue.goetyominous.client.events;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.StormNecromancer;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class BossMusicClientEvents {

    private BossMusicClientEvents() {
    }

    @SubscribeEvent
    public static void playBossMusic(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob boss)) {
            return;
        }
        if (!(boss.level() instanceof ClientLevel)) {
            return;
        }
        if (boss.isNoAi() || !(MiscCapHelper.getMobTarget(boss) instanceof Player)) {
            return;
        }

        if (boss instanceof PiglinShaman shaman) {
            ClientEvents.playBossMusic(ModSounds.SHAMAN_BOSS.get(), shaman, 1.0F, 1.0F);
        } else if (boss instanceof GuzzlerBoss guzzler) {
            ClientEvents.playBossMusic(ModSounds.GUZZLER_BOSS.get(),
                    ModSounds.GUZZLER_BOSS_POST.get(), guzzler, 1.0F, 1.0F);
        } else if (boss instanceof StormNecromancer storm) {
            ClientEvents.playBossMusic(
                    com.Polarice3.Goety.init.ModSounds.WITHER_NECROMANCER_THEME.get(),
                    com.Polarice3.Goety.init.ModSounds.ARENA_END.get(),
                    storm, 0.75F, 1.0F);
        }
    }
}
