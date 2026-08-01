package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.mobs.UrbhadhachServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class UrbhadhachSpell extends SummonSpell {
    public UrbhadhachSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.UrbhadhachSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.UrbhadhachCastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.UrbhadhachSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.UrbhadhachCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof UrbhadhachServant;
    }

    public int summonLimit() {
        return net.minecraftforge.fml.ModList.get().isLoaded("goetyominous")
                ? com.qiuyue.goetyominous.config.MobsConfig.UrbhadhachServantLimit.get()
                : 8;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        int i = 1;
        if (this.rightStaff(staff)) {
            if (staff.getItem() instanceof com.Polarice3.Goety.common.items.magic.NamelessStaff) {
                i = 3;
            } else {
                i = 2 + caster.level().random.nextInt(2);
            }
        }

        for (int i1 = 0; i1 < i; ++i1) {
            UrbhadhachServant summonedentity = new UrbhadhachServant(
                    ModEntityTypes.URBHADHACH_SERVANT.get(), worldIn);
            summonedentity.setTrueOwner(caster);
            summonedentity.moveTo(BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn), 0.0F, 0.0F);
            MobUtil.moveDownToGround(summonedentity);
            summonedentity.setPersistenceRequired();
            summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
            summonedentity.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            this.buffSummon(caster, summonedentity, potency);
            this.SummonSap(caster, summonedentity);
            this.setTarget(caster, summonedentity);
            if (worldIn.addFreshEntity(summonedentity)) {
                this.uponSummon(worldIn, caster, staff, summonedentity);
            }
            this.summonAdvancement(caster, summonedentity);
        }

        this.SummonDown(caster);
        com.Polarice3.Goety.utils.SoundUtil.playNecromancerSummon(caster);
    }
}
