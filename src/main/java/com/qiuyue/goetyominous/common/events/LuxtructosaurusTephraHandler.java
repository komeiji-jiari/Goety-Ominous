package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.github.alexmodguy.alexscaves.server.entity.item.TephraEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.common.entities.projectile.ServantTephraEntity;
import com.qiuyue.goetyominous.common.entities.util.ServantMagmaLink;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LuxtructosaurusTephraHandler {

    private static final Map<UUID, Vec3> PENDING_VELOCITY_RESTORE = new HashMap<>();

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        LivingEntity owner = tephraOwner(direct);
        if (owner == null && direct instanceof LuxtructosaurusServant blasting
                && event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            owner = blasting;
        }
        if (owner == null) {
            return;
        }
        if (direct instanceof ServantTephraEntity tephra && tephra.isDangerous() && victim != owner) {
            return;
        }
        LivingEntity master = owner instanceof IOwned owned ? owned.getTrueOwner() : null;
        if (victim == owner || victim == master || isAllyOf(owner, master, victim)) {
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

    private static LivingEntity tephraOwner(Entity entity) {
        if (entity instanceof TephraEntity tephra && tephra.getOwner() instanceof LivingEntity owner) {
            return owner;
        }
        return null;
    }

    private static boolean isAllyOf(LivingEntity owner, LivingEntity master, LivingEntity victim) {
        if (owner.isAlliedTo(victim) || victim.isAlliedTo(owner)) {
            return true;
        }
        LivingEntity ref = master != null ? master : owner;
        return victim instanceof IOwned owned && owned.getTrueOwner() == ref;
    }
}
