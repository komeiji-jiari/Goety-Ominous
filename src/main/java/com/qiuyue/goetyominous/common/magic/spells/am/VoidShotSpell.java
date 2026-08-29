package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantVoidWormShot;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoidShotSpell extends EverChargeSpell {

    public int defaultSoulCost() {
        return (Integer) SpellConfig.VoidShotSoulCost.get();
    }

    public int defaultCastUp() {
        return (Integer) SpellConfig.VoidShotCastUp.get();
    }

    public int Cooldown() {
        return (Integer) SpellConfig.VoidShotDuration.get();
    }

    public int defaultSpellCooldown() {
        return (Integer) SpellConfig.VoidShotCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
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
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(64);
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTicks, SpellStat spellStat) {
        int castUp = this.castUp(caster, staff);
        if (useTicks < castUp) {
            return;
        }
        if (useTicks >= castUp + (Integer) SpellConfig.VoidShotDuration.get()) {
            caster.stopUsingItem();
            if (caster instanceof Player player) {
                ItemStack focus = IWand.getFocus(staff);
                if (!focus.isEmpty()) {
                    SEHelper.addCooldown(player, focus.getItem(), this.spellCooldown(player));
                    SEHelper.sendSEUpdatePacket(player);
                }
            }
            return;
        }
        boolean voidStaff = this.typeStaff(staff, SpellType.VOID);
        int tickRate = voidStaff ? 7 : 10;
        if (useTicks % tickRate != 0) return;

        LivingEntity target = this.acquireTarget(worldIn, caster);

        int potency = WandUtil.getPotencyLevel(caster);
        float velocityLevel = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        float speed = 0.5F + velocityLevel * 0.1F;
        float extraDamage = potency * 2.0F;

        Vec3 forward = caster.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(forward.x, 0.0D, forward.z).normalize();
        Vec3 right = horizontal.cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize();
        double lateral = worldIn.random.triangle(0.0D, 1.0D);
        Vec3 riseDir = horizontal.scale(2.5D).add(0.0D, 3.0D, 0.0D).add(right.scale(lateral * 1.5D));

        EntityServantVoidWormShot shot = new EntityServantVoidWormShot(worldIn, caster, target, false);
        shot.setPos(caster.getX() + horizontal.x * 1.5D + right.x * lateral,
                caster.getY() + 1.0D,
                caster.getZ() + horizontal.z * 1.5D + right.z * lateral);
        shot.setExtraDamage(extraDamage);
        shot.setSpeed(speed);
        shot.setRiseDir(riseDir);
        shot.setRiseTicks(20);
        shot.setVoidStaff(voidStaff);
        worldIn.addFreshEntity(shot);
    }

    private LivingEntity acquireTarget(ServerLevel worldIn, LivingEntity caster) {
        CompoundTag data = caster.getPersistentData();

        LivingEntity aimed = this.getTarget(caster);
        if (aimed != null && aimed.isAlive() && !aimed.isSpectator() && !caster.isAlliedTo(aimed)) {
            data.putUUID("GoetyOminousVoidShotTarget", aimed.getUUID());
            return aimed;
        }

        UUID lockedId = data.hasUUID("GoetyOminousVoidShotTarget") ? data.getUUID("GoetyOminousVoidShotTarget") : null;
        if (lockedId != null) {
            LivingEntity locked = EntityFinder.getLivingEntityByUuiD(lockedId);
            if (locked != null && locked.isAlive() && !locked.isSpectator()
                    && caster.distanceToSqr(locked) <= 64.0D * 64.0D
                    && !caster.isAlliedTo(locked)) {
                return locked;
            }
            data.remove("GoetyOminousVoidShotTarget");
        }

        int range = this.defaultStats().getRange();
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate((double) range),
                e -> e != caster && e.isAlive() && !e.isSpectator()
                        && !caster.isAlliedTo(e) && caster.hasLineOfSight(e))) {
            if (entity instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned) {
                LivingEntity trueOwner = owned.getTrueOwner();
                if (trueOwner != null && (trueOwner == caster || caster.isAlliedTo(trueOwner))) {
                    continue;
                }
            }
            double dist = caster.distanceToSqr(entity);
            if (dist < bestDist) {
                bestDist = dist;
                best = entity;
            }
        }
        if (best != null) {
            data.putUUID("GoetyOminousVoidShotTarget", best.getUUID());
        }
        return best;
    }
}
