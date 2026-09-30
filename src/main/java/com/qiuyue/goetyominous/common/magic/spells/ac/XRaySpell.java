package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.XRayPacket;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class XRaySpell extends ChargingSpell {

    private static final float BASE_RADIUS = 1.0F;
    private static final int HIT_INTERVAL = 3;
    private static final int SYNC_INTERVAL = 2;
    private static final int DEFAULT_RANGE = 25;
    private static final int IRRADIATED_DURATION = 800;
    private static final double SYNC_RANGE = 128.0D;
    private static final float BASE_DAMAGE = 1.5F;
    private static final int XRAY_POTENCY = 2;
    private static final int GAMMA_POTENCY = 3;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.XRaySoulCost.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.XRayCoolDown.get();
    }

    @Override
    public int Cooldown() {
        return 1;
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.XRayCastDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ACSoundRegistry.RAYGUN_START.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(SpellConfig.XRayRange.get());
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        double radiusLevel = spellStat.getRadius();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            radiusLevel += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }

        boolean xRay = potency >= XRAY_POTENCY;
        boolean gamma = potency >= GAMMA_POTENCY;
        float radius = BASE_RADIUS + (float) radiusLevel / 2.0F;
        float damage = BASE_DAMAGE + potency;
        double distance = Math.max(1.0D, range);

        Vec3 from = rayOrigin(caster);
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 to = from.add(look.scale(distance));
        Vec3 end = xRay ? to : worldIn.clip(new ClipContext(from, to,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster)).getLocation();

        HitResult entityHit = xRay
                ? xRayScan(worldIn, caster, from, look, distance)
                : firstEntityHit(caster, from, to, end);
        if (entityHit != null) {
            end = entityHit.getLocation();
        }

        if (useTicks % SYNC_INTERVAL == 0) {
            ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                            caster.getX(), caster.getY(), caster.getZ(), SYNC_RANGE, worldIn.dimension())),
                    new XRayPacket(caster.getId(), end, gamma));
        }

        if (useTicks % HIT_INTERVAL == 0) {
            hurtAround(worldIn, caster, end, radius, gamma, damage);
        }
    }

    private static Vec3 rayOrigin(LivingEntity caster) {
        return new Vec3(caster.getX(), caster.getEyeY(), caster.getZ());
    }

    private static HitResult firstEntityHit(LivingEntity caster, Vec3 from, Vec3 to, Vec3 blockEnd) {
        HitResult hit = ProjectileUtil.getHitResultOnViewVector(caster,
                Entity::canBeHitByProjectile, from.distanceTo(to));
        if (hit instanceof EntityHitResult entityHit
                && entityHit.getLocation().distanceToSqr(from) > blockEnd.distanceToSqr(from)) {
            return null;
        }
        return hit instanceof EntityHitResult ? hit : null;
    }

    private static HitResult xRayScan(ServerLevel level, LivingEntity caster, Vec3 from, Vec3 look, double distance) {
        AABB maxAABB = caster.getBoundingBox().inflate(distance);
        Vec3 cursor = from;
        for (double step = 1.0D; step < distance; ++step) {
            Vec3 next = cursor.add(look);
            HitResult hit = ProjectileUtil.getEntityHitResult(level, caster, cursor, next, maxAABB,
                    Entity::canBeHitByProjectile);
            if (hit != null) {
                return hit;
            }
            cursor = next;
        }
        return null;
    }

    private static void hurtAround(ServerLevel level, LivingEntity caster, Vec3 center,
                                   float radius, boolean gamma, float damage) {
        AABB hitBox = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        for (Entity entity : level.getEntities(caster, hitBox, Entity::canBeHitByProjectile)) {
            if (entity == caster || entity.is(caster)
                    || MobUtil.areAllies(caster, entity)
                    || entity.isAlliedTo(caster) || caster.isAlliedTo(entity)
                    || caster.isPassengerOfSameVehicle(entity)) {
                continue;
            }
            if (!entity.hurt(ACDamageTypes.causeRaygunDamage(level.registryAccess(), caster), damage)) {
                continue;
            }
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(),
                        IRRADIATED_DURATION, gamma ? 4 : 0));
            }
        }
    }
}
