package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBrewParticlesPacket;
import com.Polarice3.Goety.utils.BrewUtils;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShamanHealBrew extends PiglinShamanBrew {

    public ShamanHealBrew(EntityType<PiglinShamanBrew> type, Level level) {
        super(type, level);
    }

    public ShamanHealBrew(Level level, LivingEntity owner) {
        super(level, owner);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != this.getOwner() && entity.isAlive() && !entity.isSpectator() && entity.isPickable();
    }

    @Override
    protected void onHit(HitResult result) {
        this.hasImpulse = true;
        if (this.level().isClientSide) {
            return;
        }
        ItemStack itemstack = this.getItem();
        List<MobEffectInstance> mobEffects = PotionUtils.getMobEffects(itemstack);
        List<BrewEffectInstance> brewEffects = BrewUtils.getBrewEffects(itemstack);
        if (!mobEffects.isEmpty() || !brewEffects.isEmpty()) {
            this.applySplash(mobEffects, brewEffects,
                    result.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) result).getEntity() : null);
        }
        ModNetwork.sendToALL(new SAddBrewParticlesPacket(itemstack, this.blockPosition(),
                PotionUtils.getPotion(itemstack).hasInstantEffects(), BrewUtils.getColor(itemstack)));
        this.discard();
    }

    private void applySplash(List<MobEffectInstance> mobEffects, List<BrewEffectInstance> brewEffects,
                             @Nullable Entity hitEntity) {
        ItemStack itemstack = this.getItem();
        int area = BrewUtils.getAreaOfEffect(itemstack) + 4;
        int areaSqr = Mth.square(area);
        AABB aabb = this.getBoundingBox().inflate(area, (double) area / 2.0D, area);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (list.isEmpty()) {
            return;
        }
        Entity owner = this.getOwner();
        Entity source = owner != null ? owner : this;
        for (LivingEntity living : list) {
            if (!living.isAffectedByPotions()) {
                continue;
            }
            double d0 = this.distanceToSqr(living);
            if (d0 >= (double) areaSqr) {
                continue;
            }
            double d1 = 1.0D - Math.sqrt(d0) / (double) areaSqr;
            if (living == hitEntity) {
                d1 = 1.0D;
            }
            boolean isAlly = owner instanceof LivingEntity livingOwner
                    && ServantAllyUtil.areAllied(livingOwner, living);
            for (MobEffectInstance instance : mobEffects) {
                MobEffect effect = instance.getEffect();
                boolean isHarmful = effect.getCategory() == MobEffectCategory.HARMFUL;
                boolean isInstantDamage = effect == MobEffects.HARM;
                if (isAlly && isHarmful && !isInstantDamage || !isAlly && !isHarmful) {
                    continue;
                }
                if (effect.isInstantenous()) {
                    effect.applyInstantenousEffect(this, owner, living, instance.getAmplifier(), d1);
                } else {
                    int duration = (int) (d1 * (double) instance.getDuration() + 0.5D);
                    if (duration > 20) {
                        living.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(),
                                instance.isAmbient(), instance.isVisible()), source);
                    }
                }
            }
            for (BrewEffectInstance instance : brewEffects) {
                BrewEffect effect = instance.getEffect();
                boolean isHarmful = effect.getCategory() == MobEffectCategory.HARMFUL;
                if (isAlly && isHarmful || !isAlly && !isHarmful) {
                    continue;
                }
                if (effect.isInstantenous()) {
                    effect.applyInstantenousEffect(this, owner, living, instance.getAmplifier(), d1);
                } else {
                    effect.applyBlockEffect(this.level(), living.blockPosition(),
                            owner instanceof LivingEntity livingOwner ? livingOwner : null,
                            instance.getDuration(), instance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                }
            }
        }
    }
}
