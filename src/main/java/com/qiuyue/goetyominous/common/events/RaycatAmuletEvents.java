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

public class RaycatAmuletEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();

        if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.EntityTypes.CREEPERS)) {
            creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, 6.0F, 1.0D, 1.2D,
                    target -> target != null && RaycatAmuletItem.hasAmulet(target)));
        }

        if (AlexCavesCompat.isAlexCavesLoaded() && entity instanceof NucleeperEntity nucleeper) {
            nucleeper.goalSelector.addGoal(1, new AvoidEntityGoal<>(nucleeper, Player.class, 10.0F, 1.0D, 1.2D,
                    target -> target instanceof Player player && RaycatAmuletItem.hasAmulet(player)) {
                @Override
                public void tick() {
                    super.tick();
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
        if (entity instanceof NucleeperServant servant && player == servant.getTrueOwner()) {
            return;
        }
        event.setNewTarget(null);
    }

    private static boolean isNucleeper(Entity entity) {
        return entity instanceof NucleeperServant
                || (AlexCavesCompat.isAlexCavesLoaded() && entity instanceof NucleeperEntity);
    }

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
        event.setResult(Event.Result.DENY);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 0));
    }

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

    private static void convertExistingIrradiated(LivingEntity entity) {
        MobEffectInstance rad = entity.getEffect(ACEffectRegistry.IRRADIATED.get());
        if (rad != null) {
            entity.removeEffect(ACEffectRegistry.IRRADIATED.get());
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 0));
        }
    }

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
