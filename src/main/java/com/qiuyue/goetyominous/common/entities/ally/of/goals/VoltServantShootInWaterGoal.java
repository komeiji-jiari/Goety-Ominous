package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.entities.projectiles.of.VoltServantElectricCharge;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPEntities;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Volt 仆从的水中射击剧本——逐字节对照 OF 原版 VoltShootInWaterGoal 新建。
 * 这是伏特鳐"水陆双 AI"的水中一半：进水后由它开火（陆地版 VoltServantShootGoal 要求不在水里）。
 * 和陆地版的区别：
 *  - canUse 要求在水里；
 *  - 射击冷却 30 tick（陆地是 24）；
 *  - 靠近到攻击范围才开火（陆地版是冷却好了就开火）。
 */
public class VoltServantShootInWaterGoal extends RamblerServantAttackGoal {

    private final VoltServant volt;   // 这本剧本是给哪只仆从用的
    private int cooldown;             // 射击冷却（本地字段，跟原版一致）

    public VoltServantShootInWaterGoal(VoltServant volt) {
        super(volt);
        this.cooldown = 0;
        this.volt = volt;
    }

    // ① 什么时候能"上场"？基类（有目标）通过 + 在水里
    @Override
    public boolean canUse() {
        return super.canUse() && this.volt.isInWater();
    }

    // ② 上场后：只要基类没失效 + 没上岸就一直演下去
    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.volt.isInWater();
    }

    // ③ 每 tick 都在演：看目标 → 靠近到射程 → 开火 → 收招
    @Override
    public void tick() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            this.volt.lookAt(target, 30.0F, 30.0F);
            this.volt.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
            double distanceSqr = this.volt.distanceToSqr(target.getX(), target.getY(), target.getZ());

            if (this.volt.getAttackState() == 1) {
                // ===== 正在射击状态 =====
                ++this.timer;
                this.cooldown = 30;                       // 射击结束后 30 tick 冷却
                this.volt.getNavigation().stop();         // 开枪时停在水里
                if (this.timer == 1) {
                    this.volt.setPose(OPPoses.SHOOTING.get());  // 摆射击姿势
                }
                if (this.timer == 10) {
                    this.volt.playSound(OPSoundEvents.VOLT_SHOOT.get(), 3.0F,
                            1.0F / (0.8F + this.volt.getRandom().nextFloat() * 0.4F));
                }
                if (this.timer == 12) {
                    this.shootCharge(target);             // 第 12 tick 发射电球
                }
                if (this.timer > 20) {
                    this.timer = 0;
                    this.volt.setAttackState(0);          // 收招回待机
                }
            } else {
                // ===== 待机：游向目标，靠近到射程且冷却好了就开火 =====
                this.volt.getNavigation().moveTo(target, 1.1);
                if (this.cooldown > 0) {
                    --this.cooldown;
                } else if (distanceSqr <= this.getAttackReachSqr(target)) {
                    this.volt.setAttackState(1);
                }
            }
        }
    }

    // ④ 真正的"开枪"：造一颗电球，从眼睛位置朝目标打出去（公式照抄原版）
    private void shootCharge(LivingEntity target) {
        // 用我们自己的电球子类（爆炸带友军过滤），setOwner 让爆炸能认出这是伏特兽打的
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
            charge.setRainbow(true);   // 精英电球变色
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
