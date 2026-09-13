package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
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

/**
 * 伏特瑶召唤聚晶对应的法术。
 * <p>
 * 流派：风暴（{@link SpellType#STORM}）<br>
 * 数值：100 灵魂 / 100 tick 蓄力 / 1000 tick 冷却 / 仆从上限 8<br>
 * 数量：风暴魔杖一次随机召唤 3~5 只，其他魔杖只召唤 1 只<br>
 * 施法音效照搬精灵聚晶（{@code prepare_summon}）
 * <p>
 * 写法参照风暴派系的召唤法术 SpriteSpell（精灵聚晶）与项目内的 SpiderSpell（垂丝聚晶）。
 */
public class VoltSummonSpell extends SummonSpell {

    public VoltSummonSpell() {
    }

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.VoltSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.VoltCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.VoltCoolDown.get();
    }

    @Override
    public int SummonDownDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.VoltSummonDown.get();
    }

    /** 蓄力时的施法音效，照搬精灵聚晶。 */
    @Override
    public @Nullable SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof VoltServant;
    }

    @Override
    public int summonLimit() {
        return com.qiuyue.goetyominous.config.SpellConfig.VoltLimit.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels((Enchantment) ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!this.isShifting(caster)) {
            int i = 1;
            // rightStaff 判断「法杖流派 == 本法术流派」，对风暴法术而言就是风暴魔杖
            if (this.rightStaff(staff)) {
                i = 3 + caster.level().random.nextInt(3);
            }

            for (int i1 = 0; i1 < i; ++i1) {
                VoltServant voltServant = new VoltServant(OfEntityRegistry.VOLT_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), voltServant, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }

                voltServant.setTrueOwner(caster);
                voltServant.moveTo(blockPos, caster.getYRot(), 0.0F);
                voltServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                voltServant.setPersistenceRequired();
                voltServant.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                this.buffSummon(caster, voltServant, potency);
                this.SummonSap(caster, voltServant);
                this.setTarget(caster, voltServant);
                worldIn.addFreshEntity(voltServant);
                this.summonAdvancement(caster, voltServant);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent) ModSounds.SUMMON_SPELL.get());
        }
    }
}
