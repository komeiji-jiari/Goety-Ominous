package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DropBearServant extends Summoned implements IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(9);
    public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(15);
    public static final Animation ANIMATION_JUMPUP = Animation.create(20);
    private static final EntityDataAccessor<Boolean> UPSIDE_DOWN = SynchedEntityData.defineId(DropBearServant.class, EntityDataSerializers.BOOLEAN);
    public float prevUpsideDownProgress;
    public float upsideDownProgress;
    public boolean fallRotation = random.nextBoolean();
    private int animationTick;
    private boolean jumpingUp = false;
    private Animation currentAnimation;
    private int upwardsFallingTicks = 0;
    private boolean isUpsideDownNavigator;

    private final GroundPathNavigatorWide groundNavigator;

    private final DirectPathNavigator directNavigator;

    private int ceilingTravelCooldown;

    private boolean ceilingTraveling;

    private boolean ceilingTravelStarted;

    @Nullable
    private BlockPos lastFailedCeilingStart;

    @Nullable
    private BlockPos lastFailedCeilingOwnerPos;

    private int groundPathCheckCooldown;
    private boolean groundPathReachable;

    private int landCooldown;

    @Nullable
    private BlockPos idleWanderCenter;

    @Nullable
    private BlockPos idleWanderTarget;

    private int idleWanderTicks;

    public DropBearServant(EntityType type, Level world) {
        super(type, world);
        this.groundNavigator = new GroundPathNavigatorWide(this, level());
        this.directNavigator = new DirectPathNavigator(this, level());
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.DropBearServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.DropBearServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DropBearServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.DropBearServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void setConfigurableAttributes() {
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.DropBearServantHealth.get());
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.DropBearServantDamage.get());
        }
        if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(AttributesConfig.DropBearServantFollowRange.get());
        }
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(AttributesConfig.DropBearServantKnockbackResistance.get());
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.DropBearServantLimit.get();
    }

    public static BlockPos getLowestPos(LevelAccessor world, BlockPos pos) {
        while (!world.getBlockState(pos).isFaceSturdy(world, pos, Direction.DOWN) && pos.getY() < 320) {
            pos = pos.above();
        }
        return pos;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.DROPBEAR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.DROPBEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.DROPBEAR_HURT.get();
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_BITE : random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
        }
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIDropMelee());
        this.goalSelector.addGoal(2, new SeekCeilingEntryGoal(this));

        this.goalSelector.addGoal(5, new AIUpsideDownWander());
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new ServantFollowOwnerGoal(this));
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypes.IN_WALL);
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        super.checkFallDamage(y, onGroundIn, state, pos);
    }

    protected void playBlockFallSound() {
        this.onLand();
        super.playBlockFallSound();
    }

    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveControl = new MoveControl(this);
            this.navigation = this.groundNavigator;
            this.isUpsideDownNavigator = false;
        } else {
            this.moveControl = new FlightMoveController(this, 1.1F, false);
            this.navigation = this.directNavigator;
            this.isUpsideDownNavigator = true;
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
            FoodProperties foodProperties = itemstack.getFoodProperties(this);
            if (foodProperties != null) {
                this.heal((float)foodProperties.getNutrition());
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.gameEvent(GameEvent.EAT, this);
                this.eat(this.level(), itemstack);
                Level var6 = this.level();
                if (var6 instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)var6;

                    for(int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        serverLevel.sendParticles((SimpleParticleType) ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                    }
                }

                pPlayer.swing(pHand);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        prevUpsideDownProgress = upsideDownProgress;
        if (this.isUpsideDown() && upsideDownProgress < 5F) {
            upsideDownProgress++;
        }
        if (!this.isUpsideDown() && upsideDownProgress > 0F) {
            upsideDownProgress--;
        }
        if (!this.level().isClientSide) {
            if (this.getTrueOwner() != null && CuriosFinder.hasAmethystNecklace(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
            BlockState belowState = level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
            BlockPos worldHeight = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition());
            boolean validAboveState = this.hasSturdyCeilingAbove();
            boolean validBelowState = belowState.isFaceSturdy(level(), this.getBlockPosBelowThatAffectsMyMovement(), Direction.UP);
            LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null && distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1 && this.hasLineOfSight(attackTarget)) {
                if (this.getAnimationTick() == 6) {
                    if (this.getAnimation() == ANIMATION_BITE) {
                        final float yRotRad = this.getYRot() * Mth.DEG_TO_RAD;
                        attackTarget.knockback(0.5F, Mth.sin(yRotRad), -Mth.cos(yRotRad));
                        attackTarget.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    }
                } else if (this.getAnimationTick() == 9) {
                    if (this.getAnimation() == ANIMATION_SWIPE_L) {
                        final float rot = getYRot() + 90;
                        final float rotRad = rot * Mth.DEG_TO_RAD;
                        attackTarget.knockback(0.5F, Mth.sin(rotRad), -Mth.cos(rotRad));
                        attackTarget.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    } else if (this.getAnimation() == ANIMATION_SWIPE_R) {
                        final float rot = getYRot() - 90;
                        final float rotRad = rot * Mth.DEG_TO_RAD;
                        attackTarget.knockback(0.5F, Mth.sin(rotRad), -Mth.cos(rotRad));
                        attackTarget.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    }
                }
            }
            if (jumpingUp && this.getY() > worldHeight.getY()) {
                jumpingUp = false;
            }

            if (jumpingUp && this.verticalCollision && !validAboveState && !this.onGround()) {
                jumpingUp = false;
            }
            if ((this.onGround() && this.getAnimation() == ANIMATION_JUMPUP && this.getAnimationTick() > 10 || jumpingUp && this.getAnimation() == NO_ANIMATION)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 2F, 0));
                jumpingUp = true;
            }
            this.updateAmbushState();
            if (this.isUpsideDown()) {
                jumpingUp = false;
                this.setNoGravity(!this.onGround());
                final float f = 0.91F;
                this.setDeltaMovement(this.getDeltaMovement().multiply(f, 1F, f));
                if (!this.verticalCollision) {

                    if (this.onGround() || (this.getTrueOwner() == null && validBelowState) || upwardsFallingTicks > 5) {
                        this.setUpsideDown(false);
                        upwardsFallingTicks = 0;
                    } else {
                        if (!validAboveState) {
                            upwardsFallingTicks++;
                        }
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
                    }
                } else {
                    upwardsFallingTicks = 0;
                }
                if (this.horizontalCollision) {
                    upwardsFallingTicks = 0;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.3F, 0));
                }
                if (this.isInWall() && level().isEmptyBlock(this.getBlockPosBelowThatAffectsMyMovement())) {
                    this.setPos(this.getX(), this.getY() - 1, this.getZ());
                }
            } else {
                this.setNoGravity(false);
                if (validAboveState && this.shouldGrabCeiling()) {
                    this.setUpsideDown(true);
                }
            }

            if (this.isUpsideDown()) {
                if (!this.isUpsideDownNavigator)
                    switchNavigator(false);
            } else {
                if (this.isUpsideDownNavigator)
                    switchNavigator(true);
            }
        }
    }

    private boolean isOwnerSneaking() {
        LivingEntity owner = this.getTrueOwner();
        return owner != null && owner.isShiftKeyDown();
    }

    private void updateAmbushState() {
        LivingEntity owner = this.getTrueOwner();

        if (this.ceilingTravelCooldown > 0) {
            this.ceilingTravelCooldown--;
        }
        if (this.landCooldown > 0) {
            this.landCooldown--;
        }

        if (this.getTarget() != null) {
            if (this.isUpsideDown()) {
                this.startLanding();
            } else {
                this.cancelClimb();
            }
            return;
        }

        if (owner == null) {
            if (this.isUpsideDown()) {
                this.setUpsideDown(false);
            }
            this.cancelClimb();
            return;
        }

        if (this.isOwnerSneaking()) {
            if (this.isUpsideDown()) {
                this.startLanding();
            } else {
                this.cancelClimb();
            }
            return;
        }

        if (this.isUpsideDown()) {
            if (this.ceilingTraveling || (this.isFollowing() && !this.isCommanded())) {
                this.driveCeilingTravel(owner);
            } else {
                this.startLanding();
            }
            return;
        }

        if (this.jumpingUp || this.ceilingTraveling) {
            this.driveCeilingTravel(owner);
            return;
        }

        if (this.isFollowing() && !this.isCommanded()) {
            if (this.landCooldown > 0) {
                this.cancelClimb();
                return;
            }
            if (this.isCeilingTravelEligible()) {
                this.driveCeilingTravel(owner);
                return;
            }
            this.cancelClimb();
            return;
        }

        this.cancelClimb();
        if (this.isUpsideDown()) {
            this.setUpsideDown(false);
        }
    }

    private void cancelClimb() {
        if (this.getAnimation() == ANIMATION_JUMPUP) {
            this.setAnimation(NO_ANIMATION);
        }
        this.jumpingUp = false;
        this.ceilingTraveling = false;
        this.ceilingTravelStarted = false;
        this.idleWanderTarget = null;
        this.idleWanderTicks = 0;
    }

    private boolean shouldGrabCeiling() {
        return this.ceilingTraveling || this.jumpingUp;
    }

    private boolean isCeilingTravelEligible() {
        if (this.isOwnerSneaking()) {
            return false;
        }
        if (this.ceilingTravelCooldown > 0) {
            return false;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            return false;
        }
        return this.canCeilingTravelTo(owner, this.blockPosition());
    }

    private boolean canCeilingTravelTo(LivingEntity owner, BlockPos startGroundPos) {
        if (this.lastFailedCeilingStart != null && this.lastFailedCeilingOwnerPos != null) {
            if (owner.blockPosition().distManhattan(this.lastFailedCeilingOwnerPos) >= 3) {
                this.lastFailedCeilingStart = null;
                this.lastFailedCeilingOwnerPos = null;
            } else {
                double ddx = startGroundPos.getX() - this.lastFailedCeilingStart.getX();
                double ddz = startGroundPos.getZ() - this.lastFailedCeilingStart.getZ();
                if (ddx * ddx + ddz * ddz < 1.5D) {
                    return false;
                }
            }
        }
        BlockPos start = new BlockPos(startGroundPos.getX(), startGroundPos.getY() + 2, startGroundPos.getZ());
        for (int i = 0; i < 30; i++) {
            if (this.level().getBlockState(start).isFaceSturdy(this.level(), start, Direction.DOWN)) {
                break;
            }
            start = start.above();
        }
        if (!this.level().getBlockState(start).isFaceSturdy(this.level(), start, Direction.DOWN)) {
            return false;
        }
        int travelY = start.getY();
        double dx = owner.blockPosition().getX() - startGroundPos.getX();
        double dz = owner.blockPosition().getZ() - startGroundPos.getZ();
        int steps = Math.max(1, (int) Math.ceil(Math.sqrt(dx * dx + dz * dz)));
        if (steps > 40) {
            return false;
        }
        for (int i = 0; i <= steps; i++) {
            int x = startGroundPos.getX() + (int) Math.floor(dx * i / steps);
            int z = startGroundPos.getZ() + (int) Math.floor(dz * i / steps);
            if (!this.hasCeilingColumnInRadius(x, z, travelY)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCeilingColumnInRadius(int x, int z, int travelY) {
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                for (int y = travelY - 3; y <= travelY + 8; y++) {
                    BlockPos probe = new BlockPos(x + ox, y, z + oz);
                    if (!this.level().getBlockState(probe).isFaceSturdy(this.level(), probe, Direction.DOWN)) {
                        continue;
                    }
                    BlockPos below = probe.below();
                    if (this.level().getBlockState(below).getCollisionShape(this.level(), below).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private BlockPos findCeilingEntryPoint(LivingEntity owner, @Nullable BlockPos exclude) {
        BlockPos center = this.blockPosition();
        for (int radius = 1; radius <= 6; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    BlockPos probe = center.offset(dx, 0, dz);
                    if (exclude != null && probe.getX() == exclude.getX() && probe.getZ() == exclude.getZ()) {
                        continue;
                    }
                    BlockPos ground = this.getGroundPosition(probe);
                    BlockPos standPos = ground.above();
                    if (!this.level().getBlockState(standPos).getCollisionShape(this.level(), standPos).isEmpty()) {
                        continue;
                    }
                    if (!this.level().getBlockState(ground).isSolid()) {
                        continue;
                    }
                    if (this.canCeilingTravelTo(owner, standPos)) {
                        return standPos;
                    }
                }
            }
        }
        return null;
    }

    private boolean canGroundPathTo(LivingEntity owner) {
        if (--this.groundPathCheckCooldown > 0) {
            return this.groundPathReachable;
        }
        this.groundPathCheckCooldown = 10;
        Path path = this.getNavigation().createPath(owner, 0);
        this.groundPathReachable = path != null && path.canReach();
        return this.groundPathReachable;
    }

    private void driveCeilingTravel(LivingEntity owner) {
        if (this.isUpsideDown()) {
            this.hangAboveOwner(owner);
        } else if (this.onGround()) {

            if (this.ceilingTravelStarted && this.getAnimation() == NO_ANIMATION
                    && (!this.jumpingUp || this.verticalCollision)) {
                this.cancelClimb();
                this.ceilingTravelCooldown = 60;
                this.lastFailedCeilingStart = this.blockPosition();
                this.lastFailedCeilingOwnerPos = owner.blockPosition();
                return;
            }

            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_JUMPUP);
                this.jumpingUp = true;
                this.ceilingTraveling = true;
                this.ceilingTravelStarted = true;
            }
        } else {

            if (this.hasSturdyCeilingAbove()) {
                this.setUpsideDown(true);
                this.ceilingTraveling = true;
            }
        }
    }

    private void hangAboveOwner(LivingEntity owner) {
        if (!this.hasLineOfSight(owner)) {
            this.startLanding();
            return;
        }
        BlockPos ownerCeiling = this.findHangingCeiling(
                new BlockPos(owner.blockPosition().getX(), this.blockPosition().getY(), owner.blockPosition().getZ()), 2);
        if (ownerCeiling == null) {
            this.startLanding();
            return;
        }

        if (this.idleWanderCenter != null && ownerCeiling.distManhattan(this.idleWanderCenter) > 3) {
            this.idleWanderTarget = null;
            this.idleWanderTicks = 0;
        }

        if (this.idleWanderTicks > 0 && this.idleWanderTarget != null) {
            double hx = this.getX() - (this.idleWanderTarget.getX() + 0.5D);
            double hz = this.getZ() - (this.idleWanderTarget.getZ() + 0.5D);
            if (hx * hx + hz * hz < 0.25D) {
                this.idleWanderTarget = null;
                this.idleWanderTicks = 0;
                this.stopHovering();
                return;
            }
            this.getMoveControl().setWantedPosition(
                    this.idleWanderTarget.getX() + 0.5D, this.idleWanderTarget.getY(), this.idleWanderTarget.getZ() + 0.5D, 0.8D);
            this.idleWanderTicks--;
            return;
        }

        double dx = this.getX() - (ownerCeiling.getX() + 0.5D);
        double dz = this.getZ() - (ownerCeiling.getZ() + 0.5D);
        if (dx * dx + dz * dz < 1.0D) {
            this.idleWanderCenter = ownerCeiling;
            if (this.getRandom().nextInt(50) == 0) {
                this.startIdleWander();
            }
            this.stopHovering();
            return;
        }

        this.getMoveControl().setWantedPosition(
                ownerCeiling.getX() + 0.5D, ownerCeiling.getY(), ownerCeiling.getZ() + 0.5D, 1.0D);
    }

    private void stopHovering() {
        this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
    }

    private void startIdleWander() {
        if (this.idleWanderCenter == null) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            int rx = this.getRandom().nextInt(7) - 3;
            int rz = this.getRandom().nextInt(7) - 3;
            if (rx == 0 && rz == 0) {
                continue;
            }
            BlockPos target = getLowestPos(this.level(),
                    new BlockPos(this.idleWanderCenter.getX() + rx, this.blockPosition().getY(), this.idleWanderCenter.getZ() + rz));
            if (!this.level().getBlockState(target).isFaceSturdy(this.level(), target, Direction.DOWN)) {
                continue;
            }
            BlockPos below = target.below();
            if (!this.level().getBlockState(below).getCollisionShape(this.level(), below).isEmpty()) {
                continue;
            }
            this.idleWanderTarget = target;
            this.idleWanderTicks = 25 + this.getRandom().nextInt(20);
            return;
        }
        this.idleWanderTarget = null;
        this.idleWanderTicks = 0;
    }

    private void startLanding() {
        if (this.isUpsideDown()) {
            this.setUpsideDown(false);
        }
        this.cancelClimb();
        this.landCooldown = 60;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(UPSIDE_DOWN, false);
    }

    public boolean isUpsideDown() {
        return this.entityData.get(UPSIDE_DOWN);
    }

    public void setUpsideDown(boolean upsideDown) {
        this.entityData.set(UPSIDE_DOWN, upsideDown);
    }

    protected BlockPos getPositionAbove() {
        return new BlockPos((int) this.position().x, (int) (this.getBoundingBox().maxY + 0.5000001D), (int) this.position().z);
    }

    private boolean hasSturdyCeilingAbove() {
        AABB box = this.getBoundingBox();
        int probeY = (int) (box.maxY + 0.5000001D);
        int xMin = Mth.floor(box.minX);
        int xMax = Mth.floor(box.maxX - 0.0001D);
        int zMin = Mth.floor(box.minZ);
        int zMax = Mth.floor(box.maxZ - 0.0001D);
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                BlockPos probe = new BlockPos(x, probeY, z);
                if (this.level().getBlockState(probe).isFaceSturdy(this.level(), probe, Direction.DOWN)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlockPos findHangingCeiling(BlockPos base, int radius) {
        for (int r = 0; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != r) {
                        continue;
                    }
                    BlockPos ceiling = getLowestPos(this.level(), base.offset(dx, 0, dz));
                    if (this.level().getBlockState(ceiling).isFaceSturdy(this.level(), ceiling, Direction.DOWN)) {
                        return ceiling;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE, ANIMATION_SWIPE_L, ANIMATION_SWIPE_R, ANIMATION_JUMPUP};
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.SPAWN_EGG) {
            this.setHasLifespan(false);
            this.setLifespan(0);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void onLand() {
        if (!this.level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 39);
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.5D))) {
                if (!isAlliedTo(entity) && !(entity instanceof DropBearServant) && entity != this) {
                    entity.hurt(this.getServantAttack(), 2.0F + random.nextFloat() * 5F);
                    launch(entity, true);
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            final double d0 = e.getX() - this.getX();
            final double d1 = e.getZ() - this.getZ();
            final double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            final float f = 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 39) {
            spawnGroundEffects();
        } else {
            super.handleEntityEvent(id);

        }
    }

    public void spawnGroundEffects() {
        float radius = 2.3F;
        if (this.level().isClientSide) {
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (Maths.STARTING_ANGLE * this.yBodyRot) + i1;
                double extraX = radius * Mth.sin(Mth.PI + angle);
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = getGroundPosition(new BlockPos(Mth.floor(this.getX() + extraX), (int) this.getY(), Mth.floor(this.getZ() + extraZ)));
                BlockState state = this.level().getBlockState(ground);
                if (!state.isAir()) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    private BlockPos getGroundPosition(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() > 2 && level().isEmptyBlock(position) && level().getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    class AIUpsideDownWander extends RandomStrollGoal {

        public AIUpsideDownWander() {
            super(DropBearServant.this, 1D, 50);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (DropBearServant.this.isUpsideDown()) {
                for (int i = 0; i < 15; i++) {
                    BlockPos randPos = DropBearServant.this.blockPosition().offset(DropBearServant.this.getRandom().nextInt(16) - 8, -2, DropBearServant.this.getRandom().nextInt(16) - 8);
                    BlockPos lowestPos = DropBearServant.getLowestPos(level(), randPos);
                    if (level().getBlockState(lowestPos).isFaceSturdy(level(), lowestPos, Direction.DOWN)) {
                        return Vec3.atCenterOf(lowestPos);
                    }
                }
                return null;
            } else {
                return super.getPosition();
            }
        }

        public boolean canUse() {

            if (DropBearServant.this.isFollowing()) {
                return false;
            }
            if (DropBearServant.this.ceilingTraveling || DropBearServant.this.jumpingUp) {
                return false;
            }
            if (!super.canUse()) {
                return false;
            }

            return !((DropBearServant.this.isStaying() || DropBearServant.this.isCommanded())
                    && DropBearServant.this.getTrueOwner() != null);
        }

        public boolean canContinueToUse() {
            if (DropBearServant.this.isUpsideDown()) {
                double d0 = DropBearServant.this.getX() - wantedX;
                double d2 = DropBearServant.this.getZ() - wantedZ;
                double d4 = d0 * d0 + d2 * d2;
                return d4 > 4;
            } else {
                return super.canContinueToUse();
            }
        }

        public void stop() {
            super.stop();
            this.wantedX = 0;
            this.wantedY = 0;
            this.wantedZ = 0;
        }

        public void start() {
            if (DropBearServant.this.isUpsideDown()) {
                this.mob.getMoveControl().setWantedPosition(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier * 0.7F);
            } else {
                this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
            }
        }

    }

    private class AIDropMelee extends Goal {

        public AIDropMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return DropBearServant.this.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = DropBearServant.this.getTarget();
            if (target != null) {
                double dist = DropBearServant.this.distanceTo(target);
                if (DropBearServant.this.isUpsideDown()) {
                    DropBearServant.this.setUpsideDown(false);
                } else {
                    if (DropBearServant.this.onGround()) {
                        DropBearServant.this.getNavigation().moveTo(target, 1.2D);
                    }
                }
                if (dist < 3D) {
                    DropBearServant.this.doHurtTarget(target);
                }
            }
        }

    }

    private class SeekCeilingEntryGoal extends Goal {
        private final DropBearServant servant;
        private BlockPos target;
        private BlockPos lastFailedTarget;
        private int recalcTicks;
        private int searchCooldown;
        private int noProgressTicks;
        private double lastDistSqr;

        public SeekCeilingEntryGoal(DropBearServant servant) {
            this.servant = servant;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.servant.getTrueOwner();
            if (owner == null || owner.isSpectator()) {
                return false;
            }
            if (this.servant.isOwnerSneaking()) {
                return false;
            }
            if (!this.servant.isFollowing() || this.servant.isCommanded()) {
                return false;
            }
            if (this.servant.getTarget() != null) {
                return false;
            }
            if (this.servant.isUpsideDown() || this.servant.ceilingTraveling || this.servant.jumpingUp) {
                return false;
            }
            if (this.servant.distanceToSqr(owner) >= Mth.square(16.0D)) {
                return false;
            }
            if (this.servant.canGroundPathTo(owner)) {
                return false;
            }
            if (this.servant.canCeilingTravelTo(owner, this.servant.blockPosition())) {
                return false;
            }
            if (this.target == null) {
                if (--this.searchCooldown > 0) {
                    return false;
                }
                this.searchCooldown = 30;
                this.target = this.servant.findCeilingEntryPoint(owner, this.lastFailedTarget);
            }
            return this.target != null;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity owner = this.servant.getTrueOwner();
            if (owner == null) {
                return false;
            }
            if (this.servant.isOwnerSneaking()) {
                return false;
            }
            if (this.servant.isUpsideDown() || this.servant.ceilingTraveling || this.servant.jumpingUp) {
                return false;
            }
            if (this.servant.distanceToSqr(owner) >= Mth.square(16.0D)) {
                return false;
            }
            if (this.servant.canGroundPathTo(owner)) {
                return false;
            }
            if (this.servant.canCeilingTravelTo(owner, this.servant.blockPosition())) {
                return false;
            }
            if (this.target == null) {
                return false;
            }
            if (this.servant.getNavigation().isDone()
                    && this.servant.distanceToSqr(Vec3.atCenterOf(this.target)) <= Mth.square(2.0D)) {
                this.lastFailedTarget = this.target;
                this.target = null;
                return false;
            }
            return true;
        }

        @Override
        public void start() {
            this.recalcTicks = 0;
            this.noProgressTicks = 0;
            this.lastDistSqr = this.servant.distanceToSqr(Vec3.atCenterOf(this.target));
            this.servant.getNavigation().moveTo(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D, 1.0D);
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }
            double distSqr = this.servant.distanceToSqr(Vec3.atCenterOf(this.target));
            if (distSqr < this.lastDistSqr - Mth.square(0.5D)) {
                this.noProgressTicks = 0;
                this.lastDistSqr = distSqr;
            } else if (++this.noProgressTicks > 60) {
                this.lastFailedTarget = this.target;
                this.target = null;
                this.servant.getNavigation().stop();
                return;
            }
            if (--this.recalcTicks <= 0) {
                this.recalcTicks = 10;
                this.servant.getNavigation().moveTo(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D, 1.0D);
            }
        }

        @Override
        public void stop() {
            this.servant.getNavigation().stop();
            this.target = null;
        }
    }

    private class ServantFollowOwnerGoal extends Summoned.FollowOwnerGoal<DropBearServant> {
        public ServantFollowOwnerGoal(DropBearServant servant) {
            super(servant, 1.0D, 2.0F, 2.0F);
        }

        @Override
        public boolean canUse() {
            if (DropBearServant.this.isUpsideDown()
                    || DropBearServant.this.ceilingTraveling
                    || DropBearServant.this.jumpingUp) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (DropBearServant.this.isUpsideDown()
                    || DropBearServant.this.ceilingTraveling
                    || DropBearServant.this.jumpingUp) {
                return false;
            }
            return super.canContinueToUse();
        }
    }

}
