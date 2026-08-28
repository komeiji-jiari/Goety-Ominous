package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class NucleeperNukeKillHandler {

    private record NukeCredit(ResourceKey<Level> dimension, UUID ownerId, Vec3 origin, double radiusSq, long until) {}

    private static final List<NukeCredit> NUKE_CREDITS = new ArrayList<>();

    public static void register(NucleeperServant nucleeper) {
        UUID ownerId = nucleeper.getOwnerId();
        if (ownerId == null) {
            LivingEntity trueOwner = nucleeper.getTrueOwner();
            if (trueOwner != null) {
                ownerId = trueOwner.getUUID();
            }
        }
        if (ownerId == null) {
            return;
        }
        float size = nucleeper.isCharged() ? 1.75F : 1.0F;
        int chunks = (int) Math.ceil(size);
        // Match NuclearExplosionEntity maxDist = chunks*22.5 + 1.
        double radius = chunks * 22.5 + 1.0;
        MinecraftServer server = nucleeper.level().getServer();
        long until = (server != null ? server.getTickCount() : 0) + 60 + (long) (2 * chunks + 1) * (2 * chunks + 1) * (2 * chunks + 1) / 3;
        NUKE_CREDITS.add(new NukeCredit(nucleeper.level().dimension(), ownerId, nucleeper.position(), radius * radius, until));
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        if (!source.is(ACDamageTypes.NUKE) || source.getDirectEntity() != null) {
            return;
        }
        NukeCredit credit = findCredit(victim);
        if (credit == null) {
            return;
        }
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(serverLevel.getPlayerByUUID(credit.ownerId) instanceof ServerPlayer owner)) {
            return;
        }
        if (!shouldGiveSouls(owner)) {
            return;
        }
        SEHelper.handleKill(owner, victim, source);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        NUKE_CREDITS.clear();
    }

    private static NukeCredit findCredit(LivingEntity victim) {
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        MinecraftServer server = serverLevel.getServer();
        if (server == null) {
            return null;
        }
        long now = server.getTickCount();
        ResourceKey<Level> dim = serverLevel.dimension();
        Iterator<NukeCredit> it = NUKE_CREDITS.iterator();
        NukeCredit hit = null;
        while (it.hasNext()) {
            NukeCredit credit = it.next();
            if (credit.until() < now) {
                it.remove();
                continue;
            }
            if (hit == null && credit.dimension().equals(dim) && victim.distanceToSqr(credit.origin()) <= credit.radiusSq()) {
                hit = credit;
            }
        }
        return hit;
    }

    private static boolean shouldGiveSouls(LivingEntity player) {
        if (MobsConfig.ServantsAlwaysGiveSE.get()) {
            return true;
        }
        if (CuriosFinder.hasDarkRobe(player)) {
            return true;
        }
        if (CuriosFinder.hasUndeadSet(player)) {
            return true;
        }
        if (ItemHelper.armorSet(player, ModArmorMaterials.BLACK_IRON)) {
            return true;
        }
        return ItemHelper.armorSet(player, ModArmorMaterials.DARK);
    }
}
