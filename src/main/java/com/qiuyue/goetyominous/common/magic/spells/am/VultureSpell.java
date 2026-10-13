package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.am.SoulVultureServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class VultureSpell extends SummonSpell {
    public VultureSpell() {
    }

    public int defaultSoulCost() {
        return (Integer) SpellConfig.VultureSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.VultureCastDuration.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return (Integer) SpellConfig.VultureCoolDown.get();
    }

    public int SummonDownDuration() {
        return (Integer) SpellConfig.VultureSummonDown.get();
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
        return (livingEntity) -> {
            return livingEntity instanceof SoulVultureServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.SoulVultureServantLimit.get();
    }

    public void commonResultHit(ServerLevel worldIn, LivingEntity caster) {
        for (int i = 0; i < caster.level().random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }

        this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels((Enchantment) ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!this.isShifting(caster)) {
            SpawnGroupData spawngroupdata = null;
            int l = 1;
            if (staff.is((Item) ModItems.NAMELESS_STAFF.get())) {
                l = caster.getRandom().nextIntBetweenInclusive(4, 7);
            } else if (this.rightStaff(staff)) {
                l += caster.getRandom().nextInt(4);
            }

            for (int i1 = 0; i1 < l; ++i1) {
                SoulVultureServant phantom = (SoulVultureServant) ((EntityType) AmEntityRegistry.SOUL_VULTURE_SERVANT.get()).create(worldIn);
                if (phantom != null) {
                    BlockPos.MutableBlockPos blockpos$mutable = BlockFinder.SummonFlyingRadius(caster.blockPosition(), phantom, worldIn, 15).mutable();

                    while ((double) blockpos$mutable.getY() < caster.getY() + 20.0 + (double) caster.getRandom().nextInt(15) && !worldIn.getBlockState(blockpos$mutable).blocksMotion()) {
                        blockpos$mutable.move(Direction.UP);
                    }

                    DifficultyInstance difficultyinstance = worldIn.getCurrentDifficultyAt(blockpos$mutable);
                    phantom.setTrueOwner(caster);
                    phantom.moveTo(blockpos$mutable, 0.0F, 0.0F);
                    spawngroupdata = phantom.finalizeSpawn(worldIn, difficultyinstance, MobSpawnType.MOB_SUMMONED, spawngroupdata, (CompoundTag) null);
                    phantom.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);

                    this.buffSummon(caster, phantom, potency);
                    this.SummonSap(caster, phantom);
                    this.setTarget(caster, phantom);
                    worldIn.addFreshEntityWithPassengers(phantom);
                    this.summonAdvancement(caster, phantom);
                }
            }

            this.SummonDown(caster);
            SoundUtil.playNecromancerSummon(caster);
        }

    }
}
