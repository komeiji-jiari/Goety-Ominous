package com.qiuyue.goetyominous.common.entities.projectile;

import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.utils.WindChargeExplosion;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BreezeWindCharge extends AbstractWindCharge {

    public BreezeWindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public BreezeWindCharge(Level level, LivingEntity owner, double x, double y, double z) {
        super(ModEntityTypes.BREEZE_WIND_CHARGE.get(), level, owner, x, y, z);
    }

    @Override
    protected void explode(Vec3 pos) {
        if (!this.level().isClientSide) {
            WindChargeExplosion explosion = new WindChargeExplosion(this.level(), this,
                    windChargeDamage(this.level(), this, this.getOwner()),
                    EXPLOSION_DAMAGE_CALCULATOR, pos.x, pos.y, pos.z, 3.0F,
                    ModParticleTypes.GUST_EMITTER_SMALL.get(), ModParticleTypes.GUST_EMITTER_LARGE.get(),
                    ModSounds.BREEZE_WIND_CHARGE_BURST.get());
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }
}
