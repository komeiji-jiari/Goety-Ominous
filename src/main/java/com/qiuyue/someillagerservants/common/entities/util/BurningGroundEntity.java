package com.qiuyue.someillagerservants.common.entities.util;

import com.Polarice3.Goety.common.entities.util.AbstractTrap;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class BurningGroundEntity extends AbstractTrap {

    public BurningGroundEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.FLAME);
    }

    public BurningGroundEntity(Level worldIn, double x, double y, double z) {
        this(ModEntityTypes.BURNING_GROUND.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public float radius() {
        return 5.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWater()) {
            this.discard();
            return;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D));
        if (!list.isEmpty()) {
            for (LivingEntity livingentity : list) {
                if (!livingentity.fireImmune()) {
                    livingentity.setSecondsOnFire(8);
                }
            }
        }
        if (this.tickCount >= this.getDuration()) {
            this.discard();
        }
    }
}
