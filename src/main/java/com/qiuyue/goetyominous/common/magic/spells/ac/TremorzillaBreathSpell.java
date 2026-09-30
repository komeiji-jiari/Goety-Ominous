package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.events.TremorzillaBreathHandler;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TremorzillaBreathSpell extends Spell {

    private static final int DURATION_TICKS_PER_LEVEL = 20;
    private static final int MAX_RANDOM_EXTRA = 150;

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
