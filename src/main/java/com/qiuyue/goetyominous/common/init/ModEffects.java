package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.effects.InfestedEffect;
import com.qiuyue.goetyominous.common.effects.OozingEffect;
import com.qiuyue.goetyominous.common.effects.TrialOmenEffect;
import com.qiuyue.goetyominous.common.effects.WeavingEffect;
import com.qiuyue.goetyominous.common.effects.WindChargedEffect;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GoetyOminous.MOD_ID);

    public static final RegistryObject<MobEffect> TRIAL_OMEN = EFFECTS.register("trial_omen", TrialOmenEffect::new);
    public static final RegistryObject<MobEffect> OOZING = EFFECTS.register("oozing", OozingEffect::new);
    public static final RegistryObject<MobEffect> WEAVING = EFFECTS.register("weaving", WeavingEffect::new);
    public static final RegistryObject<MobEffect> WIND_CHARGED = EFFECTS.register("wind_charged", WindChargedEffect::new);
    public static final RegistryObject<MobEffect> INFESTED = EFFECTS.register("infested", InfestedEffect::new);

    public static void init() {
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void triggerOnDeath(LivingEntity entity, Entity.RemovalReason reason) {
        for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
            MobEffect effect = instance.getEffect();
            if (effect instanceof OozingEffect oozing) {
                oozing.onMobRemoved(entity, instance.getAmplifier(), reason);
            } else if (effect instanceof WeavingEffect weaving) {
                weaving.onMobRemoved(entity, instance.getAmplifier(), reason);
            } else if (effect instanceof WindChargedEffect windCharged) {
                windCharged.onMobRemoved(entity, instance.getAmplifier(), reason);
            }
        }
    }

    public static void triggerOnHurt(LivingEntity entity) {
        for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
            if (instance.getEffect() instanceof InfestedEffect infested) {
                infested.onMobHurt(entity, instance.getAmplifier());
            }
        }
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
