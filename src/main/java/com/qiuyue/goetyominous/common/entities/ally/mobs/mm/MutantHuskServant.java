package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant.MutantHuskGeyserAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant.MutantHuskMeleeAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant.MutantHuskPunchAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant.MutantHuskRoarGoal;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant.MutantHuskThrowSandAttackGoal;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;

public class MutantHuskServant extends AbstractMutantServant {
    private static final EntityDataAccessor<Integer> REMAINING_LIVES = SynchedEntityData.defineId(MutantHuskServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> SAND_TYPE = SynchedEntityData.defineId(MutantHuskServant.class, EntityDataSerializers.STRING);
    public float jumpingAnimationAmount;
    public float walkAnimationAmount;
    public float idleAnimationAmount = 1.0F;
    public float keyframeAnimationAmount;
    public float[][] heldPose;
    public AnimationState noveltyAnimationState = new AnimationState();
    public int noveltyAnimationTick;
    public final int noveltyAnimationLength = 75;
    public AnimationState throwSandAnimationState = new AnimationState();
    public int throwSandAnimationTick;
    public final int throwSandAnimationLength = 47;
    public final int throwSandAnimationActionPoint = 21;
    public AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTick;
    public final int attackAnimationLength = 26;
    public final int attackAnimationActionPoint = 11;
    public AnimationState trapAnimationState = new AnimationState();
    public int trapAnimationTick;
    public final int trapAnimationLength = 55;
    public final int trapAnimationActionPoint = 33;
    public AnimationState roarAnimationState = new AnimationState();
    public int roarAnimationTick;
    public final int roarAnimationLength = 120;
    public final int roarAnimationActionPoint = 103;
    public AnimationState introAnimationState = new AnimationState();
    public int introAnimationTick;
    public final int introAnimationLength = 50;
    public AnimationState geyserAnimationState = new AnimationState();
    public int geyserAnimationTick;
    public final int geyserAnimationLength = 70;
    public final int geyserAnimationActionPoint = 37;
    public AnimationState jumpAnimationState = new AnimationState();
    public int jumpAnimationTick;
    public final int jumpAnimationLength = 7;
    public final int jumpAnimationActionPoint = 2;
    public AnimationState slamAnimationState = new AnimationState();
    public int slamAnimationTick;
    public final int slamAnimationLength = 12;
    public final int slamAnimationActionPoint = 10;
    public AnimationState downAnimationState = new AnimationState();
    public int downAnimationTick;
    public float downYaw;
    public final int downAnimationLength = 100;
    public boolean jumping;
    public int jumpingFor;
    public DamageSource deathDamageSource;
    public List<Entity> resurrectedMobs = Lists.newArrayList();
    public LivingEntity targetBeforeDeath;
    public boolean geyserDeathCheck = false;
    private static final Predicate<Entity> ALIVE = target -> target.isAlive();
    public DamageSource killedBy = this.damageSources().cramming();

    public MutantHuskServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
        this.xpReward = MutantHuskCommonConfig.exp_reward.get();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.MutantHuskServantLimit.get();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if (MutantHuskCommonConfig.uses_punch_attack.get()) {
            this.goalSelector.addGoal(2, new MutantHuskPunchAttackGoal(this));
        }
        if (MutantHuskCommonConfig.uses_roar.get()) {
            this.goalSelector.addGoal(3, new MutantHuskRoarGoal(this));
        }
        if (MutantHuskCommonConfig.uses_geyser_attack.get()) {
            this.goalSelector.addGoal(4, new MutantHuskGeyserAttackGoal(this));
        }
        if (MutantHuskCommonConfig.uses_basic_attack.get()) {
            this.goalSelector.addGoal(5, new MutantHuskMeleeAttackGoal(this));
        }
        if (MutantHuskCommonConfig.uses_sand_throw.get()) {
            this.goalSelector.addGoal(6, new MutantHuskThrowSandAttackGoal(this));
        }
        this.goalSelector.addGoal(7, new ApproachTargetGoal(this, MutantHuskCommonConfig.follow_target_wanted_distance.get(), MutantHuskCommonConfig.following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(8, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this, new Class[0])).setUnseenMemoryTicks(6000));
        if (MutantHuskCommonConfig.attacks_players.get() && !MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, (entity) -> {
            return entity.getType().is(EntityTypes.MUTANT_HUSK_TARGETS);
        }) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate(MutantHuskCommonConfig.follow_non_player_distance.get(), MutantHuskCommonConfig.follow_non_player_distance.get(), MutantHuskCommonConfig.follow_non_player_distance.get());
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
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HUSK_CANT_TARGET, this, target, this, (Predicate) null);
    }

    public boolean canHarm(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HUSK_CANT_HURT, this, target, this, (Predicate) null);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MutantHuskServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MutantHuskServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MutantHuskServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MutantHuskServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MutantHuskServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MutantHuskServantMovementSpeed.get());
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
        this.downYaw = this.getYRot();
        this.downAnimationTick = this.downAnimationLength;
        this.geyserDeathCheck = true;
        this.level().broadcastEntityEvent(this, (byte) 7);
        this.playSound(SoundEventInit.MUTANT_HUSK_DOWN.get(), 2.0F, MiscUtils.randomSoundPitch());
        this.killedBy = p_21014_;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLastDamageSource() != null && (this.getLastDamageSource().is(DamageTypes.GENERIC_KILL) || this.getLastDamageSource().is(DamageTypes.FELL_OUT_OF_WORLD))) {
            if (this.deathTime == 20 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
        } else {
            if (this.downAnimationTick == this.downAnimationLength - 7) {
                this.playSound(SoundEventInit.MUTANT_HUSK_THUD.get(), 2.0F, MiscUtils.randomSoundPitch());
            }
            if (this.deathDamageSource != null && (this.getRemainingLives() <= 0 || this.isRealDeath(this.deathDamageSource))) {
                if (this.deathTime >= 35 && !this.level().isClientSide()) {
                    this.level().broadcastEntityEvent(this, (byte) 60);
                    if (this.killedBy != null) {
                        this.dropAllTickDeathLoot(this.killedBy);
                    }
                    this.returnReviveItem(MmItems.MUTANT_HUSK_REVIVE.get());
                    this.playSound(SoundEventInit.MUTANT_HUSK_DEATH.get(), 2.5F, MiscUtils.randomSoundPitch());
                    this.remove(RemovalReason.KILLED);
                }
            } else {
                if (this.deathTime >= this.downAnimationLength) {
                    this.setRemainingLives(this.getRemainingLives() - 1);
                    this.setHealth(this.getMaxHealth());
                    this.deathTime = 0;
                    if (this.targetBeforeDeath != null) {
                        this.setTarget(this.targetBeforeDeath);
                    }
                }
                if (this.downAnimationTick == this.downAnimationLength - 40) {
                    this.playSound(SoundEventInit.MUTANT_HUSK_REVIVE.get(), 1.5F, MiscUtils.randomSoundPitch());
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

    protected SoundEvent getAmbientSound() {
        return SoundEventInit.MUTANT_HUSK_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEventInit.MUTANT_HUSK_HURT.get();
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SoundEventInit.MUTANT_HUSK_STEP.get(), 0.75F, 1.0F);
    }

    protected float getSoundVolume() {
        return 1.5F;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean notCurrentlyPlayingKeyframeAnimation() {
        return this.noveltyAnimationTick <= 0 && this.throwSandAnimationTick <= 0 && this.downAnimationTick <= 0 && this.attackAnimationTick <= 0 && this.trapAnimationTick <= 0 && this.roarAnimationTick <= 0 && this.geyserAnimationTick <= 0 && this.jumpAnimationTick <= 0 && this.slamAnimationTick <= 0 && this.introAnimationTick <= 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REMAINING_LIVES, MutantHuskCommonConfig.get_up_times.get());
        this.entityData.define(SAND_TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
        this.setRemainingLives(p_21450_.getInt("RemainingLives"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_21484_) {
        super.addAdditionalSaveData(p_21484_);
        p_21484_.putInt("RemainingLives", this.getRemainingLives());
    }

    public int getRemainingLives() {
        return this.entityData.get(REMAINING_LIVES);
    }

    public void setRemainingLives(int value) {
        this.entityData.set(REMAINING_LIVES, value);
    }

    @Override
    protected int getRestorableLives() {
        return Math.max(0, MutantHuskCommonConfig.get_up_times.get() - this.getRemainingLives());
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
        this.setRemainingLives(MutantHuskCommonConfig.get_up_times.get());
    }

    @Override
    public SoundEvent getReviveSound() {
        return SoundEventInit.MUTANT_HUSK_REVIVE.get();
    }

    public String getSandType() {
        return this.entityData.get(SAND_TYPE);
    }

    public void setSandType(String value) {
        this.entityData.set(SAND_TYPE, value);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            String sandType = MiscUtils.sandStringForPos(this.level(), this.getOnPos());
            if (this.roarAnimationTick == this.roarAnimationActionPoint) {
                this.roar();
                for (int i = 0; i < 500; ++i) {
                    Vec3 motion = PositionUtils.getOffsetMotion(this, 0.0, 0.0, 1.25 + this.random.nextGaussian() * 0.25, 0.0F, this.random.nextFloat() * 360.0F);
                    this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float) MiscUtils.sandColourForSandType(sandType))), this.getX(), this.getRandomY(), this.getZ(), motion.x, motion.y, motion.z);
                }
            }
            if (!this.level().isClientSide && this.roarAnimationTick <= this.roarAnimationActionPoint && this.roarAnimationTick > this.roarAnimationLength - 72) {
                AABB aabb = this.getBoundingBox().inflate(MutantHuskCommonConfig.roar_quicksand_range.get(), MutantHuskCommonConfig.roar_quicksand_range.get() / 2.0, MutantHuskCommonConfig.roar_quicksand_range.get());
                if (MutantHuskCommonConfig.roar_attack_griefing.get() && !MutantMoreGroupedOptionsCommonConfig.quicksand_griefing_off.get()) {
                    for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                        BlockState blockstate = this.level().getBlockState(blockpos);
                        if (!blockstate.isAir() || !QuicksandBlock.survivable(MiscUtils.quicksandBlockForSandType(sandType).defaultBlockState(), this.level(), blockpos) || this.random.nextInt(200) != 0) continue;
                        this.level().setBlockAndUpdate(blockpos, MiscUtils.quicksandBlockForSandType(sandType).defaultBlockState());
                    }
                }
            }
        }
    }

    private void roar() {
        if (this.isAlive()) {
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(MutantHuskCommonConfig.roar_damage_range.get(), MutantHuskCommonConfig.roar_damage_range.get() / 2.0, MutantHuskCommonConfig.roar_damage_range.get()), ALIVE)) {
                if (entity == this) continue;
                if (this.canHarm(entity)) {
                    if (MutantHuskCommonConfig.ignores_invulnerability_time.get()) {
                        entity.invulnerableTime = 0;
                    }
                    entity.hurt(this.damageSources().mobAttack(this), MutantHuskCommonConfig.roar_damage.get().floatValue());
                }
                this.strongKnockback(entity);
            }
        }
    }

    private void strongKnockback(Entity p_213688_1_) {
        double d0 = p_213688_1_.getX() - this.getX();
        double d1 = p_213688_1_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        p_213688_1_.push(d0 / d2 * 5.0, 0.2, d1 / d2 * 5.0);
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
        if (this.throwSandAnimationTick > 0) {
            --this.throwSandAnimationTick;
        }
        if (this.level().isClientSide && this.throwSandAnimationTick <= 0) {
            this.throwSandAnimationState.stop();
        }
        if (this.downAnimationTick > 0) {
            --this.downAnimationTick;
        }
        if (this.level().isClientSide && this.downAnimationTick <= 0) {
            this.downAnimationState.stop();
        }
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }
        if (this.level().isClientSide && this.attackAnimationTick <= 0) {
            this.attackAnimationState.stop();
        }
        if (this.trapAnimationTick > 0) {
            --this.trapAnimationTick;
        }
        if (this.level().isClientSide && this.trapAnimationTick <= 0) {
            this.trapAnimationState.stop();
        }
        if (this.roarAnimationTick > 0) {
            --this.roarAnimationTick;
        }
        if (this.level().isClientSide && this.roarAnimationTick <= 0) {
            this.roarAnimationState.stop();
        }
        if (this.geyserAnimationTick > 0) {
            --this.geyserAnimationTick;
        }
        if (this.level().isClientSide && this.geyserAnimationTick <= 0) {
            this.geyserAnimationState.stop();
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
        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }
        if (this.level().isClientSide && this.introAnimationTick <= 0) {
            this.introAnimationState.stop();
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
            this.throwSandAnimationTick = this.throwSandAnimationLength;
            this.throwSandAnimationState.start(this.tickCount);
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
            this.trapAnimationTick = this.trapAnimationLength;
            this.trapAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 10) {
            this.roarAnimationTick = this.roarAnimationLength;
            this.roarAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 11) {
            this.geyserAnimationTick = this.geyserAnimationLength;
            this.geyserAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 12) {
            this.jumpAnimationTick = this.jumpAnimationLength;
            this.jumpAnimationState.start(this.tickCount);
            this.jumping = true;
            this.noveltyAnimationTick = 0;
            this.geyserAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 13) {
            this.slamAnimationTick = this.slamAnimationLength;
            this.slamAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.jumping = false;
            this.noveltyAnimationState.stop();
        } else if (p_21375_ == 14) {
            this.introAnimationTick = this.introAnimationLength;
            this.introAnimationState.start(this.tickCount);
            this.noveltyAnimationTick = 0;
            this.noveltyAnimationState.stop();
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_) {
        this.setRemainingLives(MutantHuskCommonConfig.get_up_times.get());
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
        return MutantHuskCommonConfig.despawns;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantHuskServant.this.shouldBeStationary();
        }
    }
}
