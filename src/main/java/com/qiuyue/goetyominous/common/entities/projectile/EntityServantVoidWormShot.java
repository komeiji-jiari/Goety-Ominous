package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.Polarice3.Goety.init.ModSounds;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.network.PlayMessages;

public class EntityServantVoidWormShot extends SpellThrowableProjectile {
    private static final EntityDataAccessor<Float> STOP_HOMING_PROGRESS =
            SynchedEntityData.defineId(EntityServantVoidWormShot.class, EntityDataSerializers.FLOAT);
    private boolean leftOwner;
    private boolean launched;
    private int launchDelay;
    private int riseTicks;
    private float speed = 0.5F;
    private Vec3 riseDir = new Vec3(0.0D, 1.0D, 0.0D);
    public float prevStopHomingProgress;
    public static final float HOME_FOR = 40.0F;
    private boolean voidStaff;

    public EntityServantVoidWormShot(EntityType type, Level level) {
        super(type, level);
    }

    public EntityServantVoidWormShot(Level worldIn, EntityVoidWorm worm) {
        this(AmEntityRegistry.SERVANT_VOID_WORM_SHOT.get(), worldIn);
        this.setOwner(worm);
        this.setPos(worm.getX() - (double) (worm.getBbWidth() + 1.0F) * 0.35D * (double) Mth.sin(worm.yBodyRot * Mth.DEG_TO_RAD),
                worm.getY() + 1.0D,
                worm.getZ() + (double) (worm.getBbWidth() + 1.0F) * 0.35D * (double) Mth.cos(worm.yBodyRot * Mth.DEG_TO_RAD));
    }

    public EntityServantVoidWormShot(Level worldIn, LivingEntity shooter, boolean right) {
        this(AmEntityRegistry.SERVANT_VOID_WORM_SHOT.get(), worldIn);
        this.setOwner(shooter);
        float rot = shooter.yHeadRot + (right ? 60 : -60);
        this.setPos(shooter.getX() - (double) shooter.getBbWidth() * 0.9D * (double) Mth.sin(rot * Mth.DEG_TO_RAD),
                shooter.getY() + 1.0D,
                shooter.getZ() + (double) shooter.getBbWidth() * 0.9D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
    }

    public EntityServantVoidWormShot(Level worldIn, LivingEntity shooter, LivingEntity target, boolean right) {
        this(worldIn, shooter, right);
        this.setTarget(target);
    }

    @OnlyIn(Dist.CLIENT)
    public EntityServantVoidWormShot(Level worldIn, double x, double y, double z, double dx, double dy, double dz) {
        this(AmEntityRegistry.SERVANT_VOID_WORM_SHOT.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(dx, dy, dz);
    }

    public EntityServantVoidWormShot(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AmEntityRegistry.SERVANT_VOID_WORM_SHOT.get(), world);
    }

    public void setVoidStaff(boolean voidStaff) {
        this.voidStaff = voidStaff;
    }

    protected static float lerpRotation(float p, float q) {
        while (q - p < -180.0F) {
            p -= 360.0F;
        }
        while (q - p >= 180.0F) {
            p += 360.0F;
        }
        return Mth.lerp(0.2F, p, q);
    }

    @Override
    public void tick() {
        this.prevStopHomingProgress = this.getStopHomingProgress();
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        if (this.tickCount > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (!this.level().isClientSide) {
            if (!this.launched) {
                if (this.launchDelay > 0) {
                    --this.launchDelay;
                } else {
                    this.launch();
                }
            } else if (this.riseTicks > 0) {
                --this.riseTicks;
                this.setStopHomingProgress(this.getStopHomingProgress() + 1.0F);
            } else if (this.getTarget() != null && this.getTarget().isAlive()) {
                this.setStopHomingProgress(this.getStopHomingProgress() + 1.0F);
                LivingEntity target = this.getTarget();
                Vec3 vec = (new Vec3(target.getX() - this.getX(), target.getEyeY() - this.getY(), target.getZ() - this.getZ()))
                        .normalize().scale(this.speed * 2.4F);
                this.setDeltaMovement(vec);
            } else if (this.launched) {
                LivingEntity newTarget = this.acquireNearbyTarget();
                if (newTarget != null) {
                    this.setTarget(newTarget);
                }
            } else if (this.tickCount > 40) {
                Entity shooter = this.getOwner();
                float stopHomingProgress = this.getStopHomingProgress();
                if (stopHomingProgress < HOME_FOR) {
                    ++stopHomingProgress;
                    this.setStopHomingProgress(stopHomingProgress);
                }
                float homeScale = 1.0F - stopHomingProgress / HOME_FOR;
                if (shooter instanceof Mob mob && mob.getTarget() != null && homeScale > 0.0F) {
                    LivingEntity target = mob.getTarget();
                    if (target == null) {
                        this.kill();
                    }
                    double d0 = target.getX() - this.getX();
                    double d1 = target.getEyeY() - this.getY();
                    double d2 = target.getZ() - this.getZ();
                    Vec3 vec = (new Vec3(d0, d1, d2)).normalize().scale((double) (Math.max(homeScale, 0.5F) * 1.2F));
                    this.setDeltaMovement(vec);
                }
            }
        }
        this.baseTick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            this.onImpact(raytraceresult);
        }
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.setNoGravity(true);
        this.updateRotation();
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d.scale(0.99F));
            this.setPos(d0, d1, d2);
        }
    }

    private LivingEntity acquireNearbyTarget() {
        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return null;
        }
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                owner.getBoundingBox().inflate(24.0D),
                e -> e != owner && e.isAlive() && !e.isSpectator()
                        && !owner.isAlliedTo(e) && owner.hasLineOfSight(e))) {
            if (entity instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned) {
                LivingEntity trueOwner = owned.getTrueOwner();
                if (trueOwner != null && (trueOwner == owner || owner.isAlliedTo(trueOwner))) {
                    continue;
                }
            }
            double dist = owner.distanceToSqr(entity);
            if (dist < bestDist) {
                bestDist = dist;
                best = entity;
            }
        }
        return best;
    }

    private void launch() {
        this.launched = true;
        this.shoot(this.riseDir.x, this.riseDir.y, this.riseDir.z, this.speed, 0.0F);
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.VOID_TOUCHED_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F,
                    1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.gameEvent(GameEvent.PROJECTILE_SHOOT);
        }
    }

    protected void onEntityHit(EntityHitResult result) {
        LivingEntity shooter = this.getOwner();
        if (shooter != null && !(result.getEntity() instanceof EntityVoidWorm) && !(result.getEntity() instanceof EntityVoidWormPart)) {
            boolean b = this.wormAttack(result.getEntity(), this.damageSources().mobProjectile(this, shooter),
                    com.qiuyue.goetyominous.config.SpellConfig.VoidShotDamage.get().floatValue() + this.getExtraDamage());
            if (this.voidStaff && result.getEntity() instanceof LivingEntity living
                    && !shooter.isAlliedTo(living)) {
                living.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), 80, 0, false, true));
            }
            if (b && result.getEntity() instanceof Player player) {
                if (player.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
                    player.disableShield(true);
                }
            }
        }
        this.remove(RemovalReason.DISCARDED);
    }

    private boolean wormAttack(Entity entity, DamageSource source, float dmg) {
        return entity.hurt(source, dmg);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockState blockstate = this.level().getBlockState(result.getBlockPos());
        if (!this.level().isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STOP_HOMING_PROGRESS, 0.0F);
    }

    public float getStopHomingProgress() {
        return this.entityData.get(STOP_HOMING_PROGRESS);
    }

    public void setStopHomingProgress(float progress) {
        this.entityData.set(STOP_HOMING_PROGRESS, progress);
    }

    public int getLaunchDelay() {
        return this.launchDelay;
    }

    public void setLaunchDelay(int launchDelay) {
        this.launchDelay = launchDelay;
    }

    public int getRiseTicks() {
        return this.riseTicks;
    }

    public void setRiseTicks(int riseTicks) {
        this.riseTicks = riseTicks;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vec3 getRiseDir() {
        return this.riseDir;
    }

    public void setRiseDir(Vec3 riseDir) {
        this.riseDir = riseDir;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
        compound.putFloat("HomeTime", this.getStopHomingProgress());
        compound.putInt("LaunchDelay", this.launchDelay);
        compound.putInt("RiseTicks", this.riseTicks);
        compound.putFloat("Speed", this.speed);
        compound.putDouble("RiseDirX", this.riseDir.x);
        compound.putDouble("RiseDirY", this.riseDir.y);
        compound.putDouble("RiseDirZ", this.riseDir.z);
        compound.putBoolean("VoidStaff", this.voidStaff);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStopHomingProgress(compound.getFloat("HomeTime"));
        this.leftOwner = compound.getBoolean("LeftOwner");
        this.launchDelay = compound.getInt("LaunchDelay");
        this.riseTicks = compound.getInt("RiseTicks");
        this.speed = compound.getFloat("Speed");
        this.riseDir = new Vec3(compound.getDouble("RiseDirX"), compound.getDouble("RiseDirY"), compound.getDouble("RiseDirZ"));
        this.voidStaff = compound.getBoolean("VoidStaff");
    }

    private boolean checkLeftOwner() {
        Entity shooter = this.getOwner();
        if (shooter != null) {
            for (Entity entity1 : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                    entity -> !entity.isSpectator() && entity.isPickable())) {
                if (entity1.getRootVehicle() == shooter.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vector3d = (new Vec3(x, y, z)).normalize()
                .add(this.random.nextGaussian() * 0.0075F * inaccuracy,
                        this.random.nextGaussian() * 0.0075F * inaccuracy,
                        this.random.nextGaussian() * 0.0075F * inaccuracy)
                .scale(velocity);
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d));
        float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * Mth.RAD_TO_DEG));
        this.setXRot((float) (Mth.atan2(vector3d.y, (double) f) * Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void onImpact(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) result);
        } else if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }
        this.gameEvent(GameEvent.ENTITY_DIE);
        this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float) (x * x + z * z));
            this.setXRot((float) (Mth.atan2(y, (double) f) * Mth.RAD_TO_DEG));
            this.setYRot((float) (Mth.atan2(x, z) * Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.isSpectator() && target.isAlive() && target.isPickable()) {
            Entity shooter = this.getOwner();
            return (shooter == null || this.leftOwner || !shooter.isPassengerOfSameVehicle(target))
                    && !(target instanceof EntityServantVoidWormShot)
                    && !(target instanceof EntityVoidWormPart);
        }
        return false;
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) vector3d.horizontalDistance());
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, (double) f) * Mth.RAD_TO_DEG)));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * Mth.RAD_TO_DEG)));
    }
}
