package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CaniacServant extends Summoned implements IAnimatedEntity {

    private static final EntityDataAccessor<Float> SPIN_SPEED = SynchedEntityData.defineId(CaniacServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(CaniacServant.class, EntityDataSerializers.BOOLEAN);
    public static final Animation ANIMATION_LUNGE = Animation.create(35);

    private Animation currentAnimation;
    private int animationTick;
    private float prevLeftArmRot;
    private float leftArmRot;
    private float prevRightArmRot;
    private float rightArmRot;
    private boolean spinSecondArm = false;
    private boolean hasRunningAttributes = false;
    private float runProgress;
    private float prevRunProgress;
    private int swingSoundTimer = 0;

    public CaniacServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPIN_SPEED, 0.0F);
        this.entityData.define(RUNNING, false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CaniacServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.CaniacServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CaniacServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CaniacServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CaniacServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.CaniacServantArmor.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CaniacMeleeGoal());
        // 用 Goety 的 WanderGoal(checkNoActionTime=false)游荡:Summoned 覆写 checkDespawn 后 noActionTime 永不复位,
        // 原版 RandomStrollGoal 空闲约 5 秒即 noActionTime>=100 被永久禁用而站桩不动;WanderGoal 落点限定在主人附近。
        this.goalSelector.addGoal(3, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.CaniacServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof CaniacServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevLeftArmRot = this.leftArmRot;
        this.prevRightArmRot = this.rightArmRot;
        this.prevRunProgress = this.runProgress;
        float armSpinSpeed = this.getArmSpinSpeed();
        if (armSpinSpeed > 0.0F && this.isAlive()) {
            if (this.swingSoundTimer-- < 0) {
                this.swingSoundTimer = 5 + this.level().random.nextInt(10);
                if (!this.level().isClientSide) {
                    this.playSound(ACSoundRegistry.CANIAC_SWING.get());
                }
            }
            if (this.isLeftHanded()) {
                this.leftArmRot += armSpinSpeed;
                if (this.leftArmRot % 360.0F > 180.0F) {
                    this.spinSecondArm = true;
                }
                if (this.spinSecondArm) {
                    this.rightArmRot += armSpinSpeed;
                }
            } else {
                this.rightArmRot += armSpinSpeed;
                if (this.rightArmRot % 360.0F > 180.0F) {
                    this.spinSecondArm = true;
                }
                if (this.spinSecondArm) {
                    this.leftArmRot += armSpinSpeed;
                }
            }
            if (this.level().isClientSide) {
                if (Mth.wrapDegrees(this.leftArmRot) % 180.0F > 70.0F) {
                    this.spawnArmSwingParticles(true);
                }
                if (Mth.wrapDegrees(this.rightArmRot) % 180.0F > 70.0F) {
                    this.spawnArmSwingParticles(false);
                }
            } else {
                if (Mth.wrapDegrees(this.leftArmRot) % 180.0F > 75.0F) {
                    this.hurtMobsFromArmSwing(true, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 0.1F);
                }
                if (Mth.wrapDegrees(this.rightArmRot) % 180.0F > 75.0F) {
                    this.hurtMobsFromArmSwing(false, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 0.1F);
                }
            }
        } else {
            float f = this.getAnimation() == ANIMATION_LUNGE ? 40.0F : 15.0F;
            this.spinSecondArm = false;
            if (Mth.wrapDegrees(this.leftArmRot) != 0.0F) {
                this.leftArmRot = Mth.approachDegrees(this.leftArmRot, 0.0F, f);
            }
            if (Mth.wrapDegrees(this.rightArmRot) != 0.0F) {
                this.rightArmRot = Mth.approachDegrees(this.rightArmRot, 0.0F, f);
            }
        }
        if (this.isRunning() && !this.hasRunningAttributes) {
            this.hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        }
        if (!this.isRunning() && this.hasRunningAttributes) {
            this.hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }
        if (this.isRunning() && this.runProgress < 5.0F) {
            this.runProgress++;
        }
        if (!this.isRunning() && this.runProgress > 0.0F) {
            this.runProgress--;
        }
        if (this.getAnimation() == ANIMATION_LUNGE && this.getAnimationTick() == 10) {
            this.playSound(ACSoundRegistry.CANIAC_ATTACK.get());
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.getAnimation() != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_LUNGE};
    }

    public float getArmSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setArmSpinSpeed(float spinSpeed) {
        this.entityData.set(SPIN_SPEED, spinSpeed);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    private void spawnArmSwingParticles(boolean left) {
        Vec3 dustMotion = new Vec3(0.0F, 0.2F, this.random.nextFloat() * 0.5F - 0.25F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
        Vec3 armDustPosition = this.position().add(new Vec3(left ? 0.75F : -0.75F, 1, this.random.nextFloat() * -0.5F + 0.5F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
        BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(this.level(), armDustPosition)).below();
        BlockState state = this.level().getBlockState(ground);
        if (state.isSolid()) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, armDustPosition.x, ground.getY() + 1, armDustPosition.z, dustMotion.x, dustMotion.y, dustMotion.z);
        }
    }

    private void hurtMobsFromArmSwing(boolean left, float damageAmount, float knockbackAmount) {
        boolean strong = this.random.nextFloat() < 0.1F;
        if (strong) {
            damageAmount *= 1.5F;
            knockbackAmount = 1.0F;
        }
        Vec3 armHurtPosition = this.position().add(new Vec3(left ? 0.75F : -0.75F, 1, 1.25F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
        AABB hurtBox = new AABB(armHurtPosition.x - 1.0F, armHurtPosition.y - 1.0F, armHurtPosition.z - 1.0F, armHurtPosition.x + 1.0F, armHurtPosition.y + 1.0F, armHurtPosition.z + 1.0F);
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, hurtBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (living.is(this) || this.isAlliedTo(living) || living.getType() == this.getType()) continue;
            if (living.distanceTo(this) < 3.15F && living.hurt(damageSource, damageAmount)) {
                living.knockback(knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ());
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANIAC_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CANIAC_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANIAC_DEATH.get();
    }

    public float getArmAngle(boolean left, float partialTicks) {
        if (left) {
            return this.prevLeftArmRot + (this.leftArmRot - this.prevLeftArmRot) * partialTicks;
        }
        return this.prevRightArmRot + (this.rightArmRot - this.prevRightArmRot) * partialTicks;
    }

    public float getRunProgress(float partialTick) {
        return (this.prevRunProgress + (this.runProgress - this.prevRunProgress) * partialTick) * 0.2F;
    }

    @Override
    public float getStepHeight() {
        return this.hasRunningAttributes ? 1.1F : 0.6F;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    private class CaniacMeleeGoal extends Goal {

        private float chaseTime = 0.0F;

        public CaniacMeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = CaniacServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void stop() {
            CaniacServant.this.setRunning(false);
            CaniacServant.this.setArmSpinSpeed(0.0F);
            this.chaseTime = 0.0F;
        }

        @Override
        public void tick() {
            LivingEntity target = CaniacServant.this.getTarget();
            if (target != null && target.isAlive()) {
                double distance = CaniacServant.this.distanceTo(target);
                double attackDistance = CaniacServant.this.getBbWidth() + target.getBbWidth();
                CaniacServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (CaniacServant.this.getAnimation() == ANIMATION_LUNGE) {
                    CaniacServant.this.setArmSpinSpeed(0.0F);
                    CaniacServant.this.getNavigation().stop();
                    if (CaniacServant.this.getAnimationTick() > 15) {
                        target.hasImpulse = true;
                        Vec3 delta = CaniacServant.this.position().subtract(target.position());
                        if (distance < 10.0) {
                            target.setDeltaMovement(target.getDeltaMovement().scale(0.3F).add(delta.scale(0.1)));
                            if (CaniacServant.this.getAnimationTick() > 19 && CaniacServant.this.getAnimationTick() <= 22 && CaniacServant.this.hasLineOfSight(target) && distance < 3.5) {
                                target.hurt(CaniacServant.this.damageSources().mobAttack(CaniacServant.this), 3.0F);
                            }
                        }
                    } else if (CaniacServant.this.getAnimationTick() > 10 && CaniacServant.this.getAnimationTick() <= 13) {
                        CaniacServant.this.hasImpulse = true;
                        Vec3 delta = target.position().subtract(CaniacServant.this.position()).normalize();
                        CaniacServant.this.setDeltaMovement(CaniacServant.this.getDeltaMovement().add(delta.scale(1.3F).add(0.0, 0.35, 0.0)));
                    }
                } else {
                    this.chaseTime += 1.0F;
                    CaniacServant.this.setArmSpinSpeed(Math.min(30.0F, this.chaseTime * 5.0F));
                    if (distance > attackDistance) {
                        CaniacServant.this.getNavigation().moveTo(target, 1.0);
                        CaniacServant.this.setRunning(true);
                        if (distance < 12.0 && distance > 4.0 && CaniacServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION && CaniacServant.this.getRandom().nextInt(15) == 0) {
                            CaniacServant.this.syncAnimation(ANIMATION_LUNGE);
                        }
                    } else {
                        CaniacServant.this.setRunning(false);
                    }
                }
            }
        }
    }
}
