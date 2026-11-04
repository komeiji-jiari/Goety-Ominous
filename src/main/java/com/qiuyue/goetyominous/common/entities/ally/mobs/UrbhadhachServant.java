package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractUrbhadhach;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class UrbhadhachServant extends AbstractUrbhadhach implements PlayerRideable {
    private static final double PASSENGER_RIDING_OFFSET = 1.6D;

    public UrbhadhachServant(EntityType<? extends AbstractUrbhadhach> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StandbyGoal());
        this.goalSelector.addGoal(3, new RestrictSunGoal(this));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new ShadeSleepGoal());
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AgeableMob.class, 10, true, false, LivingEntity::isBaby));
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.UrbhadhachServantLimit.get();
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        }
        return super.getControllingPassenger();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float strafe = player.xxa * 0.5F;
            float forward = player.zza;
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, 0.0D, forward));
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public double getPassengersRidingOffset() {
        return PASSENGER_RIDING_OFFSET;
    }

    @Override
    protected float getJumpPower() {
        return this.isVehicle() ? 0.0F : super.getJumpPower();
    }

    @Override
    public boolean isSunBurnTick() {
        return super.isSunBurnTick() && !this.hasNamelessSet();
    }

    @Override
    public void spawnUpgraded() {
        super.spawnUpgraded();
        if (this.hasNamelessSet()) {
            this.setUpgraded(true);
        }
    }

    private boolean hasNamelessSet() {
        if (this.getTrueOwner() instanceof Player player) {
            boolean hasCrown = CuriosFinder.hasCurio(player,
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation("goety", "nameless_crown")));
            boolean hasCape = CuriosFinder.hasCurio(player,
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation("goety", "nameless_cape")));
            return hasCrown && hasCape;
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            int animation = this.getAnimationState();
            if ((animation == ANIM_SIT || animation == ANIM_SLEEP)
                    && (this.getTarget() != null || this.hurtTime > 0 || this.isVehicle()
                            || !this.isStaying() && !this.canRestInShade())) {
                this.triggerWakeUp();
            }
        }
    }

    private boolean canRestInShade() {
        return (this.isWandering() || this.isGuardingArea())
                && this.level().isDay()
                && !this.level().canSeeSky(this.blockPosition());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (stack.isEmpty() && !this.isVehicle()) {
                this.triggerWakeUp();
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem().isEdible() && stack.getFoodProperties(this) != null && stack.getFoodProperties(this).isMeat()) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.triggerWakeUp();
                    this.heal(stack.getFoodProperties(this).getNutrition());
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.gameEvent(GameEvent.EAT, this);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void onKillEntity(LivingEntity killedEntity) {
        if (!killedEntity.isBaby()) {
            killedEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, 0));
        }
    }

    @Override
    public void tryKill(Player player) {
        this.triggerWakeUp();
        super.tryKill(player);
    }

    class StandbyGoal extends Goal {
        private static final int SIT_DELAY = 40;
        private int stayTicks;
        private int sitTicks;

        StandbyGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return UrbhadhachServant.this.isStaying()
                    && UrbhadhachServant.this.getTarget() == null
                    && !UrbhadhachServant.this.isVehicle()
                    && !UrbhadhachServant.this.isPlayingSpecialAnimation();
        }

        @Override
        public boolean canContinueToUse() {
            return UrbhadhachServant.this.isStaying()
                    && UrbhadhachServant.this.getTarget() == null
                    && !UrbhadhachServant.this.isVehicle();
        }

        @Override
        public void start() {
            this.stayTicks = 0;
            this.sitTicks = 0;
        }

        @Override
        public void tick() {
            int animation = UrbhadhachServant.this.getAnimationState();
            if (animation == ANIM_WAKE_UP) {
                this.stayTicks = 0;
            } else {
                ++this.stayTicks;
            }
            if (animation == ANIM_SIT) {
                ++this.sitTicks;
            } else {
                this.sitTicks = 0;
            }
            UrbhadhachServant.this.getNavigation().stop();
            if (animation == ANIM_NONE && this.stayTicks > SIT_DELAY) {
                UrbhadhachServant.this.startAnimation(ANIM_SIT, SIT_DURATION);
            } else if (animation == ANIM_SIT && this.sitTicks >= SIT_DURATION) {
                UrbhadhachServant.this.startAnimation(ANIM_SLEEP, SLEEP_DURATION);
            }
        }
    }

    class ShadeSleepGoal extends Goal {
        private static final int SIT_DELAY = 40;
        private int restTicks;
        private int sitTicks;

        ShadeSleepGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return UrbhadhachServant.this.canRestInShade()
                    && UrbhadhachServant.this.getTarget() == null
                    && !UrbhadhachServant.this.isVehicle()
                    && UrbhadhachServant.this.getAnimationState() == ANIM_NONE
                    && UrbhadhachServant.this.onGround();
        }

        @Override
        public boolean canContinueToUse() {
            return UrbhadhachServant.this.canRestInShade()
                    && UrbhadhachServant.this.getTarget() == null
                    && !UrbhadhachServant.this.isVehicle()
                    && UrbhadhachServant.this.onGround();
        }

        @Override
        public void start() {
            this.restTicks = 0;
            this.sitTicks = 0;
        }

        @Override
        public void tick() {
            int animation = UrbhadhachServant.this.getAnimationState();
            if (animation == ANIM_WAKE_UP) {
                this.restTicks = 0;
            } else {
                ++this.restTicks;
            }
            if (animation == ANIM_SIT) {
                ++this.sitTicks;
            } else {
                this.sitTicks = 0;
            }
            UrbhadhachServant.this.getNavigation().stop();
            if (animation == ANIM_NONE && this.restTicks > SIT_DELAY) {
                UrbhadhachServant.this.startAnimation(ANIM_SIT, SIT_DURATION);
            } else if (animation == ANIM_SIT && this.sitTicks >= SIT_DURATION) {
                UrbhadhachServant.this.startAnimation(ANIM_SLEEP, SLEEP_DURATION);
            }
        }
    }
}
