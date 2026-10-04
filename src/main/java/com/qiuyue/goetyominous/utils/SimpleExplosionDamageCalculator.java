package com.qiuyue.goetyominous.utils;

import java.util.Optional;

import com.qiuyue.goetyominous.common.mixin.ExplosionAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class SimpleExplosionDamageCalculator extends ExplosionDamageCalculator {
    private final boolean explodesBlocks;
    private final boolean damagesEntities;
    private final Optional<Float> knockbackMultiplier;
    private final Optional<HolderSet<Block>> immuneBlocks;

    public SimpleExplosionDamageCalculator(boolean explodesBlocks, boolean damagesEntities,
                                           Optional<Float> knockbackMultiplier,
                                           Optional<HolderSet<Block>> immuneBlocks) {
        this.explodesBlocks = explodesBlocks;
        this.damagesEntities = damagesEntities;
        this.knockbackMultiplier = knockbackMultiplier;
        this.immuneBlocks = immuneBlocks;
    }

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos,
                                                       BlockState state, FluidState fluid) {
        if (this.immuneBlocks.isPresent() && state.is(this.immuneBlocks.get())) {
            return Optional.of(3600000.0F);
        }
        return super.getBlockExplosionResistance(explosion, level, pos, state, fluid);
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
        return this.explodesBlocks;
    }

    public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
        return this.damagesEntities;
    }

    public float getKnockbackMultiplier(Entity entity) {
        if (entity instanceof Player player && player.getAbilities().flying) {
            return 0.0F;
        }
        return this.knockbackMultiplier.orElse(1.0F);
    }

    public float getEntityDamageMultiplier(Explosion explosion, Entity entity) {
        ExplosionAccessor accessor = (ExplosionAccessor) explosion;
        float diameter = accessor.getRadius() * 2.0F;
        Vec3 center = new Vec3(accessor.getX(), accessor.getY(), accessor.getZ());
        double distance = Math.sqrt(entity.distanceToSqr(center)) / (double) diameter;
        double seen = (1.0 - distance) * (double) Explosion.getSeenPercent(center, entity);
        return (float) ((seen * seen + seen) / 2.0 * 7.0 * (double) diameter + 1.0);
    }
}
