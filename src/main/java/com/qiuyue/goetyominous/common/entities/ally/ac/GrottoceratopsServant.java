package com.qiuyue.goetyominous.common.entities.ally.ac;

import java.util.UUID;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantBreedGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantLayEggGoal;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GrottoceratopsServant extends AnimalSummon implements LaysEggs, IAnimatedEntity, PlayerRideable, IAutoRideable {

    private static final EntityDataAccessor<Float> TAIL_SWING_ROT = SynchedEntityData.defineId(GrottoceratopsServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(GrottoceratopsServant.class, EntityDataSerializers.BOOLEAN);
    public LegSolverQuadruped legSolver = new LegSolverQuadruped(0.0F, 1.1F, 1.15F, 1.15F, 1);
    public static final Animation ANIMATION_SPEAK_1 = Animation.create(15);
    public static final Animation ANIMATION_SPEAK_2 = Animation.create(20);
    public static final Animation ANIMATION_MELEE_RAM = Animation.create(20);
    public static final Animation ANIMATION_MELEE_TAIL_1 = Animation.create(20);
    public static final Animation ANIMATION_MELEE_TAIL_2 = Animation.create(20);
    private Animation currentAnimation;
    private int animationTick;
    private float prevTailSwingRot;
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(GrottoceratopsServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG =
            SynchedEntityData.defineId(GrottoceratopsServant.class, EntityDataSerializers.BOOLEAN);
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;
    private boolean followingStanceEnforced = false;

    public GrottoceratopsServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GrottoceratopsServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GrottoceratopsServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GrottoceratopsServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GrottoceratopsServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GrottoceratopsServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.GrottoceratopsServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof GrottoceratopsServant servant) {
                if (ownerId.equals(servant.getOwnerId())) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALT_SKIN, 0);
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(TAIL_SWING_ROT, 0F);
        this.entityData.define(AUTO_MODE, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ServantBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(3, new ServantLayEggGoal<>(this, (DinosaurEggBlock) this.createEggBlockState().getBlock(), 100, 1.0D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GrottoceratopsServantMeleeAttackGoal(1.35D, true));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getDirectEntity() instanceof VallumraptorEntity) {
            f *= 0.75F;
        }
        return super.hurt(damageSource, f);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GROTTOCERATOPS_STEP.get(), 0.7F, 0.85F);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level().isClientSide && entityIn instanceof LivingEntity target) {
            if (this.getAnimation() == NO_ANIMATION) {
                if (this.getRandom().nextBoolean()) {
                    this.syncAnimation(ANIMATION_MELEE_RAM);
                } else {
                    this.syncAnimation(this.getRandom().nextBoolean() ? ANIMATION_MELEE_TAIL_1 : ANIMATION_MELEE_TAIL_2);
                }
            }
            float multiplier = (this.getAnimation() == ANIMATION_MELEE_TAIL_1 || this.getAnimation() == ANIMATION_MELEE_TAIL_2) ? 1.5F : 1.0F;
            this.playSound(ACSoundRegistry.GROTTOCERATOPS_ATTACK.get());
            target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier);
            target.knockback(0.8D + 0.5D * multiplier, this.getX() - target.getX(), this.getZ() - target.getZ());
        }
        return true;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public void tick() {
        super.tick();
        this.enforceFollowingStanceOnce();
        this.prevBuryEggsProgress = this.buryEggsProgress;
        if (this.buryingEggs && this.buryEggsProgress < 5.0F) {
            this.buryEggsProgress++;
        }
        if (!this.buryingEggs && this.buryEggsProgress > 0.0F) {
            this.buryEggsProgress--;
        }
        float tailSwing = getTailSwingRot();
        this.prevTailSwingRot = tailSwing;
        if (this.getAnimation() == ANIMATION_MELEE_TAIL_1 || this.getAnimation() == ANIMATION_MELEE_TAIL_2) {
            float start = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? 30 : -30;
            float end = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? -180 : 180;
            if (this.getAnimationTick() <= 7) {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, start, 5));
            } else {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, end, 25));
            }
            this.walkAnimation.setSpeed(1);
        } else if (Math.abs(tailSwing) > 0.0F) {
            this.setTailSwingRot(Mth.approachDegrees(tailSwing, 0, 20));
        }
        if (!this.level().isClientSide && ((this.getAnimation() == ANIMATION_SPEAK_1 && this.getAnimationTick() == 5) || (this.getAnimation() == ANIMATION_SPEAK_2 && this.getAnimationTick() == 2))) {
            actuallyPlayAmbientSound();
        }
        this.legSolver.update(this, this.yBodyRot + getTailSwingRot(), this.getScale());
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && this.getControllingPassenger() instanceof Player
                    && this.getAnimation() == NO_ANIMATION && !this.isStaying() && !this.isImmobile()
                    && this.hasLineOfSight(target) && this.distanceTo(target) < 4.5D) {
                this.doHurtTarget(target);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public float getTailSwingRot() {
        return entityData.get(TAIL_SWING_ROT);
    }

    public float getTailSwingRot(float f) {
        return prevTailSwingRot + (getTailSwingRot() - prevTailSwingRot) * f;
    }

    public void setTailSwingRot(float rot) {
        entityData.set(TAIL_SWING_ROT, rot);
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isNoAi()) {
            return null;
        }
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Mob mob) {
            if (MobsConfig.ServantRideAutonomous.get()) {
                return null;
            }
            return mob;
        }
        if (entity instanceof LivingEntity livingEntity && !this.isAutonomous()) {
            return livingEntity;
        }
        return null;
    }

    @Override
    public void positionRider(Entity rider, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(rider)) {
            double y = this.getY() + this.getPassengersRidingOffset() + rider.getMyRidingOffset() - this.getMaxLegSolverHeight();
            rider.setPos(this.getX(), y, this.getZ());
        }
    }

    private float getMaxLegSolverHeight() {
        float backLeftH = legSolver.backLeft.getHeight(1.0F);
        float backRightH = legSolver.backRight.getHeight(1.0F);
        float frontLeftH = legSolver.frontLeft.getHeight(1.0F);
        float frontRightH = legSolver.frontRight.getHeight(1.0F);
        return Math.max(backLeftH, Math.max(backRightH, Math.max(frontLeftH, frontRightH))) * 0.8F;
    }

    @Override
    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        return (double) (this.getBbHeight() * 0.9F) + 0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.getControllingPassenger() instanceof Player player && (player.zza != 0.0F || player.xxa != 0.0F);
        boolean notInBoat = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !steering && notInBoat);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !steering);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            float f = player.zza < 0.0F ? 0.5F : 1.0F;
            return new Vec3(player.xxa * 0.25F, 0.0D, player.zza * 0.5F * f);
        }
                LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && !this.isStaying() && !this.isImmobile()) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 1.5) {
                return new Vec3(dx / dist * 0.5, 0.0D, dz / dist * 0.5);
            }
        }
        this.setSprinting(false);
        return Vec3.ZERO;
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.5F);
            this.yBodyRot = this.yHeadRot = this.yRotO = this.getYRot();
            this.getNavigation().stop();
            this.setSprinting(true);
        }
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
    }

    @Override
    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            InteractionResult altSkinResult = this.tryChangeAltSkin(player, hand);
            if (altSkinResult != null) {
                return altSkinResult;
            }
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                if (this.isFood(itemstack)) {
                    return super.mobInteract(player, hand);
                }
                if (!player.isCrouching() && !this.isBaby()) {
                    Entity entity = this.getFirstPassenger();
                    if (entity != null && entity != player) {
                        entity.stopRiding();
                        return InteractionResult.SUCCESS;
                    }
                    if (!(itemstack.getItem() instanceof IWand)) {
                        this.doPlayerRide(player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.TREE_STAR.get().asItem());
    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon mob) {
        GrottoceratopsServant baby = AcEntityRegistry.GROTTOCERATOPS_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Override
    public BlockState createEggBlockState() {
        return AcBlockRegistry.GROTTOCERATOPS_SERVANT_EGG.get().defaultBlockState();
    }

    public BlockState createEggBeddingBlockState() {
        return ACBlockRegistry.FERN_THATCH.get().defaultBlockState();
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(DATA_HAS_EGG);
    }

    @Override
    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(DATA_HAS_EGG, hasEgg);
    }

    @Override
    public void onLayEggTick(BlockPos belowEgg, int time) {
        this.walkAnimation.update(0.5F, 0.4F);
        this.level().broadcastEntityEvent(this, (byte) 77);
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimalSummon partner) {
        this.setHasEgg(true);
        this.finalizeSpawnChildFromBreeding(level, partner, partner);
    }

    public int getAltSkin() {
        return this.entityData.get(ALT_SKIN);
    }

    public void setAltSkin(int altSkin) {
        this.entityData.set(ALT_SKIN, altSkin);
    }

    public int getAltSkinForItem(ItemStack stack) {
        if (stack.is(ACItemRegistry.AMBER_CURIOSITY.get())) {
            return 1;
        }
        if (stack.is(ACItemRegistry.TECTONIC_SHARD.get())) {
            return 2;
        }
        return 0;
    }

    @Nullable
    public InteractionResult tryChangeAltSkin(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int newSkin = this.getAltSkinForItem(itemstack);
        if (newSkin > 0 && this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(newSkin == 2
                    ? ACSoundRegistry.TECTONIC_SHARD_TRANSFORM.get()
                    : ACSoundRegistry.AMBER_MONOLITH_SUMMON.get());
            if (newSkin == this.getAltSkin()) {
                this.setAltSkin(0);
            } else {
                this.setAltSkin(newSkin);
            }
            this.level().broadcastEntityEvent(this, (byte) (newSkin == 2 ? 83 : 82));
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    private void enforceFollowingStanceOnce() {
        if (this.level().isClientSide || this.followingStanceEnforced) {
            return;
        }
        this.followingStanceEnforced = true;
        if (this.getTrueOwner() != null) {
            this.setFollowing();
        }
    }

    public float getBuryEggsProgress(float partialTicks) {
        return (this.prevBuryEggsProgress + (this.buryEggsProgress - this.prevBuryEggsProgress) * partialTicks) * 0.2F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AltSkin", this.getAltSkin());
        tag.putBoolean("HasEgg", this.hasEgg());
        tag.putBoolean("FollowingStanceEnforced", this.followingStanceEnforced);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAltSkin(tag.getInt("AltSkin"));
        this.setHasEgg(tag.getBoolean("HasEgg"));
        this.followingStanceEnforced = tag.getBoolean("FollowingStanceEnforced");
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 77) {
            this.buryingEggs = true;
            float radius = this.getBbWidth() * 0.55F;
            float particleCount = (5 + random.nextInt(5)) * radius;
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (float) (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY), Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground.below());
                if (groundState.isSolid() && level().isClientSide) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, ground.getY(), this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        } else if (b == 78) {
            this.buryingEggs = false;
        } else if (b == 82 || b == 83) {
            ParticleOptions particle = b == 82
                    ? ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER.get()
                    : ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC.get();
            for (int i = 0; i < 15; ++i) {
                if (this.level().random.nextInt(8) < 3) {
                    this.level().addParticle(particle,
                            this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0F),
                            this.random.nextGaussian() * 0.05, this.random.nextFloat() * 0.2, this.random.nextGaussian() * 0.05);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.getAnimation() != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPEAK_1, ANIMATION_SPEAK_2, ANIMATION_MELEE_RAM, ANIMATION_MELEE_TAIL_1, ANIMATION_MELEE_TAIL_2};
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.syncAnimation(random.nextBoolean() ? ANIMATION_SPEAK_2 : ANIMATION_SPEAK_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        float volume = this.getSoundVolume();
        if (this.getAnimation() == ANIMATION_SPEAK_2) {
            soundevent = ACSoundRegistry.GROTTOCERATOPS_CALL.get();
            volume += 1.0F;
        }
        if (soundevent != null) {
            this.playSound(soundevent, volume, this.getVoicePitch());
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GROTTOCERATOPS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GROTTOCERATOPS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GROTTOCERATOPS_DEATH.get();
    }

    private class GrottoceratopsServantMeleeAttackGoal extends MeleeAttackGoal {
        public GrottoceratopsServantMeleeAttackGoal(double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(GrottoceratopsServant.this, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        public boolean canUse() {
            if (GrottoceratopsServant.this.isBaby()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double dist) {
            double reach = this.getAttackReachSqr(target);
            if (dist <= reach && this.mob.getSensing().hasLineOfSight(target) && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(target);
            }
        }
    }
}
