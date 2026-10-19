package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_jungle_zombie.MutantJungleZombieCommonConfig;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant.MutantJungleZombieChokeAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant.MutantJungleZombieJumpGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant.MutantJungleZombiePunchAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant.MutantJungleZombieSlamAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant.MutantJungleZombieVineAttackGoal;
import com.qiuyue.goetyominous.common.items.mm.MmItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import com.qiuyue.goetyominous.config.mm.MmConfig;

public class MutantJungleZombieServant extends AbstractMutantServant {
    private static final EntityDataAccessor<Integer> REMAINING_LIVES = SynchedEntityData.defineId(MutantJungleZombieServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIVES_LOST = SynchedEntityData.defineId(MutantJungleZombieServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> GRAB_ARM_X_ROT = SynchedEntityData.defineId(MutantJungleZombieServant.class, EntityDataSerializers.FLOAT);
    public float walkAnimationAmount;
    public float idleAnimationAmount = 1.0F;
    public float restingAnimationAmount;
    public float keyframeAnimationAmount;
    public float[][] heldPose;
    public AnimationState noveltyAnimationState = new AnimationState();
    public int noveltyAnimationTick;
    public final int noveltyAnimationLength = 90;
    public AnimationState downAnimationState = new AnimationState();
    public int downAnimationTick;
    public float downYaw;
    public final int downAnimationLength = 160;
    public AnimationState introAnimationState = new AnimationState();
    public int introAnimationTick;
    public final int introAnimationLength = 40;
    public AnimationState punchAnimationState = new AnimationState();
    public int punchAnimationTick;
    public final int punchAnimationActionPointStart = 14;
    public final int punchAnimationActionPointStop = 9;
    public AnimationState tryChokeAnimationState = new AnimationState();
    public int tryChokeAnimationTick;
    public final int tryChokeAnimationLength = 35;
    public final int tryChokeAnimationActionPointStart = 18;
    public final int tryChokeAnimationActionPointStop = 9;
    public AnimationState chokeAnimationState = new AnimationState();
    public int chokeAnimationTick;
    public final int chokeAnimationActionPoint = 15;
    public AnimationState lowAttackAnimationState = new AnimationState();
    public int lowAttackAnimationTick;
    public final int lowAttackAnimationActionPoint = 17;
    public AnimationState highAttackAnimationState = new AnimationState();
    public int highAttackAnimationTick;
    public final int highAttackAnimationActionPoint = 14;
    public AnimationState jumpAnimationState = new AnimationState();
    public int jumpAnimationTick;
    public final int jumpAnimationLength = 20;
    public final int jumpAnimationActionPoint = 12;
    public AnimationState trySlamAnimationState = new AnimationState();
    public int trySlamAnimationTick;
    public final int trySlamAnimationActionPointStart = 11;
    public final int trySlamAnimationActionPointStop = 9;
    public AnimationState slamAnimationState = new AnimationState();
    public int slamAnimationTick;
    public AnimationState stopRestingAnimationState = new AnimationState();
    public int stopRestingAnimationTick;
    public int restTick;
    public final int restLength = 60;
    public int failedAttacks;
    public DamageSource deathDamageSource;
    public LivingEntity targetBeforeDeath;
    public DamageSource killedBy = this.damageSources().generic();
    public boolean jumping;
    public int jumpingFor;

    public MutantJungleZombieServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
        this.xpReward = MutantJungleZombieCommonConfig.exp_reward.get();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MmConfig.MutantJungleZombieServantLimit.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REMAINING_LIVES, MutantJungleZombieCommonConfig.get_up_times.get());
        this.entityData.define(LIVES_LOST, 0);
        this.entityData.define(GRAB_ARM_X_ROT, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RemainingLives", this.getRemainingLives());
        compound.putInt("LivesLost", this.getLivesLost());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setRemainingLives(compound.getInt("RemainingLives"));
        this.setLivesLost(compound.getInt("LivesLost"));
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

    @Override
    protected int getRestorableLives() {
        return Math.max(0, MutantJungleZombieCommonConfig.get_up_times.get() - this.getRemainingLives());
    }

    @Override
    protected void restoreOneLife() {
        this.setRemainingLives(this.getRemainingLives() + 1);
        this.playSound(this.getReviveSound(), 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getY() + 0.5D,
                        this.getRandomZ(1.0D), 0, d0, d1, d2, 1.0D);
            }
        }
    }

    @Override
    public void prepareRevived() {
        super.prepareRevived();
        this.downAnimationTick = 0;
        this.setRemainingLives(MutantJungleZombieCommonConfig.get_up_times.get());
        this.setLivesLost(0);
    }

    @Override
    public SoundEvent getReviveSound() {
        return SoundEventInit.MUTANT_JUNGLE_ZOMBIE_IDLE.get();
    }

    public float getGrabArmXRot() {
        return this.entityData.get(GRAB_ARM_X_ROT);
    }

    public void setGrabArmXRot(float value) {
        this.entityData.set(GRAB_ARM_X_ROT, value);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if (MutantJungleZombieCommonConfig.uses_strangle_attack.get()) {
            this.goalSelector.addGoal(2, new MutantJungleZombieChokeAttackGoal(this));
        }
        if (MutantJungleZombieCommonConfig.uses_slam_attack.get()) {
            this.goalSelector.addGoal(3, new MutantJungleZombieSlamAttackGoal(this));
        }
        if (MutantJungleZombieCommonConfig.uses_punch_attack.get()) {
            this.goalSelector.addGoal(4, new MutantJungleZombiePunchAttackGoal(this));
        }
        if (MutantJungleZombieCommonConfig.uses_jump.get()) {
            this.goalSelector.addGoal(5, new MutantJungleZombieJumpGoal(this));
        }
        if (MutantJungleZombieCommonConfig.uses_vine_attack.get()) {
            this.goalSelector.addGoal(6, new MutantJungleZombieVineAttackGoal(this));
        }
        this.goalSelector.addGoal(7, new ApproachTargetGoal(this, MutantJungleZombieCommonConfig.follow_target_wanted_distance.get(), MutantJungleZombieCommonConfig.following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(8, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this, new Class[0])).setUnseenMemoryTicks(6000));
        if (MutantJungleZombieCommonConfig.attacks_players.get() && !MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, (entity) -> {
            return entity.getType().is(EntityTypes.MUTANT_JUNGLE_ZOMBIE_TARGETS);
        }) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate(MutantJungleZombieCommonConfig.follow_non_player_distance.get(), MutantJungleZombieCommonConfig.follow_non_player_distance.get(), MutantJungleZombieCommonConfig.follow_non_player_distance.get());
            }
        });
    }

    public boolean shouldBeStationary() {
        return this.downAnimationTick > 0 || this.introAnimationTick > 0 || this.restTick > 0 || this.stopRestingAnimationTick > 0;
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    boolean canTarget(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_TARGET, this, target, this, (Predicate) null);
    }

    public boolean canHarm(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_HURT, this, target, this, (Predicate) null);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MmConfig.MutantJungleZombieServantHealth.get())
                .add(Attributes.ARMOR, MmConfig.MutantJungleZombieServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, MmConfig.MutantJungleZombieServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, MmConfig.MutantJungleZombieServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, MmConfig.MutantJungleZombieServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, MmConfig.MutantJungleZombieServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, MmConfig.MutantJungleZombieServantAttackDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEventInit.MUTANT_JUNGLE_ZOMBIE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEventInit.MUTANT_JUNGLE_ZOMBIE_HURT.get();
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SoundEventInit.MUTANT_JUNGLE_ZOMBIE_STEP.get(), 0.75F, 1.0F);
    }

    protected float getSoundVolume() {
        return 1.5F;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() == MobEffects.POISON ? false : super.canBeAffected(p_21197_);
    }

    public boolean notCurrentlyPlayingKeyframeAnimation() {
        return this.noveltyAnimationTick <= 0 && this.downAnimationTick <= 0 && this.introAnimationTick <= 0 && this.punchAnimationTick <= 0 && this.tryChokeAnimationTick <= 0 && this.chokeAnimationTick <= 0 && this.lowAttackAnimationTick <= 0 && this.highAttackAnimationTick <= 0 && this.jumpAnimationTick <= 0 && this.trySlamAnimationTick <= 0 && this.slamAnimationTick <= 0;
    }

    private boolean isRealDeath(DamageSource source) {
        return source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(ModDamageSource.DISMISSED) || source.is(DamageTypes.STARVE);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.resetLifeRecovery();
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
        this.failedAttacks = 0;
        this.downYaw = this.getYRot();
        this.downAnimationTick = this.downAnimationLength;
        this.level().broadcastEntityEvent(this, (byte) 7);
        if (!this.isRealDeath(p_21014_) && !this.level().isClientSide) {
            AreaDamage areaDamage = AreaDamage.spawnAreaDamage(this.level(), this.position(), this, MutantJungleZombieCommonConfig.death_poison_cloud_damage.get().floatValue(), MMDamageTypes.poisonCloud(this.damageSources()), 0.0F, 30.0F, 0.3F, 5.0F, 80, 10, true, false, 0.0D, 0.0D, false, false, 0, false, EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_HURT, 3);
            ((ServerLevel) this.level()).addFreshEntityWithPassengers(areaDamage);
        }
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
                this.returnReviveItem(MmItems.MUTANT_JUNGLE_ZOMBIE_REVIVE.get());
                this.remove(RemovalReason.KILLED);
            }
        } else if (this.deathTime >= this.downAnimationLength) {
            this.setRemainingLives(this.getRemainingLives() - 1);
            this.setLivesLost(this.getLivesLost() + 1);
            this.setHealth(this.isOnFire() ? this.getMaxHealth() / 2.0F : this.getMaxHealth());
            this.deathTime = 0;
            if (this.targetBeforeDeath != null) {
                this.setTarget(this.targetBeforeDeath);
            }
        }
    }

    public boolean isPushable() {
        return this.downAnimationTick > 0 ? true : super.isPushable();
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

    public void dropAllTickDeathLoot(DamageSource source) {
        Entity entity = source.getEntity();
        int i = ForgeHooks.getLootingLevel(this, entity, source);
        this.captureDrops(new ArrayList());
        boolean flag = this.lastHurtByPlayerTime > 0;
        this.dropFromLootTable(source, flag);
        this.dropCustomDeathLoot(source, i, flag);
        this.dropEquipment();
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

    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return p_20122_.getEntity() != null && ModList.get().isLoaded("dungeons_mobs") && p_20122_.getEntity().getType() == ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("dungeons_mobs", "jungle_zombie")) && p_20122_.getEntity().getTeam() == this.getTeam() ? true : super.isInvulnerableTo(p_20122_);
    }

    private void stunEffect() {
        for (int i = 0; i < (3 + this.getLivesLost()) * 3; ++i) {
            if (this.random.nextInt(10) != 0) continue;
            this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.75), this.getEyeY() - 1.0 + this.random.nextDouble(), this.getRandomZ(0.75), 0.0D, 0.0D, 0.0D);
        }
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

    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();
        if (this.getDeltaMovement().horizontalDistanceSqr() > 2.500000277905201E-7 && this.random.nextInt(5) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level().getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(pos), this.getX() + ((double) this.random.nextFloat() - 0.5) * (double) this.getBbWidth(), this.getY() + 0.1, this.getZ() + ((double) this.random.nextFloat() - 0.5) * (double) this.getBbWidth(), 4.0 * ((double) this.random.nextFloat() - 0.5), 0.5, ((double) this.random.nextFloat() - 0.5) * 4.0);
            }
        }
        if (this.jumping && this.getTarget() != null && this.getDeltaMovement().horizontalDistance() <= 0.1) {
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            double x = this.getTarget().getX() - this.getX();
            double y = this.getTarget().getY() - this.getY();
            double z = this.getTarget().getZ() - this.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            this.setDeltaMovement(this.getDeltaMovement().add(new Vec3(x / d * 0.2F, 0.0D, z / d * 0.2F).scale(2.0D)));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
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
        if (!this.level().isClientSide) {
            if (this.jumping) {
                ++this.jumpingFor;
            }
            if (this.jumpingFor >= 10 && this.onGround()) {
                this.jumping = false;
                this.jumpingFor = 0;
                AreaDamage areaDamage = AreaDamage.spawnAreaDamage(this.level(), this.position(), this, MutantJungleZombieCommonConfig.jump_poison_cloud_damage.get().floatValue(), MMDamageTypes.poisonCloud(this.damageSources()), 0.0F, 5.0F, 0.2F, 1.5F, 80, 0, true, false, 0.0D, 0.0D, false, false, 0, false, EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_HURT, 3);
                ((ServerLevel) this.level()).addFreshEntityWithPassengers(areaDamage);
            }
        }
        if (!this.level().isClientSide && this.failedAttacks > 0) {
            for (int i = 0; i < this.failedAttacks * 3; ++i) {
                if (this.random.nextInt(10) != 0) continue;
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SPLASH, this.getRandomX(0.75), this.getEyeY() - 1.0 + this.random.nextDouble(), this.getRandomZ(0.75), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (this.failedAttacks >= 3 + this.getLivesLost()) {
                this.failedAttacks = 0;
                this.noveltyAnimationTick = 0;
                this.restTick = this.restLength;
                this.level().broadcastEntityEvent(this, (byte) 13);
            }
        }
        if (this.restTick == 1) {
            this.stopRestingAnimationTick = 25;
            this.stopRestingAnimationState.start(this.tickCount);
        }
    }

    public void tickDownAnimTimers() {
        if (this.noveltyAnimationTick > 0) {
            --this.noveltyAnimationTick;
        }
        if (this.level().isClientSide && this.noveltyAnimationTick <= 0) {
            this.noveltyAnimationState.stop();
        }
        if (this.downAnimationTick > 0) {
            --this.downAnimationTick;
        }
        if (this.level().isClientSide && this.downAnimationTick <= 0) {
            this.downAnimationState.stop();
        }
        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }
        if (this.level().isClientSide && this.introAnimationTick <= 0) {
            this.introAnimationState.stop();
        }
        if (this.punchAnimationTick > 0) {
            --this.punchAnimationTick;
        }
        if (this.level().isClientSide && this.punchAnimationTick <= 0) {
            this.punchAnimationState.stop();
        }
        if (this.tryChokeAnimationTick > 0) {
            --this.tryChokeAnimationTick;
        }
        if (this.level().isClientSide && this.tryChokeAnimationTick <= 0) {
            this.tryChokeAnimationState.stop();
        }
        if (this.chokeAnimationTick > 0) {
            --this.chokeAnimationTick;
        }
        if (this.level().isClientSide && this.chokeAnimationTick <= 0) {
            this.chokeAnimationState.stop();
        }
        if (this.lowAttackAnimationTick > 0) {
            --this.lowAttackAnimationTick;
        }
        if (this.level().isClientSide && this.lowAttackAnimationTick <= 0) {
            this.lowAttackAnimationState.stop();
        }
        if (this.highAttackAnimationTick > 0) {
            --this.highAttackAnimationTick;
        }
        if (this.level().isClientSide && this.highAttackAnimationTick <= 0) {
            this.highAttackAnimationState.stop();
        }
        if (this.jumpAnimationTick > 0) {
            --this.jumpAnimationTick;
        }
        if (this.level().isClientSide && this.jumpAnimationTick <= 0) {
            this.jumpAnimationState.stop();
        }
        if (this.trySlamAnimationTick > 0) {
            --this.trySlamAnimationTick;
        }
        if (this.level().isClientSide && this.trySlamAnimationTick <= 0) {
            this.trySlamAnimationState.stop();
        }
        if (this.slamAnimationTick > 0) {
            --this.slamAnimationTick;
        }
        if (this.level().isClientSide && this.slamAnimationTick <= 0) {
            this.slamAnimationState.stop();
        }
        if (this.stopRestingAnimationTick > 0) {
            --this.stopRestingAnimationTick;
        }
        if (this.level().isClientSide && this.stopRestingAnimationTick <= 0) {
            this.stopRestingAnimationState.stop();
        }
        if (this.restTick > 0) {
            this.stunEffect();
            --this.restTick;
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
            this.punchAnimationTick = 25;
            this.punchAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 7) {
            this.downYaw = this.getYRot();
            this.downAnimationTick = this.downAnimationLength;
            this.downAnimationState.start(this.tickCount);
        } else if (p_21375_ == 8) {
            this.tryChokeAnimationTick = this.tryChokeAnimationLength;
            this.tryChokeAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 9) {
            this.chokeAnimationTick = 80;
            this.chokeAnimationState.start(this.tickCount);
            this.tryChokeAnimationTick = 0;
            this.tryChokeAnimationState.stop();
        } else if (p_21375_ == 10) {
            this.lowAttackAnimationTick = 30;
            this.lowAttackAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 11) {
            this.highAttackAnimationTick = 30;
            this.highAttackAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 12) {
            this.jumpAnimationTick = this.jumpAnimationLength;
            this.jumpAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 13) {
            this.restTick = this.restLength;
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 14) {
            this.introAnimationTick = this.introAnimationLength;
            this.introAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 15) {
            this.trySlamAnimationTick = 30;
            this.trySlamAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 16) {
            this.slamAnimationTick = 80;
            this.slamAnimationState.start(this.tickCount);
            this.trySlamAnimationTick = 0;
            this.trySlamAnimationState.stop();
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_) {
        this.setRemainingLives(MutantJungleZombieCommonConfig.get_up_times.get());
        MiscUtils.spawnWithPumpkinOnHalloween(this, p_21434_.getRandom());
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    @Override
    public void onMutated() {
        this.introAnimationTick = this.introAnimationLength;
        this.noveltyAnimationTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 14);
    }

    public NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return null;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return MutantJungleZombieCommonConfig.despawns;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantJungleZombieServant.this.shouldBeStationary();
        }
    }
}
