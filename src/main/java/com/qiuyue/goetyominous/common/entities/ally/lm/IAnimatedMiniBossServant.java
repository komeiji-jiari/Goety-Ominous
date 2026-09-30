package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class IAnimatedMiniBossServant extends IAnimatedMobServant {
    public IAnimatedMiniBossServant(EntityType entity, Level world) {
        super(entity, world);
        setPersistenceRequired();
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
}
