package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.common.mixin.TrapDoorBlockInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;

public final class WindChargeBlockTrigger {
    private WindChargeBlockTrigger() {
    }

    private static boolean wooden(BlockState state) {
        SoundType soundType = state.getSoundType();
        return soundType == SoundType.WOOD
                || soundType == SoundType.NETHER_WOOD
                || soundType == SoundType.BAMBOO_WOOD;
    }

    public static void trigger(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof DoorBlock door) {
            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && wooden(state)
                    && !state.getValue(DoorBlock.OPEN)) {
                door.setOpen(null, level, state, pos, true);
            }
        } else if (state.getBlock() instanceof TrapDoorBlock trapDoor) {
            if (wooden(state) && !state.getValue(TrapDoorBlock.OPEN)) {
                level.setBlock(pos, state.setValue(TrapDoorBlock.OPEN, true), 3);
                ((TrapDoorBlockInvoker) trapDoor).invokePlaySound(null, level, pos, true);
            }
        } else if (state.getBlock() instanceof FenceGateBlock) {
            if (!state.getValue(FenceGateBlock.OPEN)) {
                level.setBlock(pos, state.setValue(FenceGateBlock.OPEN, true), 3);
                level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent(null, GameEvent.BLOCK_OPEN, pos);
            }
        } else if (state.getBlock() instanceof ButtonBlock button) {
            if (!state.getValue(ButtonBlock.POWERED)) {
                button.press(state, level, pos);
            }
        } else if (state.getBlock() instanceof LeverBlock lever) {
            lever.pull(state, level, pos);
        } else if (state.getBlock() instanceof BellBlock bell) {
            bell.attemptToRing(level, pos, null);
        } else if (state.getBlock() instanceof AbstractCandleBlock) {
            if (state.getValue(AbstractCandleBlock.LIT)) {
                AbstractCandleBlock.extinguish(null, state, level, pos);
            }
        }
    }
}
