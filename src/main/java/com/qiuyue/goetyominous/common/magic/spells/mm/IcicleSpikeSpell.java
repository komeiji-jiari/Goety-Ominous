package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.qiuyue.goetyominous.common.entities.ally.mm.ThrownIcicleServant;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class IcicleSpikeSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    @Override
    public int defaultSoulCost() {
        return MmConfig.IcicleSpikeSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.IcicleSpikeCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.IcicleSpikeCooldown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ICE_SPIKE_CAST.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float damage = MmConfig.IcicleSpikeDamage.get().floatValue() * WandUtil.damageMultiply();
        float velocity = spellStat.getVelocity();
        float potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
        }
        boolean big = this.rightStaff(staff);

        ThrownIcicleServant icicle = new ThrownIcicleServant(worldIn, caster);
        icicle.damage = (big ? damage * 1.5F : damage) + potency;
        icicle.freezingLength = MutantFrozenZombieCommonConfig.thrown_icicle_freezing_length.get() * (big ? 3 : 2) / 2;
        icicle.freezingLevel = MutantFrozenZombieCommonConfig.thrown_icicle_freezing_level.get();
        icicle.disableShieldLength = MutantFrozenZombieCommonConfig.thrown_icicle_disable_shield_length.get() * (big ? 2 : 1);
        icicle.ignoresInvulTime = MutantFrozenZombieCommonConfig.ignores_invulnerability_time.get();
        icicle.spellOwned = true;
        if (big) {
            icicle.setScale(1.5F);
            velocity *= 1.5F;
        }
        icicle.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        worldIn.addFreshEntity(icicle);
    }
}
