package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIMeleeNearby;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class EmuServant extends AnimalSummon implements IAnimatedEntity {

    public static final Animation ANIMATION_DODGE_LEFT = EntityEmu.ANIMATION_DODGE_LEFT;
    public static final Animation ANIMATION_DODGE_RIGHT = EntityEmu.ANIMATION_DODGE_RIGHT;
    public static final Animation ANIMATION_PECK_GROUND = EntityEmu.ANIMATION_PECK_GROUND;
    public static final Animation ANIMATION_SCRATCH = EntityEmu.ANIMATION_SCRATCH;
    public static final Animation ANIMATION_PUZZLED = EntityEmu.ANIMATION_PUZZLED;

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(EmuServant.class, EntityDataSerializers.INT);

    private int animationTick;
    private Animation currentAnimation;
    public int timeUntilNextEgg = this.random.nextInt(6000) + 6000;

    public EmuServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.EmuServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EmuServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.EmuServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.EmuServantFollowRange.get());
    }

    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.EMU_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.EMU_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.EMU_HURT.get();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.EmuServantLimit.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AnimalAIMeleeNearby(this, 15, 1.25D));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 1.0D, 110, 0.001F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        if (AMConfig.emuTargetSkeletons) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Pillager.class, false));
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.EMU_BREEDABLES);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isBaby() && super.canAttack(target);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SCRATCH);
        }
        return true;
    }

    @Override
    public void travel(Vec3 travelVector) {
        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)
                * (this.getAnimation() == ANIMATION_PECK_GROUND || this.getAnimation() == ANIMATION_PUZZLED ? 0.15F : 1.0F)
                * (this.isInLava() ? 0.2F : 1.0F));
        super.travel(travelVector);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getLastHurtByMob() == null && this.getTarget() == null
                    && this.getDeltaMovement().lengthSqr() < 0.03D
                    && this.getRandom().nextInt(190) == 0
                    && this.getAnimation() == NO_ANIMATION) {
                if (this.getRandom().nextInt(3) == 0) {
                    this.setAnimation(ANIMATION_PUZZLED);
                } else if (this.onGround()) {
                    this.setAnimation(ANIMATION_PECK_GROUND);
                }
            }
            LivingEntity target = this.getTarget();
            if (this.isAlive() && target != null && this.getAnimation() == ANIMATION_SCRATCH
                    && this.distanceTo(target) < 4.0F
                    && (this.getAnimationTick() == 8 || this.getAnimationTick() == 15)) {
                float f1 = this.getYRot() * ((float) Math.PI / 180F);
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
                target.knockback(0.4F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilNextEgg <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(AmItems.EMU_SERVANT_EGG.get());
            this.timeUntilNextEgg = this.random.nextInt(6000) + 6000;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag tag) {
        if (this.random.nextInt(200) == 0) {
            this.setVariant(2);
        } else if (this.random.nextInt(3) == 0) {
            this.setVariant(1);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
    }

    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon otherParent) {
        EmuServant baby = AmEntityRegistry.EMU_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
            baby.setVariant(this.getVariant());
        }
        return baby;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
        tag.putInt("EggLayTime", this.timeUntilNextEgg);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.contains("EggLayTime")) {
            this.timeUntilNextEgg = tag.getInt("EggLayTime");
        }
    }

    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getType().getDimensions().scale(this.getAgeScale());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_DODGE_LEFT, ANIMATION_DODGE_RIGHT, ANIMATION_PECK_GROUND, ANIMATION_SCRATCH, ANIMATION_PUZZLED};
    }
}
