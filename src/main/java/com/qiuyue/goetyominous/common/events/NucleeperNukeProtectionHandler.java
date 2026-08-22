package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.NucleeperExplosionZonePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class NucleeperNukeProtectionHandler {

    private record NukeProtection(ResourceKey<Level> dimension, Vec3 origin, double hRadius, double vRadius, long until, Set<UUID> ownerIds) {
    }

    private static final List<NukeProtection> PROTECTED_NUKES = new ArrayList<>();

    /**
     * 客户端侧 zone(由 NucleeperExplosionZonePacket 同步,按 level.getGameTime() 过期)。
     * NuclearExplosionEntity.tick() 的辐照循环不受 isClientSide 门控,客户端实体会被
     * 爆炸 tick 直接施加 IRRADIATED,服务端 deny 后不会同步移除 → 视觉残留。这里在客户端
     * 同样拒绝,保证两边一致。单机/联机都走包同步,不依赖共享静态区。
     */
    private static final List<NukeProtection> CLIENT_NUCKS = new ArrayList<>();

    public static void protectOwnerAndServants(ServerLevel level, NucleeperServant nucleeper) {
        long until = level.getServer().getTickCount() + protectionTicks(nucleeper);
        Set<UUID> ownerIds = collectOwnerIds(nucleeper);
        // 无兜底:owner 仅来自 Goety 认主链(刷怪蛋潜行放置时 setTrueOwner)。
        // 若为空则记录警告便于排查。
        if (ownerIds.isEmpty()) {
            GoetyOminous.LOGGER.warn("[Nucleeper] 核爆仆从无主,无法提供保护");
        }
        double[] radii = zoneRadii(nucleeper);
        // Match NuclearExplosionEntity damage AABB: inflate(chunks*22.5, chunks*9, chunks*22.5).
        PROTECTED_NUKES.add(new NukeProtection(nucleeper.level().dimension(), nucleeper.position(), radii[0], radii[1], until, ownerIds));
        GoetyOminous.LOGGER.warn("[Nucleeper] 注册保护 zone: 位置={} 半径=({},{}) ownerIds={} until={}",
                nucleeper.blockPosition(), radii[0], radii[1], ownerIds, until);
    }

    /** 从 Goety 认主链收集所有代表"主人"的 UUID(ownerId + 解析出的实体 + 上级主人)。 */
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

    /**
     * 把同一个"无辐照 zone"(含 ownerIds)同步给爆炸附近客户端,让客户端 onEffectApplicable
     * 同样拒绝辐照、onLivingTick 中和冲击波击退。必须在 addFreshEntity(explosion) 之前调用,
     * 确保客户端 tick 到爆炸实体时 zone 已就绪(同连接内包按序处理,先登记的 zone 一定先于
     * spawn 包生效)。
     */
    public static void syncZoneToClients(ServerLevel level, NucleeperServant nucleeper) {
        double[] radii = zoneRadii(nucleeper);
        long until = level.getGameTime() + protectionTicks(nucleeper);
        Vec3 pos = nucleeper.position();
        ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.x, pos.y, pos.z, 128.0, level.dimension())),
                new NucleeperExplosionZonePacket(level.dimension(), pos.x, pos.y, pos.z,
                        radii[0], radii[1], until, collectOwnerIds(nucleeper)));
    }

    /**
     * 客户端收到 NucleeperExplosionZonePacket 后登记 zone。
     * 注意:不要在这里按 until 清理旧 zone——用新 zone 的 until 做阈值会误删仍在生效的
     * 其它爆炸 zone(两枚先后爆炸时)。过期 zone 由 clientZoneCovering 惰性清理。
     */
    public static void registerClientZone(ResourceKey<Level> dimension, double x, double y, double z,
                                          double hRadius, double vRadius, long untilGameTime, Set<UUID> ownerIds) {
        synchronized (CLIENT_NUCKS) {
            CLIENT_NUCKS.add(new NukeProtection(dimension, new Vec3(x, y, z), hRadius, vRadius,
                    untilGameTime, ownerIds));
        }
    }

    /** 与服务端一致:核爆伤害 AABB 半径 = inflate(chunks*22.5, chunks*9, chunks*22.5)。 */
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
        // alexscaves 为可选联动:isNukeDamage 会访问 ACDamageTypes,未加载时抛 NoClassDefFoundError。
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
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        // alexscaves 为可选联动:ACEffectRegistry.IRRADIATED 依赖 alexscaves。
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect != ACEffectRegistry.IRRADIATED.get()) {
            return;
        }
        LivingEntity target = event.getEntity();
        Level level = target.level();
        if (level.isClientSide) {
            // 客户端:爆炸 tick 也会对客户端实体直接施加辐照。只对友方(主人/友军)拒绝,
            // 否则绿色视觉/血条着色/时长显示会永久残留(服务端 deny 后不会同步移除);
            // 敌人保留原版辐照,必须放行,避免客户端 deny 与服务端不同步。
            if (clientZoneCovering(target) != null) {
                event.setResult(Event.Result.DENY);
            }
            return;
        }
        // 核能苦力怕仆从的爆炸只对主人/友军拒绝辐照,敌人保留原版 40 分钟辐照。
        if (coveringZone(target) == null) {
            return;
        }
        event.setResult(Event.Result.DENY);
        GoetyOminous.LOGGER.debug("[Nucleeper] 已阻止 {} 受到核爆辐照效果", target.getName().getString());
    }

    /**
     * 冲击波击退: NuclearExplosionEntity 对爆炸范围内生物直接 setDeltaMovement(方向 * damage * 0.1 * factor),
     * 不经过任何事件,无法在源头拦截,且客户端爆炸 tick 也会对客户端实体重复施加。这里对处于
     * zone 内的友方(主人/友军),在每次 tick 前把爆炸级速度的"外向(径向)"分量消掉——
     * LivingTickEvent 在 aiStep/travel 之前触发,所以友军永远不会被冲击波位移;正常移动速度
     * (水平 < 0.5 blocks/tick、竖直 < 0.5)不受影响。
     * 客户端同样要处理:单机本地玩家位置由客户端主导,服务端中和速度救不了本地玩家。
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        // alexscaves 为可选联动:本 handler 只服务于核能苦力怕仆从,未加载时无需任何处理。
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        Vec3 v = entity.getDeltaMovement();
        double horizSpeedSqr = v.x * v.x + v.z * v.z;
        if (horizSpeedSqr < 0.25 && v.y < 0.5) { // 阈值 0.5 block/tick,低于任何爆炸击退,保留正常移动
            return;
        }
        NukeProtection zone = entity.level().isClientSide
                ? clientZoneCovering(entity)
                : coveringZone(entity); // 服务端:只对友方(主人/友军),敌人照常被掀飞
        if (zone == null) {
            return;
        }
        neutralizeBlastVelocity(entity, v, zone);
    }

    /** 客户端:目标是否处于某个已同步的 zone 内且为友方(ownerIds 链命中)。 */
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

    /** 把爆炸施加的速度沿"外向(径向)"分量中和,保留切向/正常移动与下坠。 */
    private static void neutralizeBlastVelocity(LivingEntity entity, Vec3 v, NukeProtection zone) {
        Vec3 rel = entity.position().subtract(zone.origin());
        double hLen = Math.sqrt(rel.x * rel.x + rel.z * rel.z);
        if (hLen < 1.0e-3) {
            // 站在爆心正上方/正下方:水平速度全部来自爆炸,清水平;保留下落(负 v.y),清上升。
            entity.setDeltaMovement(0.0, Math.min(v.y, 0.0), 0.0);
            return;
        }
        double hx = rel.x / hLen;
        double hz = rel.z / hLen;
        double outward = v.x * hx + v.z * hz;
        double newY = v.y;
        if (newY > 0.5) {
            newY = 0.0; // 压掉爆炸级上升速度(爆炸方向带 +0.3 上偏)
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
        // 单机时客户端与集成服务器同 JVM,顺带清掉客户端 zone,避免跨世界残留。
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
        return false;
    }

    private static boolean isWithin(NukeProtection protection, Entity entity) {
        return Math.abs(entity.getX() - protection.origin().x) <= protection.hRadius()
                && Math.abs(entity.getY() - protection.origin().y) <= protection.vRadius()
                && Math.abs(entity.getZ() - protection.origin().z) <= protection.hRadius();
    }
}
