package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulTridentEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 堕落圣骑仆从扔出去的那把「灵魂三叉戟」（招 38 用）。
 *
 * <h2>它几乎全是原版的，我们只改了一行</h2>
 * 这个类<b>直接继承传奇怪物自己的 {@code SoulTridentEntity}</b>，一行行为都没抄。
 * 飞行、插地、命中掉灵魂柱、命中回血、叠「灵魂碎裂」——全部原样继承。
 *
 * <p>为什么不像匕首那样抄一份？因为<b>没有要改的行为</b>。对照一下：
 * {@link ThrownPhantomDagger} 抄一份是因为要换掉伤害和友军判定；
 * {@link SoulStrike} 抄一份是因为原版继承的是传奇怪物自己的飞行基类，我们不需要。
 * 这里两者都不成立 —— 见下。
 *
 * <h2>唯一改的一处：{@link #isAlliedTo(Entity)}</h2>
 * 原版 {@code SoulTridentEntity} 的友军判定写的是
 * <pre>
 * return pEntity.getType().is(ModEntityTags.POSSESSED_ARMOR_TEAM) || super.isAlliedTo(pEntity);
 * </pre>
 * 那个标签里装的是<b>传奇怪物自己的 Boss 和小弟</b>。我们的圣骑是仆从，
 * 主人是玩家、伙伴是别的仆从 —— 那个标签一个都不沾。照抄的后果是：
 * 三叉戟飞出去的路上只要蹭到主人或者别的仆从，就会结结实实扎上去。
 *
 * <p>所以覆写成「<b>主人的自己人 = 我的自己人</b>」，和圣骑那 14 招用的是同一套判定
 * （详见 {@code PossessedPaladinServant.isAlliedTo}）。原来的那句 <b>保留</b>在
 * {@code super.isAlliedTo(...)} 里 —— 它只会「多加」几个友军，不会漏判，
 * 留着反而更贴近原作。
 *
 * <h2>⚠️ 为什么不能直接注册传奇怪物那个类</h2>
 * 因为 {@code registerEntityRenderer} 要求「渲染器的实体类型」和「注册的类型」严格对上，
 * 想改 {@code isAlliedTo} 就必须有自己的子类；有了子类就得有自己的渲染器
 * （见 {@code SoulTridentServantRenderer}）和自己的实体类型（见 {@code LmEntityRegistry}）。
 *
 * <p>⚠️ 顺带记一笔：这把三叉戟<b>插地时</b>会掉出一圈「灵魂柱爆炸」
 * （原版 {@code spawnSpiralStrike} → {@code SoulPillarExplosionEntity}）。
 * 那个爆炸实体我们<b>没有移植</b>，用的是传奇怪物自己注册的那个类型 ——
 * 里面的友军过滤同样会走 {@code caster.isAlliedTo(...)}，
 * 而 caster 就是扔出这把戟的圣骑，所以自动是对的，不用额外处理。
 */
public class SoulTrident extends SoulTridentEntity {

    public SoulTrident(EntityType<? extends SoulTrident> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 见类注释：把「友军」的定义从「传奇怪物的阵营标签」换成「主人的自己人」。
     *
     * <p>原版那句 {@code POSSESSED_ARMOR_TEAM} 的判断留在 {@code super} 里，
     * 所以返回真只有两种可能：目标是我主人的自己人，或者目标是传奇怪物那边的同阵营单位。
     * 对仆从来说后者基本不会发生，但留着不亏。
     */
    @Override
    public boolean isAlliedTo(Entity entity) {
        Entity owner = this.getOwner();
        return (owner != null && owner.isAlliedTo(entity)) || super.isAlliedTo(entity);
    }
}
