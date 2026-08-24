/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.PrimedTnt
 *  net.minecraft.world.entity.monster.Creeper
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.TickEvent$LevelTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.server.ServerStartingEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.entity.IceMonarchEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TimeFreezeManager {
    private static final List<TimeFreezeZone> ZONES = new ArrayList<TimeFreezeZone>();
    private static final Map<UUID, FrozenEntityData> FROZEN_ENTITIES = new HashMap<UUID, FrozenEntityData>();
    private static Field CREEPER_SWELL_FIELD;
    private static Field CREEPER_OLD_SWELL_FIELD;

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        new TimeFreezeManager();
    }

    public static void create(ServerLevel level, LivingEntity caster, double radius, int durationTicks) {
        if (level == null || caster == null) {
            return;
        }
        TimeFreezeZone zone = new TimeFreezeZone(level.m_46472_().m_135782_().toString(), caster.m_20148_(), caster.m_20182_(), radius, durationTicks);
        zone.preDelay = 20;
        ZONES.add(zone);
    }

    public static UUID getCasterUuidForFrozenZone(Level level, Vec3 pos) {
        if (level == null || pos == null) {
            return null;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        for (TimeFreezeZone zone : ZONES) {
            if (!zone.dimension.equals(dimension) || zone.preDelay > 0 || !(zone.center.m_82557_(pos) <= zone.radius * zone.radius)) continue;
            return zone.casterUuid;
        }
        return null;
    }

    public static boolean isInsideFrozenZone(Level level, Vec3 pos) {
        if (level == null || pos == null) {
            return false;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        for (TimeFreezeZone zone : ZONES) {
            if (!zone.dimension.equals(dimension) || !(zone.center.m_82557_(pos) <= zone.radius * zone.radius)) continue;
            return true;
        }
        return false;
    }

    private static void tickServerLevel(ServerLevel serverLevel) {
        if (ZONES.isEmpty() && FROZEN_ENTITIES.isEmpty()) {
            return;
        }
        String dimension = serverLevel.m_46472_().m_135782_().toString();
        HashSet<UUID> activeThisTick = new HashSet<UUID>();
        Iterator<TimeFreezeZone> iterator = ZONES.iterator();
        while (iterator.hasNext()) {
            TimeFreezeZone zone = iterator.next();
            if (!zone.dimension.equals(dimension)) continue;
            if (zone.preDelay > 0) {
                if (!zone.playedStartSound) {
                    serverLevel.m_5594_(null, BlockPos.m_274446_((Position)zone.center), (SoundEvent)GoetyMasteryOfMagicModSounds.TIMESTOP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                    zone.playedStartSound = true;
                }
                --zone.preDelay;
                continue;
            }
            if (zone.ticksLeft == 50 && !zone.playedResumeSound) {
                serverLevel.m_5594_(null, BlockPos.m_274446_((Position)zone.center), (SoundEvent)GoetyMasteryOfMagicModSounds.TIMERESUME.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                zone.playedResumeSound = true;
            }
            TimeFreezeManager.tickZone(serverLevel, zone, activeThisTick);
            if (zone.ticksLeft > 20 && zone.tickTimer <= 0) {
                TimeFreezeManager.playClockTickForPlayers(serverLevel, zone);
                zone.tickTimer = 19;
            }
            if (zone.tickTimer > 0) {
                --zone.tickTimer;
            }
            --zone.ticksLeft;
            if (zone.ticksLeft > 0) continue;
            TimeFreezeManager.sendTimeFreezeEndSync(serverLevel, zone);
            iterator.remove();
        }
        TimeFreezeManager.restoreInactiveFrozenEntities(serverLevel, dimension, activeThisTick);
    }

    private static void playClockTickForPlayers(ServerLevel serverLevel, TimeFreezeZone zone) {
        serverLevel.m_142425_(EntityTypeTest.m_156916_(Player.class), new AABB(zone.center.f_82479_ - zone.radius, zone.center.f_82480_ - zone.radius, zone.center.f_82481_ - zone.radius, zone.center.f_82479_ + zone.radius, zone.center.f_82480_ + zone.radius, zone.center.f_82481_ + zone.radius), player -> {
            if (player.m_7500_() || player.m_5833_()) {
                return false;
            }
            if (!player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                return false;
            }
            return player.m_20182_().m_82557_(zone.center) <= zone.radius * zone.radius;
        }).forEach(player -> player.m_6330_((SoundEvent)GoetyMasteryOfMagicModSounds.CLOCKTICKSINGLE.get(), SoundSource.PLAYERS, 0.5f, 1.0f));
    }

    public static List<LivingEntity> getFrozenEnemiesForCaster(ServerLevel level, LivingEntity caster, double searchRadius) {
        ArrayList<LivingEntity> result = new ArrayList<LivingEntity>();
        if (level == null || caster == null) {
            return result;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        for (TimeFreezeZone zone : ZONES) {
            if (!zone.dimension.equals(dimension) || zone.preDelay > 0 || zone.casterUuid == null || !zone.casterUuid.equals(caster.m_20148_())) continue;
            double radius = Math.max(zone.radius, searchRadius);
            AABB area = new AABB(zone.center.f_82479_ - radius, zone.center.f_82480_ - radius, zone.center.f_82481_ - radius, zone.center.f_82479_ + radius, zone.center.f_82480_ + radius, zone.center.f_82481_ + radius);
            for (LivingEntity living : level.m_6443_(LivingEntity.class, area, entity -> {
                Player player;
                if (entity == null || !entity.m_6084_()) {
                    return false;
                }
                if (entity == caster) {
                    return false;
                }
                if (entity.m_20148_().equals(caster.m_20148_())) {
                    return false;
                }
                if (!TimeFreezeManager.isEntityFrozen((Entity)entity)) {
                    return false;
                }
                if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
                    return false;
                }
                return entity.m_20182_().m_82557_(zone.center) <= zone.radius * zone.radius;
            })) {
                result.add(living);
            }
        }
        return result;
    }

    private static void tickZone(ServerLevel serverLevel, TimeFreezeZone zone, Set<UUID> activeThisTick) {
        double radius = zone.radius;
        AABB area = new AABB(zone.center.f_82479_ - radius, zone.center.f_82480_ - radius, zone.center.f_82481_ - radius, zone.center.f_82479_ + radius, zone.center.f_82480_ + radius, zone.center.f_82481_ + radius);
        TimeFreezeManager.freezeFluidBlocks(serverLevel, zone);
        List entities = serverLevel.m_142425_(EntityTypeTest.m_156916_(Entity.class), area, entity -> {
            Player player;
            if (entity == null || !entity.m_6084_()) {
                return false;
            }
            if (zone.casterUuid != null) {
                IceMonarchEntity monarch;
                LivingEntity owner;
                if (entity.m_20148_().equals(zone.casterUuid)) {
                    return false;
                }
                if (entity instanceof IceMonarchEntity && (owner = (monarch = (IceMonarchEntity)((Object)entity)).getTrueOwner()) != null && owner.m_20148_().equals(zone.casterUuid)) {
                    return false;
                }
            }
            if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
                return false;
            }
            return entity.m_20182_().m_82557_(zone.center) <= radius * radius;
        });
        for (Entity entity2 : entities) {
            TimeFreezeManager.freezeEntity(serverLevel, entity2, zone.dimension, activeThisTick);
        }
        TimeFreezeManager.sendTimeFreezeActiveSync(serverLevel, zone);
    }

    private static void sendTimeFreezeActiveSync(ServerLevel serverLevel, TimeFreezeZone zone) {
        AABB syncArea = new AABB(zone.center.f_82479_ - 128.0, zone.center.f_82480_ - 128.0, zone.center.f_82481_ - 128.0, zone.center.f_82479_ + 128.0, zone.center.f_82480_ + 128.0, zone.center.f_82481_ + 128.0);
        for (ServerPlayer serverPlayer : serverLevel.m_45976_(ServerPlayer.class, syncArea)) {
            GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(serverPlayer, zone.center.f_82479_, zone.center.f_82480_, zone.center.f_82481_, zone.radius, zone.casterUuid, true);
        }
    }

    private static void freezeFluidBlocks(ServerLevel serverLevel, TimeFreezeZone zone) {
        if (!zone.initializedBlocks) {
            zone.initializedBlocks = true;
            TimeFreezeManager.captureFluidBlocks(serverLevel, zone);
        }
        TimeFreezeManager.restoreFluidBlocks(serverLevel, zone);
    }

    private static void captureFluidBlocks(ServerLevel serverLevel, TimeFreezeZone zone) {
        int radius = Mth.m_14165_((double)zone.radius);
        BlockPos center = BlockPos.m_274446_((Position)zone.center);
        int minY = Math.max(serverLevel.m_141937_(), center.m_123342_() - radius);
        int maxY = Math.min(serverLevel.m_151558_() - 1, center.m_123342_() + radius);
        double radiusSq = zone.radius * zone.radius;
        for (int x = center.m_123341_() - radius; x <= center.m_123341_() + radius; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = center.m_123343_() - radius; z <= center.m_123343_() + radius; ++z) {
                    BlockState state;
                    FluidState fluidState;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (pos.m_252807_().m_82557_(zone.center) > radiusSq || (fluidState = (state = serverLevel.m_8055_(pos)).m_60819_()).m_76178_() && !(state.m_60734_() instanceof LiquidBlock)) continue;
                    zone.frozenBlocks.put(pos.m_7949_(), state);
                }
            }
        }
    }

    private static void restoreFluidBlocks(ServerLevel serverLevel, TimeFreezeZone zone) {
        for (Map.Entry<BlockPos, BlockState> entry : zone.frozenBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState frozenState = entry.getValue();
            BlockState currentState = serverLevel.m_8055_(pos);
            if (currentState == frozenState) continue;
            serverLevel.m_7731_(pos, frozenState, 18);
        }
    }

    private static void freezeEntity(ServerLevel serverLevel, Entity entity, String dimension, Set<UUID> activeThisTick) {
        UUID uuid = entity.m_20148_();
        activeThisTick.add(uuid);
        FrozenEntityData data = FROZEN_ENTITIES.get(uuid);
        if (data == null) {
            data = new FrozenEntityData(entity, dimension);
            FROZEN_ENTITIES.put(uuid, data);
        }
        entity.m_20242_(true);
        entity.m_6034_(data.position.f_82479_, data.position.f_82480_, data.position.f_82481_);
        entity.f_19790_ = data.position.f_82479_;
        entity.f_19791_ = data.position.f_82480_;
        entity.f_19792_ = data.position.f_82481_;
        entity.f_19854_ = data.position.f_82479_;
        entity.f_19855_ = data.position.f_82480_;
        entity.f_19856_ = data.position.f_82481_;
        if (entity instanceof Projectile) {
            entity.m_20256_(Vec3.f_82478_);
        }
        entity.f_19864_ = true;
        entity.f_19789_ = 0.0f;
        entity.m_146922_(data.yRot);
        entity.m_146926_(data.xRot);
        entity.f_19859_ = data.yRot;
        entity.f_19860_ = data.xRot;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.f_20883_ = data.yRot;
            livingEntity.f_20884_ = data.yRot;
            livingEntity.f_20885_ = data.yRot;
            livingEntity.f_20886_ = data.yRot;
        }
        if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.m_21573_().m_26573_();
            mob.m_21561_(false);
            mob.m_6710_(null);
            mob.m_21557_(true);
        }
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            living.m_7292_(new MobEffectInstance((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get(), 2, 0, false, false));
        }
        if (entity instanceof PrimedTnt) {
            PrimedTnt tnt = (PrimedTnt)entity;
            tnt.m_32085_(data.tntFuse);
        }
        if (entity instanceof Creeper) {
            Creeper creeper = (Creeper)entity;
            TimeFreezeManager.freezeCreeperFuse(creeper, data);
        }
    }

    private static void restoreInactiveFrozenEntities(ServerLevel serverLevel, String dimension, Set<UUID> activeThisTick) {
        Iterator<Map.Entry<UUID, FrozenEntityData>> iterator = FROZEN_ENTITIES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FrozenEntityData> entry = iterator.next();
            UUID uuid = entry.getKey();
            FrozenEntityData data = entry.getValue();
            if (!data.dimension.equals(dimension) || activeThisTick.contains(uuid)) continue;
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity != null) {
                TimeFreezeManager.restoreEntity(entity, data);
            }
            iterator.remove();
        }
    }

    private static void restoreEntity(Entity entity, FrozenEntityData data) {
        entity.m_20242_(data.hadNoGravity);
        entity.m_20256_(data.motion);
        entity.f_19864_ = true;
        entity.m_146922_(data.yRot);
        entity.m_146926_(data.xRot);
        entity.f_19859_ = data.yRot;
        entity.f_19860_ = data.xRot;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.f_20883_ = data.yRot;
            livingEntity.f_20884_ = data.yRot;
            livingEntity.f_20885_ = data.yRot;
            livingEntity.f_20886_ = data.yRot;
        }
        if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.m_21557_(data.hadNoAi);
            if (!data.hadNoAi) {
                mob.m_21573_().m_26573_();
                mob.m_6710_(null);
            }
        }
        if (entity instanceof PrimedTnt) {
            PrimedTnt tnt = (PrimedTnt)entity;
            tnt.m_32085_(data.tntFuse);
        }
        if (entity instanceof Creeper) {
            Creeper creeper = (Creeper)entity;
            TimeFreezeManager.restoreCreeperFuse(creeper, data);
        }
    }

    private static void freezeCreeperFuse(Creeper creeper, FrozenEntityData data) {
        if (CREEPER_SWELL_FIELD == null || CREEPER_OLD_SWELL_FIELD == null) {
            creeper.m_32283_(0);
            return;
        }
        try {
            CREEPER_SWELL_FIELD.setInt(creeper, data.creeperSwell);
            CREEPER_OLD_SWELL_FIELD.setInt(creeper, data.creeperOldSwell);
            creeper.m_32283_(0);
        }
        catch (Exception ignored) {
            creeper.m_32283_(0);
        }
    }

    private static void restoreCreeperFuse(Creeper creeper, FrozenEntityData data) {
        if (CREEPER_SWELL_FIELD == null || CREEPER_OLD_SWELL_FIELD == null) {
            return;
        }
        try {
            CREEPER_SWELL_FIELD.setInt(creeper, data.creeperSwell);
            CREEPER_OLD_SWELL_FIELD.setInt(creeper, data.creeperOldSwell);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void createGeneric(ServerLevel level, Vec3 center, double radius, int durationTicks) {
        if (level == null || center == null) {
            return;
        }
        TimeFreezeZone zone = new TimeFreezeZone(level.m_46472_().m_135782_().toString(), null, center, radius, durationTicks);
        zone.preDelay = 20;
        ZONES.add(zone);
    }

    public static void createFromCaster(ServerLevel level, LivingEntity caster, double radius, int duration) {
        TimeFreezeManager.create(level, caster, radius, duration);
    }

    public static boolean isEntityFrozen(Entity entity) {
        if (entity == null) {
            return false;
        }
        String dimension = entity.m_9236_().m_46472_().m_135782_().toString();
        for (TimeFreezeZone zone : ZONES) {
            Player player;
            if (zone == null || !zone.dimension.equals(dimension) || zone.preDelay > 0) continue;
            if (zone.casterUuid != null && zone.casterUuid.equals(entity.m_20148_())) {
                return false;
            }
            if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
                return false;
            }
            if (!(entity.m_20182_().m_82557_(zone.center) <= zone.radius * zone.radius)) continue;
            return true;
        }
        return false;
    }

    private static void sendTimeFreezeEndSync(ServerLevel serverLevel, TimeFreezeZone zone) {
        for (ServerPlayer serverPlayer : serverLevel.m_45976_(ServerPlayer.class, new AABB(zone.center.f_82479_ - 128.0, zone.center.f_82480_ - 128.0, zone.center.f_82481_ - 128.0, zone.center.f_82479_ + 128.0, zone.center.f_82480_ + 128.0, zone.center.f_82481_ + 128.0))) {
            GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(serverPlayer, zone.center.f_82479_, zone.center.f_82480_, zone.center.f_82481_, zone.radius, zone.casterUuid, false);
        }
    }

    static {
        try {
            CREEPER_SWELL_FIELD = Creeper.class.getDeclaredField("swell");
            CREEPER_SWELL_FIELD.setAccessible(true);
            CREEPER_OLD_SWELL_FIELD = Creeper.class.getDeclaredField("oldSwell");
            CREEPER_OLD_SWELL_FIELD.setAccessible(true);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static class TimeFreezeZone {
        private final String dimension;
        private final UUID casterUuid;
        private final Vec3 center;
        private final double radius;
        private int ticksLeft;
        private int preDelay = 0;
        private boolean playedStartSound = false;
        private boolean playedResumeSound = false;
        private int tickTimer = 0;
        private final Map<BlockPos, BlockState> frozenBlocks = new HashMap<BlockPos, BlockState>();
        private boolean initializedBlocks = false;

        private TimeFreezeZone(String dimension, UUID casterUuid, Vec3 center, double radius, int ticksLeft) {
            this.dimension = dimension;
            this.casterUuid = casterUuid;
            this.center = center;
            this.radius = radius;
            this.ticksLeft = ticksLeft;
        }
    }

    private static class FrozenEntityData {
        private final String dimension;
        private final Vec3 position;
        private final Vec3 motion;
        private final float yRot;
        private final float xRot;
        private final boolean hadNoAi;
        private final boolean hadNoGravity;
        private final int tntFuse;
        private final int creeperSwell;
        private final int creeperOldSwell;
        private final double xOld;
        private final double yOld;
        private final double zOld;
        private final float yRotO;
        private final float xRotO;

        private FrozenEntityData(Entity entity, String dimension) {
            this.dimension = dimension;
            this.position = entity.m_20182_();
            this.motion = entity.m_20184_();
            this.yRot = entity.m_146908_();
            this.xRot = entity.m_146909_();
            this.hadNoGravity = entity.m_20068_();
            this.xOld = entity.f_19790_;
            this.yOld = entity.f_19791_;
            this.zOld = entity.f_19792_;
            this.yRotO = entity.f_19859_;
            this.xRotO = entity.f_19860_;
            if (entity instanceof Mob) {
                Mob mob = (Mob)entity;
                this.hadNoAi = mob.m_21525_();
            } else {
                this.hadNoAi = false;
            }
            if (entity instanceof PrimedTnt) {
                PrimedTnt tnt = (PrimedTnt)entity;
                this.tntFuse = tnt.m_32100_();
            } else {
                this.tntFuse = 0;
            }
            int swell = 0;
            int oldSwell = 0;
            if (entity instanceof Creeper) {
                Creeper creeper = (Creeper)entity;
                if (CREEPER_SWELL_FIELD != null && CREEPER_OLD_SWELL_FIELD != null) {
                    try {
                        swell = CREEPER_SWELL_FIELD.getInt(creeper);
                        oldSwell = CREEPER_OLD_SWELL_FIELD.getInt(creeper);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            this.creeperSwell = swell;
            this.creeperOldSwell = oldSwell;
        }
    }

    @Mod.EventBusSubscriber
    private static class TimeFreezeManagerForgeBusEvents {
        private TimeFreezeManagerForgeBusEvents() {
        }

        @SubscribeEvent
        public static void serverLoad(ServerStartingEvent event) {
            ZONES.clear();
            FROZEN_ENTITIES.clear();
        }

        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Level level = event.level;
            if (!(level instanceof ServerLevel)) {
                return;
            }
            ServerLevel serverLevel = (ServerLevel)level;
            TimeFreezeManager.tickServerLevel(serverLevel);
        }

        @OnlyIn(value=Dist.CLIENT)
        @SubscribeEvent
        public static void clientLoad(FMLClientSetupEvent event) {
        }
    }
}

