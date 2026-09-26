package com.qiuyue.goetyominous.common.entities.ally.of;

import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.utils.SmoothAnimationState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkyvernSegmentServant extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_ENTITY_UUID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ENTITY_ID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> FRONT_ENTITY_UUID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> FRONT_ENTITY_ID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> BACK_ENTITY_UUID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BACK_ENTITY_ID =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_ARMS =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> OFFSET_ARMS =
            SynchedEntityData.defineId(SkyvernSegmentServant.class, EntityDataSerializers.BOOLEAN);

    public final SmoothAnimationState fly1AnimationState = new SmoothAnimationState();
    public final SmoothAnimationState fly2AnimationState = new SmoothAnimationState();
    public final SmoothAnimationState attackAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState rollAnimationState = new SmoothAnimationState();
    public boolean renderHurtFlag = false;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    public SkyvernSegmentServant(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEAD_ENTITY_UUID, Optional.empty());
        this.entityData.define(HEAD_ENTITY_ID, -1);
        this.entityData.define(FRONT_ENTITY_UUID, Optional.empty());
        this.entityData.define(FRONT_ENTITY_ID, -1);
        this.entityData.define(BACK_ENTITY_UUID, Optional.empty());
        this.entityData.define(BACK_ENTITY_ID, -1);
        this.entityData.define(INDEX, 0);
        this.entityData.define(HAS_ARMS, false);
        this.entityData.define(OFFSET_ARMS, false);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("HeadUUID")) {
            this.setHeadUUID(compoundTag.getUUID("HeadUUID"));
        }
        if (compoundTag.hasUUID("FrontUUID")) {
            this.setFrontEntityUUID(compoundTag.getUUID("FrontUUID"));
        }
        if (compoundTag.hasUUID("BackUUID")) {
            this.setBackEntityUUID(compoundTag.getUUID("BackUUID"));
        }
        this.setIndex(compoundTag.getInt("Index"));
        this.setHasArms(compoundTag.getBoolean("HasArms"));
        this.setHasOffsetArms(compoundTag.getBoolean("HasOffsetArms"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.getHeadUUID() != null) {
            compoundTag.putUUID("HeadUUID", this.getHeadUUID());
        }
        if (this.getFrontEntityUUID() != null) {
            compoundTag.putUUID("FrontUUID", this.getFrontEntityUUID());
        }
        if (this.getBackEntityUUID() != null) {
            compoundTag.putUUID("BackUUID", this.getBackEntityUUID());
        }
        compoundTag.putInt("Index", this.getIndex());
        compoundTag.putBoolean("HasArms", this.hasArms());
        compoundTag.putBoolean("HasOffsetArms", this.hasOffsetArms());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        Entity head = this.getHeadEntity();
        return head != null && head.isPickable();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource)
                || damageSource.is(DamageTypes.IN_WALL)
                || damageSource.is(DamageTypes.FALL);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        Entity head = this.getHeadEntity();
        if (!this.isInvulnerableTo(damageSource) && head != null) {
            head.hurt(damageSource, amount);
        }
        return false;
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getHeadUUID() {
        return this.entityData.get(HEAD_ENTITY_UUID).orElse(null);
    }

    public void setFrontEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(FRONT_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getFrontEntityUUID() {
        return this.entityData.get(FRONT_ENTITY_UUID).orElse(null);
    }

    public void setBackEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(BACK_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getBackEntityUUID() {
        return this.entityData.get(BACK_ENTITY_UUID).orElse(null);
    }

    public void setHeadEntityId(int id) {
        this.entityData.set(HEAD_ENTITY_ID, id);
    }

    public void setFrontEntityId(int id) {
        this.entityData.set(FRONT_ENTITY_ID, id);
    }

    public void setBackEntityId(int id) {
        this.entityData.set(BACK_ENTITY_ID, id);
    }

    @Nullable
    public Entity getHeadEntity() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getHeadUUID();
            return uuid == null ? null : ((ServerLevel) this.level()).getEntity(uuid);
        }
        int id = this.entityData.get(HEAD_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public boolean isGhost() {
        Entity head = this.getHeadEntity();
        return head instanceof SkyvernServant skyvern && skyvern.isGhost();
    }

    @Nullable
    public Entity getFrontEntity() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getFrontEntityUUID();
            return uuid == null ? null : ((ServerLevel) this.level()).getEntity(uuid);
        }
        int id = this.entityData.get(FRONT_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    @Nullable
    public Entity getBackEntity() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getBackEntityUUID();
            return uuid == null ? null : ((ServerLevel) this.level()).getEntity(uuid);
        }
        int id = this.entityData.get(BACK_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public int getIndex() {
        return this.entityData.get(INDEX);
    }

    public void setIndex(int index) {
        this.entityData.set(INDEX, index);
    }

    public boolean hasArms() {
        return this.entityData.get(HAS_ARMS);
    }

    public void setHasArms(boolean hasArms) {
        this.entityData.set(HAS_ARMS, hasArms);
    }

    public boolean hasOffsetArms() {
        return this.entityData.get(OFFSET_ARMS);
    }

    public void setHasOffsetArms(boolean offsetArms) {
        this.entityData.set(OFFSET_ARMS, offsetArms);
    }

    public void setLinkIds(@Nullable Entity head, @Nullable Entity front, @Nullable Entity back) {
        this.setHeadEntityId(head != null ? head.getId() : -1);
        this.setFrontEntityId(front != null ? front.getId() : -1);
        this.setBackEntityId(back != null ? back.getId() : -1);
    }

    @Override
    public void tick() {
        super.tick();
        Entity head = this.getHeadEntity();
        Entity front = this.getFrontEntity();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
            if (head instanceof SkyvernServant skyvern) {
                this.renderHurtFlag = skyvern.hurtTime > 0 || skyvern.deathTime > 0;
            }
            if (this.lSteps > 0) {
                double x = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double y = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double z = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                double lerpRot = Mth.wrapDegrees(this.lyr - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) lerpRot / (float) this.lSteps);
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(x, y, z);
            } else {
                this.reapplyPosition();
            }
        } else {
            if (front == null || head == null) {
                if (this.tickCount > 3) {
                    this.discard();
                }
            } else {
                float maxDistFromFront = 0.8F;
                Vec3 ideal = this.getIdealPosition(front);
                Vec3 distVec = ideal.subtract(this.position());
                float extraLength = (float) Math.max(distVec.length() - (double) maxDistFromFront, 0.0D);
                Vec3 stretch = distVec.length() > 1.0D
                        ? distVec.normalize().scale((double) (1.0F + extraLength))
                        : distVec;
                this.setPos(this.position().add(stretch.scale(0.8D)));

                Vec3 frontBack = front.position().add(new Vec3(0.0D, 0.0D, 0.8D)
                        .xRot(-((float) Math.toRadians(front.getXRot())))
                        .yRot(-((float) Math.toRadians(front.getYRot()))));
                double dx = frontBack.x - this.getX();
                double dy = frontBack.y - this.getY();
                double dz = frontBack.z - this.getZ();
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                float xRot = Mth.wrapDegrees((float) (-(Mth.atan2(dy, horizontal) * 57.2957763671875D)));
                float yRot = Mth.wrapDegrees((float) (Mth.atan2(dz, dx) * 57.2957763671875D) - 90.0F);
                this.setXRot(Mth.approachDegrees(this.getXRot(), xRot, 8.0F));
                this.setYRot(Mth.approachDegrees(this.getYRot(), yRot, 8.0F));
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
                this.reapplyPosition();
            }
        }
        if (!this.level().isClientSide && this.tickCount % 5 == 0) {
            this.pushEntities();
        }
        if (head != null && this.getPose() != head.getPose()) {
            this.setPose(head.getPose());
        }
    }

    private void setupAnimationStates() {
        Pose pose = this.getPose();
        boolean standing = pose == Pose.STANDING || pose == Pose.ROARING;
        this.fly1AnimationState.animateWhen(!this.hasOffsetArms() && standing, this.tickCount);
        this.fly2AnimationState.animateWhen(this.hasOffsetArms() && standing, this.tickCount);
        this.rollAnimationState.animateWhen(pose == OPPoses.ROLLING.get(), this.tickCount);
        this.attackAnimationState.animateWhen(pose == OPPoses.ATTACKING.get(), this.tickCount);
    }

    public Vec3 getIdealPosition(Entity front) {
        Entity head = this.getHeadEntity();
        float backStretch = -0.85F;
        if (head != null) {
            float headDelta = Mth.clamp((float) head.getDeltaMovement().length(), 0.0F, 1.0F);
            if (front == head) {
                backStretch -= 0.3F;
            }
            backStretch *= 1.0F - headDelta * 0.3F;
        }
        Vec3 offsetFromParent = new Vec3(0.0D, 0.0D, (double) backStretch)
                .xRot(-((float) Math.toRadians(front.getXRot())))
                .yRot(-((float) Math.toRadians(front.getYRot())));
        return front.position().add(offsetFromParent);
    }

    private void pushEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        for (Entity entity : entities) {
            this.push(entity);
        }
    }

    @Override
    public void push(Entity entity) {
        if (this.isPassengerOfSameVehicle(entity) || entity instanceof SkyvernSegmentServant
                || entity.noPhysics || this.noPhysics) {
            return;
        }
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        double max = Mth.absMax(dx, dz);
        if (max < 0.01D) {
            return;
        }
        max = Math.sqrt(max);
        dx /= max;
        dz /= max;
        double scale = 1.0D / max;
        if (scale > 1.0D) {
            scale = 1.0D;
        }
        dx *= scale;
        dz *= scale;
        dx *= 0.05D;
        dz *= 0.05D;
        if (!entity.isPassenger() && entity.isPushable()) {
            entity.push(dx, 0.0D, dz);
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;
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

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        return Math.sqrt(distanceSqr) < 1024.0D;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0D);
    }
}
