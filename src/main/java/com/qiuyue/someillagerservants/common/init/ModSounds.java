package com.qiuyue.someillagerservants.common.init;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SomeIllagerServants.MOD_ID);

    public static void init() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SoundEvent> ACOLYTE_IDLE_1 = create("acolyte_idle_1");
    public static final RegistryObject<SoundEvent> ACOLYTE_IDLE_2 = create("acolyte_idle_2");
    public static final RegistryObject<SoundEvent> ACOLYTE_IDLE_3 = create("acolyte_idle_3");
    public static final RegistryObject<SoundEvent> ACOLYTE_IDLE_4 = create("acolyte_idle_4");
    public static final RegistryObject<SoundEvent> ACOLYTE_IDLE_5 = create("acolyte_idle_5");

    public static final RegistryObject<SoundEvent> ACOLYTE_HURT_1 = create("acolyte_hurt_1");
    public static final RegistryObject<SoundEvent> ACOLYTE_HURT_2 = create("acolyte_hurt_2");
    public static final RegistryObject<SoundEvent> ACOLYTE_HURT_3 = create("acolyte_hurt_3");

    public static final RegistryObject<SoundEvent> ACOLYTE_DEATH_1 = create("acolyte_death_1");
    public static final RegistryObject<SoundEvent> ACOLYTE_DEATH_2 = create("acolyte_death_2");
    public static final RegistryObject<SoundEvent> ACOLYTE_DEATH_3 = create("acolyte_death_3");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SomeIllagerServants.MOD_ID, name)));
    }
}