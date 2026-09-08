package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.qiuyue.goetyominous.common.entities.projectile.FartServantEntity;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FartSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FartSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FartCastDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FartCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return new ArrayList<>();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {

        if (typeStaff(staff, SpellType.NETHER)) {
            caster.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 2.0F, 1.0F);
            worldIn.explode(caster, caster.getX(), caster.getY(), caster.getZ(),
                    3.0F, false, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
            this.playSound(worldIn, caster,
                    AMSoundRegistry.STINK_RAY.get(), 1.0F,
                    0.9F + (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F);
            return;
        }

        FartServantEntity fart = new FartServantEntity(worldIn, caster,
                caster.getMainArm() == HumanoidArm.LEFT);
        Vec3 viewVec = caster.getViewVector(1.0F);
        caster.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ITEM_INTERACT_START);
        fart.shoot(viewVec.x, viewVec.y, viewVec.z, 0.6F, 0.0F);
        worldIn.addFreshEntity(fart);
        this.playSound(worldIn, fart,
                AMSoundRegistry.STINK_RAY.get(), 1.0F,
                0.9F + (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F);
    }
}
