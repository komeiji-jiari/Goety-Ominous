package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「警觉」状态 goal（状态 9，对应原版 {@code AlertedGoal}）。
 *
 * <h2>它是怎么被触发的</h2>
 * 好几种招式收招时都会问一句 {@code canBeAlerted()}（见 {@link PossessedPaladinAttackGoal#stop()}、
 * 以及 {@link PossessedPaladinStateGoal#stop()}）。一旦掷中，就把状态切成 9 —— 也就是本类。
 * 目前开放这个分支的只有招 10（后空翻）和招 38（掷三叉戟），几率都是 35%。
 *
 * <h2>它干什么</h2>
 * 圣骑会绕着目标侧向滑步一小段（{@link #velocity} 决定方向，{@link #start()} 里有一半概率翻转），
 * 持续一个带随机抖动的时长，然后收招。
 *
 * <h2>为什么它必须和状态 5 一起存在</h2>
 * {@link #stop()} 里写的是 {@code setAttackState(hasParried ? 5 : attackendstate)} ——
 * 如果这期间圣骑成功格挡过（{@code hasParried} 被置 true），它不会回待机，而是切到<b>状态 5</b>。
 * 状态 5 由 {@code IStateGoal} 认领，所以注册本类时必须把状态 5 的 goal 也一起注册上，
 * 否则一样会卡在没人认领的状态里出不来。
 */
public class PossessedPaladinAlertedGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本 goal 才有资格接管（9 = 警觉）。 */
    private final int getattackstate;
    /** 接管后要处于的状态。 */
    private final int attackstate;
    /** 收招完毕要回到的状态，通常就是 0（待机）。 */
    protected final int attackendstate;
    /** 最多维持多少 tick（实际还会加上一个随机抖动）。 */
    private final int attackfinaltick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。 */
    protected final int attackseetick;
    /** 收招时有没有资格<b>再次</b>转进警觉状态。原版这一处传的是 false。 */
    private final boolean canAllertedState;
    /** 再次转进警觉的几率（百分数）。 */
    private final double allertedStateChance;
    /** 侧向滑步的速度。start() 里有一半概率取反，于是方向随机左右。 */
    public float velocity = 0.15F;

    public PossessedPaladinAlertedGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                       int attackendstate, int attackfinaltick, int attackseetick,
                                       boolean canAllertedState, double allertedStateChance) {
        this.entity = entity;
        this.canAllertedState = canAllertedState;
        this.allertedStateChance = allertedStateChance;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.entity.getAttackState() == this.getattackstate;
    }

    @Override
    public void start() {
        // 一半概率把滑步方向翻过来，免得每次都往同一边闪。
        // 原版写的是 nextInt()（无参，整个 int 范围），乘 100 会溢出 —— 通过率大约一半。
        if (this.entity.getRandom().nextInt() * 100 < 50) {
            this.velocity *= -1.0F;
        }
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    /** 同 {@link PossessedPaladinAttackGoal#canBeAlerted()}，原版那个溢出写法的复刻。 */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    @Override
    public void stop() {
        // 关键分支：格挡成功过就转进状态 5（由 IStateGoal 认领），否则回 attackendstate。
        this.entity.setAttackState(this.entity.hasParried ? 5 : this.attackendstate);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        // 时长带随机抖动：原版在 attackfinaltick 上加了 nextInt(-7, 10)，
        // 也就是实际时长在 [final-7, final+9] 之间浮动，让滑步看着不那么机械。
        return this.attackfinaltick > 0
                ? this.entity.attackTicks <= this.attackfinaltick + this.entity.getRandom().nextInt(-7, 10)
                : this.canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }

        if (target != null) {
            // 算出目标「身侧 8 格」的一个点，朝它滑过去。角度取身体朝向 +90 度，所以是横向的。
            // 原版在这里还算了两个变量 n / p 但压根没用上，属于死代码，这里略去。
            float radius = 8.0F;
            double angle = Math.toRadians((double) (-this.entity.yBodyRot + 90.0F));
            double sin = (double) (Mth.sin((float) angle) * radius);
            double cos = (double) (Mth.cos((float) angle) * radius);
            // 目标 → 身侧点的偏移量，归一化后按 velocity 缩放，直接设为当前速度。
            Vec3 sideOffset = new Vec3(target.getX() + sin - this.entity.getX(), 0.0D,
                    target.getZ() + cos - this.entity.getZ());
            Vec3 finalPos = sideOffset.normalize().scale((double) this.velocity);
            this.entity.setDeltaMovement(finalPos);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
