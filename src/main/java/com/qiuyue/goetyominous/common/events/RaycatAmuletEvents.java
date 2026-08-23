package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.init.ModTags;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.GoetyOminous;
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
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Raycat护符行为(与 Goety Feline Amulet 一致,并复刻 Alex's Caves 雷猫的能力):
 * 佩戴后附近的苦力怕会逃跑;苦力怕/幻翼不会将佩戴者设为攻击目标。
 * 核能苦力怕(野生的 NucleeperEntity 与 NucleeperServant)同样会被驱赶并退引信,
 * 包括其他玩家的仆从与无主仆从;佩戴者自己的核能苦力怕仆从不被驱赶。
 * 复刻雷猫的辐照转化:佩戴者自身及其友军(Goety 仆从/驯服动物)受到的辐照
 * 会被转化为生命恢复 I(15秒),辐照不再造成伤害。采用 MobEffectEvent 事件驱动,
 * 仅在辐照实际施加时响应,不做高频轮询检测。
 */
@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
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
     */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
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
