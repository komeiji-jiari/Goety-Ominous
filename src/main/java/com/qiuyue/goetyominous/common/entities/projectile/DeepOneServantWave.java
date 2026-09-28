package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.AbstractWave;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class DeepOneServantWave extends AbstractWave {

    public DeepOneServantWave(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public DeepOneServantWave(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.DEEP_ONE_SERVANT_WAVE.get(), level);
        this.setOwner(shooter);
    }

    private boolean scaleBasedDamage = false;

    public void setScaleBasedDamage(boolean scaleBasedDamage) {
        this.scaleBasedDamage = scaleBasedDamage;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.playSound(SoundEvents.GENERIC_SWIM, 0.15F, 1.0F);
        }
        if (this.level().isClientSide) {
            for (int i = 0; i <= 4; ++i) {
                float xOffset = (float) i / 4.0F - 0.5F + (this.random.nextFloat() - 0.5F) * 0.2F;
                this.spawnParticleAt((0.2F + this.random.nextFloat() * 0.2F) * this.getWaveScale(), -0.2F,
                        xOffset * 1.4F * this.getWaveScale(), ParticleTypes.SPLASH);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.scaleBasedDamage = tag.getBoolean("ScaleBasedDamage");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ScaleBasedDamage", this.scaleBasedDamage);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (!this.scaleBasedDamage) {
            return super.getDimensions(pose);
        }
        return this.getType().getDimensions().scale(this.getWaveScale());
    }

    @Override
    public void shrinkingTick() {
        this.discard();
    }

    @Override
    public void attackEntities(float scale) {
        DamageSource source = this.scaleBasedDamage
                ? this.damageSources().mobProjectile(this, this.getOwner())
                : ModDamageSource.indirectDrench(this, this.getOwner());
        AABB box = this.scaleBasedDamage
                ? this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D)
                : this.getBoundingBox().inflate(0.5F * scale, 0.5F, 0.5F * scale);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
            Entity waveOwner = this.getOwner() != null ? this.getOwner() : this;
            if (!waveOwner.isAlliedTo(entity) && !entity.isAlliedTo(waveOwner)
                    && !MobUtil.areAllies(entity, waveOwner)) {
                float damage = (this.scaleBasedDamage ? scale + 1.0F : 5.0F) + this.getExtraDamage();
                entity.hurt(source, damage);
                this.setSlamming(true);
                entity.knockback(0.1D + 0.5D * scale,
                        (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                        (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
            }
        }
    }
}
