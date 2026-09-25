package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.minecraft.world.entity.LivingEntity;

/**
 * 仆从代码里反复用到的几个小换算 —— 说白了就是「除法」。
 *
 * <h2>为什么要有这个类</h2>
 * 这些方法原作是放在 {@code MathUtils} 里的。我们自己抄一份的原因很简单：
 * <b>它们一个字节的原作代码都不依赖</b>，纯粹是「乘个 0.01」「乘个 20」这种小学算术。
 * 为了这么三行去 import 别人的工具类，等于白白给自己拴上一根绳子。
 *
 * <p>工具类不需要被 new，所以构造器写成 {@code private} 挡掉 —— 这是 Java 的惯例写法，
 * 意思就是「这里只提供静态方法，别试着创建它的实例」。
 */
public final class ServantMath {

    private ServantMath() {
    }

    /**
     * 「按目标最大生命值的百分比」追加伤害。
     *
     * <p>用途是让招式对「血厚的单位」更疼 —— 打 100 血的僵尸和打 500 血的 BOSS，
     * 固定伤害的招式对后者几乎没感觉，这个函数就是补偿这件事的。
     *
     * @param percent 百分数，<b>不是</b>小数。传 {@code 3.0F} 表示「最大生命的 3%」。
     */
    public static float entityBasedHpDamage(LivingEntity entity, float percent) {
        return (float) (entity.getMaxHealth() * ((double) percent * 0.01));
    }

    /**
     * 秒 → tick。Minecraft 一秒 20 tick，所以就是乘 20。
     *
     * @param seconds 秒数，可以带小数。
     */
    public static int toTicks(float seconds) {
        return (int) (seconds * 20.0F);
    }

    /**
     * 百分数 → 系数。
     *
     * <p>⚠️ 名字叫「转换百分比」，但调用方往往传的是<b>别的</b>东西 ——
     * 比如 {@code ThrownPhantomDagger} 传的是目标的最大生命值本身
     * （{@code toPercent(target.getMaxHealth())}），实际效果是
     * 「每 100 点最大生命 → +1 点伤害」。原版就是这么写的，<b>照抄没动</b>，
     * 看到别顺手「修正」成除以 100 的直觉写法。
     */
    public static float toPercent(float percent) {
        return (float) ((double) percent * 0.01);
    }
}
