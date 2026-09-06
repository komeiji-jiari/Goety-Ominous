package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.magic.spells.SoulHealSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.RandomUtil;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.api.items.magic.IWand;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class InquillagerServant extends SpellcasterIllagerServant {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(InquillagerServant.class,
            EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/inquillager.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/inquillager_2.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/inquillager_3.png"));
    });
    public int coolDown;
    public int healTimes;

    public InquillagerServant(EntityType<? extends InquillagerServant> p_i48556_1_, Level p_i48556_2_) {
        super(p_i48556_1_, p_i48556_2_);
        this.coolDown = 0;
        this.healTimes = 0;
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    public boolean isMainWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof SwordItem || itemStack.is(ItemTags.SWORDS);
    }
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (this.isMainWeapon(itemstack)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }


    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new HealingSelfSpellGoal());
        this.goalSelector.addGoal(2, new ThrowPotionGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.InquillagerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.InquillagerArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.InquillagerDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.InquillagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.InquillagerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.InquillagerDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.coolDown = pCompound.getInt("Cooldown");
        this.healTimes = pCompound.getInt("HealTimes");
        if (pCompound.contains("Outfit")) {
            this.setOutfitType(pCompound.getInt("Outfit"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Cooldown", this.coolDown);
        pCompound.putInt("HealTimes", this.healTimes);
        pCompound.putInt("Outfit", this.getOutfitType());
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }

        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber() {
        return TEXTURE_BY_TYPE.size();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.coolDown > 0) {
            --this.coolDown;
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void increaseHealTimes() {
        ++this.healTimes;
    }

    public void setHealTimes(int healTimes) {
        this.healTimes = healTimes;
    }

    public int getHealTimes() {
        return healTimes;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData ilivingentitydata = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        this.setOutfitType(RandomUtil.nextInt(pLevel.getRandom(), this.OutfitTypeNumber()));
        return ilivingentitydata;
    }

    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            return false;
        } else {
            return super.hurt(source, amount);
        }
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    protected void enchantSpawnedWeapon(RandomSource randomSource, float p_241844_1_) {
        super.enchantSpawnedWeapon(randomSource, p_241844_1_);
        ItemStack itemstack = this.getMainHandItem();
        if (itemstack.getItem() == Items.IRON_SWORD) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.FIRE_ASPECT, 2);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }
    }

    @Override
    public IllagerServantArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return IllagerServantArmPose.SPELLCASTING;
        } else if (this.isAggressive()) {
            return IllagerServantArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
        }
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.INQUILLAGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.INQUILLAGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.INQUILLAGER_HURT.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.INQUILLAGER_CELEBRATE.get();
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (InquillagerServant.this.getTarget() != null) {
                InquillagerServant.this.getLookControl().setLookAt(InquillagerServant.this.getTarget(),
                        (float) InquillagerServant.this.getMaxHeadYRot(),
                        (float) InquillagerServant.this.getMaxHeadXRot());
            }
        }
    }

    class HealingSelfSpellGoal extends SpellcasterUseSpellGoal {
        private HealingSelfSpellGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return InquillagerServant.this.getHealth() < InquillagerServant.this.getMaxHealth() / 2
                        && InquillagerServant.this.getCoolDown() <= 0;
            }
        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 20;
        }

        protected void performSpellCasting() {
            if (InquillagerServant.this.level() instanceof ServerLevel) {
                new SoulHealSpell().mobSpellResult(InquillagerServant.this, ItemStack.EMPTY);
                if (InquillagerServant.this.getHealTimes() > 3) {
                    InquillagerServant.this.setHealTimes(0);
                    InquillagerServant.this.setCoolDown(1000);
                } else {
                    InquillagerServant.this.increaseHealTimes();
                    InquillagerServant.this.setCoolDown(200);
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.SUMMON_VEX;
        }
    }

    static class ThrowPotionGoal extends Goal {
        public int bombTimer;
        public InquillagerServant inquillagerservant;

        public ThrowPotionGoal(InquillagerServant inquillagerservant) {
            this.inquillagerservant = inquillagerservant;
        }

        @Override
        public boolean canUse() {
            if (this.inquillagerservant.getTarget() != null) {
                LivingEntity livingEntity = this.inquillagerservant.getTarget();
                return this.inquillagerservant.distanceTo(livingEntity) > 4.0
                        && this.inquillagerservant.distanceTo(livingEntity) <= 10
                        && this.inquillagerservant.getSensing().hasLineOfSight(livingEntity);
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.inquillagerservant.getTarget() != null && !this.inquillagerservant.getTarget().isDeadOrDying();
        }

        @Override
        public void stop() {
            this.bombTimer = 0;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingEntity = this.inquillagerservant.getTarget();
            if (livingEntity != null) {
                ++this.bombTimer;
                if (this.bombTimer >= 60) {
                    Vec3 vector3d = livingEntity.getDeltaMovement();
                    double d0 = livingEntity.getX() + vector3d.x - this.inquillagerservant.getX();
                    double d1 = livingEntity.getEyeY() - (double) 1.1F - this.inquillagerservant.getY();// Y
                    double d2 = livingEntity.getZ() + vector3d.z - this.inquillagerservant.getZ();
                    float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    Potion potion;
                    if (livingEntity.isInvertedHealAndHarm()) {
                        potion = Potions.HEALING;
                    } else {
                        potion = Potions.HARMING;
                    }
                    ThrownPotion potionentity = new ThrownPotion(this.inquillagerservant.level(),
                            this.inquillagerservant);
                    potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
                    potionentity.setXRot(potionentity.getXRot() - -20.0F);
                    potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
                    if (!this.inquillagerservant.isSilent()) {
                        this.inquillagerservant.level().playSound((Player) null, this.inquillagerservant.getX(),
                                this.inquillagerservant.getY(), this.inquillagerservant.getZ(), SoundEvents.WITCH_THROW,
                                this.inquillagerservant.getSoundSource(), 1.0F,
                                0.8F + this.inquillagerservant.random.nextFloat() * 0.4F);
                    }
                    this.inquillagerservant.level().addFreshEntity(potionentity);
                    this.bombTimer = 0;
                }
            }
        }
    }

    static class AttackGoal extends ModMeleeAttackGoal {
        public AttackGoal(InquillagerServant p_i50577_2_) {
            super(p_i50577_2_, 1.0D, false);
        }
    }
}
