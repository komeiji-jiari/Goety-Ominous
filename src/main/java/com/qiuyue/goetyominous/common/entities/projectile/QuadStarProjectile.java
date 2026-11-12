package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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

public class QuadStarProjectile extends Projectile {

    public static final int CHARGE = 0;
    public static final int FLIGHT = 1;
    public static final int STUCK = 2;

    public static final int CHARGE_TICKS = 30;
    public static final int LOCK_TICKS = 10;
    public static final int FLIGHT_LIFE = 100;
    public static final int STUCK_LIFE = 300;
    public static final float DAMAGE = 8.0F;
    public static final double SPEED = 1.35D;
    public static final float TIP_LENGTH = 0.66F;
    public static final float EMBED_DEPTH = 0.34F;

    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(
            QuadStarProjectile.class, EntityDataSerializers.INT);

    private int chargeTick;
    private int flightTick;
    private int stuckTick;

    public QuadStarProjectile(EntityType<? extends QuadStarProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public QuadStarProjectile(Level level, LivingEntity shooter, Vec3 aim) {
        this(ModEntityTypes.QUAD_STAR.get(), level);
        this.setOwner(shooter);
        Vec3 dir = aim.normalize();
        this.setPos(shooter.getEyePosition().add(dir.scale(0.9D)));
        this.setRot(yawOf(dir), pitchOf(dir));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.noPhysics = true;
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
    }

    public int getState() {
        return this.entityData.get(DATA_STATE);
    }

    private void setState(int state) {
        this.entityData.set(DATA_STATE, state);
    }

    public Vec3 aimDir() {
        return Vec3.directionFromRotation(this.getXRot(), this.getYRot());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.getState() == FLIGHT) {
                Vec3 motion = this.aimDir().scale(SPEED);
                this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
            }
            return;
        }
        switch (this.getState()) {
            case CHARGE:
                this.tickCharge();
                break;
            case FLIGHT:
                this.tickFlight();
                break;
            default:
                if (++this.stuckTick > STUCK_LIFE) {
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
        if (this.chargeTick < CHARGE_TICKS - LOCK_TICKS) {
            this.aimAtTarget();
        }
        if (this.chargeTick >= CHARGE_TICKS) {
            this.launch();
        }
    }

    private void aimAtTarget() {
        Entity owner = this.getOwner();
        LivingEntity target = owner instanceof Mob mob ? mob.getTarget() : null;
        Vec3 dir;
        if (target != null && target.isAlive()) {
            dir = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(this.position());
        } else if (owner != null) {
            dir = owner.getLookAngle();
        } else {
            return;
        }
        if (dir.lengthSqr() < 1.0E-6D) {
            return;
        }
        this.setRot(yawOf(dir), pitchOf(dir));
    }

    private void launch() {
        this.setState(FLIGHT);
        this.noPhysics = false;
        this.setDeltaMovement(this.aimDir().scale(SPEED));
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 1.0F, 1.6F);
    }

    private void tickFlight() {
        if (++this.flightTick > FLIGHT_LIFE) {
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
        DamageSource source = this.damageSources().mobProjectile(this,
                owner instanceof LivingEntity livingOwner ? livingOwner : null);
        target.hurt(source, DAMAGE);
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StarState", this.getState());
        tag.putInt("StarChargeTick", this.chargeTick);
        tag.putInt("StarStuckTick", this.stuckTick);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setState(tag.getInt("StarState"));
        this.chargeTick = tag.getInt("StarChargeTick");
        this.stuckTick = tag.getInt("StarStuckTick");
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
