package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBannerPatterns {
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS =
            DeferredRegister.create(Registries.BANNER_PATTERN, GoetyOminous.MOD_ID);

    public static void init() {
        BANNER_PATTERNS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<BannerPattern> FLOW = BANNER_PATTERNS.register("flow", () -> new BannerPattern("flow"));
    public static final RegistryObject<BannerPattern> GUSTER = BANNER_PATTERNS.register("guster", () -> new BannerPattern("guster"));
}
