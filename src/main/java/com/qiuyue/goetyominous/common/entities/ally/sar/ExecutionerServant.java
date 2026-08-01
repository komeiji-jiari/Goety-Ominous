package com.qiuyue.goetyominous.common.entities.ally.sar;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.teamabnormals.savage_and_ravage.common.item.CleaverOfBeheadingItem;
import com.teamabnormals.savage_and_ravage.core.registry.SRItems;
import com.teamabnormals.savage_and_ravage.core.registry.SRSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class ExecutionerServant extends AbstractIllagerServant {

    public ExecutionerServant(EntityType<? extends Owned> entity, Level world) {
        super(entity, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ExecutionerMeleeAttackGoal(this, 1.0D));
    }

    @Override
    public boolean canOpenDoors() {
        return true;
    }

    @Override
    public IllagerServantArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerServantArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ExecutionerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ExecutionerServantFollowRange.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.ExecutionerServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ExecutionerServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.ExecutionerServantArmor.get());
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(SRItems.CLEAVER_OF_BEHEADING.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        RandomSource randomsource = worldIn.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficultyIn);
        this.populateDefaultEquipmentEnchantments(randomsource, difficultyIn);
        return data;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(com.qiuyue.goetyominous.common.items.sar.SarItems.EXECUTIONER_SERVANT_SPAWN_EGG.get());
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SRSoundEvents.EXECUTIONER_CELEBRATE.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SRSoundEvents.EXECUTIONER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SRSoundEvents.EXECUTIONER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SRSoundEvents.EXECUTIONER_HURT.get();
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item instanceof AxeItem || item instanceof SwordItem || item instanceof CleaverOfBeheadingItem || itemstack.is(ItemTags.AXES) || itemstack.is(ItemTags.SWORDS)) {
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
        return super.mobInteract(pPlayer, pHand);
    }


    static class ExecutionerMeleeAttackGoal extends ModMeleeAttackGoal {
        private final ExecutionerServant executioner;

        public ExecutionerMeleeAttackGoal(ExecutionerServant p_34123_, double speedModifier) {
            super(p_34123_, speedModifier, false);
            this.executioner = p_34123_;
        }

        @Override
        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.getAttackInterval();
        }

        @Override
        protected int getAttackInterval() {
            return 50;
        }

        @Override
        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            float f = this.mob.getBbWidth() - 0.1F;
            return f * 2.0F * f * 2.0F + p_179512_1_.getBbWidth();
        }
    }
}



