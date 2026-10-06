package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.effects.TrialOmenEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<MobEffect> TRIAL_OMEN = EFFECTS.register("trial_omen", TrialOmenEffect::new);

    public static void init() {
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void transformBadOmenIntoTrialOmen(Player player) {
        MobEffectInstance badOmen = player.getEffect(MobEffects.BAD_OMEN);
        if (badOmen != null) {
            int duration = 18000 * (badOmen.getAmplifier() + 1);
            player.removeEffect(MobEffects.BAD_OMEN);
            player.addEffect(new MobEffectInstance(TRIAL_OMEN.get(), duration, 0));
        }
    }
}