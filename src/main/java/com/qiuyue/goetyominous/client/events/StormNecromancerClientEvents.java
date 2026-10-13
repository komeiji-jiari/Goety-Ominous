package com.qiuyue.goetyominous.client.events;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.StormNecromancer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class StormNecromancerClientEvents {

    private StormNecromancerClientEvents() {
    }

    @SubscribeEvent
    public static void playBossMusic(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof StormNecromancer necromancer)) {
            return;
        }
        if (!(necromancer.level() instanceof ClientLevel)) {
            return;
        }
        if (necromancer.isNoAi() || !(MiscCapHelper.getMobTarget(necromancer) instanceof Player)) {
            return;
        }
        ClientEvents.playBossMusic(ModSounds.WITHER_NECROMANCER_THEME.get(), ModSounds.ARENA_END.get(), necromancer, 0.75F, 1.0F);
    }
}
