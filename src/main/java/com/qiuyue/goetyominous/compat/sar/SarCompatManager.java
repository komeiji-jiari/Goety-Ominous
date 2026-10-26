package com.qiuyue.goetyominous.compat.sar;

import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.qiuyue.goetyominous.common.entities.ally.sar.*;
import com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import com.qiuyue.goetyominous.config.sar.SarConfig;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class SarCompatManager {

    public static void init(IEventBus modEventBus) {
        IllagerType.create("SarOminous", new SarOminousType());

        SarEntityRegistry.register(modEventBus);

        SarItems.register(modEventBus);

        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(SarItems.SAR_ITEMS);

        modEventBus.addListener(SarCompatManager::setCustomAttributes);
    }

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SarConfig.SPEC,
                "goetyominous/goetyominous-savage_and_ravage.toml");
        SarConfig.loadConfig(SarConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-savage_and_ravage.toml").toString());
    }

    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(SarEntityRegistry.CREEPIE_SERVANT.get(), CreepieServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.GRIEFER_SERVANT.get(), GrieferServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.EXECUTIONER_SERVANT.get(), ExecutionerServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.TRICKSTER_SERVANT.get(), TricksterServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.RUNE_PRISON.get(), RunePrison.setCustomAttributes().build());
    }
}
