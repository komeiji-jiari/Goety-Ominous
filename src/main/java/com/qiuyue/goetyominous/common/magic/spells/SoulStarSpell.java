package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.MCDEffectOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.SoulStar;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModAttributes;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public static final String SOUL_EATER_TAG = "GoetyOminousSoulEater";
    private static final int DURATION_BONUS_PER_LEVEL = 40;

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
                ModEnchantments.RADIUS.get(),
                ModEnchantments.SOUL_EATER.get(),
                ModEnchantments.DURATION.get()
        );
    }

    @Override
    public int castDuration(LivingEntity caster, ItemStack staff) {
        return super.castDuration(caster, staff)
                + WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) * DURATION_BONUS_PER_LEVEL;
    }

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(64);
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        if (caster instanceof Player player) {
            int total = this.castDuration(caster, staff);
            if (castTime > 0 && castTime < total) {
                int cooldown = (int) ((long) this.spellCooldown(caster) * castTime / total);
                SEHelper.addSpellCooldown(player, this, cooldown);
            }
        }
    }

    private static void spawnSoulParticles(ServerLevel level, LivingEntity caster, boolean alternate) {
        RandomSource random = level.getRandom();
        Vec3 origin = caster.position();
        float yaw = caster.yBodyRot * Mth.DEG_TO_RAD;

        double fx = -Mth.sin(yaw);
        double fz = Mth.cos(yaw);
        double rx = -Mth.cos(yaw);
        double rz = -Mth.sin(yaw);
        double lf = (random.nextDouble() * 2.0D - 1.0D) * 0.91D;
        double lr = (random.nextDouble() * 2.0D - 1.0D) * 1.06D;
        double ox = fx * lf + rx * lr;
        double oz = fz * lf + rz * lr;
        double x = origin.x + ox;
        double y = origin.y + (random.nextBoolean() ? 0.01D : -0.01D);
        double z = origin.z + oz;
        double h = Math.sqrt(ox * ox + oz * oz);
        double out = random.nextDouble() * 0.5D;
        double vx = h > 1.0E-4D ? ox / h * out : 0.0D;
        double vz = h > 1.0E-4D ? oz / h * out : 0.0D;
        double vy = 1.0D + random.nextDouble();

        level.sendParticles(new MCDEffectOption(
                ModParticleTypes.SOUL_LEADER.get(),
                0.4F + random.nextFloat() * 0.1F,
                20 + random.nextInt(21),
                (float) (origin.x - x),
                0.5F + random.nextFloat() * 0.5F,
                (float) (origin.z - z),
                alternate), x, y, z, 0, vx, vy, vz, 1.0D);

        Vec3 spark = soulCone(random, 0.0D, 67.5D)
                .scale(1.5D + random.nextDouble() * 1.5D)
                .add(0.0D, random.nextDouble() * 0.5D, 0.0D);
        float gravity = -(50.0F + random.nextFloat() * 100.0F) / 40000.0F;

        level.sendParticles(new MCDEffectOption(
                ModParticleTypes.SOUL_SPARK.get(),
                0.06F + random.nextFloat() * 0.06F,
                20 + random.nextInt(21),
                0.6F + random.nextFloat() * 0.4F,
                1.0F + random.nextFloat() * 29.0F,
                gravity,
                alternate), origin.x, origin.y, origin.z, 0, spark.x, spark.y, spark.z, 1.0D);
    }

    private static Vec3 soulCone(RandomSource random, double minDeg, double maxDeg) {
        double cosOuter = Math.cos(Math.toRadians(maxDeg));
        double cosInner = Math.cos(Math.toRadians(minDeg));
        double cos = Mth.lerp(random.nextDouble(), cosOuter, cosInner);
        double sin = Math.sqrt(Math.max(0.0D, 1.0D - cos * cos));
        double phi = random.nextDouble() * Math.PI * 2.0D;
        return new Vec3(Math.cos(phi) * sin, cos, Math.sin(phi) * sin);
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        boolean nameless = staff.is(ModItems.NAMELESS_STAFF.get());

        if (useTicks >= 16 && useTicks % 4 == 0) {
            spawnSoulParticles(worldIn, caster, nameless);
        }

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
        int soulEater = WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), caster);
        star.getPersistentData().putInt(SOUL_EATER_TAG, soulEater);
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
