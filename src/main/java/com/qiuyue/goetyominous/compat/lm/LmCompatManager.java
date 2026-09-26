package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlameDrifterServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornWarriorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
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

        // 圣骑那一套招式用的粒子（灵魂粒 / 红灵魂火 / 幻影匕首拖尾）。
        // 注册在这里而不是主类里，是因为这些粒子只服务于「LM 那批仆从」——
        // LM 不在场时它们一个都用不上，没必要占注册名。
        LmParticles.register(modEventBus);
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                OvergrownColossusServant.createAttributes().build());
        event.put(LmEntityRegistry.POSSESSED_PALADIN_SERVANT.get(),
                PossessedPaladinServant.createAttributes().build());

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
    }
}
