package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ai.PotionGroupGoal;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.entities.projectile.PitchforkEntity;
import com.qiuyue.goetyominous.common.entities.projectile.WitchBombEntity;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

public class Fanatic extends AbstractGOCultist implements RangedAttackMob, ICultist {

    private static final EntityDataAccessor<Integer> DATA_TYPE_ID =
            SynchedEntityData.defineId(Fanatic.class, EntityDataSerializers.INT);

    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_11.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_1.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_2.png"));
        map.put(3, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_3.png"));
        map.put(4, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_4.png"));
        map.put(5, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_5.png"));
        map.put(6, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_6.png"));
        map.put(7, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_7.png"));
        map.put(8, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_8.png"));
        map.put(9, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_9.png"));
        map.put(10, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/fanatic/fanatic_10.png"));
    });

    public Fanatic(EntityType<? extends Fanatic> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 10;
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(5));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PotionGroupGoal<>(this, 1.25F));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(2, new PitchforkAttackGoal(this, 1.0D, 120, 10.0F));
        this.goalSelector.addGoal(2, new ThrowBombsGoal(this));
        this.goalSelector.addGoal(2, new ThrowPearlGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FanaticHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FanaticDamage.get())
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.FanaticHealth.get());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.FanaticDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 1);
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.getOutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.getOutfitTypeNumber());
        }
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int getOutfitTypeNumber() {
        return TEXTURE_BY_TYPE.size();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Outfit", this.getOutfitType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setOutfitType(pCompound.getInt("Outfit"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (this.random.nextInt(6)) {
            case 0 -> ModSounds.FANATIC_AMBIENT_1.get();
            case 1 -> ModSounds.FANATIC_AMBIENT_2.get();
            case 2 -> ModSounds.FANATIC_AMBIENT_3.get();
            case 3 -> ModSounds.FANATIC_AMBIENT_4.get();
            case 4 -> ModSounds.FANATIC_AMBIENT_5.get();
            case 5 -> ModSounds.FANATIC_AMBIENT_6.get();
            default -> ModSounds.FANATIC_AMBIENT_1.get();
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return switch (this.random.nextInt(5)) {
            case 0 -> ModSounds.FANATIC_HURT_1.get();
            case 1 -> ModSounds.FANATIC_HURT_2.get();
            case 2 -> ModSounds.FANATIC_HURT_3.get();
            case 3 -> ModSounds.FANATIC_HURT_4.get();
            case 4 -> ModSounds.FANATIC_HURT_5.get();
            default -> ModSounds.FANATIC_HURT_1.get();
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (this.random.nextInt(4)) {
            case 0 -> ModSounds.FANATIC_DEATH_1.get();
            case 1 -> ModSounds.FANATIC_DEATH_2.get();
            case 2 -> ModSounds.FANATIC_DEATH_3.get();
            case 3 -> ModSounds.FANATIC_DEATH_4.get();
            default -> ModSounds.FANATIC_DEATH_1.get();
        };
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return switch (this.random.nextInt(4)) {
            case 0 -> ModSounds.FANATIC_CELEBRATE_1.get();
            case 1 -> ModSounds.FANATIC_CELEBRATE_2.get();
            case 2 -> ModSounds.FANATIC_CELEBRATE_3.get();
            case 3 -> ModSounds.FANATIC_CELEBRATE_4.get();
            default -> ModSounds.FANATIC_CELEBRATE_1.get();
        };
    }

    @Override
    public boolean isBarterable() {
        return false;
    }

    public boolean hasBomb() {
        return this.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.WITCH_BOMB.get());
    }

    public boolean hasPearl() {
        return this.getItemInHand(InteractionHand.OFF_HAND).is(Items.ENDER_PEARL);
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        PitchforkEntity pitchforkEntity = new PitchforkEntity(this.level(), this, new ItemStack(ModItems.PITCHFORK.get()));
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - pitchforkEntity.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        pitchforkEntity.shoot(d0, d1 + d3 * 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(pitchforkEntity);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (worldIn.getRandom().nextInt(100) == 0) {
            CrimsonSpiderServant spider = new CrimsonSpiderServant(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), level());
            if (this.isPersistenceRequired()) spider.setPersistenceRequired();
            spider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            spider.finalizeSpawn(worldIn, difficultyIn, MobSpawnType.JOCKEY, null, null);
            spider.setOwnerId(this.getUUID());
            this.startRiding(spider);
            worldIn.addFreshEntity(spider);
        }
        this.setOutfitType(this.random.nextInt(this.getOutfitTypeNumber()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        int random = this.random.nextInt(12);
        int witchbomb = this.random.nextInt(8);
        int pitchfork = this.random.nextInt(6);

        if (pitchfork != 0) {
            switch (random) {
                case 0 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                case 1 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                case 2 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_PICKAXE));
                case 3 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SHOVEL));
                case 4 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                case 5 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
                case 6 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
                case 7 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
                case 8 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                case 9 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
                case 10 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_PICKAXE));
                case 11 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SHOVEL));
            }
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PITCHFORK.get()));
        }
        this.setDropChance(EquipmentSlot.MAINHAND, 0.025F);

        boolean flag = true;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = this.getItemBySlot(slot);
                if (!flag && this.random.nextFloat() < 0.5F) break;
                flag = false;
                if (stack.isEmpty()) {
                    int i = this.random.nextInt(8);
                    if (i == 4) --i;
                    Item item = getEquipmentForSlot(slot, i);
                    if (item != null) {
                        this.setItemSlot(slot, new ItemStack(item));
                        this.setDropChance(slot, 0.025F);
                    }
                }
            }
        }

        if (this.getMainHandItem().is(ModItems.PITCHFORK.get())) {
            return;
        }

        if (witchbomb == 0) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.WITCH_BOMB.get()));
        } else if (this.level().random.nextFloat() <= 0.25F) {
            int offhandChoice = this.level().random.nextInt(3);
            if (offhandChoice == 0) {
                Item torch = this.level().random.nextBoolean()
                        ? com.Polarice3.Goety.common.blocks.ModBlocks.IRON_DUNGEON_TORCH_ITEM.get()
                        : com.Polarice3.Goety.common.blocks.ModBlocks.GOLD_DUNGEON_TORCH_ITEM.get();
                this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(torch));
                this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
            } else if (offhandChoice == 1) {
                this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.ENDER_PEARL));
                this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.getMainHandItem().is(ModItems.PITCHFORK.get())) {
            for (int i = 0; i < this.random.nextInt(3) + (pLooting > 0 ? this.random.nextInt(pLooting) : 0); ++i) {
                this.spawnAtLocation(Items.WHEAT);
            }
        }
    }

    static class PitchforkAttackGoal extends RangedAttackGoal {
        private final Fanatic fanatic;

        public PitchforkAttackGoal(Fanatic fanatic, double speed, int interval, float range) {
            super(fanatic, speed, interval, range);
            this.fanatic = fanatic;
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                    && this.fanatic.getTarget() != null
                    && this.fanatic.getMainHandItem().is(ModItems.PITCHFORK.get());
        }

        @Override
        public void start() {
            super.start();
            this.fanatic.setAggressive(true);
            this.fanatic.startUsingItem(InteractionHand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.fanatic.stopUsingItem();
            this.fanatic.setAggressive(false);
        }
    }

    static class ThrowBombsGoal extends Goal {
        public int bombTimer;
        public Fanatic fanatic;

        public ThrowBombsGoal(Fanatic fanatic) {
            this.fanatic = fanatic;
        }

        @Override
        public boolean canUse() {
            if (this.fanatic.getTarget() != null && this.fanatic.hasBomb()) {
                LivingEntity target = this.fanatic.getTarget();
                return this.fanatic.distanceTo(target) > 2.0F
                        && this.fanatic.distanceTo(target) <= 10.0F
                        && this.fanatic.getSensing().hasLineOfSight(target);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.fanatic.getTarget() != null
                    && !this.fanatic.getTarget().isDeadOrDying()
                    && this.fanatic.hasBomb();
        }

        @Override
        public void stop() {
            this.bombTimer = 0;
        }

        @Override
        public void tick() {
            super.tick();
            ++this.bombTimer;
            if (this.bombTimer >= 60) {
                LivingEntity target = this.fanatic.getTarget();
                if (target != null) {
                    WitchBombEntity bomb = new WitchBombEntity(this.fanatic.level(), this.fanatic);
                    ItemStack bombStack = new ItemStack(ModItems.WITCH_BOMB.get());
                    if (this.fanatic.getRandom().nextFloat() <= 0.3F) {
                        Potion potion = switch (this.fanatic.getRandom().nextInt(3)) {
                            case 0 -> Potions.POISON;
                            case 1 -> Potions.WEAKNESS;
                            case 2 -> Potions.SLOWNESS;
                            default -> Potions.POISON;
                        };
                        ItemStack splashPotion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
                        bombStack.setTag(splashPotion.getTag());
                    }
                    bomb.setItem(bombStack);
                    Vec3 motion = target.getDeltaMovement();
                    double d0 = target.getX() + motion.x - this.fanatic.getX();
                    double d1 = target.getEyeY() - 1.1F - this.fanatic.getY();
                    double d2 = target.getZ() + motion.z - this.fanatic.getZ();
                    float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    bomb.shoot(d0, d1 + f * 0.2F, d2, 0.75F, 2.0F);
                    this.fanatic.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F / (this.fanatic.getRandom().nextFloat() * 0.4F + 0.8F));
                    this.fanatic.level().addFreshEntity(bomb);
                    this.bombTimer = 0;
                }
            }
        }
    }

    static class ThrowPearlGoal extends Goal {
        public int pearlTimer;
        public Fanatic fanatic;

        public ThrowPearlGoal(Fanatic fanatic) {
            this.fanatic = fanatic;
        }

        @Override
        public boolean canUse() {
            return this.fanatic.getTarget() != null
                    && this.fanatic.hasPearl()
                    && this.fanatic.distanceTo(this.fanatic.getTarget()) >= 12.0F
                    && this.fanatic.getSensing().hasLineOfSight(this.fanatic.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return this.fanatic.getTarget() != null
                    && !this.fanatic.getTarget().isDeadOrDying()
                    && this.fanatic.hasPearl();
        }

        @Override
        public void stop() {
            this.pearlTimer = 0;
            this.fanatic.setAggressive(false);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.pearlTimer;
            LivingEntity target = this.fanatic.getTarget();
            if (target != null) {
                if (this.pearlTimer >= 40) {
                    ThrownEnderpearl pearl = new ThrownEnderpearl(this.fanatic.level(), this.fanatic);
                    pearl.shootFromRotation(this.fanatic, this.fanatic.getXRot(), this.fanatic.getYRot(), 0.0F, 1.5F, 1.0F);
                    this.fanatic.playSound(SoundEvents.ENDER_PEARL_THROW, 1.0F, 0.4F / (this.fanatic.getRandom().nextFloat() * 0.4F + 0.8F));
                    this.fanatic.level().addFreshEntity(pearl);
                    this.fanatic.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                    this.pearlTimer = 0;
                } else {
                    double d1 = target.getX() - this.fanatic.getX();
                    double d2 = target.getZ() - this.fanatic.getZ();
                    this.fanatic.getNavigation().stop();
                    this.fanatic.setAggressive(true);
                    this.fanatic.lookAt(target, 30.0F, 30.0F);
                }
            }
        }
    }
}


