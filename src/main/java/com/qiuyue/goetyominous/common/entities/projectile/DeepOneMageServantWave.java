package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.projectiles.AbstractWave;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class DeepOneMageServantWave extends AbstractWave {

    public DeepOneMageServantWave(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public DeepOneMageServantWave(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WAVE.get(), level);
        this.setOwner(shooter);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getType().getDimensions().scale(this.getWaveScale());
    }

    @Override
    public void shrinkingTick() {
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.getWaitingTicks() <= 0) {
            int particleCount = 0;
            while ((float) particleCount < this.getWaveScale()) {
                for (int i = 0; i <= 4; i++) {
                    float xOffset = (float) i / 4.0F - 0.5F + (this.random.nextFloat() - 0.5F) * 0.2F;
                    this.spawnParticleAt((0.2F + this.random.nextFloat() * 0.2F) * this.getWaveScale(), 1.2F,
                            xOffset * 1.2F * this.getWaveScale(), ACParticleRegistry.WATER_FOAM.get());
                    this.spawnParticleAt((0.2F + this.random.nextFloat() * 0.2F) * this.getWaveScale(), -0.2F,
                            xOffset * 1.4F * this.getWaveScale(), ParticleTypes.SPLASH);
                }
                particleCount++;
            }
        }
    }

    @Override
    public void attackEntities(float scale) {
        AABB aabb = this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D);
        LivingEntity owner = this.getOwner();
        DamageSource source = this.damageSources().mobProjectile(this, owner);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (!this.canWaveHit(entity, owner)) {
                continue;
            }
            entity.hurt(source, scale + 1.0F);
            this.setSlamming(true);
            entity.knockback(0.1D + 0.5D * scale,
                    (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                    (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
        }
    }

    private boolean canWaveHit(Entity entity, LivingEntity owner) {
        if (entity instanceof DeepOneBaseEntity) {
            return false;
        }
        if (owner != null) {
            if (entity.equals(owner)) {
                return false;
            }
            if (owner instanceof Mob mob) {
                if (mob.getTarget() == entity) {
                    return true;
                }
                if (mob.getVehicle() != null && entity == mob.getVehicle() && mob.getTarget() != entity) {
                    return false;
                }
            }
            if (owner.isAlliedTo(entity) || entity.isAlliedTo(owner) || MobUtil.areAllies(owner, entity)) {
                return false;
            }
            if (entity instanceof Projectile projectile && projectile.getOwner() == owner) {
                return false;
            }
            if (entity instanceof IOwned owned0 && owner instanceof IOwned owned1) {
                return !MobUtil.ownerStack(owned0, owned1);
            }
        }
        return true;
    }
}
