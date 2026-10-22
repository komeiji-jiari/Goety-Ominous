package com.qiuyue.goetyominous.common.entities.ally.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import tallestred.piglinproliferation.client.PPSounds;

public class ZPiglinAlchemistServant extends PiglinAlchemistServant {

    public ZPiglinAlchemistServant(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.setImmune(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PiglinAlchemistServant.createAttributes();
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
    public boolean canBreed() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(PpItems.ZPIGLIN_ALCHEMIST_SERVANT_SPAWN_EGG.get());
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
        if (this.getRandom().nextInt(20) == 0 && this.beltInventory.anyMatch(stack -> stack.getItem() instanceof PotionItem)) {
            this.playSound(PPSounds.ALCHEMIST_WALK.get(),
                    0.5F * (this.beltInventory.countMatches(stack -> stack.getItem() instanceof PotionItem) * 0.5F), 1.0F);
        }
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean canBarter() {
        return false;
    }
}
