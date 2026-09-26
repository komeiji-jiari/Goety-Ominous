package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SkyvernSpell extends SummonSpell {
    public SkyvernSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.SkyvernSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.SkyvernCastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.SkyvernSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.SkyvernCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof SkyvernServant;
    }

    public int summonLimit() {
        return MobsConfig.SkyvernServantLimit.get();
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
            int count = 1;

            for (int i = 0; i < count; ++i) {
                SkyvernServant skyvern = new SkyvernServant(OfEntityRegistry.SKYVERN_SERVANT.get(), worldIn);
                BlockPos.MutableBlockPos blockpos = BlockFinder.SummonFlyingRadius(caster.blockPosition(), skyvern, worldIn, 15).mutable();
                while (blockpos.getY() < caster.getY() + 8 + caster.getRandom().nextInt(5)
                        && !worldIn.getBlockState(blockpos.above()).blocksMotion()) {
                    blockpos.move(Direction.UP);
                }
                skyvern.setTrueOwner(caster);
                skyvern.moveTo(blockpos, caster.getYRot(), 0.0F);
                skyvern.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                skyvern.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                this.buffSummon(caster, skyvern, potency);
                this.SummonSap(caster, skyvern);
                this.setTarget(caster, skyvern);
                if (worldIn.addFreshEntity(skyvern)) {
                    this.uponSummon(worldIn, caster, staff, skyvern);
                }
                this.summonAdvancement(caster, skyvern);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
