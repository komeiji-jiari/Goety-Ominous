package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「二阶段变身」goal（对应原版 {@code P_PSecondPhaseStateGoal}）。
 *
 * <p>它只认领<b>状态 26</b>：血量掉到 65% 以下时，圣骑原地站住演一整段 7.35 秒的变身，
 * 中途正式切进二阶段，收尾放一圈灵魂弹。
 *
 * <h2>变身是怎么被触发的</h2>
 * 原版全项目里<b>没有任何地方直接调用 {@code setPhase(2)}</b> —— 那一次切换就藏在本类的
 * {@link #tick()} 里。触发链条是：
 * <pre>
 * 血量 &lt; 65%（crackiness 到 MEDIUM）
 *   → {@link #canUse()} 通过 → {@link #start()} 把状态切成 26，变身动画开始
 *   → 第 49 tick → <b>setPhase(2)</b>   ← 真正的阶段切换在这一刻
 *   → 第 147 tick → {@link #stop()} 把状态切回 0，可以重新出招
 * </pre>
 * 之所以把切阶段放在动画<b>中间</b>而不是 {@code start()}，是为了让「撕开盔甲露出红光」
 * 那一下和贴图/粒子变红同时发生 —— 太早切，玩家会在动画开头就看到二阶段的样子。
 *
 * <h2>⚠️ 第 49 tick 那一句绝对不能省</h2>
 * 漏掉它会出大问题，而且症状很怪：{@code stop()} 之后状态回到 0，
 * {@link #canUse()} 又会立刻通过（血量还是低于 65%、阶段还停在 1），于是<b>再次变身</b>，
 * 147 tick 一轮、无限循环。而状态 26 期间实体是完全免伤的（见
 * {@code PossessedPaladinServant#hurt} 的第一层）—— 结果就是圣骑从此永久无敌、再也不出招。
 */
public class PossessedPaladinSecondPhaseGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时本类才接管。注册传的是 0（待机）。 */
    private final int getattackstate;
    /** 接管后要处于的状态。注册传 26。 */
    private final int attackstate;
    /** 收招要回的状态。注册传 0。 */
    protected final int attackendstate;
    /** 这招最多持续多少 tick。注册传 {@code toTicks(7.38F)} = 147。 */
    private final int attackfinaltick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。注册传 0 = 一开始就锁死。 */
    protected final int attackseetick;

    /** 第几 tick 正式切进二阶段。原版写死在 {@code tick()} 里，这里提成常量方便对照。 */
    public static final int PHASE_SWITCH_TICK = 49;

    public PossessedPaladinSecondPhaseGoal(PossessedPaladinServant entity, int getattackstate,
                                           int attackstate, int attackendstate,
                                           int attackfinaltick, int attackseetick) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    // 原版还有第二个构造函数，多一个 interruptsAI 参数（为 false 时干脆不设 flags）。
    // 圣骑只注册一处，用的是第一个构造函数，所以第二个不实现。

    /**
     * 起手条件：正闲着（状态 0），而且该变身了。
     *
     * <p>{@link PossessedPaladinServant#shouldEnterSecondPhase()} 里打包了原版写在这一行的
     * 两个条件 —— {@code getPhase() <= 1} 和 {@code crackiness 是 MEDIUM 或 HIGH}。
     *
     * <p>⚠️ <b>这里没有 {@code attackDelayTicks} 和距离检查</b>，和别的攻击 goal 不一样。
     * 变身不是「招式」，不需要目标在射程内、也不该被硬直挡住 —— 原版就是纯看血量的。
     * 圣骑被打到 65% 血的那一刻，无论它在干什么（只要状态是 0），都会立刻开始变身。
     */
    @Override
    public boolean canUse() {
        return this.entity.getAttackState() == this.getattackstate
                && this.entity.shouldEnterSecondPhase();
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
     * 收招。回状态 0，并把计时器清零给下一招用。
     *
     * <p>⚠️ 这里<b>不</b>重置阶段 —— 变身是不可逆的。原版唯一的「变回去」藏在
     * {@code PossessedPaladinEntity.tick()} 里：{@code ModConfig.MOB_CONFIG.canBossesResetPhases}
     * 打开时，脱战够久就把 {@code phase} 拨回 1，让玩家能重新打一遍完整的 BOSS 战。
     * 那是「副本 BOSS」的思路，本移植<b>故意没搬</b>，两个原因：
     * <ol>
     *   <li>它是为「玩家打不过跑掉、回头还能从头再打一次」设计的。仆从一直跟着主人，
     *       根本没有「重新挑战」这回事，掉回一阶段只会白白削弱自己的战斗力。</li>
     *   <li>真搬过来会变成一个死循环：血量一旦被修回 75% 以上就掉回一阶段，
     *       一挨打又立刻满足变身条件 → 再播一遍 7.35 秒的变身。而状态 26 期间是<b>完全免伤</b>的
     *       （见 {@code PossessedPaladinServant#hurt} 的第一层），
     *       等于圣骑每隔一会儿就白赚七秒无敌。</li>
     * </ol>
     * 第 2 条不是纸上谈兵：主人现在可以用诅咒金属块把二阶段圣骑的血修回 75% 以上
     * （见 {@code PossessedPaladinServant#mobInteract}），这条路径是真能走到的。
     */
    @Override
    public void stop() {
        this.entity.setAttackState(this.attackendstate);
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

        // 见类注释最后一段：这一句是变身能"结束"的唯一保证，少了就是无限变身 + 永久无敌。
        if (this.entity.attackTicks == PHASE_SWITCH_TICK) {
            this.entity.setPhase(PossessedPaladinServant.SECOND_PHASE);
        }
    }

    /** 变身每一 tick 都要精确，不能有延迟。 */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
