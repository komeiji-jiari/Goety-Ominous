package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「二阶段跳砸连招」goal（对应原版 {@code JumpSmashComboGoal}）。
 *
 * <p>它只认领<b>状态 32</b> —— 二阶段跳劈落地之后的那一长串连招（115 tick / 5.79 秒）。
 * 状态 32 是状态 24（一阶段落地砸）的加强版，由实体在
 * {@code UpdateWithAttack()} 里切过去：
 *
 * <pre>
 * if (getAttackState() == 23 &amp;&amp; onGround()) {
 *     setAttackState(getPhase() &gt;= 2 ? 32 : 24);
 * }
 * </pre>
 *
 * <h2>⚠️ 它和 {@link PossessedPaladinStateGoal} 只差一个 tick()</h2>
 * 两个类逐行比对，唯一的实质差别是「什么时候转过头去看目标」：
 *
 * <table border="1">
 *   <caption>看目标的时机</caption>
 *   <tr><th></th><th>StateGoal（一般招式）</th><th>本类（跳砸连招）</th></tr>
 *   <tr><td>条件</td><td>{@code attackTicks < attackseetick} 就一直看</td>
 *       <td><b>25~27 tick 看，40~65 tick 看</b>，其余一律锁死朝向</td></tr>
 * </table>
 *
 * <p>为什么本类要这么拧巴：这招是<b>三段砸</b>，中间有两段冲刺位移。
 * 全程盯着目标看的话，圣骑会在冲刺途中不停扭头，人就歪着飞出去了；
 * 全程锁死的话，第三段又追不上走位的目标。所以作者手工挑了这两个窗口 ——
 * 正好是「该调整方向的空档」，冲刺那几 tick 之外的其余时间都咬死朝向。
 *
 * <p>本类的 {@code attackseetick} 字段注册时传 0，而且 {@link #tick()} 里
 * <b>压根没读它</b>（原版也没读）—— 留着只是为了和注册参数一一对应。
 *
 * <p>另外两个方法 {@link #canBeAlerted()} 和 {@link #stop()} 的形态和
 * {@link PossessedPaladinStateGoal} 完全一样，那边有详细注释，这里不重复。
 */
public class PossessedPaladinJumpSmashComboGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本类才接管。注册传的是 32。 */
    private final int getattackstate;
    /** 接管后要处于的状态。和 getattackstate 相同，所以 start() 不会真的切状态。 */
    private final int attackstate;
    /** 收招要回的状态。注册传 0（待机）。 */
    protected final int attackendstate;
    /** 这招最多持续多少 tick。注册传 {@code toTicks(5.79F)} = 115。 */
    private final int attackfinaltick;
    /** 原版留着但 {@link #tick()} 从不读取（看目标的时机是写死的 25~27 / 40~65）。 */
    protected final int attackseetick;
    /** 收招时有没有资格转进「警觉」状态（9）。注册传 true。 */
    private final boolean canAllertedState;
    /** 转进警觉状态的概率。注册传 25.0。 */
    private final double allertedStateChance;

    public PossessedPaladinJumpSmashComboGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
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
    // 圣骑只注册一处，用的是第一个构造函数，所以第二个不实现。

    /** 只认状态，不看目标、不看距离、不看冷却 —— 这是被上一招「指派」过来的，不是自己抢的。 */
    @Override
    public boolean canUse() {
        return this.entity.getAttackState() == this.getattackstate;
    }

    /** {@code <=} 是刻意的：最后一 tick 还要维持，早一 tick 放手会露出「没人管」的空档。 */
    @Override
    public boolean canContinueToUse() {
        return this.attackfinaltick > 0 ? this.entity.attackTicks <= this.attackfinaltick : this.canUse();
    }

    /** 状态 23 落地时已经把状态切成 32 了，这里通常什么都不做。 */
    @Override
    public void start() {
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    /**
     * 原版写法照抄。注意 {@code nextInt()} 是<b>无参</b>的（返回整个 int 范围），
     * 乘 100 会溢出成负数，而负数恒小于 chance —— 所以实际通过率大约就是一半。
     * 看着像 bug，但原版行为确实如此。详见 {@link PossessedPaladinStateGoal}。
     */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    /**
     * 收招。
     *
     * <p>⚠️ {@code jump_cooldown} <b>不在本类里设</b>，而是由注册处的匿名子类补一句 ——
     * 原版就是这么写的（{@code PossessedPaladinEntity.java:830}），
     * 和状态 24 那条完全同一个套路，别以为这里漏了。
     * 漏设的后果见 {@code registerGoals} 里状态 24 的注释：圣骑会一落地就又起跳，变成跳蚤。
     */
    @Override
    public void stop() {
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public void tick() {
        // 见类注释的表格：只有这两个窗口看目标，其余时间锁死朝向。
        if (this.entity.attackTicks >= 25 && this.entity.attackTicks < 28 && this.entity.targetIsNotNull()) {
            LivingEntity target = this.entity.target();
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else if (this.entity.attackTicks >= 40 && this.entity.attackTicks <= 65 && this.entity.targetIsNotNull()) {
            LivingEntity target = this.entity.target();
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            // 和另外几个 goal 一致：yRotO 是「冻结朝向」。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    /** 这一招每 tick 都有位移和伤害判定，不能有延迟。 */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
