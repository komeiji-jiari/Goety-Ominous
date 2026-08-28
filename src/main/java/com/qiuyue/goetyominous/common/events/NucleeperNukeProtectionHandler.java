package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.SEHelper;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.NucleeperExplosionZonePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class NucleeperNukeProtectionHandler {

    private record NukeProtection(ResourceKey<Level> dimension, Vec3 origin, double hRadius, double vRadius, long until, Set<UUID> ownerIds) {
    }

    private static final List<NukeProtection> PROTECTED_NUKES = new ArrayList<>();

    
    private static final List<NukeProtection> CLIENT_NUCKS = new ArrayList<>();

    
    private static Field spawnedParticleField;

    private static Field getSpawnedParticleField() {
        if (spawnedParticleField == null) {
            try {
                spawnedParticleField = NuclearExplosionEntity.class.getDeclaredField("spawnedParticle");
                spawnedParticleField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                GoetyOminous.LOGGER.error("[Nucleeper] 无法反射定位 NuclearExplosionEntity.spawnedParticle(AC 版本可能变化)", e);
            }
        }
        return spawnedParticleField;
    }

    private static void suppressVanillaCloud(NuclearExplosionEntity explosion) {
        Field field = getSpawnedParticleField();
        if (field == null) {
            return;
        }
        try {
            field.setBoolean(explosion, true);
        } catch (IllegalAccessException e) {
            GoetyOminous.LOGGER.error("[Nucleeper] 设置 spawnedParticle 失败", e);
        }
    }

    
    @SubscribeEvent
    public static void onExplosionJoin(EntityJoinLevelEvent event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        if (!(event.getEntity() instanceof NuclearExplosionEntity explosion)) {
            return;
        }
        if (!explosion.isNoGriefing()) {
            return;
        }
        if (!isOurServantExplosion(explosion)) {
            return;
        }
        suppressVanillaCloud(explosion);
    }

    
    private static boolean isOurServantExplosion(NuclearExplosionEntity explosion) {
        Vec3 pos = explosion.position();
        Level level = explosion.level();
        if (level.isClientSide) {
            long now = level.getGameTime();
            synchronized (CLIENT_NUCKS) {
                for (NukeProtection zone : CLIENT_NUCKS) {
                    if (zone.until() >= now && zone.dimension().equals(level.dimension())
                            && pos.distanceToSqr(zone.origin()) < 0.01) {
                        return true;
                    }
                }
            }
        } else if (level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();
            if (server == null) {
                return false;
            }
            long now = server.getTickCount();
            for (NukeProtection zone : PROTECTED_NUKES) {
                if (zone.until() >= now && zone.dimension().equals(level.dimension())
                        && pos.distanceToSqr(zone.origin()) < 0.01) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void protectOwnerAndServants(ServerLevel level, NucleeperServant nucleeper) {
        long until = level.getServer().getTickCount() + protectionTicks(nucleeper);
        Set<UUID> ownerIds = collectOwnerIds(nucleeper);
        
        
        if (ownerIds.isEmpty()) {
            GoetyOminous.LOGGER.warn("[Nucleeper] 核爆仆从无主,无法提供保护");
        }
        double[] radii = zoneRadii(nucleeper);
        // Match NuclearExplosionEntity damage AABB: inflate(chunks*22.5, chunks*9, chunks*22.5).
        PROTECTED_NUKES.add(new NukeProtection(nucleeper.level().dimension(), nucleeper.position(), radii[0], radii[1], until, ownerIds));
        GoetyOminous.LOGGER.warn("[Nucleeper] 注册保护 zone: 位置={} 半径=({},{}) ownerIds={} until={}",
                nucleeper.blockPosition(), radii[0], radii[1], ownerIds, until);
    }

    
    private static Set<UUID> collectOwnerIds(NucleeperServant nucleeper) {
        Set<UUID> ownerIds = new HashSet<>();
        addOwner(ownerIds, nucleeper.getOwnerId());
        LivingEntity trueOwner = nucleeper.getTrueOwner();
        if (trueOwner != null) {
            addOwner(ownerIds, trueOwner.getUUID());
        }
        LivingEntity masterOwner = nucleeper.getMasterOwner();
        if (masterOwner != null) {
            addOwner(ownerIds, masterOwner.getUUID());
        }
        return ownerIds;
    }

    
    public static void syncZoneToClients(ServerLevel level, NucleeperServant nucleeper) {
        double[] radii = zoneRadii(nucleeper);
        long until = level.getGameTime() + protectionTicks(nucleeper);
        Vec3 pos = nucleeper.position();
        ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.x, pos.y, pos.z, 128.0, level.dimension())),
                new NucleeperExplosionZonePacket(level.dimension(), pos.x, pos.y, pos.z,
                        radii[0], radii[1], until, collectOwnerIds(nucleeper)));
    }

    
    public static void registerClientZone(ResourceKey<Level> dimension, double x, double y, double z,
                                          double hRadius, double vRadius, long untilGameTime, Set<UUID> ownerIds) {
        synchronized (CLIENT_NUCKS) {
            CLIENT_NUCKS.add(new NukeProtection(dimension, new Vec3(x, y, z), hRadius, vRadius,
                    untilGameTime, ownerIds));
        }
    }

    
    private static double[] zoneRadii(NucleeperServant nucleeper) {
        float size = nucleeper.isCharged() ? 1.75F : 1.0F;
        int chunks = (int) Math.ceil(size);
        return new double[]{chunks * 22.5 + 1.0, chunks * 9.0 + 1.0};
    }

    private static void addOwner(Set<UUID> ownerIds, UUID ownerId) {
        if (ownerId != null) {
            ownerIds.add(ownerId);
        }
    }

    private static long protectionTicks(NucleeperServant nucleeper) {
        float size = nucleeper.isCharged() ? 1.75F : 1.0F;
        int chunks = (int) Math.ceil(size);
        long stack = (long) (2 * chunks + 1) * (2 * chunks + 1) * (2 * chunks + 1);
        return 60 + stack / 3;
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (!isNukeDamage(event.getSource())) {
            return;
        }
        if (coveringZone(event.getEntity()) == null) {
            return;
        }
        event.setCanceled(true);
        GoetyOminous.LOGGER.debug("[Nucleeper] 已取消 {} 受到的核爆伤害", event.getEntity().getName().getString());
    }

    
    

    
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        Vec3 v = entity.getDeltaMovement();
        double horizSpeedSqr = v.x * v.x + v.z * v.z;
        if (horizSpeedSqr < 0.25 && v.y < 0.5) { 
            return;
        }
        NukeProtection zone = entity.level().isClientSide
                ? clientZoneCovering(entity)
                : coveringZone(entity); 
        if (zone == null) {
            return;
        }
        neutralizeBlastVelocity(entity, v, zone);
    }

    
    private static NukeProtection clientZoneCovering(LivingEntity entity) {
        Level level = entity.level();
        long now = level.getGameTime();
        ResourceKey<Level> dim = level.dimension();
        synchronized (CLIENT_NUCKS) {
            Iterator<NukeProtection> it = CLIENT_NUCKS.iterator();
            while (it.hasNext()) {
                NukeProtection zone = it.next();
                if (zone.until() < now) {
                    it.remove();
                    continue;
                }
                if (zone.dimension().equals(dim) && isWithin(zone, entity) && isProtected(entity, zone)) {
                    return zone;
                }
            }
        }
        return null;
    }

    
    private static void neutralizeBlastVelocity(LivingEntity entity, Vec3 v, NukeProtection zone) {
        Vec3 rel = entity.position().subtract(zone.origin());
        double hLen = Math.sqrt(rel.x * rel.x + rel.z * rel.z);
        if (hLen < 1.0e-3) {
            
            entity.setDeltaMovement(0.0, Math.min(v.y, 0.0), 0.0);
            return;
        }
        double hx = rel.x / hLen;
        double hz = rel.z / hLen;
        double outward = v.x * hx + v.z * hz;
        double newY = v.y;
        if (newY > 0.5) {
            newY = 0.0; 
        }
        if (outward > 0.0) {
            entity.setDeltaMovement(v.x - hx * outward, newY, v.z - hz * outward);
        } else if (newY != v.y) {
            entity.setDeltaMovement(v.x, newY, v.z);
        }
    }

    private static boolean isNukeDamage(DamageSource source) {
        return source.is(ACDamageTypes.NUKE) || source.is(ACDamageTypes.INTENTIONAL_GAME_DESIGN);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PROTECTED_NUKES.clear();
        
        synchronized (CLIENT_NUCKS) {
            CLIENT_NUCKS.clear();
        }
    }

    private static NukeProtection coveringZone(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        MinecraftServer server = serverLevel.getServer();
        if (server == null) {
            return null;
        }
        long now = server.getTickCount();
        ResourceKey<Level> dim = serverLevel.dimension();
        Iterator<NukeProtection> it = PROTECTED_NUKES.iterator();
        while (it.hasNext()) {
            NukeProtection protection = it.next();
            if (protection.until() < now) {
                it.remove();
                continue;
            }
            if (protection.dimension().equals(dim) && isWithin(protection, entity)
                    && isProtected(entity, protection)) {
                return protection;
            }
        }
        return null;
    }

    private static boolean isProtected(LivingEntity entity, NukeProtection protection) {
        if (protection.ownerIds().contains(entity.getUUID())) {
            return true;
        }
        if (entity instanceof IOwned owned) {
            UUID ownerId = owned.getOwnerId();
            if (ownerId != null && protection.ownerIds().contains(ownerId)) {
                return true;
            }
            LivingEntity master = owned.getMasterOwner();
            if (master != null && protection.ownerIds().contains(master.getUUID())) {
                return true;
            }
        }
        if (!entity.level().isClientSide && isGoodwillAlly(entity, protection)) {
            return true;
        }
        return false;
    }

    
    private static boolean isGoodwillAlly(LivingEntity entity, NukeProtection protection) {
        MinecraftServer server = entity.level().getServer();
        if (server == null) {
            return false;
        }
        for (UUID ownerId : protection.ownerIds()) {
            Player owner = server.getPlayerList().getPlayer(ownerId);
            if (owner != null && SEHelper.isAlly(owner, entity)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWithin(NukeProtection protection, Entity entity) {
        return Math.abs(entity.getX() - protection.origin().x) <= protection.hRadius()
                && Math.abs(entity.getY() - protection.origin().y) <= protection.vRadius()
                && Math.abs(entity.getZ() - protection.origin().z) <= protection.hRadius();
    }
}
