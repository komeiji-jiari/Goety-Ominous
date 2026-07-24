package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.someillagerservants.config.MobsConfig;
import com.qiuyue.someillagerservants.common.init.ModSounds;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class UrbhadhachServant extends Summoned implements PlayerRideable {
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(UrbhadhachServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ROAR_ID = SynchedEntityData.defineId(UrbhadhachServant.class, EntityDataSerializers.INT);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private int roarCooldown;
    private int healTime;

    public UrbhadhachServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.xpReward = 0;
        this.moveControl = new UrbhadhachServantMoveControl(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new JumpAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(2, new AttackGoal());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AgeableMob.class, 10, true, false, LivingEntity::isBaby));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.UrbhadhachHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.UrbhadhachDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.URBHADHACH_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.URBHADHACH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.URBHADHACH_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(ModSounds.URBHADHACH_STEP.get(), 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(ModSounds.URBHADHACH_ROAR.get(), 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(DATA_ROAR_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RoarCooldown", this.roarCooldown);
        compound.putInt("HealTime", this.healTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.roarCooldown = compound.getInt("RoarCooldown");
        this.healTime = compound.getInt("HealTime");
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
    public void travel(Vec3 pTravelVector) {
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
            super.travel(new Vec3(strafe, 0.0, forward));
            return;
        }
        super.travel(pTravelVector);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.71D;
    }

    @Override
    protected float getJumpPower() {
        return this.isVehicle() ? 0.0F : super.getJumpPower();
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
        if (!this.hasNamelessSet() && this.level().isDay() && this.level().canSeeSky(this.blockPosition()) && !this.level().isRainingAt(this.blockPosition())) {
            this.setSecondsOnFire(8);
        }

        if (this.healTime > 0) {
            --this.healTime;
            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }
        }

        if (this.roarCooldown > 0) {
            --this.roarCooldown;
        }

        if (this.getHealth() <= this.getMaxHealth() / 2) {
            for (AgeableMob ageableMob : this.level().getEntitiesOfClass(AgeableMob.class, this.getBoundingBox().inflate(32))) {
                if (ageableMob.isBaby() && this.getTarget() != ageableMob) {
                    this.setTarget(ageableMob);
                }
            }
            if (this.getTarget() != null && this.getTarget().isBaby()) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false));
            }
        }

        if (this.isVehicle()) return;

        if (this.roarCooldown <= 0) {
            if (this.getTarget() != null && !this.getTarget().isBaby() && !this.isStanding() && this.isAlive()) {
                this.getLookControl().setLookAt(this.getTarget().position());
                this.roarCooldown = 300;
                this.setRoarTick(20);
                this.level().broadcastEntityEvent(this, (byte) 104);
            }
        }

        if (this.getRoarTick() > 0) {
            this.decreaseRoarTick();
            if (this.getRoarTick() == 10) {
                this.roar();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }
            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }

    public int getRoarTick() {
        return this.entityData.get(DATA_ROAR_ID);
    }

    public void setRoarTick(int roarTick) {
        this.entityData.set(DATA_ROAR_ID, roarTick);
    }

    public void decreaseRoarTick() {
        this.setRoarTick(this.getRoarTick() - 1);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            return super.getDimensions(pose).scale(1.0F, 1.0F + f);
        }
        return super.getDimensions(pose);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = target.hurt(this.damageSources().mobAttack(this), (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.level().broadcastEntityEvent(this, (byte) 105);
            this.playSound(ModSounds.URBHADHACH_ATTACK.get(), 1.0F, 1.0F);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.FREEZING.get(), 100, 1), this);
                if (this.random.nextFloat() < 0.25F) {
                    if (livingEntity instanceof Player player) {
                        if (player.isUsingItem() && player.getUseItem().is(Items.SHIELD)) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                            player.stopUsingItem();
                            player.level().broadcastEntityEvent(player, (byte) 30);
                        }
                    }
                    livingEntity.knockback(4.0F,
                            Mth.sin(livingEntity.getYRot() * ((float) Math.PI / 180F)),
                            -Mth.cos(livingEntity.getYRot() * ((float) Math.PI / 180F)));
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }
                if (livingEntity.isBaby()) {
                    livingEntity.hurt(this.damageSources().mobAttack(this), livingEntity.getMaxHealth());
                }
                if (livingEntity.isDeadOrDying()) {
                    this.onKillBaby(livingEntity);
                    if (!livingEntity.isBaby()) {
                        livingEntity.addEffect(new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.FREEZING.get(), 100, 0));
                    }
                }
            }
            this.doEnchantDamageEffects(this, target);
        }
        return flag;
    }

    private void onKillBaby(LivingEntity killedEntity) {
        if (killedEntity.isBaby() && killedEntity.getMobType() != MobType.UNDEAD) {
            this.playSound(SoundEvents.GENERIC_EAT, 2.0F, 0.25F);
            this.playSound(SoundEvents.PLAYER_BURP, 2.0F, 0.25F);
            this.heal(killedEntity.getMaxHealth() * 2);
            this.healTime = 600;
        }
    }

    @Override
    public boolean isSunBurnTick() {
        return this.level().isDay() && !this.level().isRainingAt(this.blockPosition());
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.MOVEMENT_SLOWDOWN
                || effectInstance.getEffect() == com.Polarice3.Goety.common.effects.GoetyEffects.FREEZING.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (stack.isEmpty() && !this.isVehicle()) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem().isEdible() && stack.getFoodProperties(this) != null && stack.getFoodProperties(this).isMeat()) {
                if (this.getHealth() < this.getMaxHealth()) {
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
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (ModDamageSource.freezeAttacks(source) || source.is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) {
            return false;
        }
        float damage = amount;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            damage = amount * 2;
        }
        if (this.isStanding()) {
            damage = amount * 1.5F;
        }
        return super.hurt(source, damage);
    }

    private void roar() {
        if (this.isAlive() && !this.isSilent()) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(4.0D),
                    MobUtil.NO_CREATIVE_OR_SPECTATOR.and(e -> e != this && !MobUtil.areAllies(this, e)))) {
                if (entity.hurt(ModDamageSource.directFreeze(this), 6.0F)) {
                    this.knockBack(entity);
                }
            }

            if (this.isOnFire()) {
                this.clearFire();
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(16.0D),
                        MobUtil.NO_CREATIVE_OR_SPECTATOR.and(e -> e != this && !MobUtil.areAllies(this, e)))) {
                    if (livingEntity.canFreeze() && livingEntity.getMaxHealth() < this.getMaxHealth()) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300));
                    }
                }
                this.heal(2.0F);
                Vec3 center = this.getBoundingBox().getCenter();
                int particleRadius = 4;
                for (int ix = -particleRadius; ix <= particleRadius; ++ix) {
                    for (int j = -particleRadius; j <= particleRadius; ++j) {
                        for (int k = -particleRadius; k <= particleRadius; ++k) {
                            double d13 = (double) ix + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d15 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d17 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d19 = Math.sqrt(d13 * d13 + d15 * d15 + d17 * d17) / 0.5D + this.random.nextGaussian() * 0.05D;
                            serverLevel.sendParticles(ModParticleTypes.FROST_NOVA.get(),
                                    center.x, center.y, center.z, 0,
                                    d13 / d19, d15 / d19, d17 / d19, 0.5F);
                            if (ix != -particleRadius && ix != particleRadius && j != -particleRadius && j != particleRadius) {
                                k += particleRadius * 2 - 1;
                            }
                        }
                    }
                }
                serverLevel.sendParticles(
                        new com.Polarice3.Goety.client.particles.ShockwaveParticleOption(0.0F, (float)particleRadius * 2.0F, 1),
                        center.x, center.y + 0.5D, center.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void knockBack(Entity entity) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        entity.push(d0 / d2 * 2.0D, 0.1D, d1 / d2 * 2.0D);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getRoarTick() > 0;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(DATA_STANDING_ID, standing);
    }

    public float getStandingAnimationScale(float partialTick) {
        return Mth.lerp(partialTick, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 104) {
            this.roarCooldown = 300;
            this.setRoarTick(20);
            this.playSound(ModSounds.URBHADHACH_STRONG_ROAR.get(), 5.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.UrbhadhachServantLimit.get();
    }

    class AttackGoal extends MeleeAttackGoal {
        private int attackSpeed;

        public AttackGoal() {
            super(UrbhadhachServant.this, 1.25D, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTime()) {
                this.resetAttack();
                this.mob.doHurtTarget(enemy);
                UrbhadhachServant.this.setStanding(false);
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.attackTime()) {
                    UrbhadhachServant.this.setStanding(false);
                    this.resetAttack();
                }
                if (this.ticksUntilNextAttack() <= 10) {
                    UrbhadhachServant.this.setStanding(true);
                    UrbhadhachServant.this.playWarningSound();
                }
            } else {
                this.resetAttack();
                UrbhadhachServant.this.setStanding(false);
            }
        }

        @Override
        public void start() {
            super.start();
            this.attackSpeed = 0;
        }

        @Override
        public void stop() {
            UrbhadhachServant.this.setStanding(false);
            super.stop();
        }

        @Override
        public void tick() {
            this.attackSpeed = Math.max(this.attackSpeed - 1, 0);
            super.tick();
        }

        protected void resetAttack() {
            this.attackSpeed = this.mob.getRandom().nextFloat() <= 0.25 ? 10 : 20;
        }

        protected boolean attackTime() {
            return this.attackSpeed <= 0;
        }

        @Override
        protected int getAttackInterval() {
            return this.attackSpeed;
        }

        protected int ticksUntilNextAttack() {
            return this.attackSpeed;
        }
    }

    static class JumpAtTargetGoal extends Goal {
        private final UrbhadhachServant mob;
        private LivingEntity target;
        private final float yd;

        public JumpAtTargetGoal(UrbhadhachServant mob, float yd) {
            this.mob = mob;
            this.yd = yd;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isVehicle()) return false;
            this.target = this.mob.getTarget();
            if (target == null) return false;
            double d0 = this.mob.distanceToSqr(target);
            if (d0 >= Mth.square(4.0F) && d0 < Mth.square(8.0F)) {
                return this.mob.onGround() && !this.mob.isUnderWater() && this.mob.getRandom().nextFloat() < 0.25F;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.onGround();
        }

        @Override
        public void start() {
            this.mob.setStanding(true);
            Vec3 delta = this.mob.getDeltaMovement();
            Vec3 dir = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
            if (dir.lengthSqr() > 1.0E-7D) dir = dir.normalize().add(delta);
            this.mob.setYRot(-((float) Mth.atan2(this.target.getX() - this.mob.getX(), this.target.getZ() - this.mob.getZ())) * (180F / (float) Math.PI));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.setDeltaMovement(dir.x, this.yd, dir.z);
        }

        @Override
        public void stop() {
            this.mob.setStanding(false);
        }
    }

    static class UrbhadhachServantMoveControl extends MoveControl {
        private final UrbhadhachServant frostHunter;

        public UrbhadhachServantMoveControl(UrbhadhachServant frostHunter) {
            super(frostHunter);
            this.frostHunter = frostHunter;
        }

        @Override
        public void tick() {
            LivingEntity target = this.frostHunter.getTarget();
            if (this.frostHunter.isInWater()) {
                if (target != null && target.getY() > this.frostHunter.getY()) {
                    this.frostHunter.setDeltaMovement(this.frostHunter.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }
                if (this.operation != Operation.MOVE_TO || this.frostHunter.getNavigation().isDone()) {
                    this.frostHunter.setSpeed(0.0F);
                    return;
                }
                double d0 = this.wantedX - this.frostHunter.getX();
                double d1 = this.wantedY - this.frostHunter.getY();
                double d2 = this.wantedZ - this.frostHunter.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.frostHunter.setYRot(this.rotlerp(this.frostHunter.getYRot(), f, 90.0F));
                this.frostHunter.yBodyRot = this.frostHunter.getYRot();
                float speed = (float) (this.speedModifier * this.frostHunter.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.frostHunter.getSpeed(), speed);
                this.frostHunter.setSpeed(f2);
                this.frostHunter.setDeltaMovement(this.frostHunter.getDeltaMovement().add(f2 * d0 * 0.005D, f2 * d1 * 0.1D, f2 * d2 * 0.005D));
            } else {
                if (!this.frostHunter.onGround()) {
                    this.frostHunter.setDeltaMovement(this.frostHunter.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
    }
}
