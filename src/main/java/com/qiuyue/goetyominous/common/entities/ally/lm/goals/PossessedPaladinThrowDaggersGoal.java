package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;

/**
 * 堕落圣骑仆从的「投匕首」goal（对应原版 {@code ThrowDaggersGoal}）。
 *
 * <p>它管两招，区别只有「几阶段能用」和「扔几轮」：
 * <ul>
 *   <li><b>状态 15</b>（一阶段专用）：后退一步，扔 3 把匕首；</li>
 *   <li><b>状态 28</b>（二阶段专用）：扔三轮，共 10 把 —— 3 + 4 + 3。</li>
 * </ul>
 * 招式本体都写在实体 {@code UpdateWithAttack} 的状态 15 / 28 分支里。
 *
 * <h2>它和别的攻击 goal 唯一的区别：一条距离下限</h2>
 * 原版 {@code ThrowDaggersGoal} 与通用攻击 goal 是平级的两份拷贝，逐行比对下来
 * <b>只有 {@link #canUse()} 里多了一句 {@code distanceTo(target) > 7.0F}</b>。
 * 所以这里继承 {@link PossessedPaladinAttackGoal}，只覆写这一个方法。
 *
 * <p>那句「大于 7 格」是这招存在的意义 —— 圣骑的近招（剑气斩 5 格、盾击 6 格）都够不着远处时，
 * 才轮到它掏匕首。距离上限还是照旧由构造参数的 {@code attackrange}（12 格）把关。
 */
public class PossessedPaladinThrowDaggersGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinThrowDaggersGoal(PossessedPaladinServant entity, int getattackstate,
                                            int attackstate, int attackendstate, int attackMaxtick,
                                            int attackseetick, float attackrange,
                                            boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    /**
     * 起手条件 = 通用攻击的全部条件，再加一条「目标必须在 7 格开外」。
     *
     * <p>⚠️ 注意这条距离下限是和剑气斩 / 盾击的「互斥开关」：
     * 剑气斩要求 5 格内、盾击要求 6 格内、本招要求 7 格外，三者的距离区间完全不重叠。
     * 也就是说<b>同一个目标距离永远只可能有一招合法</b>，同优先级下谁排前面都无所谓。
     */
    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return super.canUse()
                && this.entity.distanceTo(target) > 7.0F;
    }
}
