package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.events.ac.TremorzillaBreathHandler;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TremorzillaBreathSpell extends Spell {

    private static final int DURATION_TICKS_PER_LEVEL = 20;
    private static final int MAX_RANDOM_EXTRA = 150;
    private static final int CHARGE_SPIKES = 6;
    private static final int CHARGE_COMPLETE_SPIKE = 5;
    private static final float CHARGE_VOLUME = 8.0F;
    private static final float CHARGE_PITCH_BASE = 0.7F;
    private static final float CHARGE_PITCH_RANGE = 0.7F;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TremorzillaBreathCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TremorzillaBreathCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TremorzillaBreathCoolDown.get();
    }

    @Override
    public @Nullable SoundEvent CastingSound() {
        return ACSoundRegistry.TREMORZILLA_CHARGE_NORMAL.get();
    }

    @Override
    public float castingVolume() {
        return CHARGE_VOLUME;
    }

    @Override
    public float castingPitch() {
        return CHARGE_PITCH_BASE;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        int duration = Math.max(1, this.castDuration(caster, staff));
        float progress = (float) castTime / (float) duration;
        int spike = chargeSpike(castTime, duration);
        if (spike < 1 || spike > CHARGE_COMPLETE_SPIKE || spike == chargeSpike(castTime - 1, duration)) {
            return;
        }
        SoundEvent soundEvent = spike == CHARGE_COMPLETE_SPIKE
                ? ACSoundRegistry.TREMORZILLA_CHARGE_COMPLETE.get()
                : ACSoundRegistry.TREMORZILLA_CHARGE_NORMAL.get();
        playSound(worldIn, caster, soundEvent, CHARGE_PITCH_BASE + progress * CHARGE_PITCH_RANGE);
    }

    private static void playSound(ServerLevel worldIn, LivingEntity caster, SoundEvent soundEvent, float pitch) {
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), soundEvent,
                SoundSource.PLAYERS, CHARGE_VOLUME, pitch);
    }

    private static int chargeSpike(int castTime, int duration) {
        return (int) Math.floor((double) castTime * CHARGE_SPIKES / (double) duration);
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int durationLevel = 0;
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            durationLevel = WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        float damage = SpellConfig.TremorzillaBreathDamage.get().floatValue() + (float) potency;
        double range = SpellConfig.TremorzillaBreathRange.get();
        int maxBeamTime = SpellConfig.TremorzillaBreathDuration.get()
                + caster.getRandom().nextInt(MAX_RANDOM_EXTRA)
                + durationLevel * DURATION_TICKS_PER_LEVEL;

        TremorzillaBreathHandler.startBeam(worldIn, caster, damage, range, maxBeamTime);
    }
}
