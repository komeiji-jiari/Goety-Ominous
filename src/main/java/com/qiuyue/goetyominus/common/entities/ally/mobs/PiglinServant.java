package com.qiuyue.goetyominus.common.entities.ally.mobs;

import java.util.UUID;
import javax.annotation.Nullable;

import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PiglinServant extends AbstractPiglinServant implements CrossbowAttackMob, InventoryCarrier {
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW;
    private static final EntityDataAccessor<Boolean> DATA_IS_DANCING;
    private static final UUID SPEED_MODIFIER_BABY_UUID;
    private static final AttributeModifier SPEED_MODIFIER_BABY;
    private static final float CROSSBOW_POWER = 1.6F;
    private boolean cannotHunt;
    private int admireTicks;
    private int piglinAge;
    private int danceTicks;
    private LivingEntity lastTarget;
    private final com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal meleeGoal = new com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal(this, 1.0D, false);
    private final com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal<PiglinServant> crossbowGoal = new com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal<>(this, 1.0D, 15.0F);

    public PiglinServant(EntityType<? extends AbstractPiglinServant> p_34683_, Level p_34684_) {
        super(p_34683_, p_34684_);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinServantDamage.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.5D) {
            public boolean canUse() {
                return PiglinServant.this.isBaby() && super.canUse();
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            public boolean canUse() {
                return !PiglinServant.this.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal<>(
                this, net.minecraft.world.entity.LivingEntity.class, 8.0F, 0.6D, 1.2D) {
            public boolean canUse() {
                return PiglinServant.this.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            if (this.isBaby()) return;
            if (this.meleeGoal != null && this.crossbowGoal != null) {
                this.goalSelector.removeGoal(this.meleeGoal);
                this.goalSelector.removeGoal(this.crossbowGoal);
            }
            if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
                this.goalSelector.addGoal(2, this.crossbowGoal);
            } else {
                this.goalSelector.addGoal(2, this.meleeGoal);
            }
        }
    }

    public void setItemSlot(EquipmentSlot p_149994_, ItemStack p_149995_) {
        super.setItemSlot(p_149994_, p_149995_);
        if (p_149994_ == EquipmentSlot.MAINHAND) {
            if (this.meleeDamageDealt > 0 && !(p_149995_.getItem() instanceof SwordItem)
                    && !(p_149995_.getItem() instanceof AxeItem)) {
                this.meleeDamageDealt = 0;
            }
            if (this.rangedDamageDealt > 0 && !(p_149995_.getItem() instanceof CrossbowItem)) {
                this.rangedDamageDealt = 0;
            }
            this.reassessWeaponGoal();
        }
    }

    public void addAdditionalSaveData(CompoundTag p_34751_) {
        super.addAdditionalSaveData(p_34751_);
        if (this.isBaby()) {
            p_34751_.putBoolean("IsBaby", true);
        }

        if (this.cannotHunt) {
            p_34751_.putBoolean("CannotHunt", true);
        }

        this.writeInventoryToTag(p_34751_);
        p_34751_.putInt("PiglinAge", this.piglinAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_34725_) {
        super.readAdditionalSaveData(p_34725_);
        this.setBaby(p_34725_.getBoolean("IsBaby"));
        this.setCannotHunt(p_34725_.getBoolean("CannotHunt"));
        this.readInventoryFromTag(p_34725_);
        if (p_34725_.contains("PiglinAge")) {
            this.piglinAge = p_34725_.getInt("PiglinAge");
        }
        this.reassessWeaponGoal();
    }

    protected void dropCustomDeathLoot(DamageSource p_34697_, int p_34698_, boolean p_34699_) {
        super.dropCustomDeathLoot(p_34697_, p_34698_, p_34699_);
        Entity entity = p_34697_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                ItemStack itemstack = new ItemStack(Items.PIGLIN_HEAD);
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(itemstack);
            }
        }

        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
    }

    protected ItemStack addToInventory(ItemStack p_34779_) {
        return this.getInventory().addItem(p_34779_);
    }

    protected boolean canAddToInventory(ItemStack p_34781_) {
        return this.getInventory().canAddItem(p_34781_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY_ID, false);
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
        this.entityData.define(DATA_IS_DANCING, false);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_34727_) {
        super.onSyncedDataUpdated(p_34727_);
        if (DATA_BABY_ID.equals(p_34727_)) {
            this.refreshDimensions();
        }

    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34717_, DifficultyInstance p_34718_, MobSpawnType p_34719_, @Nullable SpawnGroupData p_34720_, @Nullable CompoundTag p_34721_) {
        RandomSource randomsource = p_34717_.getRandom();
        if (p_34719_ != MobSpawnType.STRUCTURE) {
            if (randomsource.nextFloat() < 0.2F) {
                this.setBaby(true);
            } else if (this.isAdult()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
            }
        }

        this.populateDefaultEquipmentSlots(randomsource, p_34718_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34718_);
        return super.finalizeSpawn(p_34717_, p_34718_, p_34719_, p_34720_, p_34721_);
    }

    public boolean removeWhenFarAway(double p_34775_) {
        return !this.isPersistenceRequired();
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219189_, DifficultyInstance p_219190_) {
        if (this.isAdult()) {
            this.maybeWearArmor(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET), p_219189_);
            this.maybeWearArmor(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE), p_219189_);
            this.maybeWearArmor(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS), p_219189_);
            this.maybeWearArmor(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS), p_219189_);
        }

    }

    private void maybeWearArmor(EquipmentSlot p_219192_, ItemStack p_219193_, RandomSource p_219194_) {
        if (p_219194_.nextFloat() < 0.1F) {
            this.setItemSlot(p_219192_, p_219193_);
        }

    }

    public InteractionResult mobInteract(Player p_34745_, InteractionHand p_34746_) {
        ItemStack itemstack = p_34745_.getItemInHand(p_34746_);
        if (this.getTrueOwner() != null && p_34745_ == this.getTrueOwner()
                && itemstack.is(Items.GOLD_INGOT) && !this.isBaby()
                && this.admireTicks <= 0) {
            if (!p_34745_.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOLD_INGOT));
            this.admireTicks = 120;
            this.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(p_34745_, p_34746_);
    }

    protected float getStandingEyeHeight(Pose p_34740_, EntityDimensions p_34741_) {
        float f = super.getStandingEyeHeight(p_34740_, p_34741_);
        return this.isBaby() ? f - 0.82F : f;
    }

    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() * 0.92;
    }

    public void setBaby(boolean p_34729_) {
        this.getEntityData().set(DATA_BABY_ID, p_34729_);
        if (!this.level().isClientSide) {
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            attributeinstance.removeModifier(SPEED_MODIFIER_BABY);
            if (p_34729_) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_BABY);
            }
        }

    }

    public boolean isBaby() {
        return (Boolean)this.getEntityData().get(DATA_BABY_ID);
    }

    private boolean isNearJukebox() {
        BlockPos pos = this.blockPosition();
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            if (this.level().getBlockState(blockpos).is(Blocks.JUKEBOX)
                    && this.level().getBlockEntity(blockpos) instanceof net.minecraft.world.level.block.entity.JukeboxBlockEntity jukebox
                    && jukebox.isRecordPlaying()) {
                return true;
            }
        }
        return false;
    }

    private void setCannotHunt(boolean p_34792_) {
        this.cannotHunt = p_34792_;
    }

    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    protected void customServerAiStep() {
        if (this.isConverting() && this.timeInOverworld == 0) {
            this.playSound(SoundEvents.PIGLIN_RETREAT, 1.0F, 1.0F);
        }
        if (this.admireTicks > 0) {
            this.admireTicks--;
            if (this.admireTicks == 0) {
                if (this.getOffhandItem().is(Items.GOLD_INGOT)) {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.swing(InteractionHand.OFF_HAND);
                if (this.level() instanceof ServerLevel serverLevel && this.getTrueOwner() != null) {
                    net.minecraft.world.level.storage.loot.LootTable loottable = serverLevel.getServer().getLootData().getLootTable(net.minecraft.world.level.storage.loot.BuiltInLootTables.PIGLIN_BARTERING);
                    net.minecraft.world.level.storage.loot.LootParams lootparams = new net.minecraft.world.level.storage.loot.LootParams.Builder(serverLevel)
                            .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY);
                    for (net.minecraft.world.item.ItemStack drop : loottable.getRandomItems(lootparams)) {
                        net.minecraft.world.entity.ai.behavior.BehaviorUtils.throwItem(this, drop, this.getTrueOwner().position());
                    }
                }
            }
        }
        if (this.isNearJukebox()) {
            this.setDancing(true);
            this.danceTicks = 0;
        } else if (this.isDancing()) {
            this.setDancing(false);
            this.danceTicks = 0;
        }
        LivingEntity target = this.getTarget();
        if (target == null && this.lastTarget != null && !this.lastTarget.isAlive()
                && !this.isDancing() && this.random.nextFloat() < 0.10F) {
            boolean hasEnemies = !this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(10.0),
                    e -> e != this && e.isAlive() && e != this.getTrueOwner()
                            && !(e instanceof AbstractPiglinServant)
                            && !(e instanceof net.minecraft.world.entity.animal.horse.AbstractHorse)).isEmpty();
            if (!hasEnemies) {
                this.setDancing(true);
                this.danceTicks = 0;
            }
        }
        this.lastTarget = target;
        if (this.isDancing() && target != null) {
            this.setDancing(false);
            this.danceTicks = 0;
        }
        if (this.isDancing()) {
            ++this.danceTicks;
            if (this.danceTicks > 100 && !this.isNearJukebox()) {
                this.setDancing(false);
                this.danceTicks = 0;
            }
        } else {
            this.danceTicks = 0;
        }
        super.customServerAiStep();
        if (this.isBaby()) {
            this.piglinAge++;
            if (this.piglinAge >= MobsConfig.PiglinServantBabyGrowthTime.get()) {
                this.setBaby(false);
                this.piglinAge = 0;
                if (this.getMainHandItem().isEmpty()) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
                } else {
                    this.reassessWeaponGoal();
                }
            }
        }
    }

    public int getExperienceReward() {
        return this.xpReward;
    }

    protected void finishConversion(ServerLevel p_34756_) {
        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
        super.finishConversion(p_34756_);
    }

    private ItemStack createSpawnWeapon() {
        return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    public boolean isChargingCrossbow() {
        return (Boolean)this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean p_34753_) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, p_34753_);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public PiglinArmPose getArmPose() {
        if (this.isDancing()) {
            return PiglinArmPose.DANCING;
        } else if (this.getOffhandItem().isPiglinCurrency()) {
            return PiglinArmPose.ADMIRING_ITEM;
        } else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
            return PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON;
        } else if (this.isChargingCrossbow()) {
            return PiglinArmPose.CROSSBOW_CHARGE;
        } else {
            return this.isAggressive() && this.isHolding((is) -> {
                return is.getItem() instanceof CrossbowItem;
            }) ? PiglinArmPose.CROSSBOW_HOLD : PiglinArmPose.DEFAULT;
        }
    }

    public int getAge() {
        if (this.isBaby()) {
            return this.piglinAge - MobsConfig.PiglinServantBabyGrowthTime.get();
        }
        return MobsConfig.PiglinServantBabyGrowthTime.get();
    }

    public void onBreed() {
        if (this.random.nextFloat() < 0.5F) {
            this.setDancing(true);
        }
    }

    public boolean isDancing() {
        return (Boolean)this.entityData.get(DATA_IS_DANCING);
    }

    public void setDancing(boolean p_34790_) {
        this.entityData.set(DATA_IS_DANCING, p_34790_);
    }

    public boolean hurt(DamageSource p_34694_, float p_34695_) {
        if (this.admireTicks > 0) {
            this.admireTicks = 0;
            if (this.getOffhandItem().is(Items.GOLD_INGOT)) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }
        return super.hurt(p_34694_, p_34695_);
    }

    public void performRangedAttack(LivingEntity p_34704_, float p_34705_) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity p_34707_, ItemStack p_34708_, Projectile p_34709_, float p_34710_) {
        this.shootCrossbowProjectile(this, p_34707_, p_34709_, p_34710_, 1.6F);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof CrossbowItem;
    }

    protected void holdInMainHand(ItemStack p_34784_) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, p_34784_);
    }

    protected void holdInOffHand(ItemStack p_34786_) {
        if (p_34786_.isPiglinCurrency()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, p_34786_);
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        } else {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, p_34786_);
        }

    }

    @Override
    protected void onMeleeDamageDealt() {
        if (this.meleeDamageDealt >= MobsConfig.PiglinServantEvolutionDamage.get() && !this.level().isClientSide && this.isHoldingMeleeWeapon()) {
            this.convertToBrute();
        }
    }

    private void convertToBrute() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        PiglinBruteServant brute = new PiglinBruteServant(
                ModEntityTypes.PIGLIN_BRUTE_SERVANT.get(), serverLevel);
        brute.copyTrueOwner(this);
        brute.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        brute.setBaby(this.isBaby());
        brute.setHealth(brute.getMaxHealth());

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                brute.setItemSlot(slot, stack.copy());
            }
        }

        for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
            ItemStack stack = this.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                brute.getInventory().setItem(i, stack.copy());
            }
        }
        brute.setGuaranteedDrop(EquipmentSlot.MAINHAND);

        boolean immune = this.isImmuneToZombification();

        this.discard();

        if (immune) {
            brute.setImmuneToZombification(true);
        }

        serverLevel.addFreshEntity(brute);
        serverLevel.sendParticles(ParticleTypes.FLASH,
                this.getX(), this.getY() + 1.0, this.getZ(),
                1, 0, 0, 0, 0);
    }

    public boolean startRiding(Entity p_34701_, boolean p_34702_) {
        return super.startRiding(p_34701_, p_34702_);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_34767_) {
        return SoundEvents.PIGLIN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_DEATH;
    }

    protected void playStepSound(BlockPos p_34748_, BlockState p_34749_) {
        this.playSound(SoundEvents.PIGLIN_STEP, 0.15F, 1.0F);
    }

    protected void playSoundEvent(SoundEvent p_219196_) {
        this.playSound(p_219196_, this.getSoundVolume(), this.getVoicePitch());
    }

    protected void playConvertedSound() {
        this.playSoundEvent(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }

    static {
        DATA_BABY_ID = SynchedEntityData.defineId(PiglinServant.class, EntityDataSerializers.BOOLEAN);
        DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(PiglinServant.class, EntityDataSerializers.BOOLEAN);
        DATA_IS_DANCING = SynchedEntityData.defineId(PiglinServant.class, EntityDataSerializers.BOOLEAN);
        SPEED_MODIFIER_BABY_UUID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
        SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.20000000298023224, Operation.MULTIPLY_BASE);
    }
}
