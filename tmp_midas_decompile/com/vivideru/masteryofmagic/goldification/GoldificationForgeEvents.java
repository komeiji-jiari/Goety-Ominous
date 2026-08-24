/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraftforge.event.PlayLevelSoundEvent
 *  net.minecraftforge.event.PlayLevelSoundEvent$AtEntity
 *  net.minecraftforge.event.PlayLevelSoundEvent$AtPosition
 *  net.minecraftforge.event.TickEvent$LevelTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingExperienceDropEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$StartTracking
 *  net.minecraftforge.event.entity.player.PlayerEvent$StopTracking
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.event.level.BlockEvent$FluidPlaceBlockEvent
 *  net.minecraftforge.event.level.ChunkWatchEvent$UnWatch
 *  net.minecraftforge.event.level.ChunkWatchEvent$Watch
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.event.level.LevelEvent$Unload
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.goldification;

import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import com.vivideru.masteryofmagic.goldification.GoldificationSoundUtil;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class GoldificationForgeEvents {
    private GoldificationForgeEvents() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        Level level;
        if (event.phase == TickEvent.Phase.END && (level = event.level) instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            GoldificationManager.tick(level2);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().m_5776_()) {
            GoldificationManager.onEntityJoin(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Level level = event.getEntity().m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            event.getPosition().ifPresent(position -> {
                if (GoldificationManager.isBlockGoldified(level2, position)) {
                    event.setNewSpeed(1000000.0f);
                }
            });
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        ServerLevel level;
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel) || !GoldificationManager.isBlockGoldified(level = (ServerLevel)levelAccessor, event.getPos())) {
            return;
        }
        event.setCanceled(true);
        GoldificationManager.shatterBlock(level, event.getPos(), (Entity)event.getPlayer());
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLeftClickGoldifiedLiquid(PlayerInteractEvent.LeftClickBlock event) {
        ServerLevel level;
        Level level2 = event.getLevel();
        if (!(level2 instanceof ServerLevel) || (level = (ServerLevel)level2).m_6425_(event.getPos()).m_76178_() || !GoldificationManager.isBlockGoldified(level, event.getPos())) {
            return;
        }
        event.setCanceled(true);
        GoldificationManager.shatterBlock(level, event.getPos(), (Entity)event.getEntity());
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getAmount() > 0.0f && GoldificationManager.shatterEntity((Entity)event.getEntity(), event.getSource().m_7639_())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getAmount() > 0.0f && GoldificationManager.shatterEntity((Entity)event.getEntity(), event.getSource().m_7639_())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity) && GoldificationManager.shatterEntity(target, (Entity)event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (GoldificationManager.isShattering((Entity)event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingExperience(LivingExperienceDropEvent event) {
        if (GoldificationManager.isShattering((Entity)event.getEntity())) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        for (BlockPos position : new ArrayList(event.getAffectedBlocks())) {
            if (!GoldificationManager.isBlockGoldified(level2, position)) continue;
            GoldificationManager.shatterBlock(level2, position, event.getExplosion().getExploder());
            event.getAffectedBlocks().remove(position);
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        ServerLevel level;
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor instanceof ServerLevel && GoldificationManager.isBlockGoldified(level = (ServerLevel)levelAccessor, event.getPos())) {
            GoldificationManager.removeBlockGoldification(level, event.getPos());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        ServerLevel level;
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor instanceof ServerLevel && GoldificationManager.isBlockGoldified(level = (ServerLevel)levelAccessor, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockSound(PlayLevelSoundEvent.AtPosition event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        BlockPos position = BlockPos.m_274446_((Position)event.getPosition());
        if (GoldificationManager.isBlockGoldified(level2, position)) {
            GoldificationSoundUtil.remapBlockSound((PlayLevelSoundEvent)event, position, true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onEntitySound(PlayLevelSoundEvent.AtEntity event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            if (GoldificationManager.isEntityGoldified(event.getEntity()) || GoldificationManager.isShattering(event.getEntity())) {
                event.setCanceled(true);
                return;
            }
            BlockPos position = event.getEntity().m_20097_();
            if (GoldificationManager.isBlockGoldified(level2, position)) {
                GoldificationSoundUtil.remapBlockSound((PlayLevelSoundEvent)event, position, false);
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            if (GoldificationManager.isEntityGoldified(event.getTarget())) {
                GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(player2, event.getTarget(), true);
            }
        }
    }

    @SubscribeEvent
    public static void onStopTracking(PlayerEvent.StopTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            GoetyMasteryOfMagicNetwork.sendGoldifiedEntity(player2, event.getTarget(), false);
        }
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        GoldificationManager.syncChunk(event.getPlayer(), event.getLevel(), event.getPos());
    }

    @SubscribeEvent
    public static void onChunkUnwatch(ChunkWatchEvent.UnWatch event) {
        GoetyMasteryOfMagicNetwork.clearGoldificationChunk(event.getPlayer(), (ResourceKey<Level>)event.getLevel().m_46472_(), event.getPos());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            GoldificationManager.syncPlayer(player2);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            GoldificationManager.syncPlayer(player2);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)levelAccessor;
            GoldificationManager.onLevelUnload(level);
        }
    }
}

