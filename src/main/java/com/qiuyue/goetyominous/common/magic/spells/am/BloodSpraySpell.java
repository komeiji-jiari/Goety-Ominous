package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.EntityMosquitoServantSpit;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantHemolymph;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BloodSpraySpell extends Spell {

    public int defaultSoulCost() {
        return (Integer) SpellConfig.BloodSpraySoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.BloodSprayCastDuration.get();
    }

    public int defaultSpellCooldown() {
        return (Integer) SpellConfig.BloodSprayCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.LAVA_POP;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        if (useTicks % 2 == 0) {
            int potency = WandUtil.getPotencyLevel(caster);
            float velocityLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
            float speed = 1.0F + velocityLevel * 0.2F;
            boolean netherStaff = this.typeStaff(staff, this.getSpellType());
            boolean netherOutfit = CuriosFinder.hasNetherCrown(caster)
                    && CuriosFinder.hasNetherRobe(caster);
            boolean enhanced = netherStaff && netherOutfit;

            Vec3 view = caster.getViewVector(1.0F);
            Vec3 right = view.cross(new Vec3(0, 1, 0)).normalize();
            double offset = 0.3D;
            double height = 0.3D;
            double fx = caster.getX() + right.x * offset;
            double fy = caster.getEyeY() + height;
            double fz = caster.getZ() + right.z * offset;

            if (enhanced) {
                worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                        SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0F,
                        0.5F + (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F);
                EntityServantHemolymph hemolymph = new EntityServantHemolymph(worldIn, caster,
                        caster.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT);
                hemolymph.setPos(fx, fy, fz);
                hemolymph.setExtraDamage(hemolymph.getExtraDamage() + potency);
                hemolymph.shoot(view.x, view.y, view.z, speed, 3.0F);
                worldIn.addFreshEntity(hemolymph);
            } else {
                worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                        SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0F,
                        1.2F + (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F);
                EntityMosquitoServantSpit spit = new EntityMosquitoServantSpit(worldIn, caster);
                spit.setPos(fx, fy, fz);
                spit.setExtraDamage(spit.getExtraDamage() + potency);
                spit.shoot(view.x, view.y, view.z, speed, 10.0F);
                worldIn.addFreshEntity(spit);
            }
        }
    }
}
