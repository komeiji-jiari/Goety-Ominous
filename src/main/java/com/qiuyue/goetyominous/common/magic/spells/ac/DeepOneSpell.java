package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.common.Mod;

public class DeepOneSpell extends SummonSpell {

    public DeepOneSpell() {
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.DeepOneSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.DeepOneCastDuration.get();
    }

    public int SummonDownDuration() {
        return (Integer) SpellConfig.DeepOneSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.DeepOneCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof DeepOneServant;
    }

    public int summonLimit() {
        return MobsConfig.DeepOneServantLimit.get();
    }

    public void commonResult(ServerLevel worldIn, LivingEntity caster) {
        if (this.isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof DeepOneServant) {
                    this.teleportServants(caster, entity);
                }
            }

            for (int i = 0; i < caster.level().random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(),
                        1, 0.0, 0.0, 0.0, 0.0);
            }

            this.playSound(worldIn, caster, ModSounds.DROWNED_NECROMANCER_SUMMON.get());
        }
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
            if (this.rightStaff(staff)) {
                count = 2 + caster.level().random.nextInt(4);
            }

            for (int i = 0; i < count; ++i) {
                Summoned summonedentity =
                        new DeepOneServant(AcEntityRegistry.DEEP_ONE_SERVANT.get(), worldIn);

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }

                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (!caster.isUnderWater()) {
                    MobUtil.moveDownToGround(summonedentity);
                }

                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }

                this.summonAdvancement(caster, summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent) ModSounds.DROWNED_NECROMANCER_SUMMON.get());
        }
    }
}
