package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class StraySkelewagServant extends SkelewagServant {

    public StraySkelewagServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkelewagServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SkelewagServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkelewagServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.SkelewagServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SkelewagServantMovementSpeed.get());
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.getTarget();
        boolean attackTick = !this.level().isClientSide
                && target != null
                && this.distanceTo(target) < 2.0F + target.getBbWidth()
                && this.hasLineOfSight(target)
                && ((this.getAnimation() == ANIMATION_STAB && this.getAnimationTick() == 7)
                || (this.getAnimation() == ANIMATION_SLASH && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 0 && this.getAnimationTick() < 25));
        super.tick();
        if (attackTick) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600), this);
        }

        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            if (CuriosFinder.hasFrostSet(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
        }
    }
}
