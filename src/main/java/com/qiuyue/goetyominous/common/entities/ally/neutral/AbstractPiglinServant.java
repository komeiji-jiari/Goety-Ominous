package com.qiuyue.goetyominous.common.entities.ally.neutral;

import javax.annotation.Nullable;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ServantUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.NetherWartPotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractPiglinServant extends Summoned implements ILooter {
    protected static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;
    protected static final int CONVERSION_TIME = 300;
    protected static final float PIGLIN_EYE_HEIGHT = 1.79F;
    protected int timeInOverworld;
    protected int rangedDamageDealt;
    public @Nullable AbstractPiglinServant breedingPartner;
    public void setImmune(boolean immune) { this.setImmuneToZombification(immune); }
    private int breedCool;
    private int eatCool;
    private int eatenFoodLevel;
    private int foodLevel;
    public @Nullable BlockPos chestPos;
    public String chestDim;
    public @Nullable BlockPos dumpChestPos;
    public String dumpChestDim;
    private final SimpleContainer inventory;
    protected int meleeDamageDealt;
    public boolean isImmune() { return this.isImmuneToZombification(); }
    public int getRangedDamageDealt() { return this.rangedDamageDealt; }
    public int getMeleeDamageDealt() { return this.meleeDamageDealt; }

    public AbstractPiglinServant(EntityType<? extends Owned> p_34652_, Level p_34653_) {
        super(p_34652_, p_34653_);
        this.setCanPickUpLoot(true);
        this.applyOpenDoorsAbility();
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.chestDim = Level.OVERWORLD.location().toString();
        this.dumpChestDim = Level.OVERWORLD.location().toString();
        this.inventory = new SimpleContainer(8);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new com.qiuyue.goetyominous.common.entities.ai.PiglinPutFoodChestGoal(this));
        this.goalSelector.addGoal(3, new GiveExcessFoodGoal(this));
        this.goalSelector.addGoal(4, new com.qiuyue.goetyominous.common.entities.ai.PiglinPutLootChestGoal(this));
        this.goalSelector.addGoal(5, new ThrowLootGoal(this));
        this.goalSelector.addGoal(7, new MakeLove(this));
        this.goalSelector.addGoal(7, new com.qiuyue.goetyominous.common.entities.ai.PiglinLootFoodChestGoal(this));
    }

    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        }

    }

    protected float getStandingEyeHeight(Pose p_259213_, EntityDimensions p_259279_) {
        return 1.79F;
    }

    protected abstract boolean canHunt();

    public void setImmuneToZombification(boolean p_34671_) {
        this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, p_34671_);
    }

    protected boolean isImmuneToZombification() {
        return (Boolean)this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
    }

    public void addAdditionalSaveData(CompoundTag p_34661_) {
        p_34661_.putInt("BreedCool", this.breedCool);
        p_34661_.putInt("EatCool", this.eatCool);
        p_34661_.putInt("EatenFoodLevel", this.eatenFoodLevel);
        this.saveLooterData(p_34661_);
        this.writeInventoryToTag(p_34661_);
        p_34661_.putInt("MeleeDamageDealt", this.meleeDamageDealt);
        p_34661_.putInt("RangedDamageDealt", this.rangedDamageDealt);
        super.addAdditionalSaveData(p_34661_);

        if (this.isImmuneToZombification()) {
            p_34661_.putBoolean("IsImmuneToZombification", true);
        }

        p_34661_.putInt("TimeInOverworld", this.timeInOverworld);
    }

    public void readAdditionalSaveData(CompoundTag p_34659_) {
        if (p_34659_.contains("BreedCool")) {
            this.breedCool = p_34659_.getInt("BreedCool");
        }
        if (p_34659_.contains("EatCool")) {
            this.eatCool = p_34659_.getInt("EatCool");
        }
        if (p_34659_.contains("EatenFoodLevel")) {
            this.eatenFoodLevel = p_34659_.getInt("EatenFoodLevel");
        }
        this.readLooterData(p_34659_);
        this.readInventoryFromTag(p_34659_);
        this.meleeDamageDealt = p_34659_.getInt("MeleeDamageDealt");
        this.rangedDamageDealt = p_34659_.getInt("RangedDamageDealt");
        super.readAdditionalSaveData(p_34659_);
        this.setImmuneToZombification(p_34659_.getBoolean("IsImmuneToZombification"));
        this.timeInOverworld = p_34659_.getInt("TimeInOverworld");
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.breedCool > 0) {
            --this.breedCool;
        }
        if (this.eatCool > 0) {
            --this.eatCool;
        }
        if (this.eatenFoodLevel > 0 && this.tickCount % 20 == 0) {
            --this.eatenFoodLevel;
            this.heal(1.0F);
        }
        if (this.isConverting()) {
            ++this.timeInOverworld;
        } else {
            this.timeInOverworld = 0;
            this.timeInOverworld = 0;
        }

        if (this.timeInOverworld > 300 && ForgeEventFactory.canLivingConvert(this, com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_SERVANT.get(), (timer) -> {
            this.timeInOverworld = timer;
        })) {
            this.playConvertedSound();
            this.finishConversion((ServerLevel)this.level());
        }

    }

    public boolean validLootToStore(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (this.validFood(itemStack)) return false;
        if (itemStack.is(Items.GOLD_INGOT) || itemStack.is(Items.GOLD_NUGGET)
                || itemStack.is(Items.GOLD_BLOCK) || itemStack.is(Items.GOLDEN_APPLE)
                || itemStack.is(Items.GOLDEN_CARROT) || itemStack.is(Items.GOLDEN_SWORD)
                || itemStack.is(Items.GOLDEN_AXE) || itemStack.is(Items.GOLDEN_PICKAXE)
                || itemStack.is(Items.GOLDEN_SHOVEL) || itemStack.is(Items.GOLDEN_HOE)
                || itemStack.is(Items.GOLDEN_HELMET) || itemStack.is(Items.GOLDEN_CHESTPLATE)
                || itemStack.is(Items.GOLDEN_LEGGINGS) || itemStack.is(Items.GOLDEN_BOOTS)
                || itemStack.is(Items.RAW_GOLD) || itemStack.is(Items.RAW_GOLD_BLOCK)) {
            return false;
        }
        return true;
    }

    public boolean canPickUpLoot() {
        return true;
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return this.validFood(itemStack) && this.canHaveMoreFood() && this.getInventory().canAddItem(itemStack);
    }

    protected void pickUpItem(ItemEntity itemEntity) {
        InventoryCarrier.pickUpItem(this, this, itemEntity);
    }

    public boolean inventoryFull() {
        int i = 0;
        for(int j = 0; j < this.getInventory().getContainerSize(); ++j) {
            if (this.getInventory().getItem(j).isEmpty()) {
                ++i;
            }
        }
        return i == 0;
    }

    public abstract static class ThrowItemGoal extends Goal {
        public AbstractPiglinServant piglin;
        public LivingEntity target;
        public Predicate<ItemStack> predicate = (itemStack) -> false;
        public Predicate<LivingEntity> targetPredicate = (living) -> false;
        public int throwTime;

        public ThrowItemGoal(AbstractPiglinServant piglin) {
            this.piglin = piglin;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            if (this.piglin.getTarget() == null && !this.piglin.isCommanded() && this.hasItem()) {
                this.target = this.getThrowTarget();
                if (this.target != null) {
                    if (this.piglin.isStaying()) {
                        return this.piglin.isWithinThrowingDistance(this.target) && this.piglin.hasLineOfSight(this.target);
                    }
                    return (double)this.target.distanceTo(this.piglin) <= 8.0 && this.piglin.hasLineOfSight(this.target);
                }
            }
            return false;
        }

        public void stop() {
            this.target = null;
            this.throwTime = 0;
        }

        public boolean isWithinThrowingDistance() {
            return this.target == null ? false : this.piglin.isWithinThrowingDistance(this.target);
        }

        public void tick() {
            if (this.target == null) { this.stop(); }
            this.piglin.getLookControl().setLookAt(this.target, 10.0F, (float)this.piglin.getMaxHeadXRot());
            if (this.isWithinThrowingDistance()) {
                this.piglin.getNavigation().stop();
                ++this.throwTime;
                if (this.throwTime > 20) {
                    this.throwItem();
                    this.throwTime = 0;
                }
            } else {
                this.piglin.getNavigation().moveTo(this.target, 0.6);
            }
            if (this.target instanceof Mob mob) {
                mob.getLookControl().setLookAt(this.piglin);
                mob.getNavigation().stop();
            }
        }

        public void throwItem() {
            if (this.target == null) { this.stop(); }
            for (ItemStack itemstack : this.piglin.itemsInInv(this.predicate)) {
                BehaviorUtils.throwItem(this.piglin, itemstack.copyAndClear(), this.target.position());
            }
        }

        public boolean hasItem() {
            return !this.piglin.itemsInInv(this.predicate).isEmpty();
        }

        public @Nullable LivingEntity getThrowTarget() {
            List<LivingEntity> list = this.piglin.level().getEntitiesOfClass(LivingEntity.class, this.piglin.getBoundingBox().inflate(16.0));
            list.sort(Comparator.comparingDouble(this.piglin::distanceToSqr));
            for (LivingEntity servant : list) {
                if (servant != this.piglin && this.targetPredicate.test(servant) && this.piglin.hasLineOfSight(servant)) {
                    return servant;
                }
            }
            return null;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class ThrowLootGoal extends ThrowItemGoal {
        public ThrowLootGoal(AbstractPiglinServant piglin) {
            super(piglin);
            this.predicate = piglin::validLootToStore;
            this.targetPredicate = (livingEntity) -> {
                return piglin.getTrueOwner() != null && livingEntity == piglin.getTrueOwner();
            };
        }

        public @Nullable LivingEntity getThrowTarget() {
            return this.piglin.getTrueOwner();
        }
    }

    public static class GiveExcessFoodGoal extends ThrowItemGoal {
        public GiveExcessFoodGoal(AbstractPiglinServant piglin) {
            super(piglin);
            this.predicate = (itemStack) -> {
                return piglin.validFood(itemStack);
            };
            this.targetPredicate = (living) -> {
                return living instanceof AbstractPiglinServant servant
                        && servant.getTrueOwner() == piglin.getTrueOwner()
                        && servant.wantsMoreFood() && !servant.isBaby();
            };
        }

        public boolean canUse() {
            return this.piglin.hasExcessFood() ? super.canUse() : false;
        }

        @Override
        public void throwItem() {
            if (this.target == null) { this.stop(); return; }
            for (ItemStack itemstack : this.piglin.itemsInInv(this.predicate)) {
                int count = itemstack.getCount();
                int excess = 0;
                if (count > itemstack.getMaxStackSize() / 2) {
                    excess = count / 2;
                }
                if (count > 24) {
                    excess = count - 24;
                }
                if (excess > 0) {
                    ItemStack toThrow = itemstack.split(excess);
                    BehaviorUtils.throwItem(this.piglin, toThrow, this.target.position());
                }
                break;
            }
        }
    }

    public boolean isWithinDistance(Entity entity, double distance) {
        if (entity == null) return false;
        return this.blockPosition().closerThan(entity.blockPosition(), distance);
    }

    public boolean isWithinThrowingDistance(Entity entity) {
        return this.isWithinDistance(entity, 5.0);
    }

    public List<ItemStack> itemsInInv(Predicate<ItemStack> predicate) {
        return com.Polarice3.Goety.api.entities.ally.illager.ILooter.super.itemsInInv(predicate);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SlotAccess getSlot(int p_149995_) {
        int i = p_149995_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149995_);
    }

    public boolean isWithinGuard(BlockPos pos) {
        if (this.getBoundPos() == null) return false;
        return this.getBoundPos().distSqr(pos) <= 256.0D;
    }

    public int getBreedCool() {
        return this.breedCool;
    }

    public void setBreedCool(int cool) {
        this.breedCool = cool;
    }

    public boolean canBreed() {
        return this.foodLevel + this.countFoodPointsInInventory() >= 12
                && !this.isSleeping() && this.breedCool <= 0
                && this.hurtTime <= 0 && this.getTarget() == null
                && !this.isFollowing() && !this.isCommanded()
                && !this.isBaby();
    }

    private boolean hungry() {
        return this.foodLevel < 12;
    }

    private void eatUntilFull() {
        if (this.hungry() && this.countFoodPointsInInventory() != 0) {
            for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);
                if (!itemstack.isEmpty()) {
                    Integer integer = this.getFoodPoints().get(itemstack.getItem());
                    if (integer != null) {
                        int j = itemstack.getCount();
                        for(int k = j; k > 0; --k) {
                            this.foodLevel += integer;
                            this.getInventory().removeItem(i, 1);
                            if (!this.hungry()) return;
                        }
                    }
                }
            }
        }
    }

    private void digestFood(int p_35549_) {
        this.foodLevel -= p_35549_;
    }

    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }

    public int countFoodPointsInInventory() {
        SimpleContainer simplecontainer = this.getInventory();
        return this.getFoodPoints().entrySet().stream().mapToInt((entry) -> {
            return simplecontainer.countItem(entry.getKey()) * entry.getValue();
        }).sum();
    }

    public int countFoodInInventory() {
        SimpleContainer simplecontainer = this.getInventory();
        return this.getFoodPoints().keySet().stream().mapToInt(simplecontainer::countItem).sum();
    }

    public boolean validFood(ItemStack itemStack) {
        return itemStack.is(Items.CARROT) || itemStack.is(Items.PORKCHOP)
                || itemStack.is(Items.COOKED_PORKCHOP) || itemStack.is(Items.GOLDEN_CARROT)
                || itemStack.is(Items.POTATO) || itemStack.is(Items.BAKED_POTATO) || itemStack.is(Items.BEETROOT);
    }

    public Map<Item, Integer> getFoodPoints() {
        Map<Item, Integer> foodPoints = new HashMap<>();
        for(int j = 0; j < this.getInventory().getContainerSize(); ++j) {
            ItemStack itemStack = this.getInventory().getItem(j);
            if (this.validFood(itemStack)) {
                FoodProperties foodProperties = itemStack.getFoodProperties(this);
                if (foodProperties != null) {
                    foodPoints.put(itemStack.getItem(), foodProperties.getNutrition());
                }
            }
        }
        return foodPoints;
    }

    public boolean hasExcessFood() {
        return this.countFoodInInventory() > 24;
    }

    public boolean canHaveMoreFood() {
        return this.countFoodPointsInInventory() < 64;
    }

    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float prevHealth = target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0;
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            int dealt = (int) Math.ceil(prevHealth - living.getHealth());
            if (dealt > 0) {
                this.meleeDamageDealt += dealt;
                this.onMeleeDamageDealt();
            }
        }
        return result;
    }

    protected void onMeleeDamageDealt() {
    }

    public void onRangedDamageDealt(int damage) {
        this.rangedDamageDealt += damage;
    }

    protected void onRangedDamageDealt() {
    }

    public boolean isConverting() {
        return !this.level().dimensionType().piglinSafe()
                && !this.isImmuneToZombification()
                && !this.isNoAi()
                && !(this.getTrueOwner() != null && CuriosFinder.hasNetherRobe(this.getTrueOwner()));
    }

    protected void finishConversion(ServerLevel serverLevel) {
        com.Polarice3.Goety.common.entities.neutral.ZPiglinServant zombifiedpiglin =
                new com.Polarice3.Goety.common.entities.neutral.ZPiglinServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_SERVANT.get(), serverLevel);
        zombifiedpiglin.copyTrueOwner(this);
        zombifiedpiglin.setBaby(this.isBaby());
        zombifiedpiglin.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombifiedpiglin.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombifiedpiglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombifiedpiglin.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        ForgeEventFactory.onLivingConvert(this, zombifiedpiglin);
        this.discard();
        serverLevel.addFreshEntity(zombifiedpiglin);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide
                && this.getTrueOwner() != null
                && CuriosFinder.hasNamelessSet(this.getTrueOwner())
                && !this.isBaby()) {
            EntityType<? extends Mob> variantType = getZombieVariantType();
            if (variantType != null) {
                Mob zombie = this.convertTo(variantType, true);
                if (zombie instanceof Owned owned) {
                    owned.copyTrueOwner(this);
                }
                ForgeEventFactory.onLivingConvert(this, zombie);
                return;
            }
        }
        super.die(source);
    }

    @Nullable
    private EntityType<? extends Mob> getZombieVariantType() {
        if (this instanceof ElitePiglinHunterServant) return ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get();
        if (this instanceof StrongPiglinHunterServant) return ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT.get();
        if (this instanceof PiglinHunterServant) return ModEntityTypes.ZPIGLIN_HUNTER_SERVANT.get();
        if (this instanceof ElitePiglinBruteServant) return ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT.get();
        if (this instanceof StrongPiglinBruteServant) return ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT.get();
        if (this instanceof PiglinBruteServant) return com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_BRUTE_SERVANT.get();
        if (this instanceof PiglinServant) return com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_SERVANT.get();
        if (this instanceof FungusThrower) return ModEntityTypes.ZFUNGUS_THROWER.get();
        return null;
    }

    public AbstractPiglinServant getBreedOffspring(ServerLevel serverLevel, @Nullable AbstractPiglinServant partner) {
        PiglinServant baby = new PiglinServant(
                com.qiuyue.goetyominous.common.init.ModEntityTypes.PIGLIN_SERVANT.get(),
                serverLevel);
        baby.copyTrueOwner(this);
        if (partner != null) {
            if (partner.isGuardingArea()) {
                baby.setBoundPos(partner.getBoundPos());
                baby.setBoundDim(partner.getBoundLevel());
            }
            baby.setWandering(partner.isWandering());
            baby.setStaying(partner.isStaying());
        }
        return baby;
    }

    public static class MakeLove extends Goal {
        private final AbstractPiglinServant piglin;
        protected @Nullable AbstractPiglinServant partner;
        private long loveTime;
        private boolean hasBred;

        public MakeLove(AbstractPiglinServant piglin) {
            this.piglin = piglin;
        }

        public boolean canUse() {
            if (!this.piglin.canBreed()) {
                return false;
            } else {
                this.partner = this.getFreePartner();
                return this.partner != null;
            }
        }

        public boolean canContinueToUse() {
            return this.partner != null && this.partner.isAlive()
                    && this.piglin.level().getGameTime() <= this.loveTime
                    && this.piglin.canBreed() && this.partner.canBreed();
        }

        public void start() {
            this.hasBred = false;
            this.piglin.breedingPartner = this.partner;
            this.partner.breedingPartner = this.piglin;
            int i = 275 + this.piglin.level().getRandom().nextInt(50);
            this.loveTime = this.piglin.level().getGameTime() + (long)i;
        }

        public void stop() {
            if (this.piglin.breedingPartner != null) {
                this.piglin.breedingPartner.breedingPartner = null;
            }
            this.piglin.breedingPartner = null;
            this.partner = null;
            this.loveTime = 0L;
        }

        public void tick() {
            if (this.partner == null) {
                this.stop();
            }
            this.piglin.getLookControl().setLookAt(this.partner, 10.0F, (float)this.piglin.getMaxHeadXRot());
            this.piglin.getNavigation().moveTo(this.partner, 0.5);
            if (this.piglin.level().getGameTime() >= this.loveTime && this.piglin.distanceToSqr(this.partner) <= 5.0) {
                if (!this.hasBred) {
                    this.partner.eatAndDigestFood();
                    this.piglin.eatAndDigestFood();
                }
                this.breed();
            } else if (this.piglin.level().getRandom().nextInt(35) == 0) {
                this.piglin.level().broadcastEntityEvent(this.piglin, (byte)12);
                this.piglin.level().broadcastEntityEvent(this.partner, (byte)12);
            }
        }

        public void breed() {
            this.piglin.breedCool = 6000;
            if (this.partner != null && !this.hasBred) {
                this.partner.breedCool = 6000;
                Level var2 = this.piglin.level();
                if (var2 instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)var2;
                    AbstractPiglinServant baby = this.piglin.getBreedOffspring(serverLevel, this.partner);
                    if (baby != null) {
                        baby.moveTo(this.piglin.getX(), this.piglin.getY(), this.piglin.getZ(), 0.0F, 0.0F);
                        baby.setBaby(true);
                        this.hasBred = true;

                        boolean motherImmune = this.piglin.isImmuneToZombification();
                        boolean fatherImmune = this.partner.isImmuneToZombification();
                        if (motherImmune && fatherImmune) {
                            baby.setImmuneToZombification(true);
                        } else if (motherImmune || fatherImmune) {
                            if (baby.getRandom().nextBoolean()) {
                                baby.setImmuneToZombification(true);
                            }
                        }

                        if (this.piglin.level().addFreshEntity(baby)) {
                            this.piglin.level().broadcastEntityEvent(baby, (byte)12);
                            this.piglin.playSound(net.minecraft.sounds.SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
                            this.piglin.onBreed();
                        }
                    }
                }
            }
        }

        private @Nullable AbstractPiglinServant getFreePartner() {
            List<AbstractPiglinServant> list = this.piglin.level().getEntitiesOfClass(AbstractPiglinServant.class, this.piglin.getBoundingBox().inflate(16.0));
            list.sort(Comparator.comparingDouble(this.piglin::distanceToSqr));
            AbstractPiglinServant piglinServant = null;
            for (AbstractPiglinServant servant : list) {
                if (servant != this.piglin && servant.getTrueOwner() == this.piglin.getTrueOwner()
                        && servant.canBreed() && servant.breedingPartner == null
                        && this.piglin.hasLineOfSight(servant)) {
                    piglinServant = servant;
                }
            }
            return piglinServant;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public void onBreed() {
    }

    public void handleEntityEvent(byte p_35391_) {
        if (p_35391_ == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else {
            super.handleEntityEvent(p_35391_);
        }
    }

    protected void addParticlesAroundSelf(ParticleOptions p_35288_) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(p_35288_, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), d0, d1, d2);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer.getUUID().equals(this.getTrueOwner().getUUID())) {
            if (this.validFood(itemstack) && this.canHaveMoreFood()
                    && this.getInventory().canAddItem(itemstack)) {
                this.getInventory().addItem(itemstack.copyWithCount(1));
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (item instanceof NetherWartPotion && !this.isImmuneToZombification()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                    if (!pPlayer.getInventory().add(bottle)) {
                        pPlayer.drop(bottle, false);
                    }
                }
                this.setImmuneToZombification(true);
                this.playSound(SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1.0D, this.getZ(),
                            7, 0.5D, 0.5D, 0.5D, 0.0D);
                }
                return InteractionResult.SUCCESS;
            }

            if (isNetherwardTinctureItem(item) && !this.isImmuneToZombification()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setImmuneToZombification(true);
                this.playSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, 1.0F);
                this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.CONFUSION, 200, 0));
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1.0D, this.getZ(),
                            7, 0.5D, 0.5D, 0.5D, 0.0D);
                }
                return InteractionResult.SUCCESS;
            }

            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand) && this.canHaveWeapon()) {
                if (this.isAcceptedWeapon(item)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);

                    for(int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
                    }

                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
                }
            }

            if (pPlayer.getMainHandItem().is(com.Polarice3.Goety.common.items.ModItems.WAYSTONE.get())) {
                if (com.Polarice3.Goety.common.items.WaystoneItem.isSameDimension(this, pPlayer.getMainHandItem())) {
                    BlockEntity var18 = com.Polarice3.Goety.common.items.WaystoneItem.getBlockEntity(pPlayer.getMainHandItem(), this.level());
                    if (var18 instanceof ChestBlockEntity chestBlock) {
                        if (chestBlock.canOpen(pPlayer) && !this.level().isClientSide) {
                            BlockPos blockPos = com.Polarice3.Goety.common.items.WaystoneItem.getBlockPos(pPlayer.getMainHandItem());
                            if (blockPos == null) return InteractionResult.FAIL;

                            this.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                            if (this.level() instanceof ServerLevel serverLevel) {
                                for(int j = 0; j < 7; ++j) {
                                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                            this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0),
                                            0, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, 0.5);
                                }
                            }
                            this.setChestPos(blockPos);
                            this.setChestDim(this.level().dimension());
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }

            if (item instanceof ArmorItem && this.canWearArmor()) {
                return ServantUtil.equipServantArmor(this, this, itemstack, super.mobInteract(pPlayer, pHand));
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    protected boolean isAcceptedWeapon(Item item) {
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof CrossbowItem;
    }

    @Override
    public void healServant() {
        super.healServant();
        if (this.level().isClientSide) return;
        if (this.eatenFoodLevel > 0) return;
        if (this.getHealth() >= this.getMaxHealth()) return;
        if (this.countFoodPointsInInventory() <= 0) return;

        SimpleContainer inv = this.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                Integer points = this.getFoodPoints().get(stack.getItem());
                if (points != null) {
                    this.eatenFoodLevel = points;
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    inv.removeItem(i, 1);
                    return;
                }
            }
        }
    }

    private static boolean isNetherwardTinctureItem(net.minecraft.world.item.Item item) {
        try {
            Class<?> clazz = Class.forName(
                    "io.redspace.ironsspellbooks.item.consumables.NetherwardTinctureItem");
            return clazz.isInstance(item);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean canHaveWeapon() {
        return true;
    }

    @Override
    public BlockPos getChestPos() {
        return this.chestPos;
    }

    @Override
    public void setChestPos(BlockPos pos) {
        this.chestPos = pos;
    }

    @Override
    public ResourceKey<Level> getChestLevel() {
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.chestDim));
    }

    @Override
    public String getChestDim() {
        return this.chestDim;
    }

    @Override
    public void setChestDim(String dim) {
        this.chestDim = dim;
    }

    @Override
    public void setChestDim(ResourceKey<Level> dim) {
        this.chestDim = dim.location().toString();
    }

    @Override
    public BlockPos getDumpChestPos() {
        return this.dumpChestPos;
    }

    @Override
    public void setDumpChestPos(BlockPos pos) {
        this.dumpChestPos = pos;
    }

    @Override
    public String getDumpChestDim() {
        return this.dumpChestDim;
    }

    @Override
    public void setDumpChestDim(String dim) {
        this.dumpChestDim = dim;
    }

    @Override
    public void setDumpChestDim(ResourceKey<Level> dim) {
        this.dumpChestDim = dim.location().toString();
    }

    public abstract PiglinArmPose getArmPose();

    @Nullable
    public LivingEntity getTarget() {
        return super.getTarget();
    }

    protected boolean isHoldingMeleeWeapon() {
        return this.getMainHandItem().getItem() instanceof TieredItem;
    }

    public void playAmbientSound() {
        if (this.getTarget() == null && !this.isAggressive()) {
            super.playAmbientSound();
        }
    }

    protected void sendDebugPackets() {
        super.sendDebugPackets();
    }

    protected abstract void playConvertedSound();

    static {
        DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(AbstractPiglinServant.class, EntityDataSerializers.BOOLEAN);
    }
}
