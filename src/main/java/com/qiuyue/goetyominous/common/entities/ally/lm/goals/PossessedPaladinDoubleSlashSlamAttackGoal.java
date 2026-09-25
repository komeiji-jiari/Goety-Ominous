package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;

/**
 * 堕落圣骑仆从的「二连斩砸」goal（状态 3，对应原版 {@code DoubleSlashSlamAttackGoal}）。
 *
 * <p>招式本身就是「二连斩 + 再来一下砸地」：第一刀（tick 20）、第二刀（tick 36）、
 * 第三下跳劈砸地（tick 62）。伤害判定写在实体 {@code UpdateWithAttack()} 的状态 3 分支里。
 *
 * <h2>为什么用继承而不是照抄一份</h2>
 * 原版把 {@code DoubleSlashSlamAttackGoal} 和 {@code P_PAttackGoal} 写成了<b>两个平级的类</b>，
 * 代码几乎一模一样。逐行比对过，两者的 {@code canUse / canContinueToUse / tick / canBeAlerted}
 * <b>完全相同</b>，真正有区别的只有两处：
 * <ol>
 *   <li>{@link #start()} 多一句 {@code shouldAttackMore = false}（清掉上一招留下的"还想再打"标记）；</li>
 *   <li>{@link #stop()} 换成了自己的一套「收招去哪」逻辑，而且<b>完全不问</b> {@code canBeAlerted()}。</li>
 * </ol>
 * 所以这里改成继承 {@link PossessedPaladinAttackGoal}，只覆写这两个方法。
 * 好处是 {@code tick()} 之类的公共逻辑<b>只有一份</b> —— 原版那种复制粘贴的写法，
 * 一旦要修 bug 就得记得改两处，漏一处就是隐蔽的行为不一致。
 * 代价是和反编译源码对照时不能逐行看，所以这段说明留在这里。
 *
 * <h2>⚠️ 一阶段下 stop() 永远收在状态 7</h2>
 * 原版这行三元表达式里塞了两个 {@code getPhase() < 2}：
 * <pre>
 * (!targetIsNotNull() || getPhase() &lt; 2 || !(dist &gt;= 6)) &amp;&amp; (!shouldAttackMore || getPhase() &lt; 2) ? 7 : 8
 * </pre>
 * 二阶段没做之前 {@code getPhase()} 恒为 1，于是两个括号里各有一个 {@code getPhase() < 2} 直接为真，
 * <b>整个条件恒真 → 永远切到状态 7（近身收招）</b>，状态 8（转身横扫）这一支在一阶段根本走不到。
 *
 * <p>这看着像原版手滑，但<b>照抄不改</b>：这是原版在一阶段的真实行为，改了就等于和原作不一样。
 * 表达式原样保留还有另一个好处 —— 等 Stage 2D 做好二阶段、{@code getPhase()} 会返回 2 时，
 * 这段逻辑自动就对了，不用回头再改。
 *
 * <p>顺带一提，短路求值在这里很关键：一阶段 {@code distanceTo(target())} 那一项
 * <b>根本不会被求值</b>（前面的 {@code getPhase() < 2} 已经短路了），所以不会因为
 * target 为 null 而炸。等二阶段生效后这个隐患才会露出来 —— 那是原版自己的问题，到时候再说。
 */
public class PossessedPaladinDoubleSlashSlamAttackGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinDoubleSlashSlamAttackGoal(PossessedPaladinServant entity, int getattackstate,
                                                     int attackstate, int attackendstate, int attackMaxtick,
                                                     int attackseetick, float attackrange,
                                                     boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    @Override
    public void start() {
        // 比父类多这一句：清掉"还想再打一下"的标记。
        // 这个标记是砸地第二下（状态 8）的伤害分支置上的，用来表示"打中了，可以接续招"。
        // 起新招前必须先清掉，否则上一招的残留会让这一招的收招判断走错分支。
        this.entity.shouldAttackMore = false;
        super.start();
    }

    @Override
    public void stop() {
        // 注意：这里<b>没有</b>调用 canBeAlerted()，所以这一招收招时不会转进状态 9（警觉）。
        // 顺带也就少消耗一次随机数 —— 和父类的 stop() 随机序列不一样，这不是 bug。
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                (!this.entity.targetIsNotNull()
                        || this.entity.getPhase() < 2
                        || !(this.entity.distanceTo(this.entity.target()) >= 6.0F))
                        && (!this.entity.shouldAttackMore || this.entity.getPhase() < 2)
                        ? 7 : 8);
        this.entity.attackCooldown = 0;
        // 同样注意：不在这里清零 attackTicks。接手的状态 goal 会在自己的 stop() 里清。
        // 详见 PossessedPaladinAttackGoal.stop() 的注释。
    }
}
