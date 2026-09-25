package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;

/**
 * 堕落圣骑仆从的「剑气斩」goal（对应原版 {@code SlashFromGoal}）。
 *
 * <p>招式本体很短：冲刺过去横斩一刀（第 15 tick 挥剑、第 18 tick 判定），
 * 但它真正的分量在<b>收招之后接什么</b> —— 这一招是整套连段的「岔路口」。
 *
 * <h2>它管三个状态</h2>
 * <ul>
 *   <li><b>状态 16</b>（本类起手）：剑气斩本体，20 tick。第 18 tick 用扇形判定，命中会置
 *       {@code hasHurt}；</li>
 *   <li><b>状态 18</b>：长收招，50 tick。它<b>有</b>独立的突刺判定（第 18 tick 直线突刺）
 *       —— 又一处「看着像过场、其实是主攻」，见实体 {@code UpdateWithAttack} 的状态 18 分支；</li>
 *   <li><b>状态 17</b>：短收招，30 tick。<b>没有</b>任何伤害判定，纯动画。</li>
 * </ul>
 * 后两个由 {@link #stop()} 切过去，再由两条 {@link PossessedPaladinStateGoal}（优先级 0）认领。
 * <b>两条注册的 stop() 都要设 {@code slash_from_cooldown}</b>，漏一条就会走另一条支线时没有冷却。
 *
 * <h2>为什么用继承而不是照抄一份</h2>
 * 和 {@link PossessedPaladinDoubleSlashSlamAttackGoal} 一样的理由：原版 {@code SlashFromGoal}
 * 与 {@code P_PAttackGoal} 是平级类，但逐行比对下来
 * {@code canUse / start / canBeAlerted / canContinueToUse / tick / requiresUpdateEveryTick}
 * <b>全部相同</b>，只有 {@link #stop()} 不一样（连构造参数都是同一套）。所以只覆写这一个方法。
 *
 * <h2>⚠️ stop() 里那个嵌套掷骰，两层都溢出</h2>
 * <pre>
 * nextInt() * 100 &gt;= 50 &amp;&amp; !hasHurt
 *     ? (nextInt() * 100 &lt; 70 ? 18 : 17)
 *     : (stab_grab_cooldown &lt;= 0 ? 19 : 18)
 * </pre>
 * {@code nextInt()} 无参，乘 100 溢出成负数，于是：
 * <ul>
 *   <li>外层 {@code 负数 >= 50} 恒假 → <b>几乎总是走 else 那一支</b>；</li>
 *   <li>else 支：突刺抓取的冷却好了就接 <b>19（突刺抓取）</b>，没好就接 <b>18</b>；</li>
 *   <li>内层（极少走到）{@code 负数 < 70} 恒真 → 接 18，17 才是真正罕见的那一档。</li>
 * </ul>
 * 也就是说实战里这一招<b>基本等于「突刺抓取的前置」</b>。看着像 bug，但这是原版行为，照抄不改 ——
 * 想「修好」它反而会让圣骑的连段节奏跟原作不一样。
 *
 * <p>⚠️ 这个 {@code hasHurt} 判断还有一层：它读的是<b>上一次</b> {@link #stop()} 之后没人清过的值。
 * 原版是在读完之后的下一行才把它清成 false，顺序不能反。
 */
public class PossessedPaladinSlashFromGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinSlashFromGoal(PossessedPaladinServant entity, int getattackstate,
                                         int attackstate, int attackendstate, int attackMaxtick,
                                         int attackseetick, float attackrange,
                                         boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    /**
     * 收招：三层判断决定接哪一招。机制见类注释里那段「两层都溢出」。
     *
     * <p>注意这里<b>没有</b>调用 {@code canBeAlerted()} —— 所以这一招收尾不会转进警觉状态（9），
     * 也就比父类少消耗一次随机数。这不是 bug。
     *
     * <p>注意 {@code hasHurt = false} 必须写在 {@code setAttackState} <b>之后</b>：
     * 那一行要读它，先清就等于永远读到 false，连段会永远走同一支。
     */
    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                this.entity.getRandom().nextInt() * 100 >= 50 && !this.entity.hasHurt
                        ? (this.entity.getRandom().nextInt() * 100 < 70 ? 18 : 17)
                        : (this.entity.stab_grab_cooldown <= 0 ? 19 : 18));
        this.entity.hasHurt = false;
        this.entity.attackCooldown = 0;
        // 同样注意：不在这里清零 attackTicks。接手的状态 goal 会在自己的 stop() 里清。
    }
}
