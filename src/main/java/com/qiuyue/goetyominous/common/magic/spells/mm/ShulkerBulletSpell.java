package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBulletSpell extends EverChargeSpell {
    private static final int SHOT_INTERVAL = 30;
    private static final int MAX_SHOTS = 5;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ShulkerBulletSoulCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.ShulkerBulletCastDuration.get();
    }

    @Override
    public int Cooldown() {
        return SHOT_INTERVAL;
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return MAX_SHOTS;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ShulkerBulletCooldown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_SHULKER_SHOOT.get();
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

        LivingEntity target = null;
        if (caster instanceof Player) {
            Vec3 eyePos = caster.getEyePosition(1.0F);
            Vec3 lookVec = caster.getViewVector(1.0F);
            Vec3 end = eyePos.add(lookVec.scale(64.0D));
            BlockHitResult blockHit = worldIn.clip(new ClipContext(eyePos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
            double maxRange = eyePos.distanceToSqr(blockHit.getLocation());
            HitResult hit = ProjectileUtil.getEntityHitResult(worldIn, caster, eyePos, end,
                    caster.getBoundingBox().expandTowards(lookVec.scale(64.0D)).inflate(1.0D),
                    e -> e != caster && e.isAlive() && !MobUtil.areAllies(caster, e)
                            && !(e instanceof Player p && (p.isCreative() || p.isSpectator())),
                    (float) Math.sqrt(maxRange));
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
                target = living;
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
        if (target == null) {
            return;
        }

        MutantShulkerServantBullet bullet = new MutantShulkerServantBullet(MmEntityRegistry.MUTANT_SHULKER_SERVANT_BULLET.get(), worldIn);
        bullet.damage = SpellConfig.ShulkerBulletDamage.get().floatValue() + potency * 2;
        bullet.explosionSize = MutantShulkerCommonConfig.mutant_shulker_bullet_explosion_size.get().floatValue()
                + (usingVoidStaff ? 2.0F : 0.0F);
        bullet.levitationLength = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_length.get();
        bullet.levitationLevel = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_level.get();
        bullet.setRemainingHits(MutantShulkerCommonConfig.mutant_shulker_bullet_hits.get());
        bullet.moveDelay = 60;
        bullet.trackSpeed = 1.25F + velocity * 0.2F;
        bullet.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
        bullet.setTarget(target);
        bullet.setOwner(caster);
        bullet.shoot(target.getX() - bullet.getX(),
                target.getY(0.5) - bullet.getY(),
                target.getZ() - bullet.getZ(), 1.0F, 0.0F);
        worldIn.addFreshEntity(bullet);
        this.playSound(worldIn, caster, SoundEventInit.MUTANT_SHULKER_SHOOT.get(), 2.0F, 1.0F);
    }
}
