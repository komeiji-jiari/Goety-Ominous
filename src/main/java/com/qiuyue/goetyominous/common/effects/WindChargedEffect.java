package com.qiuyue.goetyominous.common.effects;

import com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.utils.WindChargeExplosion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class WindChargedEffect extends MobEffect {
    private static final float BASE_RADIUS = 3.0F;
    private static final float RANDOM_RADIUS = 2.0F;

    public WindChargedEffect() {
        super(MobEffectCategory.HARMFUL, 12438015);
    }

    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.KILLED) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() / 2.0F;
        double z = entity.getZ();
        float radius = BASE_RADIUS + entity.getRandom().nextFloat() * RANDOM_RADIUS;
        WindChargeExplosion explosion = new WindChargeExplosion(level, entity,
                level.damageSources().explosion(entity, entity),
                AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR, x, y, z, radius,
                ModParticleTypes.GUST_EMITTER_SMALL.get(), ModParticleTypes.GUST_EMITTER_LARGE.get(),
                ModSounds.BREEZE_WIND_CHARGE_BURST.get());
        explosion.explode();
        explosion.finalizeExplosion(true);
    }
}
