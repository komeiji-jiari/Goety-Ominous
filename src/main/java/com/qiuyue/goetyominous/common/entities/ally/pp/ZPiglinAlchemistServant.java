package com.qiuyue.goetyominous.common.entities.ally.pp;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.common.entities.ai.pp.AlchemistBowAttackGoal;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class ZPiglinAlchemistServant extends ZPiglinServant implements AlchemistBeltHolder {

    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<ItemStack>[] BELT_SLOTS = defineBeltInventory(6);
    private final BeltInventory beltInventory = new BeltInventory();
    private int arrowsShot;

    public ZPiglinAlchemistServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("unchecked")
    private static EntityDataAccessor<ItemStack>[] defineBeltInventory(int capacity) {
        EntityDataAccessor<ItemStack>[] slots = new EntityDataAccessor[capacity];
        for (int i = 0; i < capacity; i++) {
            slots[i] = SynchedEntityData.defineId(ZPiglinAlchemistServant.class, EntityDataSerializers.ITEM_STACK);
        }
        return slots;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        for (EntityDataAccessor<ItemStack> accessor : BELT_SLOTS) {
            this.entityData.define(accessor, ItemStack.EMPTY);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ZPiglinServant.setCustomAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AlchemistBowAttackGoal<>(this));
    }

    @Override
    public void attackGoal() {
    }

    @Override
    public void populateDefaultWeapons(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public ItemStack getProjectile(ItemStack shootable) {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack ammo = this.getProjectile(this.getItemInHand(
                ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, ammo, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem bowItem) {
            arrow = bowItem.customArrow(arrow);
        }
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
        ++this.arrowsShot;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && !(player.getOffhandItem().getItem() instanceof IWand)
                && stack.getItem() instanceof BowItem) {
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
    public int getBeltSize() {
        return this.beltInventory.size();
    }

    @Override
    public ItemStack getBeltItem(int slot) {
        return this.beltInventory.get(slot);
    }

    public void setBeltItem(int slot, ItemStack stack) {
        this.beltInventory.set(slot, stack);
    }

    @Override
    public ItemStack getItemShownOnOffhand() {
        return ItemStack.EMPTY;
    }

    @Override
    public int getArrowsShot() {
        return this.arrowsShot;
    }

    @Override
    public void setArrowsShot(int arrowsShot) {
        this.arrowsShot = arrowsShot;
    }

    @Override
    public boolean isGonnaThrowPotion() {
        return false;
    }

    @Override
    public PiglinArmPose getArmPose() {
        return PiglinArmPose.DEFAULT;
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
        return new ItemStack(PpItems.ZPIGLIN_ALCHEMIST_SERVANT_SPAWN_EGG.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag belt = new CompoundTag();
        NonNullList<ItemStack> items = this.beltInventory.values();
        for (int i = 0; i < items.size(); ++i) {
            if (!items.get(i).isEmpty()) {
                belt.put("Slot" + i, items.get(i).save(new CompoundTag()));
            }
        }
        tag.put("BeltItems", belt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag belt = tag.getCompound("BeltItems");
        for (int i = 0; i < this.beltInventory.size(); ++i) {
            this.beltInventory.set(i, belt.contains("Slot" + i)
                    ? ItemStack.of(belt.getCompound("Slot" + i)) : ItemStack.EMPTY);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        for (int i = 0; i < this.beltInventory.size(); i++) {
            ItemStack stack = this.beltInventory.get(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack.copy());
            }
        }
        this.beltInventory.clear();
        super.dropCustomDeathLoot(source, looting, recentlyHit);
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
    }
}
