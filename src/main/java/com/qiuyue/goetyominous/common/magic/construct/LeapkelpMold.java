package com.qiuyue.goetyominous.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.CorpseBlossomBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.magic.construct.LeapleafMold;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LeapkelpMold implements IMold {

    private static final BlockPos CORPSE_BLOSSOM = new BlockPos(0, 1, 0);

    public static boolean isKelpAltar(Level level, BlockPos blockPos) {
        BlockState roots = level.getBlockState(blockPos);
        if (!roots.is(ModBlocks.OVERGROWN_ROOTS.get())
                || !roots.hasProperty(BlockStateProperties.WATERLOGGED)
                || !roots.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
        }
        BlockState blossom = level.getBlockState(blockPos.offset(CORPSE_BLOSSOM));
        return blossom.is(ModBlocks.CORPSE_BLOSSOM.get())
                && blossom.getValue(CorpseBlossomBlock.WATERLOGGED);
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (level.isClientSide || !isKelpAltar(level, blockPos)) {
            return false;
        }
        if (!SEHelper.hasResearch(player, ResearchList.FLORAL)) {
            player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
            return false;
        }
        if (!this.conditionsMet(level, player)) {
            player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
            return false;
        }
        if (!LeapleafMold.canSpawn(level, blockPos)) {
            player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
            return false;
        }
        Leapkelp leapkelp = ModEntityTypes.LEAPKELP.get().create(level);
        if (leapkelp == null) {
            return false;
        }
        leapkelp.setTrueOwner(player);
        leapkelp.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(leapkelp.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
        leapkelp.moveTo(blockPos.getX() + 0.5D, blockPos.below().getY() + 0.05D, blockPos.getZ() + 0.5D, 0.0F, 0.0F);
        if (!level.addFreshEntity(leapkelp)) {
            return false;
        }
        LeapleafMold.removeBlocks(level, blockPos);
        stack.shrink(1);
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, leapkelp);
        }
        return true;
    }

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof Leapleaf servant && servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                    ++count;
                }
            }
        }
        return count < SpellConfig.LeapleafLimit.get();
    }
}
