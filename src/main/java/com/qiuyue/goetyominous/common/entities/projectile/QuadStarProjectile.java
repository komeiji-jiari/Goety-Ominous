package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class QuadStarProjectile extends Projectile {

    public static final int CHARGE = 0;
    public static final int FLIGHT = 1;
    public static final int STUCK = 2;

    public static final int DEFAULT_CHARGE_TICKS = 30;
    public static final int DEFAULT_LOCK_TICKS = 10;
    public static final int DEFAULT_FLIGHT_LIFE = 100;
    public static final int DEFAULT_STUCK_LIFE = 300;
    public static final float DEFAULT_DAMAGE = 8.0F;
    public static final float DEFAULT_SPEED = 1.35F;
    public static final float DEFAULT_LINE_LENGTH = 48.0F;
    public static final float TIP_LENGTH = 0.66F;
    public static final float EMBED_DEPTH = 0.34F;

    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CHARGE_TICKS = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPEED = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LINE_LENGTH = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.FLOAT);

    private int chargeTicks = DEFAULT_CHARGE_TICKS;
    private int lockTicks = DEFAULT_LOCK_TICKS;
    private int flightLife = DEFAULT_FLIGHT_LIFE;
    private int stuckLife = DEFAULT_STUCK_LIFE;
    private float flatDamage = DEFAULT_DAMAGE;
    private float healthFraction;
    private int cursedTicks;
    private double maxOwnerDistance;
    private boolean rememberTarget;
    @Nullable
    private UUID targetUUID;
    private int chargeTick;
    private int flightTick;
    private int stuckTick;

    public QuadStarProjectile(EntityType<? extends QuadStarProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public QuadStarProjectile(Level level, LivingEntity shooter, Vec3 aim) {
        this(level, shooter, shooter.getEyePosition().add(normalized(aim, shooter).scale(0.9D)), aim);
    }

    public QuadStarProjectile(Level level, LivingEntity shooter, Vec3 at, @Nullable Vec3 aim) {
        this(ModEntityTypes.QUAD_STAR.get(), level);
        this.setOwner(shooter);
        this.setPos(at.x, at.y, at.z);
        Vec3 dir = aim == null ? initialAim(shooter, at) : normalized(aim, shooter);
        this.setRot(yawOf(dir), pitchOf(dir));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.noPhysics = true;
        if (shooter instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                this.targetUUID = target.getUUID();
            }
        }
    }

    private static Vec3 normalized(Vec3 dir, LivingEntity shooter) {
        return dir.lengthSqr() < 1.0E-6D ? shooter.getLookAngle() : dir.normalize();
    }

    private static Vec3 initialAim(LivingEntity shooter, Vec3 at) {
        if (shooter instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                Vec3 dir = target.getBoundingBox().getCenter().subtract(at);
                if (dir.lengthSqr() > 1.0E-6D) {
                    return dir.normalize();
                }
            }
        }
        return shooter.getLookAngle();
    }

    public static float yawOf(Vec3 dir) {
        return (float) (Mth.atan2(-dir.x, dir.z) * (180.0D / Math.PI));
    }

    public static float pitchOf(Vec3 dir) {
        Vec3 unit = dir.normalize();
        return (float) (-Math.asin(unit.y) * (180.0D / Math.PI));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, CHARGE);
        this.entityData.define(DATA_CHARGE_TICKS, DEFAULT_CHARGE_TICKS);
        this.entityData.define(DATA_SPEED, DEFAULT_SPEED);
        this.entityData.define(DATA_LINE_LENGTH, DEFAULT_LINE_LENGTH);
    }

    public int getState() {
        return this.entityData.get(DATA_STATE);
    }

    private void setState(int state) {
        this.entityData.set(DATA_STATE, state);
    }

    public int getChargeTicks() {
        return this.entityData.get(DATA_CHARGE_TICKS);
    }

    public float getSpeed() {
        return this.entityData.get(DATA_SPEED);
    }

    public float getLineLength() {
        return this.entityData.get(DATA_LINE_LENGTH);
    }

    public QuadStarProjectile chargeTicks(int ticks) {
        this.chargeTicks = Math.max(1, ticks);
        this.entityData.set(DATA_CHARGE_TICKS, this.chargeTicks);
        return this;
    }

    public QuadStarProjectile lockTicks(int ticks) {
        this.lockTicks = Math.max(0, ticks);
        return this;
    }

    public QuadStarProjectile flightLife(int ticks) {
        this.flightLife = Math.max(1, ticks);
        return this;
    }

    public QuadStarProjectile stuckLife(int ticks) {
        this.stuckLife = Math.max(0, ticks);
        return this;
    }

    public QuadStarProjectile speed(double speed) {
        this.entityData.set(DATA_SPEED, (float) Math.max(0.01D, speed));
        return this;
    }

    public QuadStarProjectile lineLength(float length) {
        this.entityData.set(DATA_LINE_LENGTH, Math.max(0.0F, length));
        return this;
    }

    public QuadStarProjectile maxOwnerDistance(double distance) {
        this.maxOwnerDistance = Math.max(0.0D, distance);
        return this;
    }

    public QuadStarProjectile damage(float flat, float healthFraction) {
        this.flatDamage = Math.max(0.0F, flat);
        this.healthFraction = Math.max(0.0F, healthFraction);
        return this;
    }

    public QuadStarProjectile cursedTicks(int ticks) {
        this.cursedTicks = Math.max(0, ticks);
        return this;
    }

    public QuadStarProjectile rememberTarget(boolean remember) {
        this.rememberTarget = remember;
        return this;
    }

    public Vec3 aimDir() {
        return Vec3.directionFromRotation(this.getXRot(), this.getYRot());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.getState() == FLIGHT) {
                Vec3 motion = this.aimDir().scale(this.getSpeed());
                this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
            }
            return;
        }
        if (this.maxOwnerDistance > 0.0D) {
            Entity owner = this.getOwner();
            if (owner != null
                    && this.distanceToSqr(owner) > this.maxOwnerDistance * this.maxOwnerDistance) {
                this.discard();
                return;
            }
        }
        switch (this.getState()) {
            case CHARGE:
                this.tickCharge();
                break;
            case FLIGHT:
                this.tickFlight();
                break;
            default:
                if (++this.stuckTick > this.stuckLife) {
                    this.discard();
                }
                break;
        }
    }

    private void tickCharge() {
        Entity owner = this.getOwner();
        if (owner != null && !owner.isAlive()) {
            this.discard();
            return;
        }
        ++this.chargeTick;
        if (this.chargeTick < this.chargeTicks) {
            if (this.chargeTick < this.chargeTicks - this.lockTicks) {
                this.aimAtTarget();
            }
            return;
        }
        if (this.lockTicks <= 0) {
            this.aimAtTarget();
        }
        this.launch();
    }

    @Nullable
    private LivingEntity resolveTarget() {
        Entity owner = this.getOwner();
        if (this.rememberTarget && this.targetUUID != null && this.level() instanceof ServerLevel server) {
            Entity stored = server.getEntity(this.targetUUID);
            if (stored instanceof LivingEntity living && living.isAlive()) {
                return living;
            }
        }
        if (owner instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                if (this.rememberTarget) {
                    this.targetUUID = target.getUUID();
                }
                return target;
            }
        }
        return null;
    }

    private void aimAtTarget() {
        LivingEntity target = this.resolveTarget();
        if (target != null) {
            Vec3 dir = target.getBoundingBox().getCenter().subtract(this.position());
            if (dir.lengthSqr() > 1.0E-6D) {
                this.setRot(yawOf(dir), pitchOf(dir));
            }
            return;
        }
        if (this.rememberTarget) {
            return;
        }
        Entity owner = this.getOwner();
        if (owner != null) {
            Vec3 dir = owner.getLookAngle();
            if (dir.lengthSqr() > 1.0E-6D) {
                this.setRot(yawOf(dir), pitchOf(dir));
            }
        }
    }

    private void launch() {
        this.setState(FLIGHT);
        this.noPhysics = false;
        this.setDeltaMovement(this.aimDir().scale(this.getSpeed()));
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 1.0F, 1.6F);
    }

    private void tickFlight() {
        if (++this.flightTick > this.flightLife) {
            this.discard();
            return;
        }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hit)) {
            this.onHit(hit);
            if (this.isRemoved()) {
                return;
            }
        }
        Vec3 motion = this.getDeltaMovement();
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) {
            return false;
        }
        Entity owner = this.getOwner();
        if (target == owner) {
            return false;
        }
        return !(owner instanceof LivingEntity livingOwner) || !(target instanceof LivingEntity livingTarget)
                || !MobUtil.areAllies(livingOwner, livingTarget);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.stuckLife <= 0) {
            this.discard();
            return;
        }
        Vec3 dir = this.aimDir();
        Vec3 spot = result.getLocation().subtract(dir.scale(TIP_LENGTH - EMBED_DEPTH));
        this.setPos(spot.x, spot.y, spot.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        this.setState(STUCK);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.TRIDENT_HIT_GROUND, SoundSource.NEUTRAL, 0.9F, 1.7F);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (target instanceof LivingEntity living) {
            DamageSource source = this.damageSources().mobProjectile(this,
                    owner instanceof LivingEntity livingOwner ? livingOwner : null);
            float amount = this.flatDamage + living.getMaxHealth() * this.healthFraction;
            boolean hurt = living.hurt(source, amount);
            if (hurt && this.cursedTicks > 0 && living.isAlive()) {
                living.addEffect(new MobEffectInstance(
                        GoetyEffects.CURSED.get(), this.cursedTicks, 0, false, false));
            }
        }
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StarState", this.getState());
        tag.putInt("StarChargeTick", this.chargeTick);
        tag.putInt("StarFlightTick", this.flightTick);
        tag.putInt("StarStuckTick", this.stuckTick);
        tag.putInt("StarChargeTicks", this.chargeTicks);
        tag.putInt("StarLockTicks", this.lockTicks);
        tag.putInt("StarFlightLife", this.flightLife);
        tag.putInt("StarStuckLife", this.stuckLife);
        tag.putFloat("StarSpeed", this.getSpeed());
        tag.putFloat("StarLineLength", this.getLineLength());
        tag.putDouble("StarMaxOwnerDistance", this.maxOwnerDistance);
        tag.putFloat("StarFlatDamage", this.flatDamage);
        tag.putFloat("StarHealthFraction", this.healthFraction);
        tag.putInt("StarCursedTicks", this.cursedTicks);
        tag.putBoolean("StarRememberTarget", this.rememberTarget);
        if (this.targetUUID != null) {
            tag.putUUID("StarTarget", this.targetUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setState(tag.getInt("StarState"));
        this.chargeTick = tag.getInt("StarChargeTick");
        this.flightTick = tag.getInt("StarFlightTick");
        this.stuckTick = tag.getInt("StarStuckTick");
        if (tag.contains("StarChargeTicks")) {
            this.chargeTicks(tag.getInt("StarChargeTicks"));
        }
        if (tag.contains("StarLockTicks")) {
            this.lockTicks(tag.getInt("StarLockTicks"));
        }
        if (tag.contains("StarFlightLife")) {
            this.flightLife(tag.getInt("StarFlightLife"));
        }
        if (tag.contains("StarStuckLife")) {
            this.stuckLife(tag.getInt("StarStuckLife"));
        }
        if (tag.contains("StarSpeed")) {
            this.speed(tag.getFloat("StarSpeed"));
        }
        if (tag.contains("StarLineLength")) {
            this.lineLength(tag.getFloat("StarLineLength"));
        }
        if (tag.contains("StarMaxOwnerDistance")) {
            this.maxOwnerDistance(tag.getDouble("StarMaxOwnerDistance"));
        }
        if (tag.contains("StarFlatDamage") || tag.contains("StarHealthFraction")) {
            this.damage(tag.getFloat("StarFlatDamage"), tag.getFloat("StarHealthFraction"));
        }
        if (tag.contains("StarCursedTicks")) {
            this.cursedTicks(tag.getInt("StarCursedTicks"));
        }
        this.rememberTarget = tag.getBoolean("StarRememberTarget");
        if (tag.hasUUID("StarTarget")) {
            this.targetUUID = tag.getUUID("StarTarget");
        }
        this.noPhysics = this.getState() != FLIGHT;
    }

    @Override
    public boolean isPickable() {
        return this.getState() != STUCK;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
