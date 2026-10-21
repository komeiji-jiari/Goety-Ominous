package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.IcicleSpikeServant;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IcicleSlamSpell extends Spell {
    private static final double ROW_SPACING = 1.0D;
    private static final float ROW_ANGLE = 0.2F;

    @Override
    public int defaultSoulCost() {
        return MmConfig.IcicleSlamSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.IcicleSlamCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.IcicleSlamCooldown.get();
    }

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        int cost = super.soulCost(caster, staff);
        if (WandUtil.enchantedFocus(caster) && !SpellConfig.EnchantMultiCost.get()) {
            return cost * 2;
        }
        return cost;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ICE_SPIKE_CAST.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }

        float damage = MmConfig.IcicleSlamDamage.get().floatValue() + potency;
        int freezingLength = MmConfig.IcicleSlamFreezingLength.get();
        int freezingLevel = MmConfig.IcicleSlamFreezingLevel.get();
        boolean ignoresInvulnerability = MmConfig.IcicleSlamIgnoresInvulnerability.get();

        HitResult hit = this.rayTrace(worldIn, caster, range, spellStat.getRadius());
        float angle = (float) Mth.atan2(hit.getLocation().z - caster.getZ(),
                hit.getLocation().x - caster.getX());
        LivingEntity target = this.getTarget(caster, range);
        if (target != null) {
            angle = (float) Mth.atan2(target.getZ() - caster.getZ(), target.getX() - caster.getX());
        }

        if (!this.isShifting(caster)) {
            int rowCount = this.rightStaff(staff) ? 3 : 1;
            for (int i = 0; i < range; ++i) {
                double distance = ROW_SPACING * (double) (i + 1);
                for (int row = 0; row < rowCount; ++row) {
                    float rowAngle;
                    if (row == 0) {
                        rowAngle = angle;
                    } else if (row == 1) {
                        rowAngle = angle + ROW_ANGLE;
                    } else {
                        rowAngle = angle - ROW_ANGLE;
                    }
                    this.spawnIcicle(worldIn, caster,
                            caster.getX() + (double) Mth.cos(rowAngle) * distance,
                            caster.getZ() + (double) Mth.sin(rowAngle) * distance,
                            i, damage, freezingLength, freezingLevel, ignoresInvulnerability);
                }
            }
        } else {
            for (int i = 0; i < 5; ++i) {
                float ringAngle = angle + (float) i * (float) Math.PI * 0.4F;
                this.spawnIcicle(worldIn, caster,
                        caster.getX() + (double) Mth.cos(ringAngle) * 1.5D,
                        caster.getZ() + (double) Mth.sin(ringAngle) * 1.5D,
                        0, damage, freezingLength, freezingLevel, ignoresInvulnerability);
            }
            for (int i = 0; i < 8; ++i) {
                float ringAngle = angle + (float) i * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                this.spawnIcicle(worldIn, caster,
                        caster.getX() + (double) Mth.cos(ringAngle) * 2.5D,
                        caster.getZ() + (double) Mth.sin(ringAngle) * 2.5D,
                        3, damage, freezingLength, freezingLevel, ignoresInvulnerability);
            }
            if (this.rightStaff(staff)) {
                for (int i = 0; i < 11; ++i) {
                    float ringAngle = angle + (float) i * (float) Math.PI * 4.0F / 16.0F + 2.5133462F;
                    this.spawnIcicle(worldIn, caster,
                            caster.getX() + (double) Mth.cos(ringAngle) * 3.5D,
                            caster.getZ() + (double) Mth.sin(ringAngle) * 3.5D,
                            6, damage, freezingLength, freezingLevel, ignoresInvulnerability);
                }
                for (int i = 0; i < 14; ++i) {
                    float ringAngle = angle + (float) i * (float) Math.PI * 8.0F / 32.0F + 5.0266924F;
                    this.spawnIcicle(worldIn, caster,
                            caster.getX() + (double) Mth.cos(ringAngle) * 4.5D,
                            caster.getZ() + (double) Mth.sin(ringAngle) * 4.5D,
                            9, damage, freezingLength, freezingLevel, ignoresInvulnerability);
                }
            }
        }

        this.playSound(worldIn, caster, ModSounds.ICE_SPIKE_HIT.get(), 1.0F, 0.5F);
    }

    private void spawnIcicle(ServerLevel level, LivingEntity caster, double x, double z, int startDelay,
                             float damage, int freezingLength, int freezingLevel, boolean ignoresInvulnerability) {
        IcicleSpikeServant.spawn(level, caster, new Vec3(x, caster.getY(), z), startDelay,
                damage, freezingLength, freezingLevel, ignoresInvulnerability, true);
    }
}


