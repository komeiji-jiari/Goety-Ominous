package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.WitherSlash;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WitherSlashSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.WitherSlashSoulCost.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.WitherSlashCooldown.get();
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_WITHER_SKELETON_FIRE_SLASH.get();
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
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        boolean usingNetherStaff = this.rightStaff(staff);

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }

        float baseDamage = com.qiuyue.goetyominous.config.SpellConfig.WitherSlashDamage.get().floatValue();
        float damage = (usingNetherStaff ? baseDamage * 2.5F : baseDamage) + potency;
        float leech = usingNetherStaff ? 8.0F : 4.0F;
        int witherLength = 300;
        int witherLevel = 1;
        float size = usingNetherStaff ? 2.0F : 1.0F;
        float baseSpeed = 1.5F + velocity * 0.2F;

        WitherSlash slash = new WitherSlash(worldIn, caster, caster.getYRot());
        slash.damage = damage;
        slash.leechAmount = leech;
        slash.witherLength = witherLength;
        slash.witherLevel = witherLevel;
        slash.setSize(size);

        Vec3 lookVec = caster.getLookAngle();
        Vec3 spawnPos = caster.position().add(0, caster.getEyeHeight() * 0.7, 0).add(lookVec.scale(1.5));
        slash.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        slash.setDeltaMovement(lookVec.scale(baseSpeed));

        worldIn.addFreshEntity(slash);
        this.playSound(worldIn, caster, SoundEventInit.MUTANT_WITHER_SKELETON_FIRE_SLASH.get(), 1.0F, 1.0F);
    }
}
