package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import org.joml.Vector3f;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class ImpactBlockEntity extends Entity {
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Optional<UUID>> TARGET_ID = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(ImpactBlockEntity.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RISING_DURATION = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HOVER_DURATION = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_DASH_SPEED = SynchedEntityData.defineId(ImpactBlockEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vector3f> DASH_DIRECTION = SynchedEntityData
            .defineId(ImpactBlockEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> EXTRA_DAMAGE = SynchedEntityData.defineId(ImpactBlockEntity.class,
            EntityDataSerializers.FLOAT);

    private Phase phase = Phase.RISING;
    private int phaseTick;
    private double dashSpeed;
    private Vec3 dashDirection = Vec3.ZERO;
    private int risingDuration = 30;
    private int hoverDuration = 20;
    private double maxDashSpeed = 0.65D;
    private boolean phaseInitialized;
    private double originY;
    private double prevX;
    private double prevY;
    private double prevZ;
    private boolean clientInit;

    public ImpactBlockEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
    }


    public void initializePhase(int rising, int hover, double maxSpeed) {
        this.phaseInitialized = true;
        this.risingDuration = rising;
        this.hoverDuration = hover;
        this.maxDashSpeed = maxSpeed;
        this.entityData.set(RISING_DURATION, rising);
        this.entityData.set(HOVER_DURATION, hover);
        this.entityData.set(MAX_DASH_SPEED, (float) maxSpeed);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Blocks.STONE.defaultBlockState());
        this.entityData.define(TARGET_ID, Optional.empty());
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(PHASE, 0);
        this.entityData.define(RISING_DURATION, 30);
        this.entityData.define(HOVER_DURATION, 20);
        this.entityData.define(MAX_DASH_SPEED, 0.65F);
        this.entityData.define(DASH_DIRECTION, new Vector3f(0.0F, -1.0F, 0.0F));
        this.entityData.define(EXTRA_DAMAGE, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BlockState")) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                    tag.getCompound("BlockState")));
        }
        if (tag.hasUUID("Target")) {
            this.setTargetId(tag.getUUID("Target"));
        }
        if (tag.hasUUID("Owner")) {
            this.setOwnerId(tag.getUUID("Owner"));
        }
        this.phase = Phase.values()[Mth.clamp(tag.getInt("Phase"), 0, Phase.values().length - 1)];
        this.phaseTick = tag.getInt("PhaseTick");
        this.dashSpeed = tag.getDouble("DashSpeed");
        this.risingDuration = tag.getInt("RisingDuration");
        this.hoverDuration = tag.getInt("HoverDuration");
        this.maxDashSpeed = tag.getDouble("MaxDashSpeed");
        this.phaseInitialized = tag.getBoolean("PhaseInitialized");
        if (tag.contains("DashX") && tag.contains("DashY") && tag.contains("DashZ")) {
            this.dashDirection = new Vec3(tag.getDouble("DashX"), tag.getDouble("DashY"), tag.getDouble("DashZ"));
        }
        this.entityData.set(PHASE, this.phase.ordinal());
        this.entityData.set(RISING_DURATION, this.risingDuration);
        this.entityData.set(HOVER_DURATION, this.hoverDuration);
        this.entityData.set(MAX_DASH_SPEED, (float) this.maxDashSpeed);
        this.entityData.set(DASH_DIRECTION, new Vector3f((float) this.dashDirection.x, (float) this.dashDirection.y,
                (float) this.dashDirection.z));
        this.setExtraDamage(tag.getFloat("ExtraDamage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.getBlockState()));
        if (this.getTargetId() != null) {
            tag.putUUID("Target", this.getTargetId());
        }
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        tag.putInt("Phase", this.phase.ordinal());
        tag.putInt("PhaseTick", this.phaseTick);
        tag.putDouble("DashSpeed", this.dashSpeed);
        tag.putInt("RisingDuration", this.risingDuration);
        tag.putInt("HoverDuration", this.hoverDuration);
        tag.putDouble("MaxDashSpeed", this.maxDashSpeed);
        tag.putBoolean("PhaseInitialized", this.phaseInitialized);
        tag.putDouble("DashX", this.dashDirection.x);
        tag.putDouble("DashY", this.dashDirection.y);
        tag.putDouble("DashZ", this.dashDirection.z);
        tag.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getLocalPhase() {
        return this.phase.ordinal();
    }

    public int getLocalPhaseTick() {
        return this.phaseTick;
    }

    public float getExtraDamage() {
        return this.entityData.get(EXTRA_DAMAGE);
    }

    public void setExtraDamage(float damage) {
        this.entityData.set(EXTRA_DAMAGE, damage);
    }

    @Nullable
    public UUID getTargetId() {
        return this.entityData.get(TARGET_ID).orElse(null);
    }

    public void setTargetId(@Nullable UUID uuid) {
        this.entityData.set(TARGET_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            this.setTargetId(target.getUUID());
        }
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.setOwnerId(owner.getUUID());
        }
    }

    @Nullable
    public LivingEntity getTargetEntity() {
        UUID uuid = this.getTargetId();
        if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    @Nullable
    public LivingEntity getOwnerEntity() {
        UUID uuid = this.getOwnerId();
        if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            if (!this.clientInit) {
                this.clientInit = true;
                this.prevX = this.getX();
                this.prevY = this.getY();
                this.prevZ = this.getZ();
            }

            this.xOld = this.prevX;
            this.yOld = this.prevY;
            this.zOld = this.prevZ;

            this.risingDuration = this.entityData.get(RISING_DURATION);
            this.hoverDuration = this.entityData.get(HOVER_DURATION);
            this.maxDashSpeed = this.entityData.get(MAX_DASH_SPEED);

            int serverPhase = Mth.clamp(this.entityData.get(PHASE), 0, Phase.values().length - 1);
            if (serverPhase != this.phase.ordinal()) {
                this.phase = Phase.values()[serverPhase];
                this.phaseTick = 0;

                this.dashSpeed = this.maxDashSpeed;
            }
            switch (this.phase) {
                case RISING -> {
                    double rise = 6.0D * (this.risingDuration - this.phaseTick)
                            / (this.risingDuration * (this.risingDuration + 1.0D));
                    this.setPos(this.getX(), this.getY() + rise, this.getZ());
                    ++this.phaseTick;
                    if (this.phaseTick >= this.risingDuration) {
                        this.phase = Phase.HOVER;
                        this.phaseTick = 0;
                    }
                }
                case HOVER -> {
                    ++this.phaseTick;
                    if (this.phaseTick >= this.hoverDuration) {
                        this.phase = Phase.DASH;
                        this.phaseTick = 0;
                        this.dashSpeed = 0.0D;
                        {
                            Vector3f v3 = this.entityData.get(DASH_DIRECTION);
                            this.dashDirection = new Vec3(v3.x(), v3.y(), v3.z());
                        }
                    }
                }
                case DASH -> {
                    ++this.phaseTick;
                    if (this.dashSpeed < this.maxDashSpeed) {
                        this.dashSpeed = Math.min(this.maxDashSpeed, this.dashSpeed + 0.15D);
                    }

                    {
                        Vector3f v3 = this.entityData.get(DASH_DIRECTION);
                        this.dashDirection = new Vec3(v3.x(), v3.y(), v3.z());
                    }
                    this.setPos(this.getX() + this.dashDirection.x * this.dashSpeed,
                            this.getY() + this.dashDirection.y * this.dashSpeed,
                            this.getZ() + this.dashDirection.z * this.dashSpeed);
                }
            }
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            ++this.tickCount;
            return;
        }
        super.tick();

        if (this.tickCount == 1) {
            this.originY = this.getY();
        }
        if (!this.phaseInitialized) {
            this.initializePhase(24 + this.random.nextInt(13), 12 + this.random.nextInt(17),
                    0.5D + this.random.nextDouble() * 0.3D);
        }
        if (this.tickCount > 2400) {
            this.discard();
            return;
        }
        BlockState state = this.getBlockState();
        if (!state.isAir() && this.level() instanceof ServerLevel serverLevel) {
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
            switch (this.phase) {
                case RISING -> {
                    double rise = 6.0D * (this.risingDuration - this.phaseTick)
                            / (this.risingDuration * (this.risingDuration + 1.0D));
                    this.setPos(this.getX(), this.getY() + rise, this.getZ());
                    ++this.phaseTick;
                    serverLevel.sendParticles(option, this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(),
                            1, 0.12D, 0.08D, 0.12D, 0.04D);
                    if (this.phaseTick % 3 == 0) {
                        serverLevel.sendParticles(option, this.getX(), this.originY + 0.15D, this.getZ(), 3, 0.55D,
                                0.15D, 0.55D, 0.06D);
                    }
                    if (this.phaseTick >= this.risingDuration) {
                        this.phase = Phase.HOVER;
                        this.setPhase(1);
                        this.phaseTick = 0;
                    }
                }
                case HOVER -> {
                    ++this.phaseTick;
                    double angle = this.tickCount * 0.25D;
                    double orbitY = this.getY() + this.getBbHeight() / 2.0D + Math.sin(this.tickCount * 0.15D) * 0.15D;
                    serverLevel.sendParticles(option, this.getX() + Math.cos(angle) * 0.55D, orbitY,
                            this.getZ() + Math.sin(angle) * 0.55D, 1, 0.0D, 0.0D, 0.0D, 0.02D);
                    if (this.phaseTick >= this.hoverDuration) {
                        this.phase = Phase.DASH;
                        this.setPhase(2);
                        this.phaseTick = 0;
                        this.dashSpeed = 0.0D;
                        LivingEntity target = this.getTargetEntity();
                        if (target != null && target.isAlive()) {
                            Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() / 2.0D, 0.0D)
                                    .subtract(this.position());
                            double length = toTarget.length();
                            this.dashDirection = length > 0.0D ? toTarget.scale(1.0D / length)
                                    : new Vec3(0.0D, -1.0D, 0.0D);
                        } else {
                            this.dashDirection = new Vec3(0.0D, -1.0D, 0.0D);
                        }

                        this.entityData.set(DASH_DIRECTION, new Vector3f((float) this.dashDirection.x,
                                (float) this.dashDirection.y, (float) this.dashDirection.z));
                    }
                }
                case DASH -> {
                    ++this.phaseTick;
                    if (this.dashSpeed < this.maxDashSpeed) {
                        this.dashSpeed = Math.min(this.maxDashSpeed, this.dashSpeed + 0.15D);
                    }
                    Vec3 delta = this.dashDirection.scale(this.dashSpeed);
                    this.setDeltaMovement(delta);
                    if (this.phaseTick > 0) {
                        HitResult hit = this.findPathHit(this.position(), this.position().add(delta));
                        if (hit != null) {
                            this.onImpact(hit);
                            return;
                        }
                    }
                    this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
                    serverLevel.sendParticles(option, this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(),
                            2, 0.15D, 0.1D, 0.15D, 0.05D);
                    double trail = this.dashSpeed * 0.35D;
                    serverLevel.sendParticles(option, this.getX() - this.dashDirection.x * trail,
                            this.getY() + this.getBbHeight() / 2.0D - this.dashDirection.y * trail,
                            this.getZ() - this.dashDirection.z * trail, 1, -this.dashDirection.x * 0.08D,
                            -this.dashDirection.y * 0.08D, -this.dashDirection.z * 0.08D, 0.08D);
                }
            }
        }
    }

    @Nullable
    private HitResult findPathHit(Vec3 start, Vec3 end) {

        BlockPos startPos = BlockPos.containing(start);
        boolean embedded = !this.level().getBlockState(startPos).getCollisionShape(this.level(), startPos).isEmpty();
        if (!embedded) {
            BlockHitResult blockHit = this.level()
                    .clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (blockHit.getType() != HitResult.Type.MISS) {
                return blockHit;
            }
        }
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        LivingEntity owner = this.getOwnerEntity();
        AABB pathAABB = this.getBoundingBox().expandTowards(end.subtract(start)).inflate(0.1D);
        Entity bestEntity = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity entity : serverLevel.getEntities(this, pathAABB, this::canHitEntity)) {
            if (entity == owner) {
                continue;
            }

            double dist = entity.getBoundingBox().getCenter().distanceToSqr(start);
            if (dist < bestDist) {
                bestDist = dist;
                bestEntity = entity;
            }
        }
        return bestEntity != null ? new EntityHitResult(bestEntity) : null;
    }

    private boolean canHitEntity(Entity target) {
        if (target instanceof ImpactBlockEntity) {
            return false;
        }
        if (!target.canBeHitByProjectile()) {
            return false;
        }
        LivingEntity owner = this.getOwnerEntity();
        if (target == this || target == owner) {
            return false;
        }
        if (owner != null && MobUtil.areAllies(owner, target)) {
            return false;
        }
        return true;
    }

    private void onImpact(HitResult hit) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            this.discard();
            return;
        }
        BlockState state = this.getBlockState();
        float speed = (float) this.getDeltaMovement().length();
        float hardness = 1 + state.getDestroySpeed(serverLevel, this.blockPosition());
        if (hardness <= 0.0F) {
            hardness = 1.0F;
        }
        float damage = Math.max(1.0F, Mth.ceil(speed * hardness * 3.0F)) + this.getExtraDamage();
        LivingEntity owner = this.getOwnerEntity();
        DamageSource damageSource = this.damageSources().flyIntoWall();
        boolean magma = state.is(Blocks.MAGMA_BLOCK);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(1.5D))) {
            if (target != owner && (owner == null || !MobUtil.areAllies(owner, target))) {
                if (target.hurt(damageSource, damage)) {
                    if (magma) {
                        target.setSecondsOnFire(5);
                        target.hurt(this.damageSources().onFire(), 2.0F);
                    }
                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    target.knockback(0.5D, dx, dz);
                }
            }
        }
        ServerParticleUtil.circularParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, state),
                this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 1.5F);
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX(),
                this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 14, 0.45D, 0.5D, 0.45D, 0.4D);
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX(),
                this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 6, 0.2D, 0.25D, 0.2D, 0.15D);
        ColorUtil colorUtil = new ColorUtil(state.getMapColor(serverLevel, this.blockPosition()).col);
        serverLevel.sendParticles(
                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 2.0F, 1),
                this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.playSound(null, this.blockPosition(), state.getSoundType().getBreakSound(), SoundSource.BLOCKS,
                1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    private enum Phase {
        RISING,
        HOVER,
        DASH
    }
}
