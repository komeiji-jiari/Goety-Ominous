package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

public class IllagerElephantServant extends RaiderServant implements ITargetsDroppedItems, IAnimatedEntity, PlayerRideable {
    public static final Animation ANIMATION_TRUMPET_0 = Animation.create(20);
    public static final Animation ANIMATION_TRUMPET_1 = Animation.create(30);
    public static final Animation ANIMATION_CHARGE_PREPARE = Animation.create(25);
    public static final Animation ANIMATION_STOMP = Animation.create(20);
    public static final Animation ANIMATION_FLING = Animation.create(25);
    public static final Animation ANIMATION_EAT = Animation.create(30);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(IllagerElephantServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(IllagerElephantServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.defineId(IllagerElephantServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(IllagerElephantServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHARGE_COOLDOWN = SynchedEntityData.defineId(IllagerElephantServant.class, EntityDataSerializers.INT);
    public static final Map<DyeColor, Item> DYE_COLOR_ITEM_MAP = Util.make(Maps.newHashMap(), map -> {
        map.put(DyeColor.WHITE, Items.WHITE_CARPET);
        map.put(DyeColor.ORANGE, Items.ORANGE_CARPET);
        map.put(DyeColor.MAGENTA, Items.MAGENTA_CARPET);
        map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_CARPET);
        map.put(DyeColor.YELLOW, Items.YELLOW_CARPET);
        map.put(DyeColor.LIME, Items.LIME_CARPET);
        map.put(DyeColor.PINK, Items.PINK_CARPET);
        map.put(DyeColor.GRAY, Items.GRAY_CARPET);
        map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_CARPET);
        map.put(DyeColor.CYAN, Items.CYAN_CARPET);
        map.put(DyeColor.PURPLE, Items.PURPLE_CARPET);
        map.put(DyeColor.BLUE, Items.BLUE_CARPET);
        map.put(DyeColor.BROWN, Items.BROWN_CARPET);
        map.put(DyeColor.GREEN, Items.GREEN_CARPET);
        map.put(DyeColor.RED, Items.RED_CARPET);
        map.put(DyeColor.BLACK, Items.BLACK_CARPET);
    });
    public float prevStandProgress;
    public float standProgress;
    public int maxStandTime = 75;
    public boolean aiItemFlag = false;
    public SimpleContainer elephantInventory;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 0;
    private boolean hasChestVarChanged = false;
    private boolean hasChargedSpeed = false;
    private int chargingTicks = 0;
    private int chestFeedCooldown = 0;

    public IllagerElephantServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.initElephantInventory();
        this.setMaxUpStep(1.1f);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.IllagerElephantServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.IllagerElephantServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.IllagerElephantServantKnockbackResistance.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.IllagerElephantServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.IllagerElephantServantMovementSpeed.get());
    }

    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.IllagerElephantServantHealth.get());
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(AttributesConfig.IllagerElephantServantFollowRange.get());
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(AttributesConfig.IllagerElephantServantKnockbackResistance.get());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.IllagerElephantServantDamage.get());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.IllagerElephantServantMovementSpeed.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag tag) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
    }

    @Override
    public void setTrueOwner(LivingEntity owner) {
        LivingEntity previousOwner = this.getTrueOwner();
        if (owner != null) {
            this.setOwnerId(owner.getUUID());
            this.setOwnerClientId(owner.getId());
        } else {
            this.removeTrueOwner();
        }
        if (!this.level().isClientSide && previousOwner != owner && owner instanceof Player player) {
            if (countServants(player) > MobsConfig.IllagerElephantServantLimit.get()) {
                this.discard();
            }
        }
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof IllagerElephantServant servant) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Nullable
    public static DyeColor getCarpetColor(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        return block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ELEPHANT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ELEPHANT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ELEPHANT_DIE.get();
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    private void initElephantInventory() {
        SimpleContainer animalchest = this.elephantInventory;
        this.elephantInventory = new SimpleContainer(54) {
            @Override
            public boolean stillValid(Player player) {
                return IllagerElephantServant.this.isAlive() && !IllagerElephantServant.this.isRemoved();
            }
        };
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.elephantInventory.getContainerSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (itemstack.isEmpty()) continue;
                this.elephantInventory.setItem(j, itemstack.copy());
            }
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getAnimationTick() < 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ElephantMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new ElephantChestFeedGoal());
        this.targetSelector.addGoal(6, new ElephantPickupItemsGoal(this));
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(AMSoundRegistry.ELEPHANT_WALK.get(), 0.2f, 1.0f);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return (Player) passenger;
            }
        }
        return null;
    }

    private boolean hasRider() {
        return !this.getPassengers().isEmpty();
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.isActivelySteering();
        boolean notInBoat = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !steering && notInBoat);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !steering);
    }

    private boolean isActivelySteering() {
        if (this.getControllingPassenger() instanceof Player player) {
            return player.zza != 0.0f || player.xxa != 0.0f;
        }
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 2 && (passenger instanceof Player || passenger instanceof IServant);
    }

    @Override
    public boolean canBeRidden(LivingEntity livingEntity) {
        if (this.getPassengers().size() >= 2) {
            return false;
        }
        if (livingEntity instanceof Player player) {
            return player == this.getTrueOwner();
        }
        if (livingEntity instanceof IOwned) {
            return ((IOwned) livingEntity).getTrueOwner() == this.getTrueOwner();
        }
        return false;
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    public int getChargeCooldown() {
        return this.entityData.get(CHARGE_COOLDOWN);
    }

    public void setChargeCooldown(int cooldown) {
        this.entityData.set(CHARGE_COOLDOWN, cooldown);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STANDING, false);
        this.entityData.define(CHESTED, false);
        this.entityData.define(CARPET_COLOR, -1);
        this.entityData.define(CHARGING, false);
        this.entityData.define(CHARGE_COOLDOWN, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevStandProgress = this.standProgress;
        if (this.isStanding()) {
            if (this.standProgress < 5.0f) {
                this.standProgress += 0.5f;
            }
        } else if (this.standProgress > 0.0f) {
            this.standProgress -= 0.5f;
        }
        if (this.isStaying()) {
            if (this.isStanding()) {
                this.setStanding(false);
            }
            this.standingTime = 0;
        } else {
            boolean rearUpAllowed = this.isWandering() && this.getTarget() == null && !this.hasRider() && this.getNavigation().isDone();
            if (this.isStanding()) {
                if (++this.standingTime > this.maxStandTime) {
                    this.setStanding(false);
                    this.standingTime = 0;
                    this.maxStandTime = 75 + this.random.nextInt(50);
                } else if (!rearUpAllowed) {
                    this.setStanding(false);
                    this.standingTime = 0;
                }
            }
            if (!this.level().isClientSide && !this.isStanding() && rearUpAllowed && this.getRandom().nextInt(600) == 0) {
                this.setStanding(true);
            }
        }
        if (this.hasChestVarChanged && this.elephantInventory != null && !this.isChested()) {
            if (!this.level().isClientSide) {
                for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                    if (this.elephantInventory.getItem(i).isEmpty()) continue;
                    this.spawnAtLocation(this.elephantInventory.getItem(i), 1.0f);
                }
                this.elephantInventory.clearContent();
            }
            this.hasChestVarChanged = false;
        }
        this.chargingTicks = this.isCharging() ? ++this.chargingTicks : 0;
        Player rider = this.getControllingPassenger() instanceof Player player ? player : null;
        boolean riderCombat = rider != null && rider.getLastHurtMob() != null && !this.isAlliedTo(rider.getLastHurtMob());
        if (!this.level().isClientSide && this.getChargeCooldown() > 0) {
            this.setChargeCooldown(this.getChargeCooldown() - 1);
        }
        if (!this.level().isClientSide && !riderCombat && this.getTarget() == null && !this.getMainHandItem().isEmpty() && this.canTargetItem(this.getMainHandItem()) && this.getHealth() < this.getMaxHealth()) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_EAT);
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 17) {
                this.eatItemEffect(this.getMainHandItem());
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.heal(10.0f);
            }
        }
        if (!this.level().isClientSide && this.chestFeedCooldown > 0) {
            --this.chestFeedCooldown;
        }
        if (!this.level().isClientSide && this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            this.yBodyRot = this.getYRot();
            if (this.getAnimationTick() == 20 && !this.isStaying()) {
                this.setCharging(true);
            }
        }
        LivingEntity target = this.getTarget();
        double maxAttackMod = 0.0;
        if (riderCombat) {
            target = rider.getLastHurtMob();
            maxAttackMod = 4.0;
        }
        if (!this.level().isClientSide && this.isCharging() && this.chargingTicks > 100) {
            this.setCharging(false);
            this.setChargeCooldown(200);
        }
        if (!this.level().isClientSide && target != null) {
            if (this.distanceTo(target) > this.getBbWidth() * 2.0f + 0.5f && !this.isActivelySteering() && this.hasLineOfSight(target) && this.getAnimation() == NO_ANIMATION && !this.isCharging() && this.getChargeCooldown() == 0 && !this.isStaying()) {
                this.setAnimation(ANIMATION_CHARGE_PREPARE);
            }
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE && !this.isActivelySteering()) {
                this.lookAt(target, 360.0f, 30.0f);
                this.yBodyRot = this.getYRot();
            }
            double dist = this.distanceTo(target);
            if (this.getControllingPassenger() != null && this.getAnimation() == NO_ANIMATION && !this.isCharging() && dist < 4.5 + maxAttackMod) {
                this.doHurtTarget(target);
            }
            if (dist < 10.0 && this.isCharging() && this.getAnimation() != ANIMATION_FLING) {
                this.setAnimation(ANIMATION_FLING);
            }
            if (dist < this.getBbWidth() * 0.5f + target.getBbWidth() * 0.5f && this.isCharging()) {
                target.knockback(1.0, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hasImpulse = true;
                target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.7, 0.0));
                target.hurt(this.damageSources().mobAttack(this), 2.4f * (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                this.launch(target, true);
                this.setCharging(false);
                this.setChargeCooldown(400);
            }
        }
        if (!this.level().isClientSide && this.getTarget() == null && !riderCombat) {
            this.setCharging(false);
        }
        if (!this.level().isClientSide && this.isStaying() && this.isCharging()) {
            this.setCharging(false);
            this.setChargeCooldown(200);
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE || this.getAnimation() == ANIMATION_FLING) {
                this.setAnimation(NO_ANIMATION);
            }
        }
        if (this.isCharging() && !this.hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.IllagerElephantServantMovementSpeed.get() + 0.3);
            this.hasChargedSpeed = true;
        }
        if (!this.isCharging() && this.hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.IllagerElephantServantMovementSpeed.get());
            this.hasChargedSpeed = false;
        }
        if (!this.level().isClientSide && this.getRandom().nextInt(400) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_TRUMPET_0 : ANIMATION_TRUMPET_1);
        }
        if (!this.level().isClientSide && (this.getAnimation() == ANIMATION_TRUMPET_0 && this.getAnimationTick() == 8 || this.getAnimation() == ANIMATION_TRUMPET_1 && this.getAnimationTick() == 4)) {
            this.gameEvent(GameEvent.ENTITY_ROAR);
            this.playSound(AMSoundRegistry.ELEPHANT_TRUMPET.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (!this.level().isClientSide && this.isAlive() && this.isCharging()) {
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))) {
                if (entity == this || this.hasPassenger(entity) || this.isAlliedTo(entity)) continue;
                entity.hurt(this.damageSources().mobAttack(this), 8.0f + this.random.nextFloat() * 8.0f);
                this.launch(entity, true);
            }
            this.setMaxUpStep(2.0f);
        } else {
            this.setMaxUpStep(1.1f);
        }
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
            float f = huge ? 2.0f : 0.5f;
            e.push(d0 / d2 * f, huge ? 0.5 : 0.2f, d1 / d2 * f);
        }
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.STRIDER_EAT, this.getVoicePitch(), this.getSoundVolume());
        for (int i = 0; i < 8 + this.random.nextInt(3); ++i) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            float radius = this.getBbWidth() * 0.65f;
            float angle = (float) Math.PI / 180 * this.yBodyRot;
            double extraX = radius * Mth.sin((float) Math.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6f, this.getZ() + extraZ, d0, d1, d2);
        }
    }

    private ItemStack takeFoodFromChest(Predicate<ItemStack> foodPredicate) {
        for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
            ItemStack stack = this.elephantInventory.getItem(i);
            if (stack.isEmpty() || !foodPredicate.test(stack)) continue;
            ItemStack take = stack.split(1);
            if (stack.isEmpty()) {
                this.elephantInventory.setItem(i, ItemStack.EMPTY);
            }
            return take;
        }
        return ItemStack.EMPTY;
    }

    private boolean hasFoodInChest(Predicate<ItemStack> foodPredicate) {
        if (this.elephantInventory == null) return false;
        for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
            ItemStack stack = this.elephantInventory.getItem(i);
            if (!stack.isEmpty() && foodPredicate.test(stack)) return true;
        }
        return false;
    }

    private boolean isServantFood(ItemStack stack, AbstractIllagerServant servant) {
        if (stack.is(Items.ROTTEN_FLESH)) {
            return false;
        }
        FoodProperties food = stack.getFoodProperties(servant);
        if (food == null) {
            return false;
        }
        if (food.isMeat() && food.getNutrition() <= 3) {
            return false;
        }
        return food.getEffects().isEmpty();
    }

    private void feedServantFood(AbstractIllagerServant servant, ItemStack food) {
        FoodProperties props = food.getFoodProperties(servant);
        int nutrition = props == null ? 0 : props.getNutrition();
        servant.heal(1.0f + (float) nutrition * 0.5f);
        this.level().playSound(null, servant.getX(), servant.getY(), servant.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.6f, this.random.nextFloat() * 0.4f + 1.0f);
        for (int k = 0; k < 6; ++k) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            double ex = servant.getX() + (this.random.nextFloat() - 0.5f) * servant.getBbWidth() * 0.6f;
            double ey = servant.getY() + servant.getBbHeight() * 0.6f;
            double ez = servant.getZ() + (this.random.nextFloat() - 0.5f) * servant.getBbWidth() * 0.6f;
            this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, food), ex, ey, ez, d0, d1, d2);
        }
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
    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level().isClientSide && entityIn instanceof LivingEntity target) {
            if (!this.isCharging()) {
                boolean fling = this.random.nextBoolean();
                this.setAnimation(fling ? ANIMATION_FLING : ANIMATION_STOMP);
                float damage = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                if (fling) {
                    target.knockback(1.0, target.getX() - this.getX(), target.getZ() - this.getZ());
                    target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.3, 0.0));
                    this.launch(target, false);
                } else {
                    target.knockback(0.3, target.getX() - this.getX(), target.getZ() - this.getZ());
                }
                target.hurt(this.damageSources().mobAttack(this), damage);
            }
        }
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean owner = this.getTrueOwner() == player;
        InteractionResult type = super.mobInteract(player, hand);
        if (this.isChested() && player.isShiftKeyDown()) {
            if (owner) {
                this.openGUI(player);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (this.canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            return InteractionResult.SUCCESS;
        }
        if (owner && stack.is(ItemTags.WOOL_CARPETS)) {
            DyeColor color = IllagerElephantServant.getCarpetColor(stack);
            if (color != this.getColor()) {
                if (this.getColor() != null) {
                    this.spawnAtLocation(this.getCarpetItemBeingWorn());
                }
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.LLAMA_SWAG, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.setColor(color);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (owner && this.getColor() != null && stack.is(Tags.Items.SHEARS)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            if (this.getColor() != null) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
            return InteractionResult.SUCCESS;
        }
        if (owner && !this.isChested() && stack.is(Tags.Items.CHESTS_WOODEN)) {
            this.setChested(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (owner && this.isChested() && stack.is(Tags.Items.SHEARS)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.spawnAtLocation(Blocks.CHEST);
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                this.spawnAtLocation(this.elephantInventory.getItem(i));
            }
            this.elephantInventory.clearContent();
            this.setChested(false);
            return InteractionResult.SUCCESS;
        }
        if (owner && !this.isBaby() && type == InteractionResult.PASS) {
            if (stack.getItem() instanceof IWand) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide) {
                if (player.getVehicle() == this) {
                    player.stopRiding();
                } else if (this.getPassengers().size() < 2) {
                    player.startRiding(this);
                } else {
                    this.getFirstPassenger().stopRiding();
                    player.startRiding(this);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (type == InteractionResult.PASS) {
            type = stack.interactLivingEntity(player, this, hand);
        }
        return type;
    }

    public boolean triggerCharge() {
        if (this.getControllingPassenger() == null) {
            return false;
        }
        if (this.isCharging() || this.getChargeCooldown() > 0 || this.getAnimation() != NO_ANIMATION) {
            return false;
        }
        this.setAnimation(ANIMATION_CHARGE_PREPARE);
        return true;
    }

    public Animation getAnimation() {
        return this.currentAnimation;
    }

    public void setAnimation(Animation animation) {
        if (this.currentAnimation != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_TRUMPET_0, ANIMATION_TRUMPET_1, ANIMATION_CHARGE_PREPARE, ANIMATION_STOMP, ANIMATION_FLING, ANIMATION_EAT};
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    public Item getCarpetItemBeingWorn() {
        if (this.getColor() != null) {
            return DYE_COLOR_ITEM_MAP.get(this.getColor());
        }
        return Items.AIR;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isChested()) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                this.spawnAtLocation(this.elephantInventory.getItem(i));
            }
            this.elephantInventory.clearContent();
            this.setChested(false);
        }
        if (this.getColor() != null) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("Chested", this.isChested());
        compound.putInt("ChargeCooldown", this.getChargeCooldown());
        compound.putInt("Carpet", this.entityData.get(CARPET_COLOR));
        if (this.elephantInventory != null) {
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (itemstack.isEmpty()) continue;
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemstack.save(compoundTag);
                nbttaglist.add(compoundTag);
            }
            compound.put("Items", nbttaglist);
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStanding(compound.getBoolean("Standing"));
        this.setChested(compound.getBoolean("Chested"));
        this.setChargeCooldown(compound.getInt("ChargeCooldown"));
        if (compound.contains("Carpet")) {
            this.entityData.set(CARPET_COLOR, compound.getInt("Carpet"));
        }
        if (this.elephantInventory != null) {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initElephantInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag compoundTag = nbttaglist.getCompound(i);
                int j = compoundTag.getByte("Slot") & 0xFF;
                if (j < 0 || j >= this.elephantInventory.getContainerSize()) continue;
                this.elephantInventory.setItem(j, ItemStack.of(compoundTag));
            }
        }
    }

    public boolean isChested() {
        return this.entityData.get(CHESTED);
    }

    public void setChested(boolean chested) {
        this.entityData.set(CHESTED, chested);
        this.hasChestVarChanged = true;
    }

    public boolean setSlot(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.elephantInventory.getContainerSize()) {
            this.elephantInventory.setItem(j, itemStackIn);
            return true;
        }
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.elephantInventory != null && !this.level().isClientSide) {
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (itemstack.isEmpty()) continue;
                this.spawnAtLocation(itemstack, 0.0f);
            }
        }
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    @Nullable
    public DyeColor getColor() {
        int color = this.entityData.get(CARPET_COLOR);
        return color == -1 ? null : DyeColor.byId(color);
    }

    public void setColor(@Nullable DyeColor color) {
        this.entityData.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.ELEPHANT_FOODSTUFFS);
    }

    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0f);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        this.aiItemFlag = false;
    }

    public void onFindTarget(ItemEntity e) {
        this.aiItemFlag = true;
    }

    public double getMaxDistToItem() {
        return Math.pow(this.getBbWidth() + 3.0f, 2.0);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            int seat = this.getPassengers().indexOf(passenger);
            float standAdd = -0.3f * this.standProgress;
            float scale = 1.1f;
            float scaleY = scale * (-0.4f * standAdd);
            float radius = scale * (0.5f + standAdd);
            float angle = (float) Math.PI / 180 * this.yBodyRot;
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
                float sinWave = Mth.sin((float) Math.PI * (this.getAnimationTick() / 25.0f));
                radius += sinWave * 0.2f * scale;
            }
            if (this.getAnimation() == ANIMATION_STOMP) {
                float sinWave = Mth.sin((float) Math.PI * (this.getAnimationTick() / 20.0f));
                radius -= sinWave * 1.0f * scale;
                scaleY += sinWave * 0.7f * scale;
            }
            if (seat > 0) {
                radius -= scale * 1.25f * seat;
            }
            double extraX = radius * Mth.sin((float) Math.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + scaleY + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        if (player.zza != 0.0f || player.xxa != 0.0f) {
            float f = player.zza < 0.0f ? 0.5f : 1.0f;
            return new Vec3(player.xxa * 0.25f, 0.0, player.zza * 0.5f * f);
        }
        // 骑乘空闲时自动向目标追击（战象冲锋），停留/蓄力起手时保持原地
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && !this.isStaying() && !this.isImmobile()) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 1.5) {
                return new Vec3(dx / dist * 0.5, 0.0, dz / dist * 0.5);
            }
        }
        this.setSprinting(false);
        return Vec3.ZERO;
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0f || player.xxa != 0.0f) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25f);
            this.yBodyRot = this.yHeadRot = this.getYRot();
            this.yRotO = this.yHeadRot;
            this.getNavigation().stop();
            this.setSprinting(true);
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public double getPassengersRidingOffset() {
        float scale = 1.1f;
        float f = Math.min(0.25f, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        return this.getBbHeight() - 0.05f - scale * (0.1f * Mth.cos(f1 * 1.4f) * 1.4f * f);
    }

    public void openGUI(Player playerEntity) {
        if (!this.level().isClientSide && !this.hasPassenger(playerEntity)) {
            NetworkHooks.openScreen((ServerPlayer) playerEntity, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return ChestMenu.sixRows(p_createMenu_1_, p_createMenu_2_, IllagerElephantServant.this.elephantInventory);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("entity.alexsmobs.elephant.chest");
                }
            });
        }
    }

    class ElephantChestFeedGoal extends Goal {
        @Override
        public boolean canUse() {
            if (IllagerElephantServant.this.chestFeedCooldown > 0 || !IllagerElephantServant.this.isChested()
                    || IllagerElephantServant.this.elephantInventory == null) {
                return false;
            }
            for (Entity passenger : IllagerElephantServant.this.getPassengers()) {
                if (passenger instanceof AbstractIllagerServant servant
                        && servant.getHealth() < servant.getMaxHealth()
                        && IllagerElephantServant.this.hasFoodInChest(stack -> IllagerElephantServant.this.isServantFood(stack, servant))) {
                    return true;
                }
            }
            return selfNeedsChestFood();
        }

        @Override
        public void start() {
            for (Entity passenger : IllagerElephantServant.this.getPassengers()) {
                if (passenger instanceof AbstractIllagerServant servant && servant.getHealth() < servant.getMaxHealth()) {
                    ItemStack feed2 = IllagerElephantServant.this.takeFoodFromChest(stack -> IllagerElephantServant.this.isServantFood(stack, servant));
                    if (!feed2.isEmpty()) {
                        IllagerElephantServant.this.feedServantFood(servant, feed2);
                    }
                }
            }
            if (selfNeedsChestFood()) {
                ItemStack feed = IllagerElephantServant.this.takeFoodFromChest(IllagerElephantServant.this::canTargetItem);
                if (!feed.isEmpty()) {
                    IllagerElephantServant.this.setItemInHand(InteractionHand.MAIN_HAND, feed);
                }
            }
            IllagerElephantServant.this.chestFeedCooldown = 200;
        }

        private boolean selfNeedsChestFood() {
            Player rider = IllagerElephantServant.this.getControllingPassenger() instanceof Player player ? player : null;
            boolean riderCombat = rider != null && rider.getLastHurtMob() != null && !IllagerElephantServant.this.isAlliedTo(rider.getLastHurtMob());
            return !riderCombat && IllagerElephantServant.this.getTarget() == null
                    && IllagerElephantServant.this.getHealth() < IllagerElephantServant.this.getMaxHealth()
                    && IllagerElephantServant.this.getMainHandItem().isEmpty()
                    && IllagerElephantServant.this.hasFoodInChest(IllagerElephantServant.this::canTargetItem);
        }
    }

    class ElephantPickupItemsGoal extends CreatureAITargetItems {
        public ElephantPickupItemsGoal(IllagerElephantServant e) {
            super(e, false);
        }

        @Override
        public boolean canUse() {
            return IllagerElephantServant.this.getTarget() == null && super.canUse();
        }
    }

    class ElephantMeleeAttackGoal extends MeleeAttackGoal {
        private int attackCooldown = 0;

        public ElephantMeleeAttackGoal(IllagerElephantServant e, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(e, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        protected int getAttackInterval() {
            return 30;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double dist) {
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
            double reach = this.getAttackReachSqr(target);
            if (dist <= reach && this.attackCooldown <= 0 && this.mob.getSensing().hasLineOfSight(target)) {
                if (!IllagerElephantServant.this.isCharging()) {
                    this.attackCooldown = this.getAttackInterval();
                    this.mob.swing(InteractionHand.MAIN_HAND);
                    this.mob.doHurtTarget(target);
                }
            }
        }
    }
}
