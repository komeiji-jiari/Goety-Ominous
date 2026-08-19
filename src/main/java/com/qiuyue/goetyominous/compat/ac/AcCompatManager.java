package com.qiuyue.goetyominous.compat.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;


public class AcCompatManager {


    public static void init(IEventBus modEventBus) {
                AcBlockRegistry.register(modEventBus);

                AcBlockEntityRegistry.register(modEventBus);

                AcEntityRegistry.register(modEventBus);

                AcItems.register(modEventBus);
    }


    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(AcEntityRegistry.GROTTOCERATOPS_SERVANT.get(), GrottoceratopsServant.setCustomAttributes().build());
    }
}
