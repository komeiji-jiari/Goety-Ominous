package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TrialSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<SoundEvent> POLISHED_TUFF_BREAK = create("polished_tuff_break");
    public static final RegistryObject<SoundEvent> POLISHED_TUFF_STEP = create("polished_tuff_step");
    public static final RegistryObject<SoundEvent> POLISHED_TUFF_PLACE = create("polished_tuff_place");
    public static final RegistryObject<SoundEvent> POLISHED_TUFF_HIT = create("polished_tuff_hit");
    public static final RegistryObject<SoundEvent> POLISHED_TUFF_FALL = create("polished_tuff_fall");

    public static final RegistryObject<SoundEvent> TUFF_BRICKS_BREAK = create("tuff_bricks_break");
    public static final RegistryObject<SoundEvent> TUFF_BRICKS_STEP = create("tuff_bricks_step");
    public static final RegistryObject<SoundEvent> TUFF_BRICKS_PLACE = create("tuff_bricks_place");
    public static final RegistryObject<SoundEvent> TUFF_BRICKS_HIT = create("tuff_bricks_hit");
    public static final RegistryObject<SoundEvent> TUFF_BRICKS_FALL = create("tuff_bricks_fall");

    public static final RegistryObject<SoundEvent> COPPER_GRATE_BREAK = create("copper_grate_break");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_STEP = create("copper_grate_step");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_PLACE = create("copper_grate_place");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_HIT = create("copper_grate_hit");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_FALL = create("copper_grate_fall");

    public static final RegistryObject<SoundEvent> COPPER_BULB_BREAK = create("copper_bulb_break");
    public static final RegistryObject<SoundEvent> COPPER_BULB_STEP = create("copper_bulb_step");
    public static final RegistryObject<SoundEvent> COPPER_BULB_PLACE = create("copper_bulb_place");
    public static final RegistryObject<SoundEvent> COPPER_BULB_HIT = create("copper_bulb_hit");
    public static final RegistryObject<SoundEvent> COPPER_BULB_FALL = create("copper_bulb_fall");
    public static final RegistryObject<SoundEvent> COPPER_BULB_TURN_ON = create("copper_bulb_turn_on");
    public static final RegistryObject<SoundEvent> COPPER_BULB_TURN_OFF = create("copper_bulb_turn_off");

    public static final RegistryObject<SoundEvent> COPPER_DOOR_CLOSE = create("copper_door_close");
    public static final RegistryObject<SoundEvent> COPPER_DOOR_OPEN = create("copper_door_open");
    public static final RegistryObject<SoundEvent> COPPER_TRAPDOOR_CLOSE = create("copper_trapdoor_close");
    public static final RegistryObject<SoundEvent> COPPER_TRAPDOOR_OPEN = create("copper_trapdoor_open");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
