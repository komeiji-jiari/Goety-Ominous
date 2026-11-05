package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class FieryHideEvents {
    private static final float REFLECT_RATIO = 0.2F;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        MobEffectInstance instance = target.getEffect(ModEffects.FIERY_HIDE.get());
        if (instance == null || !ModDamageSource.physicalAttacks(event.getSource())) {
            return;
        }
        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker) || attacker == target) {
            return;
        }
        if (!attacker.fireImmune()) {
            attacker.setSecondsOnFire(3 + instance.getAmplifier() * 2);
        }
        float reflect = event.getAmount() * REFLECT_RATIO;
        if (reflect > 0.0F) {
            attacker.hurt(attacker.damageSources().thorns(target), reflect);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(ModEffects.FIERY_HIDE.get())
                && event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setCanceled(true);
            entity.clearFire();
        }
    }
}
