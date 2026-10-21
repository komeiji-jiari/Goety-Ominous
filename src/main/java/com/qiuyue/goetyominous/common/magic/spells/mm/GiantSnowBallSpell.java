package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.GiantSnowballServant;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public class GiantSnowBallSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return MmConfig.GiantSnowballSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.GiantSnowballCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.GiantSnowballCooldown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(ModEnchantments.POTENCY.get(), ModEnchantments.VELOCITY.get());
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }

        float speed = 2.0F + velocity * 0.1F;

        Vec3 view = caster.getViewVector(1.0F);
        Vec3 spawnPos = caster.getEyePosition(1.0F).add(view.scale(1.0D));

        GiantSnowballServant snowball = new GiantSnowballServant(worldIn, caster);
        snowball.moveTo(spawnPos.x, spawnPos.y - 0.2D, spawnPos.z, caster.getYRot(), caster.getXRot());
        snowball.setExtraDamage(MmConfig.GiantSnowballDamage.get().floatValue() + potency);
        if (this.rightStaff(staff)) {
            snowball.setSizeScale(1.5F);
        }
        snowball.shoot(view.x, view.y, view.z, speed, 0.0F);
        worldIn.addFreshEntity(snowball);

        this.playSound(worldIn, caster, ModSounds.FROZEN_ZOMBIE_SNOWBALL.get(), 1.0F, 0.75F);
    }
}
