package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.SpellEntity;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.client.sound.DicerServantLaserSoundHandler;
import com.qiuyue.goetyominous.common.init.of.OfDamageTypes;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.unusualmodding.opposing_force.registry.OPParticles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DicerServantLaser extends SpellEntity {

    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FIERY = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STORM = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IMMEDIATE = SynchedEntityData.defineId(DicerServantLaser.class, EntityDataSerializers.BOOLEAN);

    public LivingEntity caster;
    public double endPosX;
    public double endPosY;
    public double endPosZ;
    public double collidePosX;
    public double collidePosY;
    public double collidePosZ;
    public double prevCollidePosX;
    public double prevCollidePosY;
    public double prevCollidePosZ;
    public float renderYaw;
    public float renderPitch;
    public float prevYaw;
    public float prevPitch;
    public Direction blockSide = null;
    public boolean on = true;
    public ControlledAnimation appear = new ControlledAnimation(3);

    private boolean eyeSpawn = false;

    public DicerServantLaser(EntityType<? extends DicerServantLaser> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public DicerServantLaser(Level level, LivingEntity caster, double x, double y, double z,
                             float yaw, float pitch, int duration, float damage) {
        this(OfEntityRegistry.DICER_SERVANT_LASER.get(), level);
        this.caster = caster;
        this.setOwner(caster);
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setDuration(duration);
        this.setDamage(damage);
        this.setPos(x, y, z);
        this.calculateEndPos();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(YAW, 0.0F);
        this.entityData.define(PITCH, 0.0F);
        this.entityData.define(DURATION, 0);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(FIERY, false);
        this.entityData.define(STORM, false);
        this.entityData.define(IMMEDIATE, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStorm(compound.getBoolean("Storm"));
        this.setFiery(compound.getBoolean("Fiery"));
        this.setDuration(compound.getInt("Duration"));
        this.setDamage(compound.getFloat("Damage"));
        this.setImmediate(compound.getBoolean("Immediate"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Storm", this.isStorm());
        compound.putBoolean("Fiery", this.isFiery());
        compound.putInt("Duration", this.getDuration());
        compound.putFloat("Damage", this.getDamage());
        compound.putBoolean("Immediate", this.isImmediate());
    }

    @Override
    public void tick() {
        this.baseTick();
        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        LivingEntity owner = this.getOwner();
        if (owner != null) {
            this.caster = owner;
        }
        if (this.caster != null && this.caster.isAlive() && !this.isRemoved()) {
            this.setYaw((float) (((double) this.caster.yHeadRot + 90.0D) * Math.PI / 180.0D));
            this.setPitch((float) (-(double) this.caster.getXRot() * Math.PI / 180.0D));
            if (!this.level().isClientSide) {
                if (this.eyeSpawn) {
                    Vec3 look = this.caster.getLookAngle().normalize();
                    Vec3 eyePos = this.caster.getEyePosition();
                    double backward = 0.3D;
                    this.setPos(eyePos.x() - look.x() * backward,
                            eyePos.y() - 0.2D - look.y() * backward,
                            eyePos.z() - look.z() * backward);
                } else {
                    Vec3 offset = this.caster.getLookAngle().normalize().scale(0.75D);
                    this.setPos(this.caster.getX() + offset.x(),
                            this.caster.getY() + 2.45D + offset.y(),
                            this.caster.getZ() + offset.z());
                }
            }
        }
        if (this.caster != null) {
            this.renderYaw = (float) (((double) this.caster.yHeadRot + 90.0D) * Math.PI / 180.0D);
            this.renderPitch = (float) (-(double) this.caster.getXRot() * Math.PI / 180.0D);
        }

        if (!this.on && this.appear.getTimer() == 0) {
            this.discard();
        }
        if (this.on && this.started()) {
            this.appear.increaseTimer();
        } else {
            this.appear.decreaseTimer();
        }
        if (this.caster != null && !this.caster.isAlive()) {
            this.discard();
        }

        if (this.started()) {
            this.calculateEndPos();
            List<LivingEntity> entities = this.raytraceEntities(this.level(),
                    new Vec3(this.getX(), this.getY(), this.getZ()),
                    new Vec3(this.endPosX, this.endPosY, this.endPosZ)).entities;
            if (this.blockSide != null) {
                this.spawnLaserParticles();
            }
            if (this.level().isClientSide && this.isAlive()) {
                DicerServantLaserSoundHandler.playFor(this);
            }
            if (!this.level().isClientSide) {
                for (LivingEntity target : entities) {
                    if (this.caster == null || this.caster.isAlliedTo(target) || target == this.caster) {
                        continue;
                    }
                    if (this.isFiery()) {
                        target.setSecondsOnFire(5);
                    }
                    target.hurt(OfDamageTypes.laser(this.level(), this, this.caster), this.getDamage());
                    if (this.isStorm()) {
                        float chance = 0.25F;
                        float chainDamage = this.getDamage() / 2.0F;
                        if (this.level().isThundering() && this.level().isRainingAt(target.blockPosition())) {
                            chance += 0.25F;
                            chainDamage = this.getDamage();
                        }
                        if (this.level().getRandom().nextFloat() < chance) {
                            target.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(),
                                    MathHelper.secondsToTicks(5)));
                        }
                        WandUtil.chainLightning(target, this.caster, 4.0D, chainDamage);
                    }
                }
            }
        }
        if (this.elapsed() > this.getDuration()) {
            this.on = false;
        }
    }

    private boolean started() {
        return this.isImmediate() || this.tickCount > 20;
    }

    private int elapsed() {
        return this.isImmediate() ? this.tickCount : this.tickCount - 20;
    }

    private void spawnLaserParticles() {
        for (int i = 0; i < 6; ++i) {
            float yaw = (float) ((double) (this.random.nextFloat() * 2.0F) * Math.PI);
            float motionY = this.random.nextFloat() * 0.15F;
            float motionX = 0.25F * Mth.cos(yaw);
            float motionZ = 0.25F * Mth.sin(yaw);
            this.level().addParticle(OPParticles.LASER_BOLT_DUST.get(),
                    this.collidePosX, this.collidePosY + 0.1D, this.collidePosZ,
                    motionX, motionY, motionZ);
        }
    }

    private void calculateEndPos() {
        double radius = 24.0D;
        if (this.level().isClientSide) {
            this.endPosX = this.getX() + radius * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosZ = this.getZ() + radius * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosY = this.getY() + radius * Math.sin(this.renderPitch);
        } else {
            this.endPosX = this.getX() + radius * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosZ = this.getZ() + radius * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosY = this.getY() + radius * Math.sin(this.getPitch());
        }
    }

    public LaserHitResult raytraceEntities(Level level, Vec3 start, Vec3 end) {
        LaserHitResult result = new LaserHitResult();
        result.setBlockHit(level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.getBlockHit() != null) {
            Vec3 location = result.getBlockHit().getLocation();
            this.collidePosX = location.x;
            this.collidePosY = location.y;
            this.collidePosZ = location.z;
            this.blockSide = result.getBlockHit().getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.blockSide = null;
        }

        AABB aabb = new AABB(
                Math.min(this.getX(), this.collidePosX),
                Math.min(this.getY(), this.collidePosY),
                Math.min(this.getZ(), this.collidePosZ),
                Math.max(this.getX(), this.collidePosX),
                Math.max(this.getY(), this.collidePosY),
                Math.max(this.getZ(), this.collidePosZ)
        ).inflate(1.0);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity entity : entities) {
            if (entity == this.caster) {
                continue;
            }
            if (this.caster != null
                    && (this.caster.isAlliedTo(entity) || entity.isAlliedTo(this.caster)
                    || MobUtil.areAllies(this.caster, entity))) {
                continue;
            }

            float f = entity.getPickRadius() + 0.1F;
            AABB box = entity.getBoundingBox().inflate(f, f, f);
            Optional<Vec3> clip = box.clip(start, end);
            if (box.contains(start) || clip.isPresent()) {
                result.addEntityHit(entity);
            }
        }
        return result;
    }

    public float getYaw() {
        return this.entityData.get(YAW);
    }

    public void setYaw(float yaw) {
        this.entityData.set(YAW, yaw);
    }

    public float getPitch() {
        return this.entityData.get(PITCH);
    }

    public void setPitch(float pitch) {
        this.entityData.set(PITCH, pitch);
    }

    public int getDuration() {
        return this.entityData.get(DURATION);
    }

    public void setDuration(int duration) {
        this.entityData.set(DURATION, duration);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public boolean isFiery() {
        return this.entityData.get(FIERY);
    }

    public void setFiery(boolean fiery) {
        this.entityData.set(FIERY, fiery);
    }

    // tick() 已由远端(2026-09-24)整体重写为 OF 投射物独立实现：在 raytraceEntities 之前
    // 就按施法者视线方向同步了位置，覆盖了本地"把 setPos 提到 super.tick() 之前"的修法。
    public boolean isStorm() {
        return this.entityData.get(STORM);
    }

    public void setStorm(boolean storm) {
        this.entityData.set(STORM, storm);
    }

    public boolean isImmediate() {
        return this.entityData.get(IMMEDIATE);
    }

    public void setImmediate(boolean immediate) {
        this.entityData.set(IMMEDIATE, immediate);
    }

    public boolean isEyeSpawn() {
        return this.eyeSpawn;
    }

    public void setEyeSpawn(boolean eyeSpawn) {
        this.eyeSpawn = eyeSpawn;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.level().isClientSide) {
            DicerServantLaserSoundHandler.clearFor(this);
        }
        super.remove(reason);
    }

    public static class LaserHitResult {
        private BlockHitResult blockHit;
        public final List<LivingEntity> entities = new ArrayList<>();

        public BlockHitResult getBlockHit() {
            return this.blockHit;
        }

        public void setBlockHit(HitResult hitResult) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                this.blockHit = (BlockHitResult) hitResult;
            }
        }

        public void addEntityHit(LivingEntity entity) {
            this.entities.add(entity);
        }
    }

    public static class ControlledAnimation {
        private final int duration;
        private int timer;

        public ControlledAnimation(int duration) {
            this.duration = duration;
        }

        public int getTimer() {
            return this.timer;
        }

        public void increaseTimer() {
            if (this.timer < this.duration) {
                ++this.timer;
            }
        }

        public void decreaseTimer() {
            if (this.timer > 0) {
                --this.timer;
            }
        }
    }
}
