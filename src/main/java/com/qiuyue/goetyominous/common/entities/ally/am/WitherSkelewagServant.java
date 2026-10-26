package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import com.qiuyue.goetyominous.config.am.AmConfig;

public class WitherSkelewagServant extends SkelewagServant {

    public WitherSkelewagServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AmConfig.SkelewagServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_DAMAGE, AmConfig.SkelewagServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45);
    }

    @Override
    public boolean isInWater() {
        return super.isInWater() || this.isInLava();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getFluidState(pos).is(FluidTags.WATER) || level.getFluidState(pos).is(FluidTags.LAVA)) {
            return 10.0F + level.getLightLevelDependentMagicValue(pos) - 0.5F;
        }
        return super.getWalkTargetValue(pos, level());
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
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
        }
        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            if (CuriosFinder.hasNetherSet(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
        }
    }
}
