package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.WaterHurtingProjectile;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class WaterBoltProjectile extends WaterHurtingProjectile {

    private static final EntityDataAccessor<Optional<UUID>> ARC_TOWARDS_ENTITY_UUID =
            SynchedEntityData.defineId(WaterBoltProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> BUBBLING =
            SynchedEntityData.defineId(WaterBoltProjectile.class, EntityDataSerializers.BOOLEAN);

    private float damageBonus = 0.0F;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private final Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;
    private boolean spawnedSplash;
    private int wooshSoundTime;
    private int dieIn = -1;
    private boolean playedSplashSound;
    public boolean ricochet;
    public float seekAmount = 0.3F;

    public WaterBoltProjectile(EntityType<? extends WaterBoltProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WaterBoltProjectile(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.WATER_BOLT.get(), level);
        float f = shooter instanceof Player ? 0.3F : 0.1F;
        this.setPos(shooter.getX(), shooter.getEyeY() - (double) f, shooter.getZ());
        this.setOwner(shooter);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARC_TOWARDS_ENTITY_UUID, Optional.empty());
        this.entityData.define(BUBBLING, false);
    }

    @Override
    public float getGravity() {
        return 0.0F;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public void tick() {
        this.baseTick();

        if (!this.level().isClientSide) {
            Entity arcTowards = this.getArcingTowards();
            if (arcTowards != null
                    && (this.tickCount > 3 || (double) this.seekAmount > 0.3)
                    && this.dieIn == -1
                    && this.distanceTo(arcTowards) > 1.5F
                    && (this.tickCount < 20 || (double) this.seekAmount > 0.3 && this.tickCount < 40)) {
                Vec3 arcVec = arcTowards.position()
                        .add(0.0, (double) (0.85F * arcTowards.getBbHeight()), 0.0)
                        .subtract(this.position()).normalize();
                float prevDeltaScale = 1.0F - (this.seekAmount - 0.3F) * 0.3F;
                this.setDeltaMovement(this.getDeltaMovement().scale((double) prevDeltaScale)
                        .add(arcVec.scale((double) this.seekAmount)));
            }
        } else {
            for (int j = 0; j < 3 + this.random.nextInt(2); ++j) {
                this.level().addParticle(
                        !this.isInWaterOrBubble() && !this.isBubbling()
                                ? ParticleTypes.FALLING_WATER : ParticleTypes.BUBBLE_COLUMN_UP,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5),
                        0.0, -0.10000000149011612, 0.0);
            }
        }

        if (this.wooshSoundTime <= 0) {
            this.wooshSoundTime = 30 + this.level().random.nextInt(30);
            this.playSound(ACSoundRegistry.SEA_STAFF_WOOSH.get());
        }

        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        this.updateRotation();
        if (this.level().getBlockStates(this.getBoundingBox())
                .noneMatch(BlockBehaviour.BlockStateBase::isAir) && !this.isInWaterOrBubble()) {
            this.discard();
        } else {
            this.setDeltaMovement(vec3.scale(0.8999999761581421));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.07000000029802322, 0.0));
            }

            if (this.level().isClientSide) {
                if (this.lSteps > 0) {
                    double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                    double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                    double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                    this.setYRot(Mth.wrapDegrees((float) this.lyr));
                    this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                    --this.lSteps;
                    this.setPos(d5, d6, d7);
                } else {
                    this.reapplyPosition();
                }
            } else {
                this.setPos(d0, d1, d2);
            }
        }

        Vec3 trailAt = this.position().add(0.0, (double) (this.getBbHeight() / 2.0F), 0.0);
        if (this.trailPointer == -1) {
            for (int i = 0; i < this.trailPositions.length; ++i) {
                this.trailPositions[i] = trailAt;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        if (this.dieIn > 0) {
            --this.dieIn;
            if (this.dieIn == 0) {
                this.discard();
            }
        }
    }

    @Override
    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);
        if (!this.spawnedSplash && this.level() instanceof ServerLevel serverLevel) {
            this.spawnedSplash = true;
            BlockPos pos;
            for (pos = this.blockPosition().above();
                 this.level().isEmptyBlock(pos) && pos.getY() > this.level().getMinBuildHeight();
                 pos = pos.below()) {
            }
            serverLevel.sendParticles((SimpleParticleType) ACParticleRegistry.BIG_SPLASH.get(),
                    this.getX(), (double) ((float) pos.getY() + 1.5F), this.getZ(),
                    0, 1.2999999523162842, 1.0, 0.0, 1.0);
        }
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale((double) partialTick));
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = (double) yr;
        this.lxr = (double) xr;
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
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.playedSplashSound) {
            this.playedSplashSound = true;
            this.playSound(ACSoundRegistry.SEA_STAFF_HIT.get());
        }
        if (!this.level().isClientSide && !this.ownedBy(hitResult.getEntity()) && this.tickCount > 2) {
            this.damageMobs();
            if (this.dieIn == -1) {
                this.dieIn = 5;
            }
        }
    }

    private void damageMobs() {
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().mobProjectile(this,
                owner instanceof LivingEntity living ? living : null);
        AABB bashBox = this.getBoundingBox().inflate(2.0, 2.0, 2.0);
        Entity lastHitMob = null;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (entity instanceof DeepOneBaseEntity) {
                continue;
            }
            if (owner != null && (entity.is(owner) || entity.isAlliedTo(owner))) {
                continue;
            }
            lastHitMob = entity;
            if (entity.hurt(source, 3.0F + this.damageBonus) && this.isBubbling()) {
                entity.addEffect(new MobEffectInstance(ACEffectRegistry.BUBBLED.get(), 200));
                if (!entity.level().isClientSide) {
                    AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(
                            entity.getId(), this.getId(), 1, 200));
                }
            }
        }
        if (this.ricochet && lastHitMob != null) {
            this.ricochet = false;
            this.onRicochetHit(lastHitMob);
        }
    }

    private void onRicochetHit(Entity hitBy) {
        Entity owner = this.getOwner();
        AABB searchBox = hitBy.getBoundingBox().inflate(32.0, 32.0, 32.0);
        Entity ricochetTo = null;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (entity instanceof DeepOneBaseEntity) {
                continue;
            }
            if (entity.is(hitBy)) {
                continue;
            }
            if (owner != null && (entity.is(owner) || entity.isAlliedTo(owner))) {
                continue;
            }
            if (entity.distanceTo(hitBy) <= 3.0D) {
                continue;
            }
            if (ricochetTo != null && !(ricochetTo.distanceTo(hitBy) > entity.distanceTo(hitBy))) {
                continue;
            }
            ricochetTo = entity;
        }
        if (ricochetTo != null && owner instanceof LivingEntity living) {
            WaterBoltProjectile bolt = new WaterBoltProjectile(this.level(), living);
            bolt.copyPosition(this);
            bolt.setArcingTowards(ricochetTo.getUUID());
            Vec3 arcVec = ricochetTo.position()
                    .add(0.0, (double) (0.3F + 1.0F * ricochetTo.getBbHeight()), 0.0)
                    .subtract(this.position()).normalize();
            bolt.setDeltaMovement(bolt.getDeltaMovement().add(arcVec));
            bolt.setBubbling(this.isBubbling());
            this.level().addFreshEntity(bolt);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
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

    public boolean isBubbling() {
        return this.entityData.get(BUBBLING);
    }

    public void setBubbling(boolean bool) {
        this.entityData.set(BUBBLING, bool);
    }

    public Entity getArcingTowards() {
        UUID id = this.entityData.get(ARC_TOWARDS_ENTITY_UUID).orElse(null);
        if (id == null) {
            return null;
        }
        return this.level() instanceof ServerLevel serverLevel ? serverLevel.getEntity(id) : null;
    }

    public void setArcingTowards(@Nullable UUID arcingTowards) {
        this.entityData.set(ARC_TOWARDS_ENTITY_UUID, Optional.ofNullable(arcingTowards));
    }

    public float getDamageBonus() {
        return this.damageBonus;
    }

    public void setDamageBonus(float damageBonus) {
        this.damageBonus = damageBonus;
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.setBubbling(tag.getBoolean("Bubbling"));
        this.damageBonus = tag.getFloat("DamageBonus");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("Bubbling", this.isBubbling());
        compoundTag.putFloat("DamageBonus", this.damageBonus);
    }
}
