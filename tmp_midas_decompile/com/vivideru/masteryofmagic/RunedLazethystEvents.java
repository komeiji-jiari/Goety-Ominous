/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RunedLazethystEvents {
    private static final int TARGET_RANGE = 20;

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (level.f_46443_) {
            return;
        }
        Player player = event.getEntity();
        if (!player.m_6144_()) {
            return;
        }
        if (!(player.m_21205_().m_41720_() instanceof IWand)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.m_7702_(pos);
        if (!(blockEntity instanceof ChargedRunedLazethystBlockEntity)) {
            return;
        }
        ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
        if (be.ownerUUID != null && !be.ownerUUID.equals(player.m_20148_())) {
            return;
        }
        event.setCanceled(true);
        level.m_5594_(null, pos, SoundEvents.f_11887_, SoundSource.BLOCKS, 1.0f, 1.0f);
        Block uncharged = RunedLazethystEvents.resolveUnchargedFromCharged(level, pos);
        if (uncharged != null) {
            level.m_7731_(pos, uncharged.m_49966_(), 3);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.f_46443_) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (!(held.m_41720_() instanceof IWand)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.m_7702_(pos);
        if (!(blockEntity instanceof ChargedRunedLazethystBlockEntity)) {
            return;
        }
        ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
        if (be.ownerUUID != null && !be.ownerUUID.equals(player.m_20148_())) {
            return;
        }
        be.cycleTargetMode();
        level.m_5594_(null, pos, (SoundEvent)SoundEvents.f_12490_.m_203334_(), SoundSource.BLOCKS, 0.6f, 1.0f);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static Block resolveUnchargedFromCharged(Level level, BlockPos pos) {
        ResourceLocation id = BuiltInRegistries.f_256975_.m_7981_((Object)level.m_8055_(pos).m_60734_());
        if (id == null) {
            return null;
        }
        String path = id.m_135815_();
        if ((path = path.replace("runed_lazethys_block", "runed_lazethyst_block")).endsWith("_charged")) {
            path = path.substring(0, path.length() - "_charged".length());
        } else if (path.endsWith("_block_charged")) {
            path = path.substring(0, path.length() - "_block_charged".length());
        } else if (path.contains("_charged")) {
            path = path.replace("_charged", "");
        }
        ResourceLocation unchargedId = new ResourceLocation(id.m_135827_(), path);
        Block b = (Block)BuiltInRegistries.f_256975_.m_7745_(unchargedId);
        if (b == null) {
            return (Block)GoetyMasteryOfMagicModBlocks.RUNED_LAZETHYST_BLOCK.get();
        }
        return b;
    }

    public static int getTargetRange() {
        return 20;
    }
}

