package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServantPart;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AbstractFlyingProjectile;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class BigShulkerBullet extends AbstractFlyingProjectile {

    private static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(BigShulkerBullet.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_FIREWORK =
            SynchedEntityData.defineId(BigShulkerBullet.class, EntityDataSerializers.BOOLEAN);

    private float inertia = 1.0F;

    public BigShulkerBullet(EntityType<? extends BigShulkerBullet> type, Level level) {
        super(type, level);
    }

    public BigShulkerBullet(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ,
                            float size, boolean isFirework, LivingEntity passenger) {
        super(LmEntityRegistry.BIG_SHULKER_BULLET.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.setBulletSize(size);
        this.setIsFirework(isFirework);
        if (passenger != null) {
            passenger.startRiding(this, true);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE, 1.0F);
        this.entityData.define(IS_FIREWORK, false);
    }

    public float getBulletSize() {
        return this.entityData.get(SIZE);
    }

    public void setBulletSize(float size) {
        this.entityData.set(SIZE, size);
    }

    public boolean getIsFirework() {
        return this.entityData.get(IS_FIREWORK);
    }

    public void setIsFirework(boolean isFirework) {
        this.entityData.set(IS_FIREWORK, isFirework);
    }

    public void setInertia(float inertia) {
        this.inertia = inertia;
    }

    @Override
    protected float getInertia() {
        return this.inertia;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
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

    @Override
    protected boolean shouldBurn() {
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
        if (this.isVehicle() && this.getFirstPassenger() instanceof Player player) {
            player.setShiftKeyDown(false);
        }
        if (this.getIsFirework() && this.tickCount > 12) {
            this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 0.75F);
            float f1 = (this.random.nextFloat() - 0.75F) * 3.0F;
            float f2 = (this.random.nextFloat() - 0.75F) * 2.5F;
            float f3 = (this.random.nextFloat() - 0.7F) * 3.0F;
            float f4 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f5 = (this.random.nextFloat() - 0.5F) * 2.0F;
            float f6 = (this.random.nextFloat() - 0.5F) * 4.0F;
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; ++i) {
                    serverLevel.sendParticles(ModParticles.SHULKER_EXPLOSION.get(),
                            this.getX() + f1, this.getY() + 1.0D + f2, this.getZ() + f3, 15, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(ModParticles.PURPLE_SHULKER_EXPLOSION.get(),
                            this.getX() + f4, this.getY() + 1.0D + f5, this.getZ() + f6, 15, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        boolean expired = this.getIsFirework() ? this.tickCount > 16 : this.tickCount > 300;
        if (expired || (this.getOwner() == null && !this.level().isClientSide)) {
            if (this.getIsFirework()) {
                this.AreaAttack(5.0F, 5.0F, 360.0F, 8.0F);
            }
            this.discard();
        }
        if (this.level().isClientSide) {
            float random1 = 0.25F;
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F + this.random.nextFloat() * random1;
            this.level().addParticle(new AnnihilationBombTrail.OrbData(r, g, b, this.getBulletSize() * 0.5F, this.getBulletSize() * 0.75F, this.getId()),
                    this.getX() + 1.5F * (this.random.nextFloat() - 0.5F),
                    this.getY() + 1.5F * (this.random.nextFloat() - 0.5F),
                    this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.getIsFirework() || this.level().isClientSide) {
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
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, (int) (40.0F * this.getBulletSize()), 1), this.getEffectSource());
        }
        if (flag && hit.isAlive()) {
            this.doEnchantDamageEffects(owner, hit);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.getIsFirework()) {
            return;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.SHULKER_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    private List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(distanceX, distanceY, distanceZ),
                e -> e != (Entity) this && this.distanceTo(e) <= radius + e.getBbWidth() / 2.0F && e.getY() <= this.getY() + distanceY);
    }

    private void AreaAttack(float range, float height, float arc, float damage) {
        if (!(this.getOwner() instanceof LivingEntity owner)) {
            return;
        }
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180.0D / Math.PI) - 90.0D) % 360.0D);
            float entityAttackingAngle = this.getYRot() % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            boolean inArc = entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc || this.isAlly(entityHit)) {
                continue;
            }
            float extra = entityHit.getMaxHealth() * 0.01F;
            entityHit.hurt(ModDamageTypes.causeGravityDamage(owner, owner),
                    (float) (damage * ModConfig.MOB_CONFIG.ShulkerMimicDamageMutliplier.get() + extra));
        }
    }
}
