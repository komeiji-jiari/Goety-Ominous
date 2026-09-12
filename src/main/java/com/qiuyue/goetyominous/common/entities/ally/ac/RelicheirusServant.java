package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.block.PewenBranchBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
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
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantTemptGoal;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RelicheirusServant extends AnimalSummon implements LaysEggs, IAnimatedEntity {

    public LegSolverQuadruped legSolver = new LegSolverQuadruped(-0.15F, 0.6F, 0.5F, 0.75F, 1);
    public static final Animation ANIMATION_SPEAK_1 = Animation.create(13);
    public static final Animation ANIMATION_SPEAK_2 = Animation.create(20);
    public static final Animation ANIMATION_EAT_TREE = Animation.create(40);
    public static final Animation ANIMATION_EAT_TRILOCARIS = Animation.create(50);
    public static final Animation ANIMATION_PUSH_TREE = Animation.create(60);
    public static final Animation ANIMATION_SCRATCH_1 = Animation.create(60);
    public static final Animation ANIMATION_SCRATCH_2 = Animation.create(40);
    public static final Animation ANIMATION_SHAKE = Animation.create(30);
    public static final Animation ANIMATION_MELEE_SLASH_1 = Animation.create(20);
    public static final Animation ANIMATION_MELEE_SLASH_2 = Animation.create(20);
    private static final EntityDataAccessor<Integer> PECK_Y =
            SynchedEntityData.defineId(RelicheirusServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID =
            SynchedEntityData.defineId(RelicheirusServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(RelicheirusServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG =
            SynchedEntityData.defineId(RelicheirusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PUSHING_TREES_FOR =
            SynchedEntityData.defineId(RelicheirusServant.class, EntityDataSerializers.INT);
    public static final int PUSHING_TREES_TICKS = 1200;
    private Animation currentAnimation;
    private int animationTick;
    private float prevRaiseArmsAmount = 0;
    private float raiseArmsAmount = 0;
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;
    private boolean followingStanceEnforced = false;

    public RelicheirusServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RelicheirusServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.RelicheirusServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RelicheirusServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.RelicheirusServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.RelicheirusServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.RelicheirusServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof RelicheirusServant servant) {
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
            if (countServants(player) >= MobsConfig.RelicheirusServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof RelicheirusServant servant) {
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
        this.entityData.define(PECK_Y, 0);
        this.entityData.define(HELD_MOB_ID, -1);
        this.entityData.define(ALT_SKIN, 0);
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(PUSHING_TREES_FOR, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RelicheirusServantMeleeGoal(this));
        this.goalSelector.addGoal(2, new ServantBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(3, new ServantLayEggGoal<>(this, (DinosaurEggBlock) this.createEggBlockState().getBlock(), 100, 1.0D));
        this.goalSelector.addGoal(4, new ServantTemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.TREE_STAR.get()), false));
        this.goalSelector.addGoal(5, new RelicheirusServantPushTreesGoal(this, 25));
        this.goalSelector.addGoal(6, new RelicheirusServantNibblePewensGoal(this, 20));
        this.goalSelector.addGoal(7, new Summoned.WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new RelicheirusServantPreyTargetGoal(this));
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
        if (this.getAnimation() != ANIMATION_EAT_TREE) {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        }
        this.prevRaiseArmsAmount = raiseArmsAmount;
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (shouldRaiseArms() && raiseArmsAmount < 5F) {
            raiseArmsAmount++;
        }
        if (!shouldRaiseArms() && raiseArmsAmount > 0F) {
            raiseArmsAmount--;
        }
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (!level().isClientSide) {
            if (this.isStillEnough() && random.nextInt(200) == 0 && this.getAnimation() == NO_ANIMATION && !this.isStaying()) {
                Animation idle;
                float rand = random.nextFloat();
                if (rand < 0.15F) {
                    idle = ANIMATION_SCRATCH_1;
                } else if (rand < 0.3F) {
                    idle = ANIMATION_SCRATCH_2;
                } else {
                    idle = ANIMATION_SHAKE;
                }
                this.syncAnimation(idle);
            }
            boolean held = false;
            LivingEntity target = this.getTarget();
            if (target != null && target.distanceTo(this) < 10 && target instanceof TrilocarisEntity) {
                if (this.getAnimation() == ANIMATION_EAT_TRILOCARIS) {
                    if (this.getAnimationTick() < 20) {
                        held = true;
                        this.setHeldMobId(target.getId());
                    } else if (this.getAnimationTick() <= 50) {
                        Vec3 trilocarisPos = getTrilocarisPos();
                        target.setPos(trilocarisPos);
                        if (this.getAnimationTick() >= 45 && target.isAlive()) {
                            target.hurt(damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        }
                        held = true;
                        target.fallDistance = 0;
                    }
                }
            }
            if (!held && getHeldMobId() != -1) {
                this.setHeldMobId(-1);
            }
            if (this.getPushingTreesFor() > 0) {
                this.setPushingTreesFor(this.getPushingTreesFor() - 1);
            }
        }
        if (this.getAnimation() == ANIMATION_SPEAK_1 && this.getAnimationTick() == 1 || this.getAnimation() == ANIMATION_SPEAK_2 && this.getAnimationTick() == 1) {
            actuallyPlayAmbientSound();
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
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

    private boolean isStillEnough() {
        return this.getDeltaMovement().horizontalDistance() < 0.05;
    }

    public boolean shouldRaiseArms() {
        return this.getAnimation() == ANIMATION_EAT_TREE || this.getAnimation() == ANIMATION_PUSH_TREE
                || this.getAnimation() == ANIMATION_SCRATCH_1 || this.getAnimation() == ANIMATION_SCRATCH_2
                || this.getAnimation() == ANIMATION_MELEE_SLASH_1 || this.getAnimation() == ANIMATION_MELEE_SLASH_2;
    }

    public float getRaiseArmsAmount(float partialTick) {
        return (prevRaiseArmsAmount + (raiseArmsAmount - prevRaiseArmsAmount) * partialTick) * 0.2F;
    }

    public float getDanceProgress(float partialTicks) {
        return 0.0F;
    }

    public float getBuryEggsProgress(float partialTicks) {
        return (this.prevBuryEggsProgress + (this.buryEggsProgress - this.prevBuryEggsProgress) * partialTicks) * 0.2F;
    }

    public int getHeadRotSpeed() {
        return 5;
    }

    public float getScale() {
        return this.isBaby() ? 0.25F : 1.0F;
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile();
    }

    public void setPeckY(int y) {
        this.entityData.set(PECK_Y, y);
    }

    public void setPushingTreesFor(int time) {
        this.entityData.set(PUSHING_TREES_FOR, time);
    }

    public int getPushingTreesFor() {
        return this.entityData.get(PUSHING_TREES_FOR);
    }

    public int getPeckY() {
        return this.entityData.get(PECK_Y);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
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

    private Vec3 getTrilocarisPos() {
        Vec3 triloUp = new Vec3(0, 0F, 1.5F);
        if (this.getAnimation() == ANIMATION_EAT_TRILOCARIS && getAnimationTick() >= 15F) {
            float anim1 = Math.min(getAnimationTick() - 15F, 15F) / 15F;
            float anim2 = Math.min(getAnimationTick(), 15F) / 15F;
            triloUp = triloUp.add(0, (this.getEyeHeight() + 1F) * anim1, anim2 * -1F + 1F);
        }
        Vec3 head = triloUp.xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.position().add(head);
    }

    public BlockPos getStandAtTreePos(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        return target.relative(dir.getOpposite(), 4).atY((int) this.getY());
    }

    public boolean lockTreePosition(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        float targetRot = Mth.approachDegrees(this.getYRot(), dir.toYRot(), 20);
        this.setYRot(targetRot);
        this.setYHeadRot(targetRot);
        this.yBodyRot = targetRot;
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        Vec3 vec31 = Vec3.atCenterOf(target.relative(dir.getOpposite(), 2));
        Vec3 vec32 = vec31.subtract(this.position());
        if (vec32.length() > 1) {
            vec32 = vec32.normalize();
        }
        Vec3 delta = new Vec3(vec32.x * 0.1F, 0F, vec32.z * 0.1F);
        this.setDeltaMovement(this.getDeltaMovement().add(delta));
        return this.distanceToSqr(vec31.x, this.getY(), vec31.z) < 4.0D && Mth.degreesDifferenceAbs(this.getYRot(), dir.toYRot()) < 7;
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_EAT_TRILOCARIS) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public void push(double x, double y, double z) {
        if (this.getAnimation() != ANIMATION_EAT_TRILOCARIS) {
            super.push(x, y, z);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.RELICHEIRUS_STEP.get(), 1.0F, 1.0F);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.99F * dimensions.height;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            InteractionResult altSkinResult = this.tryChangeAltSkin(player, hand);
            if (altSkinResult != null) {
                return altSkinResult;
            }
        }
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (this.isFood(itemstack)) {
                return super.mobInteract(player, hand);
            }
            if (itemstack.is(ACItemRegistry.PRIMORDIAL_SOUP.get())) {
                if (!this.level().isClientSide) {
                    this.heal(20.0F);
                    this.setPushingTreesFor(PUSHING_TREES_TICKS);
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                    this.gameEvent(GameEvent.EAT, this);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 8; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02;
                            double d1 = this.random.nextGaussian() * 0.02 + 0.1;
                            double d2 = this.random.nextGaussian() * 0.02;
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getRandomX(1.0F),
                                    this.getY() + this.getBbHeight() * this.getScale() + 0.3F + this.random.nextDouble() * 0.5F,
                                    this.getRandomZ(1.0F), 0, d0, d1, d2, 0.5);
                        }
                    }
                    if (!itemstack.getCraftingRemainingItem().isEmpty()) {
                        this.spawnAtLocation(itemstack.getCraftingRemainingItem().copy());
                    }
                    this.usePlayerItem(player, hand, itemstack);
                    player.swing(hand);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
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
        RelicheirusServant baby = AcEntityRegistry.RELICHEIRUS_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Override
    public BlockState createEggBlockState() {
        return AcBlockRegistry.RELICHEIRUS_SERVANT_EGG.get().defaultBlockState();
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
        return new Animation[]{ANIMATION_SPEAK_1, ANIMATION_SPEAK_2, ANIMATION_EAT_TREE, ANIMATION_EAT_TRILOCARIS,
                ANIMATION_PUSH_TREE, ANIMATION_SCRATCH_1, ANIMATION_SCRATCH_2, ANIMATION_SHAKE,
                ANIMATION_MELEE_SLASH_1, ANIMATION_MELEE_SLASH_2};
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.syncAnimation(random.nextBoolean() ? ANIMATION_SPEAK_2 : ANIMATION_SPEAK_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.RELICHEIRUS_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RELICHEIRUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RELICHEIRUS_DEATH.get();
    }
}
