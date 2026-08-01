package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Beldam;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Fanatic;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Zealot;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CultistConversionEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!MobsConfig.MonolithConversionEnabled.get()) return;
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Villager || entity instanceof WanderingTrader || entity instanceof Witch)) return;
        if (entity.tickCount % 20 != 0) return;

        boolean nearMonolith = !entity.level().getEntitiesOfClass(
                com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith.class,
                entity.getBoundingBox().inflate(32.0D)).isEmpty();

        if (nearMonolith) {
            int time = entity.getPersistentData().getInt("sis_monolith_time") + 20;
            entity.getPersistentData().putInt("sis_monolith_time", time);

            int conversionTime = (entity.isBaby() ? MobsConfig.MonolithConversionTime.get() / 3 : MobsConfig.MonolithConversionTime.get()) * 20;
            if (time >= conversionTime) {
                convertEntity(entity, (ServerLevel) entity.level());
                entity.getPersistentData().putInt("sis_monolith_time", 0);
            }
        } else if (entity.getPersistentData().getInt("sis_monolith_time") > 0) {
            entity.getPersistentData().putInt("sis_monolith_time", 0);
        }
    }

    private static void convertEntity(LivingEntity entity, ServerLevel level) {
        if (entity instanceof Villager) {
            Fanatic cultist = ModEntityTypes.FANATIC.get().create(level);
            if (cultist != null) {
                cultist.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                cultist.finalizeSpawn(level, level.getCurrentDifficultyAt(cultist.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                level.addFreshEntity(cultist);
                entity.discard();
            }
        } else if (entity instanceof WanderingTrader) {
            Zealot cultist = ModEntityTypes.ZEALOT.get().create(level);
            if (cultist != null) {
                cultist.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                cultist.finalizeSpawn(level, level.getCurrentDifficultyAt(cultist.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                level.addFreshEntity(cultist);
                entity.discard();
            }
        } else if (entity instanceof Witch) {
            Beldam cultist = ModEntityTypes.BELDAM.get().create(level);
            if (cultist != null) {
                cultist.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                cultist.finalizeSpawn(level, level.getCurrentDifficultyAt(cultist.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                level.addFreshEntity(cultist);
                entity.discard();
            }
        }
    }
}
