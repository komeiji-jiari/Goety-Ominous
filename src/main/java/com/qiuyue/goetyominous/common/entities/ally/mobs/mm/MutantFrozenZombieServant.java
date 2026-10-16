package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.ItemInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.interfaces.ISnowstormSource;
import com.alexander.mutantmore.util.HeatUtils;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieIcicleSlamGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieIcicleThrowAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieJumpGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieMeleeAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieRoarGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant.MutantFrozenZombieSnowThrowAttackGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class MutantFrozenZombieServant extends AbstractMutantServant implements ISnowstormSource {
    private static final EntityDataAccessor<Integer> REMAINING_LIVES = SynchedEntityData.defineId(MutantFrozenZombieServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIVES_LOST = SynchedEntityData.defineId(MutantFrozenZombieServant.class, EntityDataSerializers.INT);
    public float jumpingAnimationAmount;
    public float walkAnimationAmount;
    public float idleAnimationAmount = 1.0F;
    public float keyframeAnimationAmount;
    public float[][] heldPose;
    public AnimationState noveltyAnimationState = new AnimationState();
    public int noveltyAnimationTick;
    public final int noveltyAnimationLength = 60;
    public AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTick;
    public final int attackAnimationLength = 32;
    public final int attackAnimationActionPoint = 17;
    public AnimationState throwAnimationState = new AnimationState();
    public int throwAnimationTick;
    public final int throwAnimationLength = 42;
    public final int throwAnimationActionPoint = 19;
    public AnimationState roarAnimationState = new AnimationState();
    public int roarAnimationTick;
    public final int roarAnimationLength = 120;
    public final int roarAnimationActionPoint = 100;
    public AnimationState downAnimationState = new AnimationState();
    public int downAnimationTick;
    public float downYaw;
    public final int downAnimationLength = 100;
    public AnimationState introAnimationState = new AnimationState();
    public int introAnimationTick;
    public final int introAnimationLength = 45;
    public AnimationState iciclesAnimationState = new AnimationState();
    public int iciclesAnimationTick;
    public final int iciclesAnimationLength = 70;
    public final int iciclesAnimationActionPoint = 36;
    public AnimationState snowThrowAnimationState = new AnimationState();
    public int snowThrowAnimationTick;
    public final int snowThrowAnimationLength = 62;
    public final int snowThrowAnimationActionPoint = 20;
    public AnimationState jumpAnimationState = new AnimationState();
    public int jumpAnimationTick;
    public final int jumpAnimationLength = 7;
    public final int jumpAnimationActionPoint = 2;
    public AnimationState slamAnimationState = new AnimationState();
    public int slamAnimationTick;
    public final int slamAnimationLength = 12;
    public final int slamAnimationActionPoint = 10;
    public boolean jumping;
    public int jumpingFor;
    public boolean wantsToJump;
    public DamageSource deathDamageSource;
    public List<Entity> resurrectedMobs = Lists.newArrayList();
    public LivingEntity targetBeforeDeath;
    public int snowstormTime = 0;
    public DamageSource killedBy = this.damageSources().cramming();

    public MutantFrozenZombieServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
        this.xpReward = MutantFrozenZombieCommonConfig.exp_reward.get();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.MutantFrozenZombieServantLimit.get();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if (MutantFrozenZombieCommonConfig.uses_ice_smash.get()) {
            this.goalSelector.addGoal(2, new MutantFrozenZombieJumpGoal(this));
        }
        if (MutantFrozenZombieCommonConfig.uses_melee_attack.get()) {
            this.goalSelector.addGoal(3, new MutantFrozenZombieMeleeAttackGoal(this));
        }
        if (MutantFrozenZombieCommonConfig.uses_roar.get()) {
            this.goalSelector.addGoal(4, new MutantFrozenZombieRoarGoal(this));
        }
        if (MutantFrozenZombieCommonConfig.uses_snow_throw.get()) {
            this.goalSelector.addGoal(5, new MutantFrozenZombieSnowThrowAttackGoal(this));
        }
        if (MutantFrozenZombieCommonConfig.uses_icicle_slam.get()) {
            this.goalSelector.addGoal(6, new MutantFrozenZombieIcicleSlamGoal(this));
        }
        if (MutantFrozenZombieCommonConfig.uses_icicle_throw.get()) {
            this.goalSelector.addGoal(7, new MutantFrozenZombieIcicleThrowAttackGoal(this));
        }
        this.goalSelector.addGoal(8, new ApproachTargetGoal(this, MutantFrozenZombieCommonConfig.follow_target_wanted_distance.get(), MutantFrozenZombieCommonConfig.following_movement_speed_multiplier.get(), true) {
            @Override
            public boolean canUse() {
                return MutantFrozenZombieServant.this.snowstormTime <= 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new ApproachTargetGoal(this, 0.0, MutantFrozenZombieCommonConfig.following_movement_speed_multiplier_snowstorm.get(), true) {
            @Override
            public boolean canUse() {
                return MutantFrozenZombieServant.this.snowstormTime > 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(9, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this, new Class[0])).setUnseenMemoryTicks(6000));
        if (MutantFrozenZombieCommonConfig.attacks_players.get() && !MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, (entity) -> {
            return entity.getType().is(EntityTypes.MUTANT_FROZEN_ZOMBIE_TARGETS);
        }) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate(MutantFrozenZombieCommonConfig.follow_non_player_distance.get(), MutantFrozenZombieCommonConfig.follow_non_player_distance.get(), MutantFrozenZombieCommonConfig.follow_non_player_distance.get());
            }
        });
    }

    public boolean shouldBeStationary() {
        return this.downAnimationTick > 0 || this.introAnimationTick > 0;
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    boolean canTarget(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_FROZEN_ZOMBIE_CANT_TARGET, this, target, this, null);
    }

    public boolean canHarm(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_FROZEN_ZOMBIE_CANT_HURT, this, target, this, null);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MutantFrozenZombieServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MutantFrozenZombieServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MutantFrozenZombieServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MutantFrozenZombieServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MutantFrozenZombieServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MutantFrozenZombieServantMovementSpeed.get());
    }

    private boolean isRealDeath(DamageSource source) {
        return source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(ModDamageSource.DISMISSED) || source.is(DamageTypes.STARVE);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isDeadOrDying() && (pSource.is(ModDamageSource.DISMISSED) || pSource.is(DamageTypes.STARVE))) {
            this.deathDamageSource = pSource;
            this.killedBy = pSource;
            if (this.deathTime > 35) {
                this.deathTime = 35;
            }
            return true;
        }
        return super.hurt(pSource, pAmount);
    }

    public void die(DamageSource p_21014_) {
        this.targetBeforeDeath = this.getTarget();
        this.deathDamageSource = p_21014_;
        if (this.getRemainingLives() <= 0 || this.isRealDeath(p_21014_)) {
            super.die(p_21014_);
        }
        this.downYaw = this.getYRot();
        this.downAnimationTick = this.downAnimationLength;
        this.level().broadcastEntityEvent(this, (byte) 7);
        this.killedBy = p_21014_;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLastDamageSource() != null && (this.getLastDamageSource().is(DamageTypes.GENERIC_KILL) || this.getLastDamageSource().is(DamageTypes.FELL_OUT_OF_WORLD))) {
            if (this.deathTime == 20 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
        } else if (this.deathDamageSource != null && (this.getRemainingLives() <= 0 || this.isRealDeath(this.deathDamageSource))) {
            if (this.deathTime >= 35 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                if (this.killedBy != null) {
                    this.dropAllTickDeathLoot(this.killedBy);
                }
                this.remove(RemovalReason.KILLED);
            }
        } else {
            if (this.deathTime >= this.downAnimationLength) {
                this.setRemainingLives(this.getRemainingLives() - 1);
                this.setLivesLost(this.getLivesLost() + 1);
                this.setHealth(this.isOnFire() ? this.getMaxHealth() / 2.0F : this.getMaxHealth());
                this.deathTime = 0;
                if (this.targetBeforeDeath != null) {
                    this.setTarget(this.targetBeforeDeath);
                }
            }
        }
    }

    public boolean isInvulnerable() {
        return this.downAnimationTick > 0 ? true : super.isInvulnerable();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();

        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            EquipmentSlot equipmentslot;
            if (itemstack.is(Items.ROTTEN_FLESH) && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getY() + this.getBbHeight() + 0.3D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (item instanceof ArmorItem armoritem) {
                equipmentslot = armoritem.getEquipmentSlot();
            } else if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock) {
                equipmentslot = EquipmentSlot.HEAD;
            } else if (itemstack.is(ItemTags.SWORDS)
                    || itemstack.is(ItemTags.AXES)
                    || itemstack.is(ItemTags.PICKAXES)
                    || itemstack.is(ItemTags.SHOVELS)
                    || itemstack.is(ItemTags.HOES)
                    || itemstack.is(ItemTags.TOOLS)
                    || item instanceof PhilosophersMaceItem
                    || item instanceof TieredItem
                    || item instanceof ShieldItem
                    || isCataclysmWeapon(item)) {
                if (pPlayer.isShiftKeyDown()) {
                    equipmentslot = EquipmentSlot.OFFHAND;
                } else {
                    equipmentslot = EquipmentSlot.MAINHAND;
                }
            } else {
                return super.mobInteract(pPlayer, pHand);
            }

            ItemStack itemstack2 = this.getItemBySlot(equipmentslot);
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
            this.setItemSlot(equipmentslot, itemstack.copy());
            this.dropEquipment(equipmentslot, itemstack2);
            this.setGuaranteedDrop(equipmentslot);
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    private static boolean isCataclysmWeapon(Item item) {
        try {
            Class<?> clazz = Class.forName("com.github.L_Ender.cataclysm.items.Cataclysm_Weapon_Item");
            return clazz.isInstance(item);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected void dropAllDeathLoot(DamageSource source) {
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        this.spawnAtLocation(new ItemStack(ItemInit.MUTANT_FROZEN_ZOMBIE_SPIKES.get()));
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
        Collection<ItemEntity> drops = this.captureDrops((Collection) null);
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
            ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY() + (double) p_19986_, this.getZ(), p_19985_);
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

    public boolean canFreeze() {
        return false;
    }

    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return p_20122_.getEntity() != null && ModList.get().isLoaded("dungeons_mobs") && p_20122_.getEntity().getType() == ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("dungeons_mobs", "frozen_zombie")) && p_20122_.getEntity().getTeam() == this.getTeam() ? true : super.isInvulnerableTo(p_20122_);
    }

    public boolean canCollideWith(Entity p_20303_) {
        return p_20303_ instanceof IcicleSpikeServant ? false : super.canCollideWith(p_20303_);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.roarAnimationTick == this.roarAnimationActionPoint) {
            this.snowstormTime = MutantFrozenZombieCommonConfig.roar_snowstorm_duration.get();
        }
    }

    @Override
    public double snowstormRange() {
        return this.shouldHaveSnowstorm() ? MutantFrozenZombieCommonConfig.roar_snowstorm_range.get() : 0.0;
    }

    @Override
    public double snowstormRangeY() {
        return this.shouldHaveSnowstorm() ? MutantFrozenZombieCommonConfig.roar_snowstorm_range_y.get() : 0.0;
    }

    public boolean shouldHaveSnowstorm() {
        return this.deathTime <= 0 && this.snowstormTime > 0;
    }

    @Override
    public boolean canFreeze(LivingEntity heatTarget) {
        if (heatTarget.getType().is(EntityTypes.UNAFFECTED_BY_SNOWSTORMS)) {
            return false;
        }
        if (heatTarget.getType().is(EntityTypes.UNAFFECTED_BY_MUTANT_FROZEN_ZOMBIE_SNOWSTORMS)) {
            return false;
        }
        if (this.getTeam() != null && heatTarget.getTeam() != null && (this.getTeam() == heatTarget.getTeam() || this.getTeam().isAlliedTo(heatTarget.getTeam()))) {
            return false;
        }
        if (heatTarget.isSpectator()) {
            return false;
        }
        LivingEntity trueOwner = this.getTrueOwner();
        if (trueOwner != null && (heatTarget == trueOwner || MobUtil.areAllies(trueOwner, heatTarget))) {
            return false;
        }
        return !(heatTarget instanceof Player) || !((Player) heatTarget).isCreative();
    }

    public boolean notCurrentlyPlayingKeyframeAnimation() {
        return this.noveltyAnimationTick <= 0 && this.attackAnimationTick <= 0 && this.downAnimationTick <= 0 && this.iciclesAnimationTick <= 0 && this.introAnimationTick <= 0 && this.roarAnimationTick <= 0 && this.snowThrowAnimationTick <= 0 && this.throwAnimationTick <= 0 && this.jumpAnimationTick <= 0 && this.slamAnimationTick <= 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REMAINING_LIVES, MutantFrozenZombieCommonConfig.get_up_times.get());
        this.entityData.define(LIVES_LOST, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
        this.setRemainingLives(p_21450_.getInt("RemainingLives"));
        this.setLivesLost(p_21450_.getInt("LivesLost"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_21484_) {
        super.addAdditionalSaveData(p_21484_);
        p_21484_.putInt("RemainingLives", this.getRemainingLives());
        p_21484_.putInt("LivesLost", this.getLivesLost());
    }

    public int getRemainingLives() {
        return this.entityData.get(REMAINING_LIVES);
    }

    public void setRemainingLives(int value) {
        this.entityData.set(REMAINING_LIVES, value);
    }

    public int getLivesLost() {
        return this.entityData.get(LIVES_LOST);
    }

    public void setLivesLost(int value) {
        this.entityData.set(LIVES_LOST, value);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() == EffectInit.FREEZING.get() ? false : super.canBeAffected(p_21197_);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.downAnimationTick > 0) {
            this.setYRot(this.downYaw);
            this.setYHeadRot(this.downYaw);
            this.yRotO = this.downYaw;
            this.yHeadRotO = this.downYaw;
            this.yBodyRot = this.downYaw;
            this.yBodyRotO = this.downYaw;
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();
        FrostWalkerEnchantment.onEntityMoved(this, this.level(), this.blockPosition(), 1);
        if (this.snowstormTime > 0) {
            --this.snowstormTime;
        }
        HeatUtils.performSnowstorm(this, this.blockPosition(), this.level(), MutantFrozenZombieCommonConfig.roar_snowstorm_freeze_speed.get(), MutantFrozenZombieCommonConfig.roar_snowstorm_max_freeze_amount.get(), false);
        Vec3 velocity = this.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
        if (!this.level().isClientSide && groundSpeed <= 0.01F && this.noveltyAnimationTick <= 0 && this.random.nextInt(400) == 0 && this.notCurrentlyPlayingKeyframeAnimation() && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        if (!this.level().isClientSide && this.noveltyAnimationTick > 0 && groundSpeed > 0.01F) {
            this.noveltyAnimationTick = 0;
            this.level().broadcastEntityEvent(this, (byte) 5);
        }
        this.jumpingFor = this.jumping ? ++this.jumpingFor : 0;
        if (this.onGround() && this.jumping && this.jumpingFor >= 20) {
            this.jumping = false;
        }
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
        if (this.downAnimationTick > 0) {
            --this.downAnimationTick;
        }
        if (this.level().isClientSide && this.downAnimationTick <= 0) {
            this.downAnimationState.stop();
        }
        if (this.iciclesAnimationTick > 0) {
            --this.iciclesAnimationTick;
        }
        if (this.level().isClientSide && this.iciclesAnimationTick <= 0) {
            this.iciclesAnimationState.stop();
        }
        if (this.snowThrowAnimationTick > 0) {
            --this.snowThrowAnimationTick;
        }
        if (this.level().isClientSide && this.snowThrowAnimationTick <= 0) {
            this.snowThrowAnimationState.stop();
        }
        if (this.roarAnimationTick > 0) {
            --this.roarAnimationTick;
        }
        if (this.level().isClientSide && this.roarAnimationTick <= 0) {
            this.roarAnimationState.stop();
        }
        if (this.throwAnimationTick > 0) {
            --this.throwAnimationTick;
        }
        if (this.level().isClientSide && this.throwAnimationTick <= 0) {
            this.throwAnimationState.stop();
        }
        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }
        if (this.level().isClientSide && this.introAnimationTick <= 0) {
            this.introAnimationState.stop();
        }
        if (this.jumpAnimationTick > 0) {
            --this.jumpAnimationTick;
        }
        if (this.level().isClientSide && this.jumpAnimationTick <= 0) {
            this.jumpAnimationState.stop();
        }
        if (this.slamAnimationTick > 0) {
            --this.slamAnimationTick;
        }
        if (this.level().isClientSide && this.slamAnimationTick <= 0) {
            this.slamAnimationState.stop();
        }
    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
            this.noveltyAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5) {
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 6) {
            this.throwAnimationTick = this.throwAnimationLength;
            this.throwAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 7) {
            this.downYaw = this.getYRot();
            this.downAnimationTick = this.downAnimationLength;
            this.downAnimationState.start(this.tickCount);
        } else if (p_21375_ == 8) {
            this.attackAnimationTick = this.attackAnimationLength;
            this.attackAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 9) {
            this.iciclesAnimationTick = this.iciclesAnimationLength;
            this.iciclesAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 10) {
            this.roarAnimationTick = this.roarAnimationLength;
            this.roarAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 11) {
            this.snowThrowAnimationTick = this.snowThrowAnimationLength;
            this.snowThrowAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 14) {
            this.introAnimationTick = this.introAnimationLength;
            this.introAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 15) {
            this.jumpAnimationTick = this.jumpAnimationLength;
            this.jumpAnimationState.start(this.tickCount);
            this.jumping = true;
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 16) {
            this.slamAnimationTick = this.slamAnimationLength;
            this.slamAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.jumping = false;
            this.noveltyAnimationState.stop();
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_) {
        this.setRemainingLives(MutantFrozenZombieCommonConfig.get_up_times.get());
        MiscUtils.spawnWithPumpkinOnHalloween(this, p_21434_.getRandom());
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    @Override
    public void onMutated() {
        this.introAnimationTick = this.introAnimationLength;
        this.noveltyAnimationTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 14);
        if (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.SNOWBALL) {
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (this.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.SNOWBALL) {
            this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    public NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return null;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return MutantFrozenZombieCommonConfig.despawns;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantFrozenZombieServant.this.shouldBeStationary();
        }
    }
}
