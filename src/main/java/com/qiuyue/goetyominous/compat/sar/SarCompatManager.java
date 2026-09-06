package com.qiuyue.goetyominous.compat.sar;

import com.qiuyue.goetyominous.common.entities.ally.sar.*;
import com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class SarCompatManager {

    public static void init(IEventBus modEventBus) {
        SarEntityRegistry.register(modEventBus);

        SarItems.register(modEventBus);
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
