package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEffects;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.common.events.TremorsaurusSpiritHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class TremorSpiritSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TremorSpiritCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TremorSpiritTime.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TremorSpiritCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
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
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        TremorsaurusSpiritHandler.setStaffBonus(caster, this.rightStaff(staff));
        caster.addEffect(new MobEffectInstance(AcEffects.TREMORSAURUS_SPIRIT.get(),
                MathHelper.secondsToTicks(SpellConfig.TremorSpiritBuffSeconds.get() * duration),
                potency, false, false, true));

        this.playSound(worldIn, caster, ACSoundRegistry.TREMORSAURUS_ROAR.get());
    }
}
