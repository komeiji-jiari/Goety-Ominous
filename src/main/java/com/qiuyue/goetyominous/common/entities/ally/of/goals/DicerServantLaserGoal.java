package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.qiuyue.goetyominous.common.entities.projectiles.of.DicerServantLaser;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/**
 * Dicer 仆从的激光 AI：瞄准 10 帧后从胸口射出 OF 敌对版的激光（DicerServantLaser）。
 * 激光直接继承 OF 原版 DicerLaser，外观/音效/粒子与敌对版一致，
 * 但重写了命中判定用 Goety 的 MobUtil.areAllies 过滤友军，不会误伤自己人。
 * 发射参数照搬 OF 原版 DicerLaserGoal：位置=胸口(y+2.45)、yaw/pitch 换算弧度、
 * 持续 89 帧、伤害取仆从的攻击力属性。
 */
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
                    // 照搬 OF 原版 DicerLaserGoal 的发射参数，效果/数值与敌对版完全一致：
                    // 起点在胸口(y+2.45)，yaw=(yBodyRot+90)*PI/180 弧度，pitch=-xRot*PI/180 弧度，
                    // 持续 89 帧，伤害=精英5点/普通4点，精英变体点燃目标
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
