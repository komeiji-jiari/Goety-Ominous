package com.qiuyue.goetyominous.common.effects;

import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class OozingEffect extends MobEffect {
    private static final double RADIUS_TO_CHECK_SLIMES = 2.0D;
    private static final int SLIME_SIZE = 2;
    private static final int SPAWNED_COUNT = 2;

    public OozingEffect() {
        super(MobEffectCategory.HARMFUL, 10092451);
    }

    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.KILLED) {
            return;
        }
        Level level = entity.level();
        int maxCramming = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
        int count = numberOfSlimesToSpawn(maxCramming, level, entity, SPAWNED_COUNT);
        for (int i = 0; i < count; ++i) {
            this.spawnSlimeOffspring(level, entity.getX(), entity.getY() + 0.5D, entity.getZ());
        }
    }

    private static int numberOfSlimesToSpawn(int maxCramming, Level level, LivingEntity entity, int wanted) {
        if (maxCramming < 1) {
            return wanted;
        }
        List<Slime> nearby = level.getEntities(EntityType.SLIME,
                entity.getBoundingBox().inflate(RADIUS_TO_CHECK_SLIMES), slime -> slime != entity);
        return Mth.clamp(0, maxCramming - nearby.size(), wanted);
    }

    private void spawnSlimeOffspring(Level level, double x, double y, double z) {
        Slime slime = EntityType.SLIME.create(level);
        if (slime == null) {
            return;
        }
        slime.setSize(SLIME_SIZE, true);
        slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(slime);
    }
}
