package com.qiuyue.goetyominus.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.init.ItemInit;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantHoglinServant.MutantHoglinChargeAttackGoal;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantHoglinServant.MutantHoglinKickAttackGoal;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantHoglinServant.MutantHoglinMeleeAttackGoal;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantHoglinServant.MutantHoglinStompAttackGoal;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.config.MobsConfig;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinClientConfig;
import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;

import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;

public class MutantHoglinServant extends AbstractMutantServant implements PlayerRideable {
    private static final ForgeConfigSpec.BooleanValue DISABLED;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DISABLED = builder.define("no_grief", false);
        builder.build();
    }
    public AnimationState noveltyAnimationState = new AnimationState();
    public int noveltyAnimationTick;
    public final int noveltyAnimationLength = 30;
    public AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTick;
    public final int attackAnimationLength = 22;
    public final int attackAnimationActionPoint = 13;
    public AnimationState stompAnimationState = new AnimationState();
    public int stompAnimationTick;
    public final int stompAnimationLength = 50;
    public final int stompAnimationActionPoint1 = 39;
    public final int stompAnimationActionPoint2 = 24;
    public final int stompAnimationActionPoint3 = 13;
    public AnimationState kickAnimationState = new AnimationState();
    public int kickAnimationTick;
    public final int kickAnimationLength = 45;
    public final int kickAnimationActionPoint = 24;
    public AnimationState prepareChargeAnimationState = new AnimationState();
    public int prepareChargeAnimationTick;
    public final int prepareChargeAnimationLength = 26;
    public AnimationState introAnimationState = new AnimationState();
    public int introAnimationTick;
    public final int introAnimationLength = 28;
    public AnimationState deathAnimationState = new AnimationState();
    public AnimationState danceAnimationState = new AnimationState();
    public final int deathAnimationActionPoint = 65;
    public boolean charging;
    public boolean dancing;
    public float enragedAmount;
    public int enrageDuration;
    public boolean wantsToCharge;
    public boolean riderChargeRequested;
    public DamageSource killedBy = this.damageSources().cramming();

    public MutantHoglinServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();

        if (this.getTrueOwner() == null || pPlayer != this.getTrueOwner()) {
            return InteractionResult.SUCCESS;
        }

        if (pPlayer.isShiftKeyDown() && item == Items.CRIMSON_FUNGUS && this.getHealth() < this.getMaxHealth()) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(SoundEvents.HOGLIN_AMBIENT, 1.0F, 1.0F);
            this.heal(2.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isVehicle()) {
            pPlayer.startRiding(this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            if (this.charging || this.prepareChargeAnimationTick > 0) {
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.25F);
                super.travel(new Vec3(0, 0, 1));
                return;
            }

            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float strafe = player.xxa * 0.5F;
            float forward = player.zza * 0.5F;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }

            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, 0.0, forward));
            return;
        }
        super.travel(pTravelVector);
    }

    public boolean isAutonomous() {
        return false;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected float getJumpPower() {
        return this.isVehicle() ? 0.0F : super.getJumpPower();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if (this.isVehicle()) {
            return false;
        }
        return super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    public Component getDismountMessage() {
        return Component.translatable("mount.onboard", this.getDisplayName());
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.6D;
    }

    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        if (this.isVehicle()) {
            Vec3 forward = PositionUtils.getOffsetMotion(this, 0.0, 0.0, 1.35, 0.0F, this.yBodyRot);
            pPassenger.setPos(pPassenger.getX() + forward.x, pPassenger.getY() + forward.y, pPassenger.getZ() + forward.z);
        }
    }

    public void triggerRiderCharge() {
        this.wantsToCharge = true;
        this.riderChargeRequested = true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if ((Boolean)MutantHoglinCommonConfig.uses_charge.get()) {
            this.goalSelector.addGoal(2, new MutantHoglinChargeAttackGoal(this));
        }

        if ((Boolean)MutantHoglinCommonConfig.uses_stomp_attack.get()) {
            this.goalSelector.addGoal(3, new MutantHoglinStompAttackGoal(this));
        }

        if ((Boolean)MutantHoglinCommonConfig.uses_basic_attack.get()) {
            this.goalSelector.addGoal(4, new MutantHoglinMeleeAttackGoal(this));
        }

        if ((Boolean)MutantHoglinCommonConfig.uses_kick_attack.get()) {
            this.goalSelector.addGoal(5, new MutantHoglinKickAttackGoal(this));
        }

        this.goalSelector.addGoal(6, new ApproachTargetGoal(this, (Double)MutantHoglinCommonConfig.follow_target_wanted_distance.get(), (Double)MutantHoglinCommonConfig.following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(7, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this, new Class[0])).setUnseenMemoryTicks(6000));
        if ((Boolean)MutantHoglinCommonConfig.attacks_players.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, (entity) -> {
            return entity.getType().is(EntityTypes.MUTANT_HOGLIN_TARGETS);
        }) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate((Double)MutantHoglinCommonConfig.follow_non_player_distance.get(), (Double)MutantHoglinCommonConfig.follow_non_player_distance.get(), (Double)MutantHoglinCommonConfig.follow_non_player_distance.get());
            }
        });
    }

    public boolean shouldBeStationary() {
        return this.introAnimationTick > 0;
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    boolean canTarget(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HOGLIN_CANT_TARGET, this, target, this, (Predicate)null);
    }

    public boolean canHarm(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HOGLIN_CANT_HURT, this, target, this, (Predicate)null);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MutantHoglinServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MutantHoglinServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MutantHoglinServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MutantHoglinServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MutantHoglinServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MutantHoglinServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MutantHoglinServantAttackDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.MutantHoglinServantAttackKnockback.get());
    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)SoundEventInit.MUTANT_HOGLIN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return (SoundEvent)SoundEventInit.MUTANT_HOGLIN_HURT.get();
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound((SoundEvent)SoundEventInit.MUTANT_HOGLIN_STEP.get(), 0.75F, 1.0F);
    }

    protected float getSoundVolume() {
        return 1.5F;
    }

    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return 2.0F;
    }

    public float getEnragedAmount() {
        return Mth.clamp(this.enragedAmount, 0.0F, 1.0F);
    }

    public boolean doHurtTarget(Entity p_34207_) {
        return !(p_34207_ instanceof LivingEntity) ? false : HoglinBase.hurtAndThrowTarget(this, (LivingEntity)p_34207_);
    }

    public boolean notCurrentlyPlayingKeyframeAnimation() {
        return this.noveltyAnimationTick <= 0 && this.introAnimationTick <= 0 && this.kickAnimationTick <= 0 && this.attackAnimationTick <= 0 && this.prepareChargeAnimationTick <= 0 && this.stompAnimationTick <= 0;
    }

    public void aiStep() {
        super.aiStep();
        if (this.charging) {
            Iterator var1 = this.level().getEntities(this, this.getBoundingBox()).iterator();

            while(var1.hasNext()) {
                Entity entity = (Entity)var1.next();
                if (this.canHarm(entity)) {
                    double d0 = entity.getX() - this.getX();
                    double d1 = entity.getZ() - this.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                    entity.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
                    entity.hurt(MMDamageTypes.cantAvoidMobAttack(this.damageSources(), this), ((Double)MutantHoglinCommonConfig.charge_attack_damage.get()).floatValue() * this.enragedDamageMultiplier());
                    if (entity instanceof LivingEntity) {
                        MiscUtils.disableShield((LivingEntity)entity, (Integer)MutantHoglinCommonConfig.charge_disable_shield_length.get());
                    }
                }
            }

        }

    }

    public float enragedSpeedMultiplier() {
        return 1.0F + this.enragedAmount * (Float)MutantHoglinCommonConfig.enraged_speed_multiplier.get();
    }

    public float enragedDamageMultiplier() {
        return 1.0F + this.enragedAmount * (Float)MutantHoglinCommonConfig.enraged_damage_multiplier.get();
    }

    public float enragedKnockbackMultiplier() {
        return 1.0F + this.enragedAmount * (Float)MutantHoglinCommonConfig.enraged_knockback_multiplier.get();
    }

    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.MutantHoglinServantMovementSpeed.get() * (double)this.enragedSpeedMultiplier());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.MutantHoglinServantAttackDamage.get() * (double)this.enragedDamageMultiplier());
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(AttributesConfig.MutantHoglinServantAttackKnockback.get() * (double)this.enragedKnockbackMultiplier());
        if (this.enrageDuration > 0) {
            --this.enrageDuration;
        }

        if (this.enrageDuration <= 0 && this.getEnragedAmount() > 0.0F) {
            float amount = this.enragedAmount = this.getEnragedAmount() - (Float)MutantHoglinCommonConfig.enrage_lower_speed.get();
            if (amount <= 0.0F) {
                this.enragedAmount = 0.0F;
            } else {
                this.enragedAmount = amount;
            }
        }

        Vec3 velocity = this.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
        if (!this.level().isClientSide && groundSpeed <= 0.01F && this.noveltyAnimationTick <= 0 && this.random.nextInt(400) == 0 && this.notCurrentlyPlayingKeyframeAnimation() && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            Objects.requireNonNull(this);
            this.noveltyAnimationTick = 30;
            this.level().broadcastEntityEvent(this, (byte)4);
        }

        if (!this.level().isClientSide && this.noveltyAnimationTick > 0 && groundSpeed > 0.01F) {
            this.noveltyAnimationTick = 0;
            this.level().broadcastEntityEvent(this, (byte)5);
        }

        if (this.level().isClientSide && this.charging) {
            ShakeCameraEvent.shake(this.level(), 6, 0.015F, this.blockPosition(), 10);
        }

    }

    public void enrage() {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)16);
        }

        this.enragedAmount = Mth.clamp(this.getEnragedAmount() + (Float)MutantHoglinCommonConfig.enrage_amount.get(), 0.0F, 1.0F);
        this.enrageDuration = Mth.clamp(this.enrageDuration + (Integer)MutantHoglinCommonConfig.enrage_duration.get(), 0, (Integer)MutantHoglinCommonConfig.max_enrage_duration.get());
    }

    public void tickDownAnimTimers() {
        if (this.noveltyAnimationTick > 0) {
            --this.noveltyAnimationTick;
        }

        if (this.level().isClientSide && this.noveltyAnimationTick <= 0) {
            this.noveltyAnimationState.stop();
        }

        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.level().isClientSide && this.attackAnimationTick <= 0) {
            this.attackAnimationState.stop();
        }

        if (this.kickAnimationTick > 0) {
            --this.kickAnimationTick;
        }

        if (this.level().isClientSide && this.kickAnimationTick <= 0) {
            this.kickAnimationState.stop();
        }

        if (this.prepareChargeAnimationTick > 0) {
            --this.prepareChargeAnimationTick;
        }

        if (this.level().isClientSide && this.prepareChargeAnimationTick <= 0) {
            this.prepareChargeAnimationState.stop();
        }

        if (this.stompAnimationTick > 0) {
            --this.stompAnimationTick;
        }

        if (this.level().isClientSide && this.stompAnimationTick <= 0) {
            this.stompAnimationState.stop();
        }

        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }

        if (this.level().isClientSide && this.introAnimationTick <= 0) {
            this.introAnimationState.stop();
        }

    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            Objects.requireNonNull(this);
            this.noveltyAnimationTick = 30;
            this.noveltyAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5) {
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 6) {
            Objects.requireNonNull(this);
            this.attackAnimationTick = 22;
            this.attackAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 7) {
            this.deathAnimationState.start(this.tickCount);
        } else if (p_21375_ == 8) {
            this.danceAnimationState.start(this.tickCount);
            this.dancing = true;
        } else if (p_21375_ == 9) {
            Objects.requireNonNull(this);
            this.kickAnimationTick = 45;
            this.kickAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 10) {
            Objects.requireNonNull(this);
            this.prepareChargeAnimationTick = 26;
            this.prepareChargeAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 11) {
            Objects.requireNonNull(this);
            this.stompAnimationTick = 50;
            this.stompAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 12) {
            this.charging = true;
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 13) {
            this.charging = false;
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 14) {
            Objects.requireNonNull(this);
            this.introAnimationTick = 28;
            this.introAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 15) {
            AABB griefingBoundingBox = this.getBoundingBox().inflate((Double)MutantHoglinCommonConfig.stomp_attack_range.get(), (Double)MutantHoglinCommonConfig.stomp_attack_range.get() * 3.0, (Double)MutantHoglinCommonConfig.stomp_attack_range.get());
            Iterator var3 = BlockPos.betweenClosed(Mth.floor(griefingBoundingBox.minX), Mth.floor(griefingBoundingBox.minY), Mth.floor(griefingBoundingBox.minZ), Mth.floor(griefingBoundingBox.maxX), Mth.floor(griefingBoundingBox.maxY), Mth.floor(griefingBoundingBox.maxZ)).iterator();

            while(var3.hasNext()) {
                BlockPos blockpos = (BlockPos)var3.next();
                BlockState blockstate = this.level().getBlockState(blockpos);
                if (!blockstate.isAir() && this.level().getBlockState(blockpos.above()).isAir() && this.random.nextBoolean()) {
                    this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), (double)blockpos.getX() + this.random.nextGaussian() * 0.5, (double)blockpos.above().getY() + 0.1, (double)blockpos.getZ() + this.random.nextGaussian() * 0.5, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
                }
            }
        } else if (p_21375_ == 16) {
            this.enrage();
        } else {
            super.handleEntityEvent(p_21375_);
        }

    }

    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        boolean flag = super.hurt(p_21016_, p_21017_);
        if (flag && !this.level().isClientSide) {
            this.enrage();
            if (p_21016_.getEntity() != null && p_21016_.getEntity() instanceof ServerPlayer && this.getEnragedAmount() >= 1.0F) {
                MiscUtils.awardMutantMoreAdvancement((ServerPlayer)p_21016_.getEntity(), "mutantmore/enrage_mutant_hoglin", "enraged");
            }
        }

        return flag;
    }

    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return p_20122_.is(DamageTypes.FALLING_BLOCK) ? true : super.isInvulnerableTo(p_20122_);
    }

    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        if (this.random.nextInt(100) == 0) {
            this.level().broadcastEntityEvent(this, (byte)8);
            this.dancing = true;
        } else {
            this.level().broadcastEntityEvent(this, (byte)7);
        }

        this.killedBy = p_21014_;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLastDamageSource() == null || !this.getLastDamageSource().is(DamageTypes.GENERIC_KILL) && !this.getLastDamageSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (!this.level().isClientSide) {
                if (this.dancing) {
                    if (this.deathTime == 140) {
                        this.level().broadcastEntityEvent(this, (byte)60);
                        if (this.killedBy != null) {
                            this.dropAllTickDeathLoot(this.killedBy);
                        }

                        this.remove(RemovalReason.KILLED);
                    }
                } else if (this.deathTime == 100) {
                    this.level().broadcastEntityEvent(this, (byte)60);
                    if (this.killedBy != null) {
                        this.dropAllTickDeathLoot(this.killedBy);
                    }

                    this.remove(RemovalReason.KILLED);
                }
            }

            int var10000 = this.deathTime;
            Objects.requireNonNull(this);
            if (var10000 == 65) {
                Iterator var2;
                if (!this.level().isClientSide) {
                    Vec3 particlePos = PositionUtils.getOffsetPos(this, 0.0, 0.0, 2.25, 0.0F, this.yBodyRot);
                    ((ServerLevel)this.level()).sendParticles((SimpleParticleType)ParticleTypeInit.SHOCKWAVE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                    var2 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate((Double)MutantHoglinCommonConfig.death_range.get(), (Double)MutantHoglinCommonConfig.death_range.get(), (Double)MutantHoglinCommonConfig.death_range.get()), MiscUtils.ALIVE).iterator();

                    while(true) {
                        Entity entity;
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    return;
                                }

                                entity = (Entity)var2.next();
                            } while(entity == this);
                        } while(!entity.onGround() && (!entity.isPassenger() || !entity.getRootVehicle().onGround()));

                        if (this.canHarm(entity)) {
                            if ((Boolean)MutantHoglinCommonConfig.ignores_invulnerability_time.get()) {
                                entity.invulnerableTime = 0;
                            }

                            entity.hurt(MMDamageTypes.earthquakeAttack(this.damageSources(), this), ((Double)MutantHoglinCommonConfig.death_damage.get()).floatValue());
                        }

                        entity.push(this.getRandom().nextGaussian() * 0.75, 1.0, this.getRandom().nextGaussian() * 0.75);
                    }
                } else {
                    ShakeCameraEvent.shake(this.level(), 6, 0.15F, this.blockPosition(), ((Double)MutantHoglinCommonConfig.death_range.get()).intValue());
                    AABB griefingBoundingBox = this.getBoundingBox().inflate((Double)MutantHoglinCommonConfig.death_range.get(), (Double)MutantHoglinCommonConfig.death_range.get() * 3.0, (Double)MutantHoglinCommonConfig.death_range.get());
                    var2 = BlockPos.betweenClosed(Mth.floor(griefingBoundingBox.minX), Mth.floor(griefingBoundingBox.minY), Mth.floor(griefingBoundingBox.minZ), Mth.floor(griefingBoundingBox.maxX), Mth.floor(griefingBoundingBox.maxY), Mth.floor(griefingBoundingBox.maxZ)).iterator();

                    while(var2.hasNext()) {
                        BlockPos blockpos = (BlockPos)var2.next();
                        BlockState blockstate = this.level().getBlockState(blockpos);
                        if (!blockstate.isAir() && this.level().getBlockState(blockpos.above()).isAir() && this.random.nextBoolean()) {
                            this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), (double)blockpos.getX() + this.random.nextGaussian() * 0.5, (double)blockpos.above().getY() + 0.1, (double)blockpos.getZ() + this.random.nextGaussian() * 0.5, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
                        }
                    }
                }
            }
        } else if (this.deathTime == 20 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }

    }

    protected void dropAllDeathLoot(DamageSource source) {
    }

    public void dropAllTickDeathLoot(DamageSource source) {
        Entity entity = source.getEntity();
        int i = ForgeHooks.getLootingLevel(this, entity, source);
        this.captureDrops(new ArrayList());
        boolean flag = this.lastHurtByPlayerTime > 0;
        this.dropFromLootTable(source, flag);
        this.dropCustomDeathLoot(source, i, flag);
        this.dropEquipment();
        Collection<ItemEntity> drops = this.captureDrops((Collection)null);
        if (!ForgeHooks.onLivingDrops(this, source, drops, i, this.lastHurtByPlayerTime > 0)) {
            drops.forEach((e) -> {
                this.level().addFreshEntity(e);
            });
        }

        ItemEntity tuskDrop = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                new ItemStack(ItemInit.MUTANT_HOGLIN_TUSK.get(), 2));
        this.level().addFreshEntity(tuskDrop);
    }

    public ItemEntity spawnAtLocation(ItemStack p_19985_, float p_19986_) {
        if (p_19985_.isEmpty()) {
            return null;
        } else if (this.level().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY() + (double)p_19986_, this.getZ(), p_19985_);
            itementity.setDefaultPickUpDelay();
            itementity.setExtendedLifetime();
            if (this.captureDrops() != null) {
                this.captureDrops().add(itementity);
            } else {
                this.level().addFreshEntity(itementity);
            }

            return itementity;
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.MutantHoglinServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof MutantHoglinServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    public void onMutated() {
        this.introAnimationTick = 28;
        this.noveltyAnimationTick = 0;
        this.level().broadcastEntityEvent(this, (byte)14);
    }

    public NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return null;
    }

    public TagKey<Block> walksThroughTag() {
        return Blocks.MUTANT_HOGLIN_WALKS_THROUGH;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> showHealthBarConfig() {
        return MutantHoglinClientConfig.show_health_bar;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return MutantHoglinCommonConfig.despawns;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantHoglinServant.this.shouldBeStationary();
        }
    }
}