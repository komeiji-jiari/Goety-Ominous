package com.qiuyue.goetyominous.common.blocks.ac;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public abstract class ServantEggBlock extends DinosaurEggBlock implements EntityBlock {

    public ServantEggBlock(Properties properties, RegistryObject births, int widthPx, int heightPx) {
        super(properties, births, widthPx, heightPx);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }
}
