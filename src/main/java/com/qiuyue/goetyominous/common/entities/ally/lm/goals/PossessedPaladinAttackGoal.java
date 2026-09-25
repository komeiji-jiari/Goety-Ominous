package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「通用攻击」goal（对应原版 {@code P_PAttackGoal}）。
 *
 * <p>这是 14 招里最能打的一个类 —— 原版用它一个就撑起了四招：
 * <ul>
 *   <li>状态 2：二连斩第一段</li>
 *   <li>状态 10：后空翻</li>
 *   <li>状态 25：盾击</li>
 *   <li>状态 38：掷三叉戟旋转（二阶段）</li>
 * </ul>
 * 四者的区别只有构造参数（时长、判定距离、是否可能转警觉），以及注册时补的那段掷骰条件。
 *
 * <h2>和砸地 goal 的关系</h2>
 * 流程几乎一样：待机（0）时掷骰命中 → {@link #start()} 切到招式状态 → 招式演完
 * {@link #canContinueToUse()} 返回 false → {@link #stop()} 决定下一站。
 * 区别在于本类的 {@link #stop()} 会先问一次 {@link #canBeAlerted()}：命中就切到 9（警觉），
 * 否则回 {@code attackendstate}（通常就是 0 待机）。
 */
public class PossessedPaladinAttackGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时才有资格起手。0 = 待机。 */
    private final int getattackstate;
    /** 起手后切到的状态。 */
    private final int attackstate;
    /** 招式结束时切回的状态。被 {@link #canBeAlerted()} 的分支覆盖。 */
    private final int attackendstate;
    /** 这招最多持续多少 tick。 */
    private final int attackMaxtick;
    /** 前多少 tick 继续盯着目标看，之后锁死朝向。 */
    private final int attackseetick;
    /** 触发距离。 */
    private final float attackrange;
    /** 收招时有没有资格转进「警觉」状态（9）。 */
    private final boolean canAllertedState;
    /** 转进警觉状态的几率（百分数）。canAllertedState 为 false 时不生效。 */
    private final double allertedStateChance;

    public PossessedPaladinAttackGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                      int attackendstate, int attackMaxtick, int attackseetick,
                                      float attackrange, boolean canAllertedState, double allertedStateChance) {
        this.entity = entity;
        this.canAllertedState = canAllertedState;
        this.allertedStateChance = allertedStateChance;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    // 原版还有第二个构造函数，多一个 interruptFlagTypes 参数用来自定义要抢占哪些行为标志。
    // 圣骑的 14 处注册一处都没用到它，所以这里不实现。

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
        // 注意：原版这里还有一句 !animationLockedForTests()，那是作者留的调试开关，
        // 恒返回 false，等于没有作用，移植时省掉。
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    /**
     * 原版写法照抄。{@code nextInt()} 是<b>无参</b>的（返回整个 int 范围），乘 100 会溢出成负数，
     * 负数恒小于 chance，所以实际通过率约等于一半。看着像 bug，但原版行为就是这样。
     */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    @Override
    public void stop() {
        // 重掷下一轮的二连斩类型 / 侧滚类型，这样下一招不会老是同一款。
        this.entity.randomizeAttacks();
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        this.entity.attackCooldown = 0;
        // 注意：这里<b>不</b>清零 attackTicks（砸地那个类也一样）。下一招的 attackTicks 由
        // 接手的状态 goal（PossessedPaladinStateGoal / PossessedPaladinAlertedGoal）在 stop() 里清。
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
            // 和砸地一致：冻结朝向，注意是 yRotO 不是 yBodyRot。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
