package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.miauczel.legendary_monsters.config.ModConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class IAnimatedMiniBossServant extends IAnimatedMobServant {
    public IAnimatedMiniBossServant(EntityType entity, Level world) {
        super(entity, world);
        setPersistenceRequired();
    }

    public final int REDUCED_DAMAGE_TICKS = 100;
    public int reducedDamageTicks = REDUCED_DAMAGE_TICKS;

    @Override
    public void tick() {
        if (reducedDamageTicks > 0) reducedDamageTicks--;
        super.tick();
    }

    public double damageCap() {
        return ModConfig.MOB_CONFIG.MiniBossDamageCap.get();
    }

    public float damageReduction() {
        return 1;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        setPersistenceRequired();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (reducedDamageTicks <= 0) {
            reducedDamageTicks = REDUCED_DAMAGE_TICKS;
        }
        if (pSource.is(DamageTypes.FALL)) return false;
        if ((pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !pSource.is(DamageTypes.MAGIC)) && reducedDamageTicks > 0) {
            pAmount *= damageReduction();

        }
        if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(pSource, pAmount);
        } else {
            pAmount = (float) Math.min(damageCap(), pAmount);
        }
        return super.hurt(pSource, pAmount);
    }
}
