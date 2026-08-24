/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class BlackstoneChaliceInteractHandler {
    private static final int VIAL_COST = 1000;
    private static final int BUCKET_COST = 6000;

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos;
        if (event.getLevel().m_5776_()) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockEntity blockEntity = level.m_7702_(pos = event.getPos());
        if (!(blockEntity instanceof BlackstoneChaliceBlockEntity)) {
            return;
        }
        BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)blockEntity;
        ItemStack held = player.m_21120_(event.getHand());
        if (!be.hasOwner()) {
            be.setOwnerIfAbsent(player.m_20148_().toString(), player.m_7755_().getString());
        }
        if (held.m_150930_(Items.f_42590_)) {
            if (!be.consumeBlood(1000)) {
                return;
            }
            if (!player.m_150110_().f_35937_) {
                held.m_41774_(1);
            }
            ItemStack vial = new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get());
            if (be.hasOwner()) {
                UndeadBloodVialItem.setSource(vial, UUID.fromString(be.getOwnerUUID()), be.getOwnerName());
            }
            if (!player.m_150109_().m_36054_(vial)) {
                player.m_36176_(vial, false);
            }
            level.m_5594_(null, pos, SoundEvents.f_11770_, SoundSource.BLOCKS, 1.0f, 1.0f);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        if (held.m_150930_(Items.f_42446_)) {
            if (!be.consumeBlood(6000)) {
                return;
            }
            if (!player.m_150110_().f_35937_) {
                held.m_41774_(1);
            }
            ItemStack bucket = new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_BUCKET.get());
            if (be.hasOwner()) {
                UndeadBloodVialItem.setSource(bucket, UUID.fromString(be.getOwnerUUID()), be.getOwnerName());
            }
            if (!player.m_150109_().m_36054_(bucket)) {
                player.m_36176_(bucket, false);
            }
            level.m_5594_(null, pos, SoundEvents.f_11781_, SoundSource.BLOCKS, 1.0f, 1.0f);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}

