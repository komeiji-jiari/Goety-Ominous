package com.qiuyue.goetyominous.common.entities.hostile.piglin;

import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ZHostileFungusThrower extends AbstractFungusThrowerHostile {

    public ZHostileFungusThrower(EntityType<? extends AbstractFungusThrowerHostile> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    protected Item getPackItem() {
        return ModItems.RAGGED_FUNGUS_PACK.get();
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
    }
}
