package com.qiuyue.goetyominous.client.events.of;

import com.qiuyue.goetyominous.client.sound.of.DicerServantLaserSoundHandler;
import com.qiuyue.goetyominous.client.sound.of.FrowzyServantPantingSoundHandler;
import com.qiuyue.goetyominous.client.sound.of.SkyvernServantLoopSoundHandler;
import com.qiuyue.goetyominous.client.sound.of.TerrorServantSawSoundHandler;
import com.qiuyue.goetyominous.client.sound.of.VoltServantElectricChargeSoundHandler;
import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.qiuyue.goetyominous.common.entities.projectile.of.DicerServantLaser;
import com.qiuyue.goetyominous.common.entities.projectile.of.VoltServantElectricCharge;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class OfServantSoundEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            return;
        }
        if (entity instanceof FrowzyServant frowzy) {
            if (frowzy.isAggressive() && frowzy.isAlive()) {
                FrowzyServantPantingSoundHandler.playFor(frowzy);
            } else {
                FrowzyServantPantingSoundHandler.clearFor(frowzy);
            }
        } else if (entity instanceof SkyvernServant skyvern) {
            if (skyvern.isAlive()) {
                SkyvernServantLoopSoundHandler.playFor(skyvern);
            } else {
                SkyvernServantLoopSoundHandler.clearFor(skyvern);
            }
        } else if (entity instanceof TerrorServant terror) {
            if (terror.isAlive() && terror.getPose() == OPPoses.SAWING.get()) {
                TerrorServantSawSoundHandler.playFor(terror);
            } else {
                TerrorServantSawSoundHandler.clearFor(terror);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof FrowzyServant frowzy) {
            FrowzyServantPantingSoundHandler.clearFor(frowzy);
        } else if (entity instanceof SkyvernServant skyvern) {
            SkyvernServantLoopSoundHandler.clearFor(skyvern);
        } else if (entity instanceof TerrorServant terror) {
            TerrorServantSawSoundHandler.clearFor(terror);
        } else if (entity instanceof DicerServantLaser laser) {
            DicerServantLaserSoundHandler.clearFor(laser);
        } else if (entity instanceof VoltServantElectricCharge charge) {
            VoltServantElectricChargeSoundHandler.clearFor(charge);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof DicerServantLaser laser && laser.isAlive()) {
                DicerServantLaserSoundHandler.playFor(laser);
            } else if (entity instanceof VoltServantElectricCharge charge && charge.isAlive()) {
                VoltServantElectricChargeSoundHandler.playFor(charge);
            }
        }
    }
}
