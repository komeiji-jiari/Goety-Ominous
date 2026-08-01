package com.qiuyue.goetyominous.common.entities.ally.ua;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.SummonedFlying;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.teamabnormals.upgrade_aquatic.core.registry.UAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class FlareServant extends SummonedFlying {
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(FlareServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> VOID_STAFF_SUMMONED = SynchedEntityData.defineId(FlareServant.class, EntityDataSerializers.BOOLEAN);
    private Vec3 orbitOffset = Vec3.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;
    private AttackPhase attackPhase = AttackPhase.CIRCLE;

    public FlareServant(EntityType<? extends SummonedFlying> type, Level world) {
        super(type, world);
        this.xpReward = 0;
        this.moveControl = new MoveHelperController(this);
        this.lookControl = new LookHelperController(this);
    }

    protected BodyRotationControl createBodyControl() {
        return new BodyHelperController(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PickAttackGoal());
        this.goalSelector.addGoal(2, new SweepAttackGoal());
        this.goalSelector.addGoal(3, new OrbitPointGoal());
        this.targetSelector.addGoal(1, new AttackLivingEntityGoal());
    }

    @Override
    public void followGoal() {
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(1, new AttackLivingEntityGoal());
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FlareServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.FlareServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FlareServantDamage.get())
                .add(Attributes.FLYING_SPEED, AttributesConfig.FlareServantFlyingSpeed.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FlareServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FlareServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0);
        this.entityData.define(VOID_STAFF_SUMMONED, false);
    }

    public void setPhantomSize(int sizeIn) {
        this.entityData.set(SIZE, Mth.clamp(sizeIn, 0, 64));
    }

    private void updatePhantomSize() {
        this.refreshDimensions();
        double attackDamage = 8.0D + this.getPhantomSize();
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        }
    }

    public int getPhantomSize() {
        return this.entityData.get(SIZE);
    }

    public void setVoidStaffSummoned(boolean summoned) {
        this.entityData.set(VOID_STAFF_SUMMONED, summoned);
    }

    public boolean isVoidStaffSummoned() {
        return this.entityData.get(VOID_STAFF_SUMMONED);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SIZE.equals(key)) {
            this.updatePhantomSize();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public boolean isInvisible() {
        if (this.isUpgraded()) {
            return true;
        }
        return super.isInvisible();
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        if (this.isUpgraded()) {
            return false;
        }
        return super.isInvisibleTo(p_20178_);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity livingEntity) {
            LivingEntity owner = this.getTrueOwner();
            if (owner != null) {
                boolean hasVoidSet = CuriosFinder.hasVoidSet(owner);

                if (hasVoidSet && this.isVoidStaffSummoned()) {
                    int amplifier = 1;
                    int duration = MathHelper.secondsToTicks(10);

                    if (!livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                        livingEntity.addEffect(new MobEffectInstance(
                                GoetyEffects.VOID_TOUCHED.get(),
                                duration,
                                amplifier,
                                false,
                                true
                        ));
                    }
                } else if (hasVoidSet) {
                    int amplifier = 0;
                    int duration = MathHelper.secondsToTicks(10);

                    if (!livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                        livingEntity.addEffect(new MobEffectInstance(
                                GoetyEffects.VOID_TOUCHED.get(),
                                duration,
                                amplifier,
                                false,
                                true
                        ));
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isUpgraded()) {
            this.noPhysics = true;
        }

        if (!this.level().isClientSide && this.isAlive()) {
            MobEffectInstance restfulnessEffect = this.getEffect(UAMobEffects.RESTFULNESS.get());

            if (restfulnessEffect != null) {
                this.convertToPhantom();
                return;
            }

            MobEffectInstance myInsomnia = this.getEffect(UAMobEffects.INSOMNIA.get());
            if (myInsomnia != null) {
                this.discard();
                return;
            }
        }

        if (this.level().isClientSide) {
            float f = Mth.cos((float) (this.getId() * 3 + this.tickCount) * 0.13F + (float) Math.PI);
            float f1 = Mth.cos((float) (this.getId() * 3 + this.tickCount + 1) * 0.13F + (float) Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F * (float) i);
            float f3 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);
            this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (double) f2, this.getY() + (double) f4, this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            this.level().addParticle(ParticleTypes.PORTAL, this.getX() - (double) f2, this.getY() + (double) f4, this.getZ() - (double) f3, 0.0D, 0.0D, 0.0D);
        }
    }

    private void convertToPhantom() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        com.Polarice3.Goety.common.entities.ally.undead.PhantomServant phantom =
                com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get().create(serverLevel);
        if (phantom != null) {
            phantom.setTrueOwner(this.getTrueOwner());
            phantom.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            phantom.setPhantomSize(this.getPhantomSize());

            if (this.hasCustomName()) {
                phantom.setCustomName(this.getCustomName());
            }

            serverLevel.addFreshEntity(phantom);
            this.discard();

            for (int i = 0; i < 20; ++i) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag pDataTag) {
        this.orbitPosition = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, pDataTag);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AX")) {
            this.orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }
        this.setPhantomSize(compound.getInt("Size"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AX", this.orbitPosition.getX());
        compound.putInt("AY", this.orbitPosition.getY());
        compound.putInt("AZ", this.orbitPosition.getZ());
        compound.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean canAttackType(EntityType<?> typeIn) {
        return true;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(com.qiuyue.goetyominous.common.items.ua.UaItems.FLARE_SERVANT_SPAWN_EGG.get());
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    enum AttackPhase {
        CIRCLE,
        SWOOP
    }

    public class AttackLivingEntityGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int tickDelay = 20;

        private AttackLivingEntityGoal() {
        }

        public boolean canUse() {
            if (FlareServant.this.getTrueOwner() == null) {
                return false;
            }

            if (this.tickDelay > 0) {
                --this.tickDelay;
                return false;
            } else {
                this.tickDelay = 20;
                List<LivingEntity> list = FlareServant.this.level().getNearbyEntities(LivingEntity.class, this.attackTargeting, FlareServant.this, FlareServant.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    for (LivingEntity mob : list) {
                        if (mob == FlareServant.this.getTrueOwner()) {
                            continue;
                        }

                        if (MobUtil.areAllies(FlareServant.this, mob)) {
                            continue;
                        }

                        if (FlareServant.this.canAttack(mob, TargetingConditions.DEFAULT)) {
                            FlareServant.this.setTarget(mob);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = FlareServant.this.getTarget();
            if (livingentity == null) {
                return false;
            }

            if (livingentity == FlareServant.this.getTrueOwner()) {
                return false;
            }

            if (MobUtil.areAllies(FlareServant.this, livingentity)) {
                return false;
            }

            return FlareServant.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    class BodyHelperController extends BodyRotationControl {
        public BodyHelperController(Mob p_i49925_2_) {
            super(p_i49925_2_);
        }

        public void clientTick() {
            FlareServant.this.yHeadRot = FlareServant.this.yBodyRot;
            FlareServant.this.yBodyRot = FlareServant.this.getYRot();
        }
    }

    class LookHelperController extends LookControl {
        public LookHelperController(Mob entityIn) {
            super(entityIn);
        }

        public void tick() {
        }
    }

    abstract class MoveGoal extends Goal {
        public MoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return FlareServant.this.orbitOffset.distanceToSqr(FlareServant.this.getX(), FlareServant.this.getY(), FlareServant.this.getZ()) < 4.0D;
        }
    }

    class MoveHelperController extends MoveControl {
        private float speedFactor = 0.1F;

        public MoveHelperController(Mob entityIn) {
            super(entityIn);
        }

        public void tick() {
            if (FlareServant.this.horizontalCollision) {
                FlareServant.this.setYRot(FlareServant.this.getYRot() + 180.0F);
                this.speedFactor = 0.1F;
            }

            float f = (float) (FlareServant.this.orbitOffset.x - FlareServant.this.getX());
            float f1 = (float) (FlareServant.this.orbitOffset.y - FlareServant.this.getY());
            float f2 = (float) (FlareServant.this.orbitOffset.z - FlareServant.this.getZ());
            double d0 = Mth.sqrt(f * f + f2 * f2);
            double d1 = 1.0D - (double) Mth.abs(f1 * 0.7F) / d0;
            f = (float) ((double) f * d1);
            f2 = (float) ((double) f2 * d1);
            d0 = Mth.sqrt(f * f + f2 * f2);
            double d2 = Mth.sqrt(f * f + f2 * f2 + f1 * f1);
            float f3 = FlareServant.this.getYRot();
            float f4 = (float) Mth.atan2(f2, f);
            float f5 = Mth.wrapDegrees(FlareServant.this.getYRot() + 90.0F);
            float f6 = Mth.wrapDegrees(f4 * (180F / (float) Math.PI));
            FlareServant.this.setYRot(Mth.approachDegrees(f5, f6, 4.0F) - 90.0F);
            FlareServant.this.yBodyRot = FlareServant.this.getYRot();
            if (Mth.degreesDifferenceAbs(f3, FlareServant.this.getYRot()) < 3.0F) {
                this.speedFactor = Mth.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
            } else {
                this.speedFactor = Mth.approach(this.speedFactor, 0.2F, 0.025F);
            }

            float f7 = (float) (-(Mth.atan2(-f1, d0) * (double) (180F / (float) Math.PI)));
            FlareServant.this.setXRot(f7);
            float f8 = FlareServant.this.getYRot() + 90.0F;
            double d3 = (double) (this.speedFactor * Mth.cos(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f / d2);
            double d4 = (double) (this.speedFactor * Mth.sin(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f2 / d2);
            double d5 = (double) (this.speedFactor * Mth.sin(f7 * ((float) Math.PI / 180F))) * Math.abs((double) f1 / d2);
            Vec3 vec3d = FlareServant.this.getDeltaMovement();
            FlareServant.this.setDeltaMovement(vec3d.add((new Vec3(d3, d5, d4)).subtract(vec3d).scale(0.2D)));
        }
    }

    class OrbitPointGoal extends MoveGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        private OrbitPointGoal() {
        }

        public boolean canUse() {
            return FlareServant.this.getTarget() == null || FlareServant.this.attackPhase == AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + FlareServant.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + FlareServant.this.random.nextFloat() * 9.0F;
            this.clockwise = FlareServant.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (FlareServant.this.random.nextInt(350) == 0) {
                this.height = -4.0F + FlareServant.this.random.nextFloat() * 9.0F;
            }

            if (FlareServant.this.random.nextInt(250) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (FlareServant.this.random.nextInt(450) == 0) {
                this.angle = FlareServant.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (FlareServant.this.orbitOffset.y < FlareServant.this.getY() && !FlareServant.this.level().isEmptyBlock(FlareServant.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (FlareServant.this.orbitOffset.y > FlareServant.this.getY() && !FlareServant.this.level().isEmptyBlock(FlareServant.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(FlareServant.this.orbitPosition)) {
                FlareServant.this.orbitPosition = FlareServant.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            FlareServant.this.orbitOffset = (new Vec3(FlareServant.this.orbitPosition.getX(), FlareServant.this.orbitPosition.getY(), FlareServant.this.orbitPosition.getZ())).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class PickAttackGoal extends Goal {
        private int tickDelay;

        private PickAttackGoal() {
        }

        public boolean canUse() {
            LivingEntity livingentity = FlareServant.this.getTarget();
            return livingentity != null && FlareServant.this.canAttack(FlareServant.this.getTarget(), TargetingConditions.DEFAULT);
        }

        public void start() {
            this.tickDelay = 10;
            FlareServant.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            FlareServant.this.orbitPosition = FlareServant.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, FlareServant.this.orbitPosition).above(10 + FlareServant.this.random.nextInt(20));
        }

        public void tick() {
            if (FlareServant.this.attackPhase == AttackPhase.CIRCLE) {
                --this.tickDelay;
                if (this.tickDelay <= 0) {
                    FlareServant.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.tickDelay = (8 + FlareServant.this.random.nextInt(4)) * 20;
                    FlareServant.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + FlareServant.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget() {
            FlareServant.this.orbitPosition = (FlareServant.this.getTarget().blockPosition()).above(20 + FlareServant.this.random.nextInt(20));
            if (FlareServant.this.orbitPosition.getY() < FlareServant.this.level().getSeaLevel()) {
                FlareServant.this.orbitPosition = new BlockPos(FlareServant.this.orbitPosition.getX(), FlareServant.this.level().getSeaLevel() + 1, FlareServant.this.orbitPosition.getZ());
            }
        }
    }

    class SweepAttackGoal extends MoveGoal {
        private SweepAttackGoal() {
        }

        public boolean canUse() {
            return FlareServant.this.getTarget() != null && FlareServant.this.attackPhase == AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = FlareServant.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative()) {
                if (!this.canUse()) {
                    return false;
                } else {
                    if (FlareServant.this.tickCount % 20 == 0) {
                        List<Cat> list = FlareServant.this.level().getEntitiesOfClass(Cat.class, FlareServant.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);
                        if (!list.isEmpty()) {
                            for (Cat catentity : list) {
                                catentity.hiss();
                            }

                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        }

        public void start() {
        }

        public void stop() {
            if (FlareServant.this.getTarget() instanceof Player) {
                FlareServant.this.setTarget(null);
            }
            FlareServant.this.attackPhase = AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = FlareServant.this.getTarget();
            FlareServant.this.orbitOffset = new Vec3(livingentity.getX(), livingentity.getY() + (double) livingentity.getBbHeight() * 0.5D, livingentity.getZ());
            if (FlareServant.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                FlareServant.this.doHurtTarget(livingentity);
                FlareServant.this.attackPhase = AttackPhase.CIRCLE;
                FlareServant.this.level().levelEvent(1039, FlareServant.this.blockPosition(), 0);
            } else if (FlareServant.this.horizontalCollision || FlareServant.this.hurtTime > 0) {
                FlareServant.this.attackPhase = AttackPhase.CIRCLE;
            }
        }
    }
}



