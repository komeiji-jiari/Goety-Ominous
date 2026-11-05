package com.qiuyue.goetyominous.client.events.ac;

import com.qiuyue.goetyominous.client.sound.ac.CandicornServantChargeSoundHandler;
import com.qiuyue.goetyominous.client.sound.ac.CorrodentServantDigSoundHandler;
import com.qiuyue.goetyominous.client.sound.ac.NucleeperServantSoundHandler;
import com.qiuyue.goetyominous.client.sound.ac.TremorzillaEmergenceRoarSound;
import com.qiuyue.goetyominous.common.entities.ally.ac.CandicornServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.CorrodentServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AcServantSoundEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            return;
        }
        if (entity instanceof CandicornServant candicorn) {
            if (candicorn.isCharging() && candicorn.isAlive()) {
                CandicornServantChargeSoundHandler.startChargeFor(candicorn);
            } else {
                CandicornServantChargeSoundHandler.clearChargeFor(candicorn);
            }
        } else if (entity instanceof CorrodentServant corrodent) {
            if (corrodent.isDigging() && corrodent.isAlive()) {
                CorrodentServantDigSoundHandler.startDigFor(corrodent);
            } else {
                CorrodentServantDigSoundHandler.clearDigFor(corrodent);
            }
        } else if (entity instanceof NucleeperServant nucleeper) {
            if (nucleeper.isTriggered() && nucleeper.isAlive()) {
                NucleeperServantSoundHandler.startSirenFor(nucleeper);
            } else {
                NucleeperServantSoundHandler.clearSoundFor(nucleeper);
            }
        } else if (entity instanceof TremorzillaServant tremorzilla) {
            if (tremorzilla.getAnimation() == TremorzillaServant.ANIMATION_ROAR_2
                    && tremorzilla.tryPlayEmergenceRoar()) {
                TremorzillaEmergenceRoarSound.play(tremorzilla);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof CandicornServant candicorn) {
            CandicornServantChargeSoundHandler.clearChargeFor(candicorn);
        } else if (entity instanceof CorrodentServant corrodent) {
            CorrodentServantDigSoundHandler.clearDigFor(corrodent);
        } else if (entity instanceof NucleeperServant nucleeper) {
            NucleeperServantSoundHandler.clearSoundFor(nucleeper);
        }
    }
}
