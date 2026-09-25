package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 大 Boss 级仆从的基类，对应传奇怪物（Legendary Monsters）原版的 IAnimatedBoss。
 *
 * <p>目前保留三样东西：<b>限伤</b>（{@link #damageCap()}）、<b>效果免疫</b>（{@link #addEffect}）、
 * <b>动态减伤</b>（{@link #hurt}）。
 *
 * <p>原版 IAnimatedBoss 的下列机制已按需求裁掉，不要往这里加回来：
 * 出生点 / 传送回家、脱战回血、休眠免疫、高低差保护、拆方块、Boss 血条、Boss 音乐。
 *
 * <h2>「动态减伤」到底是什么</h2>
 * 原版叫 damage adaptation（伤害适应），一句话：<b>短时间内连续挨打，从第二下开始伤害减半</b>。
 * 它挡的是「爆发伤害」，一个拿满附魔剑的玩家一秒砍十几刀也没法秒掉 Boss。
 * 拆开看是三个计时器：
 *
 * <pre>
 * damageTimeFactor      100 = 完全没适应。每挨一下 -adaptationFactor()（原版 10，圣骑覆写成 20）。
 *                       30 tick 内没再挨打就弹回 100。
 * damageAdaptationTicks 每次挨打重置成 30，归零时把上面那个拉回 100。
 * reducedDamageTicks    从 100 开始每 tick 减一，减到 0 就停 —— 它是「减伤力度」的刻度尺，
 *                       见下面 {@link #hurt} 里那条乘法。
 * </pre>
 *
 * <p>判定「进入减伤」的条件是 {@link #reducedDamage()}：{@code damageTime() < 100}。
 * 也就是说<b>只要 30 tick 内挨过一下</b>，后续伤害就开始打折。
 * 圣骑 5 下之内就能把 damageTimeFactor 打到 0（100 / 20），进入最深的减伤。
 *
 * <p>⚠️ 原版还有两个方法 {@code damageReductionSystem()} / {@code damageAdaptationSystem()}，
 * 查过字节码：<b>全项目没有任何地方调用它们</b>，是作者留下的死代码（子类覆写成 true 也不生效）。
 * 所以这里不搬。
 *
 * <p>⚠️ 原版同一段 {@code hurt()} 里还有一条「防偷鸡」判据：目标离自己超过
 * {@code antiCheeseDistance()}（15 格）时完全免伤。那个是给「玩家放风筝打 Boss」设计的，
 * 搬到一个<b>玩家的仆从</b>身上会变成「敌人站在远处就拿它没办法」的漏洞，所以<b>故意没搬</b>。
 */
public class IAnimatedBossServant extends IAnimatedMonsterServant {

    /** 挨打后多久算「这一轮结束」，到点把伤害适应弹回 100。原版 30 tick。 */
    public static final int DAMAGE_ADAPTATION_TICKS = 30;

    /** 减伤力度的刻度尺总长。从 100 每 tick 减一，减到 0 就停在「最深减伤」。原版 100 tick。 */
    public static final int REDUCED_DAMAGE_TICKS = 100;

    /** 减伤最深能打到几折。原版 0.5，也就是最多减一半。 */
    public static final float MIN_DAMAGE_REDUCTION = 0.5F;

    /** 伤害适应的进度条，100 = 没适应。 */
    public int damageTimeFactor = 100;
    /** 距离上次挨打过了多少 tick。归零时把 {@link #damageTimeFactor} 拉回 100。 */
    public int damageAdaptationTicks = DAMAGE_ADAPTATION_TICKS;
    /** 减伤力度的刻度尺，见 {@link #hurt}。 */
    public int reducedDamageTicks = REDUCED_DAMAGE_TICKS;

    public IAnimatedBossServant(EntityType entity, Level world) {
        super(entity, world);
    }

    /** 单次伤害上限。子类覆写成自己的配置项。 */
    public double damageCap() {
        return 21.0D;
    }

    /**
     * 每挨一下，伤害适应进度往前推多少。原版默认 10，圣骑覆写成 20（更快进入减伤）。
     */
    public int adaptationFactor() {
        return 10;
    }

    /**
     * 减伤力度。原版写法是 {@code Math.max(20, damageTimeFactor)} —— 下限 20 是防止它变成负数。
     * 返回值 &lt; 100 就算「已进入减伤」，见 {@link #reducedDamage()}。
     */
    public int damageTime() {
        return Math.max(20, this.damageTimeFactor);
    }

    /** 当前是否处于减伤状态。 */
    public boolean reducedDamage() {
        return this.damageTime() < 100;
    }

    /**
     * 百分数 → 小数。原版 {@code toPercent(100) = 1.0}、{@code toPercent(50) = 0.5}。
     */
    private static float toPercent(float percentage) {
        return percentage * 0.01F;
    }

    @Override
    public void tick() {
        super.tick();
        // 原版这三个计时器是每 tick 无条件推进的（客户端也推），照抄。
        if (this.reducedDamageTicks > 0) {
            --this.reducedDamageTicks;
        }
        if (this.damageAdaptationTicks > 0) {
            --this.damageAdaptationTicks;
        }
        if (this.damageAdaptationTicks == 0) {
            this.damageTimeFactor = 100;
        }
    }

    /**
     * 受击总入口，逐条对应原版 {@code IAnimatedBoss.hurt()}：
     *
     * <ol>
     *   <li><b>无视无敌的伤害原样放过</b>（{@code /kill}、虚空伤害这类），连限伤都不吃。</li>
     *   <li><b>摔落 / 溺水 / 火焰 / 冰冻完全免伤。</b>注意火焰在这里挡一道，
     *       {@code fireImmune()} 在 {@code LivingEntity.hurt} 里再挡一道 —— 两道都要有：
     *       前者挡的是「火焰伤害」，后者挡的是「身上着火」。</li>
     *   <li><b>动态减伤</b>：已进入减伤状态时，乘上一个系数 ——
     *       刻度尺满格（{@code reducedDamageTicks = 100}）时是 1.0（等于没减），
     *       掉到 50 以下就锁死在 0.5。所以减伤是「越打越硬」，而不是一挨打就砍半。</li>
     *   <li>挨打成功后才推进伤害适应 —— 被格挡 / 被免疫的那些不算数。</li>
     * </ol>
     *
     * <p><b>限伤（{@link #damageCap()}）就写在本方法里，紧跟在动态减伤后面</b>，
     * 顺序是「先减后限」：先乘减伤系数，再 {@code Math.min(amount, damageCap())}。
     * 这两步走完之后才交给 {@code super.hurt}，由它去扣护甲和抗性。
     * 所以限的是<b>护甲之前</b>的伤害 —— 一记 100 点的重击，先被减伤砍到 50、
     * 再被限到 21，最后才过护甲，最终掉的血只会比 21 更少，上限是硬的。
     *
     * <p>⚠️ 这里踩过一个坑：限伤曾经写在 {@code actuallyHurt} 里，当时的想法是
     * 「那里 amount 已经算完护甲，才是真正掉的血，限在那儿更准」。结果恰好相反 ——
     * {@code actuallyHurt} 拿到的 amount 早就被护甲削过一轮了（100 → 减伤 50 → 护甲 15），
     * 再去 {@code min(15, 21)} 还是 15，上限 21 根本碰不到，<b>限伤形同虚设</b>。
     * LM 原版 {@code IAnimatedBoss.hurt()} 是把 {@code Math.min(damageCap, amount)}
     * 写在方法最前面的，现在按那个位置搬回来。
     *
     * <p>注意最上面那条 {@code BYPASSES_INVULNERABILITY} 分支直接就 return 了，
     * 所以 {@code /kill}、虚空伤害这类依然连限伤都不吃，和原版一致。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }

        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)
                || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.FREEZE)) {
            return false;
        }

        if (this.reducedDamage()) {
            // 原版这里夹的是 (0.5, amount)：amount 当上限是原作者的写法，
            // 效果是「伤害本来就低于 0.5 时不减」，原样保留。
            amount *= Mth.clamp(toPercent((float) this.reducedDamageTicks),
                    MIN_DAMAGE_REDUCTION, amount);
        }

        // 限伤。位置在减伤之后、护甲之前 —— 也就是「先减后限」。
        amount = (float) Math.min((double) amount, this.damageCap());

        boolean hurt1 = super.hurt(source, amount);
        if (hurt1) {
            this.damageAdaptationTicks = DAMAGE_ADAPTATION_TICKS;
            this.damageTimeFactor -= this.adaptationFactor();
        }
        return hurt1;
    }

    /**
     * 效果免疫：<b>只免疫负面效果（debuff）</b>，正面效果照常吃。
     *
     * <p>判断用的是 {@link MobEffectCategory#HARMFUL}，而不是
     * {@code MobEffect.isBeneficial()}。这两个看着差不多，实际差一档：
     * 原版把效果分三类 —— {@code BENEFICIAL}（治疗、力量、迅捷）、
     * {@code HARMFUL}（中毒、凋零、缓慢）、{@code NEUTRAL}（不祥之兆这类不好不坏的）。
     * {@code isBeneficial()} 只对第一类返回 true，拿它当条件会把第三类也一起挡在门外。
     * 既然要的是「免疫 debuff」，那就只挡 {@code HARMFUL}，另外两类都放行，语义最干净。
     *
     * <p>注意用的是 addEffect（Forge 的真钩子），不是 LM 原版的 canApplyMobEffect
     * —— 那个方法在 Forge 里压根不存在，是个永远不生效的死方法。
     *
     * <p>这个限制不影响本类自己打控制：圣骑给敌人上眩晕走的是
     * {@code 受害者.addEffect(...)}，进的是受害者的 addEffect，不会被这里拦下。
     */
    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, Entity pEntity) {
        if (pEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            return false;
        }
        return super.addEffect(pEffectInstance, pEntity);
    }

    // ==========================================================================================
    // 下面 4 个方法是招式的「通用零件」，原本住在爷爷类 IAnimatedMonsterServant 里。
    //
    // ⚠️ 2026-09-25 合并 origin/master 时，协作者那边的 IAnimatedMonsterServant 把它们连同
    //    applyEffectTo / sendBasicHotBarMessage / yaw / pitch / repelEntities 一起删掉了 ——
    //    他那边没有圣骑仆从，在他眼里这些都是没人调用的死代码。
    //    而本地从没动过那个文件，所以 git 静默接受了他的删除，一个冲突都不报，
    //    一路等到编译才炸出 22 个「找不到符号」。
    //
    //    因此把它们搬进本类：IAnimatedBossServant 这个文件<b>只存在于我们这边</b>，
    //    协作者没有，以后合并不可能再被连带删掉。
    //
    //    方法体逐字照抄祖先版本，一行逻辑都没改，避免顺手「优化」二次引入 bug。
    //    只有圣骑仆从（PossessedPaladinServant）在用这四个东西。
    // ==========================================================================================

    /**
     * 在半径 {@code PlayerRange} 格内的<b>每个玩家</b>的动作栏上显示一句话。
     *
     * <p>和爷爷类里那个 {@code sendBasicHotBarMessage} 的区别：那个只发给某一个指定玩家，
     * 这个是在范围内广播 —— 圣骑说台词时周围的人都听得到。
     *
     * <p>{@code message} 传的是<b>翻译键</b>（如 {@code message.goetyominous.possessed_paladin_servant.awaken.1}），
     * 走 {@link Component#translatable} 查语言文件，所以中英文会自动切。
     */
    public void sendAdvancedHotBarMessage(String message, ChatFormatting chatFormatting, float PlayerRange) {
        List<Player> list = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(PlayerRange));
        for (Player player : list) {
            Component messageComponent = Component.translatable(message).withStyle(chatFormatting);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(messageComponent));
            }
        }
    }

    /**
     * 朝<b>当前朝向的侧后方</b>冲一下（冲刺位移）。
     *
     * <p>{@code vec} 是前后分量、{@code offset} 是左右分量，两者都用 {@code yBodyRot}（身体朝向）
     * 换算成世界坐标 —— 所以传正数往前、传负数往后（圣骑的某些招式就是靠这个「先后撤再前冲」）。
     *
     * <p>{@code Vscale} 是力度缩放：先算出「目标落点相对自己的位移」，再乘这个系数当作速度。
     * 注意只改 X/Z，<b>Y 轴速度原样保留</b>，所以跳起来用也不会把人往地里按。
     *
     * <p>{@code livingEntity} 参数由调用方传 {@code this} —— 是 LM 原版的写法，照抄没动。
     */
    public void advancedDash(LivingEntity livingEntity, float vec, float offset, float Vscale) {
        float f = Mth.cos(livingEntity.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(livingEntity.yBodyRot * ((float) Math.PI / 180F));
        double theta = (livingEntity.yBodyRot) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        Vec3 rollPos = new Vec3(livingEntity.getX() + vec * vecX + f * offset, getY(), livingEntity.getZ() + vec * vecZ + f1 * offset);
        Vec3 sub = position().subtract(rollPos);
        Vec3 finalPos = sub.scale(Vscale);
        setDeltaMovement(finalPos.x, getDeltaMovement().y, finalPos.z);
    }

    /**
     * 给目标「叠层」上 debuff：没说有就上 0 级，已经有了就在原等级上 +{@code bonusLevel}，
     * 但封顶在 {@code maxLevel}。
     *
     * <p>圣骑用它给被砍中的敌人叠「灵魂碎裂」（{@code ModEffects.SOUL_FRACTURE}）：
     * 砍得越多层数越高，最高 {@code maxLevel} 层。
     * 幻影匕首（{@code ThrownPhantomDagger}）命中也叠同一个效果 ——
     * 那边调的是 LM 的 {@code EntityUtil.applyStackingEffect}，逻辑和这里这份一模一样。
     *
     * <p>⚠️ 中间那个 {@code else if (!(A && B))} 写法很啰嗦（其实就是 {@code else}），
     * 是 LM 原版这么写的，<b>照抄没动</b>。两者等价，不要顺手简化。
     */
    public void applyStackingEffect(LivingEntity entity, MobEffect effect, int bonusLevel, int maxLevel, int duration) {

        MobEffectInstance effectInstance = entity.getEffect(effect);

        if (entity.hasEffect(effect) && effectInstance != null) {

            int effectLevel = effectInstance.getAmplifier();
            if (effectLevel < maxLevel) {
                entity.addEffect(new MobEffectInstance(effect, duration, effectLevel + bonusLevel));
            }
        } else if (!(entity.hasEffect(effect) && effectInstance != null)) {
            entity.addEffect(new MobEffectInstance(effect, duration, 0));
        }
    }

    /**
     * 朝一个<b>坐标</b>冲，而不是朝朝向冲 —— 这是和 {@code calculatedDash}（朝目标冲）的区别。
     *
     * <p>圣骑的突进类招式用的是这个：先记下目标当时的位置，之后哪怕目标跑了，
     * 也是照着<b>原来那个点</b>冲过去，不会中途拐弯。
     *
     * <p>外面的 {@code if (target != null)} 是 LM 原版就有的 —— 没锁定目标就<b>完全不位移</b>。
     * 看着像多余，其实是有意的（防止脱战状态下乱冲），照抄保留。
     */
    public void calculatedDashToPositon(float Multiplier, Vec3 position) {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.setDeltaMovement((position.x - this.getX()) * Multiplier, 0, (position.z - this.getZ()) * Multiplier);
        }
    }
}
