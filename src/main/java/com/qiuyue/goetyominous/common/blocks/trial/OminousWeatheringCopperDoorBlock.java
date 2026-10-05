package com.qiuyue.goetyominous.common.blocks.trial;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class OminousWeatheringCopperDoorBlock extends CopperDoorBlock implements OminousWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    public OminousWeatheringCopperDoorBlock(BlockSetType blockSetType, WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(blockSetType, properties);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            this.onRandomTick(state, level, pos, random);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return OminousWeatheringCopper.hasNext(state);
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}
