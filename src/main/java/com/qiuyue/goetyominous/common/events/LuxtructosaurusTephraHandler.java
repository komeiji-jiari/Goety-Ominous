package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.server.entity.item.TephraEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.common.entities.util.ServantMagmaLink;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LuxtructosaurusTephraHandler {

    private static final Map<UUID, Vec3> PENDING_VELOCITY_RESTORE = new HashMap<>();

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event) {
        if (isServantTephra(event.getEntity())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        LuxtructosaurusServant servant = servantOwner(direct);
        if (servant == null && direct instanceof LuxtructosaurusServant blasting
                && event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            servant = blasting;
        }
        if (servant == null) {
            return;
        }
        if (victim == servant || servant.isAlliedTo(victim) || victim.isAlliedTo(servant)) {
            PENDING_VELOCITY_RESTORE.put(victim.getUUID(), victim.getDeltaMovement());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (PENDING_VELOCITY_RESTORE.isEmpty() || event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity entity = event.getEntity();
        Vec3 restore = PENDING_VELOCITY_RESTORE.remove(entity.getUUID());
        if (restore != null) {
            entity.setDeltaMovement(restore);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PENDING_VELOCITY_RESTORE.clear();
        ServantMagmaLink.clear();
    }

    private static LuxtructosaurusServant servantOwner(Entity entity) {
        if (entity instanceof TephraEntity tephra && tephra.getOwner() instanceof LuxtructosaurusServant servant) {
            return servant;
        }
        return null;
    }

    private static boolean isServantTephra(Entity entity) {
        return servantOwner(entity) != null;
    }
}
