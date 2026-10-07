package com.qiuyue.goetyominous.common.events.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
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

public class MineGuardianSummonHandler {

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
        Level level = event.getEntity().level();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(AcBlockRegistry.MINE_GUARDIAN_BLOCK.get())) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Player player = event.getEntity();
        if (!underLimit(serverLevel, player)) {
            return;
        }
        summon(serverLevel, stack, pos, player, event);
    }

    private static boolean underLimit(ServerLevel level, Player player) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof MineGuardianServant servant
                    && servant.isAlive()
                    && servant.getTrueOwner() == player) {
                count++;
            }
        }
        return count < MobsConfig.MineGuardianServantLimit.get();
    }

    private static void summon(ServerLevel serverLevel, ItemStack stack, BlockPos pos, Player player,
                               PlayerInteractEvent.RightClickBlock event) {
        MineGuardianServant servant = AcEntityRegistry.MINE_GUARDIAN_SERVANT.get().create(serverLevel);
        if (servant == null) {
            return;
        }
        servant.setTrueOwner(player);

        double x = pos.getX() + 0.5D;
        double z = pos.getZ() + 0.5D;
        float yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * (180.0F / (float) Math.PI)) - 90.0F;
        servant.moveTo(x, pos.getY(), z, yaw, 0.0F);
        if (!serverLevel.noCollision(servant)) {
            servant.moveTo(x, pos.getY() + 1.0D, z, yaw, 0.0F);
        }
        servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(servant.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null, null);

        if (serverLevel.addFreshEntity(servant)) {
            BlockState state = serverLevel.getBlockState(pos);
            serverLevel.levelEvent(2001, pos, Block.getId(state));
            serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            stack.shrink(1);
            serverLevel.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, servant);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
