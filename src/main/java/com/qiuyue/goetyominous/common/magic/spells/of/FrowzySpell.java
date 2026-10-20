package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.of.OfConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FrowzySpell extends SummonSpell {

    public FrowzySpell() {
    }

    public int defaultSoulCost() {
        return OfConfig.FrowzySoulCost.get();
    }

    public int defaultCastDuration() {
        return OfConfig.FrowzyCastDuration.get();
    }

    public int SummonDownDuration() {
        return OfConfig.FrowzySummonDown.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return OfConfig.FrowzyCoolDown.get();
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
        return livingEntity -> livingEntity instanceof FrowzyServant;
    }

    public int summonLimit() {
        return (Integer) OfConfig.FrowzyServantLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!this.isShifting(caster)) {
            int count = 1;
            if (staff.is(ModItems.NAMELESS_STAFF.get())) {
                count = 7;
            } else if (this.rightStaff(staff)) {
                count = 2 + caster.level().random.nextInt(4);
            }

            for (int i = 0; i < count; ++i) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                FrowzyServant frowzy = new FrowzyServant(OfEntityRegistry.FROWZY_SERVANT.get(), worldIn);
                frowzy.setTrueOwner(caster);
                frowzy.moveTo(blockpos, caster.getYRot(), 0.0F);
                frowzy.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                frowzy.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, frowzy, potency);
                this.SummonSap(caster, frowzy);
                this.setTarget(caster, frowzy);
                if (worldIn.addFreshEntity(frowzy)) {
                    this.uponSummon(worldIn, caster, staff, frowzy);
                }
                this.summonAdvancement(caster, frowzy);
            }

            this.SummonDown(caster);
            com.Polarice3.Goety.utils.SoundUtil.playNecromancerSummon(caster);
        }
    }
}
