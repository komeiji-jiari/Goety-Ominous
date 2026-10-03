package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACLoadedMods;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GumWormSegmentServantEntity extends Entity implements KeybindUsingMount, PlayerRideableJumping, FlyingMount {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> FRONT_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> FRONT_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> BACK_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BACK_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(GumWormSegmentServantEntity.class, EntityDataSerializers.INT);

    private static final int MAX_ORPHAN_TICKS = 100;

    public boolean renderHurtFlag = false;
    private int orphanTicks;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float prevZRot;
    private float zRot;
    private boolean lastZRotDirection;
    private int zRotTickOffset = this.random.nextInt(10);
    private Vec3 surfacePosition;
    private double surfaceY;
    private Vec3 prevSurfacePosition;

    public GumWormSegmentServantEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        if (ACLoadedMods.isEntityCullingLoaded()) {
            this.noCulling = true;
        }
    }

    public GumWormSegmentServantEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.GUM_WORM_SEGMENT_SERVANT.get(), level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
    }

    @Nullable
    public UUID getBackEntityUUID() {
        return this.entityData.get(BACK_ENTITY_UUID).orElse(null);
    }

    public void setBackEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(BACK_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getHeadUUID() {
        return this.entityData.get(HEAD_ENTITY_UUID).orElse(null);
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getFrontEntityUUID() {
        return this.entityData.get(FRONT_ENTITY_UUID).orElse(null);
    }

    public void setFrontEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(FRONT_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHeadEntity() {
        if (!this.level().isClientSide) {
            UUID id = this.getHeadUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(HEAD_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public Entity getFrontEntity() {
        if (!this.level().isClientSide) {
            UUID id = this.getFrontEntityUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(FRONT_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public Entity getBackEntity() {
        if (!this.level().isClientSide) {
            UUID id = this.getBackEntityUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(BACK_ENTITY_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public int getIndex() {
        return this.entityData.get(INDEX);
    }

    public void setIndex(int i) {
        this.entityData.set(INDEX, i);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("HeadUUID")) {
            this.setHeadUUID(compound.getUUID("HeadUUID"));
        }
        if (compound.hasUUID("FrontUUID")) {
            this.setFrontEntityUUID(compound.getUUID("FrontUUID"));
        }
        if (compound.hasUUID("BackUUID")) {
            this.setBackEntityUUID(compound.getUUID("BackUUID"));
        }
        this.setIndex(compound.getInt("Index"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getHeadUUID() != null) {
            compound.putUUID("HeadUUID", this.getHeadUUID());
        }
        if (this.getFrontEntityUUID() != null) {
            compound.putUUID("FrontUUID", this.getFrontEntityUUID());
        }
        if (this.getBackEntityUUID() != null) {
            compound.putUUID("BackUUID", this.getBackEntityUUID());
        }
        compound.putInt("Index", this.getIndex());
    }

    public static void createWormSegmentsFor(GumWormServant gumWorm, int count) {
        if (gumWorm.getRidingSegment() != null) {
            return;
        }
        GumWormSegmentServantEntity prev = null;
        GumWormSegmentServantEntity ridingSegment = null;
        for (int i = 0; i < count; ++i) {
            GumWormSegmentServantEntity current = new GumWormSegmentServantEntity(AcEntityRegistry.GUM_WORM_SEGMENT_SERVANT.get(), gumWorm.level());
            current.setHeadUUID(gumWorm.getUUID());
            current.setFrontEntityUUID(prev == null ? gumWorm.getUUID() : prev.getUUID());
            if (prev != null) {
                prev.setBackEntityUUID(current.getUUID());
            }
            current.setIndex(i);
            current.setPos(current.getIdealPosition(prev == null ? gumWorm : prev));
            gumWorm.level().addFreshEntity(current);
            prev = current;
            if (i == GumWormServant.RIDING_SEGMENT_INDEX) {
                ridingSegment = prev;
            }
        }
        if (ridingSegment == null) {
            ridingSegment = prev;
        }
        gumWorm.setRidingSegmentUUID(ridingSegment.getUUID());
        gumWorm.setRidingSegmentId(ridingSegment.getId());
    }

    public Vec3 getIdealPosition(@Nullable Entity parent) {
        Entity head = this.getHeadEntity();
        Entity front = parent == null ? this.getFrontEntity() : parent;
        if (front != null) {
            boolean limp = head instanceof GumWormServant gumWorm && gumWorm.isLimp();
            float backStretch = limp ? -1.5F : -2.5F;
            float sideSwing;
            if (limp) {
                sideSwing = this.limpSideSwing();
            } else {
                sideSwing = 0.0F;
                if (head != null) {
                    float headDelta = Mth.clamp((float) head.getDeltaMovement().length(), 0.0F, 1.0F);
                    if (front == head) {
                        boolean leaping = head instanceof GumWormServant gumWorm && gumWorm.isLeaping();
                        backStretch -= leaping ? 0.7F : 1.2F;
                    }
                    backStretch *= 1.0F - headDelta * 0.3F;
                    sideSwing = (0.5F + (float) this.getIndex() * 0.05F) * (float) Math.sin((float) head.tickCount * 0.2F - (float) this.getIndex());
                }
            }
            Vec3 offsetFromParent = new Vec3(sideSwing, 0.0D, backStretch).xRot(-((float) Math.toRadians(front.xRotO))).yRot(-((float) Math.toRadians(front.yRotO)));
            return front.position().add(offsetFromParent);
        }
        return this.position();
    }

    @Override
    public boolean isPickable() {
        Entity head = this.getHeadEntity();
        return head != null && head.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity head = this.getHeadEntity();
        if (!this.isInvulnerableTo(source) && head != null) {
            head.hurt(source, amount);
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        Entity head = this.getHeadEntity();
        if (damageSource.getEntity() != null && head instanceof GumWormServant gumWorm && gumWorm.isRidingPlayer(damageSource.getEntity())) {
            return true;
        }
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL) || damageSource.is(DamageTypes.FALL);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevZRot = this.zRot;
        this.prevSurfacePosition = this.surfacePosition;
        this.surfacePosition = this.calculateLightAbovePosition();
        this.surfaceY = this.calculateSurfaceY();
        Entity head = this.getHeadEntity();
        Entity front = this.getFrontEntity();
        Entity back = this.getBackEntity();
        if (this.level().isClientSide) {
            if (head instanceof GumWormServant gumWorm) {
                this.renderHurtFlag = gumWorm.hurtTime > 0 || gumWorm.deathTime > 0;
                this.lastZRotDirection = gumWorm.getZRotDirection();
            }
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                double lerpRot = Mth.wrapDegrees(this.lyr - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) lerpRot / (float) this.lSteps);
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
            Player clientPlayer = AlexsCaves.PROXY.getClientSidePlayer();
            if (clientPlayer != null && clientPlayer.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(4)) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), clientPlayer.getId(), 0));
                    clientPlayer.stopRiding();
                }
                if (AlexsCaves.PROXY.isKeyDown(3)) {
                    if (head instanceof GumWormServant gumWorm) {
                        gumWorm.onRidingPlayerAttack();
                    }
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), clientPlayer.getId(), 1));
                }
            }
        } else {
            boolean riddenFlag = false;
            this.entityData.set(HEAD_ENTITY_ID, head != null ? head.getId() : -1);
            this.entityData.set(FRONT_ENTITY_ID, front != null ? front.getId() : -1);
            this.entityData.set(BACK_ENTITY_ID, back != null ? back.getId() : -1);
            if (front == null || head == null) {
                if (this.orphanTicks++ > MAX_ORPHAN_TICKS) {
                    this.discard();
                }
            } else {
                this.orphanTicks = 0;
                float maxDistFromFront = 2.0F;
                Vec3 ideal = this.getIdealPosition(front);
                Vec3 distVec = ideal.subtract(this.position());
                float extraLength = (float) Math.max(distVec.length() - (double) maxDistFromFront, 0.0D);
                Vec3 vec31 = distVec.length() > 1.0D ? distVec.normalize().scale(1.0F + extraLength) : distVec;
                Vec3 vec32 = this.position().add(vec31);
                riddenFlag = head instanceof GumWormServant gumWorm && gumWorm.isRidingMode();
                if (!(front.isInWall() && !riddenFlag || head instanceof GumWormServant gumWorm && gumWorm.isLeaping())) {
                    float f = Mth.approach((float) this.getY(), riddenFlag ? (float) Math.max(this.surfaceY, ideal.y) : (float) Math.min(this.surfaceY, vec31.y), 1.0F);
                    vec32 = new Vec3(vec32.x, f, vec32.z);
                }
                this.setPos(vec32);
                Vec3 frontsBack = front.position().add(new Vec3(0.0D, 0.0D, 3.0D).xRot(-((float) Math.toRadians(front.getXRot()))).yRot(-((float) Math.toRadians(front.getYRot()))));
                double d0 = frontsBack.x - this.getX();
                double d1 = frontsBack.y - this.getY(0.0D);
                double d2 = frontsBack.z - this.getZ();
                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                float f1 = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 57.2957763671875D)));
                float f2 = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F);
                this.setXRot(Mth.approachDegrees(this.getXRot(), f1, 5.0F));
                this.setYRot(Mth.approachDegrees(this.getYRot(), f2, 7.0F));
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
                if (this.hasControllingPassenger() && head instanceof GumWormServant gumWorm && !gumWorm.isRidingMode()) {
                    this.ejectPassengers();
                }
            }
            this.setNoGravity(!riddenFlag);
        }
        this.pushEntities();
        GumWormServant wormHead = head instanceof GumWormServant worm ? worm : null;
        if (wormHead != null && wormHead.isLimp()) {
            this.zRot = Mth.approachDegrees(this.zRot, wormHead.getBodyZRot(1.0F) + this.limpTwist(), this.limpTwistSpeed());
        } else if (this.zRotTickOffset < 0) {
            int i = (this.lastZRotDirection ? -1 : 1) * (this.getIndex() * 5 + 15);
            if (front instanceof GumWormServant gumWorm) {
                this.zRot = Mth.approachDegrees(this.zRot, gumWorm.getBodyZRot(1.0F) + (float) i, 8.0F);
            } else if (front instanceof GumWormSegmentServantEntity segment) {
                this.zRot = Mth.approachDegrees(this.zRot, segment.zRot + (float) i, 8.0F);
            }
        } else {
            --this.zRotTickOffset;
        }
    }

    private float limpSideSwing() {
        float i = (float) this.getIndex();
        return Mth.sin(i * 0.55F) * 0.9F + Mth.sin(i * 1.3F + 1.1F) * 0.35F;
    }

    private float limpTwist() {
        int i = this.getIndex();
        return Mth.sin((float) i * 1.7F) * 16.0F + Mth.sin((float) i * 0.6F) * 9.0F;
    }

    private float limpTwistSpeed() {
        return 6.0F + (Mth.sin((float) this.getIndex() * 1.7F) + 1.0F) * 6.0F;
    }

    private void pushEntities() {
        if (this.level().isClientSide) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::push);
        } else {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
                if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;
                    for (Entity entity : list) {
                        if (entity.isPassenger()) {
                            continue;
                        }
                        ++j;
                    }
                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }
                for (Entity entity : list) {
                    this.push(entity);
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (this.isPassengerOfSameVehicle(entity) || entity instanceof GumWormSegmentServantEntity || entity.noPhysics || this.noPhysics) {
            return;
        }
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Mth.absMax(d0, d1);
        if (d2 < 0.01F) {
            return;
        }
        d2 = Math.sqrt(d2);
        d0 /= d2;
        d1 /= d2;
        double d3 = 1.0 / d2;
        if (d3 > 1.0) {
            d3 = 1.0;
        }
        d0 *= d3;
        d1 *= d3;
        d0 *= 0.05F;
        d1 *= 0.05F;
        if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
            entity.push(d0, 0.0D, d1);
        }
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (this.surfacePosition != null && this.prevSurfacePosition != null) {
            Vec3 difference = this.surfacePosition.subtract(this.prevSurfacePosition);
            return this.prevSurfacePosition.add(difference.scale(f)).add(0.0D, this.getEyeHeight(), 0.0D);
        }
        return super.getLightProbePosition(f);
    }

    private Vec3 calculateLightAbovePosition() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
        while (mutableBlockPos.getY() < this.level().getMaxBuildHeight()
                && this.level().getBlockState(mutableBlockPos).isSuffocating(this.level(), mutableBlockPos)) {
            mutableBlockPos.move(0, 1, 0);
        }
        return new Vec3(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    private double calculateSurfaceY() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set((int) Math.round(this.surfacePosition.x), (int) (Math.max(this.surfacePosition.y, this.getY(1.0D)) + 2.0D), (int) Math.round(this.surfacePosition.z));
        while (mutableBlockPos.getY() > this.level().getMinBuildHeight()
                && !this.level().getBlockState(mutableBlockPos).isSuffocating(this.level(), mutableBlockPos)) {
            mutableBlockPos.move(0, -1, 0);
        }
        return 1.0D + (double) mutableBlockPos.getY();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0D);
    }

    public float getBodyZRot(float partialTicks) {
        return this.prevZRot + (this.zRot - this.prevZRot) * partialTicks;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public Vec3 getRiderPosition(Entity playerOwner) {
        float f = (float) ((double) (this.getBbHeight() + 0.25F) + playerOwner.getMyRidingOffset());
        Vec3 offset = new Vec3(0.0D, f, 0.15D).xRot(-((float) Math.toRadians(this.getXRot()))).yRot(-((float) Math.toRadians(this.getYRot())));
        Vec3 position = this.position().add(offset);
        double setY = this.surfaceY;
        int worldHeight = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) position.x, (int) position.z);
        if (position.y > setY || this.surfaceY > (double) ((float) worldHeight - 3.0F)) {
            setY = position.y;
        }
        return new Vec3(position.x, setY, position.z);
    }

    protected void clampRotation(LivingEntity livingEntity) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            this.clampRotation(living);
            if (passenger instanceof Player player && this.getHeadEntity() instanceof GumWormServant gumWorm) {
                gumWorm.tickController(player);
            }
            Vec3 riderPosition = this.getRiderPosition(passenger);
            moveFunction.accept(passenger, riderPosition.x, riderPosition.y, riderPosition.z);
            return;
        }
        super.positionRider(passenger, moveFunction);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (type == 0 && keyPresser.isPassengerOfSameVehicle(this)) {
            keyPresser.stopRiding();
        }
        if (type == 1 && this.getHeadEntity() instanceof GumWormServant gumWorm) {
            gumWorm.onRidingPlayerAttack();
        }
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return (this.getRemovalReason() == null || this.getRemovalReason().shouldSave()) && !this.isPassenger();
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    public void onPlayerJump(int i) {
    }

    @Override
    public boolean canJump() {
        return this.getHeadEntity() instanceof GumWormServant gumWorm && !gumWorm.isLeaping() && !gumWorm.recentlyLeapt();
    }

    @Override
    public void handleStartJump(int i) {
        if (this.getHeadEntity() instanceof GumWormServant gumWorm) {
            gumWorm.onPlayerJump(i);
        }
    }

    @Override
    public void handleStopJump() {
    }
}
