package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServantPart;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class GravityBigShulkerBullet extends ThrowableProjectile {

    private static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(GravityBigShulkerBullet.class, EntityDataSerializers.FLOAT);

    public GravityBigShulkerBullet(EntityType<GravityBigShulkerBullet> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE, 1.0F);
    }

    public float getBulletSize() {
        return this.entityData.get(SIZE);
    }

    public void setBulletSize(float size) {
        this.entityData.set(SIZE, size);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getBulletSize() * 1.25F, this.getBulletSize() * 1.25F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (SIZE.equals(accessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    private boolean isAlly(Entity entity) {
        if (entity == this || entity == this.getOwner()) {
            return true;
        }
        if (entity instanceof ShulkerMimicServant || entity instanceof ShulkerMimicServantPart) {
            return true;
        }
        return this.getOwner() instanceof LivingEntity owner && entity instanceof LivingEntity living
                && MobUtil.areAllies(owner, living);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return !this.isAlly(target) && super.canHitEntity(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 300 || (this.getOwner() == null && !this.level().isClientSide)) {
            this.discard();
        }
        if (this.level().isClientSide) {
            this.level().addParticle(new AnnihilationBombTrail.OrbData(1.0F, 1.0F, 1.0F, this.getBulletSize() * 0.5F, this.getBulletSize() * 0.75F, this.getId()),
                    this.getX() + 1.5F * (this.random.nextFloat() - 0.5F),
                    this.getY() + 1.5F * (this.random.nextFloat() - 0.5F),
                    this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        Entity hit = result.getEntity();
        if (this.isAlly(hit) || hit instanceof ShulkerMimicServantPart) {
            return;
        }
        if (!(this.getOwner() instanceof LivingEntity owner)) {
            return;
        }
        boolean flag = hit.hurt(ModDamageTypes.causeGravityDamage(owner, owner), 3.0F + this.getBulletSize() * 3.0F);
        if (hit instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.GRAVITY_PULL.get(), 100, 1), this.getEffectSource());
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 1), this.getEffectSource());
        }
        if (flag && hit.isAlive()) {
            this.doEnchantDamageEffects(owner, hit);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.SHULKER_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}
