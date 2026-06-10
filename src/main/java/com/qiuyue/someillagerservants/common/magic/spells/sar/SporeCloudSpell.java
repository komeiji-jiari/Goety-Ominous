package com.qiuyue.someillagerservants.common.magic.spells.sar;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.someillagerservants.common.entities.ally.sar.SporeCloud;
import com.qiuyue.someillagerservants.compat.mod.SavageRavageCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SporeCloudSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return 16;
    }

    @Override
    public int defaultCastDuration() {
        return 60;
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return 200;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
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
        if (!SavageRavageCompat.isSavageRavageLoaded()) {
            return;
        }

        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        Vec3 vector3d = caster.getViewVector(1.0F);
        double d0 = caster.getX() + vector3d.x / 2.0D;
        double d1 = caster.getEyeY() - 0.2D;
        double d2 = caster.getZ() + vector3d.z / 2.0D;

        SporeCloud sporeCloud = new SporeCloud(worldIn, d0, d1, d2);
        sporeCloud.setOwner(caster);
        sporeCloud.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 1.5F, 1.0F);
        sporeCloud.setCloudSize(2);
        sporeCloud.setCharged(caster.isCrouching());
        sporeCloud.setSpawnCloudInstantly(false);
        sporeCloud.setPotencyLevel(potency);
        sporeCloud.setDurationLevel(duration);

        worldIn.addFreshEntity(sporeCloud);

        if (rightStaff(staff)) {
            Vec3 vector3d2 = caster.getViewVector(1.0F);
            double d0_2 = caster.getX() + vector3d2.x / 2.0D;
            double d1_2 = caster.getEyeY() - 0.2D;
            double d2_2 = caster.getZ() + vector3d2.z / 2.0D;

            SporeCloud sporeCloud2 = new SporeCloud(worldIn, d0_2, d1_2, d2_2);
            sporeCloud2.setOwner(caster);
            sporeCloud2.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 1.5F, 1.0F);
            sporeCloud2.setCloudSize(2);
            sporeCloud2.setCharged(caster.isCrouching());
            sporeCloud2.setSpawnCloudInstantly(false);
            sporeCloud2.setPotencyLevel(potency);
            sporeCloud2.setDurationLevel(duration);

            worldIn.addFreshEntity(sporeCloud2);
        }

        this.playSound(worldIn, caster, 1.0F, 1.0F);
    }
}
