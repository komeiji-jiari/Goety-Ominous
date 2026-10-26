package com.qiuyue.goetyominous.compat.mm.jade;

import com.qiuyue.goetyominous.common.entities.ally.mm.AbstractMutantServant;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MutantServantJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(
                MutantServantLifeProvider.INSTANCE, AbstractMutantServant.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(
                MutantServantLifeComponent.INSTANCE, AbstractMutantServant.class);
    }
}
