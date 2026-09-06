package com.qiuyue.goetyominous.compat.mm;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.*;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class MmCompatManager {

    public static void init(IEventBus modEventBus) {
        MmEntityRegistry.register(modEventBus);

        MmItems.register(modEventBus);
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(), MutantWitherSkeletonServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(), MutantHoglinServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_SHULKER_SERVANT.get(), MutantShulkerServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP.get(), MutantShulkerServantTrap.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_BLAZE_SERVANT.get(), MutantBlazeServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.RODLING_SERVANT.get(), RodlingServant.createConfiguredAttributes().build());
    }
}