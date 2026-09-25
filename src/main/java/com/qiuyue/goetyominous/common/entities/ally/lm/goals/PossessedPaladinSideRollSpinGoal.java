package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「侧滚旋转」goal（对应原版 {@code SideRollSpinGoal}）。
 *
 * <p>它一次管两招 —— 状态 29 和状态 30，两者只有<b>翻滚方向相反</b>：
 * 一个往左滚，一个往右滚，滚完接同一个原地 360° 旋风斩。
 * 所以注册处写了两条，本类本身不带方向，方向写在实体
 * {@code UpdateWithAttack()} 的分支里。
 *
 * <h2>为什么不复用 {@link PossessedPaladinAttackGoal}</h2>
 * 两个类的 {@code canUse / start / stop / canContinueToUse} 确实一模一样，
 * 但 {@link #tick()} 不同 —— 而且是<b>反过来</b>的：
 * <ul>
 *   <li>{@code PossessedPaladinAttackGoal}：前 N tick 盯着目标，之后冻结朝向；</li>
 *   <li>本类：<b>翻滚期间（tick 0-15）和收尾期间（tick 40 之后）冻结朝向</b>，
 *       中间那段（16-39，也就是旋风斩真正挥剑的时候）才盯着目标。</li>
 * </ul>
 * 所以硬要复用就得在父类里塞开关，反而更难读，这里单独立一个类。
 *
 * <h2>关于 attackseetick 参数</h2>
 * 原版的 {@code tick()} 里并没有用这个字段，它写死了 15 / 40 两个界限
 * （注册时传进来的也是 0）。这里保留这个参数只是为了构造函数签名和原版一致，
 * 方便以后回头对照反编译源码 —— <b>它在原版里就是个死字段</b>。
 */
public class PossessedPaladinSideRollSpinGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时才有资格起手。0 = 待机。 */
    private final int getattackstate;
    /** 起手后切到的状态。29 = 向左滚，30 = 向右滚。 */
    private final int attackstate;
    /** 招式结束时切回的状态。被 {@link #canBeAlerted()} 的分支覆盖。 */
    private final int attackendstate;
    /** 这招最多持续多少 tick。 */
    private final int attackMaxtick;
    /**
     * 原版留给「前多少 tick 继续盯目标」的字段，但 {@link #tick()} 里从没读过它。
     * 保留仅为对齐原版签名，见类注释。
     */
    private final int attackseetick;
    /** 触发距离。 */
    private final float attackrange;
    /** 收招时有没有资格转进「警觉」状态（9）。 */
    private final boolean canAllertedState;
    /** 转进警觉状态的几率（百分数）。canAllertedState 为 false 时不生效。 */
    private final double allertedStateChance;

    public PossessedPaladinSideRollSpinGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
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

    // 原版还有第二个构造函数（多一个 interruptFlagTypes 参数用来自定义要抢占哪些行为标志）。
    // 圣骑的两处注册都没用它，所以不实现 —— 和 PossessedPaladinAttackGoal 的处理一致。

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    /**
     * 原版写法照抄。{@code nextInt()} 是<b>无参</b>的（返回整个 int 范围），乘 100 会溢出成负数，
     * 所以实际通过率约等于一半。机制详见 {@link PossessedPaladinAttackGoal#canBeAlerted()}。
     */
    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState
                && this.entity.targetIsNotNull()
                && this.entity.distanceTo(this.entity.target()) > 7.0F;
    }

    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    /**
     * 朝向控制，和原版逐行对照。
     *
     * <p>滚的那 15 tick 里必须冻结朝向（不然模型会一边滚一边扭头，很怪），
     * 旋风斩那 24 tick 里必须死死咬住目标（不然原地转圈根本打不中人），
     * 40 tick 之后收尾了，又冻结回原朝向。
     */
    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks >= 0
                && (this.entity.attackTicks <= 15 || this.entity.attackTicks >= 40 || target == null)) {
            // 冻结朝向。注意是 yRotO（上一 tick 的朝向），不是 yBodyRot —— 两者别互抄。
            this.entity.setYRot(this.entity.yRotO);
        } else {
            // 走到这里说明 target 一定不为 null：上面那个条件已经把 null 的情况接走了。
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
