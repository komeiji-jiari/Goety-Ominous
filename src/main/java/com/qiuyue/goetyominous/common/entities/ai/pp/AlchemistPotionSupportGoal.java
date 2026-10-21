package com.qiuyue.goetyominous.common.entities.ai.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import tallestred.piglinproliferation.client.PPSounds;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AlchemistPotionSupportGoal extends Goal {

    private final PiglinAlchemistServant alchemist;
    @Nullable
    private SupportAction pendingAction;
    @Nullable
    private LivingEntity potionTarget;
    private boolean selfTarget;
    private ItemStack heldPotion = ItemStack.EMPTY;
    private int throwTicks;
    private int nextThrowTick;

    public AlchemistPotionSupportGoal(PiglinAlchemistServant alchemist) {
        this.alchemist = alchemist;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.alchemist.tickCount < this.nextThrowTick) {
            return false;
        }
        if (this.alchemist.isGonnaThrowPotion() || this.alchemist.isUsingItem()) {
            return false;
        }
        this.pendingAction = this.findAction();
        return this.pendingAction != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.throwTicks <= 0 || this.heldPotion.isEmpty()) {
            return false;
        }
        if (this.selfTarget) {
            return this.alchemist.isAlive();
        }
        return this.potionTarget != null && this.potionTarget.isAlive()
                && this.alchemist.hasLineOfSight(this.potionTarget);
    }

    @Override
    public void start() {
        SupportAction action = this.pendingAction;
        this.pendingAction = null;
        if (action == null) {
            return;
        }
        int slot = this.findPotionSlot(action.potion());
        if (slot < 0) {
            return;
        }
        ItemStack potion = this.alchemist.beltInventory.get(slot).copy();
        this.alchemist.beltInventory.set(slot, ItemStack.EMPTY);
        this.heldPotion = potion;
        this.alchemist.setItemShownOnOffhand(potion);
        this.alchemist.willThrowPotion(true);
        this.potionTarget = action.target();
        this.selfTarget = action.self();
        this.throwTicks = this.selfTarget ? 15 : 20;
        this.alchemist.getNavigation().stop();
        this.alchemist.swing(InteractionHand.OFF_HAND);
    }

    @Override
    public void tick() {
        if (this.heldPotion.isEmpty()) {
            return;
        }
        if (--this.throwTicks == 5) {
            this.alchemist.playSound(PPSounds.ALCHEMIST_ABOUT_TO_THROW_POTION.get(), 1.0F, 1.0F);
        }
        if (this.selfTarget) {
            this.checkPanic();
            if (this.throwTicks <= 0) {
                this.throwPotionAtSelf();
            }
            return;
        }
        if (this.potionTarget == null) {
            this.throwTicks = 0;
            return;
        }
        this.alchemist.getLookControl().setLookAt(this.potionTarget, 30.0F, 30.0F);
        double distance = this.alchemist.distanceToSqr(this.potionTarget);
        if (distance > 25.0D) {
            this.alchemist.getNavigation().moveTo(this.potionTarget, 1.0D);
        } else {
            this.alchemist.getNavigation().stop();
        }
        if (this.throwTicks <= 0) {
            if (distance > 100.0D) {
                this.throwTicks = 5;
                return;
            }
            this.throwPotionAtTarget();
        }
    }

    @Override
    public void stop() {
        if (!this.heldPotion.isEmpty()) {
            this.returnPotionToBelt();
        }
        this.heldPotion = ItemStack.EMPTY;
        this.alchemist.willThrowPotion(false);
        this.alchemist.setItemShownOnOffhand(ItemStack.EMPTY);
        this.alchemist.getNavigation().stop();
        this.potionTarget = null;
        this.throwTicks = 0;
        this.nextThrowTick = this.alchemist.tickCount + 20;
    }

    private void throwPotionAtSelf() {
        this.alchemist.setYHeadRot(-200.0F);
        this.alchemist.setXRot(90.0F);
        this.alchemist.throwPotion(this.heldPotion, this.alchemist.getXRot(), this.alchemist.getYHeadRot());
        this.finishThrow();
    }

    private void throwPotionAtTarget() {
        if (this.potionTarget == null) {
            return;
        }
        double dX = this.potionTarget.getX() - this.alchemist.getX();
        double dZ = this.potionTarget.getZ() - this.alchemist.getZ();
        double horizontal = Math.sqrt(dX * dX + dZ * dZ);
        float yRot = (float) (Mth.atan2(dZ, dX) * (double) (180.0F / (float) Math.PI)) - 90.0F;
        float xRot = (float) (-(Mth.atan2(this.potionTarget.getY(0.5D) - this.alchemist.getEyeY(), horizontal) * (double) (180.0F / (float) Math.PI)));
        this.alchemist.setYHeadRot(yRot);
        this.alchemist.setYRot(yRot);
        this.alchemist.setXRot(xRot);
        this.alchemist.throwPotion(this.heldPotion, xRot, yRot);
        this.finishThrow();
    }

    private void finishThrow() {
        this.heldPotion = ItemStack.EMPTY;
        this.alchemist.setItemShownOnOffhand(ItemStack.EMPTY);
        this.throwTicks = 0;
        this.nextThrowTick = this.alchemist.tickCount + 40;
    }

    private void checkPanic() {
        LivingEntity threat = this.alchemist.getLastHurtByMob();
        if (threat != null && threat.isAlive() && this.alchemist.distanceToSqr(threat) < 16.0D) {
            this.throwTicks = Math.min(20, this.throwTicks + 2);
            Vec3 pos = LandRandomPos.getPosAway(this.alchemist, 5, 7, threat.position());
            if (pos != null && this.alchemist.getNavigation().isDone()) {
                this.alchemist.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.2D);
            }
        }
    }

    private void returnPotionToBelt() {
        for (int slot = 0; slot < this.alchemist.beltInventory.size(); slot++) {
            if (this.alchemist.beltInventory.get(slot).isEmpty()) {
                this.alchemist.beltInventory.set(slot, this.heldPotion);
                return;
            }
        }
        this.alchemist.spawnAtLocation(this.heldPotion);
    }

    @Nullable
    private SupportAction findAction() {
        if (this.alchemist.isOnFire() && this.canApply(this.alchemist, Potions.FIRE_RESISTANCE)) {
            return new SupportAction(this.alchemist, Potions.FIRE_RESISTANCE, true);
        }
        if (this.alchemist.getHealth() < this.alchemist.getMaxHealth()) {
            if (this.canApply(this.alchemist, Potions.STRONG_REGENERATION)) {
                return new SupportAction(this.alchemist, Potions.STRONG_REGENERATION, true);
            }
            if (!this.hasPotion(Potions.STRONG_REGENERATION) && this.canApply(this.alchemist, Potions.STRONG_HEALING)) {
                return new SupportAction(this.alchemist, Potions.STRONG_HEALING, true);
            }
        }
        List<LivingEntity> allies = this.getAllies();
        for (LivingEntity ally : allies) {
            if (ally.isOnFire() && this.canApply(ally, Potions.FIRE_RESISTANCE)) {
                return new SupportAction(ally, Potions.FIRE_RESISTANCE, false);
            }
        }
        for (LivingEntity ally : allies) {
            if (ally.getHealth() < ally.getMaxHealth() && this.canApply(ally, Potions.STRONG_REGENERATION)) {
                return new SupportAction(ally, Potions.STRONG_REGENERATION, false);
            }
        }
        for (LivingEntity ally : allies) {
            if (ally.getHealth() < ally.getMaxHealth() && !this.hasPotion(Potions.STRONG_REGENERATION)
                    && this.canApply(ally, Potions.STRONG_HEALING)) {
                return new SupportAction(ally, Potions.STRONG_HEALING, false);
            }
        }
        for (LivingEntity ally : allies) {
            if (ally instanceof Mob mob && mob.getTarget() != null
                    && ally.getHealth() < ally.getMaxHealth() * 0.5F
                    && !ally.isHolding(item -> item.getItem() instanceof ProjectileWeaponItem)
                    && this.canApply(ally, Potions.STRONG_STRENGTH)) {
                return new SupportAction(ally, Potions.STRONG_STRENGTH, false);
            }
        }
        return null;
    }

    private List<LivingEntity> getAllies() {
        List<LivingEntity> allies = new ArrayList<>();
        LivingEntity owner = this.alchemist.getTrueOwner();
        if (owner != null && owner.isAlive() && owner != this.alchemist) {
            allies.add(owner);
        }
        for (AbstractPiglinServant servant : this.alchemist.level().getEntitiesOfClass(AbstractPiglinServant.class,
                this.alchemist.getBoundingBox().inflate(10.0D, 4.0D, 10.0D),
                servant -> servant != this.alchemist && servant.isAlive())) {
            if (owner != null ? servant.getTrueOwner() == owner : servant.getTrueOwner() == null) {
                allies.add(servant);
            }
        }
        return allies;
    }

    private boolean canApply(LivingEntity target, Potion potion) {
        if (!this.hasPotion(potion) || this.hasEffects(target, potion)) {
            return false;
        }
        if (target.isInvertedHealAndHarm()) {
            for (MobEffectInstance effect : potion.getEffects()) {
                if (effect.getEffect() == MobEffects.HEAL) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasEffects(LivingEntity target, Potion potion) {
        for (MobEffectInstance effect : potion.getEffects()) {
            if (target.hasEffect(effect.getEffect())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPotion(Potion potion) {
        return this.findPotionSlot(potion) >= 0;
    }

    private int findPotionSlot(Potion potion) {
        for (int slot = 0; slot < this.alchemist.beltInventory.size(); slot++) {
            ItemStack stack = this.alchemist.beltInventory.get(slot);
            if (stack.is(Items.SPLASH_POTION) && PotionUtils.getPotion(stack) == potion) {
                return slot;
            }
        }
        return -1;
    }

    private record SupportAction(LivingEntity target, Potion potion, boolean self) {
    }
}
