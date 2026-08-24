/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.AreaEffectCloud
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.entity.PartEntity
 *  net.minecraftforge.eventbus.api.Event
 *  org.joml.Vector3f
 */
package com.vivideru.masteryofmagic.goldification;

import com.vivideru.masteryofmagic.config.GameplayConfig;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.goldification.GoldificationAreaJob;
import com.vivideru.masteryofmagic.goldification.GoldificationEntityData;
import com.vivideru.masteryofmagic.goldification.GoldificationSavedData;
import com.vivideru.masteryofmagic.goldification.GoldificationTags;
import com.vivideru.masteryofmagic.goldification.GoldifiedBlockEntry;
import com.vivideru.masteryofmagic.goldification.event.GoldificationExpireEvent;
import com.vivideru.masteryofmagic.goldification.event.GoldifiedBlockShatterEvent;
import com.vivideru.masteryofmagic.goldification.event.GoldifiedEntityShatterEvent;
import com.vivideru.masteryofmagic.goldification.event.GoldifyBlockEvent;
import com.vivideru.masteryofmagic.goldification.event.GoldifyEntityEvent;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.eventbus.api.Event;
import org.joml.Vector3f;

public final class GoldificationManager {
    private static final Map<ResourceKey<Level>, List<GoldificationAreaJob>> AREA_JOBS = new HashMap<ResourceKey<Level>, List<GoldificationAreaJob>>();
    private static final Map<ResourceKey<Level>, PriorityQueue<EntityExpiry>> ENTITY_EXPIRIES = new HashMap<ResourceKey<Level>, PriorityQueue<EntityExpiry>>();
    private static final Map<ResourceKey<Level>, Set<UUID>> ACTIVE_ENTITIES = new HashMap<ResourceKey<Level>, Set<UUID>>();
    private static final Set<UUID> SHATTERING_ENTITIES = new HashSet<UUID>();

    private GoldificationManager() {
    }

    public static GoldificationAreaJob goldifyArea(ServerLevel level, Vec3 center, double radius, long durationTicks, @Nullable Entity source) {
        return GoldificationManager.goldifyArea(level, center, radius, durationTicks, source, null);
    }

    public static GoldificationAreaJob goldifyArea(ServerLevel level, Vec3 center, double radius, long durationTicks, @Nullable Entity source, @Nullable Consumer<GoldificationAreaJob.Result> completion) {
        GoldificationAreaJob job = new GoldificationAreaJob(level, center, Math.max(0.5, radius), Math.max(1L, durationTicks), source, completion);
        AREA_JOBS.computeIfAbsent((ResourceKey<Level>)level.m_46472_(), ignored -> new ArrayList()).add(job);
        GoldificationManager.playApplicationEffect(level, center);
        return job;
    }

    public static boolean goldifyBlock(ServerLevel level, BlockPos position, long durationTicks, @Nullable Entity source) {
        boolean changed = GoldificationManager.goldifyBlockUntil(level, position, level.m_46467_() + Math.max(1L, durationTicks), source, true);
        if (changed) {
            GoldificationManager.playApplicationEffect(level, Vec3.m_82512_((Vec3i)position));
        }
        return changed;
    }

    static boolean goldifyBlockUntil(ServerLevel level, BlockPos position, long expireGameTime, @Nullable Entity source, boolean synchronize) {
        return GoldificationManager.goldifyBlockUntil(level, position, expireGameTime, source, synchronize, false);
    }

    public static boolean goldifyBlockForMidas(ServerLevel level, BlockPos position, long durationTicks, @Nullable Entity source) {
        return GoldificationManager.goldifyBlockUntil(level, position, level.m_46467_() + Math.max(1L, durationTicks), source, true, true);
    }

    private static boolean goldifyBlockUntil(ServerLevel level, BlockPos position, long expireGameTime, @Nullable Entity source, boolean synchronize, boolean midasOverride) {
        long autoShatter;
        long packed;
        if (midasOverride ? !GoldificationManager.isValidMidasAuraBlock(level, position) : !GoldificationManager.isValidBlock(level, position)) {
            return false;
        }
        GoldificationSavedData data = GoldificationSavedData.get(level);
        GoldifiedBlockEntry existing = data.get(packed = position.m_121878_());
        if (existing != null && existing.expireGameTime() >= expireGameTime) {
            return false;
        }
        BlockState state = level.m_8055_(position);
        GoldifyBlockEvent event = new GoldifyBlockEvent(level, position, state, expireGameTime, source);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        long created = existing == null ? level.m_46467_() : existing.createdGameTime();
        long l = autoShatter = existing == null ? -1L : existing.autoShatterGameTime();
        UUID sourceUuid = source != null ? source.m_20148_() : (existing == null ? null : existing.sourceUuid());
        data.put(packed, new GoldifiedBlockEntry(expireGameTime, created, autoShatter, sourceUuid));
        if (synchronize) {
            GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)LongArrayList.wrap((long[])new long[]{packed}), true);
        }
        return true;
    }

    public static boolean goldifyEntity(Entity entity, long durationTicks, @Nullable Entity source) {
        Level level = entity.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel level2 = (ServerLevel)level;
        boolean changed = GoldificationManager.goldifyEntityUntil(entity, level2.m_46467_() + Math.max(1L, durationTicks), source, true);
        if (changed) {
            GoldificationManager.playApplicationEffect(level2, entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.5, 0.0));
        }
        return changed;
    }

    static boolean goldifyEntityUntil(Entity entity, long expireGameTime, @Nullable Entity source, boolean synchronize) {
        return GoldificationManager.goldifyEntityUntil(entity, expireGameTime, source, synchronize, false);
    }

    public static boolean goldifyEntityForMidas(Entity entity, long durationTicks, @Nullable Entity source) {
        Level level = entity.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel level2 = (ServerLevel)level;
        return GoldificationManager.goldifyEntityUntil(entity, level2.m_46467_() + Math.max(1L, durationTicks), source, true, true);
    }

    private static boolean goldifyEntityUntil(Entity entity, long expireGameTime, @Nullable Entity source, boolean synchronize, boolean midasOverride) {
        long existingExpire;
        ServerLevel level;
        block7: {
            block6: {
                Level level2 = entity.m_9236_();
                if (!(level2 instanceof ServerLevel)) break block6;
                level = (ServerLevel)level2;
                if (!(midasOverride ? !GoldificationManager.isValidMidasAuraEntity(entity, source) : !GoldificationManager.isValidEntity(entity))) break block7;
            }
            return false;
        }
        long l = existingExpire = GoldificationEntityData.isGoldified(entity) ? GoldificationEntityData.getExpireGameTime(entity) : Long.MIN_VALUE;
        if (existingExpire >= expireGameTime) {
            return false;
        }
        GoldifyEntityEvent event = new GoldifyEntityEvent(entity, expireGameTime, source);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        long created = existingExpire == Long.MIN_VALUE ? level.m_46467_() : GoldificationEntityData.getCreatedGameTime(entity);
        long autoShatter = existingExpire == Long.MIN_VALUE ? -1L : GoldificationEntityData.getAutoShatterGameTime(entity);
        UUID sourceUuid = source != null ? source.m_20148_() : GoldificationEntityData.getSourceUuid(entity);
        GoldificationEntityData.set(entity, expireGameTime, created, autoShatter, sourceUuid);
        ACTIVE_ENTITIES.computeIfAbsent((ResourceKey<Level>)level.m_46472_(), ignored -> new HashSet()).add(entity.m_20148_());
        GoldificationManager.queueEntityExpiry(level, entity.m_20148_(), expireGameTime);
        if (synchronize) {
            GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(entity, true);
        }
        return true;
    }

    public static boolean removeBlockGoldification(ServerLevel level, BlockPos position) {
        if (GoldificationSavedData.get(level).remove(position.m_121878_()) == null) {
            return false;
        }
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)LongArrayList.wrap((long[])new long[]{position.m_121878_()}), false);
        GoldificationManager.resumeFluid(level, position);
        return true;
    }

    public static boolean removeEntityGoldification(Entity entity) {
        if (!GoldificationEntityData.clear(entity)) {
            return false;
        }
        Level level = entity.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            GoldificationManager.removeActiveEntity(level2, entity.m_20148_());
        }
        if (entity.m_9236_() instanceof ServerLevel) {
            GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(entity, false);
        }
        return true;
    }

    public static boolean isBlockGoldified(ServerLevel level, BlockPos position) {
        GoldificationSavedData data = GoldificationSavedData.get(level);
        GoldifiedBlockEntry entry = data.get(position.m_121878_());
        if (entry == null) {
            return false;
        }
        if (entry.expireGameTime() <= level.m_46467_()) {
            data.remove(position.m_121878_());
            GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)LongArrayList.wrap((long[])new long[]{position.m_121878_()}), false);
            MinecraftForge.EVENT_BUS.post((Event)GoldificationExpireEvent.forBlock(level, position));
            return false;
        }
        return true;
    }

    public static boolean isEntityGoldified(Entity entity) {
        if (!GoldificationEntityData.isGoldified(entity)) {
            return false;
        }
        Level level = entity.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            if (GoldificationEntityData.getExpireGameTime(entity) <= level2.m_46467_()) {
                GoldificationEntityData.clear(entity);
                GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(entity, false);
                MinecraftForge.EVENT_BUS.post((Event)GoldificationExpireEvent.forEntity(level2, entity));
                return false;
            }
        }
        return true;
    }

    public static boolean shatterBlock(ServerLevel level, BlockPos position, @Nullable Entity breaker) {
        GoldificationSavedData data = GoldificationSavedData.get(level);
        GoldifiedBlockEntry entry = data.get(position.m_121878_());
        if (entry == null || entry.expireGameTime() <= level.m_46467_()) {
            return false;
        }
        BlockState state = level.m_8055_(position);
        if (state.m_60795_()) {
            GoldificationManager.removeBlockGoldification(level, position);
            return false;
        }
        GoldifiedBlockShatterEvent event = new GoldifiedBlockShatterEvent(level, position, state, breaker, GoldificationManager.randomNuggetCount(level));
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        if (!level.m_7731_(position, Blocks.f_50016_.m_49966_(), 35)) {
            return false;
        }
        data.remove(position.m_121878_());
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)LongArrayList.wrap((long[])new long[]{position.m_121878_()}), false);
        GoldificationManager.spawnNuggets(level, Vec3.m_82512_((Vec3i)position), event.getNuggetCount());
        GoldificationManager.playShatterEffect(level, Vec3.m_82512_((Vec3i)position), SoundSource.BLOCKS);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean shatterEntity(Entity target, @Nullable Entity attacker) {
        ServerLevel level;
        block12: {
            block11: {
                Level level2 = target.m_9236_();
                if (!(level2 instanceof ServerLevel)) break block11;
                level = (ServerLevel)level2;
                if (GoldificationManager.isEntityGoldified(target)) break block12;
            }
            return false;
        }
        GoldifiedEntityShatterEvent event = new GoldifiedEntityShatterEvent(target, attacker, GoldificationManager.randomNuggetCount(level));
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        if (!GoldificationEntityData.clear(target)) {
            return false;
        }
        GoldificationManager.removeActiveEntity(level, target.m_20148_());
        Vec3 dropPosition = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0);
        GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(target, false);
        SHATTERING_ENTITIES.add(target.m_20148_());
        try {
            if (target instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)target;
                player.m_6074_();
            } else {
                target.m_146870_();
            }
        }
        finally {
            SHATTERING_ENTITIES.remove(target.m_20148_());
        }
        GoldificationManager.spawnNuggets(level, dropPosition, event.getNuggetCount());
        GoldificationManager.playShatterEffect(level, dropPosition, SoundSource.HOSTILE);
        return true;
    }

    public static boolean isShattering(Entity entity) {
        return SHATTERING_ENTITIES.contains(entity.m_20148_());
    }

    public static ClearResult clearArea(ServerLevel level, Vec3 center, double radius) {
        LongArrayList removedBlocks = GoldificationSavedData.get(level).clearWithin(center.f_82479_, center.f_82480_, center.f_82481_, radius);
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)removedBlocks, false);
        LongListIterator longListIterator = removedBlocks.iterator();
        while (longListIterator.hasNext()) {
            long packed = (Long)longListIterator.next();
            GoldificationManager.resumeFluid(level, BlockPos.m_122022_((long)packed));
        }
        int entities = 0;
        AABB bounds = new AABB(center.f_82479_ - radius, center.f_82480_ - radius, center.f_82481_ - radius, center.f_82479_ + radius, center.f_82480_ + radius, center.f_82481_ + radius);
        double radiusSquared = radius * radius;
        for (Entity entity : level.m_6249_((Entity)null, bounds, candidate -> candidate.m_20182_().m_82557_(center) <= radiusSquared)) {
            if (!GoldificationManager.removeEntityGoldification(entity)) continue;
            ++entities;
        }
        return new ClearResult(removedBlocks.size(), entities);
    }

    public static ClearResult clearAll(ServerLevel level) {
        LongArrayList removedBlocks = GoldificationSavedData.get(level).clearAll();
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)removedBlocks, false);
        LongListIterator longListIterator = removedBlocks.iterator();
        while (longListIterator.hasNext()) {
            long packed = (Long)longListIterator.next();
            GoldificationManager.resumeFluid(level, BlockPos.m_122022_((long)packed));
        }
        int entities = 0;
        for (Entity entity : level.m_8583_()) {
            if (!GoldificationManager.removeEntityGoldification(entity)) continue;
            ++entities;
        }
        return new ClearResult(removedBlocks.size(), entities);
    }

    public static void tick(ServerLevel level) {
        PriorityQueue<EntityExpiry> expiries;
        long gameTime = level.m_46467_();
        LongArrayList expiredBlocks = GoldificationSavedData.get(level).removeExpired(gameTime);
        if (!expiredBlocks.isEmpty()) {
            GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(level, (LongCollection)expiredBlocks, false);
            LongListIterator longListIterator = expiredBlocks.iterator();
            while (longListIterator.hasNext()) {
                long packed = (Long)longListIterator.next();
                GoldificationManager.resumeFluid(level, BlockPos.m_122022_((long)packed));
                MinecraftForge.EVENT_BUS.post((Event)GoldificationExpireEvent.forBlock(level, BlockPos.m_122022_((long)packed)));
            }
        }
        if ((expiries = ENTITY_EXPIRIES.get(level.m_46472_())) != null) {
            while (!expiries.isEmpty() && expiries.peek().expireGameTime <= gameTime) {
                EntityExpiry expiry = expiries.poll();
                Entity entity = level.m_8791_(expiry.entityUuid);
                if (entity == null || !GoldificationEntityData.isGoldified(entity) || GoldificationEntityData.getExpireGameTime(entity) != expiry.expireGameTime) continue;
                GoldificationEntityData.clear(entity);
                GoldificationManager.removeActiveEntity(level, entity.m_20148_());
                GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(entity, false);
                MinecraftForge.EVENT_BUS.post((Event)GoldificationExpireEvent.forEntity(level, entity));
            }
        }
        Set<UUID> activeEntities = ACTIVE_ENTITIES.get(level.m_46472_());
        ArrayList<Entity> impactShatters = new ArrayList<Entity>();
        if (activeEntities != null) {
            Iterator<UUID> iterator = activeEntities.iterator();
            while (iterator.hasNext()) {
                UUID entityUuid = iterator.next();
                Entity entity = level.m_8791_(entityUuid);
                if (entity == null || !GoldificationEntityData.isGoldified(entity)) {
                    iterator.remove();
                    continue;
                }
                GoldificationEntityData.enforceFrozenState(entity);
                if (!GoldificationEntityData.shouldShatterOnGroundImpact(entity)) continue;
                impactShatters.add(entity);
            }
            if (activeEntities.isEmpty()) {
                ACTIVE_ENTITIES.remove(level.m_46472_());
            }
        }
        for (Entity entity : impactShatters) {
            GoldificationManager.shatterEntity(entity, null);
        }
        List<GoldificationAreaJob> jobs = AREA_JOBS.get(level.m_46472_());
        if (jobs != null) {
            Iterator<GoldificationAreaJob> iterator = jobs.iterator();
            int budget = (Integer)GameplayConfig.GOLDIFICATION_BLOCKS_PER_TICK.get();
            while (iterator.hasNext()) {
                GoldificationAreaJob job = iterator.next();
                job.tick(budget);
                if (!job.isFinished()) continue;
                iterator.remove();
            }
        }
    }

    public static void onEntityJoin(Entity entity) {
        ServerLevel level;
        block6: {
            block5: {
                Level level2 = entity.m_9236_();
                if (!(level2 instanceof ServerLevel)) break block5;
                level = (ServerLevel)level2;
                if (GoldificationEntityData.isGoldified(entity)) break block6;
            }
            return;
        }
        long expire = GoldificationEntityData.getExpireGameTime(entity);
        if (expire <= level.m_46467_()) {
            GoldificationEntityData.clear(entity);
        } else {
            ACTIVE_ENTITIES.computeIfAbsent((ResourceKey<Level>)level.m_46472_(), ignored -> new HashSet()).add(entity.m_20148_());
            GoldificationEntityData.enforceFrozenState(entity);
            GoldificationManager.queueEntityExpiry(level, entity.m_20148_(), expire);
        }
    }

    public static void syncChunk(ServerPlayer player, ServerLevel level, ChunkPos chunk) {
        LongArrayList positions = GoldificationSavedData.get(level).getValidPositionsInChunk(chunk, level.m_46467_());
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(player, (ResourceKey<Level>)level.m_46472_(), (LongCollection)positions, true);
    }

    public static void syncPlayer(ServerPlayer player) {
        ServerLevel level = player.m_284548_();
        GoetyMasteryOfMagicNetwork.clearGoldificationClientState(player, (ResourceKey<Level>)level.m_46472_());
        double radius = (double)(player.f_8924_.m_6846_().m_11312_() + 2) * 16.0;
        LongArrayList positions = GoldificationSavedData.get(level).getValidPositionsNear(player.m_20185_(), player.m_20189_(), radius, level.m_46467_());
        GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(player, (ResourceKey<Level>)level.m_46472_(), (LongCollection)positions, true);
    }

    public static void onLevelUnload(ServerLevel level) {
        AREA_JOBS.remove(level.m_46472_());
        ENTITY_EXPIRIES.remove(level.m_46472_());
        ACTIVE_ENTITIES.remove(level.m_46472_());
    }

    public static boolean isValidBlock(ServerLevel level, BlockPos position) {
        BlockState state = level.m_8055_(position);
        boolean liquid = state.m_60734_() instanceof LiquidBlock;
        if (state.m_60795_() || !liquid && state.m_60799_() == RenderShape.INVISIBLE || state.m_204336_(GoldificationTags.IMMUNE_BLOCKS)) {
            return false;
        }
        if (!((Boolean)GameplayConfig.GOLDIFICATION_GOLDIFY_BLOCK_ENTITIES.get()).booleanValue() && level.m_7702_(position) != null) {
            return false;
        }
        return !state.m_60713_(Blocks.f_50752_) && !state.m_60713_(Blocks.f_50257_) && !state.m_60713_(Blocks.f_50258_) && !state.m_60713_(Blocks.f_50142_) && !state.m_60713_(Blocks.f_50272_) && !state.m_60713_(Blocks.f_50448_) && !state.m_60713_(Blocks.f_50447_) && !state.m_60713_(Blocks.f_50677_) && !state.m_60713_(Blocks.f_50678_) && !state.m_60713_(Blocks.f_50375_) && !state.m_60713_(Blocks.f_152480_) && !state.m_60713_(Blocks.f_50110_);
    }

    public static boolean isValidEntity(Entity entity) {
        ArmorStand armorStand;
        if (entity.m_213877_() || entity instanceof PhilosopherKingMidasEntity || entity.m_6095_().m_204039_(GoldificationTags.IMMUNE_ENTITY_TYPES) || entity instanceof LightningBolt || entity instanceof AreaEffectCloud || entity instanceof ExperienceOrb || entity.isMultipartEntity()) {
            return false;
        }
        if (entity instanceof ArmorStand && (armorStand = (ArmorStand)entity).m_31677_()) {
            return false;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return !player.m_7500_() && !player.m_5833_() && (Boolean)GameplayConfig.GOLDIFICATION_GOLDIFY_PLAYERS.get() != false;
        }
        return true;
    }

    private static boolean isValidMidasAuraBlock(ServerLevel level, BlockPos position) {
        BlockState state = level.m_8055_(position);
        return !state.m_60795_() && !state.m_60713_(Blocks.f_50375_) && !state.m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get());
    }

    private static boolean isValidMidasAuraEntity(Entity entity, @Nullable Entity source) {
        if (entity.m_213877_() || entity == source || entity instanceof PhilosopherKingMidasEntity || entity instanceof PartEntity) {
            return false;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return !player.m_7500_() && !player.m_5833_();
        }
        return true;
    }

    private static void queueEntityExpiry(ServerLevel level, UUID entityUuid, long expireGameTime) {
        ENTITY_EXPIRIES.computeIfAbsent((ResourceKey<Level>)level.m_46472_(), ignored -> new PriorityQueue<EntityExpiry>(Comparator.comparingLong(EntityExpiry::expireGameTime))).add(new EntityExpiry(entityUuid, expireGameTime));
    }

    private static void removeActiveEntity(ServerLevel level, UUID entityUuid) {
        Set<UUID> entities = ACTIVE_ENTITIES.get(level.m_46472_());
        if (entities != null) {
            entities.remove(entityUuid);
            if (entities.isEmpty()) {
                ACTIVE_ENTITIES.remove(level.m_46472_());
            }
        }
    }

    private static void resumeFluid(ServerLevel level, BlockPos position) {
        FluidState fluidState = level.m_6425_(position);
        if (!fluidState.m_76178_()) {
            level.m_186469_(position, fluidState.m_76152_(), 1);
        }
    }

    private static int randomNuggetCount(ServerLevel level) {
        int configuredMin = (Integer)GameplayConfig.GOLDIFICATION_NUGGET_DROP_MIN.get();
        int configuredMax = (Integer)GameplayConfig.GOLDIFICATION_NUGGET_DROP_MAX.get();
        int min = Math.min(configuredMin, configuredMax);
        int max = Math.max(configuredMin, configuredMax);
        return min + (max > min ? level.f_46441_.m_188503_(max - min + 1) : 0);
    }

    private static void spawnNuggets(ServerLevel level, Vec3 position, int amount) {
        if (amount <= 0) {
            return;
        }
        ItemEntity item = new ItemEntity((Level)level, position.f_82479_, position.f_82480_, position.f_82481_, new ItemStack((ItemLike)Items.f_42587_, amount));
        item.m_32060_();
        level.m_7967_((Entity)item);
    }

    private static void playApplicationEffect(ServerLevel level, Vec3 position) {
        level.m_8767_((ParticleOptions)new DustParticleOptions(new Vector3f(1.0f, 0.64f, 0.08f), 1.0f), position.f_82479_, position.f_82480_, position.f_82481_, 12, 0.45, 0.45, 0.45, 0.02);
        level.m_5594_(null, BlockPos.m_274446_((Position)position), SoundEvents.f_12065_, SoundSource.BLOCKS, 0.8f, 0.82f);
    }

    private static void playShatterEffect(ServerLevel level, Vec3 position, SoundSource source) {
        level.m_8767_((ParticleOptions)new BlockParticleOption(ParticleTypes.f_123794_, Blocks.f_50074_.m_49966_()), position.f_82479_, position.f_82480_, position.f_82481_, 28, 0.5, 0.5, 0.5, 0.08);
        level.m_5594_(null, BlockPos.m_274446_((Position)position), SoundEvents.f_12062_, source, 1.0f, 0.72f);
        level.m_5594_(null, BlockPos.m_274446_((Position)position), SoundEvents.f_12064_, source, 0.7f, 1.05f);
    }

    public record ClearResult(int blocks, int entities) {
    }

    private record EntityExpiry(UUID entityUuid, long expireGameTime) {
    }
}

