package com.qiuyue.goetyominous.compat.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.BrainiacServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneMageServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class AcCompatManager {

    public static void init(IEventBus modEventBus) {
                AcBlockRegistry.register(modEventBus);
                AcBlockEntityRegistry.register(modEventBus);
                AcEntityRegistry.register(modEventBus);
                AcItems.register(modEventBus);
                AcParticles.register(modEventBus);
    }

    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(AcEntityRegistry.GROTTOCERATOPS_SERVANT.get(), GrottoceratopsServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.TREMORSAURUS_SERVANT.get(), TremorsaurusServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.VALLUMRAPTOR_SERVANT.get(), VallumraptorServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.NUCLEEPER_SERVANT.get(), NucleeperServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.BRAINIAC_SERVANT.get(), BrainiacServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.MINE_GUARDIAN_SERVANT.get(), MineGuardianServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.HULLBREAKER_SERVANT.get(), HullbreakerServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.DEEP_ONE_SERVANT.get(), DeepOneServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT.get(), DeepOneKnightServant.setCustomAttributes().build());
        event.put(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT.get(), DeepOneMageServant.setCustomAttributes().build());
    }
}
