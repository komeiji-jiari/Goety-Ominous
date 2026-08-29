package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPEntities;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class VoltServantShootInWaterGoal extends RamblerServantAttackGoal {

    private final VoltServant volt;
    private int cooldown;

    public VoltServantShootInWaterGoal(VoltServant volt) {
        super(volt);
        this.cooldown = 0;
        this.volt = volt;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.volt.isInWater();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.volt.isInWater();
    }

    @Override
    public void tick() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            this.volt.lookAt(target, 30.0F, 30.0F);
            this.volt.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
            double distanceSqr = this.volt.distanceToSqr(target.getX(), target.getY(), target.getZ());

            if (this.volt.getAttackState() == 1) {
                ++this.timer;
                this.cooldown = 30;
                this.volt.getNavigation().stop();
                if (this.timer == 1) {
                    this.volt.setPose(OPPoses.SHOOTING.get());
                }
                if (this.timer == 10) {
                    this.volt.playSound(OPSoundEvents.VOLT_SHOOT.get(), 3.0F,
                            1.0F / (0.8F + this.volt.getRandom().nextFloat() * 0.4F));
                }
                if (this.timer == 12) {
                    this.shootCharge(target);
                }
                if (this.timer > 20) {
                    this.timer = 0;
                    this.volt.setAttackState(0);
                }
            } else {
                this.volt.getNavigation().moveTo(target, 1.1);
                if (this.cooldown > 0) {
                    --this.cooldown;
                } else if (distanceSqr <= this.getAttackReachSqr(target)) {
                    this.volt.setAttackState(1);
                }
            }
        }
    }

    private void shootCharge(LivingEntity target) {
        VoltServantElectricCharge charge = new VoltServantElectricCharge(OPEntities.ELECTRIC_CHARGE.get(), this.volt.level());
        charge.setOwner(this.volt);
        charge.moveTo(this.volt.getX(), this.volt.getY() + this.volt.getEyeHeight(), this.volt.getZ());

        double d0 = target.getX() - this.volt.getX();
        double d1 = target.getY() + (double) target.getEyeHeight() - 1.1D - charge.getY();
        double d2 = target.getZ() - this.volt.getZ();
        float f9 = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.01F;
        float f10 = this.volt.isElite() ? 0.5F : 0.25F;
        if (this.volt.isCharged()) {
            f10 += 0.25F;
        }
        if (this.volt.isElite()) {
            charge.setRainbow(true);
            f10 += 0.33F;
        }
        charge.shoot(d0, d1 + (double) f9, d2, f10, 2.0F);
        this.volt.level().addFreshEntity(charge);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return (double) (this.monster.getBbWidth() * 3.0F * this.monster.getBbWidth() * 3.0F + target.getBbWidth());
    }
}
