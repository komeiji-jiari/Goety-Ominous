package com.qiuyue.goetyominous.compat.ua;

import com.qiuyue.goetyominous.common.entities.ally.ua.FlareServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.ThrasherServant;
import com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import com.qiuyue.goetyominous.config.ua.UaConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class UaCompatManager {

    public static void init(IEventBus modEventBus) {
        UaEntityRegistry.register(modEventBus);

        UaItems.register(modEventBus);

        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(UaItems.UA_ITEMS);

        modEventBus.addListener(UaCompatManager::setCustomAttributes);
    }

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, UaConfig.SPEC,
                "goetyominous/goetyominous-upgrade_aquatic.toml");
        UaConfig.loadConfig(UaConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-upgrade_aquatic.toml").toString());
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(UaEntityRegistry.THRASHER_SERVANT.get(),
                ThrasherServant.setCustomAttributes().build());

        event.put(UaEntityRegistry.GREAT_THRASHER_SERVANT.get(),
                GreatThrasherServant.setCustomAttributes().build());

        event.put(UaEntityRegistry.FLARE_SERVANT.get(),
                FlareServant.registerAttributes().build());
    }
}
