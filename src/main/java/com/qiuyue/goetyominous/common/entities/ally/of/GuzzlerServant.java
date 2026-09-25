package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.GuzzlerServantAttackGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import com.unusualmodding.opposing_force.utils.OPMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class GuzzlerServant extends Summoned implements AttackState {
    private static final EntityDataAccessor<Integer> ATTACK_STATE;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState spewAnimationState = new AnimationState();
    public final AnimationState stompAnimationState = new AnimationState();

    public GuzzlerServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GuzzlerServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GuzzlerServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GuzzlerServantAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GuzzlerServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GuzzlerServantFollowRange.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GuzzlerServantAttackGoal(this, 22.0F));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 1.0D, 110, 0.001F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    @Override
    public float maxUpStep() {
        return 1.1F;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()
                && itemstack.is(Items.LAVA_BUCKET) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(10.0F);
                this.playSound(SoundEvents.BUCKET_EMPTY_LAVA, 1.0F, 1.0F);
                this.gameEvent(GameEvent.EAT, this);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 8; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D + 0.1D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                this.getRandomX(1.0F),
                                this.getY() + this.getBbHeight() + 0.3F + this.random.nextDouble() * 0.5F,
                                this.getRandomZ(1.0F), 0, d0, d1, d2, 0.5D);
                    }
                }
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
            }
            player.swing(hand);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.GuzzlerServantLimit.get();
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
    }

    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        this.spewAnimationState.animateWhen(this.getAttackState() == 1, this.tickCount);
        this.stompAnimationState.animateWhen(this.getAttackState() == 2, this.tickCount);
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float pos = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float speed = Math.min(pos * 10.0F, 1.0F);
        this.walkAnimation.update(speed, 0.4F);
    }

    private void guzzleEffect() {
        float random = this.random.nextFloat() * ((float) Math.PI * 2.0F);
        float random1 = this.random.nextFloat() * 1.4F + 1.4F;
        float x = Mth.sin(random) * 0.75F * random1;
        float y = Mth.sin(random) * 0.1F * random1;
        float z = Mth.cos(random) * 0.75F * random1;
        this.level().addParticle(ParticleTypes.FLAME,
                this.getX() + (double) x, this.getEyeY() - (double) y, this.getZ() + (double) z,
                0.0D, 0.0D, 0.0D);
    }

    private void stompEffect() {
        Vec3 groundedVec = OPMath.getGroundBelowPosition(this.level(),
                new Vec3(this.getRandomX(1.5D), this.getY() + 0.25D, this.getRandomZ(1.5D)));
        BlockPos ground = BlockPos.containing(groundedVec.add(0.0D, 0.5D, 0.0D));
        BlockState state = this.level().getBlockState(ground);
        for (int i = 0; i <= this.getRandom().nextInt(50) + 80; ++i) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true,
                    this.getRandomX(1.5D), this.getY() + 0.25D, this.getRandomZ(1.5D),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 39) {
            this.guzzleEffect();
        }
        if (id == 40) {
            this.stompEffect();
        }
        super.handleEntityEvent(id);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 140;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return OPSoundEvents.GUZZLER_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.GUZZLER_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.GUZZLER_DEATH.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 0.8F);
    }

    static {
        ATTACK_STATE = SynchedEntityData.defineId(GuzzlerServant.class, EntityDataSerializers.INT);
    }
}
