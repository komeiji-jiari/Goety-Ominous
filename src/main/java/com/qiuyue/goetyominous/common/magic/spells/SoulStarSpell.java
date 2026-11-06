package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.SoulStar;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModAttributes;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SoulStarSpell extends Spell {
    private static final int BASE_INTERVAL = 8;
    private static final int RIGHT_STAFF_INTERVAL = 5;
    private static final int NAMELESS_STAFF_INTERVAL = 4;
    private static final int RISE_TICKS = 30;
    private static final double BASE_SPEED = 1.0D;
    private static final double VELOCITY_PER_LEVEL = 0.1D;
    private static final float NAMELESS_DAMAGE_MULTIPLIER = 1.5F;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SoulStarSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SoulStarCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return this.defaultCastDuration();
    }

    @Override
    public int spellCooldown(LivingEntity caster) {
        return (int) (this.castDuration(caster, ItemStack.EMPTY) * ModAttributes.getCooldownDiscount(caster));
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.GRAVE_MONSTROSITY_CALL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(
                ModEnchantments.POTENCY.get(),
                ModEnchantments.VELOCITY.get(),
                ModEnchantments.RADIUS.get()
        );
    }

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(64);
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        boolean nameless = staff.is(ModItems.NAMELESS_STAFF.get());
        int interval = nameless ? NAMELESS_STAFF_INTERVAL : this.rightStaff(staff) ? RIGHT_STAFF_INTERVAL : BASE_INTERVAL;
        if (useTicks % interval != 0) {
            return;
        }

        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        double extraRadius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
            extraRadius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) * 0.5D;
        }

        float damage = SpellConfig.SoulStarDamage.get().floatValue() + potency;
        if (nameless) {
            damage *= NAMELESS_DAMAGE_MULTIPLIER;
        }
        double speed = BASE_SPEED + velocity * VELOCITY_PER_LEVEL;

        SoulStar star = new SoulStar(caster, this.acquireTarget(worldIn, caster), worldIn);
        BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), star, worldIn);
        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
        star.setNecro(nameless);
        star.setBaseDamage(damage);
        star.setExtraRadius((float) extraRadius);
        star.speed = speed;
        star.setPos(vec3.subtract(0.0D, 2.0D, 0.0D));
        star.signalTo(vec3.add(0.0D, 4.0D, 0.0D), RISE_TICKS);
        star.life = 0;
        worldIn.addFreshEntity(star);
    }

    private LivingEntity acquireTarget(ServerLevel worldIn, LivingEntity caster) {
        LivingEntity aimed = this.getTarget(caster);
        if (aimed != null && aimed.isAlive() && !aimed.isSpectator() && !ServantAllyUtil.areAllied(caster, aimed)) {
            return aimed;
        }

        int range = this.defaultStats().getRange();
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate(range),
                entity -> entity != caster && entity.isAlive() && !entity.isSpectator()
                        && !ServantAllyUtil.areAllied(caster, entity) && caster.hasLineOfSight(entity))) {
            if (entity instanceof Owned owned) {
                LivingEntity trueOwner = owned.getTrueOwner();
                if (trueOwner != null && (trueOwner == caster || ServantAllyUtil.areAllied(caster, trueOwner))) {
                    continue;
                }
            }
            double distance = caster.distanceToSqr(entity);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = entity;
            }
        }
        return best;
    }
}
