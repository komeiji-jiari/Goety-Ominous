package com.qiuyue.goetyominus.common.init.lm;

import com.qiuyue.goetyominus.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * LegendaryMonsters 联动音效注册类
 * 负责注册 LM 模组加载时所需的音效事件
 * 注意：这个类只在 LegendaryMonsters 模组加载时才会被调用
 */
public class LmSounds {

    public static final DeferredRegister<SoundEvent> LM_SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<SoundEvent> LM_MUSIC_DISC = LM_SOUNDS.register(
            "lm_music_disc",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, "lm_music_disc")));

    /**
     * 注册 LM 音效到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        LM_SOUNDS.register(modEventBus);
    }
}
