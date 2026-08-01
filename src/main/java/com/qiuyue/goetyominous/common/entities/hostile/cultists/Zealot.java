package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ai.PotionGroupGoal;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

public class Zealot extends AbstractGOCultist implements CrossbowAttackMob, RangedAttackMob, ICultist {

    private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE =
            SynchedEntityData.defineId(Zealot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID =
            SynchedEntityData.defineId(Zealot.class, EntityDataSerializers.INT);

    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/zealot_1.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/zealot_2.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/zealot_3.png"));
        map.put(3, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/zealot_4.png"));
    });

    public Zealot(EntityType<? extends Zealot> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PotionGroupGoal<>(this, 1.25F));
        this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 20, 30.0F));
        this.goalSelector.addGoal(2, new com.Polarice3.Goety.common.entities.ai.CreatureCrossbowAttackGoal<>(this, 1.0D, 24.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZealotHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.ZealotHealth.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING_STATE, false);
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int type) {
        if (type < 0 || type >= TEXTURE_BY_TYPE.size()) {
            type = this.random.nextInt(TEXTURE_BY_TYPE.size());
        }
        this.entityData.set(DATA_TYPE_ID, type);
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

    public boolean isChargingCrossbow() {
        return this.entityData.get(DATA_CHARGING_STATE);
    }

    public void setChargingCrossbow(boolean isCharging) {
        this.entityData.set(DATA_CHARGING_STATE, isCharging);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbowStack, Projectile projectile, float projectileAngle) {
        this.shootCrossbowProjectile(this, target, projectile, projectileAngle, 1.6F);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrow = this.getMobArrow(itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem bowItem) {
            abstractarrow = bowItem.customArrow(abstractarrow);
        }
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }

    protected AbstractArrow getMobArrow(ItemStack arrowStack, float distanceFactor) {
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
        if (abstractarrow instanceof Arrow arrow) {
            for (MobEffectInstance effectInstance : this.getActiveEffects()) {
                if (!effectInstance.getEffect().isBeneficial()) {
                    arrow.addEffect(effectInstance);
                }
            }
        }
        if (this.isOnFire()) {
            abstractarrow.setSecondsOnFire(100);
        }
        return abstractarrow;
    }

    public ItemStack getProjectile(ItemStack shootable) {
        if (shootable.getItem() instanceof ProjectileWeaponItem weapon) {
            Predicate<ItemStack> predicate = weapon.getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon == Items.BOW || weapon == Items.CROSSBOW;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        this.setOutfitType(this.random.nextInt(TEXTURE_BY_TYPE.size()));

        if (worldIn.getRandom().nextInt(100) == 0) {
            CrimsonSpiderServant spider = new CrimsonSpiderServant(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), level());
            if (this.isPersistenceRequired()) spider.setPersistenceRequired();
            spider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            spider.finalizeSpawn(worldIn, difficultyIn, MobSpawnType.JOCKEY, null, null);
            spider.setOwnerId(this.getUUID());
            this.startRiding(spider);
            worldIn.addFreshEntity(spider);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        int random = this.level().random.nextInt(2);
        if (random == 1) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
        this.setDropChance(EquipmentSlot.MAINHAND, 0.025F);

        boolean flag = true;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR && slot != EquipmentSlot.HEAD) {
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
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        if (pSource.getEntity() instanceof Player) {
            float chance = 0.5F;
            int maxCount = 1;
            switch (pLooting) {
                case 1 -> { chance = 0.75F; maxCount = 2; }
                case 2 -> { chance = 0.875F; maxCount = 3; }
                case 3 -> { chance = 0.9167F; maxCount = 4; }
            }
            if (this.random.nextFloat() <= chance) {
                this.spawnAtLocation(new ItemStack(Items.EMERALD, this.random.nextInt(maxCount)));
            }
        }

        this.spawnAtLocation(new ItemStack(Items.ARROW, this.random.nextInt(3) + 1));
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
}
