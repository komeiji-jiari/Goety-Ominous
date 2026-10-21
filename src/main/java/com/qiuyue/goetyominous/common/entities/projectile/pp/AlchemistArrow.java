package com.qiuyue.goetyominous.common.entities.projectile.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.ArrayList;
import java.util.List;

public class AlchemistArrow extends Arrow {

    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> customEffects = new ArrayList<>();

    public AlchemistArrow(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    @Override
    public void setEffectsFromItem(ItemStack stack) {
        super.setEffectsFromItem(stack);
        this.potion = stack.is(Items.TIPPED_ARROW) ? PotionUtils.getPotion(stack) : Potions.EMPTY;
        this.customEffects.clear();
        for (MobEffectInstance effect : PotionUtils.getCustomEffects(stack)) {
            this.customEffects.add(new MobEffectInstance(effect));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (!this.level().isClientSide && target instanceof LivingEntity living && this.isFriendlyTarget(living)) {
            Entity source = this.getEffectSource();
            for (MobEffectInstance effect : this.potion.getEffects()) {
                living.addEffect(new MobEffectInstance(effect.getEffect(),
                        Math.max(effect.mapDuration(duration -> duration / 8), 1),
                        effect.getAmplifier(), effect.isAmbient(), effect.isVisible()), source);
            }
            for (MobEffectInstance effect : this.customEffects) {
                living.addEffect(new MobEffectInstance(effect), source);
            }
            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.discard();
            return;
        }
        super.onHitEntity(result);
    }

    private boolean isFriendlyTarget(LivingEntity target) {
        if (!(this.getOwner() instanceof AbstractPiglinServant shooter)
                || !(target instanceof AbstractPiglinServant ally)) {
            return false;
        }
        LivingEntity owner = shooter.getTrueOwner();
        return owner != null && owner == ally.getTrueOwner();
    }
}
