package com.qiuyue.goetyominous.compat.mm;

import com.qiuyue.goetyominous.common.entities.ally.mm.*;
import com.qiuyue.goetyominous.common.init.mm.MmBlockEntities;
import com.qiuyue.goetyominous.common.init.mm.MmBlocks;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.init.mm.ModSpellControllers;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class MmCompatManager {

    public static void init(IEventBus modEventBus) {
        com.qiuyue.goetyominous.common.network.mm.MmPackets.register();

        MmEntityRegistry.register(modEventBus);

        MmBlocks.register(modEventBus);
        MmBlockEntities.register(modEventBus);

        ModSpellControllers.ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(MmCompatManager::setCustomAttributes);

        MmItems.register(modEventBus);

        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(MmItems.MM_ITEMS);

        MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.events.mm.MmTesseractTags.class);
        MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.events.mm.MutantShulkerServantEvents.class);
        MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.events.mm.WitherScytheEvents.class);
        MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.magic.spells.mm.RodStrikeSpell.class);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MmClientCompatManager.register(modEventBus));
    }

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MmConfig.SPEC,
                "goetyominous/goetyominous-mutantmore.toml");
        MmConfig.loadConfig(MmConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-mutantmore.toml").toString());
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(), MutantWitherSkeletonServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(), MutantHoglinServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_SHULKER_SERVANT.get(), MutantShulkerServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP.get(), MutantShulkerServantTrap.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_BLAZE_SERVANT.get(), MutantBlazeServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.RODLING_SERVANT.get(), RodlingServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_JUNGLE_ZOMBIE_SERVANT.get(), MutantJungleZombieServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_HUSK_SERVANT.get(), MutantHuskServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_FROZEN_ZOMBIE_SERVANT.get(), MutantFrozenZombieServant.createConfiguredAttributes().build());
    }
}