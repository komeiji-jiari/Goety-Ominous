package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 地雷守护者仆从爆炸保护: 主人 + 同主人的友军仆从不吃爆炸伤害, 也不吃冲击击退。
 * <p>
 * 伤害: MineExplosion 的伤害来源 direct entity 就是地雷守护者本身(爆炸前它已 remove, 但引用仍在),
 * 在 LivingHurtEvent 里按 direct entity 识别并取消。双端都取消, 单机本地玩家不会闪红/掉血。
 * <p>
 * 击退: MineExplosion.explode() 是直接 entity.setDeltaMovement(), 不走任何事件,
 * 所以在爆炸前后快照/恢复受保护实体的速度来抵消冲击。tick 里该爆炸块双端都会执行,
 * 双端各自快照+恢复, 单机本地玩家和服务端实体一致。
 * <p>
 * 注意: 不能加 @Mod.EventBusSubscriber——本类引用 MineGuardianServant(其直接引用
 * Alex's Caves 类型),AC 缺失时会 NoClassDefFoundError。由 GoetyOminous 构造器在
 * isAlexCavesLoaded() 门内手动 MinecraftForge.EVENT_BUS.register。
 */
public class MineGuardianExplosionProtectionHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof MineGuardianServant servant)) {
            return;
        }
        if (isProtected(servant, event.getEntity())) {
            event.setCanceled(true);
        }
    }

    // 主人或友军仆从。双向 isAlliedTo: 覆盖主人(entityIn == trueOwner)、同主人仆从(MobUtil.ownerStack)、
    // 主人盟友、链式主从(getMasterOwner 对比链顶)。
    private static boolean isProtected(MineGuardianServant servant, LivingEntity victim) {
        if (victim == servant) {
            return false;
        }
        return servant.isAlliedTo(victim) || victim.isAlliedTo(servant);
    }

    // 爆炸前调用: 快照爆炸范围内所有受保护实体的当前速度, 返回快照列表。
    public static List<Map.Entry<Entity, Vec3>> snapshotProtectedVelocities(Level level, MineGuardianServant servant,
                                                                            double x, double y, double z, float radius) {
        // MineExplosion 的实体伤害/击退范围是 radius*2, 扩一格兜底。
        double range = radius * 2.0F + 1.0F;
        AABB aabb = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        List<Map.Entry<Entity, Vec3>> snapshots = new ArrayList<>();
        for (Entity entity : level.getEntities(servant, aabb, e -> e instanceof LivingEntity living && isProtected(servant, living))) {
            snapshots.add(new AbstractMap.SimpleImmutableEntry<>(entity, entity.getDeltaMovement()));
        }
        return snapshots;
    }

    // 爆炸后调用: 把速度恢复成爆炸前的值, 抵消冲击击退。
    public static void restoreProtectedVelocities(List<Map.Entry<Entity, Vec3>> snapshots) {
        for (Map.Entry<Entity, Vec3> snapshot : snapshots) {
            Entity entity = snapshot.getKey();
            if (entity.isAlive()) {
                entity.setDeltaMovement(snapshot.getValue());
            }
        }
    }
}
