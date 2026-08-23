package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.init.ModTags;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.items.ac.RaycatAmuletItem;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * Raycat护符行为(与 Goety Feline Amulet 一致,并复刻 Alex's Caves 雷猫的能力):
 * 佩戴后附近的苦力怕会逃跑;苦力怕/幻翼不会将佩戴者设为攻击目标。
 * 核能苦力怕(野生的 NucleeperEntity 与 NucleeperServant)同样会被驱赶并退引信,
 * 包括其他玩家的仆从与无主仆从;佩戴者自己的核能苦力怕仆从不被驱赶。
 * 复刻雷猫的辐照转化:佩戴者自身及其友军(Goety 仆从/驯服动物)受到的辐照
 * 会被转化为生命恢复 I(15秒),辐照不再造成伤害。双保险:
 * MobEffectEvent.Applicable 在辐照将要施加时拦截转化(响应式,对新辐照即时生效);
 * PlayerTickEvent 每 40 刻(2秒)低频清理一次"已存在"的辐照(如戴上护符前就中招的),
 * 避免高频轮询。
 *
 * 注意:不能加 @Mod.EventBusSubscriber 注解——该注解会被 Forge 在 mod 构造时无条件
 * Class.forName,而本类直接引用 Alex's Caves 类型(NucleeperEntity/ACEffectRegistry),
 * AC 未加载时会 NoClassDefFoundError。改为在 GoetyOminous 构造器的
 * isAlexCavesLoaded() 门内手动 MinecraftForge.EVENT_BUS.register 本类。
 */
public class RaycatAmuletEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();

        // 苦力怕:避开佩戴者(与原版 Feline Amulet 一致)
        if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.EntityTypes.CREEPERS)) {
            creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, 6.0F, 1.0D, 1.2D,
                    target -> target != null && RaycatAmuletItem.hasAmulet(target)));
        }

        // 野生核能苦力怕(Alex's Caves 联动):像被雷猫吓到一样逃离佩戴者并退引信
        if (AlexCavesCompat.isAlexCavesLoaded() && entity instanceof NucleeperEntity nucleeper) {
            nucleeper.goalSelector.addGoal(1, new AvoidEntityGoal<>(nucleeper, Player.class, 10.0F, 1.0D, 1.2D,
                    target -> target instanceof Player player && RaycatAmuletItem.hasAmulet(player)) {
                @Override
                public void tick() {
                    super.tick();
                    // 逃窜期间清空目标 → 走 tick 里的 noTarget 分支,引信回退直至解除
                    nucleeper.setTarget(null);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {
        LivingEntity newTarget = event.getNewTarget();
        if (!(newTarget instanceof Player player)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        boolean relevant = entity.getType().is(ModTags.EntityTypes.CREEPERS)
                || entity instanceof Phantom
                || isNucleeper(entity);
        if (!relevant) {
            return;
        }
        if (!RaycatAmuletItem.hasAmulet(player)) {
            return;
        }
        // 佩戴者自己的核能苦力怕仆从不被驱赶
        if (entity instanceof NucleeperServant servant && player == servant.getTrueOwner()) {
            return;
        }
        event.setNewTarget(null);
    }

    private static boolean isNucleeper(Entity entity) {
        return entity instanceof NucleeperServant
                || (AlexCavesCompat.isAlexCavesLoaded() && entity instanceof NucleeperEntity);
    }

    /**
     * 辐照 → 生命恢复 I(15秒):
     * 当辐照将要施加到佩戴者自身或佩戴者的友军(Goety 仆从/驯服动物)身上时,
     * 阻止辐照生效,并改为给予生命恢复 I 持续 300 刻(15秒)。
     * 事件驱动(仅在实际施加辐照时触发一次),不做高频检测。
     *
     * 注意:不能按 isClientSide 跳过客户端——AC 的核爆实体在客户端 tick 也会直接给
     * 客户端实体施加 IRRADIATED(不受 isClientSide 门控,见 NucleeperNukeProtectionHandler
     * 注释),若只在服务端拦截,客户端实体上会残留辐照效果,表现为血条特殊渲染和
     * buff 图标不消失。这里服务端、客户端都要 deny。
     */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect != ACEffectRegistry.IRRADIATED.get()) {
            return;
        }
        if (amuletWearerFor(entity) == null) {
            return;
        }
        // 辐照被护符吸收,转化为生命恢复 I(15秒)
        event.setResult(Event.Result.DENY);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 15, 0));
    }

    /**
     * 低频清理"已存在"的辐照(高频部分交给 onEffectApplicable 响应式处理):
     * 每 40 刻(2秒)检查佩戴者自身与附近友军身上已有的辐照,移除并转化为生命恢复 I(15秒)。
     * 覆盖戴上护符前就已中招、或辐照以非 addEffect 路径遗留的情况。
     *
     * 同样不能按 isClientSide 跳过客户端:AC 核爆实体在客户端 tick 直接施加的辐照
     * 服务端不知道,只有客户端自己清理才能让血条特殊渲染和 buff 图标消失。
     * 客户端只清理本端实体,不产生网络同步,安全。
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (!RaycatAmuletItem.hasAmulet(player)) {
            return;
        }
        if (player.tickCount % 40 != 0) {
            return;
        }
        List<LivingEntity> candidates = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(16.0D, 8.0D, 16.0D));
        for (LivingEntity entity : candidates) {
            if (amuletWearerFor(entity) == player) {
                convertExistingIrradiated(entity);
            }
        }
    }

    /** 移除实体身上已有的辐照,转化为生命恢复 I(15秒) */
    private static void convertExistingIrradiated(LivingEntity entity) {
        MobEffectInstance rad = entity.getEffect(ACEffectRegistry.IRRADIATED.get());
        if (rad != null) {
            entity.removeEffect(ACEffectRegistry.IRRADIATED.get());
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 15, 0));
        }
    }

    /** 返回拥有该实体(自身或主人)且佩戴了 Raycat 护符的玩家;无则返回 null */
    private static Player amuletWearerFor(LivingEntity entity) {
        if (entity instanceof Player player) {
            return RaycatAmuletItem.hasAmulet(player) ? player : null;
        }
        if (entity instanceof IOwned owned && owned.getTrueOwner() instanceof Player owner) {
            return RaycatAmuletItem.hasAmulet(owner) ? owner : null;
        }
        if (entity instanceof TamableAnimal tame && tame.getOwner() instanceof Player owner) {
            return RaycatAmuletItem.hasAmulet(owner) ? owner : null;
        }
        return null;
    }
}
