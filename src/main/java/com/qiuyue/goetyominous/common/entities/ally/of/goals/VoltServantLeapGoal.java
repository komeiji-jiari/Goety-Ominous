package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Volt 仆从的跳跃扑击剧本——逐字节对照 OF 原版 VoltLeapGoal 重写。
 * 与之前版本的关键区别：
 *  - 直接 extends Goal（不是 AttackGoal），flags 只有 JUMP + LOOK；
 *  - 这是"无伤突进"：只给自己一个朝随机方向（目标大致方向 ±75°）的速度，
 *    不造成任何伤害，靠姿势状态机播 JUMP_START→JUMP_FALL→JUMP_END 三段动画；
 *  - canUse 条件对齐原版：有活目标 + 跳跃冷却好了 + 站地上 + 不在水里 + 距离<30。
 */
public class VoltServantLeapGoal extends Goal {

    private final VoltServant volt;

    public VoltServantLeapGoal(VoltServant volt) {
        this.volt = volt;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    // ① 什么时候能"上场"？有活目标 + 冷却好了 + 站地上 + 不在水里 + 够近
    @Override
    public boolean canUse() {
        LivingEntity target = this.volt.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.volt.leapCooldown > 0) {
            return false;
        }
        if (!this.volt.onGround() || this.volt.isInWater()) {
            return false;
        }
        return this.volt.distanceTo(target) < 30.0D;
    }

    // ② 上场后：只要还在空中（没落地、没进水）就一直演下去
    @Override
    public boolean canContinueToUse() {
        return !this.volt.onGround() && !this.volt.isInWater();
    }

    // ③ 起跳：随机偏转 ±75° 方向，给个 1.5 速度，摆 LONG_JUMPING 姿势（无伤害！）
    @Override
    public void start() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            float f1 = -1.0F;
            float f2 = (float) Math.toRadians((f1 + 90.0F) + this.volt.getRandom().nextFloat() * 150.0F - 75.0F);
            float f3 = 1.5F;
            this.volt.playSound(OPSoundEvents.VOLT_SQUISH.get(), 0.2F, 1.0F);
            Vec3 vec3 = this.volt.getDeltaMovement()
                    .add((double) f3 * Math.cos((double) f2), 0.0D, (double) f3 * Math.sin((double) f2));
            this.volt.setPose(Pose.LONG_JUMPING);
            this.volt.setDeltaMovement(vec3.x, 0.9D, vec3.z);   // Y 方向固定 0.9（原版值）
            this.volt.getNavigation().stop();
            this.volt.leapCooldown = 40 + this.volt.getRandom().nextInt(20);
        }
    }

    // ④ 起跳后每 tick：一直盯着目标
    @Override
    public void tick() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            this.volt.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }
}
