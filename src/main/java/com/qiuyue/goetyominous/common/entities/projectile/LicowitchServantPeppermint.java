package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.SpellTargetProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class LicowitchServantPeppermint extends SpellTargetProjectile {

    private static final EntityDataAccessor<Optional<Vec3>> SPIN_AROUND = SynchedEntityData.defineId(LicowitchServantPeppermint.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    private static final EntityDataAccessor<Float> SPIN_RADIUS = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPIN_SPEED = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_ANGLE = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> STRAIGHT = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SEEKING_ENTITY = SynchedEntityData.defineId(LicowitchServantPeppermint.class, EntityDataSerializers.INT);

    public ItemStack peppermintRenderStack;
    private int despawnsIn = -1;
    private int prevDespawnsIn;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float spinAngle;

    public LicowitchServantPeppermint(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.peppermintRenderStack = new ItemStack(ACBlockRegistry.SMALL_PEPPERMINT.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPIN_AROUND, Optional.empty());
        this.entityData.define(SPIN_RADIUS, 1.0F);
        this.entityData.define(SPIN_SPEED, 1.0F);
        this.entityData.define(START_ANGLE, 0.0F);
        this.entityData.define(STRAIGHT, false);
        this.entityData.define(LIFESPAN, 200);
        this.entityData.define(SEEKING_ENTITY, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.despawnsIn == -1) {
            this.despawnsIn = this.getLifespan();
        }
        this.prevDespawnsIn = this.despawnsIn;
        if (this.despawnsIn > 0) {
            this.despawnsIn--;
        } else if (!this.level().isClientSide) {
            this.discard();
        }
        this.setSpinRadius(3.0F);
        this.setSpinSpeed(7.0F);
        Vec3 encirclePos = this.getSpinAroundPosition();
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double lerpX = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double lerpY = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double lerpZ = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) ((this.lxr - (double) this.getXRot()) / (double) this.lSteps));
                this.lSteps--;
                this.setPos(lerpX, lerpY, lerpZ);
            } else {
                this.reapplyPosition();
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
            Entity owner = this.getOwner();
            if (owner instanceof Mob mob) {
                LivingEntity target = mob.getTarget();
                if (target != null && encirclePos != null) {
                    Vec3 add = target.getEyePosition().subtract(encirclePos);
                    if (add.length() > 1.0D) {
                        add = add.normalize();
                    }
                    this.setSpinAroundPosition(encirclePos.add(add.scale(0.05D)));
                }
            } else if (owner instanceof Player player) {
                Entity seeking = this.getSeekingEntityId() == -1 ? null : this.level().getEntity(this.getSeekingEntityId());
                if (seeking != null) {
                    Vec3 add = seeking.getEyePosition().subtract(this.position());
                    if (add.length() > 1.0D) {
                        add = add.normalize();
                    }
                    this.setSpinRadius(4.0F - 4.0F * Math.min(1.0F, (float) this.tickCount / 30.0F));
                    this.setSpinAroundPosition(this.position().add(add));
                } else {
                    this.setSpinAroundPosition(player.position().add(0.0D, (double) (player.getBbHeight() * 0.45F), 0.0D));
                }
            }
        }
        if (this.isStraight()) {
            if (!this.level().isClientSide) {
                Vec3 vec3 = new Vec3(0.0D, 0.0D, (double) (-0.01F * this.getSpinSpeed())).yRot((float) (-Math.toRadians(this.getYRot())));
                this.setDeltaMovement(this.getDeltaMovement().add(vec3));
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.9D).add(0.0D, -0.08D, 0.0D));
                }
                if (this.verticalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.9D, 0.0D).multiply(0.4D, 1.0D, 0.4D));
                }
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        } else if (encirclePos == null) {
            this.setSpinAroundPosition(this.position());
        } else if (!this.level().isClientSide) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f = Math.min(1.0F, (float) this.tickCount / 30.0F);
            Vec3 angle = new Vec3(0.0D, 0.0D, (double) (f * this.getSpinRadius())).yRot((float) (-Math.toRadians(this.getStartAngle() + this.spinAngle)));
            Vec3 encircle = encirclePos.add(angle);
            Vec3 newDelta = encircle.subtract(this.position());
            this.setDeltaMovement(newDelta.scale(0.05D * (double) this.getSpinSpeed()));
            this.spinAngle += this.getSpinSpeed();
        }
        this.hurtEntities();
    }

    public float getDespawnTime(float partialTicks) {
        return (float) this.prevDespawnsIn + (float) (this.despawnsIn - this.prevDespawnsIn) * partialTicks;
    }

    @Nullable
    public Vec3 getSpinAroundPosition() {
        return this.entityData.get(SPIN_AROUND).orElse(null);
    }

    public void setSpinAroundPosition(@Nullable Vec3 vec3) {
        this.entityData.set(SPIN_AROUND, Optional.ofNullable(vec3));
    }

    public float getSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setSpinSpeed(float spinSpeed) {
        this.entityData.set(SPIN_SPEED, spinSpeed);
    }

    public float getSpinRadius() {
        return this.entityData.get(SPIN_RADIUS);
    }

    public void setSpinRadius(float spinRadius) {
        this.entityData.set(SPIN_RADIUS, spinRadius);
    }

    public boolean isStraight() {
        return this.entityData.get(STRAIGHT);
    }

    public void setStraight(boolean straight) {
        this.entityData.set(STRAIGHT, straight);
    }

    public float getStartAngle() {
        return this.entityData.get(START_ANGLE);
    }

    public void setStartAngle(float startAngle) {
        this.entityData.set(START_ANGLE, startAngle);
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setSeekingEntityId(int seekingEntityId) {
        this.entityData.set(SEEKING_ENTITY, seekingEntityId);
    }

    public int getSeekingEntityId() {
        return this.entityData.get(SEEKING_ENTITY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStraight(compound.getBoolean("Straight"));
        if (compound.contains("DespawnsIn")) {
            this.despawnsIn = compound.getInt("DespawnsIn");
        }
        this.setLifespan(compound.getInt("Lifespan"));
        if (compound.contains("AroundX") && compound.contains("AroundY") && compound.contains("AroundZ")) {
            this.setSpinAroundPosition(new Vec3(compound.getDouble("AroundX"), compound.getDouble("AroundY"), compound.getDouble("AroundZ")));
        }
        this.setSpinSpeed(compound.getFloat("SpinSpeed"));
        this.setSpinRadius(compound.getFloat("SpinRadius"));
        this.setStartAngle(compound.getFloat("StartAngle"));
        this.spinAngle = compound.getFloat("SpinAngle");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("DespawnsIn", this.despawnsIn);
        compound.putBoolean("Straight", this.isStraight());
        Vec3 spinPos = this.getSpinAroundPosition();
        if (spinPos != null) {
            compound.putDouble("AroundX", spinPos.x);
            compound.putDouble("AroundY", spinPos.y);
            compound.putDouble("AroundZ", spinPos.z);
        }
        compound.putFloat("SpinSpeed", this.getSpinSpeed());
        compound.putFloat("SpinRadius", this.getSpinRadius());
        compound.putFloat("StartAngle", this.getStartAngle());
        compound.putFloat("SpinAngle", this.spinAngle);
        compound.putInt("Lifespan", this.getLifespan());
    }

    private void hurtEntities() {
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity owner = this.getOwner();
        DamageSource source = this.damageSources().mobProjectile(this, owner);
        boolean flag = false;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (owner != null) {
                if (entity.is(owner)) {
                    continue;
                }
                if (entity.isAlliedTo(owner)) {
                    continue;
                }
            }
            if (MobUtil.areAllies(entity, owner != null ? owner : this)) {
                continue;
            }
            if (entity.hurt(source, 3.0F)) {
                flag = true;
                entity.knockback(0.3F, this.getX() - entity.getX(), this.getZ() - entity.getZ());
            }
        }
        if (flag && this.getSeekingEntityId() != -1) {
            this.discard();
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = (double) yRot;
        this.lxr = (double) xRot;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.lxd = x;
        this.lyd = y;
        this.lzd = z;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
}
