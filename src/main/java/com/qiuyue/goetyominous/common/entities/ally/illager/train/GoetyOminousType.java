package com.qiuyue.goetyominous.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.init.ModTags;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import com.Polarice3.Goety.common.entities.ModEntityType;

public class GoetyOminousType implements ITrainIllager {

    @Override
    public boolean canSpawn(Level level, BlockPos blockPos, int range) {
        return this.getIllager(level, blockPos, range) != null;
    }

    @Override
    public boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
        EntityType<?> entityType = this.getIllager(level, blockPos, range);
        if (entityType == ModEntityTypes.INQUILLAGER_SERVANT.get()) {
            return mob instanceof Neollager neollager && neollager.isMagic();
        } else if (entityType == ModEntityTypes.CONQUILLAGER_SERVANT.get()) {
            return mob.getType() == ModEntityType.PILLAGER_SERVANT.get();
        }

        return false;
    }

    @Override
    public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
        RitualChecker checker = new RitualChecker(level, blockPos, blockState -> true, range, 0);

        if (checker.hasBlocks(blockState -> blockState.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS), 2)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.CREEPER_TOTEM.get()), 8)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.FENCES_WOODEN), 16)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof CraftingTableBlock, 1)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.JUNGLE_LOG), 8)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.JUNGLE_PLANKS), 32)) {
            return ModEntityTypes.CONQUILLAGER_SERVANT.get();

        } else if (checker.hasBlocks(blockState -> blockState.is(BlockTags.PLANKS), 64)
                && checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), 60)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.WALLS), 10)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.FENCES), 10)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.STASH_URN.get()), 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BrewingStandBlock, 1)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof EnchantmentTableBlock, 1)) {
            return ModEntityTypes.INQUILLAGER_SERVANT.get();
        }

        return null;
    }
}
