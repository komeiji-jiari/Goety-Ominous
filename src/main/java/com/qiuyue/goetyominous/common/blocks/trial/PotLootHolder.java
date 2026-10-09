package com.qiuyue.goetyominous.common.blocks.trial;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface PotLootHolder {
    void goetyominous$unpackLoot(ServerLevel level, BlockPos pos, @Nullable Player player);
}
