package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「突刺抓取」起手 goal（对应原版 {@code StabGrabGoal}）。
 *
 * <p>它只认领<b>状态 19</b>：冲上去一记突刺，刺中就<b>把目标拽上自己的坐骑位</b>。
 * 收招时按 {@code succedGrabbing} 分三路：
 * <pre>
 * 抓住了        → 状态 20（抓取处决，110 tick 的处决演出）
 * 没抓住 &amp;&amp; 二阶段 → 状态 33（突刺收招）
 * 没抓住 &amp;&amp; 一阶段 → 状态 21（突刺收招）
 * </pre>
 *
 * <h2>⚠️ 为什么它不能复用 {@link PossessedPaladinAttackGoal}</h2>
 * 看着像「又一个攻击 goal」，但四处关键差异，抄错任何一处都会出问题：
 * <ol>
 *   <li><b>{@link #canUse()} 只认状态，不看目标、不看距离、不看冷却。</b>
 *       别的攻击 goal 都要求「状态 0 + 目标在射程内」，本类什么都不要求 ——
 *       因为它是被上一招的 {@code stop()} <b>指派</b>过来的，不是自己抢的。
 *       一旦状态变成 19，本类必须立刻接管；加任何额外条件都可能让状态 19 没人认领
 *       （那正是「永久卡死」的成因）。</li>
 *   <li><b>{@link #canContinueToUse()} 用的是 {@code <=}，不是 {@code <}。</b>
 *       即最后一 tick（第 28 tick）的判定<b>还能打出去</b>。改成 {@code <} 会让突刺判定
 *       永远差一 tick 打不出来。</li>
 *   <li><b>{@link #stop()} 完全不问 {@code canBeAlerted()}。</b>
 *       它永远不去状态 9，去向只由 {@code succedGrabbing} 和阶段决定。
 *       那个 {@code allertedStateChance} 参数在原版里<b>根本没被读过</b>，
 *       但为了跟注册表对齐还是留着。</li>
 *   <li><b>{@link #stop()} 会清零 {@code attackTicks}。</b>
 *       别的 goal 都把清零交给接手的状态 goal，这里自己清了 ——
 *       因为下一个状态（20/21/33）的计时要从 0 重新数。</li>
 * </ol>
 *
 * <p>另外 {@link #requiresUpdateEveryTick()} 返回 <b>true</b>（别的 goal 是 false）——
 * 突刺这种「精确到 tick」的位移不能有延迟。
 */
public class PossessedPaladinStabGrabGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本类才接管。原版注册传的是 19。 */
    private final int getattackstate;
    /** 接管后要处于的状态。和 getattackstate 相同时不做切换。 */
    private final int attackstate;
    /** 收招要回的状态。本类实际不用它 —— 去向由 {@link #stop()} 写死。 */
    protected final int attackendstate;
    /** 招式总长度。<b>含</b>最后一 tick（见 {@link #canContinueToUse()}）。 */
    private final int attackfinaltick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。 */
    protected final int attackseetick;
    /** 原版留着但本类从不读取，保留是为了和注册处对齐。 */
    private final boolean canAllertedState;
    /** 同上，原版本类从不读取。 */
    private final double allertedStateChance;

    public PossessedPaladinStabGrabGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
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

    // 原版还有第二个构造函数，多一个 interruptsAI 参数（为 false 时干脆不设 flags）。
    // 圣骑只在状态 19 这一处注册本类，用的是第一个构造函数，所以第二个不实现。

    /** 见类注释第 1 条：只认状态，别的什么都不看。 */
    @Override
    public boolean canUse() {
        return this.entity.getAttackState() == this.getattackstate;
    }

    /** 见类注释第 2 条：{@code <=} 是刻意的，最后一 tick 还要打。 */
    @Override
    public boolean canContinueToUse() {
        return this.attackfinaltick > 0 ? this.entity.attackTicks <= this.attackfinaltick : this.canUse();
    }

    @Override
    public void start() {
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    /** 见类注释第 3 条：去向由 {@code succedGrabbing} 和阶段决定，绝不转警觉。 */
    @Override
    public void stop() {
        this.entity.setAttackState(this.entity.succedGrabbing
                ? 20
                : (this.entity.getPhase() >= 2 ? 33 : 21));
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
            // 和其它 goal 一致：yRotO 是「冻结朝向」，不是「对齐身体」。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    /** 见类注释最后一段：突刺不能有 tick 延迟。 */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
