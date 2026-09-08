package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.interfaces.IHasCustomExplosion;
import com.alexander.mutantmore.interfaces.IHeatSource;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.pathfinding.ImprovedMoveControl;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant.MutantBlazeServantFlyStrafeMovementGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant.MutantBlazeServantSoundInstance;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.CreateBlazeBurnerCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import org.joml.Vector3d;

import javax.annotation.Nullable;

public class MutantBlazeServant extends AbstractMutantServant implements IHeatSource, IHasCustomExplosion {
    private static final List<EntityDataAccessor<Boolean>> SHIELDS_BROKEN;
    private static final EntityDataAccessor<Boolean> FLYING;
    public final AnimationState introAnimation = new AnimationState();
    public final AnimationState noveltyAnimation = new AnimationState();
    public final AnimationState shootAnimation = new AnimationState();
    public final AnimationState rodShotAnimation = new AnimationState();
    public final AnimationState stunnedAnimation = new AnimationState();
    public final AnimationState deathAnimation = new AnimationState();
    private static final EntityDataAccessor<Boolean> BULWARK_FOCUS =
            SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BLAZING_HELM =
            SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> UNHOLY_BLOOD =
            SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN);
    private int unholyBloodInvulnTime = 0;
    private int[] shieldHitCounts = new int[7];
    private static final UUID BULWARK_HEALTH_UUID = UUID.fromString("d4444444-0000-4000-8000-000000000001");
    private static final UUID BLAZING_HELM_HEALTH_UUID = UUID.fromString("e5555555-0000-4000-8000-000000000001");
    private static final UUID UNHOLY_BLOOD_HEALTH_UUID = UUID.fromString("f6666666-0000-4000-8000-000000000001");
    public int introAnimationTick;
    public int introAnimationLength = 25;
    public int noveltyAnimationTick;
    public int noveltyAnimationLength = 80;
    public int shootAnimationTick;
    public int shootAnimationLength = 70;
    public int shootAnimationActionPoint = 30;
    public int rodShotAnimationTick;
    public int rodShotAnimationLength = 105;
    public int rodShotAnimationActionStartPoint = 34;
    public int rodShotAnimationActionEndPoint = 21;
    public int[] shieldRegenerateTicks = new int[]{0, 0, 0, 0, 0, 0, 0};
    public int shieldRegenerateTime;
    public int stunnedTicks;
    public int stunnedLength;
    public int stunnedAnimationActionPoint;
    private boolean enragedParticlesDone = false;
    public static final int MAX_RODLING_SERVANTS = 7;
    public DamageSource killedBy;
    private static final int HEAT_BLOCK_SCAN_INTERVAL = 10;
    private static final int FURNACE_TOP_UP = 200;
    private int heatBlockScanTick;
    public static EntityDimensions crouchingDimensions;
    public MutantBlazeServantShieldPart[] subEntities;
    private static final Predicate<Entity> SHOCKWAVABLE;
    private static final ForgeConfigSpec.BooleanValue DISABLED;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DISABLED = builder.define("no_grief", false);
        builder.build();
    }

    public MutantBlazeServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
        this.shieldRegenerateTime = MutantBlazeCommonConfig.shield_regenerate_speed.get();
        this.stunnedLength = 203;
        this.stunnedAnimationActionPoint = 20;
        this.killedBy = this.damageSources().cramming();
        this.subEntities = new MutantBlazeServantShieldPart[7];
        this.moveControl = new ImprovedMoveControl(this, 90.0F, 3);
        this.xpReward = MutantBlazeCommonConfig.exp_reward.get();
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);

        for (int i = 0; i < 7; ++i) {
            MutantBlazeServantShieldPart shield = new MutantBlazeServantShieldPart(this, i);
            this.subEntities[i] = shield;
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if (MutantBlazeCommonConfig.uses_fly.get()) {
            this.goalSelector.addGoal(2, new MutantBlazeServantFlyStrafeMovementGoal(this));
        }
        if (MutantBlazeCommonConfig.uses_fireball_attack.get()) {
            this.goalSelector.addGoal(3, new com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant.MutantBlazeServantShootAttackGoal(this));
        }
        if (MutantBlazeCommonConfig.uses_rod_shot.get()) {
            this.goalSelector.addGoal(4, new com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant.MutantBlazeServantRodShotAttackGoal(this));
        }
        this.goalSelector.addGoal(5, new ApproachTargetGoal(this, MutantBlazeCommonConfig.following_target_distance.get(), MutantBlazeCommonConfig.following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(6, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this)).setUnseenMemoryTicks(6000));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false,
                entity -> com.Polarice3.Goety.utils.MobUtil.isOwnedTargetable(this, entity)) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate(MutantBlazeCommonConfig.follow_non_player_distance.get(),
                        MutantBlazeCommonConfig.follow_non_player_distance.get(), MutantBlazeCommonConfig.follow_non_player_distance.get());
            }
        });
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.MutantBlazeServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof MutantBlazeServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getMasterOwner() == player) {
            if (this.getHealth() < this.getMaxHealth()) {
                int healAmount = 0;
                if (itemstack.is(Items.BLAZE_ROD)) {
                    healAmount = 2;
                } else if (itemstack.is(Items.BLAZE_POWDER)) {
                    healAmount = 1;
                } else if (itemstack.is(Items.COAL) || itemstack.is(Items.CHARCOAL)) {
                    healAmount = 2;
                }
                if (healAmount > 0) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.heal(healAmount);
                    this.playSound(SoundEventInit.MUTANT_BLAZE_IDLE.get(), 1.0F, 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getRandomX(1.0D), this.getY() + 0.5D, this.getRandomZ(1.0D),
                                    0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.BULWARK_FOCUS.get()) && !this.hasBulwarkFocus()) {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.setBulwarkFocus(true);
                this.applyEnhancementModifiers();
                this.heal(20.0F);
                this.playSound(SoundEventInit.MUTANT_BLAZE_FLARE.get(), 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.BLAZING_HELM.get()) && !this.hasBlazingHelm()) {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.setBlazingHelm(true);
                this.applyEnhancementModifiers();
                this.heal(10.0F);
                this.playSound(SoundEventInit.MUTANT_BLAZE_FLARE.get(), 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.UNHOLY_BLOOD.get()) && !this.hasUnholyBlood()) {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.setUnholyBlood(true);
                this.applyEnhancementModifiers();
                this.heal(30.0F);
                this.playSound(SoundEventInit.MUTANT_BLAZE_FLARE.get(), 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    boolean canTarget(Entity target) {
        return target instanceof LivingEntity living
                && com.Polarice3.Goety.utils.MobUtil.isOwnedTargetable(this, living);
    }

    protected PathNavigation createNavigation(Level p_33348_) {
        return new MutantBlazeServantNavigation(this, p_33348_);
    }

    public NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return null;
    }

    public boolean shouldBeStationary() {
        return this.deathTime > 0 || this.introAnimationTick > 0 || this.stunnedTicks > 0;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    public int rodlingServantCount() {
        return this.level().getEntitiesOfClass(RodlingServant.class,
                        this.getBoundingBox().inflate(64.0D),
                        rodling -> rodling.isAlive() && this.getUUID().equals(rodling.getOwnerId()))
                .size();
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantHealth.get())
                .add(Attributes.ARMOR, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, com.qiuyue.goetyominous.config.AttributesConfig.MutantBlazeServantMovementSpeed.get());
    }

    public void onMutated() {
        this.setPose(Pose.CROUCHING);
        this.introAnimationTick = this.introAnimationLength;
        this.level().broadcastEntityEvent(this, (byte)11);
    }

    protected void updateAnimations() {
        this.introAnimation.animateWhen(this.introAnimationTick > 0 && this.isAlive(), this.tickCount);
        this.noveltyAnimation.animateWhen(this.noveltyAnimationTick > 0 && this.isAlive(), this.tickCount);
        this.shootAnimation.animateWhen(this.shootAnimationTick > 0 && this.isAlive(), this.tickCount);
        this.rodShotAnimation.animateWhen(this.rodShotAnimationTick > 0 && this.isAlive(), this.tickCount);
        this.stunnedAnimation.animateWhen(this.stunnedTicks > 0 && this.isAlive(), this.tickCount);
        this.deathAnimation.animateWhen(this.isDeadOrDying(), this.tickCount);
    }

    public void tickDownAnimTimers() {
        if (this.noveltyAnimationTick > 0) {
            --this.noveltyAnimationTick;
        }

        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }

        if (this.shootAnimationTick > 0) {
            --this.shootAnimationTick;
        }

        if (this.rodShotAnimationTick > 0) {
            --this.rodShotAnimationTick;
        }

    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)SoundEventInit.MUTANT_BLAZE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return (SoundEvent)SoundEventInit.MUTANT_BLAZE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public float getVoicePitch() {
        return this.isDeadOrDying() ? 1.0F : super.getVoicePitch();
    }

    protected float getSoundVolume() {
        return this.isDeadOrDying() ? 3.0F : 2.0F;
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().play(
                    new MutantBlazeServantSoundInstance(this, SoundEventInit.MUTANT_BLAZE_LOOP.get(),
                            this.getSoundSource(), 0.0F, 0.0F, 0.0F, false, 2.0F, 1.0F));
        }
    }

    public void shootMutantBlazeFireball(double xMotion, double yMotion, double zMotion, boolean fromDeath) {
        if (this.hasUnholyBlood()) {
            GiantHellBlast blast = new GiantHellBlast(this.level(), this, xMotion, yMotion, zMotion);
            blast.explosionDamage = MutantBlazeCommonConfig.fireball_explosion_damage.get().floatValue();
            blast.explosionRadius = MutantBlazeCommonConfig.fireball_explosion_radius.get().floatValue();
            blast.explosionFire = MutantBlazeCommonConfig.fireball_explosion_fire.get();
            blast.spawnedByDyingMutantBlaze = fromDeath;
            blast.setPos(blast.getX(), this.getY(0.5), blast.getZ());
            if (!fromDeath) {
                this.playSound(SoundEventInit.MUTANT_BLAZE_SHOOT.get(), 2.0F, this.getVoicePitch());
            }
            this.level().addFreshEntity(blast);
            return;
        }
        MutantBlazeServantFireball fireball = new MutantBlazeServantFireball(this.level(), this, xMotion, yMotion, zMotion);
        fireball.damage = MutantBlazeCommonConfig.fireball_damage.get().floatValue();
        fireball.explosionDamage = MutantBlazeCommonConfig.fireball_explosion_damage.get().floatValue();
        fireball.explosionRadius = MutantBlazeCommonConfig.fireball_explosion_radius.get().floatValue();
        fireball.fireLength = MutantBlazeCommonConfig.fireball_fire_length.get();
        fireball.ignoresInvulTime = MutantBlazeCommonConfig.ignores_invulnerability_time.get();
        fireball.griefing = false;
        fireball.explosionFire = MutantBlazeCommonConfig.fireball_explosion_fire.get();
        fireball.setPos(fireball.getX(), this.getY(0.5), fireball.getZ());
        if (fromDeath) {
            fireball.spawnedByDyingMutantBlaze = true;
        } else {
            this.playSound(SoundEventInit.MUTANT_BLAZE_SHOOT.get(), 2.0F, this.getVoicePitch());
        }
        this.level().addFreshEntity(fireball);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public boolean canHarmWithExplosion(Entity target) {
        return this.explosionCanHarm(target);
    }

    boolean explosionCanHarm(Entity target) {
        if (target instanceof MutantBlazeServant) {
            return false;
        }
        return !com.Polarice3.Goety.utils.MobUtil.areAllies(this, target);
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (this.hasUnholyBlood()) {
            if (this.unholyBloodInvulnTime > 0) {
                return false;
            }
            boolean inNether = this.level().dimension() == Level.NETHER;
            p_21017_ = inNether ? p_21017_ * 0.5F : p_21017_ * 0.85F;
            if (p_21017_ <= 0.0F) {
                return false;
            }
        }
        boolean flag = super.hurt(p_21016_, p_21017_);
        if (flag && this.hasUnholyBlood()) {
            this.unholyBloodInvulnTime = 10;
        }
        return flag;
    }

    public void die(DamageSource source) {
        super.die(source);
        if (!source.is(DamageTypes.GENERIC_KILL) && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_PRE_DEATH.get(), this.getSoundVolume(), 1.0F);
        } else {
            this.playSound(SoundEvents.HOSTILE_DEATH, this.getSoundVolume(), this.getVoicePitch());
        }

        this.killedBy = source;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLastDamageSource() != null && (this.getLastDamageSource().is(DamageTypes.GENERIC_KILL) || this.getLastDamageSource().is(DamageTypes.FELL_OUT_OF_WORLD))) {
            if (this.deathTime == 20 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte)60);
                this.remove(RemovalReason.KILLED);
            }
        } else {
            if (this.deathTime >= 20 && this.deathTime < 180) {
                this.setDeltaMovement(0.0, 0.05, 0.0);
            } else if (this.deathTime >= 180) {
                this.setDeltaMovement(0.0, 0.0, 0.0);
            }

            if (this.deathTime == 180) {
                this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.MUTANT_BLAZE_CHARGE_SHOT, List.of((float)this.getId(), 0.0F, 2.5F, -1.0F)), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
                this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_FLARE.get(), 3.0F, this.getVoicePitch());
            }

            int i;
            if (this.deathTime == 200) {
                ShakeCameraEvent.shake(this.level(), 33, 0.025F, this.blockPosition(), 25);
                this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_DEATH.get(), 3.0F, 1.0F);

                for(i = 0; i < 1000; ++i) {
                    this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(0.5), this.getZ(), -1.0 + this.random.nextDouble() * 2.0, -0.5 + this.random.nextDouble() * 1.0, -1.0 + this.random.nextDouble() * 2.0);
                }

                for(i = 0; i < 500; ++i) {
                    this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY(0.5), this.getZ(), -1.0 + this.random.nextDouble() * 2.0, -0.5 + this.random.nextDouble() * 1.0, -1.0 + this.random.nextDouble() * 2.0);
                }

                for(i = 0; i < 250; ++i) {
                    this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(0.5), this.getZ(), -1.0 + this.random.nextDouble() * 2.0, -0.5 + this.random.nextDouble() * 1.0, -1.0 + this.random.nextDouble() * 2.0);
                }

                if (!this.level().isClientSide) {
                    if (MutantBlazeCommonConfig.explode_into_parts.get()
                            && !MutantMoreGroupedOptionsCommonConfig.mutants_explode_into_parts_off.get()) {
                        com.alexander.mutantmore.entities.MutantBlazeRodProjectile rod;
                        for (int j = 0; j < MutantBlazeCommonConfig.part_amount.get(); ++j) {
                            rod = new com.alexander.mutantmore.entities.MutantBlazeRodProjectile(this.level(), this);
                            rod.damage = MutantBlazeCommonConfig.part_collision_damage.get().floatValue();
                            rod.fireLength = MutantBlazeCommonConfig.part_fire_length.get();
                            rod.glows = MutantBlazeCommonConfig.parts_glow.get();
                            rod.despawnTime = MutantBlazeCommonConfig.part_persist_length.get();
                            rod.ignoresInvulTime = MutantBlazeCommonConfig.ignores_invulnerability_time.get();
                            rod.setCollectable(true);
                            rod.moveTo(this.getX(), this.getY(0.6), this.getZ());
                            rod.shoot(0.0, 0.0, 0.0, 3.0F, 20.0F);
                            this.level().addFreshEntity(rod);
                        }
                    } else {
                        com.alexander.mutantmore.entities.MutantBlazeRodProjectile rod;
                        for (int j = 0; j < MutantBlazeCommonConfig.rodling_part_amount.get(); ++j) {
                            rod = new com.alexander.mutantmore.entities.MutantBlazeRodProjectile(this.level(), this);
                            rod.setCollectable(true);
                            rod.moveTo(this.getX(), this.getY(0.6), this.getZ());
                            this.level().addFreshEntity(rod);
                        }
                    }

                    Vec3 motion;
                    for(i = 0; i < 3; ++i) {
                        motion = PositionUtils.getOffsetMotion((Entity)null, 0.5, -1.25, 0.0, 0.0F, (float)(120 * i));
                        this.shootMutantBlazeFireball(motion.x, motion.y, motion.z, true);
                    }

                    for(i = 0; i < 10; ++i) {
                        motion = PositionUtils.getOffsetMotion((Entity)null, 1.0, -0.75, 0.0, 0.0F, (float)(36 * i));
                        this.shootMutantBlazeFireball(motion.x, motion.y, motion.z, true);
                    }

                    for(i = 0; i < 10; ++i) {
                        motion = PositionUtils.getOffsetMotion((Entity)null, 1.0, 0.0, 0.0, 0.0F, (float)(36 * i));
                        this.shootMutantBlazeFireball(motion.x, motion.y, motion.z, true);
                    }

                    for(i = 0; i < 10; ++i) {
                        motion = PositionUtils.getOffsetMotion((Entity)null, 1.0, 0.75, 0.0, 0.0F, (float)(36 * i));
                        this.shootMutantBlazeFireball(motion.x, motion.y, motion.z, true);
                    }

                    for(i = 0; i < 3; ++i) {
                        motion = PositionUtils.getOffsetMotion((Entity)null, 0.5, 1.25, 0.0, 0.0F, (float)(120 * i));
                        this.shootMutantBlazeFireball(motion.x, motion.y, motion.z, true);
                    }
                }
            }

            if (this.deathTime == 215) {
                if (!this.level().isClientSide()) {
                    if (this.killedBy != null) {
                        this.dropAllTickDeathLoot(this.killedBy);
                    }
                    this.spawnAtLocation(new ItemStack(com.alexander.mutantmore.init.ItemInit.MUTANT_BLAZE_CORE.get()));
                    this.remove(RemovalReason.KILLED);
                }

                for(i = 0; i < 20; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02;
                    double d1 = this.random.nextGaussian() * 0.02;
                    double d2 = this.random.nextGaussian() * 0.02;
                    this.level().addParticle(ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d0, d1, d2);
                }
            }
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
        this.dropExperience();
        Collection<ItemEntity> drops = this.captureDrops((Collection)null);
        if (!ForgeHooks.onLivingDrops(this, source, drops, i, this.lastHurtByPlayerTime > 0)) {
            drops.forEach((e) -> {
                this.level().addFreshEntity(e);
            });
        }

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

    protected void defineSynchedData() {
        super.defineSynchedData();
        Iterator var1 = SHIELDS_BROKEN.iterator();

        while(var1.hasNext()) {
            EntityDataAccessor<Boolean> SHIELD_BROKEN = (EntityDataAccessor)var1.next();
            this.entityData.define(SHIELD_BROKEN, false);
        }

        this.entityData.define(FLYING, false);
        this.entityData.define(BULWARK_FOCUS, false);
        this.entityData.define(BLAZING_HELM, false);
        this.entityData.define(UNHOLY_BLOOD, false);
    }

    public boolean isFlying() {
        return (Boolean)this.entityData.get(FLYING);
    }

    public void setFlying(boolean setTo) {
        this.entityData.set(FLYING, setTo);
    }

    public void readAdditionalSaveData(CompoundTag p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        MutantBlazeServantShieldPart[] var2 = this.subEntities;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            MutantBlazeServantShieldPart part = var2[var4];
            this.setShieldBroken(part.partNumber, p_70037_1_.getBoolean("Shield" + part.partNumber + "Broken"));
        }

        this.setBulwarkFocus(p_70037_1_.getBoolean("BulwarkFocus"));
        this.setBlazingHelm(p_70037_1_.getBoolean("BlazingHelm"));
        this.setUnholyBlood(p_70037_1_.getBoolean("UnholyBlood"));
    }

    public void addAdditionalSaveData(CompoundTag p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        MutantBlazeServantShieldPart[] var2 = this.subEntities;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            MutantBlazeServantShieldPart part = var2[var4];
            p_213281_1_.putBoolean("Shield" + part.partNumber + "Broken", this.isShieldBroken(part.partNumber));
        }
        p_213281_1_.putBoolean("BulwarkFocus", this.hasBulwarkFocus());
        p_213281_1_.putBoolean("BlazingHelm", this.hasBlazingHelm());
        p_213281_1_.putBoolean("UnholyBlood", this.hasUnholyBlood());

    }

    public boolean hasBulwarkFocus() { return this.entityData.get(BULWARK_FOCUS); }
    public void setBulwarkFocus(boolean value) { this.entityData.set(BULWARK_FOCUS, value); }
    public boolean hasBlazingHelm() { return this.entityData.get(BLAZING_HELM); }
    public void setBlazingHelm(boolean value) { this.entityData.set(BLAZING_HELM, value); }
    public boolean hasUnholyBlood() { return this.entityData.get(UNHOLY_BLOOD); }
    public void setUnholyBlood(boolean value) { this.entityData.set(UNHOLY_BLOOD, value); }

    private void addModIfMissing(AttributeInstance instance, UUID uuid, String name, double value) {
        if (instance != null && instance.getModifier(uuid) == null) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, value, AttributeModifier.Operation.ADDITION));
        }
    }

    private void applyEnhancementModifiers() {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (this.hasBulwarkFocus()) {
            this.addModIfMissing(health, BULWARK_HEALTH_UUID, "Bulwark Focus Health", MobsConfig.MBBulwarkFocusHealthBouns.get());
        }
        if (this.hasBlazingHelm()) {
            this.addModIfMissing(health, BLAZING_HELM_HEALTH_UUID, "Blazing Helm Health", MobsConfig.MBBlazingHelmHealthBouns.get());
        }
        if (this.hasUnholyBlood()) {
            this.addModIfMissing(health, UNHOLY_BLOOD_HEALTH_UUID, "Unholy Blood Health", MobsConfig.MBUnholyBloodHealthBouns.get());
        }
    }

    public boolean onShieldPartHit(int partNumber) {
        if (!this.hasBulwarkFocus()) {
            return true;
        }
        this.shieldHitCounts[partNumber]++;
        if (this.shieldHitCounts[partNumber] >= 3) {
            this.shieldHitCounts[partNumber] = 0;
            return true;
        }
        return false;
    }

    public boolean isShieldBroken(int shieldIndex) {
        return (Boolean)this.entityData.get((EntityDataAccessor)SHIELDS_BROKEN.get(shieldIndex));
    }

    public void setShieldBroken(int shieldIndex, boolean broken) {
        if (broken) {
            this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_SHIELDBREAK.get());
            this.shieldRegenerateTicks[shieldIndex] = this.shieldRegenerateTime;
        }

        this.entityData.set((EntityDataAccessor)SHIELDS_BROKEN.get(shieldIndex), broken);
        this.shieldHitCounts[shieldIndex] = 0;
    }

    public boolean isSensitiveToWater() {
        return !this.hasBlazingHelm();
    }

    protected void updateParts() {
        MutantBlazeServantShieldPart[] var1 = this.subEntities;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MutantBlazeServantShieldPart part = var1[var3];
            if (!this.level().isClientSide && !this.shieldsInactive()) {
                if (this.shieldRegenerateTicks[part.partNumber] > 0) {
                    int var10002 = this.shieldRegenerateTicks[part.partNumber]--;
                }

                if (this.shieldRegenerateTicks[part.partNumber] == 20) {
                    this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_RESHIELD.get());
                }

                if (this.shieldRegenerateTicks[part.partNumber] == 0 && this.isShieldBroken(part.partNumber)) {
                    if (this.stunnedTicks <= 0) {
                        for(int i = 0; i < 3; ++i) {
                            ((ServerLevel)this.level()).sendParticles((SimpleParticleType)ParticleTypeInit.MUTANT_BLAZE_HEAL.get(), part.getRandomX(0.75), part.getRandomY(), part.getRandomZ(0.75), 1, 0.0, 0.0, 0.0, 0.0);
                        }
                    }

                    this.setShieldBroken(part.partNumber, false);
                }
            }

            part.refreshDimensions();
            this.positionShield(part);
        }

    }

    protected void movePart(MutantBlazeServantShieldPart part, double dX, double dY, double dZ) {
        Vector3d lastPos = new Vector3d(part.getX(), part.getY(), part.getZ());
        part.setPos(this.getX() + dX, this.getY() + dY, this.getZ() + dZ);
        part.xo = lastPos.x;
        part.yo = lastPos.y;
        part.zo = lastPos.z;
        part.xOld = lastPos.x;
        part.yOld = lastPos.y;
        part.zOld = lastPos.z;
    }

    protected void positionShield(MutantBlazeServantShieldPart shield) {
        double upperShieldLayerY = this.isCrouching() ? 0.75 : 2.0;
        double lowerShieldLayerY = 0.25;
        float shieldRotateSpeed = -0.2182F;
        if (shield.partNumber == 0) {
            this.movePart(shield, (double)(Mth.cos((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(45.0F + this.yBodyRot)) * 1.5F), upperShieldLayerY, (double)(Mth.sin((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(45.0F + this.yBodyRot)) * 1.5F));
        } else if (shield.partNumber == 1) {
            this.movePart(shield, (double)(Mth.cos((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(135.0F + this.yBodyRot)) * 1.5F), upperShieldLayerY, (double)(Mth.sin((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(135.0F + this.yBodyRot)) * 1.5F));
        } else if (shield.partNumber == 2) {
            this.movePart(shield, (double)(Mth.cos((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(225.0F + this.yBodyRot)) * 1.5F), upperShieldLayerY, (double)(Mth.sin((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(225.0F + this.yBodyRot)) * 1.5F));
        } else if (shield.partNumber == 3) {
            this.movePart(shield, (double)(Mth.cos((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(315.0F + this.yBodyRot)) * 1.5F), upperShieldLayerY, (double)(Mth.sin((float)this.tickCount * shieldRotateSpeed + MiscUtils.degToRad(315.0F + this.yBodyRot)) * 1.5F));
        } else if (shield.partNumber == 4) {
            this.movePart(shield, (double)Mth.cos((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(-30.0F + this.yBodyRot)) * 1.21875, lowerShieldLayerY, (double)Mth.sin((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(-30.0F + this.yBodyRot)) * 1.21875);
        } else if (shield.partNumber == 5) {
            this.movePart(shield, (double)Mth.cos((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(90.0F + this.yBodyRot)) * 1.21875, lowerShieldLayerY, (double)Mth.sin((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(90.0F + this.yBodyRot)) * 1.21875);
        } else if (shield.partNumber == 6) {
            this.movePart(shield, (double)Mth.cos((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(210.0F + this.yBodyRot)) * 1.21875, lowerShieldLayerY, (double)Mth.sin((float)this.tickCount * -shieldRotateSpeed + MiscUtils.degToRad(210.0F + this.yBodyRot)) * 1.21875);
        }

    }

    public boolean isMultipartEntity() {
        return true;
    }

    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    public boolean shieldsInactive() {
        return this.stunnedTicks > 0 || this.deathTime > 0;
    }

    public void aiStep() {
        this.updateParts();
        if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        if (this.level().isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level().playLocalSound(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        super.aiStep();
    }

    public static boolean canBurnBlock(BlockGetter level, Block block, BlockState blockstate, BlockPos blockpos) {
        return !(block instanceof AirBlock) && block.getExplosionResistance(blockstate, level, blockpos, (Explosion)null) <= 6.0F;
    }

    public EntityDimensions getDimensions(Pose p_19975_) {
        return p_19975_ == Pose.CROUCHING ? crouchingDimensions : super.getDimensions(p_19975_);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public boolean shouldCrouchAt(BlockPos pos) {
        boolean markedCrouching = false;

        for(int i = -2; i < 3; ++i) {
            for(int j = -2; j < 3; ++j) {
                if (!markedCrouching && this.level().getBlockState(pos.above(4).north(i).east(j)).isAir() && this.level().getBlockState(pos.above(3).north(i).east(j)).isAir()) {
                    markedCrouching = false;
                } else {
                    markedCrouching = true;
                }
            }
        }

        return markedCrouching;
    }

    private void shockwave() {
        Entity entity;
        if (this.isAlive()) {
            for(Iterator var1 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate((Double)MutantBlazeCommonConfig.stun_shockwave_range.get()), SHOCKWAVABLE).iterator(); var1.hasNext(); this.strongKnockback(entity)) {
                entity = (Entity)var1.next();
                if (this.canHurt(entity)) {
                    if ((Boolean)MutantBlazeCommonConfig.ignores_invulnerability_time.get()) {
                        entity.invulnerableTime = 0;
                    }

                    entity.hurt(this.damageSources().mobAttack(this), ((Double)MutantBlazeCommonConfig.stun_shockwave_damage.get()).floatValue());
                    entity.setSecondsOnFire((Integer)MutantBlazeCommonConfig.stun_shockwave_fire_length.get());
                    if (entity instanceof LivingEntity) {
                        MiscUtils.disableShield((LivingEntity)entity, (Integer)MutantBlazeCommonConfig.stun_shockwave_disable_shield_length.get());
                    }
                }
            }
        }

    }

    boolean canHurt(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_BLAZE_CANT_HURT, this, target, this, (Predicate)null);
    }

    private void strongKnockback(Entity p_213688_1_) {
        double d0 = p_213688_1_.getX() - this.getX();
        double d1 = p_213688_1_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        p_213688_1_.push(d0 / d2 * 4.0, 0.2, d1 / d2 * 4.0);
    }

    private void spawnUnholyEnrageParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int k = 0; k < 160; ++k) {
            float f2 = this.random.nextFloat() * 5.0F;
            float f1 = this.random.nextFloat() * ((float) Math.PI * 2F);
            double d1 = Mth.cos(f1) * f2;
            double d2 = 0.01D + this.random.nextFloat() * 0.9D;
            double d3 = Mth.sin(f1) * f2;
            serverLevel.sendParticles(
                    this.random.nextInt(3) == 0 ? ParticleTypes.SOUL : ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX() + d1 * 0.15D, this.getY(0.4D), this.getZ() + d3 * 0.15D,
                    0, d1, d2, d3, 0.35F);
        }
    }

    public void baseTick() {
        boolean enraged = this.hasUnholyBlood()
                && this.getHealth() > 0.0F
                && this.getHealth() <= this.getMaxHealth() * 0.5F;
        if (enraged && !this.enragedParticlesDone) {
            this.enragedParticlesDone = true;
            this.spawnUnholyEnrageParticles();
        } else if (!enraged) {
            this.enragedParticlesDone = false;
        }
        if (this.unholyBloodInvulnTime > 0) {
            --this.unholyBloodInvulnTime;
        }
        super.baseTick();
        this.refreshDimensions();
        this.tickDownAnimTimers();
        if (this.stunnedTicks > 0) {
            --this.stunnedTicks;
        }

        if (this.getTarget() != null && this.stunnedTicks <= this.stunnedLength - 240) {
            this.getLookControl().setLookAt(this.getTarget());
        }

        if (this.stunnedTicks == 181) {
            this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_STUN_SHIELDS_BREAK.get(), 1.5F, this.getVoicePitch());
        }

        if (this.stunnedTicks == 56) {
            this.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_STUN_END.get(), 2.25F, this.getVoicePitch());
        }

        if (this.stunnedTicks == this.stunnedAnimationActionPoint) {
            this.shockwave();
        }

        if (this.stunnedTicks == this.stunnedLength - 20 || this.deathTime == 1) {
            PartEntity[] var1 = this.getParts();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                PartEntity<?> shield = var1[var3];

                for(int i = 0; i < 20; ++i) {
                    this.level().addParticle((ParticleOptions)ParticleTypeInit.BROKEN_MUTANT_BLAZE_SHIELD.get(), shield.getRandomX(0.75), shield.getRandomY(), shield.getRandomZ(0.75), 0.0, 0.0, 0.0);
                }
            }
        }

        if (this.stunnedTicks == this.stunnedLength - 135) {
            for(int i = 0; i < this.shieldRegenerateTicks.length; ++i) {
                this.shieldRegenerateTicks[i] = 20;
            }
        }

        if (!this.shouldCrouchAt(this.blockPosition()) && !this.isInWall()) {
            this.setPose(Pose.STANDING);
        } else {
            this.setPose(Pose.CROUCHING);
        }

        if (this.isInFluidType()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && this.level().getFluidState(this.blockPosition().above()).isEmpty()) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            }
        }

        Vec3 velocity = this.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
        if (!this.level().isClientSide && groundSpeed <= 0.1F && this.noveltyAnimationTick <= 0 && this.random.nextInt(400) == 0 && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
            this.level().broadcastEntityEvent(this, (byte)5);
        }

        this.heatEnvironmentBlocks();
    }

    public double heatRange() {
        return this.shouldHeat() ? (Double)MutantBlazeCommonConfig.heat_range.get() : 0.0;
    }

    public double heatRangeY() {
        return this.shouldHeat() ? (Double)MutantBlazeCommonConfig.heat_range_y.get() : 0.0;
    }

    public double heatSpeed() {
        return (Double)MutantBlazeCommonConfig.heat_speed.get();
    }

    public boolean shouldHeat() {
        return this.deathTime <= 0 && this.stunnedTicks <= this.stunnedAnimationActionPoint + 30;
    }

    public boolean canHeat(LivingEntity heatTarget) {
        if (heatTarget.getType().is(EntityTypes.CANT_BE_HEATED) || heatTarget.getType().is(EntityTypes.MUTANT_BLAZE_CANT_HEAT)) {
            return false;
        }
        if (heatTarget instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        LivingEntity trueOwner = this.getTrueOwner();
        if (trueOwner != null && (heatTarget == trueOwner || com.Polarice3.Goety.utils.MobUtil.areAllies(trueOwner, heatTarget))) {
            return false;
        }
        if (heatTarget instanceof net.minecraft.world.entity.animal.Animal) {
            return false;
        }
        if (heatTarget.fireImmune() && !MutantBlazeCommonConfig.heats_fire_immune_mobs.get()) {
            return false;
        }
        return true;
    }

    private void heatEnvironmentBlocks() {
        double rangeX = this.heatRange();
        double rangeY = this.heatRangeY();
        if (rangeX <= 0.0 || this.level().isClientSide) {
            return;
        }
        if (this.heatBlockScanTick > 0) {
            --this.heatBlockScanTick;
            return;
        }
        this.heatBlockScanTick = HEAT_BLOCK_SCAN_INTERVAL;
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos center = this.blockPosition();
        int rx = Mth.floor(rangeX);
        int ry = Mth.floor(rangeY);
        for (BlockPos pos : BlockPos.betweenClosed(
                center.getX() - rx, center.getY() - ry, center.getZ() - rx,
                center.getX() + rx, center.getY() + ry, center.getZ() + rx)) {
            BlockState state = serverLevel.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof CampfireBlock) {
                if (!state.getValue(CampfireBlock.LIT)) {
                    serverLevel.setBlock(pos, state.setValue(CampfireBlock.LIT, true), 3);
                }
            } else if (block instanceof AbstractFurnaceBlock
                    && serverLevel.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace) {
                if (furnace.litTime < FURNACE_TOP_UP) {
                    furnace.litTime = FURNACE_TOP_UP;
                    furnace.litDuration = FURNACE_TOP_UP;
                    if (!state.getValue(AbstractFurnaceBlock.LIT)) {
                        serverLevel.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
                    }
                    furnace.setChanged();
                }
            } else if (CreateBlazeBurnerCompat.isBlazeBurner(block)) {
                CreateBlazeBurnerCompat.sustainBlazeBurner(serverLevel, pos);
            }
        }
    }

    public void handleEntityEvent(byte p_28844_) {
        if (p_28844_ == 11) {
            this.introAnimationTick = this.introAnimationLength;
        } else if (p_28844_ == 5) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
        } else if (p_28844_ == 4) {
            this.shootAnimationTick = this.shootAnimationLength;
        } else if (p_28844_ == 12) {
            this.stunnedTicks = this.stunnedLength;
        } else if (p_28844_ == 13) {
            this.rodShotAnimationTick = this.rodShotAnimationLength;
        } else {
            super.handleEntityEvent(p_28844_);
        }

    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_ != null && !p_204067_.isEmpty();
    }

    public boolean hasLineOfSight(Entity p_147185_) {
        if (p_147185_.level() != this.level()) {
            return false;
        } else {
            Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            Vec3 vec31 = new Vec3(p_147185_.getX(), p_147185_.getY() + 1.0, p_147185_.getZ());
            if (vec31.distanceTo(vec3) > 128.0) {
                return false;
            } else {
                return this.level().clip(new ClipContext(vec3, vec31, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this)).getType() == Type.MISS;
            }
        }
    }

    public void setId(int p_145769_1_) {
        super.setId(p_145769_1_);

        for(int i = 0; i < this.subEntities.length; ++i) {
            this.subEntities[i].setId(p_145769_1_ + i + 1);
        }

    }

    public TagKey<Block> walksThroughTag() {
        return Blocks.MUTANT_BLAZE_WALKS_THROUGH;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> showHealthBarConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> playBattleMusicConfig() {
        return DISABLED;
    }

    static {
        SHIELDS_BROKEN = Lists.newArrayList(new EntityDataAccessor[]{SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN), SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN)});
        FLYING = SynchedEntityData.defineId(MutantBlazeServant.class, EntityDataSerializers.BOOLEAN);
        crouchingDimensions = EntityDimensions.scalable(1.9F, 2.4F);
        SHOCKWAVABLE = Entity::isAlive;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantBlazeServant.this.shouldBeStationary();
        }
    }

    static class MutantBlazeServantNavigation extends GroundPathNavigation {
        public MutantBlazeServantNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new MutantBlazeServantNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }

        protected void trimPath() {
            super.trimPath();
            if (this.mob.isCrouching() || !((MutantBlazeServant)this.mob).shouldCrouchAt(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                for(int i = 0; i < this.path.getNodeCount(); ++i) {
                    Node node = this.path.getNode(i);
                    if (!this.mob.isCrouching() && ((MutantBlazeServant)this.mob).shouldCrouchAt(new BlockPos(node.x, node.y, node.z))) {
                        this.path.truncateNodes(i);
                        return;
                    }
                }

            }
        }
    }

    static class MutantBlazeServantNodeEvaluator extends WalkNodeEvaluator {
        MutantBlazeServantNodeEvaluator() {
        }

        public void prepare(PathNavigationRegion p_77620_, Mob p_77621_) {
            super.prepare(p_77620_, p_77621_);
            this.entityWidth = Mth.floor(p_77621_.getBbWidth() + 1.0F);
            this.entityHeight = Mth.floor(MutantBlazeServant.crouchingDimensions.height);
            this.entityDepth = Mth.floor(p_77621_.getBbWidth() + 1.0F);
        }

        public BlockPathTypes getBlockPathType(BlockGetter p_77576_, int p_77577_, int p_77578_, int p_77579_) {
            return p_77576_.getBlockState(new BlockPos(p_77577_, p_77578_, p_77579_)).is(Blocks.MUTANT_BLAZE_WALKS_THROUGH) && (Boolean)MutantBlazeCommonConfig.walk_griefing.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mob_griefing_off.get() ? BlockPathTypes.OPEN : getBlockPathTypeStatic(p_77576_, new BlockPos.MutableBlockPos(p_77577_, p_77578_, p_77579_));
        }
    }
}
