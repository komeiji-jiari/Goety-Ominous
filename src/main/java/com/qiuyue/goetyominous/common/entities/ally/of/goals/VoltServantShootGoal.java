package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.entities.projectiles.of.VoltServantElectricCharge;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPEntities;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Volt 仆从的陆地射击剧本——逐字节对照 OF 原版 VoltShootGoal 重写。
 * 与之前版本的区别：
 *  - 只允许在陆地开火（水里交给 VoltServantShootInWaterGoal）；
 *  - 用本地 cooldown 字段做冷却（跟原版一致），不再用实体的 shootCooldown；
 *  - 电球速度公式完全对齐原版（f10 随精英/带电累加，精英发彩虹电球）；
 *  - 发射时机对齐原版（timer 12 发射，20 收招）。
 */
public class VoltServantShootGoal extends RamblerServantAttackGoal {

    private final VoltServant volt;   // 这本剧本是给哪只仆从用的
    private int cooldown;             // 射击冷却（本地字段，跟原版一致）

    public VoltServantShootGoal(VoltServant volt) {
        super(volt);
        this.cooldown = 0;
        this.volt = volt;
    }

    // ① 什么时候能"上场"？基类（有目标）通过 + 不在水里
    @Override
    public boolean canUse() {
        return super.canUse() && !this.volt.isInWater();
    }

    // ② 上场后：只要基类没失效 + 没进水就一直演下去
    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !this.volt.isInWater();
    }

    // ③ 每 tick 都在演：看目标 → 冷却好就开火 → 收招
    @Override
    public void tick() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            this.volt.lookAt(target, 30.0F, 30.0F);
            this.volt.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());

            if (this.volt.getAttackState() == 1) {
                // ===== 正在射击状态 =====
                ++this.timer;
                this.cooldown = 24;                       // 射击结束后 24 tick 冷却
                this.volt.getNavigation().stop();         // 开枪时站定
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
                // ===== 待机：地面且冷却好了就进入射击状态 =====
                if (this.volt.onGround() && this.cooldown > 0) {
                    --this.cooldown;
                }
                if (this.volt.onGround() && this.cooldown == 0) {
                    this.volt.setAttackState(1);
                }
                // 目标在水里时：一边等冷却一边追到岸边
                if (target.isInWater() && this.cooldown > 0) {
                    this.volt.getNavigation().moveTo(target, 1.1);
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
}
