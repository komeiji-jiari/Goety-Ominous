package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.UUID;

/**
 * 法师水浪:以 Alex's Caves 原版 WaveEntity 为蓝本重写。
 *
 * 修正旧移植版的两个 bug:
 *  1. waiting(蓄力)阶段原版是 setInvisible(true) 隐身蓄力,旧版误写成 setNoGravity(true),
 *     导致蓄力期水浪会可见地飘在原地,且后续永远不恢复重力;
 *  2. 重力判定原版是 isInWaterOrBubble(),旧版误写成 isInWater()。
 *
 * 友伤过滤改用 PhantomArrow.canHitEntity 的策略(当前目标优先放行、主人坐骑豁免、
 * Goety 盟友豁免、同主链 IOwned 豁免),并保留 AC 原版"不伤深潜者同类(DeepOneBaseEntity)"规则。
 * 未移植 PhantomArrow 中的 Enemy 敌我判定:法师本身是 Monster(Enemy),
 * 若保留会导致水浪打不中任何怪物。
 */
public class DeepOneMageServantWave extends Entity {

    private static final EntityDataAccessor<Boolean> SLAMMING = SynchedEntityData.defineId(DeepOneMageServantWave.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(DeepOneMageServantWave.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITING_TICKS = SynchedEntityData.defineId(DeepOneMageServantWave.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(DeepOneMageServantWave.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WAVE_SCALE = SynchedEntityData.defineId(DeepOneMageServantWave.class, EntityDataSerializers.FLOAT);

    private LivingEntity owner;
    private UUID ownerUUID;
    private float slamProgress;
    private float prevSlamProgress;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    public int activeWaveTicks;

    public DeepOneMageServantWave(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public DeepOneMageServantWave(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WAVE.get(), level);
        this.setOwner(shooter);
    }

    public DeepOneMageServantWave(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WAVE.get(), level);
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    public void setOwner(LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }

    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }
        return this.owner;
    }

    public float getSlamAmount(float partialTick) {
        return (this.prevSlamProgress + (this.slamProgress - this.prevSlamProgress) * partialTick) * 0.1F;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
        if (compound.contains("Lifespan")) {
            this.setLifespan(compound.getInt("Lifespan"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
        compound.putInt("Lifespan", this.getLifespan());
    }

    @Override
    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    @Override
    public void setYRot(float yRot) {
        this.entityData.set(Y_ROT, yRot);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(LIFESPAN, lifespan);
    }

    public int getWaitingTicks() {
        return this.entityData.get(WAITING_TICKS);
    }

    public void setWaitingTicks(int waitingTicks) {
        this.entityData.set(WAITING_TICKS, waitingTicks);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public void setSlamming(boolean slamming) {
        this.entityData.set(SLAMMING, slamming);
    }

    public float getWaveScale() {
        return this.entityData.get(WAVE_SCALE);
    }

    public void setWaveScale(float waveScale) {
        this.entityData.set(WAVE_SCALE, waveScale);
    }

    private void spawnParticleAt(float x, float y, float z, ParticleOptions particle) {
        Vec3 vec3 = new Vec3((double) z, (double) x, (double) y).yRot(-this.getYRot() * ((float) Math.PI / 180F));
        this.level().addParticle(particle, this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z,
                this.getDeltaMovement().x, 0.1D, this.getDeltaMovement().z);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevSlamProgress = this.slamProgress;
        if (this.getWaitingTicks() > 0) {
            if (!this.level().isClientSide) {
                this.setWaitingTicks(this.getWaitingTicks() - 1);
            }
            this.setInvisible(true);
            return;
        }
        if (this.isInvisible()) {
            this.setInvisible(false);
        }
        if (this.isSlamming()) {
            if (this.slamProgress < 10.0F) {
                this.slamProgress++;
            }
        }
        if (this.isSlamming() && this.slamProgress == 10.0F) {
            this.discard();
        }
        if (!this.isNoGravity() && !this.isInWaterOrBubble()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        float f = Math.min((float) this.activeWaveTicks / 10.0F, 1.0F);
        Vec3 boost = new Vec3(0.0D, 0.0D, f * f * 0.2F).yRot(-this.getYRot() * ((float) Math.PI / 180F));
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
            for (int i = 0; i < this.getWaveScale(); i++) {
                for (int j = 0; j <= 4; j++) {
                    float xOffset = (float) j / 4.0F - 0.5F + (this.random.nextFloat() - 0.5F) * 0.2F;
                    this.spawnParticleAt((0.2F + this.random.nextFloat() * 0.2F) * this.getWaveScale(),
                            1.2F, xOffset * 1.2F * this.getWaveScale(), ACParticleRegistry.WATER_FOAM.get());
                    this.spawnParticleAt((0.2F + this.random.nextFloat() * 0.2F) * this.getWaveScale(),
                            -0.2F, 1.4F * xOffset * this.getWaveScale(), ParticleTypes.SPLASH);
                }
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (!this.level().isClientSide) {
            this.attackEntities(this.getSlamAmount(1.0F) * 2.0F + 1.0F + this.getWaveScale());
        }
        Vec3 moveVec = this.getDeltaMovement().scale(0.9D).add(boost);
        this.move(MoverType.SELF, moveVec);
        this.setDeltaMovement(moveVec.multiply(0.99D, 0.98D, 0.99D));
        if (this.activeWaveTicks > this.getLifespan()
                || (this.activeWaveTicks > 10 && this.getDeltaMovement().horizontalDistance() < 0.04D)) {
            this.setSlamming(true);
        }
        this.activeWaveTicks++;
    }

    private void attackEntities(float scale) {
        AABB aabb = this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D);
        DamageSource source = this.damageSources().mobProjectile(this, this.owner);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (!this.canHitEntity(entity)) {
                continue;
            }
            entity.hurt(source, scale + 1.0F);
            this.setSlamming(true);
            entity.knockback(0.1D + 0.5D * scale,
                    (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                    (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
        }
    }

    /**
     * 友军伤害避免(移植自 PhantomArrow.canHitEntity):
     *  - 当前目标优先放行:只要命中的实体是法师的 getTarget(),立即允许命中;
     *  - 主人坐骑豁免:不命中法师胯下的坐骑(除非它就是目标);
     *  - 主人自身豁免;
     *  - MobUtil.areAllies 盟友豁免(召唤师、同队伍、其他仆从等);
     *  - 同主链 IOwned 豁免:与法师同属一个召唤主的实体不命中;
     *  - 保留 AC 原版"不伤深潜者同类(DeepOneBaseEntity)"规则。
     */
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof DeepOneBaseEntity) {
            return false;
        }
        if (this.getOwner() != null) {
            Entity owner = this.getOwner();
            if (entity.equals(owner)) {
                return false;
            }
            if (owner instanceof Mob mob) {
                if (mob.getTarget() == entity) {
                    return true;
                }
                if (mob.getVehicle() != null && entity == mob.getVehicle() && mob.getTarget() != entity) {
                    return false;
                }
            }
            if (MobUtil.areAllies(owner, entity)) {
                return false;
            }
            if (entity instanceof Projectile projectile && projectile.getOwner() == owner) {
                return false;
            }
            if (entity instanceof IOwned owned0 && owner instanceof IOwned owned1) {
                return !MobUtil.ownerStack(owned0, owned1);
            }
        }
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (WAVE_SCALE.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getWaveScale());
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
    protected void defineSynchedData() {
        this.entityData.define(SLAMMING, false);
        this.entityData.define(LIFESPAN, 10);
        this.entityData.define(WAITING_TICKS, 0);
        this.entityData.define(Y_ROT, 0.0F);
        this.entityData.define(WAVE_SCALE, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
