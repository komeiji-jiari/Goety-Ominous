package com.qiuyue.someillagerservants.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.AreaDamage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WitherBreathSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.someillagerservants.config.SpellConfig.WitherBreathSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 120;
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.someillagerservants.config.SpellConfig.WitherBreathCooldown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FIRE_BREATH.get();
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
        int potencyBonus = spellStat.getPotency();
        int durationBonus = spellStat.getDuration();

        if (WandUtil.enchantedFocus(caster)) {
            potencyBonus += WandUtil.getPotencyLevel(caster);
            durationBonus += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        boolean usingNetherStaff = this.rightStaff(staff);
        int buffLevelBonus = potencyBonus;

        float baseRadius = 16.0F;
        int extraTime = 200 + (durationBonus * 40);

        Vec3 offsetPos = PositionUtils.getOffsetPos(caster, 0.0, 0.0, (double)(baseRadius / 2.0F + caster.getBbWidth()), 0.0F, caster.yBodyRot);
        AABB bb = (new AABB(offsetPos.add((double)(baseRadius / 2.0F), 1.0, (double)(baseRadius / 2.0F)), offsetPos.subtract((double)(baseRadius / 2.0F), 1.0, (double)(baseRadius / 2.0F)))).move(0.0, (double)(caster.getBbHeight() / 2.0F), 0.0);

        for (net.minecraft.world.entity.LivingEntity entity : worldIn.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, bb)) {
            if (entity == caster) {
                continue;
            }

            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }

                if (caster instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned && owned.getTrueOwner() != null && player == owned.getTrueOwner()) {
                    continue;
                }
            } else if (com.Polarice3.Goety.utils.MobUtil.areAllies(caster, entity)) {
                continue;
            }

            if (!caster.hasLineOfSight(entity)) {
                continue;
            }
        }

        AreaDamage areaDamage = AreaDamage.spawnAreaDamage(
                worldIn,
                offsetPos,
                caster,
                0.0F,
                null,
                baseRadius,
                baseRadius,
                0.0F,
                3.0F,
                extraTime,
                0,
                false,
                false,
                0.0,
                0.0,
                false,
                false,
                0,
                false,
                null,
                4);
        areaDamage.setSentFrom(BlockPos.containing(caster.position().add(0.0, (double)caster.getBbHeight() * 0.6, 0.0)));
        areaDamage.witherBreathBuffLevelBonus = buffLevelBonus;
        areaDamage.witherBreathUsingNetherStaff = usingNetherStaff;
        worldIn.addFreshEntity(areaDamage);

        this.playSound(worldIn, caster, ModSounds.RUMBLE.get(), 2.0F, 1.0F);
    }
}