package com.qiuyue.goetyominous.common.entities.ally.pp;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.common.entities.ai.pp.TravelerKickGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.TravelerSitGoal;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import com.qiuyue.goetyominous.config.pp.PpConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class ZPiglinTravelerServant extends ZPiglinServant implements CrossbowAttackMob, TravelerAnimatable {

    protected static final EntityDataAccessor<Integer> KICK_TICKS =
            SynchedEntityData.defineId(ZPiglinTravelerServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> KICK_COOLDOWN =
            SynchedEntityData.defineId(ZPiglinTravelerServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(ZPiglinTravelerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(ZPiglinTravelerServant.class, EntityDataSerializers.BOOLEAN);

    private ModMeleeAttackGoal meleeGoal;
    private BackawayCrossbowGoal<ZPiglinTravelerServant> crossbowGoal;

    public ZPiglinTravelerServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ZPiglinServant.setCustomAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(KICK_TICKS, 0);
        this.entityData.define(KICK_COOLDOWN, 0);
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.meleeGoal = new ModMeleeAttackGoal(this, 1.0D, true);
        this.crossbowGoal = new BackawayCrossbowGoal<>(this, 1.0D, 15.0F);
        this.goalSelector.addGoal(2, this.crossbowGoal);
        this.goalSelector.addGoal(2, new TravelerKickGoal<>(this));
        this.goalSelector.addGoal(6, new TravelerSitGoal<>(this));
    }

    @Override
    public void attackGoal() {
    }

    public void reassessWeaponGoal() {
        if (!this.level().isClientSide && this.meleeGoal != null && this.crossbowGoal != null) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.crossbowGoal);
            if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
                this.goalSelector.addGoal(2, this.crossbowGoal);
            } else {
                this.goalSelector.addGoal(2, this.meleeGoal);
            }
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            this.reassessWeaponGoal();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, random.nextFloat() < PpConfig.PiglinTravelerServantCrossbowChance.get()
                ? new ItemStack(Items.CROSSBOW)
                : new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected void customServerAiStep() {
        if (this.getKickCoolDown() > 0) {
            this.setKickCoolDown(this.getKickCoolDown() - 1);
        }
        if (this.getKickTicks() > 0) {
            this.setKickTicks(this.getKickTicks() - 1);
        }
        super.customServerAiStep();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.reassessWeaponGoal();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && !(player.getOffhandItem().getItem() instanceof IWand)
                && stack.getItem() instanceof CrossbowItem) {
            ItemStack previous = this.getMainHandItem();
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
            this.setItemSlot(EquipmentSlot.MAINHAND, stack.copyWithCount(1));
            this.dropEquipment(EquipmentSlot.MAINHAND, previous);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float angle) {
        this.shootCrossbowProjectile(this, target, projectile, angle, 1.6F);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof CrossbowItem;
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, charging);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public PiglinArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return PiglinArmPose.CROSSBOW_CHARGE;
        }
        if (this.isAggressive() && this.getMainHandItem().getItem() instanceof CrossbowItem) {
            return PiglinArmPose.CROSSBOW_HOLD;
        }
        return PiglinArmPose.DEFAULT;
    }

    @Override
    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    public void sit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }

    @Override
    public int getKickTicks() {
        return this.entityData.get(KICK_TICKS);
    }

    @Override
    public void setKickTicks(int kickTicks) {
        this.entityData.set(KICK_TICKS, kickTicks);
    }

    @Override
    public int getKickCoolDown() {
        return this.entityData.get(KICK_COOLDOWN);
    }

    @Override
    public void setKickCoolDown(int kickCoolDown) {
        this.entityData.set(KICK_COOLDOWN, kickCoolDown);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(PpItems.ZPIGLIN_TRAVELER_SERVANT_SPAWN_EGG.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }
}
