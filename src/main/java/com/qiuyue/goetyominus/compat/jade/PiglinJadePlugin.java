package com.qiuyue.goetyominus.compat.jade;

import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import snownee.jade.addon.vanilla.MobBreedingProvider;
import snownee.jade.addon.vanilla.MobGrowthProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PiglinJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(
                PiglinGrowthProvider.INSTANCE, AbstractPiglinServant.class);
        registration.registerEntityDataProvider(
                PiglinBreedProvider.INSTANCE, AbstractPiglinServant.class);
        registration.registerEntityDataProvider(
                PiglinEvolutionProvider.INSTANCE, AbstractPiglinServant.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(
                MobGrowthProvider.INSTANCE, AbstractPiglinServant.class);
        registration.registerEntityComponent(
                MobBreedingProvider.INSTANCE, AbstractPiglinServant.class);
        registration.registerEntityComponent(
                PiglinEvolutionComponent.INSTANCE, AbstractPiglinServant.class);


    }
}