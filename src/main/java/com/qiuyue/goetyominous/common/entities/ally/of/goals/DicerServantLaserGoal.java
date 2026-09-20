package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class DicerServantLaserGoal extends RamblerServantAttackGoal {
    private final DicerServant dicer;
    private DicerServantLaser beam;

    public DicerServantLaserGoal(DicerServant dicer) {
        super(dicer);
        this.dicer = dicer;
    }

    public boolean canUse() {
        return super.canUse() && this.dicer.laserCooldown == 0 && this.dicer.getPose() == Pose.STANDING;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.dicer.laserCooldown == 0;
    }

    public void start() {
        super.start();
        this.dicer.setLasering(false);
        this.dicer.setPose(Pose.STANDING);
    }

    public void stop() {
        super.stop();
        this.dicer.setLasering(false);
        this.dicer.setPose(Pose.STANDING);
        this.dicer.laserCooldown = 100 + this.dicer.getRandom().nextInt(100);
        if (this.beam != null) {
            this.beam.discard();
        }
    }

    public void tick() {
        LivingEntity target = this.dicer.getTarget();
        if (target != null) {
            double distance = this.dicer.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (this.dicer.isLasering()) {
                ++this.timer;
                this.dicer.getNavigation().stop();
                if (this.timer < 5) {
                    this.dicer.lookAt(target, 30.0F, 30.0F);
                    this.dicer.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.dicer.setYRot(this.dicer.yBodyRot);
                    this.dicer.setYHeadRot(this.dicer.yBodyRot);
                    this.dicer.yBodyRotO = this.dicer.getYRot();
                    this.dicer.yHeadRotO = this.dicer.getYRot();
                }

                if (this.timer >= 5) {
                    this.dicer.getLookControl().setLookAt(target.getX(), target.getY() + (double)(target.getEyeHeight() / 2.0F), target.getZ(), 1.5F, 90.0F);
                    this.dicer.setYRot(this.dicer.yBodyRot);
                    this.dicer.setYHeadRot(this.dicer.yBodyRot);
                    this.dicer.yBodyRotO = this.dicer.getYRot();
                    this.dicer.yHeadRotO = this.dicer.getYRot();
                }

                if (this.timer == 10) {
                    this.dicer.setPose(OPPoses.LASERING.get());
                    this.beam = new DicerServantLaser(
                            this.dicer.level(), this.dicer,
                            this.dicer.getX(), this.dicer.getY() + 2.45, this.dicer.getZ(),
                            (this.dicer.yBodyRot + 90.0F) * (float) (Math.PI / 180.0D),
                            -this.dicer.getXRot() * (float) (Math.PI / 180.0D),
                            89,
                            this.dicer.isElite() ? 5 : 4);
                    this.beam.setFiery(this.dicer.isElite());
                    this.dicer.level().addFreshEntity(this.beam);
                }

                if (this.timer == 100) {
                    this.beam.discard();
                }

                if (this.timer > 110) {
                    this.timer = 0;
                    this.dicer.setAttackState(0);
                    this.dicer.laserCooldown = 100 + this.dicer.getRandom().nextInt(100);
                }
            } else {
                this.dicer.lookAt(target, 30.0F, 30.0F);
                this.dicer.getLookControl().setLookAt(target, 30.0F, 30.0F);
                if (distance < 512.0) {
                    this.dicer.setLasering(true);
                }
            }
        }
    }
}
