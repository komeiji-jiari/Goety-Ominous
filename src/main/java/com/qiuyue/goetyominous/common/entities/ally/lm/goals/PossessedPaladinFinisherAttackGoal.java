package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「二阶段终结技」goal（对应原版 {@code FinisherAttackGoal}）。
 *
 * <p>它认领<b>状态 37</b> —— 这只是二阶段才解锁的十六秒半长连招
 * （{@code toTicks(16.33F)} = 326 tick），一共十一段动作。
 * 具体每一段在第几 tick 干什么，全写在 {@code PossessedPaladinServant.UpdateWithAttack()}
 * 的状态 37 分支里，本类只负责三件事：<b>什么时候抢、什么时候放、放的时候朝哪儿看</b>。
 *
 * <h2>它和各路 {@code StateGoal} 的区别</h2>
 * {@link PossessedPaladinStateGoal} 是「被上一招指派过来收尾的」，所以
 * {@code canUse()} 只看状态号；本类是<b>主动抢招</b>的，所以判据要全得多：
 * <pre>
 *   有目标 &amp;&amp; 目标活着 &amp;&amp; 距离 &lt; 7 格 &amp;&amp; 状态 == 0 &amp;&amp; 硬直已过
 * </pre>
 * 那个「距离 &lt; 7 格」就是终结技的出手门槛 —— 站远了不会放。
 *
 * <h2>⚠️ 一个和 StateGoal 不一样的收尾动作</h2>
 * 本类的 {@link #stop()} <b>多调一句 {@code randomizeAttacks()}</b>。
 * 这招太长了，中间十一段全是固定顺序、没有随机；重掷一次是为了让「打完这招之后先接哪招」
 * 重新洗牌，免得每次终结技之后都接同一招。
 *
 * <p>另外本类<b>不</b>像 {@code StateGoal} 那样把 {@code attackTicks} 清零 ——
 * 因为 {@code setAttackState()} 内部已经清了，写两遍是多余的。
 *
 * <h2>⚠️ 视角控制是「四段开、三段关」</h2>
 * 这一招有大量位移（冲刺、跳起、落地），所以不能全程盯着目标：
 *
 * <table border="1">
 *   <caption>哪些 tick 段会扭头看目标</caption>
 *   <tr><th>tick 区间</th><th>看目标？</th></tr>
 *   <tr><td>&lt; 140</td><td>看</td></tr>
 *   <tr><td>160 &lt; t &lt; 168</td><td>看</td></tr>
 *   <tr><td>168 &lt; t &lt; 260</td><td>看</td></tr>
 *   <tr><td>275 &lt; t &lt; 290</td><td>看</td></tr>
 *   <tr><td>其余（含 140~160、260~275、290 以后）</td><td><b>锁死朝向</b></td></tr>
 * </table>
 *
 * <p>注意这些区间是从原版<b>照抄的、故意写成这种「大于小于」的松散形式</b>：
 * 区间之间留着 168、260、275~290 这些接缝，有几 tick 两边都不落。
 * 别看着别扭就「顺手」改写成 {@code >= 160 && < 168} 那种整齐写法 ——
 * 数值一变，视角手感就跟原版不一样了。
 *
 * <p>锁朝向用的是 {@code setYRot(yRotO)}，效果是「这一 tick 身体不转」，
 * 和 {@link PossessedPaladinStateGoal} 的做法一致。
 */
public class PossessedPaladinFinisherAttackGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本类才有资格接手。注册传 0（待机）—— 和别的招式一样从待机里抢。 */
    private final int getattackstate;
    /** 接手之后要处于的状态。注册传 37。 */
    private final int attackstate;
    /** 这招收尾要回的状态。注册传 0（待机）。 */
    protected final int attackendstate;
    /** 这招最多持续多少 tick。注册传 {@code toTicks(16.33F)} = 326。 */
    private final int attackfinaltick;
    /**
     * 原版留着但<b>本类和原版都没读过它</b>（{@link #canUse()} 里没用到，
     * 看目标的时机是 {@link #tick()} 里写死的四段）。
     * 注册传 0，留着只是为了和原版的构造参数一一对应。
     */
    protected final int attackseetick;
    /** 收招时有没有资格转进「警觉」状态（9）。本类注册传 true。 */
    private final boolean canAllertedState;
    /** 转进警觉状态的概率。本类注册传 25.0。 */
    private final double allertedStateChance;

    public PossessedPaladinFinisherAttackGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                              int attackendstate, int attackfinaltick, int attackseetick,
                                              float attackrange, boolean canAllertedState,
                                              double allertedStateChance) {
        this.entity = entity;
        this.canAllertedState = canAllertedState;
        this.allertedStateChance = allertedStateChance;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    // 原版还有第二个构造函数，把 flags 换成调用方传进来的 EnumSet。
    // 圣骑只注册一处，用的是第一个，所以第二个不实现。

    /** 出手门槛：7 格以内、目标活着、当前待机、硬直已过。 */
    private final float attackrange;

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    /**
     * ⚠️ 是 {@code <} 不是 {@code <=}（隔壁 {@code StateGoal} 用的是 {@code <=}）。
     * 到第 326 tick 就撒手，之后交给别的 goal 收尾。原版如此，照抄。
     */
    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackfinaltick;
    }

    /** 状态从 0 切到 37。原版这里没有「相同就不切」的判断，直接切。 */
    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    /**
     * 原版写法照抄。注意 {@code nextInt()} 是<b>无参</b>的（返回整个 int 范围），
     * 乘 100 会溢出成负数，而负数恒小于 chance —— 所以实际通过率大约就是一半，
     * 不是字面上的 25%。看着像 bug，但原版行为确实如此。
     * 详见 {@link PossessedPaladinStateGoal} 里同一段逻辑的说明。
     */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    /**
     * 收招。
     *
     * <p>三步顺序别动：先重掷招式变体（{@code randomizeAttacks}），再决定回待机还是进警觉，
     * 最后把硬直清掉。清硬直必须做 —— 不然下一招会卡在 {@code getAttackDelayTicks() <= 0}
     * 这一条上出不来。
     */
    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        this.entity.attackCooldown = 0;
        // ⚠️ 这一句是<b>我们加的</b>，原版没有 —— 补一个安全网，防止仆从永久飘在天上。
        //
        // 招式在第 229 tick 会关掉重力飘起来，第 255 tick 才关回去。
        // 万一在这一段里被打断（挨打出格挡 → 状态 5 的 goal 优先级 0 会把本 goal 挤掉），
        // 「关回去」那一句就永远轮不到了，圣骑会一直浮在半空。
        // 原版是 BOSS，打断它的手段少，这个坑不明显；当仆从天天挨打，必须堵上。
        // 正常打完时这一句是空操作（重力早就关回去了），所以不会影响原版表现。
        this.entity.setNoGravity(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        // 见类注释的表格。⚠️ 那几个区间的边界和「大于小于」的写法都是照抄原版的，别整理。
        if (this.entity.attackTicks < 140
                || this.entity.attackTicks > 160 && this.entity.attackTicks < 168
                || this.entity.attackTicks > 168 && this.entity.attackTicks < 260
                || this.entity.attackTicks > 275 && this.entity.attackTicks < 290) {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
                this.entity.lookAt(target, 30.0F, 30.0F);
            }
        } else {
            // 冻结朝向：这一 tick 身体不转。跳起 / 落地那些段落靠它保住姿势。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    /**
     * 原版返回的是 {@code false}，这里照抄。
     *
     * <p>不过要说明白：这个返回值在本项目里<b>其实不起作用</b>。
     * vanilla 的 {@code GoalSelector.tick()} 末尾调的是 {@code tickRunningGoals(true)}，
     * 那个写死的 {@code true} 会让 {@code p_186082_ || requiresUpdateEveryTick()}
     * 直接短路为真（1.20.1 {@code GoalSelector.java}）。所以 {@link #tick()} 照样每 tick 都跑。
     *
     * <p>那为什么还照抄 false 而不是改 true：这个值将来要是被别处读取，
     * 改了就跟原版对不上了。保持原样最省心。
     */
    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
