package com.qiuyue.goetyominus.common.entities.ally.mobs;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominus.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class ZPiglinHunterServant extends ZPiglinServant implements CrossbowAttackMob {

    public ZPiglinHunterServant(EntityType<? extends ZPiglinServant> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal<>(this, 1.0D, 15.0F));
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinHunterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinHunterServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get());
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    protected boolean isAcceptedWeapon(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem;
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof CrossbowItem;
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void attackGoal() {
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float angle) {
        this.shootCrossbowProjectile(this, target, projectile, angle, 1.6F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(ZPiglinHunterServant.class, EntityDataSerializers.BOOLEAN);
    public boolean isChargingCrossbow() { return this.entityData.get(DATA_IS_CHARGING_CROSSBOW); }
    public void setChargingCrossbow(boolean charging) { this.entityData.set(DATA_IS_CHARGING_CROSSBOW, charging); }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return data;
    }

    public PiglinArmPose getArmPose() {
        if (this.isChargingCrossbow()) return PiglinArmPose.CROSSBOW_CHARGE;
        if (this.isAggressive() && this.getMainHandItem().getItem() instanceof CrossbowItem)
            return PiglinArmPose.CROSSBOW_HOLD;
        return PiglinArmPose.DEFAULT;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        if (heldItem instanceof SwordItem || heldItem instanceof AxeItem) {
            return InteractionResult.PASS;
        }

        ItemStack mainHandStack = this.getMainHandItem();

        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && !(player.getOffhandItem().getItem() instanceof IWand)
                && heldItem instanceof CrossbowItem) {
            this.playSound(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
            this.setItemSlot(EquipmentSlot.MAINHAND, heldStack.copyWithCount(1));
            this.dropEquipment(EquipmentSlot.MAINHAND, mainHandStack);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}