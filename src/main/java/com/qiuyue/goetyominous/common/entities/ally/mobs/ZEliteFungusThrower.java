package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ZEliteFungusThrower extends ZFungusThrower {

    public final AnimationState healAnimationState = new AnimationState();

    public ZEliteFungusThrower(EntityType<? extends ZPiglinServant> type, Level level) {
        super(type, level);
        this.refreshDimensions();
    }

    @Override
    public float getScale() {
        return 1.25F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(0.75F, 1.9375F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FungusThrowerServantDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal ->
                goal instanceof com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal
                        || goal instanceof LookAtPlayerGoal
                        || goal instanceof HurtByTargetGoal
                        || goal instanceof AvoidEntityGoal);
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, net.minecraft.world.entity.LivingEntity.class, 8.0F, 1.2D, 1.2D,
                target -> target == this.getTarget() && target != this.getTrueOwner()));
        this.goalSelector.addGoal(2, new ThrowAcidGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public boolean isHealing() {
        return false;
    }

    @Override
    protected float getFungusDamage() {
        return 5.0F;
    }
}
