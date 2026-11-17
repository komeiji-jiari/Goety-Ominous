package com.qiuyue.goetyominous.common.entities.hostile.piglin;

import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HostileFungusThrower extends AbstractFungusThrowerHostile {

    public HostileFungusThrower(EntityType<? extends AbstractFungusThrowerHostile> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getPackItem() {
        return ModItems.FUNGUS_PACK.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int sound = this.random.nextInt(4);
        if (sound == 0) return ModSounds.FUNGUS_THROWER_IDLE_1.get();
        if (sound == 1) return ModSounds.FUNGUS_THROWER_IDLE_2.get();
        if (sound == 2) return ModSounds.FUNGUS_THROWER_IDLE_3.get();
        return ModSounds.FUNGUS_THROWER_IDLE_4.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        int sound = this.random.nextInt(3);
        if (sound == 0) return ModSounds.FUNGUS_THROWER_HURT_1.get();
        if (sound == 1) return ModSounds.FUNGUS_THROWER_HURT_2.get();
        return ModSounds.FUNGUS_THROWER_HURT_3.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        int sound = this.random.nextInt(3);
        if (sound == 0) return ModSounds.FUNGUS_THROWER_DEATH_1.get();
        if (sound == 1) return ModSounds.FUNGUS_THROWER_DEATH_2.get();
        return ModSounds.FUNGUS_THROWER_DEATH_3.get();
    }
}
