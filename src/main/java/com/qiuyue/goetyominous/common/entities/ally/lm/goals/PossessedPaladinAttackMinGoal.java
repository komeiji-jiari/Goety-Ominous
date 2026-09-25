package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「通用攻击（带距离下限）goal」（对应原版 {@code IAttackGoalMin}）。
 *
 * <h2>它和 {@link PossessedPaladinAttackGoal} 是什么关系</h2>
 * 原版有两个长得几乎一样、但住在不同包里的类：{@code P_PAttackGoal}（圣骑专用，四招共用）
 * 和 {@code IAttackGoalMin}（所有动画 Boss 通用，名字里的 Min 指「最小值」）。
 * 两者唯一的实质差别就是本类多了一条<b>距离下限</b>：
 *
 * <pre>
 * P_PAttackGoal    ：distanceTo(target) &lt;  attackrange
 * IAttackGoalMin   ：distanceTo(target) &lt;  attackrange  &amp;&amp;  distanceTo(target) &gt;  attackrangemin
 * </pre>
 *
 * 圣骑只有<b>一处</b>用到它 —— 状态 22 的「跳劈起手」，注册参数是
 * {@code (0, 22, 23, toTicks(1.67F), toTicks(1.67F), 16.0F, 6.0F)}，也就是
 * 「目标在 6~16 格之间才起跳」。近身了就不跳 —— 因为跳劈是拉近距离用的，
 * 人已经贴脸了再跳毫无意义。
 *
 * <h2>⚠️ 三处和 {@link PossessedPaladinAttackGoal} 不一样，别互抄</h2>
 * <ol>
 *   <li><b>{@link #stop()} 不问 {@code canBeAlerted()}，也不重掷招式类型。</b>
 *       它只有一个动作：把状态切成 {@code attackendstate}。照抄隔壁那个类的话，
 *       圣骑会在跳劈收招时莫名其妙转进「警觉」状态（9）。</li>
 *   <li><b>{@link #stop()} 不清 {@code attackTicks}。</b>只清 {@code attackCooldown}。
 *       这一点很要紧：下一站是状态 23（滞空下劈），由 {@code PossessedPaladinJumpFallGoal} 认领，
 *       那关的时限是 {@code attackfinaltick = 100}，指的是<b>从起跳算起的总时长</b>。
 *       如果这里把 attackTicks 清零，状态 23 就又能再撑 100 tick，整个滞空段会被拉长一倍。</li>
 *   <li><b>{@link #canContinueToUse()} 用的是 {@code <}，不是 {@code <=}。</b>
 *       这和隔壁一样，但和 {@code PossessedPaladinStabGrabGoal} 的 {@code <=} 不同，注意别串。</li>
 * </ol>
 *
 * <p>原版还有个第二个构造函数（自定义要抢占哪些行为标志），圣骑这处注册没用到，不实现。
 */
public class PossessedPaladinAttackMinGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时才有资格起手。这里传 0（待机）。 */
    private final int getattackstate;
    /** 起手后切到的状态。这里传 22。 */
    private final int attackstate;
    /** 招式结束时切到的状态。这里传 <b>23</b>（滞空下劈），不是 0。 */
    private final int attackendstate;
    /** 这招最多持续多少 tick。 */
    private final int attackMaxtick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。 */
    private final int attackseetick;
    /** 触发距离上限。 */
    private final float attackrange;
    /** 触发距离下限 —— 本类比隔壁多出来的那一条。 */
    private final float attackrangemin;

    public PossessedPaladinAttackMinGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                         int attackendstate, int attackMaxtick, int attackseetick,
                                         float attackrange, float attackrangemin) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.attackrangemin = attackrangemin;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.distanceTo(target) > this.attackrangemin
                && this.entity.getAttackDelayTicks() <= 0;
        // 原版这里还有一句 !animationLockedForTests()，是作者留的调试开关（恒为 false），移植时省掉。
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    /** 见类注释第 2 条：只切状态，别的什么都不动。 */
    @Override
    public void stop() {
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            // 和另外几个 goal 一致：yRotO 是「冻结朝向」。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
