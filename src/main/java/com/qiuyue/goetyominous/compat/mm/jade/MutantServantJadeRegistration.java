package com.qiuyue.goetyominous.compat.mm.jade;

import com.qiuyue.goetyominous.common.entities.ally.mm.AbstractMutantServant;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;

public class MutantServantJadeRegistration {

    public static void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(
                MutantServantLifeProvider.INSTANCE, AbstractMutantServant.class);
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(
                MutantServantLifeComponent.INSTANCE, AbstractMutantServant.class);
    }
}
