/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.event;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.vivideru.masteryofmagic.magic.RunedLazethystHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RunedLazethystRightClickEvent {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        boolean isRuned;
        Level level = event.getLevel();
        if (level.f_46443_) {
            return;
        }
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!(stack.m_41720_() instanceof IFocus)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = level.m_8055_(pos);
        ResourceLocation blockId = BuiltInRegistries.f_256975_.m_7981_((Object)state.m_60734_());
        if (blockId == null) {
            return;
        }
        String path = blockId.m_135815_();
        boolean bl = isRuned = path.contains("runed_lazethyst_block") || path.contains("runed_lazethys_block");
        if (!isRuned) {
            return;
        }
        InteractionResult result = RunedLazethystHandler.handleUse(state, level, pos, player, hand);
        if (result.m_19077_()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}

