package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AcidPoolSpell extends Spell {

    public static final String FEL_MARKER = "goetyominous:fel_acid_pool";

    private static final int COLOR = 15493099;
    private static final int WARMUP_COLOR = 16635131;
    private static final int WARMUP_TICKS = MathHelper.secondsToTicks(0.7F);

    @Override
    public int defaultSoulCost() {
        return SpellConfig.AcidPoolSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.AcidPoolCastDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.AcidPoolCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return GoetyOminous.FEL;
    }

    @Override
    public boolean ReduceCastTime(LivingEntity caster) {
        return super.ReduceCastTime(caster)
                || com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneHat(caster);
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int durationLv = spellStat.getDuration();
        int radiusLv = 0;
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            durationLv += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            radiusLv += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }

        HitResult hit = this.rayTrace(worldIn, caster, range, 3.0D);
        LivingEntity target = this.getTarget(caster, range);

        Vec3 pos = null;
        if (target != null) {
            pos = target.position();
        } else if (hit instanceof BlockHitResult blockHit) {
            pos = blockHit.getLocation();
        }
        if (pos == null) {
            return;
        }

        AcidPool acidPool = new AcidPool(ModEntityType.ACID_POOL.get(), worldIn);
        acidPool.setColor(COLOR);
        acidPool.setWarmupColor(WARMUP_COLOR);
        acidPool.moveTo(pos);
        acidPool.setRadius(SpellConfig.AcidPoolRadius.get().floatValue() + radiusLv * 0.25F);
        acidPool.setDamage(SpellConfig.AcidPoolBaseDamage.get().floatValue() + potency);
        acidPool.setWarmupDelayTicks(WARMUP_TICKS);
        acidPool.setDuration(SpellConfig.AcidPoolBaseDuration.get() + durationLv * 20);
        acidPool.setSoundEvent(ModSounds.TOWER_WRAITH_ACID_VOCAL.get().getLocation().toString());
        acidPool.setOwner(caster);
        if (this.typeStaff(staff, GoetyOminous.FEL)) {
            acidPool.getPersistentData().putBoolean(FEL_MARKER, true);
        }
        if (target == null || !target.isInWater()) {
            MobUtil.moveDownToGround(acidPool);
        }
        if (worldIn.addFreshEntity(acidPool)) {
            this.playSound(worldIn, caster, ModSounds.TOWER_WRAITH_ACID_VOCAL.get());
        }
    }
}
