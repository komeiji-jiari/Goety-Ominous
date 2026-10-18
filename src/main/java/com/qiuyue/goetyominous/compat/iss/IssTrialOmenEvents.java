package com.qiuyue.goetyominous.compat.iss;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.compat.mod.IronsSpellbooksCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IssTrialOmenEvents {

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        MobEffect issTrialOmen = IronsSpellbooksCompat.trialOmen();
        if (issTrialOmen == null || event.getEffectInstance().getEffect() != issTrialOmen) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        event.setResult(Event.Result.DENY);
        event.getEntity().addEffect(new MobEffectInstance(ModEffects.TRIAL_OMEN.get(),
                instance.getDuration(), instance.getAmplifier(), false, true, true));
    }
}
