package com.qiuyue.goetyominus.common.magic.spells.ua;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominus.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.goetyominus.common.entities.ally.ua.ThrasherServant;
import com.qiuyue.goetyominus.common.init.ua.UaEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ThrasherSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominus.config.SpellConfig.ThrasherSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 120;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominus.config.SpellConfig.ThrasherCooldown.get();
    }

    @Override
    public int SummonDownDuration() {
        return 300;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof ThrasherServant;
    }

    @Override
    public int summonLimit() {
        return 10;
    }

    @Override
    public void commonResultHit(ServerLevel worldIn, LivingEntity caster) {
        for (int i = 0; i < caster.level().random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
        this.playSound(worldIn, caster, ModSounds.DROWNED_NECROMANCER_SUMMON.get());
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!isShifting(caster)) {
            int summonCount = 1;
            boolean useGreatThrasher = false;

            if (rightStaff(staff) && CuriosFinder.hasAbyssRobes(caster)) {
                useGreatThrasher = true;
                summonCount = 1;
            } else if (rightStaff(staff)) {
                summonCount = 2;
            }

            for (int i = 0; i < summonCount; ++i) {
                Summoned summonedentity;

                if (useGreatThrasher) {
                    summonedentity = new GreatThrasherServant(UaEntityRegistry.GREAT_THRASHER_SERVANT.get(), worldIn);
                } else {
                    summonedentity = new ThrasherServant(UaEntityRegistry.THRASHER_SERVANT.get(), worldIn);
                }

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }

                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);

                if (CuriosFinder.hasAbyssCrown(caster)) {
                    summonedentity.setPersistenceRequired();
                } else {
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                }

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
            this.playSound(worldIn, caster, ModSounds.DROWNED_NECROMANCER_SUMMON.get());
        }
    }
}
