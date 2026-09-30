package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.common.ritual.ModRitualFactory;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModRituals {

    public static final DeferredRegister<ModRitualFactory> RITUALS =
            DeferredRegister.create(
                    com.Polarice3.Goety.common.ritual.ModRituals.RITUALS.getRegistryKey(),
                    GoetyOminous.MOD_ID);

    public static final RegistryObject<ModRitualFactory> CAVE_TABLET_CONVERT =
            RITUALS.register("cave_tablet_convert",
                    () -> new ModRitualFactory(CaveTabletConvertRitual::new));

    private ModRituals() {
    }

    public static void register(IEventBus modEventBus) {
        RITUALS.register(modEventBus);
    }
}
