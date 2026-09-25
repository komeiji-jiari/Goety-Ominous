package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class VoltBoltSpell extends Spell {

    private static final int BASE_EFFECT_DURATION = 300;
    private static final int SPASMS_DURATION = 300;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.VoltBoltCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.VoltBoltCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.VoltBoltCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity caster) {
        return new ColorUtil(0.45F, 0.65F, 1.0F);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        int effectDuration = (duration + 1) * BASE_EFFECT_DURATION;
        int spasmsDuration = this.rightStaff(staff) ? (duration + 1) * SPASMS_DURATION : 0;

        VoltServantElectricCharge charge = this.createBolt(worldIn, caster, staff,
                potency, effectDuration, spasmsDuration, this.getRadiusBonus(caster));
        if (this.rightStaff(staff)) {
            charge.setRainbow(true);
        }
        worldIn.addFreshEntity(charge);
        this.playSound(worldIn, caster, ModSounds.THUNDERBOLT.get());
    }

    private float getBoltSpeed(LivingEntity caster, ItemStack staff) {
        float base = this.rightStaff(staff) ? 0.5F : 0.25F;
        float velocityLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        return base + velocityLevel * 0.1F;
    }

    private float getRadiusBonus(LivingEntity caster) {
        return WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
    }

    private VoltServantElectricCharge createBolt(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
                                                 int potency, int effectDuration, int spasmsDuration, float radiusBonus) {
        Vec3 view = caster.getViewVector(1.0F);
        VoltServantElectricCharge charge = new VoltServantElectricCharge(
                OfEntityRegistry.VOLT_SERVANT_ELECTRIC_CHARGE.get(), worldIn);
        charge.setOwner(caster);
        charge.moveTo(caster.getX() + view.x / 2.0D, caster.getEyeY() - 0.2D, caster.getZ() + view.z / 2.0D);
        charge.setDamageBonus((float) potency);
        charge.setEffectDuration(effectDuration);
        charge.setSpasmsDuration(spasmsDuration);
        charge.setRadiusBonus(radiusBonus);
        charge.shoot(view.x, view.y, view.z, this.getBoltSpeed(caster, staff), 1.0F);
        return charge;
    }
}
