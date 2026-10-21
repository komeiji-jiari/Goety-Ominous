package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantShulkerServantBullet;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public class ShulkerBulletSpell extends Spell {
    private static final float[] SHOT_ANGLES = {0.0F, -10.0F, 10.0F};
    private static final int[] SHOT_DELAYS = {0, 10, 15};

    @Override
    public int defaultSoulCost() {
        return MmConfig.ShulkerBulletSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.ShulkerBulletCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.ShulkerBulletCooldown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        boolean usingVoidStaff = this.rightStaff(staff);
        float damage = MmConfig.ShulkerBulletDamage.get().floatValue() + potency * 2;
        float explosionSize = MutantShulkerCommonConfig.mutant_shulker_bullet_explosion_size.get().floatValue()
                + (usingVoidStaff ? 2.0F : 0.0F);
        int hits = MutantShulkerCommonConfig.mutant_shulker_bullet_hits.get();
        float trackSpeed = 1.25F + velocity * 0.2F;

        LivingEntity target = this.findTarget(worldIn, caster);
        if (target == null) {
            return;
        }
        for (int i = 0; i < SHOT_ANGLES.length; ++i) {
            float angle = SHOT_ANGLES[i];
            int delay = SHOT_DELAYS[i];
            Runnable shot = () -> this.shootBullet(worldIn, caster, target, angle, damage, explosionSize, hits, trackSpeed);
            if (delay <= 0) {
                shot.run();
            } else {
                worldIn.getServer().tell(new TickTask(worldIn.getServer().getTickCount() + delay, shot));
            }
        }
    }

    private void shootBullet(ServerLevel worldIn, LivingEntity caster, LivingEntity target, float yawOffset,
                             float damage, float explosionSize, int hits, float trackSpeed) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level() != worldIn) {
            return;
        }
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != worldIn) {
            return;
        }
        MutantShulkerServantBullet bullet = new MutantShulkerServantBullet(MmEntityRegistry.MUTANT_SHULKER_SERVANT_BULLET.get(), worldIn);
        bullet.damage = damage;
        bullet.explosionSize = explosionSize;
        bullet.setRemainingHits(hits);
        bullet.moveDelay = 8;
        bullet.trackSpeed = trackSpeed;
        bullet.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
        bullet.setTarget(target);
        bullet.setOwner(caster);
        Vec3 aim = new Vec3(target.getX() - bullet.getX(),
                target.getY(0.5) - bullet.getY(),
                target.getZ() - bullet.getZ()).yRot((float) Math.toRadians(yawOffset));
        bullet.shoot(aim.x, aim.y, aim.z, 1.0F, 0.0F);
        worldIn.addFreshEntity(bullet);
        this.playSound(worldIn, caster, SoundEventInit.MUTANT_SHULKER_SHOOT.get(), 2.0F, this.projPitch(caster.getRandom()));
    }

    @Nullable
    private LivingEntity findTarget(ServerLevel worldIn, LivingEntity caster) {
        LivingEntity target = null;
        if (caster instanceof Player) {
            Vec3 eyePos = caster.getEyePosition(1.0F);
            Vec3 lookVec = caster.getViewVector(1.0F);
            double bestDot = 0.98D;
            for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class,
                    caster.getBoundingBox().inflate(64.0D),
                    e -> e != caster && e.isAlive() && !MobUtil.areAllies(caster, e)
                            && !(e instanceof Player p && (p.isCreative() || p.isSpectator())))) {
                Vec3 toEntity = entity.getEyePosition(1.0F).subtract(eyePos);
                double distSq = toEntity.lengthSqr();
                if (distSq < 1.0D) continue;
                double dot = toEntity.normalize().dot(lookVec);
                if (dot > bestDot && caster.hasLineOfSight(entity)) {
                    bestDot = dot;
                    target = entity;
                }
            }
        }
        if (target == null) {
            double bestDist = Double.MAX_VALUE;
            for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class,
                    caster.getBoundingBox().inflate(32.0D),
                    e -> e != caster && e.isAlive() && !MobUtil.areAllies(caster, e)
                            && !(e instanceof Player p && (p.isCreative() || p.isSpectator())))) {
                double d = caster.distanceToSqr(entity);
                if (d < bestDist) {
                    bestDist = d;
                    target = entity;
                }
            }
        }
        return target;
    }
}
