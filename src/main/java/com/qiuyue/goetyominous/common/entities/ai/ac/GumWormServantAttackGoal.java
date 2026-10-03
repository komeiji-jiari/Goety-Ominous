package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormServant;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormServantAttackGoal extends Goal {

    private final GumWormServant entity;
    private boolean leapingAttack;
    private int leapTicks;
    private int maxLeapTicks;
    private float leapHeight;

    public GumWormServantAttackGoal(GumWormServant worm) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null && target.isAlive() && this.entity.timeBetweenAttacks <= 0 && !this.entity.isRidingMode();
    }

    @Override
    public void stop() {
        this.entity.setBiting(false);
        this.entity.setLeaping(false);
        this.leapTicks = 0;
    }

    @Override
    public void start() {
        this.leapTicks = 0;
        this.leapHeight = 0.2F + this.entity.getRandom().nextFloat() * 0.2F;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return;
        }
        double dist = this.entity.distanceTo(target);
        if (dist > 13.0D && !this.leapingAttack && this.entity.leapAttackCooldown >= 0 && (this.entity.isInWall() || this.entity.onGround()) && this.leapTicks == 0) {
            this.leapingAttack = true;
            this.maxLeapTicks = 15 + this.entity.getRandom().nextInt(8);
            this.leapTicks = 0;
        }
        if (this.leapingAttack) {
            this.entity.getNavigation().stop();
            if ((this.entity.isInWall() || this.entity.onGround()) && !this.entity.isLeaping()) {
                this.entity.setLeaping(true);
                this.entity.leapAttackCooldown = 330;
            }
            if (this.entity.isLeaping()) {
                Vec3 leapOnPos = target.position().subtract(this.entity.position());
                float f = -((float) Mth.atan2(leapOnPos.x, leapOnPos.z)) * 180.0F / (float) Math.PI;
                this.entity.setYRot(f);
                this.entity.setTargetDigPitch(-(float) (Mth.atan2(leapOnPos.y, leapOnPos.horizontalDistance()) * 57.2957763671875D));
                if (this.leapTicks <= this.maxLeapTicks) {
                    ++this.leapTicks;
                    float leapUp = (1.0F - (float) this.leapTicks / (float) this.maxLeapTicks) * 8.0F * this.leapHeight;
                    Vec3 delta = leapOnPos.scale(0.1D);
                    this.entity.setDeltaMovement(this.entity.getDeltaMovement().scale(0.1D).add(delta.add(0.0D, leapUp, 0.0D)));
                } else {
                    this.leapingAttack = false;
                    this.leapTicks = 0;
                    this.entity.leapAttackCooldown = 300;
                }
            }
        } else {
            this.entity.getNavigation().moveTo(target, 1.0D);
            this.entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if (this.entity.isLeaping()) {
                this.entity.setLeaping(false);
            }
        }
        if (this.entity.isMouthOpen() && this.entity.attackAllAroundMouth((float) this.entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 2.0F)) {
            this.entity.timeBetweenAttacks = this.leapingAttack ? 100 : 20;
            this.entity.leapAttackCooldown = 300;
            this.leapingAttack = false;
            this.entity.attemptPlayAttackNoise();
        }
        this.entity.setBiting(dist < (double) (15.0F + target.getBbWidth()));
    }
}
