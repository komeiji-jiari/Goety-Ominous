package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.FireSlimeServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InfernoSpell extends SummonSpell {

    public InfernoSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.InfernoSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.InfernoCastDuration.get();
    }

    public int SummonDownDuration() {
        return 0;
    }

    public int defaultSpellCooldown() {
        return SpellConfig.InfernoCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof FireSlimeServant;
    }

    public int summonLimit() {
        return MobsConfig.FireSlimeServantLimit.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        int count = this.rightStaff(staff) ? 2 : 1;
        float yRot = caster.getViewYRot(0.0F);
        float xRot = caster.getXRot();
        float bodyRot = caster.getYRot();
        double dirX = -Mth.sin(bodyRot * ((float) Math.PI / 180.0F)) * Mth.cos(xRot * ((float) Math.PI / 180.0F));
        double dirY = -Mth.sin(xRot * ((float) Math.PI / 180.0F));
        double dirZ = Mth.cos(bodyRot * ((float) Math.PI / 180.0F)) * Mth.cos(xRot * ((float) Math.PI / 180.0F));

        for (int i = 0; i < count; ++i) {
            FireSlimeServant slime = new FireSlimeServant(OfEntityRegistry.FIRE_SLIME_SERVANT.get(), worldIn);
            slime.shootFromOwner(dirX, dirY, dirZ, 1.5F, 0.75F);
            Vec3 motion = slime.getDeltaMovement();
            Vec3 casterMotion = caster.getDeltaMovement();
            slime.setDeltaMovement(motion.add(casterMotion.x, caster.onGround() ? 0.0D : casterMotion.y, casterMotion.z));
            slime.setPos(caster.getEyePosition().add(slime.getDeltaMovement().normalize()));
            slime.yBodyRot = yRot;
            slime.setYHeadRot(yRot);
            slime.setTrueOwner(caster);
            slime.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
            slime.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.TRIGGERED, (SpawnGroupData) null, (CompoundTag) null);
            this.buffSummon(caster, slime, potency);
            this.SummonSap(caster, slime);
            this.setTarget(caster, slime);
            if (worldIn.addFreshEntity(slime)) {
                this.uponSummon(worldIn, caster, staff, slime);
            }
            this.summonAdvancement(caster, slime);
        }

        this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
    }
}
