package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.effects.ac.TremorsaurusSpiritEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AcEffects {

    private static final DeferredRegister<MobEffect> AC_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<MobEffect> TREMORSAURUS_SPIRIT =
            AC_EFFECTS.register("tremorsaurus_spirit", TremorsaurusSpiritEffect::new);

    public static void register(IEventBus modEventBus) {
        AC_EFFECTS.register(modEventBus);
    }
}
