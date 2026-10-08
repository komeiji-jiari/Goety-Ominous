package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, GoetyOminous.MOD_ID);

    public static final RegistryObject<Potion> WIND_CHARGED = POTIONS.register("wind_charged",
            () -> new Potion("wind_charged", new MobEffectInstance(ModEffects.WIND_CHARGED.get(), 3600)));
    public static final RegistryObject<Potion> WEAVING = POTIONS.register("weaving",
            () -> new Potion("weaving", new MobEffectInstance(ModEffects.WEAVING.get(), 3600)));
    public static final RegistryObject<Potion> OOZING = POTIONS.register("oozing",
            () -> new Potion("oozing", new MobEffectInstance(ModEffects.OOZING.get(), 3600)));
    public static final RegistryObject<Potion> INFESTED = POTIONS.register("infested",
            () -> new Potion("infested", new MobEffectInstance(ModEffects.INFESTED.get(), 3600)));

    public static void init() {
        POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
