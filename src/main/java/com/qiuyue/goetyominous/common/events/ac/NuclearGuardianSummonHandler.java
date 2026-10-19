package com.qiuyue.goetyominous.common.events.ac;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.NuclearGuardianServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import com.qiuyue.goetyominous.config.ac.AcConfig;

public class NuclearGuardianSummonHandler {

    private static final Item ANIMATION_CORE =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "animation_core"));

    private static final Direction[] HORIZONTAL = {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

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
        if (!player.isShiftKeyDown()) {
            return;
        }
        Level level = player.level();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(Blocks.DISPENSER)) {
            return;
        }
        BlockPos center = findCenter(level, pos);
        if (center == null || !checkStructure(level, center, pos)) {
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
        summon(serverLevel, level, center, stack, player, event);
    }

    private static BlockPos findCenter(Level level, BlockPos dispenserPos) {
        for (Direction direction : HORIZONTAL) {
            BlockPos candidate = dispenserPos.relative(direction);
            if (level.getBlockState(candidate).is(ACBlockRegistry.BLOCK_OF_URANIUM.get())) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean checkStructure(Level level, BlockPos center, BlockPos dispenserPos) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (dy == 0) {
                        if (dx == 0 && dz == 0) {
                            if (!state.is(ACBlockRegistry.BLOCK_OF_URANIUM.get())) return false;
                        } else if (Math.abs(dx) == 1 && Math.abs(dz) == 1) {
                            if (!state.is(ACBlockRegistry.SCRAP_METAL_PLATE.get())) return false;
                        } else if (pos.equals(dispenserPos)) {
                            if (!state.is(Blocks.DISPENSER)) return false;
                        } else if (!state.is(ACBlockRegistry.WASTE_DRUM.get())) {
                            return false;
                        }
                    } else {
                        if (dx == 0 && dz == 0) {
                            if (!state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())) return false;
                        } else if (Math.abs(dx) == 1 && Math.abs(dz) == 1) {
                            if (!state.is(ACBlockRegistry.SCRAP_METAL_PLATE.get())) return false;
                        } else if (!state.is(ACBlockRegistry.SCRAP_METAL.get())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean underLimit(ServerLevel level, Player player) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof NuclearGuardianServant servant
                    && servant.isAlive()
                    && servant.getTrueOwner() == player) {
                count++;
            }
        }
        return count < AcConfig.NuclearGuardianServantLimit.get();
    }

    private static void summon(ServerLevel serverLevel, Level level, BlockPos center, ItemStack stack,
                               Player player, PlayerInteractEvent.RightClickBlock event) {
        NuclearGuardianServant servant = AcEntityRegistry.NUCLEAR_GUARDIAN_SERVANT.get().create(level);
        if (servant == null) {
            return;
        }
        servant.setTrueOwner(player);

        double x = center.getX() + 0.5D;
        double z = center.getZ() + 0.5D;
        float yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * (180.0F / (float) Math.PI)) - 90.0F;
        servant.moveTo(x, center.getY(), z, yaw, 0.0F);
        servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(servant.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null, null);

        removeStructure(level, center);

        if (serverLevel.addFreshEntity(servant)) {
            stack.shrink(1);
            level.playSound(null, center, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, servant);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static void removeStructure(Level level, BlockPos center) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    level.levelEvent(2001, pos, Block.getId(state));
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
