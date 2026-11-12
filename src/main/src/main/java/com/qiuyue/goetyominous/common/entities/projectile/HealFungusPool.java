package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HealFungusPool extends AcidPool {

    public HealFungusPool(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void poolTick() {
        if (this.level().isClientSide) return;
        if (this.tickCount % 10 != 0) return;
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (living.getHealth() >= living.getMaxHealth()) continue;
            if (this.getOwner() != null && living != this.getOwner()
                    && !MobUtil.areAllies(this.getOwner(), living)) continue;
            living.heal(1.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        living.getX(), living.getY() + 0.8D, living.getZ(),
                        2, 0.3D, 0.3D, 0.3D, 0.0D);
            }
        }
    }
}
