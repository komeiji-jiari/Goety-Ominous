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

    /**
     * 飞行方向，作为数据同步，而不是让客户端从 yaw/pitch 反推。
     *
     * <p>🔴 客户端拿到的旋转是「插值后」的值（每 tick 只覆盖剩余差的 1/3），服务端用的是精确值，两边算
     * 出来的方向必然不同；客户端自己也按这个方向积分走路，于是整条弹道和服务器越差越远，每收到一个位置
     * 包就被拽回去一次 —— 表现出来就是星和预警线一起乱抖（主人 11-13 报的）。
     *
     * <p>方向一旦是同一份数据，客户端积分和服务端就逐 tick 一致，位置包只是确认，不再有拉扯。
     */
    private static final EntityDataAccessor<Float> DATA_AIM_X = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_AIM_Y = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_AIM_Z = SynchedEntityData.defineId(
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

    /**
     * 渲染插值用的两份方向：上一 tick 的和这一 tick 的。只在客户端滚动，服务端不读，也不参与同步。
     *
     * <p>🔴 <b>为什么需要。</b>{@link #getAim()} 是同步数据，客户端每 tick 收到一次新值 —— 也就是每秒
     * 20 个离散方向。追踪时每 tick 的转角能到十几度，直接拿最新值算角度，预警线（默认 48 格长）和星体
     * 朝向就会以 20 Hz 的台阶跳过去；1° 在 48 格端点上就是 0.84 格，所以线看起来比星还晃。
     *
     * <p>位置早就有现成的插值（{@code Mth.lerp(partialTicks, xo, x)}），方向没有。这里补上同一套：每
     * tick 把上一份推进 prev，渲染时在两者之间按 partialTicks 取中间值。时间基准与位置一致，线和星
     * 始终对着同一个瞬间。
     */
    private Vec3 aimPrev = Vec3.ZERO;
    private Vec3 aimCur = Vec3.ZERO;
    private boolean aimSeeded;

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
        this.setAim(dir);
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
        this.entityData.define(DATA_AIM_X, 0.0F);
        this.entityData.define(DATA_AIM_Y, 0.0F);
        this.entityData.define(DATA_AIM_Z, 1.0F);
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

    /**
     * 同步过来的飞行方向。两端算的是同一份数，客户端不必再从（滞后的）旋转里反推。
     *
     * <p>长度不保证为 1：插值/同步不会改长度，但读出来仍然归一一下，免得某次写入的是零向量让方向塌掉。
     */
    public Vec3 getAim() {
        Vec3 dir = new Vec3(this.entityData.get(DATA_AIM_X), this.entityData.get(DATA_AIM_Y),
                this.entityData.get(DATA_AIM_Z));
        return dir.lengthSqr() < 1.0E-6D ? this.getLookAngle() : dir.normalize();
    }

    /** 客户端每 tick 滚动一次渲染用的方向。调用点在 {@link #tick()} 里、积分位置之前。 */
    private void rollAim() {
        Vec3 now = this.getAim();
        if (!this.aimSeeded) {
            this.aimPrev = now;
            this.aimCur = now;
            this.aimSeeded = true;
            return;
        }
        this.aimPrev = this.aimCur;
        this.aimCur = now;
    }

    /**
     * 渲染用的方向：上一 tick 与这一 tick 之间的中间值。
     *
     * <p>两份都是单位向量，线性混合再归一化，等于球面上的大圆插值 —— 转角小的时候与角度插值等价，
     * 又不必像 {@code Mth.rotLerp} 那样单独处理 ±180° 回绕。它和 {@code getPosition(partialTicks)}
     * 用的是同一个 partialTicks，所以线的起点和指向永远属于同一时刻。
     */
    public Vec3 getRenderAim(float partialTicks) {
        if (!this.aimSeeded) {
            // 第一帧还没有上一份，直接用当前值，免得插出一根来路不明的方向。
            return this.getAim();
        }
        Vec3 mixed = this.aimPrev.scale(1.0D - partialTicks).add(this.aimCur.scale(partialTicks));
        return mixed.lengthSqr() < 1.0E-8D ? this.aimCur : mixed.normalize();
    }

    /** 写方向，同时把实体自己的旋转对齐过去 —— 两者从这一刻起不再各算各的。 */
    private void setAim(Vec3 dir) {
        if (dir.lengthSqr() < 1.0E-6D) {
            return;
        }
        Vec3 unit = dir.normalize();
        this.entityData.set(DATA_AIM_X, (float) unit.x);
        this.entityData.set(DATA_AIM_Y, (float) unit.y);
        this.entityData.set(DATA_AIM_Z, (float) unit.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            // 先滚一次渲染方向，再积分位置：这样这一步用的方向和这一步渲染出来的方向是同一份。
            this.rollAim();
            if (this.getState() == FLIGHT) {
                // 用同步过来的那一份方向，而不是从（滞后的）旋转反推，服务端与客户端的位移才逐 tick 相同。
                Vec3 motion = this.getAim().scale(this.getSpeed());
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
                this.setAim(dir);
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
                this.setAim(dir);
            }
        }
    }

    private void launch() {
        this.setState(FLIGHT);
        this.noPhysics = false;
        this.setDeltaMovement(this.getAim().scale(this.getSpeed()));
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
        // 方向不是存盘字段：读回来时按实体自己的旋转重建一次，免得同步数据停在默认的 (0,0,1)。
        this.setAim(this.aimDir());
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
