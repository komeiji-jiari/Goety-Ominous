package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantBreedGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantLayEggGoal;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class VallumraptorServant extends AnimalSummon implements LaysEggs, IAnimatedEntity {

    public static final Animation ANIMATION_CALL_1 = Animation.create(15);
    public static final Animation ANIMATION_CALL_2 = Animation.create(25);
    public static final Animation ANIMATION_SCRATCH_1 = Animation.create(20);
    public static final Animation ANIMATION_SCRATCH_2 = Animation.create(20);
    public static final Animation ANIMATION_SHAKE = Animation.create(40);
    public static final Animation ANIMATION_STARTLEAP = Animation.create(20);
    public static final Animation ANIMATION_MELEE_BITE = Animation.create(15);
    public static final Animation ANIMATION_MELEE_SLASH_1 = Animation.create(15);
    public static final Animation ANIMATION_MELEE_SLASH_2 = Animation.create(15);
    public static final Animation ANIMATION_GRAB = Animation.create(40);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PUZZLED_HEAD_ROT = SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ELDER = SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HIDING_FOR = SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG =
            SynchedEntityData.defineId(VallumraptorServant.class, EntityDataSerializers.BOOLEAN);
    private Animation currentAnimation;
    private int animationTick;
    private float leapProgress;
    private float prevLeapProgress;
    private float runProgress;
    private float prevRunProgress;
    private float prevPuzzleHeadRot;
    private float prevSitProgress;
    private float sitProgress;
    private float hideProgress;
    private float prevHideProgress;
    private float tailYaw;
    private float prevTailYaw;
    private float targetPuzzleRot;
    private int fleeTicks = 0;
    private Vec3 fleeFromPosition;
    private boolean hasRunningAttributes = false;
    private boolean hasElderAttributes = false;
    private boolean leapImpulseApplied = false;
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;
    private boolean followingStanceEnforced = false;

    public VallumraptorServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(0.6F);
        this.tailYaw = this.getYRot();
        this.prevTailYaw = this.getYRot();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.VallumraptorServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.VallumraptorServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VallumraptorServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.VallumraptorServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.VallumraptorServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.VallumraptorServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof VallumraptorServant servant) {
                if (ownerId.equals(servant.getOwnerId())) {
                    count++;
                }
            }
        }
        return count;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.VallumraptorServantLimit.get()) {
                return null;
            }
        }
        this.setElder(this.getRandom().nextInt(100) < MobsConfig.VallumraptorElderChance.get());
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    public boolean isElder() {
        return this.entityData.get(ELDER);
    }

    public void setElder(boolean bool) {
        this.entityData.set(ELDER, bool);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof VallumraptorServant servant) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
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
        this.entityData.define(RUNNING, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(PUZZLED_HEAD_ROT, 0.0F);
        this.entityData.define(ELDER, false);
        this.entityData.define(HIDING_FOR, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ServantBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(3, new ServantLayEggGoal<>(this, (DinosaurEggBlock) this.createEggBlockState().getBlock(), 100, 1.0D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new FleeGoal());
        this.goalSelector.addGoal(1, new VallumraptorServantMeleeAttackGoal());
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
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
        prevRunProgress = runProgress;
        prevLeapProgress = leapProgress;
        prevTailYaw = tailYaw;
        prevSitProgress = sitProgress;
        prevHideProgress = hideProgress;
        float headPuzzleRot = getPuzzledHeadRot();
        if (isRunning() && runProgress < 5.0F) {
            runProgress++;
        }
        if (!isRunning() && runProgress > 0.0F) {
            runProgress--;
        }
        if (isLeaping() && leapProgress < 5.0F) {
            leapProgress++;
        }
        if (!isLeaping() && leapProgress > 0.0F) {
            leapProgress--;
        }
        if (isStaying() && sitProgress < 10.0F) {
            sitProgress++;
        }
        if (!isStaying() && sitProgress > 0.0F) {
            sitProgress--;
        }
        if (getHideFor() > 0 && hideProgress < 20.0F) {
            hideProgress++;
        }
        if (getHideFor() <= 0 && hideProgress > 0.0F) {
            hideProgress--;
        }
        
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(getRunningSpeed());
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.VallumraptorServantMovementSpeed.get());
        }
        if (isElder() && !hasElderAttributes) {
            hasElderAttributes = true;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.VallumraptorServantElderHealth.get());
            this.getAttribute(Attributes.ARMOR).setBaseValue(AttributesConfig.VallumraptorServantElderArmor.get());
            this.heal(36.0F);
        }
        if (!isElder() && hasElderAttributes) {
            hasElderAttributes = false;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.VallumraptorServantHealth.get());
            this.getAttribute(Attributes.ARMOR).setBaseValue(AttributesConfig.VallumraptorServantArmor.get());
            this.heal(28.0F);
        }
        if (this.tickCount % (this.getHideFor() > 0 ? 15 : 100) == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (!level().isClientSide) {
            this.puzzledTick(headPuzzleRot);
            if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION && !this.isStaying()) {
                if (random.nextInt(200) == 0) {
                    Animation idle;
                    float rand = random.nextFloat();
                    if (rand < 0.45F) {
                        idle = ANIMATION_SCRATCH_1;
                    } else if (rand < 0.9F) {
                        idle = ANIMATION_SCRATCH_2;
                    } else {
                        idle = ANIMATION_SHAKE;
                    }
                    this.syncAnimation(idle);
                }
            }
            if (this.getAnimation() == ANIMATION_STARTLEAP && this.getAnimationTick() == 8 && this.isLeaping() && !this.leapImpulseApplied) {
                this.leapImpulseApplied = true;
                LivingEntity target = this.getTarget();
                if (target != null) {
                    Vec3 dir = new Vec3(target.getX() - this.getX(), 0, target.getZ() - this.getZ());
                    double len = dir.lengthSqr();
                    if (len > 0.01D) {
                        dir = dir.normalize();
                        this.setDeltaMovement(dir.x * 1.35D, 0.45D, dir.z * 1.35D);
                    }
                }
            }
            if (this.isLeaping() && this.getAnimation() != ANIMATION_STARTLEAP) {
                this.setLeaping(false);
                this.leapImpulseApplied = false;
            }
            if (fleeTicks > 0) {
                fleeTicks--;
            }
            if (getHideFor() > 0) {
                this.setHideFor(this.getHideFor() - 1);
            }
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && !(target instanceof Player player && player.isCreative())) {
                if (this.getHealth() < this.getMaxHealth() * 0.45F && this.getHideFor() <= 0) {
                    int i = 80 + this.random.nextInt(40);
                    this.setHideFor(i);
                    this.fleeFromPosition = target.position();
                    this.fleeTicks = i;
                    if (target instanceof Mob mob) {
                        mob.setTarget(null);
                        mob.setLastHurtByMob(null);
                        mob.setLastHurtMob(null);
                    }
                }
            }
        }
        if (this.isLeaping()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.1F, 1, 1.1F));
        }
        if (this.getAnimation() == ANIMATION_CALL_1 && this.getAnimationTick() == 5 || this.getAnimation() == ANIMATION_CALL_2 && this.getAnimationTick() == 4) {
            this.actuallyPlayAmbientSound();
        }
        if (this.getAnimation() == ANIMATION_GRAB && this.getAnimationTick() == 5) {
            this.playSound(ACSoundRegistry.VALLUMRAPTOR_IDLE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        tailYaw = Mth.approachDegrees(this.tailYaw, this.yBodyRot, 8);
        prevPuzzleHeadRot = headPuzzleRot;
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public double getRunningSpeed() {
        return AttributesConfig.VallumraptorServantMovementSpeed.get() * 1.75D;
    }

    private void puzzledTick(float current) {
        float dist = Math.abs(targetPuzzleRot - current);
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() || this.getAnimation() != NO_ANIMATION) {
            targetPuzzleRot = 0;
        } else if (this.random.nextInt(10) == 0 && dist <= 0.1F) {
            if (random.nextFloat() < 0.25F) {
                targetPuzzleRot = 0;
            } else {
                float invSignum = random.nextFloat() < 0.1F ? Math.signum(random.nextFloat() - 0.5F) : -Math.signum(targetPuzzleRot);
                targetPuzzleRot = random.nextFloat() * 50 * invSignum;
            }
        }
        if (current < this.targetPuzzleRot && dist > 0.1F) {
            this.setPuzzledHeadRot(current + Math.min(dist, 6));
        }
        if (current > this.targetPuzzleRot && dist > 0.1F) {
            this.setPuzzledHeadRot(current - Math.min(dist, 6));
        }
    }

    public float getTailYaw(float partialTick) {
        return prevTailYaw + (tailYaw - prevTailYaw) * partialTick;
    }

    public float getPuzzledHeadRot(float partialTick) {
        return prevPuzzleHeadRot + (getPuzzledHeadRot() - prevPuzzleHeadRot) * partialTick;
    }

    private float getPuzzledHeadRot() {
        return entityData.get(PUZZLED_HEAD_ROT);
    }

    public void setPuzzledHeadRot(float rot) {
        entityData.set(PUZZLED_HEAD_ROT, rot);
    }

    public float getLeapProgress(float partialTick) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTick) * 0.2F;
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public float getSitProgress(float partialTick) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTick) / 10.0F;
    }

    public float getHideProgress(float partialTick) {
        return (prevHideProgress + (hideProgress - prevHideProgress) * partialTick) * 0.05F;
    }

    
    public int getHideFor() {
        return this.entityData.get(HIDING_FOR);
    }

    public void setHideFor(int ticks) {
        this.entityData.set(HIDING_FOR, ticks);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean bool) {
        this.entityData.set(LEAPING, bool);
    }

    @Override
    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
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
        return new Animation[]{ANIMATION_CALL_1, ANIMATION_CALL_2, ANIMATION_SCRATCH_1, ANIMATION_SCRATCH_2, ANIMATION_SHAKE, ANIMATION_STARTLEAP, ANIMATION_MELEE_BITE, ANIMATION_MELEE_SLASH_1, ANIMATION_MELEE_SLASH_2, ANIMATION_GRAB};
    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon mob) {
        VallumraptorServant baby = AcEntityRegistry.VALLUMRAPTOR_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Override
    public BlockState createEggBlockState() {
        return AcBlockRegistry.VALLUMRAPTOR_SERVANT_EGG.get().defaultBlockState();
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
        tag.putBoolean("Elder", this.isElder());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAltSkin(tag.getInt("AltSkin"));
        this.setHasEgg(tag.getBoolean("HasEgg"));
        this.followingStanceEnforced = tag.getBoolean("FollowingStanceEnforced");
        this.setElder(tag.getBoolean("Elder"));
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
    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_GRAB) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float factor = this.isRunning() ? 10.0F : 6.0F;
        float f2 = Math.min(f1 * factor, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.syncAnimation(this.getRandom().nextBoolean() && !this.isStaying() ? ANIMATION_CALL_2 : ANIMATION_CALL_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        float volume = this.getSoundVolume();
        SoundEvent soundevent = this.getAmbientSound();
        if (this.getAnimation() == ANIMATION_CALL_2) {
            soundevent = ACSoundRegistry.VALLUMRAPTOR_CALL.get();
            volume += 1.0F;
        }
        if (soundevent != null) {
            this.playSound(soundevent, volume, this.getVoicePitch());
        }
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
                if (itemstack.getItem().isEdible() && itemstack.getFoodProperties(this).isMeat() && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = itemstack.getFoodProperties(this);
                    if (foodProperties != null) {
                        this.heal(4.0F);
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                        this.gameEvent(GameEvent.EAT, this);
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 8; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02;
                                double d1 = this.random.nextGaussian() * 0.02 + 0.1;
                                double d2 = this.random.nextGaussian() * 0.02;
                                serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F + this.random.nextDouble() * 0.5F, this.getRandomZ(1.0F), 0, d0, d1, d2, 0.5);
                            }
                        }
                        player.swing(hand);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ACItemRegistry.DINOSAUR_NUGGET.get());
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.VALLUMRAPTOR_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.VALLUMRAPTOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.VALLUMRAPTOR_DEATH.get();
    }

    private class FleeGoal extends Goal {

        private FleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return VallumraptorServant.this.fleeTicks > 0 && VallumraptorServant.this.fleeFromPosition != null;
        }

        @Override
        public void stop() {
            VallumraptorServant.this.fleeFromPosition = null;
            VallumraptorServant.this.setRunning(false);
        }

        @Override
        public void tick() {
            VallumraptorServant.this.setRunning(true);
            if (VallumraptorServant.this.getNavigation().isDone()) {
                int dist = VallumraptorServant.this.getHideFor() > 0 ? 4 : 8;
                Vec3 vec3 = LandRandomPos.getPosAway(VallumraptorServant.this, dist, dist, VallumraptorServant.this.fleeFromPosition);
                if (vec3 != null) {
                    VallumraptorServant.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0F);
                }
            }
        }
    }

    private class VallumraptorServantMeleeAttackGoal extends Goal {
        private int leapCooldown = 0;
        private boolean animationHitDone = false;

        private VallumraptorServantMeleeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = VallumraptorServant.this.getTarget();
            return target != null && target.isAlive() && !VallumraptorServant.this.isBaby();
        }

        @Override
        public void start() {
            VallumraptorServant.this.setRunning(true);
        }

        @Override
        public void stop() {
            VallumraptorServant.this.setRunning(false);
            VallumraptorServant.this.setLeaping(false);
            VallumraptorServant.this.leapImpulseApplied = false;
        }

        @Override
        public void tick() {
            if (leapCooldown > 0) {
                leapCooldown--;
            }
            LivingEntity target = VallumraptorServant.this.getTarget();
            if (target == null) {
                return;
            }
            Animation anim = VallumraptorServant.this.getAnimation();
            if (anim != ANIMATION_MELEE_BITE && anim != ANIMATION_MELEE_SLASH_1 && anim != ANIMATION_MELEE_SLASH_2) {
                animationHitDone = false;
            }
            VallumraptorServant.this.getNavigation().moveTo(target, 1.0F);
            double dist = VallumraptorServant.this.distanceTo(target);
            float reach = VallumraptorServant.this.getBbWidth() + target.getBbWidth() + 1.0F;
            if (dist < reach) {
                if (VallumraptorServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION && VallumraptorServant.this.hasLineOfSight(target)) {
                    if (VallumraptorServant.this.getRandom().nextInt(3) == 0) {
                        VallumraptorServant.this.syncAnimation(ANIMATION_MELEE_BITE);
                    } else {
                        VallumraptorServant.this.syncAnimation(VallumraptorServant.this.getRandom().nextBoolean() ? ANIMATION_MELEE_SLASH_1 : ANIMATION_MELEE_SLASH_2);
                    }
                }
                int animTick = VallumraptorServant.this.getAnimationTick();
                if ((anim == ANIMATION_MELEE_BITE || anim == ANIMATION_MELEE_SLASH_1 || anim == ANIMATION_MELEE_SLASH_2)
                        && animTick >= 6 && animTick <= 8) {
                    checkAndDealDamage(target);
                }
            } else if (dist < 10.0F && VallumraptorServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                    && leapCooldown <= 0 && VallumraptorServant.this.hasLineOfSight(target)) {
                VallumraptorServant.this.syncAnimation(ANIMATION_STARTLEAP);
                VallumraptorServant.this.setLeaping(true);
                VallumraptorServant.this.leapImpulseApplied = false;
                leapCooldown = 60;
            }
        }

        private void checkAndDealDamage(LivingEntity target) {
            if (!animationHitDone && VallumraptorServant.this.hasLineOfSight(target)
                    && VallumraptorServant.this.distanceTo(target) < VallumraptorServant.this.getBbWidth() + target.getBbWidth() + 2.0F) {
                animationHitDone = true;
                VallumraptorServant.this.playSound(ACSoundRegistry.VALLUMRAPTOR_HURT.get());
                target.hurt(target.damageSources().mobAttack(VallumraptorServant.this), (float) VallumraptorServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                target.knockback(0.4F, VallumraptorServant.this.getX() - target.getX(), VallumraptorServant.this.getZ() - target.getZ());
            }
        }
    }
}
