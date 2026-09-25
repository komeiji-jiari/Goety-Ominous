package com.qiuyue.goetyominous.common.init.lm;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * 传奇怪物那批仆从用到的自定义伤害类型。
 *
 * <h2>伤害类型（DamageType）是什么</h2>
 * 每次 {@code hurt()} 都要带一个「伤害类型」，它决定三件事：
 * <b>死亡消息怎么显示</b>（「xx 被 xx 杀死了」还是「xx 被 xx 射死了」）、
 * <b>难度会不会削减它</b>、以及<b>会不会被护甲挡住</b>。
 *
 * <p>「会不会被护甲挡住」不是写在这里的，而是写在
 * {@code data/minecraft/tags/damage_type/bypasses_armor.json} 里 ——
 * 那个文件里列到的伤害类型才无视护甲。<b>两个文件要一起改，只加一个会静默失效：
 * 伤害照常打得出来，只是被护甲悄悄吃掉一截。</b>
 *
 * <p>另外注册伤害类型<b>不需要</b>像实体那样写 {@code DeferredRegister} ——
 * 它是由数据包（{@code data/} 目录下的 json）直接提供的，所以这里只有一个
 * 「名字的钥匙」，没有注册代码。
 */
public class LmDamageTypes {

    /**
     * 灵魂伤害 —— 圣骑的匕首、灵魂弹、三叉戟统一用的那个。
     *
     * <p>对应数据包里的 {@code data/goetyominous/damage_type/servant_ghost.json}。
     * ⚠️ 这两个字符串必须一模一样，改一个就得改另一个，否则游戏会在读取时直接崩
     * （不是静默失效，是崩 —— 崩了反而是好事，至少你知道自己写错了）。
     */
    public static final ResourceKey<DamageType> SERVANT_GHOST =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation(GoetyOminous.MOD_ID, "servant_ghost"));

    /**
     * 造一个「灵魂伤害」的 DamageSource，算在 {@code attacker} 头上。
     *
     * <p>两个参数都传同一个实体是有意为之：{@code DamageSource} 的
     * {@code (直接来源, 幕后主使)} 在这里是同一个（就是圣骑本人），
     * 所以击杀归属、仇恨转移这些都正常。原版也是这么传的。
     *
     * @param attacker 造成伤害的生物，同时充当「直接来源」和「幕后主使」。
     */
    public static DamageSource ghostly(LivingEntity attacker) {
        Level level = attacker.level();
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SERVANT_GHOST),
                attacker, attacker);
    }

    /**
     * 同上，但<b>允许没有主人</b>。
     *
     * <p>用途是「招式打出去了，主人却已经没了」这种边角情况 ——
     * 主人退出游戏、被卸载、或者存盘读回来时还没加载。这时候照样得有伤害，
     * 只是没有击杀归属、也不回血。
     *
     * <p>这时造出来的 {@code DamageSource} 是<b>没有攻击者</b>的那一种，
     * 和原版传 {@code null} 进去的结果一致。
     *
     * @param attacker 造成伤害的生物，可以为 {@code null}
     */
    public static DamageSource ghostlyOrAttackerless(Level level, @Nullable Entity attacker) {
        if (attacker instanceof LivingEntity living) {
            return ghostly(living);
        }
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SERVANT_GHOST));
    }
}
