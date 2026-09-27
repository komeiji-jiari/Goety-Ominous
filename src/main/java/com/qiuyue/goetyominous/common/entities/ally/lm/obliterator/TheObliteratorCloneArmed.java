package com.qiuyue.goetyominous.common.entities.ally.lm.obliterator;

import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.LmAnimatedProjectile;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.IAnimatedBoss.TheObliterator.TheObliteratorUtils;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TheObliteratorCloneArmed extends LmAnimatedProjectile {

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 17;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;
    public float lastYawToDest = 0.0F;
    public float lastPitchToDest = 0.0F;

    private static final EntityDataAccessor<Integer> ANIMATION_STATE_SET =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DESTINATION_Z =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_Y =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_X =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(TheObliteratorCloneArmed.class, EntityDataSerializers.BOOLEAN);

    public float activateProgress;
    public float prevactivateProgress;
    public final ControlledAnim RendercontrolledAnim = new ControlledAnim(15);
    public AnimationState leftHookComboAnimationState = new AnimationState();
    public AnimationState uppercutAnimationState = new AnimationState();
    public AnimationState rightUppercutAnimationState = new AnimationState();
    public float shakeStrenght = 0.05F;
    public float attackArc = 270.0F;
    public int offsetDegrees;

    public TheObliteratorCloneArmed(EntityType<? extends TheObliteratorCloneArmed> type, Level level) {
        super(type, level);
    }

    public TheObliteratorCloneArmed(Level worldIn, double x, double y, double z, float yawRad, int warmupDelayTicks,
                                    LivingEntity casterIn, float damage, float destX, float destY, float destZ,
                                    int animation, int life) {
        this(LmEntityRegistry.THE_OBLITERATOR_CLONE_ARMED.get(), worldIn);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        float yawDeg = yawRad * 57.295776F - 90.0F;
        this.setYRot(yawDeg);
        this.setPos(x, y, z);
        this.setDestinationX(destX);
        this.setDestinationY(destY);
        this.setDestinationZ(destZ);
        this.setAnimationStateValue(animation);
        this.setMaxUpStep(2.0F);
        this.setDamage(damage);
        this.setLifeTicks(life);
    }

    public float getDestinationX() {
        return this.entityData.get(DESTINATION_X);
    }

    public float getDestinationY() {
        return this.entityData.get(DESTINATION_Y);
    }

    public float getDestinationZ() {
        return this.entityData.get(DESTINATION_Z);
    }

    public void setDestinationX(float destination) {
        this.entityData.set(DESTINATION_X, destination);
    }

    public void setDestinationY(float destination) {
        this.entityData.set(DESTINATION_Y, destination);
    }

    public void setDestinationZ(float destination) {
        this.entityData.set(DESTINATION_Z, destination);
    }

    public int getAnimationStateValue() {
        return this.entityData.get(ANIMATION_STATE_SET);
    }

    public void setAnimationStateValue(int value) {
        this.entityData.set(ANIMATION_STATE_SET, value);
    }

    public void setLifeTicks(int amount) {
        this.entityData.set(LIFE, amount);
    }

    public int getLifeTicks() {
        return this.entityData.get(LIFE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(DESTINATION_X, 0.0F);
        this.entityData.define(DESTINATION_Y, 0.0F);
        this.entityData.define(DESTINATION_Z, 0.0F);
        this.entityData.define(LIFE, 0);
        this.entityData.define(ANIMATION_STATE_SET, 0);
        super.defineSynchedData();
    }

    public void setCaster(@Nullable LivingEntity casterIn) {
        this.caster = casterIn;
        this.casterUuid = casterIn == null ? null : casterIn.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    public AnimationState getAnimationState(String input) {
        if (input == "uppercut") {
            return this.uppercutAnimationState;
        }
        if (input == "right_uppercut") {
            return this.rightUppercutAnimationState;
        }
        if (input == "left_hook_combo") {
            return this.leftHookComboAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ANIMATIONSTATE.equals(key) && this.level().isClientSide) {
            switch (this.getAnimationState()) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.uppercutAnimationState.startIfStopped(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.leftHookComboAnimationState.startIfStopped(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.rightUppercutAnimationState.startIfStopped(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void stopAllAnimationStates() {
        this.uppercutAnimationState.stop();
        this.leftHookComboAnimationState.stop();
        this.rightUppercutAnimationState.stop();
    }

    @Override
    public int disappearTicks() {
        return 10;
    }

    public float damage() {
        return 6.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.BIG_ANNIHILATION_FLAME.get(), this.getRandomX(0.5D),
                    this.getRandomY(), this.getRandomZ(0.5D), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        SoundEvent swingSound = ModSounds.HEAVY_SWING.get();
        SoundEvent impactSound = ModSounds.WEAPON_IMPACT.get();
        double dx = (double) this.getDestinationX() - this.getX();
        double dy = (double) this.getDestinationY() - this.getY();
        double dz = (double) this.getDestinationZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double stopDistance = 3.0D;
        boolean shouldRotate = dist > stopDistance;
        if (shouldRotate) {
            Vec3 dest = new Vec3(this.getDestinationX(), this.getDestinationY(), this.getDestinationZ());
            this.lookAt(EntityAnchorArgument.Anchor.EYES, dest);
        }

        if (this.getAnimationState() == 2) {
            if (this.AnimationTicks > 29) {
                this.controlledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 20) {
                this.RendercontrolledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 12 && this.AnimationTicks <= 14) {
                double dx1 = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
                double dy1 = this.getY() + 3.0D;
                double dz1 = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
                float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 2.25F,
                            this.getId()), dx1, dy1, dz1, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                Vec3 toDest = new Vec3((double) this.getDestinationX() - this.getX(),
                        (double) this.getDestinationY() - this.getY(),
                        (double) this.getDestinationZ() - this.getZ());
                double distance = toDest.length();
                double speed = 0.1D + toDest.length() * 0.5D;
                if (distance > speed) {
                    this.move(MoverType.SELF, toDest.normalize().scale(speed));
                }
            }
            if (this.AnimationTicks == 11) {
                this.playSound(swingSound, 1.0F, 1.0F);
            }
            if (this.AnimationTicks == 15) {
                if (this.target() != null) {
                    this.setDestinationX((float) this.target().getX());
                    this.setDestinationY((float) this.target().getY());
                    this.setDestinationZ((float) this.target().getZ());
                }
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, this.shakeStrenght, 0, 20);
                this.SideAreaAttack(4.0F, 4.0F, this.attackArc, -90.0F, this.damage(), 120, false, true, impactSound);
            }
            if (this.AnimationTicks >= 25) {
                double dx1 = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
                double dy1 = this.getY() + 3.0D;
                double dz1 = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
                float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 2.25F,
                            this.getId()), dx1, dy1, dz1, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                Vec3 toDest = new Vec3((double) this.getDestinationX() - this.getX(),
                        (double) this.getDestinationY() - this.getY(),
                        (double) this.getDestinationZ() - this.getZ());
                double distance = toDest.length();
                double speed = 0.1D + toDest.length() * 0.5D;
                if (distance > speed) {
                    this.move(MoverType.SELF, toDest.normalize().scale(speed));
                }
            }
            if (this.AnimationTicks == 24) {
                this.playSound(swingSound, 1.0F, 1.0F);
            }
            if (this.AnimationTicks == 28) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, this.shakeStrenght, 0, 20);
                this.SideAreaAttack(5.0F, 4.0F, this.attackArc, -90.0F, this.damage(), 120, false, true, impactSound);
            }
        }

        if (this.getAnimationState() == 1) {
            if (this.AnimationTicks > 23) {
                this.controlledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 10) {
                this.RendercontrolledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 15) {
                double dx1 = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
                double dy1 = this.getY() + 3.0D;
                double dz1 = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
                float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 2.25F,
                            this.getId()), dx1, dy1, dz1, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                Vec3 toDest = new Vec3((double) this.getDestinationX() - this.getX(),
                        (double) this.getDestinationY() - this.getY(),
                        (double) this.getDestinationZ() - this.getZ());
                double distance = toDest.length();
                double speed = 0.1D + toDest.length() * 0.5D;
                if (distance <= speed) {
                    this.setPos(this.getDestinationX(), this.getDestinationY(), this.getDestinationZ());
                } else {
                    this.move(MoverType.SELF, toDest.normalize().scale(speed));
                }
            }
            if (this.AnimationTicks == 14) {
                this.playSound(swingSound, 1.0F, 1.0F);
            }
            if (this.AnimationTicks == 18) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, this.shakeStrenght, 0, 20);
                this.SideAreaAttack(5.0F, 4.0F, this.attackArc, 90.0F, this.damage(), 120, false, true, impactSound);
            }
        }

        if (this.getAnimationState() == 3) {
            if (this.AnimationTicks > 23) {
                this.controlledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 10) {
                this.RendercontrolledAnim.increaseTimer();
            }
            if (this.AnimationTicks >= 15) {
                double dx1 = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
                double dy1 = this.getY() + 3.0D;
                double dz1 = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
                float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 2.25F,
                            this.getId()), dx1, dy1, dz1, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                Vec3 toDest = new Vec3((double) this.getDestinationX() - this.getX(),
                        (double) this.getDestinationY() - this.getY(),
                        (double) this.getDestinationZ() - this.getZ());
                double distance = toDest.length();
                double speed = 0.1D + toDest.length() * 0.5D;
                if (distance <= speed) {
                    this.setPos(this.getDestinationX(), this.getDestinationY(), this.getDestinationZ());
                } else {
                    this.move(MoverType.SELF, toDest.normalize().scale(speed));
                }
            }
            if (this.AnimationTicks == 14) {
                this.playSound(swingSound, 1.0F, 1.0F);
            }
            if (this.AnimationTicks == 18) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, this.shakeStrenght, 0, 20);
                this.SideAreaAttack(5.0F, 4.0F, this.attackArc, -90.0F, this.damage(), 120, false, true, impactSound);
            }
        }

        this.prevactivateProgress = this.activateProgress;

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                ++this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (++this.lifeTicks > this.getLifeTicks()) {
                this.discard();
            }
        }
    }

    public boolean isActivate() {
        return this.entityData.get(ATTACK);
    }

    public void setActivate(boolean activate) {
        this.entityData.set(ATTACK, activate);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
        }
        if (id <= 0) {
            this.lifeTicks = 0;
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public LivingEntity target() {
        if (this.caster != null && this.caster instanceof Mob) {
            return ((Mob) this.caster).getTarget();
        }
        return null;
    }

    @Override
    public void setAnimationState(int animation) {
        this.AnimationTicks = 0;
        this.entityData.set(ANIMATIONSTATE, animation);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 3.0F, 1.0F);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 25, 0.0F, 1.0F, 0.0F, 1.0F, 14.0F,
                    false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        if (this.getAnimationState() == 0) {
            this.setAnimationState(this.getAnimationStateValue());
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        }
        int ticks = this.lifeTicks - 2;
        return ticks <= 0 ? 1.0F : 1.0F - ((float) ticks - partialTicks) / 20.0F;
    }

    public void calculatedDash(float multiplier) {
        this.setDeltaMovement((this.getDestinationX() - this.getX()) * multiplier, 0.0D,
                (this.getDestinationZ() - this.getZ()) * multiplier);
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float damage,
                               int brokenShieldTicks, boolean canStun, boolean canlaunch, SoundEvent soundEvent) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX())
                    * 57.29577951308232D - 90.0D) % 360.0D);
            float entityAttackingAngle = (this.getYRot() - boxOffset) % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (!(entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F)
                    && !(entityRelativeAngle <= -360.0F + arc / 2.0F)
                    || this.getCaster() == null || this.getCaster().isAlliedTo(entityHit)
                    || entityHit instanceof TheObliteratorServant || entityHit == this.getCaster()) {
                continue;
            }
            boolean flag = entityHit.hurt(ModDamageTypes.causeAnnihilationDamage(this.getCaster(), this.getCaster()),
                    damage + MathUtils.entityBasedHpDamage(entityHit, MobsConfig.TheObliteratorCloneBurstHpDamage.get()));
            if (flag) {
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, false);
                if (canlaunch) {
                    this.launch(entityHit, true, 0.0F, 0.5F);
                }
                if (!canStun) {
                    this.playSound(soundEvent, 1.0F, 0.5F);
                }
                if (canStun) {
                    this.playSound(SoundEvents.ANVIL_PLACE, 1.0F, 1.0F);
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 80, 1));
                }
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks <= 0) {
                continue;
            }
        }
    }

    public static void disableShield(LivingEntity livingEntity, int ticks) {
        ((Player) livingEntity).getCooldowns().addCooldown(livingEntity.getUseItem().getItem(), ticks);
        livingEntity.stopUsingItem();
        livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 30);
    }

    private void launch(LivingEntity entity, boolean huge, float launchMultiplier, float yPower) {
        double deltaX = entity.getX() - this.getX();
        double deltaZ = entity.getZ() - this.getZ();
        double distanceSquared = Math.max(deltaX * deltaX + deltaZ * deltaZ, 0.001D);
        float multiplier = huge ? launchMultiplier : 0.5F;
        entity.push(deltaX / distanceSquared * (double) multiplier, huge ? (double) yPower : 0.2D,
                deltaZ / distanceSquared * (double) multiplier);
    }

    public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return this.getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
        return this.level().getEntitiesOfClass(entityClass, this.getBoundingBox().inflate(dX, dY, dZ),
                e -> e != this && this.distanceTo(e) <= r + e.getBbWidth() / 2.0F && e.getY() <= this.getY() + dY);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }
}
