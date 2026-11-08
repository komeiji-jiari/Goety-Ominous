package com.qiuyue.goetyominous.utils.ac;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.init.ac.AcEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CurseOfRlyehHelper {

    public static final int CURSE_DURATION = 600;
    public static final int EFFECT_DURATION = 600;

    private static final float ABYSS_DAMAGE_PER_LEVEL = 0.15F;
    private static final float ELEMENT_DAMAGE_BONUS = 0.30F;
    private static final int ELEMENT_DURATION_CUT = 40;
    private static final int MAX_LEVELS = 3;

    private CurseOfRlyehHelper() {
    }

    public static boolean holdsKey(LivingEntity entity) {
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(entity.getMainHandItem())
                || KeyOfRlyehMixinHelper.isKeyOfRlyeh(entity.getOffhandItem());
    }

    private static MobEffect curseEffect() {
        return AcEffects.CURSE_OF_RLYEH.isPresent() ? AcEffects.CURSE_OF_RLYEH.get() : null;
    }

    public static void applyCurse(LivingEntity target) {
        MobEffect effect = curseEffect();
        if (effect == null || target == null || target.level().isClientSide) {
            return;
        }
        MobEffectInstance current = target.getEffect(effect);
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, MAX_LEVELS - 1);
        target.addEffect(new MobEffectInstance(effect,
                CURSE_DURATION, amplifier, false, true, true));
    }

    public static void applyCurseOnHit(LivingEntity target, ItemStack staff) {
        if (staff != null && KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff)) {
            applyCurse(target);
        }
    }

    public static void applyCurseOnHit(LivingEntity target, LivingEntity caster) {
        if (caster != null && holdsKey(caster)) {
            applyCurse(target);
        }
    }

    public static boolean hasCurse(LivingEntity target) {
        MobEffect effect = curseEffect();
        return effect != null && target.hasEffect(effect);
    }

    public static int curseLevel(LivingEntity target) {
        MobEffect effect = curseEffect();
        if (effect == null) {
            return 0;
        }
        MobEffectInstance instance = target.getEffect(effect);
        return instance == null ? 0 : Math.min(instance.getAmplifier() + 1, MAX_LEVELS);
    }

    public static float amplifyKeySpellDamage(LivingEntity target, float amount) {
        int level = curseLevel(target);
        return level > 0 ? amount * (1.0F + ABYSS_DAMAGE_PER_LEVEL * level) : amount;
    }

    public static boolean isElemental(DamageSource source) {
        return !isSpasmTick(source)
                && (source.is(DamageTypeTags.IS_LIGHTNING)
                || source.is(ModTags.DamageTypes.SHOCK_ATTACKS)
                || source.is(DamageTypeTags.IS_FIRE)
                || source.is(ModTags.DamageTypes.FIRE_ATTACKS)
                || source.is(DamageTypeTags.IS_FREEZING)
                || source.is(ModTags.DamageTypes.FROST_ATTACKS));
    }

    public static float amplifyCursedDamage(LivingEntity target, DamageSource source, float amount) {
        if (amount <= 0.0F || !hasCurse(target) || isSpasmTick(source)) {
            return amount;
        }
        if (source.is(DamageTypeTags.IS_LIGHTNING) || source.is(ModTags.DamageTypes.SHOCK_ATTACKS)) {
            target.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(),
                    EFFECT_DURATION, 0, false, true, true));
            return amount * (1.0F + ELEMENT_DAMAGE_BONUS);
        }
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(ModTags.DamageTypes.FIRE_ATTACKS)) {
            return amount * (1.0F + ELEMENT_DAMAGE_BONUS);
        }
        if (source.is(DamageTypeTags.IS_FREEZING) || source.is(ModTags.DamageTypes.FROST_ATTACKS)) {
            target.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(),
                    EFFECT_DURATION, 2, false, true, true));
            return amount * (1.0F + ELEMENT_DAMAGE_BONUS);
        }
        return amount;
    }

    public static void shortenCurse(LivingEntity target) {
        MobEffect effect = curseEffect();
        if (effect == null) {
            return;
        }
        MobEffectInstance instance = target.getEffect(effect);
        if (instance == null) {
            return;
        }
        int remaining = instance.getDuration() - ELEMENT_DURATION_CUT;
        target.removeEffect(effect);
        if (remaining > 0) {
            target.addEffect(new MobEffectInstance(effect, remaining,
                    instance.getAmplifier(), false, true, true));
        }
    }

    private static boolean isSpasmTick(DamageSource source) {
        return source.getEntity() == null
                && source.getDirectEntity() == null
                && source.is(ModDamageSource.SHOCK);
    }
}
