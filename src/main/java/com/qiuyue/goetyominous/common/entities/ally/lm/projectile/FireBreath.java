package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FireBreath extends Entity {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(FireBreath.class, EntityDataSerializers.FLOAT);
    private LivingEntity caster;
    private UUID casterUuid;

    public FireBreath(EntityType<? extends FireBreath> type, Level world) {
        super(type, world);
    }

    public FireBreath(EntityType<? extends FireBreath> type, Level world, float damage, LivingEntity caster) {
        super(type, world);
        this.setCaster(caster);
        this.setDamage(damage);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.caster != null && !this.caster.isAlive()) {
            this.discard();
        }
        if (this.caster != null) {
            Vec3 pos = new Vec3(this.caster.getX(), this.caster.getY() + 1.0, this.caster.getZ());
            this.setYRot(this.caster.yHeadRot);
            this.setPos(pos);
            this.setXRot(this.caster.getXRot());
        }
        float yaw = (float) Math.toRadians(-this.getYRot());
        float pitch = (float) Math.toRadians(-this.getXRot());
        float spread = 0.25F;
        float speed = 0.56F;
        float xComp = (float) (Math.sin(yaw) * Math.cos(pitch));
        float yComp = (float) Math.sin(pitch);
        float zComp = (float) (Math.cos(yaw) * Math.cos(pitch));
        double theta = (double) this.getYRot() * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        double vec = 0.9;
        if (this.level().isClientSide) {
            for (int i = 0; i < 80; ++i) {
                double xSpeed = (double) (speed * xComp) + (double) (spread * 1.0F * (this.random.nextFloat() * 2.0F - 1.0F)) * Math.sqrt(1.0F - xComp * xComp);
                double ySpeed = (double) (speed * yComp) + (double) (spread * 1.0F * (this.random.nextFloat() * 2.0F - 1.0F)) * Math.sqrt(1.0F - yComp * yComp);
                double zSpeed = (double) (speed * zComp) + (double) (spread * 1.0F * (this.random.nextFloat() * 2.0F - 1.0F)) * Math.sqrt(1.0F - zComp * zComp);
                this.level().addAlwaysVisibleParticle(ModParticles.FLAME_STATIC.get(), this.getX() + vec * vecX, this.getY(), this.getZ() + vec * vecZ, xSpeed, ySpeed, zSpeed);
            }
        }
        if (this.tickCount > 2 && this.caster != null) {
            this.hitEntities();
        }
        if (this.tickCount > 50) {
            this.discard();
        }
    }

    public void hitEntities() {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(10.0, 10.0, 10.0, 10.0);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitYaw = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * 57.29577951308232 - 90.0) % 360.0);
            float entityAttackingYaw = this.getYRot() % 360.0F;
            if (entityHitYaw < 0.0F) {
                entityHitYaw += 360.0F;
            }
            if (entityAttackingYaw < 0.0F) {
                entityAttackingYaw += 360.0F;
            }
            float entityRelativeYaw = entityHitYaw - entityAttackingYaw;
            float xzDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            double hitY = entityHit.getY() + (double) entityHit.getBbHeight() / 2.0;
            float entityHitPitch = (float) (Math.atan2(hitY - this.getY(), xzDistance) * 57.29577951308232 % 360.0);
            float entityAttackingPitch = -this.getXRot() % 360.0F;
            if (entityHitPitch < 0.0F) {
                entityHitPitch += 360.0F;
            }
            if (entityAttackingPitch < 0.0F) {
                entityAttackingPitch += 360.0F;
            }
            float entityRelativePitch = entityHitPitch - entityAttackingPitch;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()) + (hitY - this.getY()) * (hitY - this.getY()));
            boolean inRange = entityHitDistance <= (float) (this.tickCount / 2) + 1.0F;
            boolean yawCheck = entityRelativeYaw <= 22.5F && entityRelativeYaw >= -22.5F || entityRelativeYaw >= 337.5F || entityRelativeYaw <= -337.5F;
            boolean pitchCheck = entityRelativePitch <= 22.5F && entityRelativePitch >= -22.5F || entityRelativePitch >= 337.5F || entityRelativePitch <= -337.5F;
            boolean closeCheck = this.caster != null && entityHitDistance <= 2.0F;
            if ((!inRange || !yawCheck || !pitchCheck) && !closeCheck
                    || this.tickCount % 3 != 0
                    || this.isAlliedTo(entityHit)
                    || ServantAllyUtil.areAllied(this.caster, entityHit)
                    || entityHit == this.caster) continue;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this.caster), this.getDamage());
            if (!flag) continue;
            entityHit.setRemainingFireTicks(60);
            entityHit.setDeltaMovement(entityHit.getDeltaMovement().multiply(0.25, 1.0, 0.25));
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUuid);
            if (entity instanceof LivingEntity livingEntity) {
                this.caster = livingEntity;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
        this.setDamage(compound.getFloat("damage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
        compound.putFloat("damage", this.getDamage());
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void push(Entity entityIn) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return this.getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
        return this.level().getEntitiesOfClass(entityClass, this.getBoundingBox().inflate(dX, dY, dZ),
                e -> e != this && (double) this.distanceTo(e) <= r + (double) (e.getBbWidth() / 2.0F) && e.getY() <= this.getY() + dY);
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
