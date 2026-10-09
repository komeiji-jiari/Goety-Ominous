package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.common.mixin.TrapDoorBlockInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;

public final class WindChargeBlockTrigger {
    private WindChargeBlockTrigger() {
    }

    private static boolean canOpenByWindCharge(BlockSetType type) {
        return type.canOpenByHand();
    }

    public static void trigger(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof DoorBlock door) {
            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && canOpenByWindCharge(door.type())
                    && !state.getValue(DoorBlock.POWERED)) {
                door.setOpen(null, level, state, pos, !door.isOpen(state));
            }
        } else if (state.getBlock() instanceof TrapDoorBlock trapDoor) {
            if (canOpenByWindCharge(((TrapDoorBlockInvoker) trapDoor).getType())
                    && !state.getValue(TrapDoorBlock.POWERED)) {
                toggleTrapDoor(level, pos, state, trapDoor);
            }
        } else if (state.getBlock() instanceof FenceGateBlock) {
            if (!state.getValue(FenceGateBlock.POWERED)) {
                boolean open = state.getValue(FenceGateBlock.OPEN);
                level.setBlockAndUpdate(pos, state.setValue(FenceGateBlock.OPEN, !open));
                level.playSound(null, pos, open ? SoundEvents.FENCE_GATE_CLOSE : SoundEvents.FENCE_GATE_OPEN,
                        SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent(null, open ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos);
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

    private static void toggleTrapDoor(Level level, BlockPos pos, BlockState state, TrapDoorBlock trapDoor) {
        BlockState toggled = state.cycle(TrapDoorBlock.OPEN);
        level.setBlock(pos, toggled, 2);
        if (toggled.getValue(TrapDoorBlock.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        ((TrapDoorBlockInvoker) trapDoor).invokePlaySound(null, level, pos, toggled.getValue(TrapDoorBlock.OPEN));
    }
}
