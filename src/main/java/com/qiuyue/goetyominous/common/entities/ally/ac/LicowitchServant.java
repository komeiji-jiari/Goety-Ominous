package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ConversionCrucibleBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.entities.projectile.LicowitchServantHex;
import com.qiuyue.goetyominous.common.entities.projectile.LicowitchServantPeppermint;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ModMobType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class LicowitchServant extends Summoned implements IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> CROSSED_ARMS = SynchedEntityData.defineId(LicowitchServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<Vec3>> TELEPORTING_TO_POS = SynchedEntityData.defineId(LicowitchServant.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());

    public static final Animation ANIMATION_SWING_LEFT = Animation.create(20);
    public static final Animation ANIMATION_SWING_RIGHT = Animation.create(20);
    public static final Animation ANIMATION_EAT = Animation.create(100);
    public static final Animation ANIMATION_SPELL_0 = Animation.create(45);
    public static final Animation ANIMATION_SPELL_1 = Animation.create(50);

    private Animation currentAnimation;
    private int animationTick;

    private float prevUncrossedArmsProgress;
    private float uncrossedArmsProgress;
    private float prevTeleportingProgress;
    private float teleportingProgress;

    public boolean updateHeldItems = true;
    public boolean updateFoldedArms = true;

    private int eatCooldown = 0;
    private int teleportCooldown = 0;
    private int summonCooldown = 0;
    private boolean summonSelf;

    public LicowitchServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveHelper();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.LicowitchServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.LicowitchServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.LicowitchServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.LicowitchServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.LicowitchServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.LicowitchServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CROSSED_ARMS, true);
        this.entityData.define(TELEPORTING_TO_POS, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LicowitchAttackGoal());
        this.goalSelector.addGoal(2, new LicowitchUseCrucibleGoal());
        this.goalSelector.addGoal(3, new RandomlyTeleportGoal());
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof LicowitchServant;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.FEL;
    }

    public float getArmsUncrossedProgress(float partialTicks) {
        return (prevUncrossedArmsProgress + (uncrossedArmsProgress - prevUncrossedArmsProgress) * partialTicks) * 0.2F;
    }

    public float getTeleportingProgress(float partialTicks) {
        return (prevTeleportingProgress + (teleportingProgress - prevTeleportingProgress) * partialTicks) * 0.025F;
    }

    public boolean areArmsVisuallyCrossed(float partialTicks) {
        return getArmsUncrossedProgress(partialTicks) <= 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        prevUncrossedArmsProgress = uncrossedArmsProgress;
        prevTeleportingProgress = teleportingProgress;
        if (!this.areArmsCrossed() && uncrossedArmsProgress < 5F) {
            uncrossedArmsProgress++;
        }
        if (this.areArmsCrossed() && uncrossedArmsProgress > 0F) {
            uncrossedArmsProgress--;
        }
        if (this.getTeleportingToPos() != null && teleportingProgress < 40F) {
            teleportingProgress++;
        }
        if (this.getTeleportingToPos() == null && teleportingProgress > 0F) {
            teleportingProgress = Math.max(0, teleportingProgress - 10);
        }
        if (this.getAnimation() == ANIMATION_SPELL_1) {
            float f = this.getAnimationTick() < 10 ? 0.2F : (this.getAnimationTick() > 40 ? -0.2F : 0.0F);
            float backAmount = this.getAnimationTick() > 40 ? 0.0F : 0.1F;
            this.setDeltaMovement(new Vec3(0.0D, f, 0.0D).add(new Vec3(0.0D, 0.0D, -backAmount).yRot((float) -Math.toRadians(this.getYRot()))));
            this.fallDistance = 0.0F;
        }
        if (updateHeldItems && !level().isClientSide) {
            ItemStack main = ItemStack.EMPTY;
            if (this.getAnimation() == ANIMATION_SPELL_0 || this.getAnimation() == ANIMATION_SPELL_1
                    || this.getAnimation() == ANIMATION_SWING_LEFT || this.getAnimation() == ANIMATION_SWING_RIGHT) {
                main = new ItemStack(ACItemRegistry.SUGAR_STAFF.get());
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() < 90) {
                main = new ItemStack(ACBlockRegistry.CANDY_CANE.get());
            }
            this.setItemInHand(InteractionHand.MAIN_HAND, main);
        }
        if (updateFoldedArms) {
            boolean unfold = this.teleportingProgress > 0 || this.getAnimation() == ANIMATION_SPELL_0
                    || this.getAnimation() == ANIMATION_SPELL_1 || this.getAnimation() == ANIMATION_SWING_LEFT
                    || this.getAnimation() == ANIMATION_SWING_RIGHT;
            this.setArmsCrossed(!unfold);
        }
        if (!level().isClientSide) {
            if (this.getAnimation() == ANIMATION_SPELL_1) {
                if (this.summonSelf && this.getAnimationTick() == 36) {
                    this.summonCandyGuards(1 + this.random.nextInt(2));
                    this.summonSelf = false;
                }
            } else {
                this.summonSelf = false;
            }
            if (this.getHealth() < this.getMaxHealth() && this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                    && tickCount % 20 == 8 && eatCooldown == 0) {
                this.syncAnimation(ANIMATION_EAT);
                eatCooldown = 200;
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 90) {
                this.heal(5);
            }
            if (eatCooldown > 0) {
                eatCooldown--;
            }
            if (summonCooldown > 0) {
                summonCooldown--;
            }
            if (teleportCooldown > 0) {
                teleportCooldown--;
            }
        } else {
            if (this.getAnimation() == ANIMATION_SPELL_1 && this.getAnimationTick() < 40 && this.random.nextInt(2) == 0) {
                Vec3 staff = this.getStaffPosition();
                this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(),
                        staff.x, staff.y, staff.z,
                        this.random.nextGaussian() * 0.05F, this.random.nextGaussian() * 0.05F, this.random.nextGaussian() * 0.05F);
            } else if (this.getTeleportingToPos() != null && this.getTeleportingProgress(1.0F) < 1.0F) {
                Vec3 angle = new Vec3(random.nextBoolean() ? 0.5F : -0.5F, 2.0F, 0.0F).yRot((float) -Math.toRadians(this.yBodyRot));
                Vec3 delta = new Vec3(random.nextGaussian() * 2.0F, 4.0F, random.nextGaussian()).yRot((float) -Math.toRadians(this.yBodyRot)).add(this.position());
                this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(),
                        this.getX() + angle.x, this.getY() + angle.y, this.getZ() + angle.z, delta.x, delta.y, delta.z);
            } else if (this.random.nextFloat() < 0.003F) {
                for (int i = 0; i < this.random.nextInt(2) + 2; ++i) {
                    this.level().addParticle(ACParticleRegistry.WITCH_COOKIE.get(),
                            this.getX() + this.random.nextGaussian() * 0.13D,
                            this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * 0.13D,
                            this.getZ() + this.random.nextGaussian() * 0.13D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (this.teleportingProgress >= 40 && this.getTeleportingToPos() != null) {
            Vec3 vec3 = this.getTeleportingToPos();
            this.setPos(vec3.x, vec3.y, vec3.z);
            if (!level().isClientSide) {
                this.setTeleportingToPos(null);
            } else {
                for (int i = 0; i < this.random.nextInt(8) + 8; ++i) {
                    this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(),
                            this.getX() + this.random.nextGaussian() * 0.3D, this.getY() + this.random.nextGaussian() * 1.5D,
                            this.getZ() + this.random.nextGaussian() * 0.3D, 0.0D, 0.0D, 0.0D);
                }
            }
            this.setOldPosAndRot();
            this.postTeleport();
        }
        if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() < 90 && this.getAnimationTick() % 4 == 0) {
            this.triggerItemUseEffects(this.getItemInHand(InteractionHand.MAIN_HAND), 2);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void postTeleport() {
        this.teleportCooldown = 200 + random.nextInt(300);
    }

    public boolean canTeleport() {
        return this.teleportCooldown <= 0;
    }

    public boolean canCastSummon() {
        return this.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.summonCooldown <= 0
                && this.candyServantCount() < MobsConfig.LicowitchSummonLimit.get();
    }

    public boolean startSummonCast() {
        if (this.level().isClientSide || !this.canCastSummon()) {
            return false;
        }
        this.summonSelf = true;
        this.syncAnimation(ANIMATION_SPELL_1);
        this.summonCooldown = 100 + this.random.nextInt(100);
        this.playSound(ACSoundRegistry.LICOWITCH_CAST_SUMMON.get(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
        return true;
    }

    public int summonCandyGuards(int maxCount) {
        int summoned = 0;
        for (int i = 0; i < maxCount; i++) {
            if (this.candyServantCount() >= MobsConfig.LicowitchSummonLimit.get()) {
                break;
            }
            EntityType<?> type = this.candyTypeForRoll(this.random.nextFloat());
            if (type == null) {
                continue;
            }
            if (this.spawnCandyMob(type, this.findSpawnSpot())) {
                summoned++;
            }
        }
        return summoned;
    }

    private boolean spawnCandyMob(EntityType<?> type, Vec3 spot) {
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Entity entity = type.create(serverLevel);
        if (!(entity instanceof Mob mob)) {
            return false;
        }
        if (mob instanceof Owned owned) {
            owned.setTrueOwner(this);
            if (MobsConfig.LicowitchSummonsLife.get()) {
                owned.setLifespan(20 * (45 + this.random.nextInt(75)));
                owned.setHasLifespan(true);
            }
        }
        mob.setPos(spot);
        mob.setYRot(this.random.nextFloat() * 360.0F);
        mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(spot)),
                MobSpawnType.MOB_SUMMONED, null, null);
        mob.setPersistenceRequired();
        serverLevel.addFreshEntity(mob);
        return true;
    }

    @Nullable
    private Vec3 findSummonSpotNear(LivingEntity target, int range) {
        for (int i = 0; i < 15; ++i) {
            Vec3 heightAdjusted = target.position().add(this.random.nextInt(range * 2) - range,
                    target.getEyeHeight() + this.random.nextInt(4), this.random.nextInt(range * 2) - range);
            Vec3 ground = ACMath.getGroundBelowPosition(target.level(), heightAdjusted);
            BlockHitResult result = target.level().clip(new ClipContext(target.getEyePosition(),
                    ground.add(0.0D, 1.0D, 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));
            if (result.getType() == HitResult.Type.MISS) {
                return ground;
            }
        }
        return null;
    }

    @Nullable
    private EntityType<?> candyTypeForRoll(float roll) {
        if (roll < 0.1F) {
            return AcEntityRegistry.CANDICORN_SERVANT.get();
        } else if (roll < 0.2F) {
            return AcEntityRegistry.GUMMY_BEAR_SERVANT.get();
        } else if (roll < 0.5F) {
            return AcEntityRegistry.GUMBEEPER_SERVANT.get();
        } else if (roll < 0.65F) {
            return AcEntityRegistry.CARAMEL_CUBE_SERVANT.get();
        } else {
            return AcEntityRegistry.CANIAC_SERVANT.get();
        }
    }

    private int candyServantCount() {
        Predicate<Entity> predicate = entity -> entity.isAlive()
                && entity instanceof IOwned owned && owned.getTrueOwner() == this;
        return this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D), predicate).size();
    }

    private Vec3 findSpawnSpot() {
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double range = 2.0D + this.random.nextDouble() * 2.0D;
        Vec3 spot = new Vec3(this.getX() + Math.cos(angle) * range, this.getY(), this.getZ() + Math.sin(angle) * range);
        AABB aabb = this.getBoundingBox().move(spot.subtract(this.position()));
        if (this.level().isUnobstructed(this, Shapes.create(aabb))) {
            return spot;
        }
        return this.position();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    public boolean canReach(BlockPos pos) {
        Path path = this.getNavigation().createPath(pos, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - pos.getX();
                int j = node.y - pos.getY();
                int k = node.z - pos.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.getTeleportingToPos() != null) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3 = Vec3.ZERO;
        }
        super.travel(vec3);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player == this.getTrueOwner() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()
                && this.getTarget() == null && this.canCastSummon()) {
            if (this.startSummonCast()) {
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean b) {
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return slot.isArmor() ? super.getEquipmentDropChance(slot) : 0.0F;
    }

    @Override
    public void awardKillScore(Entity entity, int deathScore, DamageSource damageSource) {
        super.awardKillScore(entity, deathScore, damageSource);
        this.level().playSound(null, this.blockPosition(), ACSoundRegistry.LICOWITCH_CELEBRATE.get(), SoundSource.NEUTRAL, 0.3F, 0.9F + this.level().random.nextFloat() * 0.2F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.LICOWITCH_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.LICOWITCH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LICOWITCH_DEATH.get();
    }

    public boolean areArmsCrossed() {
        return this.entityData.get(CROSSED_ARMS);
    }

    public void setArmsCrossed(boolean crossed) {
        this.entityData.set(CROSSED_ARMS, crossed);
    }

    @Nullable
    public Vec3 getTeleportingToPos() {
        return this.entityData.get(TELEPORTING_TO_POS).orElse(null);
    }

    public void setTeleportingToPos(@Nullable Vec3 teleportingToPos) {
        this.entityData.set(TELEPORTING_TO_POS, Optional.ofNullable(teleportingToPos));
    }

    public Vec3 getStaffPosition() {
        Vec3 angle = new Vec3(this.getMainArm() == HumanoidArm.LEFT ? 0.18F : -0.18F, 1.625F, 1.625F)
                .yRot((float) -Math.toRadians(this.yBodyRot));
        return this.position().add(angle);
    }

    public Vec3 getSwingArmPosition() {
        Vec3 angle = new Vec3(this.getAnimation() == ANIMATION_SWING_LEFT ? 0.25F : -0.25F, 1.45F, 0.45F)
                .yRot((float) -Math.toRadians(this.yBodyRot));
        return this.position().add(angle);
    }

    public static ItemStack getHungerPotion() {
        return ACEffectRegistry.createSplashPotion(ACEffectRegistry.STRONG_HUNGER_POTION.get());
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
        AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SWING_LEFT, ANIMATION_SWING_RIGHT, ANIMATION_EAT, ANIMATION_SPELL_0, ANIMATION_SPELL_1};
    }

    private class MoveHelper extends MoveControl {

        private MoveHelper() {
            super(LicowitchServant.this);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.STRAFE) {
                this.speedModifier = 0.5F;
            }
            super.tick();
        }
    }

    private class RandomlyTeleportGoal extends Goal {

        private Vec3 teleportVec;

        private RandomlyTeleportGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = LicowitchServant.this.getTarget();
            if (LicowitchServant.this.random.nextInt(1000) == 0 && LicowitchServant.this.canTeleport()
                    && LicowitchServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                    && LicowitchServant.this.getTeleportingToPos() == null
                    && (target == null || !target.isAlive()) && !LicowitchServant.this.isStaying()
                    && !LicowitchServant.this.isCommanded()) {
                this.teleportVec = LandRandomPos.getPos(LicowitchServant.this, 16, 16);
                if (this.teleportVec != null) {
                    this.teleportVec = this.teleportVec.add(0.0D, 1.0D, 0.0D);
                    AABB aabb = LicowitchServant.this.getBoundingBox().move(this.teleportVec.subtract(LicowitchServant.this.position()));
                    return LicowitchServant.this.level().isUnobstructed(LicowitchServant.this, Shapes.create(aabb));
                }
            }
            return false;
        }

        @Override
        public void start() {
            LicowitchServant.this.setTeleportingToPos(this.teleportVec);
        }

        @Override
        public boolean canContinueToUse() {
            return LicowitchServant.this.canTeleport() || LicowitchServant.this.getTeleportingToPos() != null;
        }
    }

    private class LicowitchAttackGoal extends Goal {

        private int peppermintCooldown = 0;
        private int hexCooldown = 0;
        private int potionCooldown = 0;
        private int enqueuedAttackType;
        private int checkReachCooldown = 0;
        private int duration = 0;

        private LicowitchAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = LicowitchServant.this.getTarget();
            return target != null && target.isAlive() && !LicowitchServant.this.isStaying()
                    && !LicowitchServant.this.isCommanded();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            LicowitchServant.this.updateFoldedArms = true;
            LicowitchServant.this.updateHeldItems = true;
            this.peppermintCooldown = 0;
            this.hexCooldown = 0;
            this.potionCooldown = 0;
            this.checkReachCooldown = 0;
            this.duration = 0;
        }

        @Override
        public void tick() {
            if (this.peppermintCooldown > 0) {
                this.peppermintCooldown--;
            }
            if (this.hexCooldown > 0) {
                this.hexCooldown--;
            }
            if (this.potionCooldown > 0) {
                this.potionCooldown--;
            }
            if (this.checkReachCooldown > 0) {
                this.checkReachCooldown--;
            }
            LivingEntity target = LicowitchServant.this.getTarget();
            if (target == null || !target.isAlive()) {
                return;
            }
            if (this.duration > 200 && this.checkReachCooldown == 0) {
                this.checkReachCooldown = 100 + LicowitchServant.this.getRandom().nextInt(40);
                if (LicowitchServant.this.canTeleport() && !LicowitchServant.this.canReach(target.blockPosition())) {
                    LicowitchServant.this.setTeleportingToPos(Vec3.atBottomCenterOf(target.blockPosition()));
                    return;
                }
            }
            if (LicowitchServant.this.getTeleportingToPos() != null) {
                return;
            }
            double distance = LicowitchServant.this.distanceTo(target);
            double distanceXZ = Math.sqrt(LicowitchServant.this.distanceToSqr(target.getX(), LicowitchServant.this.getY(), target.getZ()));
            double attackDistance = LicowitchServant.this.getBbWidth() + target.getBbWidth() + 5.0D;
            LicowitchServant.this.lookAt(target, 30.0F, 30.0F);
            if (LicowitchServant.this.getAnimation() == ANIMATION_EAT
                    || (this.enqueuedAttackType == 0 && distance < attackDistance - 1.0D)
                    || (LicowitchServant.this.getAnimation() == ANIMATION_SPELL_1 && distanceXZ < 6.0D)) {
                LicowitchServant.this.getMoveControl().strafe(-4.0F, 0.0F);
                LicowitchServant.this.getNavigation().stop();
            } else if (distance > attackDistance || !LicowitchServant.this.hasLineOfSight(target)) {
                LicowitchServant.this.getNavigation().moveTo(target, 1.0D);
            }
            if (LicowitchServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                if (this.enqueuedAttackType == 0) {
                    if (LicowitchServant.this.hasLineOfSight(target)) {
                        if (this.peppermintCooldown <= 0 && LicowitchServant.this.getRandom().nextBoolean()) {
                            this.enqueuedAttackType = 1;
                            this.peppermintCooldown = 80;
                        } else if (this.hexCooldown <= 0 && LicowitchServant.this.getRandom().nextBoolean()) {
                            this.enqueuedAttackType = 2;
                            this.hexCooldown = 200;
                        } else if (this.potionCooldown <= 0 && distance < 10.0D && !target.hasEffect(MobEffects.HUNGER)) {
                            this.enqueuedAttackType = 3;
                            LicowitchServant.this.updateHeldItems = false;
                            LicowitchServant.this.setItemInHand(InteractionHand.MAIN_HAND, getHungerPotion());
                            this.potionCooldown = 100;
                        }
                    }
                } else {
                    if (this.enqueuedAttackType == 1 && distance < 10.0D) {
                        LicowitchServant.this.syncAnimation(ANIMATION_SPELL_0);
                    }
                    if (this.enqueuedAttackType == 2 && distance < 7.0D) {
                        LicowitchServant.this.syncAnimation(ANIMATION_SPELL_1);
                    }
                    if (this.enqueuedAttackType == 3 && distance < 10.0D) {
                        LicowitchServant.this.syncAnimation(LicowitchServant.this.getMainArm() == HumanoidArm.RIGHT
                                ? ANIMATION_SWING_RIGHT : ANIMATION_SWING_LEFT);
                    }
                }
            }
            if (this.enqueuedAttackType == 1 && LicowitchServant.this.getAnimation() == ANIMATION_SPELL_0
                    && LicowitchServant.this.getAnimationTick() == 18) {
                this.peppermintAttack(target);
                this.enqueuedAttackType = 0;
            }
            if (this.enqueuedAttackType == 2 && LicowitchServant.this.getAnimation() == ANIMATION_SPELL_1
                    && LicowitchServant.this.getAnimationTick() == 36) {
                this.hexAttack(target);
                this.enqueuedAttackType = 0;
            }
            if (this.enqueuedAttackType == 3 && (LicowitchServant.this.getAnimation() == ANIMATION_SWING_RIGHT
                    || LicowitchServant.this.getAnimation() == ANIMATION_SWING_LEFT)
                    && LicowitchServant.this.getAnimationTick() == 6) {
                this.potionAttack(target);
                LicowitchServant.this.updateHeldItems = true;
                this.enqueuedAttackType = 0;
            }
            ++this.duration;
        }

        private void potionAttack(LivingEntity target) {
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - LicowitchServant.this.getX();
            double d1 = target.getEyeY() - 1.1F - LicowitchServant.this.getY();
            double d2 = target.getZ() + vec3.z - LicowitchServant.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            ThrownPotion thrownPotion = new ThrownPotion(LicowitchServant.this.level(), LicowitchServant.this);
            thrownPotion.setItem(getHungerPotion());
            thrownPotion.setXRot(thrownPotion.getXRot() - -20.0F);
            thrownPotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
            if (!LicowitchServant.this.isSilent()) {
                LicowitchServant.this.level().playSound(null, LicowitchServant.this.getX(), LicowitchServant.this.getY(),
                        LicowitchServant.this.getZ(), SoundEvents.WITCH_THROW, LicowitchServant.this.getSoundSource(),
                        1.0F, 0.8F + LicowitchServant.this.getRandom().nextFloat() * 0.4F);
            }
            LicowitchServant.this.level().addFreshEntity(thrownPotion);
        }

        private void hexAttack(LivingEntity target) {
            LicowitchServant.this.lookAt(target, 180.0F, 30.0F);
            boolean summonSpace = LicowitchServant.this.candyServantCount() < MobsConfig.LicowitchSummonLimit.get();
            boolean summoned = false;
            if (summonSpace && LicowitchServant.this.getRandom().nextBoolean()) {
                Vec3 summonSpot = LicowitchServant.this.findSummonSpotNear(target, 10);
                if (summonSpot != null) {
                    EntityType<?> type = LicowitchServant.this.candyTypeForRoll(LicowitchServant.this.getRandom().nextFloat());
                    if (type != null && LicowitchServant.this.spawnCandyMob(type, summonSpot)) {
                        LicowitchServant.this.level().playSound(null, LicowitchServant.this.blockPosition(),
                                ACSoundRegistry.LICOWITCH_CAST_SUMMON.get(), LicowitchServant.this.getSoundSource(),
                                0.3F, 0.9F + LicowitchServant.this.level().random.nextFloat() * 0.2F);
                        summoned = true;
                    }
                }
            }
            if (!summoned) {
                Vec3 ground = ACMath.getGroundBelowPosition(LicowitchServant.this.level(), LicowitchServant.this.getEyePosition());
                Vec3 groundThere = ACMath.getGroundBelowPosition(target.level(), target.getEyePosition());
                LicowitchServantHex hex = new LicowitchServantHex(AcEntityRegistry.LICOWITCH_SERVANT_HEX.get(), LicowitchServant.this.level());
                hex.setOwner(LicowitchServant.this);
                hex.setPos(ground.x, groundThere.y, ground.z);
                hex.setDeltaMovement(groundThere.subtract(ground).multiply(0.25, 0.0, 0.25));
                LicowitchServant.this.level().addFreshEntity(hex);
                LicowitchServant.this.level().playSound(null, LicowitchServant.this.blockPosition(),
                        ACSoundRegistry.LICOWITCH_CAST_HEX.get(), LicowitchServant.this.getSoundSource(),
                        0.3F, 0.9F + LicowitchServant.this.level().random.nextFloat() * 0.2F);
            }
        }

        private void peppermintAttack(LivingEntity target) {
            boolean spinning = LicowitchServant.this.getRandom().nextBoolean();
            Vec3 subtract = target.position().subtract(LicowitchServant.this.position());
            float f = -((float) Mth.atan2(subtract.x, subtract.z)) * 180.0F / (float) Math.PI;
            for (int i = 0; i < 3; ++i) {
                LicowitchServantPeppermint peppermint = new LicowitchServantPeppermint(AcEntityRegistry.LICOWITCH_SERVANT_PEPPERMINT.get(), LicowitchServant.this.level());
                peppermint.setPos(LicowitchServant.this.getStaffPosition());
                peppermint.setStraight(!spinning);
                peppermint.setYRot(180.0F + f + (float) ((i - 1) * 30));
                peppermint.setSpinSpeed(spinning ? 12.0F : 8.0F);
                peppermint.setSpinRadius(3.5F);
                peppermint.setOwner(LicowitchServant.this);
                peppermint.setStartAngle((float) (i * 360) / 3.0F);
                LicowitchServant.this.level().addFreshEntity(peppermint);
            }
            LicowitchServant.this.level().playSound(null, LicowitchServant.this.blockPosition(),
                    ACSoundRegistry.LICOWITCH_CAST_PEPPERMINT.get(), LicowitchServant.this.getSoundSource(),
                    0.3F, 0.9F + LicowitchServant.this.level().random.nextFloat() * 0.2F);
        }
    }

    private static boolean canWitchUseCrucibleAt(Level world, BlockPos pos, boolean inUse) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConversionCrucibleBlockEntity crucible) {
            if (crucible.isWitchMode() && inUse) {
                return true;
            }
            if (crucible.getConvertingToBiome() == null && crucible.getFilledLevel() == 0) {
                return !crucible.isWitchMode();
            }
        }
        return false;
    }

    private static List<BlockPos> getNearbyCrucibles(Level world, BlockPos origin, int range) {
        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getPoiManager().findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.CONVERSION_CRUCIBLE.getKey()),
                            pos -> LicowitchServant.canWitchUseCrucibleAt(world, pos, true), origin, range, PoiManager.Occupancy.ANY)
                    .sorted(Comparator.comparingDouble(origin::distSqr)).toList();
        }
        return List.of();
    }

    private class LicowitchUseCrucibleGoal extends Goal {

        private BlockPos cruciblePos;
        private int executionCooldown = 10;
        private int cookTime = 0;
        private ItemEntity tossedItem;
        private int tossItemCooldown = 0;
        private int checkReachCooldown = 0;

        private LicowitchUseCrucibleGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = LicowitchServant.this.getTarget();
            if (target != null && target.isAlive() || LicowitchServant.this.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
                return false;
            }
            if (LicowitchServant.this.getTrueOwner() != null
                    && (LicowitchServant.this.isStaying() || LicowitchServant.this.isCommanded())) {
                return false;
            }
            if (this.executionCooldown-- > 0) {
                return false;
            }
            this.executionCooldown = 350 + LicowitchServant.this.getRandom().nextInt(200);
            BlockPos pos = null;
            if (this.cruciblePos != null) {
                if (LicowitchServant.canWitchUseCrucibleAt(LicowitchServant.this.level(), this.cruciblePos, true)) {
                    this.executionCooldown = 10;
                    pos = this.cruciblePos;
                } else {
                    this.cruciblePos = null;
                }
            }
            if (pos == null) {
                List<BlockPos> list = LicowitchServant.getNearbyCrucibles(LicowitchServant.this.level(),
                        LicowitchServant.this.blockPosition(), 32);
                if (!list.isEmpty()) {
                    pos = list.get(0);
                }
            }
            if (pos != null && LicowitchServant.this.getRandom().nextInt(4) == 0) {
                this.cruciblePos = pos;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = LicowitchServant.this.getTarget();
            if (LicowitchServant.this.getTrueOwner() != null
                    && (LicowitchServant.this.isStaying() || LicowitchServant.this.isCommanded())) {
                return false;
            }
            return this.cruciblePos != null
                    && LicowitchServant.canWitchUseCrucibleAt(LicowitchServant.this.level(), this.cruciblePos, true)
                    && (target == null || !target.isAlive()) && this.cookTime < 300;
        }

        @Override
        public void start() {
            LicowitchServant.this.updateHeldItems = false;
            LicowitchServant.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            LicowitchServant.this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }

        @Override
        public void stop() {
            this.cookTime = 0;
            this.tossItemCooldown = 0;
            this.checkReachCooldown = 0;
            LicowitchServant.this.updateHeldItems = true;
            LicowitchServant.this.updateFoldedArms = true;
            if (this.tossedItem != null) {
                this.tossedItem.discard();
                this.tossedItem = null;
            }
        }

        @Override
        public void tick() {
            if (this.tossItemCooldown > 0) {
                this.tossItemCooldown--;
            }
            if (this.checkReachCooldown > 0) {
                this.checkReachCooldown--;
            }
            if (this.checkReachCooldown == 0) {
                this.checkReachCooldown = 100 + LicowitchServant.this.getRandom().nextInt(40);
                if (LicowitchServant.this.canTeleport() && !LicowitchServant.this.canReach(this.cruciblePos)) {
                    LicowitchServant.this.setTeleportingToPos(Vec3.atCenterOf(this.cruciblePos
                            .relative(Direction.from2DDataValue(LicowitchServant.this.getRandom().nextInt(4)))));
                    return;
                }
            }
            Vec3 center = Vec3.atCenterOf(this.cruciblePos);
            double distance = Vec3.atBottomCenterOf(this.cruciblePos).subtract(LicowitchServant.this.position()).horizontalDistance();
            if (distance < 8.0D) {
                LicowitchServant.this.getLookControl().setLookAt(center.x,
                        center.y + 1.0D - Math.sin(this.cookTime * 0.2F) * 0.2D, center.z, 10.0F,
                        (float) LicowitchServant.this.getMaxHeadXRot());
            }
            if (distance > 2.5D) {
                LicowitchServant.this.getNavigation().moveTo((double) (this.cruciblePos.getX() + 0.5F),
                        (double) this.cruciblePos.getY(), (double) (this.cruciblePos.getZ() + 0.5F), 1.0D);
            } else {
                if (distance < 1.25D) {
                    LicowitchServant.this.getMoveControl().strafe(0.1F, 0.0F);
                } else {
                    LicowitchServant.this.getMoveControl().strafe(0.0F, 0.0F);
                }
                LicowitchServant.this.getNavigation().stop();
                BlockEntity blockEntity = LicowitchServant.this.level().getBlockEntity(this.cruciblePos);
                if (blockEntity instanceof ConversionCrucibleBlockEntity crucibleBlock) {
                    if (this.hasLineOfSightCrucible()) {
                        LicowitchServant.this.updateHeldItems = false;
                        boolean flag = crucibleBlock.isWitchMode();
                        crucibleBlock.setWitchModeDuration(10);
                        if (!flag && crucibleBlock.isWitchMode()) {
                            crucibleBlock.rerollWantedItem();
                            crucibleBlock.markUpdated();
                        }
                        if (LicowitchServant.this.areArmsVisuallyCrossed(1.0F)
                                && LicowitchServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                                && !crucibleBlock.getWantItem().isEmpty()
                                && crucibleBlock.getItemDisplayProgress(1.0F) >= 1.0F) {
                            LicowitchServant.this.setItemInHand(InteractionHand.MAIN_HAND, crucibleBlock.getWantItem().copy());
                        }
                        if (!crucibleBlock.getWantItem().isEmpty() && crucibleBlock.getItemDisplayProgress(1.0F) >= 1.0F
                                && this.tossItemCooldown == 0) {
                            if (this.tossedItem != null) {
                                this.tossedItem.discard();
                            }
                            if (LicowitchServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                                LicowitchServant.this.syncAnimation(LicowitchServant.this.getMainArm() == HumanoidArm.RIGHT
                                        ? ANIMATION_SWING_RIGHT : ANIMATION_SWING_LEFT);
                            } else if ((LicowitchServant.this.getAnimation() == ANIMATION_SWING_RIGHT
                                    || LicowitchServant.this.getAnimation() == ANIMATION_SWING_LEFT)
                                    && LicowitchServant.this.getAnimationTick() == 4) {
                                LicowitchServant.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                Vec3 from = LicowitchServant.this.getSwingArmPosition();
                                ItemStack stack = crucibleBlock.getWantItem().copy();
                                this.tossedItem = new ItemEntity(LicowitchServant.this.level(), from.x, from.y, from.z, stack);
                                Vec3 delta = center.subtract(from).normalize().scale(0.13D).add(0.0D, 0.4D, 0.0D);
                                this.tossedItem.setNeverPickUp();
                                this.tossedItem.setDeltaMovement(delta);
                                this.tossedItem.checkDespawn();
                                LicowitchServant.this.level().addFreshEntity(this.tossedItem);
                                this.tossItemCooldown = 50;
                            }
                        }
                        ++this.cookTime;
                    }
                }
            }
        }

        private boolean hasLineOfSightCrucible() {
            BlockHitResult result = LicowitchServant.this.level().clip(new ClipContext(
                    LicowitchServant.this.getEyePosition(1.0F),
                    new Vec3(this.cruciblePos.getX() + 0.5D, this.cruciblePos.getY() + 0.5D, this.cruciblePos.getZ() + 0.5D),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, LicowitchServant.this));
            BlockPos pos = result.getBlockPos();
            return pos.equals(this.cruciblePos) || LicowitchServant.this.level().isEmptyBlock(pos)
                    || LicowitchServant.this.level().getBlockEntity(pos) == LicowitchServant.this.level().getBlockEntity(this.cruciblePos);
        }
    }
}
