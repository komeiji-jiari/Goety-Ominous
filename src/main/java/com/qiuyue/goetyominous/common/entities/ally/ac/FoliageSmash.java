package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;

public class FoliageSmash {

    public static void smash(Mob mob) {
        if (mob.level().isClientSide || !ForgeEventFactory.getMobGriefingEvent(mob.level(), mob)) {
            return;
        }
        AABB aabb = mob.getBoundingBox().inflate(0.2);
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ),
                Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
            BlockState state = mob.level().getBlockState(pos);
            if (state.is(ModTags.Blocks.MONSTROSITY_BREAKS)) {
                mob.level().destroyBlock(pos, true, mob);
            }
        }
    }
}
