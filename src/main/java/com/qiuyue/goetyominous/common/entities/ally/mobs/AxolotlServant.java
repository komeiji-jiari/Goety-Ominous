package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AxolotlServant extends AnimalSummon implements LerpingModel{
    public static final int TOTAL_PLAYDEAD_TIME = 200;
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(AxolotlServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(AxolotlServant.class, EntityDataSerializers.BOOLEAN);

    private int playDeadTimer = 0;
    private final Map<String, Vector3f> modelRotationValues = new HashMap<>();
    private int explosionCountdown = -1;
    private boolean shouldExplode = false;

    public AxolotlServant(EntityType<? extends Owned> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new AxolotlMoveControl(this);
        this.lookControl = new AxolotlLookControl(this);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel pLevel, AnimalSummon pOtherParent) {
        if (!(pOtherParent instanceof AxolotlServant otherAxolotl)) {
            return null;
        }

        AxolotlServant offspring = new AxolotlServant(ModEntityTypes.AXOLOTL_SERVANT.get(), pLevel);

        if (offspring != null) {
            Variant variant;
            if (this.random.nextBoolean()) {
                variant = this.getVariant();
            } else {
                variant = otherAxolotl.getVariant();
            }
            offspring.setVariant(variant);

            if (this.getTrueOwner() != null) {
                offspring.setTrueOwner(this.getTrueOwner());
            } else if (pOtherParent.getTrueOwner() != null) {
                offspring.setTrueOwner(pOtherParent.getTrueOwner());
            }

            offspring.setPersistenceRequired();
        }
        return offspring;
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(2, new AxolotlFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_PLAYING_DEAD, false);
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    public void setPlayingDead(boolean pPlayingDead) {
        this.entityData.set(DATA_PLAYING_DEAD, pPlayingDead);
        if (pPlayingDead) {
            this.playDeadTimer = TOTAL_PLAYDEAD_TIME;
        } else {
            this.playDeadTimer = 0;
        }
    }

    public boolean isPlayingDead() {
        return this.entityData.get(DATA_PLAYING_DEAD);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
    }

    @Override
    public Map<String, Vector3f> getModelRotationValues() {
        return this.modelRotationValues;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant().getId());
        pCompound.putBoolean("PlayingDead", this.isPlayingDead());
        pCompound.putInt("PlayDeadTimer", this.playDeadTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(Variant.byId(pCompound.getInt("Variant")));
        this.setPlayingDead(pCompound.getBoolean("PlayingDead"));
        this.playDeadTimer = pCompound.getInt("PlayDeadTimer");
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            ServerParticleUtil.addParticlesAroundMiddleSelf(pLevel.getLevel(), ParticleTypes.LARGE_SMOKE, this);
        }
        if (pReason == MobSpawnType.MOB_SUMMONED) {
            this.setVariant(Variant.LUCY);
        }
        return pSpawnData;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    protected SoundEvent getAmbientSound() {
        return !this.isPlayingDead() ? (this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR) : null;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.AXOLOTL_SWIM;
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            String customName = this.getCustomName() != null ? this.getCustomName().getString() : "";

            if ("!?skillupper?!".equals(customName)) {
                if (this.getVariant() != Variant.BLUE) {
                    this.setVariant(Variant.BLUE);
                }
            } else if ("skillupper".equalsIgnoreCase(customName)) {
                if (!this.shouldExplode) {
                    this.shouldExplode = true;
                    this.explosionCountdown = 60;
                }
                if (this.shouldExplode && this.explosionCountdown > 0) {
                    this.explosionCountdown--;
                    if (!this.level().isClientSide) {
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 5; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                                        this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                        1, d0, d1, d2, 0.5F);
                            }
                        }
                    }
                    if (this.explosionCountdown <= 0) {
                        if (!this.level().isClientSide) {
                            float explosionPower = 6.0F;
                            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, Level.ExplosionInteraction.MOB);

                            if (this.getTrueOwner() instanceof Player owner) {
                                float damageAmount = explosionPower * 2.0F;
                                owner.hurt(this.damageSources().mobAttack(this), damageAmount);
                            }

                            this.discard();
                        }
                        return;
                    }
                }
            } else {
                this.shouldExplode = false;
                this.explosionCountdown = -1;
            }

            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            }

            if (!this.isNoAi() && this.isPlayingDead()) {
                if (this.playDeadTimer > 0) {
                    this.playDeadTimer--;
                } else {
                    this.setPlayingDead(false);
                }
            }

            if (!this.isInWater()) {
                this.setYRot(this.yRotO);
            }
        }
        super.aiStep();
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }


    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.is(net.minecraft.tags.ItemTags.AXOLOTL_TEMPT_ITEMS)) {
                if (this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodproperties = itemstack.getFoodProperties(this);
                    if (foodproperties != null) {
                        this.heal((float)foodproperties.getNutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverlevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverlevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(),
                                        this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                        0, d0, d1, d2, 0.5F);
                            }
                        }
                        pPlayer.swing(pHand);
                        return InteractionResult.SUCCESS;
                    }
                } else if (!this.isBaby() && this.getAge() >= 0 && this.canFallInLove()) {
                    this.usePlayerItem(pPlayer, pHand, itemstack);
                    this.setInLove(pPlayer);
                    pPlayer.swing(pHand);
                    return InteractionResult.SUCCESS;
                } else if (this.isBaby()) {
                    this.usePlayerItem(pPlayer, pHand, itemstack);
                    this.ageUp(getSpeedUpSecondsWhenFeeding(-this.getAge()), true);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this),
                (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.doEnchantDamageEffects(this, pEntity);
            this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);

            if (pEntity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) {
                this.onStopAttacking(livingEntity);
            }
        }
        return flag;
    }

    @Override
    public boolean hurt(DamageSource pDamageSource, float pAmount) {
        float health = this.getHealth();
        if (!this.level().isClientSide && !this.isNoAi() && this.level().random.nextInt(3) == 0
                && ((float)this.level().random.nextInt(3) < pAmount || health / this.getMaxHealth() < 0.5F)
                && pAmount < health && this.isInWater() && !this.isPlayingDead()) {
            this.setPlayingDead(true);
        }
        return super.hurt(pDamageSource, pAmount);
    }

    public void onStopAttacking(LivingEntity pTarget) {
        if (pTarget.isDeadOrDying()) {
            DamageSource damageSource = pTarget.getLastDamageSource();
            if (damageSource != null) {
                Entity entity = damageSource.getEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    Player player = (Player) entity;
                    List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(20.0));
                    if (players.contains(player)) {
                        this.applySupportingEffects(player);
                    }
                }
            }
        }
    }

    public void applySupportingEffects(Player pPlayer) {
        MobEffectInstance effect = pPlayer.getEffect(MobEffects.REGENERATION);
        if (effect == null || effect.endsWithin(2399)) {
            int duration = effect != null ? effect.getDuration() : 0;
            int newDuration = Math.min(2400, 100 + duration);
            pPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, newDuration, 0), this);
        }
        pPlayer.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    public boolean canBeSeenAsEnemy() {
        return !this.isPlayingDead() && super.canBeSeenAsEnemy();
    }

    static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
        private final AxolotlServant axolotl;

        public AxolotlMoveControl(AxolotlServant pAxolotl) {
            super(pAxolotl, 85, 10, 0.1F, 0.5F, false);
            this.axolotl = pAxolotl;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead() && this.axolotl.isInWater()) {
                super.tick();
            }
        }
    }

    static class AxolotlLookControl extends SmoothSwimmingLookControl {
        private final AxolotlServant axolotl;

        public AxolotlLookControl(AxolotlServant pAxolotl) {
            super(pAxolotl, 20);
            this.axolotl = pAxolotl;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead()) {
                super.tick();
            }
        }
    }

    static class AxolotlFollowOwnerGoal extends Goal {
        private final AxolotlServant axolotl;
        private LivingEntity owner;
        private final Level level;
        private final double speed;
        private final PathNavigation navigation;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public AxolotlFollowOwnerGoal(AxolotlServant pAxolotl, double pSpeed, float pStartDistance, float pStopDistance) {
            this.axolotl = pAxolotl;
            this.level = pAxolotl.level();
            this.speed = pSpeed;
            this.navigation = pAxolotl.getNavigation();
            this.startDistance = pStartDistance;
            this.stopDistance = pStopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.axolotl.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.axolotl.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else if (!this.axolotl.isFollowing() || this.axolotl.isCommanded()) {
                return false;
            } else if (this.axolotl.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.axolotl.getTarget() != null) {
                return false;
            } else {
                return !(this.axolotl.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.axolotl.getPathfindingMalus(BlockPathTypes.WATER);
            this.axolotl.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.axolotl.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            if (this.owner != null) {
                this.axolotl.getLookControl().setLookAt(this.owner, 10.0F, (float)this.axolotl.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (!this.axolotl.isLeashed() && !this.axolotl.isPassenger()) {
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean flag = this.axolotl.distanceToSqr(this.owner) >= Mth.square(range);
                        if (this.owner instanceof Mob) {
                            flag |= !this.axolotl.hasLineOfSight(this.owner) && this.axolotl.distanceToSqr(this.owner) >= Mth.square(8.0D);
                        }
                        if (flag) {
                            this.teleportToOwner();
                        } else {
                            this.navigation.moveTo(this.owner, this.speed);
                        }
                    }
                }
            }
        }

        protected void teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }
        }

        protected boolean tryTeleportToLocation(int pX, int pY, int pZ) {
            if (Math.abs((double)pX - this.owner.getX()) < 2.0D && Math.abs((double)pZ - this.owner.getZ()) < 2.0D) {
                return false;
            } else {
                BlockPos pos = new BlockPos(pX, pY, pZ);
                BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
                if (pathnodetype != BlockPathTypes.WALKABLE && pathnodetype != BlockPathTypes.WATER) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.axolotl.blockPosition());
                    return this.level.noCollision(this.axolotl, this.axolotl.getBoundingBox().move(blockpos));
                }
            }
        }

        protected int getRandomNumber(int pMin, int pMax) {
            return this.axolotl.getRandom().nextInt(pMax - pMin + 1) + pMin;
        }
    }

    public enum Variant implements StringRepresentable {
        LUCY(0, "lucy"),
        WILD(1, "wild"),
        GOLD(2, "gold"),
        CYAN(3, "cyan"),
        BLUE(4, "blue");

        private final int id;
        private final String name;

        Variant(int pId, String pName) {
            this.id = pId;
            this.name = pName;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int pId) {
            Variant[] variants = values();
            return pId >= 0 && pId < variants.length ? variants[pId] : LUCY;
        }
    }
}