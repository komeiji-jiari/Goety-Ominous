package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;

public class Agony extends Summoned {
    private static final UUID SPEED_MODIFIER_HOSTILE_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_HOSTILE;
    private static final EntityDataAccessor<Byte> FLAGS;
    private final AttackGoal meleeGoal = new AttackGoal(this, 1.0, false);
    private int blockTime;
    private int coolTime;
    private int breakShield;

    public Agony(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.reassessWeaponGoal();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GuardingGoal(this, 0.75, 20.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> target instanceof Player player && !CuriosFinder.isWitchFriendly(player)));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal(this, 1.0, 10.0F));
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AgonyHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AgonyDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.AgonyHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.AgonyDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAGS, (byte) 0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BlockTime")) {
            this.blockTime = compound.getInt("BlockTime");
        }
        if (compound.contains("CoolTime")) {
            this.coolTime = compound.getInt("CoolTime");
        }
        this.reassessWeaponGoal();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BlockTime", this.blockTime);
        compound.putInt("CoolTime", this.coolTime);
    }

    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.addGoal(2, this.meleeGoal);
        }
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof net.minecraft.world.entity.raid.Raider
                && !(this.getTrueOwner() instanceof Player)) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
        super.setItemSlot(pSlot, pStack);
        if (!this.level().isClientSide && pSlot.getType() == Type.HAND) {
            this.reassessWeaponGoal();
        }
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), worldIn.getCurrentDifficultyAt(this.blockPosition()));
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        this.reassessWeaponGoal();
        return spawnDataIn;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, ModItems.WICKED_BOLINE.get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultInstance());
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    private boolean getFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i |= mask;
        } else {
            i &= ~mask;
        }
        this.entityData.set(FLAGS, (byte) (i & 255));
    }

    public void setGuarding(boolean guarding) {
        this.setFlags(1, guarding);
    }

    public boolean isGuarding() {
        return this.getFlags(1);
    }

    public int xpReward() {
        return 10;
    }

    public float getVoicePitch() {
        return 0.45F;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return ModSounds.HERETIC_AMBIENT.get();
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        if (this.isDamageSourceBlocked(p_21239_)) {
            return SoundEvents.SHIELD_BLOCK;
        }
        return ModSounds.HERETIC_HURT.get();
    }

    protected void playHurtSound(DamageSource p_21160_) {
        if (this.breakShield <= 0) {
            super.playHurtSound(p_21160_);
        }
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return ModSounds.HERETIC_DEATH.get();
    }

    public void die(DamageSource pCause) {
        super.die(pCause);
        Level var3 = this.level();
        if (var3 instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; ++i) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.TOTEM_EFFECT.get(), this);
            }
        }
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeinstance != null) {
                if ((this.getTarget() != null || this.isAggressive()) && !attributeinstance.hasModifier(SPEED_MODIFIER_HOSTILE)) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_HOSTILE);
                } else if (attributeinstance.hasModifier(SPEED_MODIFIER_HOSTILE)) {
                    attributeinstance.removeModifier(SPEED_MODIFIER_HOSTILE);
                }
            }
        }

        int i;
        if (this.isGuarding()) {
            ++this.blockTime;
            i = MathHelper.secondsToTicks(3 + this.level().random.nextInt(3));
            if (this.blockTime > i) {
                this.coolTime = i * 2;
                this.setGuarding(false);
            }
        } else {
            this.blockTime = 0;
            if (this.coolTime > 0) {
                --this.coolTime;
            }
        }

        if (this.breakShield > 0) {
            --this.breakShield;
        }

        if (this.level().isClientSide) {
            for (i = 0; i < 2; ++i) {
                this.level().addParticle(ModParticleTypes.TOTEM_EFFECT.get(), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean flag = false;
        if (amount > 0.0F && this.isDamageSourceBlocked(source)) {
            ShieldBlockEvent ev = ForgeHooks.onShieldBlock(this, source, amount);
            if (!ev.isCanceled()) {
                if (ev.shieldTakesDamage()) {
                    this.hurtCurrentlyUsedShield(amount);
                }
                amount -= ev.getBlockedDamage();
                if (!source.is(DamageTypeTags.IS_PROJECTILE)) {
                    Entity entity = source.getDirectEntity();
                    if (entity instanceof LivingEntity) {
                        this.blockUsingShield((LivingEntity) entity);
                    }
                }
                flag = true;
            }
        }

        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        } else {
            if (flag) {
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
                this.level().broadcastEntityEvent(this, (byte) 29);
                if (amount <= 1.0F) {
                    return false;
                }
            }

            if (!this.isGuarding() && this.coolTime > 0 && !this.canDisableShield(source) && !source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                this.coolTime -= (int) (amount * 10.0F);
            }

            return super.hurt(source, amount);
        }
    }

    public boolean canDisableShield(DamageSource damageSource) {
        Entity var3 = damageSource.getDirectEntity();
        if (var3 instanceof LivingEntity livingEntity) {
            return livingEntity.getMainHandItem().canDisableShield(this.useItem, this, livingEntity);
        } else {
            return false;
        }
    }

    protected void blockUsingShield(LivingEntity p_36295_) {
        super.blockUsingShield(p_36295_);
        if (p_36295_.getMainHandItem().canDisableShield(this.useItem, this, p_36295_)) {
            this.disableShield(true);
        }
    }

    public void disableShield(boolean p_36385_) {
        float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
        if (p_36385_) {
            f += 0.75F;
        }
        if (this.random.nextFloat() < f) {
            this.coolTime = MathHelper.secondsToTicks(15);
            this.breakShield = 10;
            this.setGuarding(false);
            this.stopUsingItem();
            this.swing(InteractionHand.OFF_HAND);
            this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
            this.level().broadcastEntityEvent(this, (byte) 30);
        }
    }

    protected void hurtCurrentlyUsedShield(float p_36383_) {
        if (this.useItem.canPerformAction(ToolActions.SHIELD_BLOCK) && p_36383_ >= 3.0F) {
            int i = 1 + Mth.floor(p_36383_);
            InteractionHand interactionhand = this.getUsedItemHand();
            this.useItem.hurtAndBreak(i, this, (p_219739_) -> {
                p_219739_.broadcastBreakEvent(interactionhand);
            });
            if (this.useItem.isEmpty()) {
                if (interactionhand == InteractionHand.MAIN_HAND) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.useItem = ItemStack.EMPTY;
                this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }
        }
    }

    public void handleEntityEvent(byte p_20975_) {
        if (p_20975_ == 29) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
        } else if (p_20975_ == 30) {
            this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
            this.breakShield = 10;
        }
        super.handleEntityEvent(p_20975_);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (itemstack.is(ModItems.ECTOPLASM.get()) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.heal(2.0F);
                    Level var5 = this.level();
                    if (var5 instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02;
                            double d1 = this.random.nextGaussian() * 0.02;
                            double d2 = this.random.nextGaussian() * 0.02;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                        }
                    }
                    pPlayer.swing(pHand);
                    return InteractionResult.SUCCESS;
                }

                if (item instanceof SwordItem) {
                    ItemStack currentMain = this.getMainHandItem();
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                    if (!currentMain.isEmpty()) {
                        this.dropEquipment(EquipmentSlot.MAINHAND, currentMain);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
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

                if (item instanceof ShieldItem) {
                    ItemStack currentOff = this.getItemBySlot(EquipmentSlot.OFFHAND);
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.copyWithCount(1));
                    if (!currentOff.isEmpty()) {
                        this.dropEquipment(EquipmentSlot.OFFHAND, currentOff);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
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

                if (item instanceof ArmorItem) {
                    return com.Polarice3.Goety.utils.ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean wantsToPickUp(ItemStack stack) {
        return false;
    }

    static {
        SPEED_MODIFIER_HOSTILE = new AttributeModifier(SPEED_MODIFIER_HOSTILE_UUID, "Aggression Speed", 0.5, Operation.MULTIPLY_BASE);
        FLAGS = SynchedEntityData.defineId(Agony.class, EntityDataSerializers.BYTE);
    }

    public static class AttackGoal extends ModMeleeAttackGoal {
        private final Agony mob;

        public AttackGoal(Agony p_25552_, double speed, boolean needSight) {
            super(p_25552_, speed, needSight);
            this.mob = p_25552_;
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isGuarding();
        }

        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        protected double defaultAttackReachSqr(LivingEntity target) {
            return (double) (this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 2.5F + target.getBbWidth());
        }
    }

    public static class GuardingGoal extends Goal {
        private final Agony mob;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;
        private int ticksUntilNextAttack;

        public GuardingGoal(Agony p_25792_, double speed, float radius) {
            this.mob = p_25792_;
            this.speedModifier = speed;
            this.attackRadiusSqr = radius * radius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return this.mob.getTarget() != null && this.mob.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem && !this.mob.isHolding((itemstack) -> {
                return itemstack.getItem() instanceof ProjectileWeaponItem;
            }) && this.mob.coolTime <= 0;
        }

        public boolean canContinueToUse() {
            return this.canUse() || !this.mob.getNavigation().isDone();
        }

        public void start() {
            super.start();
            this.ticksUntilNextAttack = 0;
            this.mob.startUsingItem(InteractionHand.OFF_HAND);
            this.mob.setGuarding(true);
        }

        public void stop() {
            super.stop();
            this.mob.setGuarding(false);
            this.mob.stopUsingItem();
            this.mob.getNavigation().stop();
            this.mob.setZza(0.0F);
            this.mob.setXxa(0.0F);
            this.seeTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }
                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double) this.mob.getRandom().nextFloat() < 0.3) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }
                    if ((double) this.mob.getRandom().nextFloat() < 0.3) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }
                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }
                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                this.checkAndPerformAttack(livingentity, d0);
            }
        }

        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_) {
            double d0 = this.getAttackReachSqr(p_25557_);
            if (p_25558_ <= d0 && this.ticksUntilNextAttack <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(p_25557_);
            }
        }

        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
        }

        protected double getAttackReachSqr(LivingEntity p_25556_) {
            return (double) (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + p_25556_.getBbWidth());
        }
    }

}
