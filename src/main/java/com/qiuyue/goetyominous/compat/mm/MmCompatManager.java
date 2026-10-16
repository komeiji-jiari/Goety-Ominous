package com.qiuyue.goetyominous.compat.mm;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.*;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.init.mm.ModSpellControllers;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class MmCompatManager {

    public static void init(IEventBus modEventBus) {
        com.qiuyue.goetyominous.common.network.mm.MmPackets.register();

        MmEntityRegistry.register(modEventBus);

        ModSpellControllers.ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(MmCompatManager::setCustomAttributes);

        MmItems.register(modEventBus);

        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(MmItems.MM_ITEMS);
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