package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class EntityThrown extends ThrowableProjectile {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(EntityThrown.class, EntityDataSerializers.FLOAT);

    public EntityThrown(EntityType<? extends EntityThrown> type, Level level) {
        super(type, level);
    }

    public EntityThrown(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ,
                        float damage, LivingEntity passenger) {
        super(LmEntityRegistry.ENTITY_THROWN.get(), level);
        this.setDamage(damage);
        this.setOwner(shooter);
        this.moveTo(offsetX, offsetY, offsetZ);
        if (passenger != null) {
            passenger.startRiding(this, true);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
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
        return this.getOwner() instanceof LivingEntity owner && entity instanceof LivingEntity living
                && MobUtil.areAllies(owner, living);
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
            entityHit.setDeltaMovement(this.getDeltaMovement().x, 0.3F, this.getDeltaMovement().z);
            entityHit.hurt(ModDamageTypes.causeGravityDamage(owner, owner), damage);
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            if (this.tickCount % 3 == 0) {
                float yaw = (float) Math.toRadians(-this.getYRot() + 180.0F);
                double theta = this.getYRot() * (Math.PI / 180) + 1.5707963267948966;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                Vec3 vec3 = this.getDeltaMovement();
                double spawnX = this.getX() + vec3.x + vecX * 1.5D;
                double spawnZ = this.getZ() + vec3.z + vecZ * 1.5D;
                this.level().addParticle(new Circle.RingData(this.horizontalCollision ? 0.0F : yaw, this.horizontalCollision ? 90.0F : 0.0F,
                                30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                        spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            }
        }
        if (this.isVehicle() && this.getFirstPassenger() instanceof Player player) {
            player.setShiftKeyDown(false);
        }
        super.tick();
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CLOUD;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() == this.getFirstPassenger()) {
            return;
        }
        super.onHitEntity(result);
        if (!this.level().isClientSide && this.getOwner() instanceof LivingEntity owner) {
            Entity entity = result.getEntity();
            if (entity.hurt(this.damageSources().mobAttack(owner), 8.0F) && entity.isAlive()) {
                this.doEnchantDamageEffects(owner, entity);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        this.handleEntityEvent((byte) 1);
        if (result instanceof EntityHitResult) {
            return;
        }
        super.onHit(result);
        this.AreaAttack(3.0F, 3.0F, 360.0F, 5.0F);
        CameraShakeEntity.cameraShake(this.level(), this.position(), 5.0F, 0.09F, 5, 5);
        this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
        this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 100.0F,
                false, Circle.EnumRingBehavior.GROW), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.tickCount = 0;
        } else if (id == 1) {
            this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            double theta = this.getYRot() * (Math.PI / 180) + 1.5707963267948966;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            Vec3 vec3 = this.getDeltaMovement();
            double spawnX = this.getX() + vec3.x + vecX * 1.5D;
            double spawnZ = this.getZ() + vec3.z + vecZ * 1.5D;
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F,
                    false, Circle.EnumRingBehavior.GROW_THEN_SHRINK), spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            this.level().addParticle(new Circle.RingData(1.5707964F, 0.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F,
                    false, Circle.EnumRingBehavior.GROW_THEN_SHRINK), spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
