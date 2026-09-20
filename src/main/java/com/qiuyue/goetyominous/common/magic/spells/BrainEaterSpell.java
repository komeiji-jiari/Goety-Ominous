package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrainEaterSpell extends ChargingSpell {

    @Override
    public int Cooldown() {
        return 0;
    }

    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Override
    public int defaultCastDuration() {
        return 72000;
    }

    @Override
    public boolean everCharge() {
        return true;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
    }

    @Override
    public SpellType getSpellType() {
        return GoetyOminous.FEL;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        if (!(caster instanceof Player player)) return false;
        if (!this.hasExperience(player)) return false;
        if (this.soulsFull(player)) return false;
        return true;
    }

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        int cost = super.soulCost(caster, staff);
        return com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneRobe(caster) ? cost / 2 : cost;
    }

    @Override
    public boolean ReduceCastTime(LivingEntity caster) {
        return super.ReduceCastTime(caster) || com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneHat(caster);
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (worldIn instanceof ServerLevel serverLevel && caster.tickCount % 5 == 0) {
            com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundMiddleSelf(
                    serverLevel, ParticleTypes.WITCH, caster);
        }
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        if (caster instanceof Player player && caster.tickCount % 20 == 0) {
            if (!this.hasExperience(player) || this.soulsFull(player)) {
                player.stopUsingItem();
                return;
            }

            int velocityLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
            int drain = SpellConfig.BrainEaterDrainPerSecond.get() * (1 + velocityLevel);
            int gain = SpellConfig.BrainEaterSoulsPerDrain.get() * (1 + velocityLevel);
            if (this.typeStaff(staff, this.getSpellType())) {
                gain *= 2;
            }

            if (!player.getAbilities().instabuild) {
                player.giveExperiencePoints(-drain);
            }
            SEHelper.increaseSouls(player, gain);

            worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    private boolean hasExperience(Player player) {
        if (player.getAbilities().instabuild) return true;
        return player.experienceLevel > 0 || player.experienceProgress > 0;
    }

    private boolean soulsFull(Player player) {
        if (SEHelper.getSEActive(player)) {

            return SEHelper.getSESouls(player) >= com.Polarice3.Goety.config.MainConfig.MaxArcaSouls.get();
        }
        ItemStack totem = TotemFinder.FindTotem(player);
        if (!totem.isEmpty()) {

            return ITotem.currentSouls(totem) >= ITotem.maximumSouls(totem);
        }
        return true;
    }
}
