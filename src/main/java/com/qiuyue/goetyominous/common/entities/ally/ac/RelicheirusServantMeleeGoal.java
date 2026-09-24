package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RelicheirusServantMeleeGoal extends Goal {
    private final RelicheirusServant relicheirus;
    private boolean animationHitDone = false;

    public RelicheirusServantMeleeGoal(RelicheirusServant relicheirus) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.relicheirus = relicheirus;
    }

    private boolean isIdle() {
        return !this.relicheirus.isStaying() && !this.relicheirus.isCommanded()
                && this.relicheirus.getControllingPassenger() == null && !this.relicheirus.isImmobile()
                && !this.relicheirus.isBaby();
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.relicheirus.getTarget();
        return target != null && target.isAlive() && this.isIdle();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.relicheirus.getTarget();
        return super.canContinueToUse() && target != null && target.isAlive() && this.isIdle();
    }

    @Override
    public void tick() {
        LivingEntity target = this.relicheirus.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        Animation anim = this.relicheirus.getAnimation();
        if (anim != RelicheirusServant.ANIMATION_MELEE_SLASH_1 && anim != RelicheirusServant.ANIMATION_MELEE_SLASH_2) {
            this.animationHitDone = false;
        }
        this.relicheirus.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        double dist = this.relicheirus.distanceTo(target);
        this.relicheirus.getNavigation().moveTo(target, 1.0D);
        if (dist < (double) (this.relicheirus.getBbWidth() + target.getBbWidth()) + 1.0D
                && this.relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION
                && this.relicheirus.hasLineOfSight(target)) {
            if (target instanceof TrilocarisEntity) {
                this.relicheirus.setPeckY(target.getBlockY());
                this.relicheirus.syncAnimation(RelicheirusServant.ANIMATION_EAT_TRILOCARIS);
            } else {
                this.relicheirus.syncAnimation(this.relicheirus.getRandom().nextBoolean()
                        ? RelicheirusServant.ANIMATION_MELEE_SLASH_1 : RelicheirusServant.ANIMATION_MELEE_SLASH_2);
            }
        }
        if ((anim == RelicheirusServant.ANIMATION_MELEE_SLASH_1 || anim == RelicheirusServant.ANIMATION_MELEE_SLASH_2)
                && this.relicheirus.getAnimationTick() > 7 && this.relicheirus.getAnimationTick() <= 10) {
            this.checkAndDealDamage(target);
        }
    }

    private void checkAndDealDamage(LivingEntity target) {
        if (!this.animationHitDone && this.relicheirus.hasLineOfSight(target)
                && this.relicheirus.distanceTo(target) < (double) (this.relicheirus.getBbWidth() + target.getBbWidth()) + 2.0D) {
            this.animationHitDone = true;
            this.relicheirus.playSound(ACSoundRegistry.RELICHEIRUS_SCRATCH.get());
            target.hurt(target.damageSources().mobAttack(this.relicheirus), (float) this.relicheirus.getAttributeValue(Attributes.ATTACK_DAMAGE));
            target.knockback(0.5D, this.relicheirus.getX() - target.getX(), this.relicheirus.getZ() - target.getZ());
        }
    }
}
