package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PrimalPackSpell extends SummonSpell {

    private static final float GROTTOCERATOPS_CHANCE = 0.05F;

    public PrimalPackSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.PrimalPackCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.PrimalPackDuration.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.PrimalPackCoolDown.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.PrimalPackSummonDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof VallumraptorServant
                || livingEntity instanceof GrottoceratopsServant;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        int raptors = 0;
        int ceratops = 0;
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof LivingEntity living && entity instanceof IOwned owned
                    && owned.getTrueOwner() == caster && living.isAlive()) {
                if (living instanceof VallumraptorServant) {
                    ++raptors;
                } else if (living instanceof GrottoceratopsServant) {
                    ++ceratops;
                }
            }
        }
        if (!this.isShifting(caster)
                && (raptors >= MobsConfig.VallumraptorServantLimit.get()
                || ceratops >= MobsConfig.GrottoceratopsServantLimit.get())) {
            if (caster instanceof Player player) {
                player.displayClientMessage(
                        Component.translatable("info.goety.summon.limit"), true);
            }
            return false;
        }
        return super.conditionsMet(worldIn, caster);
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
                count = 2 + caster.level().random.nextInt(3);
            }
            boolean wildRobe = CuriosFinder.hasWildRobe(caster);

            for (int i = 0; i < count; ++i) {
                Summoned summonedentity;
                if (worldIn.random.nextFloat() < GROTTOCERATOPS_CHANCE) {
                    summonedentity = new GrottoceratopsServant(AcEntityRegistry.GROTTOCERATOPS_SERVANT.get(), worldIn);
                } else {
                    summonedentity = new VallumraptorServant(AcEntityRegistry.VALLUMRAPTOR_SERVANT.get(), worldIn);
                }

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                int life = MobUtil.getSummonLifespan(worldIn) * duration;

                summonedentity.setLimitedLife(life);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);

                if (wildRobe && summonedentity instanceof VallumraptorServant raptor) {
                    raptor.setElder(true);
                }

                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }

                this.summonAdvancement(caster, summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
