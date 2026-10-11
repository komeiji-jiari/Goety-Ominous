package com.qiuyue.goetyominous.common.entities.ally.illager.train;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public interface TrainIllagerExtension {

    EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range);

    Boolean mobCanTrainTo(EntityType<?> resolvedType, Mob mob);
}
