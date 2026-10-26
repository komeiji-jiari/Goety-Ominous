package com.qiuyue.goetyominous.compat.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinAlchemistServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinTravelerServant;
import com.qiuyue.goetyominous.common.init.pp.PpEntityRegistry;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import com.qiuyue.goetyominous.config.pp.PpConfig;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class PpCompatManager {

    public static void init(IEventBus modEventBus) {
        PpEntityRegistry.register(modEventBus);
        PpItems.register(modEventBus);
        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(PpItems.PP_ITEMS);
        modEventBus.addListener(PpCompatManager::setCustomAttributes);
    }

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PpConfig.SPEC,
                "goetyominous/goetyominous-piglin_proliferation.toml");
        PpConfig.loadConfig(PpConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-piglin_proliferation.toml").toString());
    }

    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(PpEntityRegistry.PIGLIN_ALCHEMIST_SERVANT.get(), PiglinAlchemistServant.createAttributes().build());
        event.put(PpEntityRegistry.PIGLIN_TRAVELER_SERVANT.get(), PiglinTravelerServant.createAttributes().build());
        event.put(PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT.get(), ZPiglinAlchemistServant.createAttributes().build());
        event.put(PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT.get(), ZPiglinTravelerServant.createAttributes().build());
    }
}
