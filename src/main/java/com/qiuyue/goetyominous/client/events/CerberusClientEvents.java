package com.qiuyue.goetyominous.client.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.sound.CerberusBreathSound;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Cerberus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class CerberusClientEvents {

    private CerberusClientEvents() {
    }

    @SubscribeEvent
    public static void attachBreathLoop(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel && event.getEntity() instanceof Cerberus cerberus) {
            Minecraft.getInstance().getSoundManager().play(new CerberusBreathSound(cerberus));
        }
    }
}
