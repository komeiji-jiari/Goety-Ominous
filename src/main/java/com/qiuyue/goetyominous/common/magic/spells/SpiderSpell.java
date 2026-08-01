package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.spider.*;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags.Structures;
import com.Polarice3.Goety.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SpiderSpell extends SummonSpell {
    public SpiderSpell() {
    }

    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.SpiderCost.get();
    }

    public int defaultCastDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.SpiderDuration.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.WILD_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.SpiderCoolDown.get();
    }

    public int SummonDownDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.SpiderSummonDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof SpiderServant;
        };
    }

    public int summonLimit() {
        return (Integer) SpellConfig.SpiderLimit.get();
    }

    public boolean specialStaffs(ItemStack stack) {
        return this.typeStaff(stack, SpellType.NECROMANCY) || this.typeStaff(stack, SpellType.GEOMANCY)
                || this.typeStaff(stack, SpellType.WILD) || this.typeStaff(stack, SpellType.FROST)
                || this.typeStaff(stack, SpellType.NETHER);
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
            int i = 1;
            if (this.rightStaff(staff)) {
                i = 2 + caster.level().random.nextInt(2);
            } else if (this.specialStaffs(staff)) {
                i = 2;
            }

            for (int i1 = 0; i1 < i; ++i1) {
                SpiderServant spiderServant = new SpiderServant((EntityType) ModEntityType.SPIDER_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), (Entity) spiderServant, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }

                if (this.specialStaffs(staff)) {
                    if (this.typeStaff(staff, SpellType.WILD)) {
                        spiderServant = new WebSpiderServant((EntityType) ModEntityType.WEB_SPIDER_SERVANT.get(), worldIn);
                    } else if (this.typeStaff(staff, SpellType.NECROMANCY)) {
                        spiderServant = new BoneSpiderServant((EntityType) ModEntityType.BONE_SPIDER_SERVANT.get(), worldIn);
                    } else if (this.typeStaff(staff, SpellType.FROST)) {
                        spiderServant = new IcySpiderServant((EntityType) ModEntityType.ICY_SPIDER_SERVANT.get(), worldIn);
                    } else if (this.typeStaff(staff, SpellType.GEOMANCY)) {
                        spiderServant = new CaveSpiderServant((EntityType) ModEntityType.CAVE_SPIDER_SERVANT.get(), worldIn);
                    } else if (this.typeStaff(staff, SpellType.NETHER)) {
                        spiderServant = new CrimsonSpiderServant((EntityType) ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), worldIn);
                    }

                } else if (worldIn.getBiome(blockPos).is(net.minecraftforge.common.Tags.Biomes.IS_SNOWY)) {
                    spiderServant = new IcySpiderServant((EntityType) ModEntityType.ICY_SPIDER_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(net.minecraft.tags.BiomeTags.IS_JUNGLE)) {
                    spiderServant = new WebSpiderServant((EntityType) ModEntityType.WEB_SPIDER_SERVANT.get(), worldIn);
                } else if (BlockFinder.findStructure(worldIn, blockPos, StructureTags.MINESHAFT)) {
                    spiderServant = new CaveSpiderServant((EntityType) ModEntityType.CAVE_SPIDER_SERVANT.get(), worldIn);
                } else if (BlockFinder.findStructure(worldIn, blockPos, Structures.CRYPT)) {
                    spiderServant = new BoneSpiderServant((EntityType) ModEntityType.BONE_SPIDER_SERVANT.get(), worldIn);
                } else if (worldIn.dimension() == Level.NETHER) {
                    spiderServant = new CrimsonSpiderServant((EntityType) ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), worldIn);
                }


                spiderServant.setTrueOwner(caster);
                spiderServant.moveTo(blockPos, 0.0F, 0.0F);
                spiderServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                spiderServant.setPersistenceRequired();
                spiderServant.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                this.buffSummon(caster, spiderServant, potency);
                this.SummonSap(caster, spiderServant);
                this.setTarget(caster, spiderServant);
                if (worldIn.addFreshEntity(spiderServant)) {
                    this.uponSummon(worldIn, caster, staff, spiderServant);
                }

                this.summonAdvancement(caster, spiderServant);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent) ModSounds.SUMMON_SPELL.get());
        }

    }

    public void summonParticles(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        ColorUtil colorUtil = new ColorUtil(657418);
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, 657418, 657418);
    }

}
