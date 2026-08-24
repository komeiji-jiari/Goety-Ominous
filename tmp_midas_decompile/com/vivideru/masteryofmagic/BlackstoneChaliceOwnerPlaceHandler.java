/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.common.util.BlockSnapshot
 *  net.minecraftforge.event.level.BlockEvent$EntityMultiPlaceEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class BlackstoneChaliceOwnerPlaceHandler {
    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel)) {
            return;
        }
        ServerLevel level = (ServerLevel)levelAccessor;
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        BlockPos pos = event.getPos();
        String uuid = player.m_20148_().toString();
        level.m_7654_().execute(() -> {
            BlockEntity patt988$temp = level.m_7702_(pos);
            if (patt988$temp instanceof BlackstoneChaliceBlockEntity) {
                BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)patt988$temp;
                be.setOwnerIfAbsent(uuid);
            }
        });
    }

    @SubscribeEvent
    public static void onMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel)) {
            return;
        }
        ServerLevel level = (ServerLevel)levelAccessor;
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        String uuid = player.m_20148_().toString();
        for (BlockSnapshot placed : event.getReplacedBlockSnapshots()) {
            BlockPos pos = placed.getPos();
            level.m_7654_().execute(() -> {
                BlockEntity patt1508$temp = level.m_7702_(pos);
                if (patt1508$temp instanceof BlackstoneChaliceBlockEntity) {
                    BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)patt1508$temp;
                    be.setOwnerIfAbsent(uuid);
                }
            });
        }
    }
}

