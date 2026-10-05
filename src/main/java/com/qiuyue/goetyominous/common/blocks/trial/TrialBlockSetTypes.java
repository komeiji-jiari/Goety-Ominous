package com.qiuyue.goetyominous.common.blocks.trial;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TrialBlockSetTypes {
    public static final BlockSetType COPPER = BlockSetType.register(new BlockSetType("copper", true,
            SoundType.COPPER,
            TrialSounds.COPPER_DOOR_CLOSE.get(),
            TrialSounds.COPPER_DOOR_OPEN.get(),
            TrialSounds.COPPER_TRAPDOOR_CLOSE.get(),
            TrialSounds.COPPER_TRAPDOOR_OPEN.get(),
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON));
}
