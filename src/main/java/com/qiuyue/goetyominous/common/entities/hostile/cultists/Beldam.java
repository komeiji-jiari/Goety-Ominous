package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.utils.WitchBarterHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.entities.projectile.BurningPotionEntity;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class Beldam extends AbstractGOCultist implements RangedAttackMob, ICultist {

    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5e3d9fa2-5930-4763-8b2e-778024f4a3a0");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING =
            new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM =
            SynchedEntityData.defineId(Beldam.class, EntityDataSerializers.BOOLEAN);

    private int usingTime;
    private int healCooldown;
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableTargetGoal<Player> attackPlayersGoal;

    public Beldam(EntityType<? extends Beldam> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true,
                (raider) -> raider instanceof Raider
                        && ((Raider) raider).getTarget() != null
                        && ((Raider) raider).getTarget().isAlive()
                        && raider.distanceToSqr(((Raider) raider).getTarget()) >= 36.0D
                        && !(raider instanceof Witch)
                        && !(raider instanceof Beldam));

        this.attackPlayersGoal = new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, null) {
            @Override
            public boolean canUse() {
                return Beldam.this.healCooldown <= 0 && super.canUse();
            }
        };
        this.attackPlayersGoal.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.goalSelector.addGoal(1, new BeldamBarterGoal());

        this.goalSelector.addGoal(2, new PotionAttackGoal(this));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_USING_ITEM, false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BeldamHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.WITCH.getDefaultLootTable();
    }

    @Override
    public boolean isBarterable() {
        return true;
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.BeldamHealth.get());
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.WITCH_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) { return SoundEvents.WITCH_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.WITCH_DEATH; }

    @Override
    public SoundEvent getCelebrateSound() { return SoundEvents.WITCH_CELEBRATE; }

    public void setUsingItem(boolean drinking) { this.entityData.set(DATA_USING_ITEM, drinking); }

    public boolean isDrinkingPotion() { return this.entityData.get(DATA_USING_ITEM); }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        if (worldIn.getRandom().nextFloat() < 0.05D) {
            CrimsonSpiderServant spider = new CrimsonSpiderServant(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), level());
            if (this.isPersistenceRequired()) {
                spider.setPersistenceRequired();
            }
            spider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            spider.setOwnerId(this.getUUID());
            spider.finalizeSpawn(worldIn, difficultyIn, MobSpawnType.JOCKEY, null, null);
            this.startRiding(spider);
            worldIn.addFreshEntity(spider);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void die(DamageSource pCause) {
        if (pCause.getEntity() instanceof LivingEntity killer) {
            killer.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 600, 0));
        }
        super.die(pCause);
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive()) {
            if (this.healCooldown > 0) {
                --this.healCooldown;
            }

            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack held = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                    if (held.is(Items.POTION)) {
                        List<MobEffectInstance> effects = PotionUtils.getMobEffects(held);
                        for (MobEffectInstance effect : effects) {
                            this.addEffect(new MobEffectInstance(effect));
                        }
                    } else if (held.is(Items.MILK_BUCKET)) {
                        this.removeAllEffects();
                    }

                    AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (speedAttr != null) {
                        speedAttr.removeModifier(SPEED_MODIFIER_DRINKING);
                    }
                }
            } else {
                Potion potion = this.selectSelfBuffPotion();
                if (potion != null) {
                    this.startDrinking(PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                } else if (this.random.nextFloat() < 0.5F) {
                    this.tryRemoveDebuffs();
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level().broadcastEntityEvent(this, (byte) 15);
            }
        }
        super.aiStep();
    }

    private Potion selectSelfBuffPotion() {
        if (this.getTarget() != null && !(this.getTarget() instanceof Raider)
                && !this.hasEffect(MobEffects.REGENERATION)) {
            return Potions.REGENERATION;
        } else if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER)
                && !this.hasEffect(MobEffects.WATER_BREATHING)) {
            return Potions.WATER_BREATHING;
        } else if (this.random.nextFloat() < 0.15F && (this.isOnFire()
                || (this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE)))
                && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return Potions.FIRE_RESISTANCE;
        } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
            return Potions.HEALING;
        } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null
                && !(this.getTarget() instanceof Raider)
                && !this.hasEffect(MobEffects.INVISIBILITY)) {
            return Potions.INVISIBILITY;
        }
        return null;
    }

    private void startDrinking(ItemStack potionStack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, potionStack);
        this.usingTime = potionStack.getUseDuration();
        this.setUsingItem(true);
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F,
                    0.8F + this.random.nextFloat() * 0.4F);
        }
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_DRINKING);
            speedAttr.addTransientModifier(SPEED_MODIFIER_DRINKING);
        }
    }

    private void tryRemoveDebuffs() {
        for (MobEffectInstance effect : this.getActiveEffects()) {
            if (!effect.getEffect().isBeneficial()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MILK_BUCKET));
                this.usingTime = new ItemStack(Items.MILK_BUCKET).getUseDuration();
                this.setUsingItem(true);
                if (!this.isSilent()) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WANDERING_TRADER_DRINK_MILK, this.getSoundSource(), 1.0F,
                            0.8F + this.random.nextFloat() * 0.4F);
                }
                AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) {
                    speedAttr.removeModifier(SPEED_MODIFIER_DRINKING);
                    speedAttr.addTransientModifier(SPEED_MODIFIER_DRINKING);
                }
                break;
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.isDrinkingPotion()) return;

        Vec3 targetMotion = target.getDeltaMovement();
        double d0 = target.getX() + targetMotion.x - this.getX();
        double d1 = target.getEyeY() - 1.1D - this.getY();
        double d2 = target.getZ() + targetMotion.z - this.getZ();
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));

        if (!(target instanceof Raider) && !target.isOnFire() && !target.fireImmune()
                && this.random.nextFloat() <= 0.05F) {
            BurningPotionEntity potion = new BurningPotionEntity(this.level(), this);
            potion.setXRot(potion.getXRot() - 20.0F);
            potion.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F,
                        0.8F + this.random.nextFloat() * 0.4F);
            }
            this.level().addFreshEntity(potion);
        } else {
            Potion potion = this.selectCombatPotion(target, f);
            ThrownPotion thrown = new ThrownPotion(this.level(), this);
            thrown.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
            thrown.setXRot(thrown.getXRot() - 20.0F);
            thrown.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F,
                        0.8F + this.random.nextFloat() * 0.4F);
            }
            this.level().addFreshEntity(thrown);
        }
    }

    private Potion selectCombatPotion(LivingEntity target, float distance) {
        if (target instanceof Raider raider) {
            if (raider.isOnFire()) return Potions.FIRE_RESISTANCE;
            if (raider.getHealth() <= raider.getMaxHealth() / 2) {
                if (!raider.isInvertedHealAndHarm()) return Potions.HEALING;
            }
            if (!(raider.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
                if (!raider.hasEffect(MobEffects.DAMAGE_BOOST)) return Potions.STRENGTH;
                if (!raider.hasEffect(MobEffects.MOVEMENT_SPEED)) return Potions.SWIFTNESS;
                if (!raider.hasEffect(MobEffects.REGENERATION) && !raider.isInvertedHealAndHarm())
                    return Potions.REGENERATION;
                if (!raider.isInvertedHealAndHarm()) return Potions.HEALING;
            } else {
                if (!raider.hasEffect(MobEffects.WEAKNESS)) return Potions.WEAKNESS;
                if (!raider.hasEffect(MobEffects.REGENERATION) && !raider.isInvertedHealAndHarm())
                    return Potions.REGENERATION;
                if (!raider.isInvertedHealAndHarm()) return Potions.HEALING;
            }
            this.setTarget(null);
            return Potions.HARMING;
        }

        if (target.getMobType() == MobType.UNDEAD) return Potions.HEALING;
        if (distance >= 8.0F && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) return Potions.SLOWNESS;
        if (target.getHealth() >= 8.0F && !target.hasEffect(MobEffects.POISON)
                && target.getMobType() != MobType.UNDEAD) return Potions.POISON;
        if (distance <= 3.0F && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F)
            return Potions.WEAKNESS;

        return Potions.HARMING;
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.getEntity() == this) damage = 0.0F;
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) damage = (float) ((double) damage * 0.15D);
        return damage;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level().addParticle(ParticleTypes.WITCH,
                        this.getX() + this.random.nextGaussian() * 0.13D,
                        this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * 0.13D,
                        this.getZ() + this.random.nextGaussian() * 0.13D,
                        0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.62F;
    }

    class BeldamBarterGoal extends Goal {
        private int progress = 100;

        public BeldamBarterGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean isInterruptable() { return false; }

        @Override
        public boolean canUse() {
            return Beldam.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_CURRENCY);
        }

        @Override
        public void start() {
            this.progress = 100;
            if (!Beldam.this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) Beldam.this.level();
                for (int i = 0; i < 5; ++i) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            Beldam.this.getRandomX(1.0D), Beldam.this.getRandomY() + 1.0D,
                            Beldam.this.getRandomZ(1.0D), 0,
                            Beldam.this.getRandom().nextGaussian() * 0.02D,
                            Beldam.this.getRandom().nextGaussian() * 0.02D,
                            Beldam.this.getRandom().nextGaussian() * 0.02D, 0.5F);
                }
            }
        }

        @Override
        public void tick() {
            Beldam.this.setTarget(null);
            LivingEntity trader = WitchBarterHelper.getTrader(Beldam.this);
            if (--this.progress > 0) {
                Beldam.this.getNavigation().stop();
                if (trader != null && Beldam.this.distanceTo(trader) <= 16.0F) {
                    Beldam.this.getLookControl().setLookAt(trader);
                }
            }
            if (this.progress <= 0) {
                Vec3 vec3 = trader != null ? trader.position() : Beldam.this.position();
                if (!Beldam.this.level().isClientSide() && Beldam.this.level().getServer() != null) {
                    float luck = Beldam.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_BETTER_CURRENCY) ? 1.0F : 0.0F;

                    LootTable loottable = Beldam.this.level().getServer().getLootData().getLootTable(
                            new ResourceLocation(GoetyOminous.MOD_ID, "gameplay/beldam_bartering"));

                    List<ItemStack> list = loottable.getRandomItems(
                            (new LootParams.Builder((ServerLevel) Beldam.this.level()))
                                    .withParameter(LootContextParams.THIS_ENTITY, Beldam.this)
                                    .withParameter(LootContextParams.ORIGIN, Beldam.this.position())
                                    .withLuck(luck)
                                    .create(LootContextParamSets.GIFT));

                    for (ItemStack itemstack : list) {
                        BehaviorUtils.throwItem(Beldam.this, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                    }
                }
                this.clearTrade();
            }

            if (Beldam.this.hurtTime != 0) {
                if (Beldam.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_CURRENCY)
                        || Beldam.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_BETTER_CURRENCY)) {
                    Beldam.this.spawnAtLocation(Beldam.this.getMainHandItem());
                    this.clearTrade();
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }

        private void clearTrade() {
            Beldam.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            WitchBarterHelper.setTimer(Beldam.this, 0);
            this.progress = 100;
        }
    }

    class PotionAttackGoal extends Goal {
        private final Beldam beldam;
        private LivingEntity target;
        private int seeTime;
        private int attackTime = -1;
        private final float attackRadius = 10.0F;
        private final float attackRadiusSqr = attackRadius * attackRadius;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public PotionAttackGoal(Beldam beldam) {
            this.beldam = beldam;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = this.beldam.getTarget();
            if (t != null && t.isAlive()) {
                this.target = t;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() { return canUse() || !this.beldam.getNavigation().isDone(); }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        @Override
        public void tick() {
            double d0 = this.beldam.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean canSee = this.beldam.getSensing().hasLineOfSight(this.target);
            if (canSee) ++this.seeTime;
            else this.seeTime = 0;

            if (!(this.target instanceof Raider)) {
                if ((d0 > (double) this.attackRadiusSqr * 2.0F) && this.seeTime < 20) {
                    this.beldam.getNavigation().moveTo(this.target, 1.0F);
                    this.strafingTime = -1;
                } else {
                    this.beldam.getNavigation().stop();
                    ++this.strafingTime;
                }

                if (this.strafingTime >= 20) {
                    if (this.beldam.getRandom().nextFloat() < 0.3D)
                        this.strafingClockwise = !this.strafingClockwise;
                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    this.strafingBackwards = d0 < (double) (this.attackRadiusSqr * 1.25F);
                    this.beldam.getMoveControl().strafe(this.strafingBackwards ? -0.75F : 0.25F,
                            this.strafingClockwise ? 0.5F : -0.5F);
                    this.beldam.lookAt(this.target, 30.0F, 30.0F);
                } else {
                    this.beldam.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                }
            } else {
                if (d0 > (double) this.attackRadiusSqr && this.seeTime < 5)
                    this.beldam.getNavigation().moveTo(this.target, 1.0F);
                else
                    this.beldam.getNavigation().stop();
                this.beldam.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            }

            float f = Mth.sqrt((float) d0) / this.attackRadius;
            if (--this.attackTime == 0) {
                if (!canSee) return;
                float factor = Mth.clamp(f, 0.1F, 1.0F);
                this.beldam.performRangedAttack(this.target, factor);
                this.attackTime = Mth.floor(f * 60.0F);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(f * 60.0F);
            }
        }
    }
}