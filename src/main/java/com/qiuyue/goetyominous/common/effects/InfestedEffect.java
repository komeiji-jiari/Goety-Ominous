package com.qiuyue.goetyominous.common.effects;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class InfestedEffect extends MobEffect {
    private static final float CHANCE_TO_SPAWN = 0.1F;
    private static final float MAX_SPAWN_ANGLE = 1.5707964F;
    private static final int MIN_SILVERFISH = 1;
    private static final int MAX_SILVERFISH = 2;
    private static final float LAUNCH_SPEED = 0.3F;
    private static final float LAUNCH_VERTICAL_SCALE = 1.5F;

    public InfestedEffect() {
        super(MobEffectCategory.HARMFUL, 9214860);
    }

    public void onMobHurt(LivingEntity entity, int amplifier) {
        if (entity.getRandom().nextFloat() > CHANCE_TO_SPAWN) {
            return;
        }
        int count = Mth.randomBetweenInclusive(entity.getRandom(), MIN_SILVERFISH, MAX_SILVERFISH);
        for (int i = 0; i < count; ++i) {
            this.spawnSilverfish(entity.level(), entity, entity.getX(),
                    entity.getY() + entity.getBbHeight() / 2.0D, entity.getZ());
        }
    }

    private void spawnSilverfish(Level level, LivingEntity entity, double x, double y, double z) {
        Silverfish silverfish = EntityType.SILVERFISH.create(level);
        if (silverfish == null) {
            return;
        }
        RandomSource random = entity.getRandom();
        float angle = Mth.randomBetween(random, -MAX_SPAWN_ANGLE, MAX_SPAWN_ANGLE);
        Vector3f velocity = entity.getLookAngle().toVector3f()
                .mul(LAUNCH_SPEED).mul(1.0F, LAUNCH_VERTICAL_SCALE, 1.0F).rotateY(angle);
        silverfish.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
        silverfish.setDeltaMovement(new Vec3(velocity));
        level.addFreshEntity(silverfish);
        silverfish.playSound(SoundEvents.SILVERFISH_HURT);
    }
}
