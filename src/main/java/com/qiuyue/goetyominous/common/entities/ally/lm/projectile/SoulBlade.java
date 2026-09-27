package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulBlade extends SoulBladeBase {

    public SoulBlade(EntityType<? extends SoulBlade> entityType, Level level) {
        super(entityType, level);
    }

    public SoulBlade(Level worldIn, double x, double y, double z, float yRot, int warmupDelayTicks,
                     LivingEntity casterIn, float damage, boolean isRed) {
        super(LmEntityRegistry.SOUL_BLADE_UNDERGROUND.get(), worldIn, x, y, z, yRot, warmupDelayTicks,
                casterIn, damage, isRed);
    }

    @Override
    protected int discardLifeTicks() {
        return 70;
    }
}
