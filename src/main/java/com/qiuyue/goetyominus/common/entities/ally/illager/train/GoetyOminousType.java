package com.qiuyue.goetyominus.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.init.ModTags;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.common.init.sar.SarBlocks;
import com.qiuyue.goetyominus.compat.ias.IasEntityRegistry;
import com.qiuyue.goetyominus.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominus.compat.mod.SavageRavageCompat;
import com.qiuyue.goetyominus.utils.AdvancedBlockFinder;
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
        } else if (IllageAndSpillageCompat.isIllageAndSpillageLoaded() && entityType == IasEntityRegistry.ABSORBER_SERVANT.get()) {
            return mob.getType() == ModEntityType.CRUSHER_SERVANT.get();
        } else if (SavageRavageCompat.isSavageRavageLoaded() && entityType == SarEntityRegistry.GRIEFER_SERVANT.get()) {
            return mob instanceof Neollager;
        } else if (SavageRavageCompat.isSavageRavageLoaded() && entityType == SarEntityRegistry.EXECUTIONER_SERVANT.get()) {
            return mob instanceof Neollager;
        } else if (SavageRavageCompat.isSavageRavageLoaded() && entityType == SarEntityRegistry.TRICKSTER_SERVANT.get()) {
            return mob instanceof Neollager neollager && neollager.isMagic();
        } else {
            return mob instanceof Neollager;
        }
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

        } else if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()
                && checker.hasBlocks(blockState -> blockState.is(Blocks.COBBLESTONE), 32)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AnvilBlock, 1)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_IRON), 2)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE), 2)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.GLASS), 8)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.OBSERVER), 4)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.TNT), 8)) {
            return IasEntityRegistry.TWITTOLLAGER_SERVANT.get();

        } else if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()
                && checker.hasBlocks(blockState -> blockState.is(Blocks.CHAIN), 8)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_GOLD), 2)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_IRON), 4)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_EMERALD), 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AnvilBlock, 2)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.CURSED_METAL_BLOCK.get()), 2)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.DARK_ALLOY_BLOCK.get()), 2)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.PALE_STEEL_BLOCK.get()), 2)) {
            return IasEntityRegistry.ABSORBER_SERVANT.get();

        } else if (SavageRavageCompat.isSavageRavageLoaded()) {
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
        }

        return null;
    }
}
