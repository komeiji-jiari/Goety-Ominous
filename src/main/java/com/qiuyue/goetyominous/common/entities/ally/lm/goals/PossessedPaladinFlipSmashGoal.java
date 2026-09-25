package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;

/**
 * 堕落圣骑仆从的「翻跟头砸」goal（对应原版 {@code FlipSmashGoal}）。
 *
 * <p>它是圣骑唯一的<b>远程起手</b>招：先原地翻个跟头，然后整个人朝目标扑过去，
 * 落地砸一记。所以 {@link #canUse()} 里多了一条「<b>至少离目标 5 格</b>」——
 * 贴脸时不放，专门用来拉近打不到的敌人。翻滚中它是唯一会变成「实体障碍」的时刻，
 * 见实体那边的 {@code canBeCollidedWith()}。
 *
 * <h2>它管三个状态，不是一个</h2>
 * <ul>
 *   <li><b>状态 12</b>（本类起手）：起跳 —— 第 6 tick 腾空扑向目标，第 16 tick 挥剑特效，
 *       第 19 tick 落地砸出伤害；</li>
 *   <li><b>状态 13</b>：收招变体之一，时长 20 tick；</li>
 *   <li><b>状态 14</b>：收招变体之二，时长 47 tick。</li>
 * </ul>
 * 后两个不是本类负责 —— {@link #stop()} 把状态切成 13 或 14 之后，就交给
 * {@link PossessedPaladinStateGoal} 认领了（那两条注册在优先级 0）。
 * <b>状态 13 / 14 共用同一个 {@code flip_smash_cooldown}</b>，两条注册的 stop() 都要写。
 *
 * <h2>为什么用继承而不是照抄一份</h2>
 * 和 {@link PossessedPaladinDoubleSlashSlamAttackGoal} 同理：原版把 {@code FlipSmashGoal}
 * 和 {@code P_PAttackGoal} 写成两个平级类，但逐行比对下来，{@code start / canContinueToUse /
 * tick / requiresUpdateEveryTick / canBeAlerted} <b>完全相同</b>，真正有区别的只有两处：
 * <ol>
 *   <li>{@link #canUse()} 多一条「距离 &gt; 5 格」；</li>
 *   <li>{@link #stop()} 换成了「随机二选一，切 13 还是 14」。</li>
 * </ol>
 * 所以改成继承，只覆写这两个方法 —— 公共逻辑只有一份，不会出现「改一处忘另一处」。
 *
 * <h2>⚠️ stop() 里那个掷骰子是溢出的</h2>
 * <pre>
 * getRandom().nextInt() * 100 &gt;= 50 &amp;&amp; !shouldAttackMore ? 13 : 14
 * </pre>
 * {@code nextInt()} 是<b>无参</b>的，返回整个 int 范围；乘 100 会溢出成负数，
 * 而负数 &gt;= 50 永远是 false —— 所以 <b>几乎总是走 14 那一边</b>，13 是罕见分支。
 * 和 {@link PossessedPaladinAttackGoal#canBeAlerted()} 是同一个套路（那边是「负数恒小于 chance」，
 * 所以那边几乎总是<b>命中</b>）。看着像 bug，但这是原版的真实行为，照抄不改。
 *
 * <p>顺带一提：{@link #stop()} 用掉的这一次随机数与父类不同 ——
 * 它<b>没有</b>走 {@code canBeAlerted()}，所以也<b>不会</b>转进状态 9（警觉）。
 */
public class PossessedPaladinFlipSmashGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinFlipSmashGoal(PossessedPaladinServant entity, int getattackstate,
                                         int attackstate, int attackendstate, int attackMaxtick,
                                         int attackseetick, float attackrange,
                                         boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    /**
     * 比父类多一条「离目标超过 5 格」。
     *
     * <p>父类的条件里已经有 {@code target != null}，但那是在父类的方法体里查的，
     * 本方法看不见，所以这里再取一次并判空 —— 正好也给下面那次 {@code distanceTo} 兜底。
     * 顺序上先判 {@code target != null} 再调 {@code super.canUse()}，只是因为前者更便宜。
     */
    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && super.canUse()
                && this.entity.distanceTo(target) > 5.0F;
    }

    /**
     * 收招：掷骰子决定接 13 还是 14。机制见类注释里那段「掷骰子是溢出的」。
     *
     * <p>注意这里<b>没有</b>清零 {@code attackTicks} —— 接手的状态 goal 会在自己的
     * {@code stop()} 里清，和父类的约定一致。
     *
     * <p>也注意这里不设冷却：{@code flip_smash_cooldown} 是状态 13 / 14 那两条
     * {@code PossessedPaladinStateGoal} 收尾时才设的，不是本类推的。
     */
    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                this.entity.getRandom().nextInt() * 100 >= 50 && !this.entity.shouldAttackMore ? 13 : 14);
        this.entity.attackCooldown = 0;
    }
}
