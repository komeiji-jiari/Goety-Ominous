package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.qiuyue.goetyominous.common.items.curios.CroneRobeItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SwampWolf extends BlackWolf {

    public SwampWolf(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    public void curseTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {
            MobEffect poison = this.ownerHasCroneRobe() ? GoetyEffects.ACID_VENOM.get() : MobEffects.POISON;
            living.addEffect(new MobEffectInstance(poison, MathHelper.secondsToTicks(4), 0), this);
        }
    }

    private boolean ownerHasCroneRobe() {
        LivingEntity owner = this.getMasterOwner();
        return owner != null
                && CuriosFinder.hasCurio(owner, stack -> stack.getItem() instanceof CroneRobeItem);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return super.canBeAffected(effect) && !isSwampImmune(effect);
    }

    private static boolean isSwampImmune(MobEffectInstance effect) {
        return effect.getEffect() == MobEffects.POISON
                || effect.getEffect() == GoetyEffects.ACID_VENOM.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.75F;
        }
        return super.hurt(source, amount);
    }
}
