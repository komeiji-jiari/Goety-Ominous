package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.EntityPoisonBall;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PoisonBallSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.PoisonBallCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.SLIME_JUMP;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PoisonBallCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return com.qiuyue.goetyominous.GoetyOminous.FEL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (this.rightStaff(staff)) {
            this.staffResult(worldIn, caster);
        } else {
            this.wandResult(worldIn, caster);
        }
    }

    private float getBallSpeed(LivingEntity caster) {
        float velocityLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        return 1.5F + velocityLevel * 0.1F;
    }

    public void wandResult(ServerLevel worldIn, LivingEntity caster) {
        float speed = this.getBallSpeed(caster);
        EntityPoisonBall poisonBall = new EntityPoisonBall(worldIn, caster);
        poisonBall.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, speed, 1.0F);
        worldIn.addFreshEntity(poisonBall);
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                CastingSound(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public void staffResult(ServerLevel worldIn, LivingEntity caster) {
        float speed = this.getBallSpeed(caster);
        EntityPoisonBall poisonBall = new EntityPoisonBall(worldIn, caster);
        poisonBall.setUpgraded(true);
        poisonBall.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, speed, 1.0F);
        worldIn.addFreshEntity(poisonBall);
        for (int i = 0; i < 2; ++i) {
            float spread = (i == 0 ? -2.0F : 2.0F);
            EntityPoisonBall poisonBall1 = new EntityPoisonBall(worldIn, caster);
            poisonBall1.setUpgraded(true);
            poisonBall1.shootFromRotation(caster, caster.getXRot(), caster.getYRot() + spread, 0.0F,
                    speed - (float) i / 10, 1.0F - (float) i / 10);
            worldIn.addFreshEntity(poisonBall1);
        }
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                CastingSound(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        int cost = super.soulCost(caster, staff);
        return CroneCuriosUtil.hasCroneRobe(caster) ? cost / 2 : cost;
    }

    @Override
    public boolean ReduceCastTime(LivingEntity caster) {
        return super.ReduceCastTime(caster) || CroneCuriosUtil.hasCroneHat(caster);
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (worldIn instanceof ServerLevel serverLevel && caster.tickCount % 5 == 0) {
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.WITCH, caster);
        }
    }
}
