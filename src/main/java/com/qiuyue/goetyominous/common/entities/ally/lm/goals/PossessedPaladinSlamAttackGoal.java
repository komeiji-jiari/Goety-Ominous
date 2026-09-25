package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 堕落圣骑仆从的「砸地」起手（attackState 6）。
 *
 * <p><b>先说清楚这 14 招到底是怎么挑招的</b>，不然这个类看着会很奇怪：
 * 每个招式 goal 的 {@code canUse()} 条件都长得差不多 —— 待机中（attackState 为 0）、
 * 目标在攻击距离内、没有硬直。也就是说待机时 14 个 goal <b>同时</b>满足条件。
 * 「这一下到底出哪招」由两样东西决定：
 * <ol>
 *   <li><b>优先级</b>：全部注册在 0 或 1，数值小的先被 vanilla 的 GoalSelector 选中；</li>
 *   <li><b>每 tick 掷骰</b>：就是本类 {@code canUse()} 里那句
 *       {@code getRandom().nextFloat() * 100 < slamRandom}。值 10 表示每次判定有 10% 通过，
 *       平均下来大约每 10 tick 掷中一次。</li>
 * </ol>
 *
 * <p>所以「招式概率」并不在 goal 的构造参数里。原版是在 {@code registerGoals()} 里
 * 用<b>匿名子类</b>把掷骰和冷却设置补上去的。这里因为砸地只有一招、不需要复用，
 * 就直接把那段匿名子类的内容并进本类了。
 *
 * <p>原版的注册写法（{@code PossessedPaladinEntity.java:602}）：
 * <pre>
 * goalSelector.addGoal(1, new SlamAttackGoal(this, 0, 6, 0, toTicks(1.54F), 20, 7.0F, false, 10.0F) { ... });
 * </pre>
 * 对应过来就是 (getattackstate=0, attackstate=6, attackendstate=0,
 * attackMaxtick=30 tick, attackseetick=20, attackrange=7.0F)。
 */
public class PossessedPaladinSlamAttackGoal extends Goal {

    protected final PossessedPaladinServant entity;
    /** 满足这个状态时才有资格起手。0 = 待机。 */
    private final int getattackstate;
    /** 起手后切到的状态。6 = 砸地。 */
    private final int attackstate;
    /** 招式结束时切回的状态。被 {@link #stop()} 里的分支覆盖，基本用不上。 */
    private final int attackendstate;
    /** 这招最多持续多少 tick。超过就结束。 */
    private final int attackMaxtick;
    /** 前多少 tick 还继续盯着目标看，之后锁死朝向。 */
    private final int attackseetick;
    /** 触发距离。比这个远就不起手。 */
    private final float attackrange;

    public PossessedPaladinSlamAttackGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                          int attackendstate, int attackMaxtick, int attackseetick,
                                          float attackrange) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0
                // 下面两句原本写在注册处的匿名子类里，见类注释。
                && this.entity.getRandom().nextFloat() * 100.0F < (float) this.entity.slamRandom
                && this.entity.slam_cooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void start() {
        // 起手先清掉「打中了没」的标记，这一轮的判定从现在才开始记。
        this.entity.shouldAttackMore = false;
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            // 原版这里用的是 yRotO（上一 tick 的朝向），不是 yBodyRot。
            // 效果是「出招后半段把朝向冻结住」，而不是对齐身体。别改成 yBodyRot。
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public void stop() {
        // 设冷却。原版写在注册处的匿名子类 stop() 里，这里并进来。
        this.entity.slam_cooldown = this.entity.SLAM_COOLDOWN;
        this.entity.randomizeAttacks();

        // 收招之后接哪一招。原版这一行是：
        //   (!targetIsNotNull() || !(distanceTo(target()) >= 6.0F)) && !shouldAttackMore ? 7 : 8
        // 化简一下就是：近身且没打到人 → 7（收招）；否则（离得远，或者打中了）→ 8（续招）。
        // 这里保持原版写法不化简，省得化简时手滑改了语义。
        // 注意 || 的短路是必须的：target() 可能为 null，distanceTo(null) 会 NPE。
        boolean endState = !this.entity.targetIsNotNull()
                || !(this.entity.distanceTo(this.entity.target()) >= 6.0F);
        if (this.entity.getPhase() <= 1) {
            this.entity.setAttackState(endState && !this.entity.shouldAttackMore ? 7 : 8);
        } else {
            // 二阶段把 8 换成 31（多一段格挡收招）。
            this.entity.setAttackState(endState && !this.entity.shouldAttackMore ? 7 : 31);
        }

        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
