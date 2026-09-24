package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsSpiritEntity;
import com.qiuyue.goetyominous.common.events.GrottoceratopsSpiritHandler;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class GrottoceratopsSpiritSpell extends Spell {

    private static final int BASE_SPIRITS = 3;

    @Override public int defaultSoulCost() {
        return SpellConfig.GrottoSpiritCost.get(); }

    @Override public int defaultCastDuration()  {
        return SpellConfig.GrottoSpiritCastTime.get(); }

    @Override public int defaultSpellCooldown() {
        return SpellConfig.GrottoSpiritCoolDown.get(); }

    @Override public SoundEvent CastingSound()  {
        return ACSoundRegistry.GROTTOCERATOPS_CALL.get(); }

    @Override public SpellType getSpellType()   {
        return SpellType.NETHER; }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        int cost = super.soulCost(caster, staff);
        return WandUtil.enchantedFocus(caster) ? cost * 2 : cost;
    }

    @Override
    public boolean conditionsMet(ServerLevel level, LivingEntity caster) {
        return !GrottoceratopsSpiritHandler.hasSpirits(caster) && super.conditionsMet(level, caster);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int count = BASE_SPIRITS + spellStat.getPotency();
        int lifetime = SpellConfig.GrottoSpiritTime.get();
        if (WandUtil.enchantedFocus(caster)) {
            count += WandUtil.getPotencyLevel(caster);
            lifetime *= Math.min(4, WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1);  // DURATION：每级 +100%，封顶 400%
        }
        if (this.rightStaff(staff)) {
            count += 1;
        }

        LivingEntity receiver = caster;
        if (this.isShifting(caster)) {
            LivingEntity looked = this.getTarget(caster);
            if (looked != null && isFriendly(caster, looked)) {
                receiver = looked;
            }
        }

        summonSpirits(worldIn, receiver, count, lifetime);
        this.playSound(worldIn, caster, ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(), 2.0F, 1.0F);
    }

    private static void summonSpirits(ServerLevel level, LivingEntity receiver, int count, int lifetime) {
        float rotateBy = 360.0F / (float) count;
        for (int i = 0; i < count; ++i) {
            GrottoceratopsSpiritEntity spirit = AcEntityRegistry.GROTTOCERATOPS_SPIRIT.get().create(level);
            if (spirit == null) {
                continue;
            }
            spirit.setOwner(receiver);
            spirit.copyPosition(receiver);
            spirit.setRotateOffset((float) i * rotateBy);
            spirit.setLifetime(Math.max(1, lifetime));
            level.addFreshEntity(spirit);
        }
    }

    private static boolean isFriendly(LivingEntity caster, LivingEntity target) {
        if (target == caster || !target.isAlive()) {
            return false;
        }
        if (MobUtil.areAllies(target, caster)) {
            return true;
        }
        if (target instanceof IOwned owned && owned.getTrueOwner() == caster) {
            return true;
        }
        return caster instanceof IOwned a && target instanceof IOwned t && MobUtil.ownerStack(a, t);
    }
}
