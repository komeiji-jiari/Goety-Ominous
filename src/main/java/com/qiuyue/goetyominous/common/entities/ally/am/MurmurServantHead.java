package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MurmurServantHead extends Summoned implements FlyingAnimal {
    private static final EntityDataAccessor<Optional<UUID>> BODY_UUID;
    private static final EntityDataAccessor<Integer> BODY_ID;
    private static final EntityDataAccessor<Boolean> PULLED_IN;
    private static final EntityDataAccessor<Boolean> ANGRY;
    public double prevXHair;
    public double prevYHair;
    public double prevZHair;
    public double xHair;
    public double yHair;
    public double zHair;
    public float angerProgress;
    public float prevAngerProgress;
    private boolean prevLaunched;

    public MurmurServantHead(EntityType type, Level level) {
        super(type, level);
        this.prevLaunched = false;
        this.moveControl = new MoveController();
    }

    protected MurmurServantHead(MurmurServant parent) {
        this((EntityType) com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MURMUR_SERVANT_HEAD.get(), parent.level());
        this.setBodyId(parent.getUUID());
        this.setTrueOwner(parent.getTrueOwner());
        this.setPos(parent.getNeckBottom(1.0F).add(0.0, 0.5, 0.0));
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, this.level());
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public int getExperienceReward() {
        return 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AttackGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BODY_UUID, Optional.empty());
        this.entityData.define(BODY_ID, -1);
        this.entityData.define(PULLED_IN, true);
        this.entityData.define(ANGRY, false);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get())
                && this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(2.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                        5, 0.5, 0.5, 0.5, 0.0);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private void doSpawnPositioning(MurmurServant parent) {
        this.setPos(parent.getNeckBottom(1.0F).add(0.0, 0.5, 0.0));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MurmurServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MurmurServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MurmurServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MurmurServantMovementSpeed.get());
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean isPulledIn() {
        return (Boolean)this.entityData.get(PULLED_IN);
    }

    public void setPulledIn(boolean pulledIn) {
        this.entityData.set(PULLED_IN, pulledIn);
    }

    public boolean isAngry() {
        return (Boolean)this.entityData.get(ANGRY) || !this.isAlive();
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, angry);
    }

    public Vec3 getNeckTop(float partialTick) {
        double d0 = Mth.lerp((double)partialTick, this.xo, this.getX());
        double d1 = Mth.lerp((double)partialTick, this.yo, this.getY());
        double d2 = Mth.lerp((double)partialTick, this.zo, this.getZ());
        double bounce = 0.0;
        Entity body = this.getBody();
        if (body instanceof MurmurServant) {
            bounce = ((MurmurServant)body).calculateWalkBounce(partialTick);
        }

        return new Vec3(d0, d1 + bounce, d2);
    }

    public Vec3 getNeckBottom(float partialTick) {
        Entity body = this.getBody();
        Vec3 top = this.getNeckTop(partialTick);
        if (body instanceof MurmurServant murmur) {
            Vec3 bodyBase = murmur.getNeckBottom(partialTick);
            double sub = top.subtract(bodyBase).horizontalDistance();
            return sub <= 0.06 ? new Vec3(top.x, bodyBase.y, top.z) : bodyBase;
        } else {
            return top.add(0.0, -0.5, 0.0);
        }
    }

    public boolean hasNeckBottom() {
        return true;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    public UUID getBodyId() {
        return (UUID)((Optional)this.entityData.get(BODY_UUID)).orElse((Object)null);
    }

    public void setBodyId(@Nullable UUID uniqueId) {
        this.entityData.set(BODY_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getBody() {
        if (!this.level().isClientSide) {
            UUID id = this.getBodyId();
            return id == null ? null : ((ServerLevel)this.level()).getEntity(id);
        } else {
            int id = (Integer)this.entityData.get(BODY_ID);
            return id == -1 ? null : this.level().getEntity(id);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("BodyUUID")) {
            this.setBodyId(compound.getUUID("BodyUUID"));
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getBodyId() != null) {
            compound.putUUID("BodyUUID", this.getBodyId());
        }

    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35F;
    }

    public void tick() {
        super.tick();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
        this.prevAngerProgress = this.angerProgress;
        if (this.isAngry() && this.angerProgress < 5.0F) {
            ++this.angerProgress;
        }

        if (!this.isAngry() && this.angerProgress > 0.0F) {
            --this.angerProgress;
        }

        this.moveHair();
        Entity body = this.getBody();
        MurmurServant murmur;
        if (!this.level().isClientSide) {
            if (body instanceof MurmurServant) {
                murmur = (MurmurServant)body;
                this.entityData.set(BODY_ID, body.getId());
                if (this.isPulledIn() && murmur.isAlive()) {
                    Vec3 base = murmur.getNeckBottom(1.0F).add(0.0, 0.550000011920929, 0.0);
                    Vec3 vec3 = base.subtract(this.position());
                    if (vec3.length() < 1.0) {
                        this.setPos(base.x, base.y, base.z);
                        this.noPhysics = false;
                    } else {
                        this.noPhysics = true;
                        vec3 = base.subtract(this.position()).normalize();
                        float f = this.getTarget() != null && this.getTarget().isAlive() ? 0.3F : 0.15F;
                        this.setDeltaMovement(vec3.scale((double)f));
                    }

                    this.setYRot(murmur.getYRot());
                    this.yBodyRot = murmur.getYRot();
                } else {
                    this.noPhysics = false;
                }

                LivingEntity headTarget = this.getTarget();
                LivingEntity bodyTarget = murmur.getTarget();
                if (headTarget != null && headTarget.isAlive()) {
                    if (murmur.canAttack(headTarget)) {
                        murmur.setTarget(headTarget);
                    } else {
                        this.setTarget((LivingEntity)null);
                        murmur.setTarget((LivingEntity)null);
                    }
                } else if (bodyTarget != null && bodyTarget.isAlive() && this.canAttack(bodyTarget)) {
                    this.setTarget(bodyTarget);
                }

                if (body.isRemoved()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }

            if (body == null && this.tickCount > 20) {
                this.remove(RemovalReason.DISCARDED);
            }
        } else if (body instanceof MurmurServant) {
            murmur = (MurmurServant)body;
            if (murmur.hurtTime > 0 || murmur.deathTime > 0) {
                this.hurtTime = murmur.hurtTime;
                this.deathTime = murmur.deathTime;
            }
        }

        if (this.prevLaunched && !this.isPulledIn()) {
            this.playSound((SoundEvent)AMSoundRegistry.MURMUR_NECK.get(), 3.0F * this.getSoundVolume(), this.getVoicePitch());
        }

        this.prevLaunched = this.isPulledIn();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean hurt(DamageSource source, float damage) {
        Entity body = this.getBody();
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            return body != null && body.hurt(source, 0.5F * damage) ? true : super.hurt(source, damage);
        }
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    private void moveHair() {
        this.prevXHair = this.xHair;
        this.prevYHair = this.yHair;
        this.prevZHair = this.zHair;
        double d0 = this.getX() - this.xHair;
        double d1 = this.getY() - this.yHair;
        double d2 = this.getZ() - this.zHair;
        if (d0 > 10.0) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 > 10.0) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 > 10.0) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        if (d0 < -10.0) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 < -10.0) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 < -10.0) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        this.xHair += d0 * 0.25;
        this.zHair += d2 * 0.25;
        this.yHair += d1 * 0.25;
    }

    public boolean isAlliedTo(Entity entity) {
        return this.getBodyId() != null && entity.getUUID().equals(this.getBodyId()) || super.isAlliedTo(entity);
    }

    public void playAmbientSound() {
        if (this.isPulledIn() && !this.isAngry()) {
            super.playAmbientSound();
        }

    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)AMSoundRegistry.MURMUR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.getBody() == null ? (SoundEvent)AMSoundRegistry.MURMUR_HURT.get() : null;
    }

    protected SoundEvent getDeathSound() {
        return this.getBody() == null ? (SoundEvent)AMSoundRegistry.MURMUR_HURT.get() : null;
    }

    public boolean isFlying() {
        return true;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    static {
        BODY_UUID = SynchedEntityData.defineId(MurmurServantHead.class, EntityDataSerializers.OPTIONAL_UUID);
        BODY_ID = SynchedEntityData.defineId(MurmurServantHead.class, EntityDataSerializers.INT);
        PULLED_IN = SynchedEntityData.defineId(MurmurServantHead.class, EntityDataSerializers.BOOLEAN);
        ANGRY = SynchedEntityData.defineId(MurmurServantHead.class, EntityDataSerializers.BOOLEAN);
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity = MurmurServantHead.this;

        public MoveController() {
            super(MurmurServantHead.this);
        }

        public void tick() {
            if (!MurmurServantHead.this.isPulledIn()) {
                float angle = 0.017453292F * (this.parentEntity.yBodyRot + 90.0F);
                float radius = (float)Math.sin((double)((float)this.parentEntity.tickCount * 0.2F)) * 2.0F;
                double extraX = (double)(radius * Mth.sin(3.1415927F + angle));
                double extraY = (double)radius * -Math.cos((double)angle - 1.5707963267948966);
                double extraZ = (double)(radius * Mth.cos(angle));
                Vec3 strafPlus = new Vec3(extraX, extraY, extraZ);
                if (this.operation == Operation.MOVE_TO) {
                    Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
                    double d0 = vector3d.length();
                    double width = this.parentEntity.getBoundingBox().getSize();
                    Vec3 shimmy = Vec3.ZERO;
                    LivingEntity attackTarget = this.parentEntity.getTarget();
                    if (attackTarget != null && this.parentEntity.horizontalCollision) {
                        shimmy = new Vec3(0.0, 0.005, 0.0);
                    }

                    Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05 / d0);
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d1.add(strafPlus.scale(0.003 * Math.min(d0, 100.0)).add(shimmy))));
                    if (attackTarget == null) {
                        if (d0 >= width) {
                            Vec3 deltaMovement = this.parentEntity.getDeltaMovement();
                            this.parentEntity.setYRot(-((float)Mth.atan2(deltaMovement.x, deltaMovement.z)) * 57.295776F);
                            this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                        }
                    } else {
                        double d2 = attackTarget.getX() - this.parentEntity.getX();
                        double d1 = attackTarget.getZ() - this.parentEntity.getZ();
                        this.parentEntity.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
                        this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                    }
                } else if (this.operation == Operation.WAIT) {
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(strafPlus.scale(0.003)));
                }

            }
        }
    }

    private class AttackGoal extends Goal {
        private int time;
        private int biteCooldown = 0;
        private Vec3 emergeFrom;
        private float emergeAngle;

        public AttackGoal() {
            this.emergeFrom = Vec3.ZERO;
            this.emergeAngle = 0.0F;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return MurmurServantHead.this.getTarget() != null && MurmurServantHead.this.getTarget().isAlive();
        }

        public void start() {
            this.time = 0;
            this.biteCooldown = 0;
            MurmurServantHead.this.setPulledIn(false);
        }

        public void stop() {
            this.time = 0;
            MurmurServantHead.this.setPulledIn(true);
            MurmurServantHead.this.setAngry(false);
        }

        public void tick() {
            LivingEntity target = MurmurServantHead.this.getTarget();
            Entity body = MurmurServantHead.this.getBody();
            if (target != null) {
                double dist = Math.sqrt(MurmurServantHead.this.distanceToSqr(target.getEyePosition()));
                double bodyDist = body != null ? (double)body.distanceTo(target) : 0.0;
                if (bodyDist > 16.0 && this.time > 30 && body instanceof MurmurServant) {
                    MurmurServant murmur = (MurmurServant)body;
                    murmur.setTarget(target);
                    murmur.getNavigation().moveTo(target, 1.35);
                }

                if (bodyDist > 64.0) {
                    MurmurServantHead.this.setPulledIn(true);
                } else if (this.biteCooldown == 0) {
                    MurmurServantHead.this.setPulledIn(false);
                    Vec3 moveTo = target.getEyePosition();
                    if (this.time > 30) {
                        if (!MurmurServantHead.this.isAngry()) {
                            MurmurServantHead.this.playSound((SoundEvent)AMSoundRegistry.MURMUR_ANGER.get(), 1.5F * MurmurServantHead.this.getSoundVolume(), MurmurServantHead.this.getVoicePitch());
                            MurmurServantHead.this.gameEvent(GameEvent.ENTITY_ROAR);
                        }

                        MurmurServantHead.this.setAngry(true);
                        MurmurServantHead.this.getNavigation().moveTo(moveTo.x, moveTo.y, moveTo.z, 1.3);
                    } else {
                        if (this.time == 0) {
                            this.emergeFrom = MurmurServantHead.this.getNeckTop(1.0F).add(0.0, 0.5, 0.0);
                            moveTo.subtract(this.emergeFrom);
                        }

                        boolean clockwise = false;
                        float circleDistance = 2.5F;
                        float circlingTime = (float)(30 * this.time);
                        float angle = 0.017453292F * (clockwise ? -circlingTime : circlingTime);
                        double extraX = (double)(circleDistance * Mth.sin(3.1415927F + angle));
                        double extraZ = (double)(circleDistance * Mth.cos(angle));
                        double y = Math.max(this.emergeFrom.y + 2.0, target.getEyeY());
                        Vec3 vec3 = new Vec3(this.emergeFrom.x + extraX, y, this.emergeFrom.z + extraZ);
                        MurmurServantHead.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.7);
                    }

                    MurmurServantHead.this.lookAt(Anchor.EYES, moveTo);
                    if (dist < 1.5 && MurmurServantHead.this.hasLineOfSight(target)) {
                        MurmurServantHead.this.playSound((SoundEvent)AMSoundRegistry.MURMUR_ATTACK.get(), MurmurServantHead.this.getSoundVolume(), MurmurServantHead.this.getVoicePitch());
                        this.biteCooldown = 5 + MurmurServantHead.this.getRandom().nextInt(15);
                        target.hurt(MurmurServantHead.this.damageSources().mobAttack(MurmurServantHead.this), 5.0F);
                    }
                } else {
                    MurmurServantHead.this.setPulledIn(true);
                    MurmurServantHead.this.lookAt(Anchor.EYES, target.getEyePosition());
                    MurmurServantHead.this.setAngry(false);
                }

                ++this.time;
            }

            if (this.biteCooldown > 0) {
                --this.biteCooldown;
            }

        }
    }
}
