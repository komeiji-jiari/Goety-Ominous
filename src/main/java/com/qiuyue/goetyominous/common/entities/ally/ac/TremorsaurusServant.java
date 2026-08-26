package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolver;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantBreedGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantLayEggGoal;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TremorsaurusServant extends AbstractDinosaurServant implements KeybindUsingMount, IAnimatedEntity, ShakesScreen, LaysEggs, PlayerRideable {

    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(TremorsaurusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(TremorsaurusServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(TremorsaurusServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG = SynchedEntityData.defineId(TremorsaurusServant.class, EntityDataSerializers.BOOLEAN);
    public final LegSolver legSolver = new LegSolver(new LegSolver.Leg(-0.45F, 0.75F, 1.0F, false), new LegSolver.Leg(-0.45F, -0.75F, 1.0F, false));
    public static final Animation ANIMATION_SNIFF = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(55);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SHAKE_PREY = Animation.create(40);
    private Animation currentAnimation;
    private int animationTick;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private float prevSitProgress;
    private float sitProgress;
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;
    private int lastScareTimestamp = 0;
    private boolean hasRunningAttributes = false;
    private int roarCooldown = 0;
    private double lastStompX = 0;
    private double lastStompZ = 0;
    private int roarScatterTime = 0;
    private Entity riderHitEntity = null;

    public TremorsaurusServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TremorsaurusServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TremorsaurusServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TremorsaurusServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TremorsaurusServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TremorsaurusServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.TremorsaurusServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof TremorsaurusServant servant) {
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
            if (countServants(player) >= MobsConfig.TremorsaurusServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof TremorsaurusServant servant) {
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
        this.entityData.define(RUNNING, false);
        this.entityData.define(HELD_MOB_ID, -1);
        this.entityData.define(METER_AMOUNT, 1.0F);
        this.entityData.define(DATA_HAS_EGG, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TremorsaurusServantMeleeAttackGoal());
        this.goalSelector.addGoal(2, new ServantBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(3, new ServantLayEggGoal<>(this, (DinosaurEggBlock) AcBlockRegistry.TREMORSAURUS_SERVANT_EGG.get(), 100, 1.0D));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.prevBuryEggsProgress = this.buryEggsProgress;
        this.prevScreenShakeAmount = screenShakeAmount;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.yBodyRot, (float) this.getHeadRotSpeed());
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.prevSitProgress = this.sitProgress;
        if (this.isStaying() && this.sitProgress < 10.0F) {
            ++this.sitProgress;
        }
        if (!this.isStaying() && this.sitProgress > 0.0F) {
            --this.sitProgress;
        }
        // 与原版 DinosaurEntity 一致：下蛋埋蛋期间 buryEggsProgress 升至 5（乘 0.2 后为 0~1），结束后回落
        if (this.buryingEggs && this.buryEggsProgress < 5.0F) {
            ++this.buryEggsProgress;
        }
        if (!this.buryingEggs && this.buryEggsProgress > 0.0F) {
            --this.buryEggsProgress;
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        if (this.onGround() && !this.isInFluidType() && this.walkAnimation.speed() > 0.1F && !this.isBaby()) {
            float f = (float) Math.cos(this.walkAnimation.position() * 0.8F - 1.5F);
            if (Math.abs(f) < 0.2) {
                if (screenShakeAmount <= 0.3) {
                    this.playSound(ACSoundRegistry.TREMORSAURUS_STOMP.get(), 2, 1.0F);
                    this.shakeWater();
                }
                screenShakeAmount = 1F;
            }
        }
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(getRunningSpeed());
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.TremorsaurusServantMovementSpeed.get());
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5 && !this.level().isClientSide) {
            this.playRoarSound();
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 40 && !this.isBaby()) {
            screenShakeAmount = 1F;
            roarScatterTime = 30;
            if (this.getAnimationTick() % 5 == 0 && level().isClientSide) {
                this.shakeWater();
            }
        }
        if (roarScatterTime > 0) {
            roarScatterTime--;
            scareMobs();
        }
        if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 5 && !this.level().isClientSide) {
            actuallyPlayAmbientSound();
        }
        if (!level().isClientSide) {
            if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION && !this.isStaying()) {
                if (random.nextInt(180) == 0) {
                    this.syncAnimation(ANIMATION_SNIFF);
                }
                if (random.nextInt(600) == 0 && !this.isVehicle()) {
                    this.tryRoar();
                }
            }
            boolean held = false;
            if (riderHitEntity != null && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() > 10 && this.getAnimationTick() <= 12) {
                if (this.hasLineOfSight(riderHitEntity) && this.distanceTo(riderHitEntity) < this.getBbWidth() + riderHitEntity.getBbWidth() + 2.0D) {
                    riderHitEntity.hurt(riderHitEntity.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    if (riderHitEntity instanceof LivingEntity living) {
                        living.knockback(0.5D, this.getX() - riderHitEntity.getX(), this.getZ() - riderHitEntity.getZ());
                    }
                    riderHitEntity = null;
                }
            }
            LivingEntity target = riderHitEntity instanceof LivingEntity ? (LivingEntity) riderHitEntity : this.getTarget();
            if (target != null && target.isAlive() && target.distanceTo(this) < (isVehicle() ? 10.0F : 5.5F)) {
                if (this.getAnimation() == ANIMATION_SHAKE_PREY && this.getAnimationTick() <= 35) {
                    held = true;
                    this.setHeldMobId(target.getId());
                }
            }
            if (!held && getHeldMobId() != -1) {
                this.setHeldMobId(-1);
                this.playSound(ACSoundRegistry.TREMORSAURUS_THROW.get());
                riderHitEntity = null;
            }
        } else {
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && getMeterAmount() >= 1.0F) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
                if (AlexsCaves.PROXY.isKeyDown(3) && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 3));
                }
            }
        }
        if (this.isVehicle()) {
            if (this.getMeterAmount() < 1.0F) {
                this.setMeterAmount(Math.min(this.getMeterAmount() + 0.0035F, 1.0F));
            }
        } else {
            this.setMeterAmount(0.0F);
        }
        if (this.getAnimation() == ANIMATION_SHAKE_PREY && getHeldMobId() != -1) {
            Entity entity = level().getEntity(getHeldMobId());
            if (entity != null) {
                if (this.getAnimationTick() <= 35) {
                    Vec3 shakePreyPos = getShakePreyPos();
                    Vec3 minus = new Vec3(shakePreyPos.x - entity.getX(), shakePreyPos.y - entity.getY(), shakePreyPos.z - entity.getZ());
                    entity.setDeltaMovement(minus);
                    if (this.getAnimationTick() % 10 == 0) {
                        entity.hurt(damageSources().mobAttack(this), 5 + this.getRandom().nextInt(2));
                    }
                } else {
                    entity.setDeltaMovement(entity.getDeltaMovement().scale(0.6F));
                }
            }
        }
        if (roarCooldown > 0) {
            roarCooldown--;
        }
        lastStompX = this.getX();
        lastStompZ = this.getZ();
    }

    public double getRunningSpeed() {
        return AttributesConfig.TremorsaurusServantMovementSpeed.get() * 1.75D;
    }

    private void playRoarSound() {
        if (this.isBaby()) {
            this.playSound(ACSoundRegistry.TREMORSAURUS_ROAR.get(), 1.0F, 1.5F);
        } else {
            this.playSound(ACSoundRegistry.TREMORSAURUS_ROAR.get(), 4.0F, 1.0F);
        }
    }

    private void scareMobs() {
        if (this.tickCount - lastScareTimestamp > 3) {
            lastScareTimestamp = this.tickCount;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(30, 10, 30));
        for (LivingEntity e : list) {
            if (!e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR) && !isAlliedTo(e)) {
                if (e instanceof PathfinderMob mob && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isInSittingPose())) {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                    if (mob.onGround()) {
                        Vec3 randomShake = new Vec3(random.nextFloat() - 0.5F, 0, random.nextFloat() - 0.5F).scale(0.1F);
                        mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.7F, 1, 0.7F).add(randomShake));
                    }
                    if (lastScareTimestamp == tickCount) {
                        mob.getNavigation().stop();
                    }
                    if (mob.getNavigation().isDone()) {
                        Vec3 vec = LandRandomPos.getPosAway(mob, 15, 7, this.position());
                        if (vec != null) {
                            mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 2D);
                        }
                    }
                }
                if (this.getTrueOwner() != null) {
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
                }
            }

        }
    }

    private void shakeWater() {
        if (level().isClientSide) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int radius = 8;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        mutableBlockPos.set(this.getX() + x, this.getY() + 5, this.getZ() + z);
                        while (mutableBlockPos.getY() > level().getMinBuildHeight() && level().getBlockState(mutableBlockPos).isAir()) {
                            mutableBlockPos.move(Direction.DOWN);
                        }
                        float water = getWaterLevelForBlock(level(), mutableBlockPos);
                        if (water > 0.0F) {
                            level().addParticle(ACParticleRegistry.WATER_TREMOR.get(), mutableBlockPos.getX() + 0.5F, mutableBlockPos.getY() + water + 0.01, mutableBlockPos.getZ() + 0.5F, 0, 0, 0);
                        }

                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public float getScreenShakeAmount(float partialTicks) {
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public Vec3 getShakePreyPos() {
        Vec3 jaw = new Vec3(0, -0.75, 3F);
        if (this.getAnimation() == ANIMATION_SHAKE_PREY) {
            if (this.getAnimationTick() <= 5) {
                jaw = jaw.subtract(0, 1.5F * (getAnimationTick() / 5F), 0);
            } else if (this.getAnimationTick() < 35) {
                jaw = jaw.yRot(0.8F * (float) Math.cos(this.tickCount * 0.6F));
            }
        }
        Vec3 head = jaw.xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.getEyePosition().add(head);
    }

    public void tryRoar() {
        if (roarCooldown == 0 && this.getAnimation() == NO_ANIMATION) {
            this.syncAnimation(ANIMATION_ROAR);
            this.roarCooldown = 200 + random.nextInt(200);
        }
    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon mob) {
        TremorsaurusServant baby = AcEntityRegistry.TREMORSAURUS_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimalSummon partner) {
        this.setHasEgg(true);
        this.finalizeSpawnChildFromBreeding(level, partner, partner);
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
    public BlockState createEggBlockState() {
        return AcBlockRegistry.TREMORSAURUS_SERVANT_EGG.get().defaultBlockState();
    }

    @Override
    public void onLayEggTick(BlockPos belowEgg, int time) {
        this.walkAnimation.update(0.5F, 0.4F);
        this.level().broadcastEntityEvent(this, (byte) 77);
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 77) {
            // 与原版 DinosaurEntity 一致：下蛋站立期间向四周扬起地面碎屑，并进入埋蛋姿态
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
            // 与原版 DinosaurEntity 一致：下蛋结束，复位埋蛋姿态
            this.buryingEggs = false;
        } else {
            super.handleEntityEvent(b);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasEgg(tag.getBoolean("HasEgg"));
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_ROAR || this.getAnimation() == ANIMATION_SHAKE_PREY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStompX, 0, this.getZ() - this.lastStompZ);
        float walkSpeed = 4.0F;
        if (isVehicle()) {
            walkSpeed = 1.5F;
        } else if (isRunning()) {
            walkSpeed = 2.0F;
        }
        float f2 = Math.min(f1 * walkSpeed, 1.0F);
        walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.syncAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
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
        return new Animation[]{ANIMATION_SNIFF, ANIMATION_SPEAK, ANIMATION_ROAR, ANIMATION_BITE, ANIMATION_SHAKE_PREY};
    }

    private float getWaterLevelForBlock(Level level, BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (state.is(Blocks.WATER_CAULDRON)) {
            return (6.0F + (float) state.getValue(LayeredCauldronBlock.LEVEL).intValue() * 3.0F) / 16.0F;
        } else if (random.nextFloat() < 0.33F && state.getFluidState().is(FluidTags.WATER)) {
            return state.getFluidState().getHeight(level, pos);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player;
    }

    protected void clampRotation(LivingEntity livingEntity, float clampRange) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -clampRange, clampRange);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living) {
            if (!this.touchingUnloadedChunk()) {
                passenger.setYBodyRot(this.yBodyRot);
                passenger.fallDistance = 0.0F;
                this.clampRotation(living, 105.0F);
                Vec3 seatOffset = new Vec3(0F, 0.1F, 0.6F).yRot((float) Math.toRadians(-this.yBodyRot));
                float heightBackLeft = legSolver.legs[0].getHeight(1.0F);
                float heightBackRight = legSolver.legs[1].getHeight(1.0F);
                float maxLegSolverHeight = (1F - ACMath.smin(1F - heightBackLeft, 1F - heightBackRight, 0.1F)) * 0.8F;
                moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - maxLegSolverHeight, this.getZ() + seatOffset.z);
                return;
            }
        }
        super.positionRider(passenger, moveFunction);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0 || player.xxa != 0) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.yBodyRot = this.yHeadRot = this.getYRot();
            this.yRotO = this.yHeadRot;
            this.getNavigation().stop();
            this.setTarget(null);
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED));
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

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
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
                        this.heal(15.0F);
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
        return stack.is(ACBlockRegistry.COOKED_DINOSAUR_CHOP.get().asItem()) || stack.is(ACBlockRegistry.DINOSAUR_CHOP.get().asItem());
    }

    public boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return ownerPlayer.isShiftKeyDown();
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float roarPower) {
        this.entityData.set(METER_AMOUNT, roarPower);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    this.yBodyRot = keyPresser.getYHeadRot();
                    this.setYRot(keyPresser.getYHeadRot());
                    this.syncAnimation(ANIMATION_ROAR);
                    this.setMeterAmount(0.0F);
                }
            }
            if (type == 3) {
                if (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null) {
                    HitResult hitresult = ProjectileUtil.getHitResultOnViewVector(keyPresser, entity -> !entity.is(this) && !this.isAlliedTo(entity), 10.0F);
                    this.setYHeadRot(keyPresser.getYHeadRot());
                    this.setXRot(keyPresser.getXRot());
                    boolean flag = false;
                    if (hitresult instanceof EntityHitResult entityHitResult) {
                        riderHitEntity = entityHitResult.getEntity();
                        if (this.getRandom().nextBoolean() && riderHitEntity.getBbWidth() < 2.0F || riderHitEntity instanceof FlyingAnimal) {
                            flag = true;
                        }
                    } else {
                        riderHitEntity = null;
                    }
                    this.syncAnimation(flag ? ANIMATION_SHAKE_PREY : ANIMATION_BITE);
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TREMORSAURUS_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TREMORSAURUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TREMORSAURUS_DEATH.get();
    }

    public float getSitProgress(float partialTicks) {
        return (this.prevSitProgress + (this.sitProgress - this.prevSitProgress) * partialTicks) / 10.0F;
    }

    // 与原版 DinosaurEntity 一致：埋蛋进度 0~5，*0.2 后供模型驱动埋蛋扭动姿态
    public float getBuryEggsProgress(float partialTicks) {
        return (this.prevBuryEggsProgress + (this.buryEggsProgress - this.prevBuryEggsProgress) * partialTicks) * 0.2F;
    }

    private class TremorsaurusServantMeleeAttackGoal extends Goal {
        private TremorsaurusServantMeleeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = TremorsaurusServant.this.getTarget();
            return target != null && target.isAlive() && !TremorsaurusServant.this.isBaby();
        }

        @Override
        public void start() {
            TremorsaurusServant.this.setRunning(!TremorsaurusServant.this.isVehicle());
        }

        @Override
        public void stop() {
            TremorsaurusServant.this.setRunning(false);
        }

        @Override
        public void tick() {
            LivingEntity target = TremorsaurusServant.this.getTarget();
            if (target != null) {
                // 与原版 TremorsaurusMeleeGoal 一致：小体型目标仅 50% 概率抓取甩头，否则咬击；飞行目标必抓
                boolean grab = isFlyingTarget(target) || (TremorsaurusServant.this.getRandom().nextBoolean() && Math.max(target.getBbHeight(), target.getBbWidth()) < 2.0F);
                TremorsaurusServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (!TremorsaurusServant.this.isVehicle()) {
                    TremorsaurusServant.this.tryRoar();
                }
                double dist = TremorsaurusServant.this.distanceTo(target);
                TremorsaurusServant.this.getNavigation().moveTo(target, 1.0F);
                if (dist < TremorsaurusServant.this.getBbWidth() + target.getBbWidth() + 1.0F && TremorsaurusServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    if (grab && !TremorsaurusServant.this.isBaby()) {
                        tryAnimation(ANIMATION_SHAKE_PREY);
                    } else {
                        tryAnimation(ANIMATION_BITE);
                    }
                }
                if (TremorsaurusServant.this.getAnimation() == ANIMATION_BITE && TremorsaurusServant.this.getAnimationTick() > 10 && TremorsaurusServant.this.getAnimationTick() <= 12) {
                    checkAndDealDamage(target);
                }
            }
        }

        private void checkAndDealDamage(LivingEntity target) {
            if (TremorsaurusServant.this.hasLineOfSight(target) && TremorsaurusServant.this.distanceTo(target) < TremorsaurusServant.this.getBbWidth() + target.getBbWidth() + 2.0F) {
                TremorsaurusServant.this.playSound(ACSoundRegistry.TREMORSAURUS_BITE.get());
                target.hurt(target.damageSources().mobAttack(TremorsaurusServant.this), (float) TremorsaurusServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                target.knockback(0.5F, TremorsaurusServant.this.getX() - target.getX(), TremorsaurusServant.this.getZ() - target.getZ());
            }
        }

        private boolean isFlyingTarget(LivingEntity target) {
            return target instanceof FlyingAnimal;
        }

        private void tryAnimation(Animation animation) {
            if (TremorsaurusServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                TremorsaurusServant.this.syncAnimation(animation);
            }
        }
    }
}
