package com.qiuyue.goetyominous.compat.mm.jade;

import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MutantServantJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        if (MutantMoreCompat.isMutantMoreLoaded()) {
            MutantServantJadeRegistration.register(registration);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        if (MutantMoreCompat.isMutantMoreLoaded()) {
            MutantServantJadeRegistration.registerClient(registration);
        }
    }
}
