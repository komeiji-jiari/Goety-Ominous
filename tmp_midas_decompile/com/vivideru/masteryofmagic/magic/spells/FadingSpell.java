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
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.phys.AABB
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FadingSpell
extends Spell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.FADING_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return 0;
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.VOID_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.FADING_COOLDOWN.get();
    }

    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.RANGE.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            range += WandUtil.getRangeLevel((LivingEntity)caster);
        }
        double maxDistance = this.rightStaff(staff) ? 6.0 : 4.0;
        Vec3 direction = this.getFadeDirection(caster);
        Vec3 target = this.findFarthestFreePosition(worldIn, caster, direction, maxDistance += (double)range * 0.25);
        if (target == null) {
            return;
        }
        caster.m_6021_(target.f_82479_, target.f_82480_, target.f_82481_);
        caster.m_183634_();
        worldIn.m_7605_((Entity)caster, (byte)46);
        this.playSound(worldIn, (Entity)caster, SoundEvents.f_11852_, 2.0f, 1.0f);
    }

    private Vec3 getFadeDirection(LivingEntity caster) {
        DodgeInputPacket.DodgeInput input = DodgeInputPacket.INPUTS.get(caster.m_20148_());
        if (input == null) {
            return new Vec3(0.0, 1.0, 0.0);
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

    private Vec3 findFarthestFreePosition(ServerLevel worldIn, LivingEntity caster, Vec3 direction, double maxDistance) {
        double step = 0.25;
        for (double distance = maxDistance; distance > 0.0; distance -= step) {
            Vec3 target = caster.m_20182_().m_82549_(direction.m_82490_(distance));
            if (!this.canTeleportTo(worldIn, caster, target)) continue;
            return target;
        }
        return null;
    }

    private boolean canTeleportTo(ServerLevel worldIn, LivingEntity caster, Vec3 target) {
        AABB movedBox = caster.m_20191_().m_82383_(target.m_82546_(caster.m_20182_()));
        return worldIn.m_45756_((Entity)caster, movedBox) && worldIn.m_6857_().m_156093_(target.f_82479_, target.f_82481_);
    }
}

