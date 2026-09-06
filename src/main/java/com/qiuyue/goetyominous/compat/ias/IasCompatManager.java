package com.qiuyue.goetyominous.compat.ias;

import com.qiuyue.goetyominous.common.entities.ally.illager.AbsorberServant;
import com.qiuyue.goetyominous.common.entities.ally.illager.MagispellerServant;
import com.qiuyue.goetyominous.common.entities.ally.illager.TwittollagerServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class IasCompatManager {

    public static void init(IEventBus modEventBus) {
        IasEntityRegistry.register(modEventBus);

        IasItems.register(modEventBus);
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(IasEntityRegistry.TWITTOLLAGER_SERVANT.get(), TwittollagerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.ABSORBER_SERVANT.get(), AbsorberServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.CRASHAGER_SERVANT.get(), CrashagerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.KABOOMER_SERVANT.get(), KaboomerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.ILLASHOOTER_SERVANT.get(), IllashooterServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.DISPENSER_SERVANT.get(), DispenserServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.FAKEMAGISPELLER.get(), FakeMagispeller.createAttributes().build());
        event.put(IasEntityRegistry.MAGIHEAL.get(), MagiHeal.createAttributes().build());
        event.put(IasEntityRegistry.MAGISPELLER_SERVANT.get(), MagispellerServant.setCustomAttributes().build());
    }
}
