package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ChoruslingServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.EndersentServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlameDrifterServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedApostleServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornWarriorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.AncientGuardianServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FrostbittenGolemServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.WanderingEyeServant;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class LmCompatManager {

    public static void init(IEventBus modEventBus) {
        LmEntityRegistry.register(modEventBus);

        LmSounds.register(modEventBus);

        LmItems.register(modEventBus);

        com.qiuyue.goetyominous.common.init.ModCreativeTab.registerCompatItems(LmItems.LM_ITEMS);

        LmParticles.register(modEventBus);

        modEventBus.addListener(LmCompatManager::setCustomAttributes);
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                OvergrownColossusServant.createAttributes().build());
        event.put(LmEntityRegistry.POSSESSED_PALADIN_SERVANT.get(),
                PossessedPaladinServant.createAttributes().build());

        event.put(LmEntityRegistry.FROSTBITTEN_GOLEM_SERVANT.get(),
                FrostbittenGolemServant.createAttributes().build());

        event.put(LmEntityRegistry.ANCIENT_GUARDIAN_SERVANT.get(),
                AncientGuardianServant.createAttributes().build());

        event.put(LmEntityRegistry.THE_OBLITERATOR_SERVANT.get(),
                TheObliteratorServant.createAttributes().build());

        event.put(LmEntityRegistry.HOVERING_HURRICANE_SERVANT.get(),
                HoveringHurricaneServant.createAttributes().build());

        event.put(LmEntityRegistry.CLOUD_GOLEM_SERVANT.get(),
                CloudGolemServant.createAttributes().build());

        event.put(LmEntityRegistry.WANDERING_EYE_SERVANT.get(),
                WanderingEyeServant.createAttributes().build());

        event.put(LmEntityRegistry.SHULKER_MIMIC_SERVANT.get(),
                ShulkerMimicServant.createAttributes().build());

        event.put(LmEntityRegistry.ANNIHILATION_PURSUER_SERVANT.get(),
                AnnihilationPursuerServant.createAttributes().build());

        event.put(LmEntityRegistry.FLAME_DRIFTER_SERVANT.get(),
                FlameDrifterServant.createAttributes().build());

        event.put(LmEntityRegistry.FLAMEBORN_WARRIOR_SERVANT.get(),
                FlamebornWarriorServant.createAttributes().build());

        event.put(LmEntityRegistry.FLAMEBORN_GUARD_SERVANT.get(),
                FlamebornGuardServant.createAttributes().build());

        event.put(LmEntityRegistry.BEHEADED_KNIGHT_SERVANT.get(),
                BeheadedKnightServant.createAttributes().build());

        event.put(LmEntityRegistry.RESURRECTED_KNIGHT_SERVANT.get(),
                ResurrectedKnightServant.createAttributes().build());

        event.put(LmEntityRegistry.ENDERSENT_SERVANT.get(),
                EndersentServant.createAttributes().build());

        event.put(LmEntityRegistry.CHORUSLING_SERVANT.get(),
                ChoruslingServant.createAttributes().build());

        event.put(LmEntityRegistry.FRACTURED_SERVANT.get(),
                FracturedServant.createAttributes().build());

        event.put(LmEntityRegistry.FRACTURED_APOSTLE_SERVANT.get(),
                FracturedApostleServant.createAttributes().build());

        event.put(LmEntityRegistry.HAUNTED_KNIGHT_SERVANT.get(),
                HauntedKnightServant.createAttributes().build());

        event.put(LmEntityRegistry.HAUNTED_GUARD_SERVANT.get(),
                HauntedGuardServant.createAttributes().build());
    }
}
