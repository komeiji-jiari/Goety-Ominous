package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mm.SandBoulderServant;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public class SandBallSpell extends Spell {

    private static final float BASE_SPEED = 1.0F;
    private static final float VELOCITY_PER_LEVEL = 0.1F;

    @Override
    public int defaultSoulCost() {
        return MmConfig.SandBallSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.SandBallCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.SandBallCooldown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_HUSK_ATTACK.get();
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

        float damage = MmConfig.SandBallDamage.get().floatValue() + potency;
        float speed = BASE_SPEED + velocity * VELOCITY_PER_LEVEL;

        Vec3 view = caster.getViewVector(1.0F);
        Vec3 spawnPos = caster.getEyePosition(1.0F).add(view.scale(1.0D));
        BlockPos sandPos = PositionUtils.getOffsetBlockPos(caster, 0.0D, -1.0D, 3.0D, 0.0F, caster.getYRot());

        SandBoulderServant boulder = new SandBoulderServant(worldIn, caster);
        boulder.moveTo(spawnPos.x, spawnPos.y - 0.2D, spawnPos.z, caster.getYRot(), caster.getXRot());
        boulder.setSandType(MiscUtils.sandStringForPos(worldIn, sandPos));
        boulder.damage = damage;
        boulder.lifeSpan = MutantHuskCommonConfig.sand_boulder_lifespan.get();
        boulder.griefing = MutantHuskCommonConfig.sand_boulder_griefing.get()
                && !MutantMoreGroupedOptionsCommonConfig.quicksand_griefing_off.get();
        boulder.ignoresInvulTime = MutantHuskCommonConfig.ignores_invulnerability_time.get();
        if (this.rightStaff(staff)) {
            boulder.setSizeScale(1.5F);
        }
        boulder.shoot(view.x, view.y, view.z, speed, 0.0F);
        worldIn.addFreshEntity(boulder);

        this.playSound(worldIn, caster, SoundEventInit.MUTANT_HUSK_SAND_THROW.get(), 2.0F, MiscUtils.randomSoundPitch());
    }
}
