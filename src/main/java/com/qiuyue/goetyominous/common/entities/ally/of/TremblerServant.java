package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TremblerServantRollGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import com.unusualmodding.opposing_force.registry.tags.OPDamageTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Month;

public class TremblerServant extends Summoned implements EliteVariant {
    private static final EntityDataAccessor<Boolean> ROLLING;
    private static final EntityDataAccessor<Integer> ROLL_COOLDOWN;
    private static final EntityDataAccessor<Integer> STUNNED_TICKS;
    private static final EntityDataAccessor<Boolean> TURBO;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState rollAnimationState = new AnimationState();
    public final AnimationState stunnedAnimationState = new AnimationState();

    public TremblerServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new TremblerServantMoveControl();
        this.lookControl = new TremblerServantLookControl(this);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TremblerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TremblerServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TremblerServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TremblerServantAttackDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.TremblerServantAttackKnockback.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TremblerServantKnockbackResistance.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(1, new TremblerServantRollGoal(this));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 1.0D, 110, 0.001F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public float maxUpStep() {
        return this.isRolling() ? 1.1F : 0.6F;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new TremblerServantBodyRotationControl(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ROLLING, false);
        this.entityData.define(ROLL_COOLDOWN, 60);
        this.entityData.define(STUNNED_TICKS, 0);
        this.entityData.define(TURBO, false);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.TremblerServantLimit.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() == player
                && stack.is(Items.SLIME_BALL)
                && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.heal(2.0F);
                this.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0F, 1.0F);
                this.gameEvent(GameEvent.EAT, this);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D + 0.1D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                0, d0, d1, d2, 0.5D);
                    }
                }
            }
            player.swing(hand);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compoundTag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compoundTag);

        RandomSource random = level.getRandom();
        if (random.nextInt(this.getEliteSpawnChance()) == 0) {
            this.setElite(true);
            this.setEliteStats(this);
        }

        return spawnData;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Turbo", this.isElite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setElite(compoundTag.getBoolean("Turbo"));
    }

    public boolean isRolling() {
        return this.entityData.get(ROLLING);
    }

    public void setRolling(boolean rolling) {
        this.entityData.set(ROLLING, rolling);
    }

    public int getRollCooldown() {
        return this.entityData.get(ROLL_COOLDOWN);
    }

    public void setRollCooldown(int cooldown) {
        this.entityData.set(ROLL_COOLDOWN, cooldown);
    }

    public void rollCooldown() {
        this.entityData.set(ROLL_COOLDOWN, 60);
    }

    public int getStunnedTicks() {
        return this.entityData.get(STUNNED_TICKS);
    }

    public void setStunnedTicks(int stunnedTicks) {
        this.entityData.set(STUNNED_TICKS, stunnedTicks);
    }

    public void stunnedTicks() {
        this.entityData.set(STUNNED_TICKS, 54);
    }

    @Override
    public boolean isElite() {
        return this.entityData.get(TURBO);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(TURBO, elite);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
        if (this.getStunnedTicks() <= 0 && this.getRollCooldown() > 0) {
            this.setRollCooldown(this.getRollCooldown() - 1);
        }
        if (this.getStunnedTicks() > 0) {
            this.setSprinting(false);
            this.setStunnedTicks(this.getStunnedTicks() - 1);
            this.level().broadcastEntityEvent(this, (byte) 39);
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(!this.isRolling(), this.tickCount);
        this.rollAnimationState.animateWhen(this.isRolling(), this.tickCount);
        this.stunnedAnimationState.animateWhen(this.getStunnedTicks() > 0, this.tickCount);
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f1 = Math.min(f * 16.0F, 1.0F);
        this.walkAnimation.update(f1, 0.4F);
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double) this.getBbWidth() * Math.sin((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6 - 0.3);
            double e = this.getY() + (double) this.getBbHeight() - 0.3;
            double f = this.getZ() + (double) this.getBbWidth() * Math.cos((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, d, e, f, 0.5D, 0.6D, 0.5D);
        }
    }

    @Override
    protected void blockedByShield(LivingEntity defender) {
        this.stunnedTicks();
        this.rollCooldown();
        this.setRolling(false);
        this.getNavigation().stop();
        defender.push(this);
        defender.hurtMarked = true;
        super.blockedByShield(defender);
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 39) {
            this.stunEffect();
        }
        super.handleEntityEvent(id);
    }

    public boolean isWithinYRange(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return Math.abs(target.getY() - this.getY()) < 3.0D;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.isRolling()
                && !damageSource.is(OPDamageTypeTags.DAMAGES_ROLLING_TREMBLER)
                && !damageSource.is(ModDamageSource.DISMISSED)
                && !damageSource.is(DamageTypes.STARVE)
                && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.playSound(OPSoundEvents.TREMBLER_BLOCK.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            return false;
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource damageSource) {
        return !this.isRolling();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        boolean valiant = this.random.nextInt(1000) == 0 && this.getName().getString().equalsIgnoreCase("valiant");
        LocalDate today = LocalDate.now();
        boolean aprilFools = today.getMonth() == Month.APRIL && today.getDayOfMonth() == 1;
        return valiant || aprilFools ? OPSoundEvents.TREMBLER_IDLE_FUNNY.get() : OPSoundEvents.TREMBLER_IDLE.get();
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.TREMBLER_HURT.get();
    }

    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.TREMBLER_DEATH.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        SoundType soundtype = state.getSoundType(this.level(), pos, this);
        if (this.isRolling()) {
            this.playSound(SoundEvents.DRIPSTONE_BLOCK_STEP, 0.4F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 0.9F);
            this.playSound(soundtype.getStepSound(), 0.1F, soundtype.getPitch());
        } else {
            this.playSound(OPSoundEvents.SLUG_SLIDE.get(), 0.15F, 0.9F);
        }
    }

    static {
        ROLLING = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.BOOLEAN);
        ROLL_COOLDOWN = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.INT);
        STUNNED_TICKS = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.INT);
        TURBO = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.BOOLEAN);
    }

    private class TremblerServantMoveControl extends MoveControl {
        public TremblerServantMoveControl() {
            super(TremblerServant.this);
        }

        @Override
        public void tick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.tick();
            }
        }
    }

    private class TremblerServantLookControl extends LookControl {
        public TremblerServantLookControl(TremblerServant trembler) {
            super(trembler);
        }

        @Override
        public void tick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.tick();
            }
        }
    }

    private class TremblerServantBodyRotationControl extends BodyRotationControl {
        public TremblerServantBodyRotationControl(TremblerServant trembler) {
            super(trembler);
        }

        @Override
        public void clientTick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.clientTick();
            }
        }
    }
}
