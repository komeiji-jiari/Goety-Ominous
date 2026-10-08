package com.qiuyue.goetyominous.common.entities.projectile;

import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.SimpleExplosionDamageCalculator;
import com.qiuyue.goetyominous.utils.WindChargeExplosion;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class WindCharge extends AbstractWindCharge {
    private static final float RADIUS = 1.2F;
    private static final float KNOCKBACK_MULTIPLIER = 1.22F;

    public WindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public WindCharge(Level level, double x, double y, double z, Vec3 movement) {
        super(ModEntityTypes.WIND_CHARGE.get(), x, y, z, movement, level);
    }

    @Override
    protected void explode(Vec3 pos) {
        if (!this.level().isClientSide) {
            ExplosionDamageCalculator calculator = new SimpleExplosionDamageCalculator(true, false,
                    Optional.of(KNOCKBACK_MULTIPLIER),
                    BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                            .map(holders -> (HolderSet<Block>) holders));
            WindChargeExplosion explosion = new WindChargeExplosion(this.level(), this,
                    windChargeDamage(this.level(), this, this.getOwner()), calculator, pos.x, pos.y, pos.z, RADIUS,
                    ModParticleTypes.GUST_EMITTER_SMALL.get(), ModParticleTypes.GUST_EMITTER_LARGE.get(),
                    ModSounds.WIND_CHARGE_BURST.get());
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }
}
