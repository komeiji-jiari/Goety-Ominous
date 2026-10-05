package com.qiuyue.goetyominous.common.blocks.trial;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class OminousWeatheringCopperTrapDoorBlock extends TrapDoorBlock implements OminousWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    public OminousWeatheringCopperTrapDoorBlock(BlockSetType blockSetType, WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(properties, blockSetType);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.onRandomTick(state, level, pos, random);
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
