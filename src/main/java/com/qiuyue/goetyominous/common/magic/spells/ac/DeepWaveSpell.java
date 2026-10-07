package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.ac.DeepOneMageServantWave;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.KeyOfRlyehMixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DeepWaveSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.DeepWaveCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.DeepWaveCastDuration.get();
    }

    @Override
    public @Nullable SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.DeepWaveCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = (float) spellStat.getPotency();
        int range = spellStat.getRange();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += (float) WandUtil.getPotencyLevel(caster);
            range += WandUtil.getRangeLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        HitResult hitResult = this.rayTrace(worldIn, caster, range, 3.0D);
        Entity target = this.getTarget(caster, range);
        int lifespan;
        Vec3 vec3 = null;
        if (target != null) {
            lifespan = (int) Math.floor((double) caster.distanceTo(target)) + 10;
            vec3 = target.position().subtract(caster.position());
        } else if (hitResult instanceof BlockHitResult result) {
            BlockPos blockPos = result.getBlockPos();
            Vec3 vec30 = Vec3.atCenterOf(blockPos);
            lifespan = (int) Math.floor((double) Mth.sqrt((float) caster.distanceToSqr(vec30))) + 10;
            vec3 = vec30.subtract(caster.position());
        } else {
            lifespan = 10;
        }

        lifespan += range;
        lifespan += duration * 5;

        boolean keyStaff = KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff);
        boolean rightStaff = this.rightStaff(staff);

        int h = rightStaff ? 2 : 1;
        int angleStep = rightStaff ? 10 : 8;

        int from = keyStaff ? -h - 1 : -h;
        int to = keyStaff ? h + 1 : h;

        for (int i = from; i <= to; ++i) {
            DeepOneMageServantWave wave = new DeepOneMageServantWave(worldIn, caster);
            wave.setPos(caster.getX(), caster.getY(), caster.getZ());
            wave.setLifespan(lifespan);
            wave.setExtraDamage(potency);
            if (vec3 != null) {
                wave.setYRot(-((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))) + (float) (i * angleStep));
            } else {
                wave.setYRot(caster.getYRot() + (float) (i * angleStep));
            }
            worldIn.addFreshEntity(wave);
        }
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.PLAYER_SPLASH_HIGH_SPEED, this.getSoundSource(), 1.0F, 1.0F);
    }
}
