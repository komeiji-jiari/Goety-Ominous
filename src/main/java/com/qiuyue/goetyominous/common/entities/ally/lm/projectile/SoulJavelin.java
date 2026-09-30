package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SoulJavelin extends AbstractArrow {

    private static final EntityDataAccessor<Boolean> ENHANCED =
            SynchedEntityData.defineId(SoulJavelin.class, EntityDataSerializers.BOOLEAN);

    private boolean dealtDamage;

    public SoulJavelin(EntityType<? extends SoulJavelin> entityType, Level level) {
        super(entityType, level);
    }

    public SoulJavelin(Level level, LivingEntity shooter, boolean enhanced) {
        super(LmEntityRegistry.SOUL_JAVELIN.get(), shooter, level);
        this.setEnhanced(enhanced);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENHANCED, false);
    }

    public boolean isEnhanced() {
        return this.entityData.get(ENHANCED);
    }

    public void setEnhanced(boolean enhanced) {
        this.entityData.set(ENHANCED, enhanced);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        Entity owner = this.getOwner();
        return (owner != null && owner.isAlliedTo(entity)) || super.isAlliedTo(entity);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if (this.level().isClientSide) {
            this.level().addParticle(LmParticles.GHOSTLY_SOUL.get(),
                    this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                    0.0D, 0.025D, 0.0D);
        }

        super.tick();
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();

        float damage;
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage = 8.0F + ServantMath.entityBasedHpDamage(livingEntity, 3.0F);
        } else {
            damage = 8.0F;
        }

        Entity owner = this.getOwner();
        if (hitEntity instanceof LivingEntity livingEntity && !this.isAlliedTo(livingEntity)) {
            DamageSource damageSource = LmDamageTypes.ghostlyOrAttackerless(this.level(), owner);
            this.dealtDamage = true;

            if (hitEntity.hurt(damageSource, damage)) {
                if (hitEntity.getType() == EntityType.ENDERMAN) {
                    return;
                }

                if (owner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingEntity);
                }

                this.doPostHurtEffects(livingEntity);
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
        float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
        float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
        float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
        SimpleParticleType explosion = this.isEnhanced()
                ? LmParticles.SOUL_EXPLOSION_RED.get() : LmParticles.SOUL_EXPLOSION.get();
        SimpleParticleType soul = this.isEnhanced()
                ? LmParticles.GHOSTLY_SOUL_RED.get() : LmParticles.GHOSTLY_SOUL.get();
        this.level().addParticle(explosion,
                this.getX() + (double) f9, this.getY() + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(explosion,
                this.getX() + (double) f8, this.getY() + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(soul,
                this.getX() + (double) f9, this.getY() + 2.0D + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.5D, 0.0D);
        this.level().addParticle(soul,
                this.getX() + (double) f8, this.getY() + 2.0D + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.5D, 0.0D);

        if (!this.level().isClientSide && result instanceof BlockHitResult) {
            this.discard();
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.setEnhanced(compound.getBoolean("enhanced"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
        compound.putBoolean("enhanced", this.isEnhanced());
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }
}
