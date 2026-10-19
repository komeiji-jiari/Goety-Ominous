package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import com.qiuyue.goetyominous.config.of.OfConfig;

public class UmberSpell extends SummonSpell {

    public UmberSpell() {
    }

    public int defaultSoulCost() {
        return OfConfig.UmberSoulCost.get();
    }

    public int defaultCastDuration() {
        return OfConfig.UmberCastDuration.get();
    }

    public int SummonDownDuration() {
        return OfConfig.UmberSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return OfConfig.UmberCoolDown.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.WILD_PREPARE_SPELL.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof UmberSpiderServant;
    }

    public int summonLimit() {
        return OfConfig.UmberSpiderServantLimit.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
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
            int count = this.rightStaff(staff) ? worldIn.getRandom().nextIntBetweenInclusive(2, 5) : 1;
            boolean wildSet = CuriosFinder.hasWildSet(caster);

            for (int i = 0; i < count; ++i) {
                UmberSpiderServant umberSpiderServant = new UmberSpiderServant(OfEntityRegistry.UMBER_SPIDER_SERVANT.get(), worldIn);
                BlockPos blockpos = BlockFinder.SummonRadius(caster.blockPosition(), umberSpiderServant, worldIn);
                umberSpiderServant.setTrueOwner(caster);
                umberSpiderServant.moveTo(blockpos, caster.getYRot(), 0.0F);
                MobUtil.moveDownToGround(umberSpiderServant);
                umberSpiderServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                umberSpiderServant.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                if (wildSet && worldIn.getRandom().nextBoolean() && !umberSpiderServant.isElite()) {
                    umberSpiderServant.setElite(true);
                    umberSpiderServant.setEliteStats(umberSpiderServant);
                }
                this.buffSummon(caster, umberSpiderServant, potency);
                this.SummonSap(caster, umberSpiderServant);
                this.setTarget(caster, umberSpiderServant);
                if (worldIn.addFreshEntity(umberSpiderServant)) {
                    this.uponSummon(worldIn, caster, staff, umberSpiderServant);
                }
                this.summonAdvancement(caster, umberSpiderServant);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
