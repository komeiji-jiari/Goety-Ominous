package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EliteFungusThrower extends FungusThrower implements ThrowerAnimatable {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();
    public final AnimationState healAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> DATA_THROWING =
            SynchedEntityData.defineId(EliteFungusThrower.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HEALING =
            SynchedEntityData.defineId(EliteFungusThrower.class, EntityDataSerializers.BOOLEAN);

    private boolean lastThrowing;
    private boolean lastHealing;
    private float walkWeight;

    public EliteFungusThrower(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.refreshDimensions();
    }

    @Override
    public float getScale() {
        return 1.25F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        return EntityDimensions.scalable(base.width * 1.25F, base.height * 1.25F - 0.5F);
    }

    @Override
    protected EntityType<? extends Mob> getConversionTarget() {
        return ModEntityTypes.ZELITE_FUNGUS_THROWER.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new HealFungusGoal(this));
    }

    @Override
    protected float getFungusDamage() {
        return 5.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FungusThrowerServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_THROWING, false);
        this.entityData.define(DATA_HEALING, false);
    }

    public boolean isThrowing() {
        return this.entityData.get(DATA_THROWING);
    }

    public void setThrowing(boolean throwing) {
        this.entityData.set(DATA_THROWING, throwing);
    }

    public boolean isHealing() {
        return this.entityData.get(DATA_HEALING);
    }

    public void setHealing(boolean healing) {
        this.entityData.set(DATA_HEALING, healing);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        boolean throwing = this.isThrowing();
        boolean healing = this.isHealing();
        if (throwing && !this.lastThrowing) {
            this.throwAnimationState.start(this.tickCount);
        } else if (!throwing && this.lastThrowing) {
            this.throwAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }
        if (healing && !this.lastHealing) {
            this.healAnimationState.start(this.tickCount);
        } else if (!healing && this.lastHealing) {
            this.healAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }
        this.walkAnimationState.startIfStopped(this.tickCount);
        this.idleAnimationState.startIfStopped(this.tickCount);
        this.walkWeight = approach(this.walkWeight, this.walkAnimation.speed() > 0.01F ? 1.0F : 0.0F, 0.2F);
        this.lastThrowing = throwing;
        this.lastHealing = healing;
    }

    private static float approach(float value, float target, float step) {
        return value < target ? Math.min(value + step, target) : Math.max(value - step, target);
    }

    @Override
    public float getWalkWeight() {
        return this.walkWeight;
    }

    @Override
    public AnimationState getIdleAnimationState() {
        return this.idleAnimationState;
    }

    @Override
    public AnimationState getWalkAnimationState() {
        return this.walkAnimationState;
    }

    @Override
    public AnimationState getHealAnimationState() {
        return this.healAnimationState;
    }

    public AnimationState getThrowAnimationState() {
        return this.throwAnimationState;
    }

    public void throwHealFungus(@Nullable LivingEntity target) {
        if (this.level().isClientSide || target == null) return;

        com.qiuyue.goetyominous.common.entities.projectile.AcidFungus fungus =
                new com.qiuyue.goetyominous.common.entities.projectile.AcidFungus(this.level(), this);
        fungus.setHeal(true);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.33) - fungus.getY();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        fungus.shoot(dx, dy + distance * 0.2, dz, 1.0F, 8.0F);
        this.level().addFreshEntity(fungus);

        if (!this.isSilent()) {
            int sound = this.random.nextInt(3);
            SoundEvent throwSound;
            if (sound == 0) throwSound = ModSounds.FUNGUS_THROWER_THROW_1.get();
            else if (sound == 1) throwSound = ModSounds.FUNGUS_THROWER_THROW_2.get();
            else throwSound = ModSounds.FUNGUS_THROWER_THROW_3.get();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    throwSound, this.getSoundSource(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
        }
    }

    public class HealFungusGoal extends Goal {
        private final EliteFungusThrower thrower;
        private LivingEntity healTarget;
        private int healTick;
        private int cooldown;

        public HealFungusGoal(EliteFungusThrower thrower) {
            this.thrower = thrower;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.thrower.tickCount < this.cooldown) return false;
            if (this.thrower.isThrowing()) return false;
            
            this.healTarget = this.findHealTarget();
            return this.healTarget != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.healTarget != null && this.healTarget.isAlive() && this.healTick < 47;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.healTick = 0;
            this.thrower.setHealing(true);
        }

        @Override
        public void tick() {
            ++this.healTick;
            this.thrower.getNavigation().stop();
            if (this.healTarget != null) {
                this.thrower.getLookControl().setLookAt(this.healTarget, 100.0F, 100.0F);
                this.thrower.lookAt(this.healTarget, 100.0F, 100.0F);
            }
            if (this.healTick == 29 && this.healTarget != null) {
                this.thrower.swing(InteractionHand.MAIN_HAND);
                this.thrower.throwHealFungus(this.healTarget);
            }
        }

        @Override
        public void stop() {
            this.thrower.setHealing(false);
            this.cooldown = this.thrower.tickCount + 60;
            this.healTarget = null;
            this.healTick = 0;
        }

        @Nullable
        private LivingEntity findHealTarget() {
            LivingEntity owner = this.thrower.getTrueOwner();
            if (this.isHurtAlly(owner, owner)) {
                return owner;
            }
            List<AbstractPiglinServant> allies = this.thrower.level().getEntitiesOfClass(
                    AbstractPiglinServant.class,
                    this.thrower.getBoundingBox().inflate(15.0D, 6.0D, 15.0D),
                    servant -> servant != this.thrower && this.isHurtAlly(servant, owner));
            for (AbstractPiglinServant ally : allies) {
                if (owner != null ? ally.getTrueOwner() == owner : ally.getTrueOwner() == null) {
                    return ally;
                }
            }
            return null;
        }

        private boolean isHurtAlly(@Nullable LivingEntity ally, @Nullable LivingEntity owner) {
            return ally != null && ally.isAlive() && ally != this.thrower
                    && !ally.isInvertedHealAndHarm()
                    && ally.getHealth() < ally.getMaxHealth()
                    && this.thrower.hasLineOfSight(ally);
        }
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
        ZEliteFungusThrower zombie = new ZEliteFungusThrower(
                ModEntityTypes.ZELITE_FUNGUS_THROWER.get(), serverLevel);
        zombie.copyTrueOwner(this);
        zombie.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombie.setBaby(this.isBaby());
        zombie.setHealth(zombie.getMaxHealth());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) zombie.setItemSlot(slot, stack.copy());
        }
        zombie.setGuaranteedDrop(EquipmentSlot.CHEST);
        ForgeEventFactory.onLivingConvert(this, zombie);
        this.discard();
        serverLevel.addFreshEntity(zombie);
    }
}
