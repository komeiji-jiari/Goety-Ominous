package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wild.LeapingSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeapingSpell.class)
public class LeapingSpellWaterMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void goetyominous$summonLeapkelp(ServerLevel worldIn, LivingEntity caster,
                                             ItemStack staff, SpellStat spellStat,
                                             CallbackInfo ci) {
        LeapingSpell self = (LeapingSpell) (Object) this;

        boolean abyssStaff = self.typeStaff(staff, SpellType.ABYSS);

        if (!abyssStaff && !caster.isInWater()) {
            return;
        }

        self.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!self.isShifting(caster)) {
            int count = 1;
            if (self.rightStaff(staff)) {
                count += caster.getRandom().nextBoolean() ? 1 : 0;
            }

            for (int n = 0; n < count; ++n) {
                Leapkelp summoned = new Leapkelp(ModEntityTypes.LEAPKELP.get(), worldIn);
                summoned.setTrueOwner(caster);

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summoned, worldIn);
                if (caster.isInWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                summoned.moveTo(blockPos, 0.0F, 0.0F);

                summoned.setPersistenceRequired();
                summoned.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summoned.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                self.buffSummon(caster, summoned, potency);
                self.SummonSap(caster, summoned);
                self.setTarget(caster, summoned);
                if (worldIn.addFreshEntity(summoned)) {
                    self.uponSummon(worldIn, caster, staff, summoned);
                }
                self.summonAdvancement(caster, summoned);
            }

            self.SummonDown(caster);
            self.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }

        ci.cancel();
    }
}
