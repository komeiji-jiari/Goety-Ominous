package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;


public class NucleeperSummonHandler {

    private static final Item ANIMATION_CORE =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "animation_core"));

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }
        
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (ANIMATION_CORE == null || !stack.is(ANIMATION_CORE)) {
            return;
        }
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        
        if (!level.getBlockState(pos).is(ACBlockRegistry.BLOCK_OF_URANIUM.get())) {
            return;
        }
        
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!underLimit(serverLevel, player)) {
            return;
        }
        if (!checkStructure(level, pos)) {
            return;
        }
        summon(serverLevel, level, pos, stack, player, event);
    }

    
    private static boolean checkStructure(Level level, BlockPos center) {
        return level.getBlockState(center.above(2)).is(ACBlockRegistry.SIREN_LIGHT.get())
                && level.getBlockState(center.above(1)).is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())
                && level.getBlockState(center.below(1)).is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())
                && level.getBlockState(center.below(2)).is(ACBlockRegistry.SCRAP_METAL.get());
    }

    private static boolean underLimit(ServerLevel level, Player player) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof NucleeperServant servant
                    && servant.isAlive()
                    && servant.getTrueOwner() == player) {
                count++;
            }
        }
        return count < MobsConfig.NucleeperServantLimit.get();
    }

    private static void summon(ServerLevel serverLevel, Level level, BlockPos pos, ItemStack stack,
                               Player player, PlayerInteractEvent.RightClickBlock event) {
        NucleeperServant servant = AcEntityRegistry.NUCLEEPER_SERVANT.get().create(level);
        if (servant == null) {
            return;
        }
        servant.setTrueOwner(player);
        
        BlockPos groundPos = pos.below(2);
        double x = pos.getX() + 0.5D;
        double z = pos.getZ() + 0.5D;
        
        float yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * (180.0F / (float) Math.PI)) - 90.0F;
        servant.moveTo(x, groundPos.getY() + 0.05D, z, yaw, 0.0F);
        servant.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(servant.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null, null);
        if (serverLevel.addFreshEntity(servant)) {
            removeStructure(level, pos);
            stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, servant);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    
    private static void removeStructure(Level level, BlockPos center) {
        BlockPos[] positions = {
                center.above(2), center.above(1), center, center.below(1), center.below(2)
        };
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            level.levelEvent(2001, pos, Block.getId(state));
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
