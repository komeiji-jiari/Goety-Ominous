package com.qiuyue.goetyominous.compat.sar;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.qiuyue.goetyominous.common.init.sar.SarBlocks;
import com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import com.qiuyue.goetyominous.utils.AdvancedBlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SarOminousType implements ITrainIllager {

    @Override
    public boolean canSpawn(Level level, BlockPos blockPos, int range) {
        return this.getIllager(level, blockPos, range) != null;
    }

    @Override
    public boolean mobCanTrainToType(Mob mob, EntityType<?> entityType) {
        if (!SavageRavageCompat.isSavageRavageLoaded()) {
            return false;
        }
        if (entityType == SarEntityRegistry.GRIEFER_SERVANT.get()
                || entityType == SarEntityRegistry.EXECUTIONER_SERVANT.get()) {
            return mob instanceof Neollager;
        }
        if (entityType == SarEntityRegistry.TRICKSTER_SERVANT.get()) {
            return mob instanceof Neollager neollager && neollager.isMagic();
        }
        return false;
    }

    @Override
    public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
        if (!SavageRavageCompat.isSavageRavageLoaded()) {
            return null;
        }

        RitualChecker checker = new RitualChecker(level, blockPos, blockState -> true, range, 0);

        Block sporeBombBlock = SarBlocks.getSPORE_BOMB();
        Block blastProofPlateBlock = SarBlocks.getBLAST_PROOF_PLATE();
        Block creeperTotemBlock = ModBlocks.CREEPER_TOTEM.get();
        Block gloomyTilesBlock = SarBlocks.getGLOOMY_TILES();
        Block chiseledGloomyTilesBlock = SarBlocks.getCHISELED_GLOOMY_TILES();

        if (sporeBombBlock != null && blastProofPlateBlock != null
                && checker.hasBlocks(blockState -> blockState.getBlock() == blastProofPlateBlock, 32)
                && checker.hasBlocks(blockState -> blockState.getBlock() == sporeBombBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() == creeperTotemBlock, 8)) {
            return SarEntityRegistry.GRIEFER_SERVANT.get();
        }

        Block shadeStone = ModBlocks.SHADE_STONE_BRICK_BLOCK.get();
        Block ironDungeonTorch = ModBlocks.IRON_DUNGEON_TORCH.get();
        Block tallSkull = ModBlocks.TALL_SKULL_BLOCK.get();
        Block wallTallSkull = ModBlocks.WALL_TALL_SKULL_BLOCK.get();

        if (AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                blockState -> blockState.is(shadeStone), range, 32)
                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                blockState -> blockState.getBlock() == ironDungeonTorch, range, 4)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.IRON_BARS), 16)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AnvilBlock, 1)
                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                blockState -> blockState.is(Blocks.SKELETON_SKULL) || blockState.is(Blocks.SKELETON_WALL_SKULL), range, 2)
                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                blockState -> blockState.getBlock() == tallSkull || blockState.getBlock() == wallTallSkull, range, 2)) {
            return SarEntityRegistry.EXECUTIONER_SERVANT.get();
        }

        if (gloomyTilesBlock != null && chiseledGloomyTilesBlock != null
                && checker.hasBlocks(blockState -> blockState.is(Blocks.CARVED_PUMPKIN), 2)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.HAY_BLOCK), 2)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.GLASS), 32)
                && checker.hasBlocks(blockState -> blockState.getBlock() == gloomyTilesBlock, 16)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.BOOKSHELF), 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() == chiseledGloomyTilesBlock, 2)) {
            return SarEntityRegistry.TRICKSTER_SERVANT.get();
        }

        return null;
    }

    @Override
    public boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
        return this.mobCanTrainToType(mob, this.getIllager(level, blockPos, range));
    }
}
