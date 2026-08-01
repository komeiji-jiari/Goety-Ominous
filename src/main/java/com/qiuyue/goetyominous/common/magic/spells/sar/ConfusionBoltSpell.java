package com.qiuyue.goetyominous.common.magic.spells.sar;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.sar.ConfusionBolt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ConfusionBoltSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.ConfusionBoltSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.ConfusionBoltCooldown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float velocity = spellStat.getVelocity();

        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }

        Vec3 vector3d = caster.getViewVector(1.0F);
        double d0 = caster.getX() + vector3d.x;
        double d1 = caster.getEyeY();
        double d2 = caster.getZ() + vector3d.z;

        float speed = 0.25F + (velocity * 0.05F);

        ConfusionBolt confusionBolt = new ConfusionBolt(worldIn, caster, 200);
        confusionBolt.setPos(d0, d1 - 0.5, d2);
        confusionBolt.setDeltaMovement(vector3d.normalize().scale(speed));

        worldIn.addFreshEntity(confusionBolt);

        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }
}
