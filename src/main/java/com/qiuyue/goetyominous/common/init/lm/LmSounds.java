package com.qiuyue.goetyominous.common.init.lm;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LmSounds {

    public static final DeferredRegister<SoundEvent> LM_SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<SoundEvent> LM_MUSIC_DISC = LM_SOUNDS.register(
            "lm_music_disc",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, "lm_music_disc")));

    public static void register(IEventBus modEventBus) {
        LM_SOUNDS.register(modEventBus);
    }
}
