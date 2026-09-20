package com.qiuyue.goetyominous.compat.ua;

import com.qiuyue.goetyominous.common.entities.ally.ua.FlareServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.ThrasherServant;
import com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class UaCompatManager {

    public static void init(IEventBus modEventBus) {
        UaEntityRegistry.register(modEventBus);

        UaItems.register(modEventBus);
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
