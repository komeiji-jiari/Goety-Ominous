package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.Ripple;
import com.Polarice3.Goety.common.entities.projectiles.SoulBullet;
import com.Polarice3.Goety.common.entities.projectiles.SoulStar;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

final class GrimMonarchCombat {
    private static final ResourceKey<DamageType> COMBO = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(GoetyOminous.MOD_ID, "grim_monarch_combo"));

    private GrimMonarchCombat() {
    }

    private static DamageSource comboSource(GrimMonarchEntity boss) {
        return new DamageSource(boss.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(COMBO), boss);
    }

    static void sweptDashHit(GrimMonarchEntity boss, Vec3 from, Vec3 to, double damageMultiplier) {
        AABB a = boss.getBoundingBox().move(from.subtract(boss.position()));
        AABB b = boss.getBoundingBox().move(to.subtract(boss.position()));
        AABB path = new AABB(
                Math.min(a.minX, b.minX), Math.min(a.minY, b.minY), Math.min(a.minZ, b.minZ),
                Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY), Math.max(a.maxZ, b.maxZ)
        ).inflate(0.4, 0.2, 0.4);
        double radius = (double) boss.getBbWidth() * 0.5 + 0.4;
        for (LivingEntity victim : boss.level().getEntitiesOfClass(LivingEntity.class, path, boss::validVictim)) {
            AABB box = victim.getBoundingBox();
            if (boss.hitThisWindow.contains(victim.getUUID()) || !boss.hasLineOfSight(victim)
                    || !GrimMonarchCombatMath.segmentBox(from.x, from.y, from.z, to.x, to.y, to.z,
                    box.minX - radius, box.minY - (double) boss.getBbHeight() - 0.2, box.minZ - radius,
                    box.maxX + radius, box.maxY + 0.2, box.maxZ + radius)) {
                continue;
            }
            boss.hitThisWindow.add(victim.getUUID());
            hit(boss, victim, damageMultiplier, 1.3, true, from);
        }
    }

    static void circularSweep(GrimMonarchEntity boss, float old, float now, Vec3 oldPosition) {
        if (now < 1.0F || old >= 1.6F || now <= old) {
            return;
        }
        float from = Math.max(old, 1.0F);
        float to = Math.min(now, 1.6F);
        if (to < from) {
            return;
        }
        Vec3 a = oldPosition.lerp(boss.position(), (double) ((from - old) / (now - old)));
        Vec3 b = oldPosition.lerp(boss.position(), (double) ((to - old) / (now - old)));
        double radius = GrimMonarchConstants.SWEEP_RADIUS;
        double height = boss.getBbHeight();
        AABB search = new AABB(a, b).inflate(radius, 0.0, radius).expandTowards(0.0, height, 0.0).inflate(0.0, 0.5, 0.0);
        for (LivingEntity victim : boss.level().getEntitiesOfClass(LivingEntity.class, search, boss::validVictim)) {
            if (boss.hitThisWindow.contains(victim.getUUID())) {
                continue;
            }
            AABB box = victim.getBoundingBox();
            double contact = GrimMonarchCombatMath.sweepCircleContact(a.x, a.y, a.z, b.x, b.y, b.z, radius, height,
                    box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
            if (!Double.isFinite(contact)) {
                continue;
            }
            Vec3 center = a.lerp(b, contact);
            Vec3 eye = center.add(0.0, boss.getEyeY() - boss.getY(), 0.0);
            if (boss.level().clip(new ClipContext(eye, box.getCenter(), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, boss)).getType() != HitResult.Type.MISS) {
                continue;
            }
            boss.hitThisWindow.add(victim.getUUID());
            hit(boss, victim, GrimMonarchConstants.SWEEP_DAMAGE_MULTIPLIER, 1.1, true, center);
        }
    }

    static void radialHit(GrimMonarchEntity boss, Vec3 center, double radius, double damageMultiplier,
                          double force, boolean melee) {
        radialHit(boss, center, radius, damageMultiplier, force, melee, false);
    }

    static void radialHit(GrimMonarchEntity boss, Vec3 center, double radius, double damageMultiplier,
                          double force, boolean melee, boolean shieldPiercing) {
        for (LivingEntity victim : boss.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(radius, 2.5, radius), boss::validVictim)) {
            AABB box = victim.getBoundingBox();
            if (GrimMonarchCombatMath.boxDistanceSquaredXZ(center.x, center.z, box.minX, box.minZ, box.maxX, box.maxZ)
                    > radius * radius
                    || box.maxY < center.y - 0.5 || box.minY > center.y + 2.5
                    || !boss.hasLineOfSight(victim)) {
                continue;
            }
            hit(boss, victim, damageMultiplier, force, melee, false, shieldPiercing, center);
        }
    }

    static boolean hit(GrimMonarchEntity boss, LivingEntity victim, double damageMultiplier, double force,
                       boolean melee, Vec3 origin) {
        return hit(boss, victim, damageMultiplier, force, melee, false, false, origin);
    }

    static boolean hit(GrimMonarchEntity boss, LivingEntity victim, double damageMultiplier, double force,
                       boolean melee, boolean ignoreInvulnerability, Vec3 origin) {
        return hit(boss, victim, damageMultiplier, force, melee, ignoreInvulnerability, false, origin);
    }

    static boolean hit(GrimMonarchEntity boss, LivingEntity victim, double damageMultiplier, double force,
                       boolean melee, boolean ignoreInvulnerability, boolean shieldPiercing, Vec3 origin) {
        if (boss.level().isClientSide || !boss.validVictim(victim)) {
            return false;
        }
        float amount = (float) ((AttributesConfig.GrimMonarchAttackDamage.get() * damageMultiplier
                + victim.getMaxHealth() * AttributesConfig.GrimMonarchHPPercentDamage.get())
                * boss.echoDamageScale());
        if (amount <= 0.0F) {
            return false;
        }
        DamageSource source = shieldPiercing ? comboSource(boss) : boss.damageSources().mobAttack(boss);
        boolean blocking = victim.isDamageSourceBlocked(source);
        ItemStack shield = victim.getUseItem();
        float before = victim.getHealth();
        int previousInvulnerability = victim.invulnerableTime;
        if (ignoreInvulnerability) {
            victim.invulnerableTime = 0;
        }
        boolean result = victim.hurt(source, amount);
        if (!result && ignoreInvulnerability) {
            victim.invulnerableTime = previousInvulnerability;
        }
        if (melee && blocking && victim instanceof Player player
                && GrimMonarchConstants.SHIELD_DISABLE_SECONDS > 0.0D) {
            int ticks = (int) Math.ceil(GrimMonarchConstants.SHIELD_DISABLE_SECONDS * 20.0D - 1.0E-6D);
            player.getCooldowns().addCooldown(shield.getItem(), ticks);
            player.stopUsingItem();
            boss.level().broadcastEntityEvent(player, (byte) 30);
        }
        if ((result || blocking) && force > 0.0D) {
            victim.knockback(force, origin.x - victim.getX(), origin.z - victim.getZ());
            victim.hurtMarked = true;
        }
        if (result && melee) {
            if (victim instanceof Player player) {
                boss.drainSoulFrom(player);
            } else {
                victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                float lost = Math.max(0.0F, before - victim.getHealth());
                boss.heal((float) (lost * GrimMonarchConstants.LIFE_LEECH));
            }
            victim.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 60, 0, false, false));
        }
        return result;
    }

    static void summonSoulStar(GrimMonarchEntity boss, LivingEntity target) {
        if (!(boss.level() instanceof ServerLevel server)) {
            return;
        }
        SoulStar star = new SoulStar(boss, target, server);
        BlockPos pos = BlockFinder.SummonRadius(boss.blockPosition(), star, server);
        Vec3 at = Vec3.atBottomCenterOf(pos);
        star.setNecro(false);
        star.setBaseDamage((float) (AttributesConfig.GrimMonarchAttackDamage.get()
                * GrimMonarchConstants.SOUL_STAR_DAMAGE_MULTIPLIER * boss.echoDamageScale()));
        star.setPos(at.x, at.y + 2.0D, at.z);
        star.signalTo(at.add(0.0D, 4.0D, 0.0D), 30);
        star.life = 0;
        server.addFreshEntity(star);
    }

    static void spawnCalamityRipple(GrimMonarchEntity boss) {
        if (!(boss.level() instanceof ServerLevel server)) {
            return;
        }
        Vec3 front = MobUtil.getFrontPos(boss, 1.0D);
        Ripple ripple = new Ripple(server, boss, front.x, front.y + 1.0D, front.z,
                Ripple.Preset.SOUL_RIPPLE);
        ripple.setSpeed(8.0F);
        ripple.setEndRadius(9.200001F);
        ripple.setDamage((float) (AttributesConfig.GrimMonarchAttackDamage.get()
                * GrimMonarchConstants.RIPPLE_DAMAGE_MULTIPLIER * boss.echoDamageScale()));
        ripple.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), 100));
        server.addFreshEntity(ripple);
    }

    static void spawnSoulBullet(GrimMonarchEntity boss, Vec3 at, LivingEntity target) {
        if (!(boss.level() instanceof ServerLevel server)) {
            return;
        }
        Vec3 direction = target != null
                ? target.getEyePosition().subtract(at).normalize()
                : new Vec3(0.0D, -0.2D, 0.0D);
        SoulBullet bullet = new SoulBullet(server, boss,
                direction.x * 0.4D, direction.y * 0.4D, direction.z * 0.4D);
        bullet.setPos(at.x, at.y, at.z);
        server.addFreshEntity(bullet);
    }

    static boolean hasEcho(GrimMonarchEntity boss) {
        if (!(boss.level() instanceof ServerLevel server)) {
            return false;
        }
        UUID id = boss.echoUUID();
        if (id == null) {
            return false;
        }
        Entity entity = server.getEntity(id);
        return entity == null || entity instanceof GrimMonarchEntity echo && echo.isEcho() && echo.isAlive();
    }

    static boolean spawnEcho(GrimMonarchEntity boss) {
        if (!(boss.level() instanceof ServerLevel server) || hasEcho(boss)) {
            return false;
        }
        Vec3 at = null;
        for (double radius : new double[]{4.0D, 2.5D, 5.5D, 7.0D}) {
            at = findSpawn(boss, radius, 0.8D, true);
            if (at != null) {
                break;
            }
        }
        if (at == null) {
            return false;
        }
        GrimMonarchEntity echo = ModEntityTypes.GRIM_MONARCH_ECHO.get().create(server);
        if (echo == null) {
            return false;
        }
        echo.bindEchoOwner(boss);
        echo.moveTo(at.x, at.y, at.z, boss.getYRot(), 0.0F);
        LivingEntity target = boss.getTarget();
        if (target != null && target.isAlive()) {
            echo.setTarget(target);
        }
        if (!server.addFreshEntity(echo)) {
            return false;
        }
        boss.linkEcho(echo);
        return true;
    }

    private static Vec3 findSpawn(GrimMonarchEntity boss, double radius, double jitter, boolean allowLiquid) {
        for (int i = 0; i < 24; ++i) {
            double angle = boss.getRandom().nextDouble() * Math.PI * 2.0D;
            double r = radius + jitter * (boss.getRandom().nextDouble() - 0.5D);
            Vec3 candidate = boss.position().add(Math.cos(angle) * r, 0.0D, Math.sin(angle) * r);
            Vec3 at = boss.findGround(candidate, 5.0D);
            if (at == null || !boss.supported(at) || !boss.fitsAt(at)) {
                continue;
            }
            if (!allowLiquid && !boss.level().getFluidState(net.minecraft.core.BlockPos.containing(at.x, at.y - 0.03D, at.z)).isEmpty()) {
                continue;
            }
            return at;
        }
        return null;
    }
}
