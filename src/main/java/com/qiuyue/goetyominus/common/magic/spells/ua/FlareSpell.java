package com.qiuyue.goetyominus.common.magic.spells.ua;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.qiuyue.goetyominus.common.entities.ally.ua.FlareServant;
import com.qiuyue.goetyominus.common.init.ua.UaEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FlareSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominus.config.SpellConfig.FlareSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 80;
    }

    @Nullable
    @Override
    public net.minecraft.sounds.SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominus.config.SpellConfig.FlareCooldown.get();
    }

    @Override
    public int SummonDownDuration() {
        return 300;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
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
        return livingEntity -> livingEntity instanceof FlareServant;
    }

    @Override
    public int summonLimit() {
        return 16;
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
            SpawnGroupData spawngroupdata = null;
            int numberOfFlares = 1;

            if (rightStaff(staff)) {
                numberOfFlares = caster.getRandom().nextIntBetweenInclusive(1, 4);
            }

            for (int i1 = 0; i1 < numberOfFlares; ++i1) {
                FlareServant flare = UaEntityRegistry.FLARE_SERVANT.get().create(worldIn);
                if (flare != null) {
                    BlockPos.MutableBlockPos blockpos$mutable = BlockFinder.SummonFlyingRadius(
                            caster.blockPosition(), flare, worldIn, 15
                    ).mutable();

                    while (blockpos$mutable.getY() < caster.getY() + 20 + caster.getRandom().nextInt(15)
                            && !worldIn.getBlockState(blockpos$mutable).blocksMotion()) {
                        blockpos$mutable.move(Direction.UP);
                    }

                    DifficultyInstance difficultyinstance = worldIn.getCurrentDifficultyAt(blockpos$mutable);
                    flare.setOwnerId(caster.getUUID());
                    flare.moveTo(blockpos$mutable, 0.0F, 0.0F);
                    spawngroupdata = flare.finalizeSpawn(
                            worldIn, difficultyinstance, MobSpawnType.MOB_SUMMONED,
                            spawngroupdata, (CompoundTag) null
                    );

                    boolean hasVoidCrown = CuriosFinder.hasVoidCrown(caster);
                    boolean hasVoidRobe = CuriosFinder.hasVoidRobe(caster);
                    boolean hasVoidSet = hasVoidCrown && hasVoidRobe;
                    boolean isVoidStaff = staff.getItem() instanceof IWand wand && wand.getSpellType() == SpellType.VOID;

                    flare.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);

                    if (hasVoidSet) {
                        flare.setVoidStaffSummoned(isVoidStaff);
                    }

                    if (hasVoidRobe) {
                        flare.setUpgraded(true);
                    }

                    if (potency > 0) {
                        flare.setPhantomSize(potency);
                    }

                    this.buffSummon(caster, flare, potency);
                    this.SummonSap(caster, flare);
                    this.setTarget(caster, flare);
                    worldIn.addFreshEntityWithPassengers(flare);
                    this.summonAdvancement(caster, flare);
                }
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}