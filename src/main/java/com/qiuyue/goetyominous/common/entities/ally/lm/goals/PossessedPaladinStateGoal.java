package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「收招 / 过场」状态 goal（对应原版 {@code P_PStateGoal}）。
 *
 * <h2>它到底解决什么问题</h2>
 * 每一招打完，招式 goal 的 {@code stop()} 都会把 {@code attackState} 设成<b>另一个</b>状态，
 * 而不是直接回 0。比如砸地（6）收招时会切到 7（近身收招）或 8（<b>第二下转身横扫</b>），二阶段则是 31。
 * 本类只负责「认领、控制时长、收招」这三件事，<b>自己并不产生动作或伤害</b> ——
 * 那三个状态的动画与伤害判定都写在实体的 {@code UpdateWithAttack()} 里
 * （比如状态 8 的那一记转身横扫伤害）。流程是：
 * <ol>
 *   <li>{@link #canUse()} 看到 {@code attackState} 正好等于自己负责的那个数，就把它接管过来；</li>
 *   <li>一直维持到 {@code attackTicks} 超过 {@code attackfinaltick}，也就是收招动画播完；</li>
 *   <li>{@link #stop()} 才把状态切回 0，圣骑重新回到「可以出招」的待机态。</li>
 * </ol>
 *
 * <p><b>不注册本类会怎样</b>：招式 goal 把状态切成 7/8/31 之后，没有任何 goal 认领这几个状态，
 * 于是 {@code attackTicks} 一路往上涨、{@code attackState} 却永远回不到 0。偏偏所有招式 goal 的
 * {@code canUse()} 都要求 {@code getAttackState() == 0} —— 圣骑就此<b>永久卡死</b>：
 * 站在那里，动画还在播，但既不再出招、也不再追人。这正是「砸地能放、放完就定住」的原因。
 *
 * <p>原版注册写法见 {@code PossessedPaladinEntity.java} 的 613 / 623 / 633 行，
 * 三处都是本类，只是参数不同。
 */
public class PossessedPaladinStateGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时，本 goal 才有资格接管。 */
    private final int getattackstate;
    /** 接管之后要处于的状态。和 getattackstate 相同时不做切换。 */
    private final int attackstate;
    /** 收招完毕要回到的状态，通常就是 0（待机）。 */
    protected final int attackendstate;
    /** 接管之后最多维持多少 tick，到点就收招。 */
    private final int attackfinaltick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。 */
    protected final int attackseetick;
    /**
     * 收招时有没有资格转进「警觉」状态（9）。
     *
     * <p>状态 9 由 {@code PossessedPaladinAlertedGoal} 接管（原版 {@code AlertedGoal}，
     * 注册在 {@code PossessedPaladinEntity.java:654}）。本类目前的三处注册（状态 7 / 8 / 31）
     * 都传 {@code true, 50.0D}，与原版一致。
     *
     * <p>先前这个 goal 是单独注册的（那时状态 9 还没实现），所以传 {@code false} 避开
     * 没人认领的状态 9。现在两个 goal 成对存在，就不再需要那种降级处理了。
     */
    private final boolean canAllertedState;
    /** 转进警觉状态的几率（百分数）。canAllertedState 为 false 时不生效。 */
    private final double allertedStateChance;

    public PossessedPaladinStateGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
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
    public boolean canContinueToUse() {
        // attackfinaltick 为 0 表示「没有固定时长」，那就一直管到状态被别人改掉为止。
        return this.attackfinaltick > 0 ? this.entity.attackTicks <= this.attackfinaltick : this.canUse();
    }

    @Override
    public void start() {
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    /**
     * 原版写法照抄。注意 {@code nextInt()} 是<b>无参</b>的（返回整个 int 范围），
     * 乘 100 会溢出成负数，而负数恒小于 chance —— 所以实际通过率大约就是一半。
     * 看着像 bug，但原版行为确实如此。
     */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        // 必须清零：下一招的计时是从 0 开始数的。
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            // 注意：本类（原版 P_PStateGoal）用的是 yRotO，效果是「冻结朝向」。
            // 隔壁 IStateGoal 用的是 yBodyRot，效果是「对齐身体」。两者别互抄。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
