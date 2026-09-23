package com.qiuyue.goetyominous.compat.of;

import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.FireSlimeServant;
import com.qiuyue.goetyominous.common.entities.ally.of.GuzzlerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.common.items.of.OfItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class OfCompatManager {

    public static void init(IEventBus modEventBus) {
        OfEntityRegistry.register(modEventBus);
        OfItems.register(modEventBus);
    }

    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(OfEntityRegistry.RAMBLER_SERVANT.get(), RamblerServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.DICER_SERVANT.get(), DicerServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.VOLT_SERVANT.get(), VoltServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.TREMBLER_SERVANT.get(), TremblerServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.TERROR_SERVANT.get(), TerrorServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.FIRE_SLIME_SERVANT.get(), FireSlimeServant.setCustomAttributes().build());
        event.put(OfEntityRegistry.GUZZLER_SERVANT.get(), GuzzlerServant.setCustomAttributes().build());
    }
}
