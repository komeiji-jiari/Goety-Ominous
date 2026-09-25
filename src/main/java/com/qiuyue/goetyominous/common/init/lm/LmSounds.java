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

    /**
     * 灵魂柱从地里冒出来时的「风声」。注册名和传奇怪物逐字相同。
     *
     * <p>⚠️ 它的字幕是<b>故意不写的</b>：传奇怪物的 {@code sounds.json} 里虽然声明了
     * {@code subtitles = sounds.legendary_monsters.soul_fly}，但它的语言文件里
     * <b>压根没写这条翻译</b>（英文中文都没有）—— 也就是说原版里玩家实际看不到字幕。
     * 我们照搬这个事实，不额外加 {@code subtitle}，免得多出一个原版没有的字幕。
     *
     * <p>音频文件 {@code sounds/soul_fly.ogg} 是从传奇怪物的 jar 里原样提取的。
     */
    public static final RegistryObject<SoundEvent> SOUL_FLY = LM_SOUNDS.register(
            "soul_fly",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, "soul_fly")));

    public static void register(IEventBus modEventBus) {
        LM_SOUNDS.register(modEventBus);
    }
}
