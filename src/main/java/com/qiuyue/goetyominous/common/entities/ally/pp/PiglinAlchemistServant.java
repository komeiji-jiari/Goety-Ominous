package com.qiuyue.goetyominous.common.entities.ally.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.ai.pp.AlchemistBowAttackGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.AlchemistHealArrowGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.AlchemistPotionSupportGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.LookAtPlayerWhenIdleGoal;
import com.qiuyue.goetyominous.common.entities.projectile.pp.AlchemistArrow;
import com.qiuyue.goetyominous.common.init.pp.PpEntityRegistry;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import com.qiuyue.goetyominous.config.pp.PpConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import tallestred.piglinproliferation.client.PPSounds;
import tallestred.piglinproliferation.common.loot.PPLoot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PiglinAlchemistServant extends AbstractPiglinServant implements RangedAttackMob {

    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<ItemStack>[] BELT_SLOTS = defineBeltInventory(6);
    private static final EntityDataAccessor<Boolean> DATA_IS_ABOUT_TO_THROW_POTION =
            SynchedEntityData.defineId(PiglinAlchemistServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_SHOWN_ON_OFFHAND =
            SynchedEntityData.defineId(PiglinAlchemistServant.class, EntityDataSerializers.ITEM_STACK);

    public final BeltInventory beltInventory = new BeltInventory();
    private int arrowsShot;
    private final boolean[] playerGivenSlots = new boolean[6];
    private boolean shownOffhandPlayerGiven;

    public PiglinAlchemistServant(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, PpConfig.PiglinAlchemistServantHealth.get())
                .add(Attributes.ARMOR, PpConfig.PiglinAlchemistServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, PpConfig.PiglinAlchemistServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, PpConfig.PiglinAlchemistServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, PpConfig.PiglinAlchemistServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ABOUT_TO_THROW_POTION, false);
        this.entityData.define(DATA_ITEM_SHOWN_ON_OFFHAND, ItemStack.EMPTY);
        for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
            this.entityData.define(accessor, ItemStack.EMPTY);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new AlchemistBowAttackGoal(this));
        this.goalSelector.addGoal(3, new AlchemistHealArrowGoal(this));
        this.goalSelector.addGoal(3, new AlchemistPotionSupportGoal(this));
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerWhenIdleGoal(this, net.minecraft.world.entity.player.Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerWhenIdleGoal(this, Mob.class, 15.0F));
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public PiglinArmPose getArmPose() {
        if (this.getOffhandItem().is(Items.GOLD_INGOT)) {
            return PiglinArmPose.ADMIRING_ITEM;
        }
        return PiglinArmPose.DEFAULT;
    }

    @Override
    protected boolean isAcceptedWeapon(Item item) {
        return item instanceof BowItem;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        if (this.isAdult()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            if (random.nextFloat() < PpConfig.PiglinAlchemistServantHealingArrowChance.get()) {
                ItemStack tippedArrow = PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, random.nextInt(1, 7)), Potions.STRONG_HEALING);
                this.setBeltInventorySlot(random.nextInt(this.beltInventory.size()), tippedArrow);
            }
            for (int slot = 0; slot < this.beltInventory.size(); slot++) {
                if (this.beltInventory.get(slot).isEmpty()) {
                    Potion effect = random.nextFloat() < 0.35F ? Potions.FIRE_RESISTANCE
                            : random.nextFloat() < 0.30F ? Potions.STRONG_REGENERATION
                            : random.nextFloat() < 0.25F ? Potions.STRONG_HEALING
                            : Potions.STRONG_STRENGTH;
                    this.setBeltInventorySlot(slot, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), effect));
                }
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return PPSounds.ALCHEMIST_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return PPSounds.ALCHEMIST_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PPSounds.ALCHEMIST_DEATH.get();
    }

    @Override
    protected void playConvertedSound() {
        this.playSound(PPSounds.ALCHEMIST_CONVERTED.get(), this.getSoundVolume(), this.getVoicePitch() / 0.10F);
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, BlockState state) {
        if (this.getRandom().nextInt(20) == 0 && this.beltInventory.anyMatch(stack -> stack.getItem() instanceof PotionItem)) {
            this.playSound(PPSounds.ALCHEMIST_WALK.get(), 0.5F * (this.beltInventory.countMatches(stack -> stack.getItem() instanceof PotionItem) * 0.5F), 1.0F);
        }
        this.playSound(PPSounds.ALCHEMIST_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    protected List<ItemStack> rollBarterLoot(ServerLevel serverLevel) {
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(PPLoot.ALCHEMIST_BARTER);
        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .create(LootContextParamSets.PIGLIN_BARTER);
        return lootTable.getRandomItems(lootParams);
    }

    public void performRangedAttackAt(LivingEntity target, float distanceFactor, ItemStack forcedArrow) {
        ItemStack itemstack = forcedArrow.isEmpty() ? this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem))) : forcedArrow;
        AbstractArrow arrow;
        if (itemstack.is(Items.TIPPED_ARROW)) {
            AlchemistArrow tippedArrow = new AlchemistArrow(this.level(), this);
            tippedArrow.setEffectsFromItem(itemstack);
            tippedArrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
            arrow = tippedArrow;
        } else {
            arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
        }
        if (this.getMainHandItem().getItem() instanceof BowItem bowItem) {
            arrow = bowItem.customArrow(arrow);
        }
        int powerLevel = itemstack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
        }
        int punchLevel = itemstack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
        if (punchLevel > 0) {
            arrow.setKnockback(punchLevel);
        }
        if (itemstack.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
            arrow.setSecondsOnFire(100);
        }
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        arrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
        this.arrowsShot++;
        if (!forcedArrow.isEmpty()) {
            forcedArrow.shrink(1);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack arrow = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        for (int slot = 0; slot < this.beltInventory.size() && !(this.getItemShownOnOffhand().getItem() instanceof TippedArrowItem); slot++) {
            if (this.beltInventory.get(slot).getItem() instanceof TippedArrowItem) {
                ItemStack tippedArrow = this.beltInventory.get(slot);
                List<net.minecraft.world.effect.MobEffectInstance> effects = PotionUtils.getPotion(tippedArrow).getEffects();
                if (this.getTarget() != null && target == this.getTarget() && effects.stream().anyMatch(effect -> !effect.getEffect().isBeneficial())
                        || target != this.getTarget()
                        || this.getTarget() != null && this.getTarget().isInvertedHealAndHarm() && effects.stream().anyMatch(effect -> effect.getEffect() == net.minecraft.world.effect.MobEffects.HEAL)) {
                    this.setShownOffhandPlayerGiven(this.isPlayerGivenSlot(slot));
                    this.setItemShownOnOffhand(tippedArrow.copy());
                    this.setPlayerGivenSlot(slot, false);
                    this.beltInventory.set(slot, ItemStack.EMPTY);
                }
            }
        }
        if (this.getItemShownOnOffhand().getItem() instanceof TippedArrowItem) {
            arrow = this.getItemShownOnOffhand();
        }
        this.performRangedAttackAt(target, distanceFactor, arrow);
        if (arrow.isEmpty() && this.getItemShownOnOffhand().is(arrow.getItem())) {
            this.setItemShownOnOffhand(ItemStack.EMPTY);
            this.setShownOffhandPlayerGiven(false);
        }
    }

    public int getArrowsShot() {
        return this.arrowsShot;
    }

    public void setArrowsShot(int arrowsShot) {
        this.arrowsShot = arrowsShot;
    }

    public boolean isGonnaThrowPotion() {
        return this.entityData.get(DATA_IS_ABOUT_TO_THROW_POTION);
    }

    public void willThrowPotion(boolean throwPotion) {
        this.entityData.set(DATA_IS_ABOUT_TO_THROW_POTION, throwPotion);
    }

    public ItemStack getItemShownOnOffhand() {
        return this.entityData.get(DATA_ITEM_SHOWN_ON_OFFHAND);
    }

    public void setItemShownOnOffhand(ItemStack stack) {
        this.entityData.set(DATA_ITEM_SHOWN_ON_OFFHAND, stack);
    }

    public void setBeltInventorySlot(int slot, ItemStack stack) {
        this.beltInventory.set(slot, stack);
    }

    public void throwPotion(ItemStack thrownPotion, float xRot, float yRot) {
        ThrownPotion potion = new ThrownPotion(this.level(), this);
        potion.setItem(thrownPotion);
        potion.shootFromRotation(this, xRot, yRot, -20.0F, 0.5F, 1.0F);
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SPLASH_POTION_THROW, this.getSoundSource(), 1.0F, 0.8F + this.getRandom().nextFloat() * 0.4F);
        }
        this.level().addFreshEntity(potion);
        this.willThrowPotion(false);
        thrownPotion.shrink(1);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        for (int i = 0; i < this.beltInventory.size(); i++) {
            ItemStack stack = this.beltInventory.get(i);
            if (!stack.isEmpty() && this.isPlayerGivenSlot(i)) {
                this.spawnAtLocation(stack.copy());
            }
        }
        if (this.isShownOffhandPlayerGiven() && !this.getItemShownOnOffhand().isEmpty()) {
            this.spawnAtLocation(this.getItemShownOnOffhand().copy());
        }
        this.setItemShownOnOffhand(ItemStack.EMPTY);
        this.clearPlayerGivenSlots();
        this.beltInventory.clear();
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }

    @Nullable
    @Override
    protected EntityType<? extends Mob> getZombieVariantType() {
        if (this instanceof ZPiglinAlchemistServant) {
            return null;
        }
        return PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT.get();
    }

    @Override
    protected void afterZombieConversion(Mob zombie) {
        if (zombie instanceof ZPiglinAlchemistServant zombified) {
            zombified.setItemShownOnOffhand(this.getItemShownOnOffhand().copy());
            for (int slot = 0; slot < this.beltInventory.size(); slot++) {
                zombified.setBeltInventorySlot(slot, this.beltInventory.get(slot).copy());
            }
            zombified.copyPlayerGivenStateFrom(this);
        }
    }

    @Override
    protected EntityType<? extends Mob> getConversionTarget() {
        return PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT.get();
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        ZPiglinAlchemistServant zombified = new ZPiglinAlchemistServant(
                PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.setBaby(this.isBaby());
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        zombified.setItemSlot(EquipmentSlot.MAINHAND, this.getMainHandItem().copy());
        zombified.setItemShownOnOffhand(this.getItemShownOnOffhand().copy());
        for (int slot = 0; slot < this.beltInventory.size(); slot++) {
            zombified.setBeltInventorySlot(slot, this.beltInventory.get(slot).copy());
        }
        zombified.copyPlayerGivenStateFrom(this);
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag belt = new CompoundTag();
        NonNullList<ItemStack> items = this.beltInventory.values();
        for (int i = 0; i < items.size(); ++i) {
            if (!items.get(i).isEmpty()) {
                belt.put("Slot" + i, items.get(i).save(new CompoundTag()));
            }
        }
        compound.put("BeltItems", belt);
        compound.putInt("ArrowsShot", this.arrowsShot);
        int givenMask = 0;
        for (int i = 0; i < this.playerGivenSlots.length; i++) {
            if (this.playerGivenSlots[i]) {
                givenMask |= 1 << i;
            }
        }
        compound.putInt("PlayerGivenBelt", givenMask);
        compound.putBoolean("PlayerGivenOffhand", this.shownOffhandPlayerGiven);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        CompoundTag belt = compound.getCompound("BeltItems");
        for (int i = 0; i < this.beltInventory.size(); ++i) {
            this.beltInventory.set(i, belt.contains("Slot" + i) ? ItemStack.of(belt.getCompound("Slot" + i)) : ItemStack.EMPTY);
        }
        this.arrowsShot = compound.getInt("ArrowsShot");
        int givenMask = compound.getInt("PlayerGivenBelt");
        for (int i = 0; i < this.playerGivenSlots.length; i++) {
            this.playerGivenSlots[i] = (givenMask & (1 << i)) != 0;
        }
        this.shownOffhandPlayerGiven = compound.getBoolean("PlayerGivenOffhand");
    }



    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(PpItems.PIGLIN_ALCHEMIST_SERVANT_SPAWN_EGG.get());
    }

    public boolean isPlayerGivenSlot(int slot) {
        return slot >= 0 && slot < this.playerGivenSlots.length && this.playerGivenSlots[slot];
    }

    public void setPlayerGivenSlot(int slot, boolean value) {
        if (slot >= 0 && slot < this.playerGivenSlots.length) {
            this.playerGivenSlots[slot] = value;
        }
    }

    public boolean isShownOffhandPlayerGiven() {
        return this.shownOffhandPlayerGiven;
    }

    public void setShownOffhandPlayerGiven(boolean value) {
        this.shownOffhandPlayerGiven = value;
    }

    public void copyPlayerGivenStateFrom(PiglinAlchemistServant other) {
        for (int i = 0; i < this.playerGivenSlots.length; i++) {
            this.playerGivenSlots[i] = other.playerGivenSlots[i];
        }
        this.shownOffhandPlayerGiven = other.shownOffhandPlayerGiven;
    }

    private void clearPlayerGivenSlots() {
        for (int i = 0; i < this.playerGivenSlots.length; i++) {
            this.playerGivenSlots[i] = false;
        }
        this.shownOffhandPlayerGiven = false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != player || stack.isEmpty()
                || (!isHealingArrow(stack) && !isAllowedSplashPotion(stack))) {
            return super.mobInteract(player, hand);
        }
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        int moved = this.storeInBelt(stack);
        if (moved <= 0) {
            return super.mobInteract(player, hand);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(moved);
        }
        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }

    private static boolean isHealingArrow(ItemStack stack) {
        if (!stack.is(Items.TIPPED_ARROW)) {
            return false;
        }
        for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
            if (effect.getEffect() == MobEffects.HEAL) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static MobEffect splashPotionEffect(ItemStack stack) {
        if (!stack.is(Items.SPLASH_POTION)) {
            return null;
        }
        for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
            MobEffect type = effect.getEffect();
            if (type == MobEffects.FIRE_RESISTANCE
                    || type == MobEffects.REGENERATION
                    || type == MobEffects.HEAL
                    || type == MobEffects.DAMAGE_BOOST) {
                return type;
            }
        }
        return null;
    }

    private static boolean isAllowedSplashPotion(ItemStack stack) {
        return splashPotionEffect(stack) != null;
    }

    private int storeInBelt(ItemStack held) {
        if (isHealingArrow(held)) {
            int remaining = held.getCount();
            for (int i = 0; i < this.beltInventory.size() && remaining > 0; i++) {
                ItemStack slot = this.beltInventory.get(i);
                if (this.isPlayerGivenSlot(i) && isHealingArrow(slot)) {
                    int space = slot.getMaxStackSize() - slot.getCount();
                    if (space <= 0) {
                        continue;
                    }
                    int moved = Math.min(space, remaining);
                    ItemStack updated = slot.copy();
                    updated.grow(moved);
                    this.beltInventory.set(i, updated);
                    remaining -= moved;
                }
            }
            for (int i = 0; i < this.beltInventory.size() && remaining > 0; i++) {
                if (!this.isPlayerGivenSlot(i) && this.beltInventory.get(i).isEmpty()) {
                    int moved = Math.min(64, remaining);
                    this.beltInventory.set(i, held.copyWithCount(moved));
                    this.setPlayerGivenSlot(i, true);
                    remaining -= moved;
                }
            }
            for (int i = 0; i < this.beltInventory.size() && remaining > 0; i++) {
                if (!this.isPlayerGivenSlot(i) && !this.beltInventory.get(i).isEmpty()) {
                    int moved = Math.min(64, remaining);
                    this.beltInventory.set(i, held.copyWithCount(moved));
                    this.setPlayerGivenSlot(i, true);
                    remaining -= moved;
                }
            }
            return held.getCount() - remaining;
        }
        int moved = 0;
        for (int i = 0; i < this.beltInventory.size() && moved < held.getCount(); i++) {
            if (!this.isPlayerGivenSlot(i) && this.beltInventory.get(i).isEmpty()) {
                this.beltInventory.set(i, held.copyWithCount(1));
                this.setPlayerGivenSlot(i, true);
                moved++;
            }
        }
        for (int i = 0; i < this.beltInventory.size() && moved < held.getCount(); i++) {
            if (!this.isPlayerGivenSlot(i) && !this.beltInventory.get(i).isEmpty()) {
                this.beltInventory.set(i, held.copyWithCount(1));
                this.setPlayerGivenSlot(i, true);
                moved++;
            }
        }
        return moved;
    }

    @SuppressWarnings("unchecked")
    public static EntityDataAccessor<ItemStack>[] defineBeltInventory(int capacity) {
        EntityDataAccessor<ItemStack>[] slots = new EntityDataAccessor[capacity];
        for (int i = 0; i < capacity; i++) {
            slots[i] = SynchedEntityData.defineId(PiglinAlchemistServant.class, EntityDataSerializers.ITEM_STACK);
        }
        return slots;
    }

    public class BeltInventory {
        public NonNullList<ItemStack> values() {
            NonNullList<ItemStack> items = NonNullList.withSize(this.size(), ItemStack.EMPTY);
            for (int i = 0; i < this.size(); i++) {
                items.set(i, entityData.get(BELT_SLOTS[i]));
            }
            return items;
        }

        public int size() {
            return BELT_SLOTS.length;
        }

        public void clear() {
            for (int i = 0; i < this.size(); i++) {
                entityData.set(BELT_SLOTS[i], ItemStack.EMPTY);
            }
        }

        public ItemStack get(int slot) {
            return entityData.get(BELT_SLOTS[slot]);
        }

        public void set(int slot, ItemStack stack) {
            entityData.set(BELT_SLOTS[slot], stack);
        }

        public boolean anyMatch(Predicate<ItemStack> predicate) {
            for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
                if (predicate.test(entityData.get(accessor))) {
                    return true;
                }
            }
            return false;
        }

        public boolean noneMatch(Predicate<ItemStack> predicate) {
            for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
                if (predicate.test(entityData.get(accessor))) {
                    return false;
                }
            }
            return true;
        }

        public int countMatches(Predicate<ItemStack> predicate) {
            int count = 0;
            for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
                if (predicate.test(entityData.get(accessor))) {
                    count++;
                }
            }
            return count;
        }

        public List<ItemStack> getMatches(Predicate<ItemStack> predicate) {
            List<ItemStack> items = new ArrayList<>();
            for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
                ItemStack stack = entityData.get(accessor);
                if (predicate.test(stack)) {
                    items.add(stack);
                }
            }
            return items;
        }
    }
}
