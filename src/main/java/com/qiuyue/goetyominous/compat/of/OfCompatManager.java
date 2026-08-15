package com.qiuyue.goetyominous.compat.of;

import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.common.items.of.OfItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Opposing Force 联动内容统一管理器
 */
public class OfCompatManager {

    public static void init(IEventBus modEventBus) {
        OfEntityRegistry.register(modEventBus);
        OfItems.register(modEventBus);
    }

    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(OfEntityRegistry.RAMBLER_SERVANT.get(), RamblerServant.setCustomAttributes().build());
    }
}
