package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「滞空下劈」goal（对应原版 {@code JumpFallGoal}）。
 *
 * <p>它只认领<b>状态 23</b> —— 也就是圣骑跳起来之后、人还在半空中的那一段。
 * 真正的「落地」判定不在这里，而在实体的 {@code UpdateWithAttack()}：
 *
 * <pre>
 * if (getAttackState() == 23 &amp;&amp; onGround()) {
 *     setAttackState(getPhase() &gt;= 2 ? 32 : 24);
 * }
 * </pre>
 *
 * 所以本类其实只是个「占位 + 冻结朝向」的看守：它不主动切状态，负责的是
 * 「只要还在天上，就一直维持状态 23 并锁住朝向」，最长 {@code attackfinaltick}（100 tick）。
 * 一旦落地，实体自己会把状态改成 24（一阶段）或 32（二阶段），本类就自动退场了。
 *
 * <h2>⚠️ 它和 {@link PossessedPaladinStabGrabGoal} 是双胞胎</h2>
 * 两个类逐行比对下来只差 {@link #stop()} 一个方法。原版就是两份独立拷贝，这里也保持两份 ——
 * 好处是每个类只管一个状态，注释能贴着那一招写，不用在共用类里加条件分支。
 *
 * <table border="1">
 *   <caption>两者的差异</caption>
 *   <tr><th></th><th>StabGrabGoal（状态 19）</th><th>JumpFallGoal（状态 23）</th></tr>
 *   <tr><td>stop() 去哪</td><td>看 succedGrabbing，再去 33 / 21</td><td>只看阶段，去 32 / 24</td></tr>
 *   <tr><td>注册的 attackfinaltick</td><td>28</td><td>100</td></tr>
 * </table>
 *
 * <p>另外本类的 {@code attackendstate} 字段（注册时传 24）在原版和这里<b>都没有被读过</b>，
 * 保留它只是为了和原版注册参数一一对应，别以为改了它就能改变去向。
 */
public class PossessedPaladinJumpFallGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本类才接管。注册传的是 23。 */
    private final int getattackstate;
    /** 接管后要处于的状态。和 getattackstate 相同时不做切换。 */
    private final int attackstate;
    /** 原版留着但本类从不读取，保留是为了和注册处对齐。 */
    protected final int attackendstate;
    /** 最长维持多少 tick。<b>含</b>最后一 tick（见 {@link #canContinueToUse()}）。 */
    private final int attackfinaltick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。注册传 0，等于一开始就锁死。 */
    protected final int attackseetick;
    /** 原版留着但本类从不读取。 */
    private final boolean canAllertedState;
    /** 同上，原版本类从不读取。 */
    private final double allertedStateChance;

    public PossessedPaladinJumpFallGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
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
    // 圣骑只在状态 23 这一处注册本类，用的是第一个构造函数，所以第二个不实现。

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

    @Override
    public void start() {
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    /**
     * 去向只看阶段：一阶段落到状态 24（落地砸），二阶段落到状态 32（落地砸 + 后续连招）。
     *
     * <p>⚠️ 正常落地时这条根本轮不到执行 —— 实体在 {@code UpdateWithAttack} 里
     * 一检测到 {@code onGround()} 就抢先切走了，本类随之退场。这条只兜底
     * 「一直在天上没落地、硬撑到 100 tick」的极端情况（比如掉进虚空或者卡在方块里）。
     */
    @Override
    public void stop() {
        this.entity.setAttackState(this.entity.getPhase() >= 2 ? 32 : 24);
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

    /** 滞空段的位移不能有 tick 延迟。 */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
