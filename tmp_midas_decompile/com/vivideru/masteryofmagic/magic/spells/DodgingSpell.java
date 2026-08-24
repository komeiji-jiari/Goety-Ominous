/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.Spell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.network.DodgeInputPacket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public class DodgingSpell
extends Spell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.DODGING_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return 0;
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.WIND.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.DODGING_COOLDOWN.get();
    }

    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            potency += WandUtil.getPotencyLevel((LivingEntity)caster);
        }
        Vec3 direction = this.getDodgeDirection(caster);
        double power = this.rightStaff(staff) ? 2.5 : 1.5;
        double strength = power + (double)potency / 2.0;
        caster.f_19864_ = true;
        caster.m_6853_(false);
        caster.m_20334_(direction.f_82479_ * strength, direction.f_82480_ * strength, direction.f_82481_ * strength);
        caster.f_19812_ = true;
        caster.f_19789_ = 0.0f;
        this.playSound(worldIn, caster, 2.0f, 1.0f);
    }

    private Vec3 getDodgeDirection(LivingEntity caster) {
        DodgeInputPacket.DodgeInput input = DodgeInputPacket.INPUTS.get(caster.m_20148_());
        if (input == null) {
            return new Vec3(0.0, 0.7, 0.0);
        }
        double forwardInput = 0.0;
        double strafeInput = 0.0;
        if (input.forward()) {
            forwardInput += 1.0;
        }
        if (input.backward()) {
            forwardInput -= 1.0;
        }
        if (input.left()) {
            strafeInput -= 1.0;
        }
        if (input.right()) {
            strafeInput += 1.0;
        }
        if (forwardInput == 0.0 && strafeInput == 0.0) {
            if (input.shift()) {
                return new Vec3(0.0, -1.0, 0.0);
            }
            return new Vec3(0.0, 1.0, 0.0);
        }
        Vec3 look = caster.m_20154_();
        Vec3 forward = new Vec3(look.f_82479_, 0.0, look.f_82481_).m_82541_();
        Vec3 left = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_).m_82541_();
        return forward.m_82490_(forwardInput).m_82549_(left.m_82490_(strafeInput)).m_82541_();
    }
}

