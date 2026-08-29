package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.Optional;
import java.util.UUID;

/**
 * 法师水弹:忠实移植 Alex's Caves 原版 WaterBoltEntity 的完整行为(制导、粒子、
 * 尾迹 ring buffer、lerp 插值、水面飞溅、dieIn 计时、hit 后范围伤害 + BUBBLED 效果),
 * 仅在伤害过滤中加入 Goety 的 MobUtil.areAllies,使召唤师的其他仆从不再被误伤。
 * damageMobs/onRicochetHit 内的友伤过滤与 DeepOneMageServantWave.attackEntities 保持一致。
 */
public class DeepOneMageServantWaterBolt extends Projectile {

    private static final EntityDataAccessor<Boolean> BUBBLING = SynchedEntityData.defineId(DeepOneMageServantWaterBolt.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> ARC_TOWARDS_ENTITY_UUID = SynchedEntityData.defineId(DeepOneMageServantWaterBolt.class, EntityDataSerializers.OPTIONAL_UUID);

    private Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;
    private boolean spawnedSplash;
    private int wooshSoundTime;
    private int dieIn = -1;
    private boolean ricochet;
    private float seekAmount = 0.3F;
    private boolean playedSplashSound;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    public DeepOneMageServantWaterBolt(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public DeepOneMageServantWaterBolt(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WATER_BOLT.get(), level);
        float f = shooter instanceof Player ? 0.3F : 0.1F;
        this.setPos(shooter.getX(), shooter.getEyeY() - f, shooter.getZ());
        this.setOwner(shooter);
    }

    public DeepOneMageServantWaterBolt(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WATER_BOLT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BUBBLING, false);
        this.entityData.define(ARC_TOWARDS_ENTITY_UUID, Optional.empty());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Entity target = this.getArcingTowards();
            if (target != null) {
                if (this.tickCount > 3 || (double) this.seekAmount > 0.3) {
                    if (this.dieIn == -1 && this.distanceTo(target) > 1.5F) {
                        if (this.tickCount < 20 || ((double) this.seekAmount > 0.3 && this.tickCount < 40)) {
                            Vec3 vec3 = target.position().add(0.0D, (double) (target.getBbHeight() * 0.85F), 0.0D).subtract(this.position()).normalize();
                            float f = 1.0F - (this.seekAmount - 0.3F) * 0.3F;
                            this.setDeltaMovement(this.getDeltaMovement().scale(f).add(vec3.scale(this.seekAmount)));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 3 + this.random.nextInt(2); i++) {
            this.level().addParticle(this.isInWaterOrBubble() || this.isBubbling() ? ParticleTypes.BUBBLE_COLUMN_UP : ParticleTypes.FALLING_WATER,
                    this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, -0.1D, 0.0D);
        }
        if (this.wooshSoundTime <= 0) {
            this.wooshSoundTime = 30 + this.level().random.nextInt(30);
            this.playSound(ACSoundRegistry.SEA_STAFF_WOOSH.get());
        }
        Vec3 movement = this.getDeltaMovement();
        double nx = this.getX() + movement.x;
        double ny = this.getY() + movement.y;
        double nz = this.getZ() + movement.z;
        this.updateRotation();
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockState::isAir) && !this.isInWaterOrBubble()) {
            this.discard();
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.07D, 0.0D));
        }
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
            this.setPos(nx, ny, nz);
        }
        Vec3 trailPos = this.position().add(0.0D, (double) (this.getBbHeight() / 2.0F), 0.0D);
        if (this.trailPointer == -1) {
            for (int i = 0; i < this.trailPositions.length; i++) {
                this.trailPositions[i] = trailPos;
            }
        }
        this.trailPointer++;
        if (this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailPos;
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, (entity) -> this.canHitEntity(entity));
        if (hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
            if (this.dieIn > 0) {
                this.dieIn--;
                if (this.dieIn == 0) {
                    this.discard();
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!this.playedSplashSound) {
            this.playedSplashSound = true;
            this.playSound(ACSoundRegistry.SEA_STAFF_HIT.get());
        }
        if (!this.level().isClientSide) {
            if (this.ownedBy(hit.getEntity())) {
                return;
            }
            if (this.tickCount <= 2) {
                return;
            }
            this.damageMobs();
            if (this.dieIn == -1) {
                this.dieIn = 5;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.playedSplashSound) {
            this.playedSplashSound = true;
            this.playSound(ACSoundRegistry.SEA_STAFF_HIT.get());
        }
        if (!this.level().isClientSide) {
            this.damageMobs();
            if (this.dieIn == -1) {
                this.dieIn = 5;
            }
        }
    }

    private void damageMobs() {
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().mobProjectile(this, owner instanceof LivingEntity ? (LivingEntity) owner : null);
        AABB box = this.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
        LivingEntity candidate = null;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (entity instanceof DeepOneBaseEntity) {
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
            candidate = entity;
            if (entity.hurt(source, 3.0F)) {
                if (this.isBubbling()) {
                    // 仅在服务端真正挂上 BUBBLED 时才发客户端视觉消息,避免目标免疫时客户端仍显示泡泡却无清除源。
                    if (entity.addEffect(new MobEffectInstance(ACEffectRegistry.BUBBLED.get(), 200))) {
                        // 客户端泡膜视觉:原版 1.20.1 的效果列表不同步给普通追踪玩家(仅乘客/玩家自身会收
                        // ClientboundUpdateMobEffectPacket),故与 AC 原版一致,通过 UpdateEffectVisualityEntityMessage
                        // 让所有玩家在客户端本地 addEffect BUBBLED,驱动 ACPotionEffectLayer 画泡膜。
                        // 到期清除由 BubbledVisualCleanupHandler 在服务端效果 Expired/Remove 时发 remove 消息完成。
                        AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 1, 200, false));
                    }
                }
            }
        }
        if (this.ricochet && candidate != null) {
            this.ricochet = false;
            this.onRicochetHit(candidate);
        }
    }

    private void onRicochetHit(Entity target) {
        Entity owner = this.getOwner();
        AABB box = target.getBoundingBox().inflate(32.0D, 32.0D, 32.0D);
        LivingEntity candidate = null;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (entity instanceof DeepOneBaseEntity) {
                continue;
            }
            if (entity.is(target)) {
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
            if (entity.distanceTo(target) > 3.0F) {
                continue;
            }
            if (candidate != null && candidate.distanceTo(target) <= entity.distanceTo(target)) {
                continue;
            }
            candidate = entity;
        }
        if (candidate != null && owner instanceof LivingEntity livingEntity) {
            DeepOneMageServantWaterBolt bolt = new DeepOneMageServantWaterBolt(this.level(), livingEntity);
            bolt.copyPosition(this);
            bolt.setArcingTowards(candidate.getUUID());
            Vec3 vec3 = candidate.position().add(0.0D, (double) (0.3F + 1.0F * candidate.getBbHeight()), 0.0D).subtract(this.position()).normalize();
            bolt.setDeltaMovement(bolt.getDeltaMovement().add(vec3));
            bolt.setBubbling(this.isBubbling());
            this.level().addFreshEntity(bolt);
        }
    }

    public Entity getArcingTowards() {
        UUID uuid = this.entityData.get(ARC_TOWARDS_ENTITY_UUID).orElse(null);
        if (uuid == null) {
            return null;
        } else if (this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid);
        }
        return null;
    }

    public void setArcingTowards(UUID uuid) {
        this.entityData.set(ARC_TOWARDS_ENTITY_UUID, Optional.ofNullable(uuid));
    }

    public boolean isBubbling() {
        return this.entityData.get(BUBBLING);
    }

    public void setBubbling(boolean bubbling) {
        this.entityData.set(BUBBLING, bubbling);
    }

    public Vec3 getTrailPosition(int index, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = (this.trailPointer - index) & 63;
        int j = (this.trailPointer - index - 1) & 63;
        Vec3 vec3 = this.trailPositions[j];
        Vec3 vec31 = this.trailPositions[i];
        return vec31.add(vec3.subtract(vec31).scale(partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!this.spawnedSplash) {
            Level level = this.level();
            if (level instanceof ServerLevel serverLevel) {
                this.spawnedSplash = true;
                BlockPos blockPos = this.blockPosition().above();
                while (level.isEmptyBlock(blockPos) && blockPos.getY() > level.getMinBuildHeight()) {
                    blockPos = blockPos.below();
                }
                serverLevel.sendParticles(ACParticleRegistry.BIG_SPLASH.get(), this.getX(),
                        (double) ((float) blockPos.getY() + 1.5F), this.getZ(), 0, 1.3D, 1.0D, 0.0D, 1.0D);
            }
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

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setBubbling(compound.getBoolean("Bubbling"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Bubbling", this.isBubbling());
    }
}
