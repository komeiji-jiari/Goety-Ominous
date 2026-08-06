package com.qiuyue.goetyominous.common.ritual;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

public class FelRitualType implements IRitualType {
    private final String name;

    public FelRitualType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ModBlocks.BREWING_CAULDRON.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity blockEntity, Player player, BlockPos pos, Level level) {
        if (level.getBiome(pos).is(Tags.Biomes.IS_SWAMP)) {
            return true;
        }
        RitualChecker checker = new RitualChecker(level, pos,
                state -> false, RitualRequirements.RANGE, 0);

        if (!checker.hasBlocks(state -> state.is(ModBlocks.BREWING_CAULDRON.get()), 1)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.BREWING_CAULDRON.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ModBlocks.CRYSTAL_BALL.get()), 1)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.CRYSTAL_BALL.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ModBlocks.ROTTEN_BOOKSHELF.get()), 6)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.ROTTEN_BOOKSHELF.get().getName()), true);
            }
            return false;
        }
        return true;
    }
}
