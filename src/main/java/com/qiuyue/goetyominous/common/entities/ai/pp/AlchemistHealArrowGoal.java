package com.qiuyue.goetyominous.common.entities.ai.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AlchemistHealArrowGoal extends Goal {

    private final PiglinAlchemistServant alchemist;
    @Nullable
    private LivingEntity healTarget;
    private int drawTicks;
    private int nextShotTick;

    public AlchemistHealArrowGoal(PiglinAlchemistServant alchemist) {
        this.alchemist = alchemist;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.alchemist.tickCount < this.nextShotTick) {
            return false;
        }
        if (this.alchemist.getTarget() != null || this.alchemist.isUsingItem()) {
            return false;
        }
        if (!(this.alchemist.getMainHandItem().getItem() instanceof BowItem)) {
            return false;
        }
        if (this.findArrowSlot() < 0) {
            return false;
        }
        this.healTarget = this.findHealTarget();
        return this.healTarget != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.healTarget == null || !this.healTarget.isAlive()) {
            return false;
        }
        if (!this.alchemist.isUsingItem() || this.alchemist.getTarget() != null) {
            return false;
        }
        if (!(this.alchemist.getMainHandItem().getItem() instanceof BowItem)) {
            return false;
        }
        return this.healTarget.getHealth() < this.healTarget.getMaxHealth()
                && this.alchemist.hasLineOfSight(this.healTarget);
    }

    @Override
    public void start() {
        this.drawTicks = 20;
        this.alchemist.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.alchemist, item -> item instanceof BowItem));
    }

    @Override
    public void tick() {
        if (this.healTarget == null) {
            return;
        }
        this.alchemist.getLookControl().setLookAt(this.healTarget, 30.0F, 30.0F);
        this.alchemist.getNavigation().stop();
        if (--this.drawTicks <= 0) {
            this.alchemist.stopUsingItem();
            this.shootHealingArrow();
        }
    }

    @Override
    public void stop() {
        this.alchemist.stopUsingItem();
        this.healTarget = null;
        this.drawTicks = 0;
        this.nextShotTick = this.alchemist.tickCount + 20;
    }

    private void shootHealingArrow() {
        if (this.healTarget == null) {
            return;
        }
        int slot = this.findArrowSlot();
        if (slot < 0) {
            return;
        }
        ItemStack arrow = this.alchemist.beltInventory.get(slot);
        ItemStack shot = arrow.copyWithCount(1);
        this.alchemist.performRangedAttackAt(this.healTarget, 1.5F, shot);
        arrow.shrink(1);
        this.alchemist.beltInventory.set(slot, arrow.isEmpty() ? ItemStack.EMPTY : arrow);
        this.nextShotTick = this.alchemist.tickCount + 20;
    }

    private int findArrowSlot() {
        for (int slot = 0; slot < this.alchemist.beltInventory.size(); slot++) {
            ItemStack stack = this.alchemist.beltInventory.get(slot);
            if (stack.getItem() instanceof TippedArrowItem && PotionUtils.getPotion(stack) == Potions.STRONG_HEALING) {
                return slot;
            }
        }
        return -1;
    }

    @Nullable
    private LivingEntity findHealTarget() {
        LivingEntity owner = this.alchemist.getTrueOwner();
        if (this.isHurtAlly(owner)) {
            return owner;
        }
        for (AbstractPiglinServant servant : this.alchemist.level().getEntitiesOfClass(AbstractPiglinServant.class,
                this.alchemist.getBoundingBox().inflate(15.0D, 6.0D, 15.0D),
                servant -> servant != this.alchemist && this.isHurtAlly(servant))) {
            if (owner != null ? servant.getTrueOwner() == owner : servant.getTrueOwner() == null) {
                return servant;
            }
        }
        return null;
    }

    private boolean isHurtAlly(@Nullable LivingEntity ally) {
        return ally != null && ally.isAlive() && ally != this.alchemist
                && !ally.isInvertedHealAndHarm()
                && ally.getHealth() < ally.getMaxHealth()
                && this.alchemist.hasLineOfSight(ally);
    }
}
