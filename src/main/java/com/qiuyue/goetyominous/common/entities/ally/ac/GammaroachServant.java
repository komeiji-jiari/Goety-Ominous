package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class GammaroachServant extends Summoned implements IAnimatedEntity {

    private Animation currentAnimation;
    private int animationTick;
    public static final Animation ANIMATION_SPRAY = Animation.create(40);
    public static final Animation ANIMATION_RAM = Animation.create(25);

    private static final EntityDataAccessor<Boolean> FED = SynchedEntityData.defineId(GammaroachServant.class, EntityDataSerializers.BOOLEAN);

    public GammaroachServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.1F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        // 用 Goety 的 WanderGoal(checkNoActionTime=false):非敌对 Summoned 的 noActionTime 永不复位,原版 RandomStrollGoal 空闲约5秒即被永久禁用而站桩。
        this.goalSelector.addGoal(3, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GammaroachServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GammaroachServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GammaroachServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GammaroachServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GammaroachServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.GammaroachServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FED, false);
    }

    public boolean isFed() {
        return this.entityData.get(FED);
    }

    public void setFed(boolean fed) {
        this.entityData.set(FED, fed);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.GammaroachServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof GammaroachServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.IRRADIATED.get();
    }

    public void tick() {
        super.tick();
        if (this.getAnimation() == ANIMATION_SPRAY) {
            if (this.getAnimationTick() == 10) {
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY() + 0.2F, this.getZ());
                areaeffectcloud.setParticle(ACParticleRegistry.GAMMAROACH.get());
                areaeffectcloud.setFixedColor(0X77D60E);
                areaeffectcloud.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000, 2));
                areaeffectcloud.setRadius(2.3F);
                areaeffectcloud.setDuration(200);
                areaeffectcloud.setWaitTime(10);
                areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());
                this.level().addFreshEntity(areaeffectcloud);
            } else if (this.getAnimationTick() >= 10 && this.getAnimationTick() <= 30) {
                Vec3 randomOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalize().scale(1).add(this.getEyePosition());
                this.level().addParticle(ACParticleRegistry.GAMMAROACH.get(), this.getRandomX(2), this.getEyeY(), this.getRandomZ(2), randomOffset.x, randomOffset.y + 0.23D, randomOffset.z);

            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public MobType getMobType() {
        return ModMobType.NATURAL;
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
        return new Animation[]{ANIMATION_SPRAY, ANIMATION_RAM};
    }

    public void triggerSpraying() {
        if (this.getAnimation() != ANIMATION_SPRAY) {
            this.playSound(ACSoundRegistry.GAMMAROACH_SPRAY.get());
            this.syncAnimation(ANIMATION_SPRAY);
        }
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_RAM || this.getAnimation() == ANIMATION_SPRAY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        if (prev != InteractionResult.SUCCESS) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(ACItemRegistry.SPELUNKIE.get()) && (!level().isClientSide && this.getTarget() == player || !isFed())) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.setFed(true);
                this.setLastHurtByMob(null);
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte) 49);
                if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                            5, 0.5, 0.5, 0.5, 0.0);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return prev;
    }

    public void handleEntityEvent(byte b) {
        if (b == 49) {
            ItemStack itemstack = new ItemStack(ACItemRegistry.SPELUNKIE.get());
            for (int i = 0; i < 8; ++i) {
                Vec3 headPos = (new Vec3(0D, 0.1D, 0.5D)).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + headPos.x, this.getY(0.5) + headPos.y, this.getZ() + headPos.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.15F, (random.nextFloat() - 0.5F) * 0.1F);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GAMMAROACH_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GAMMAROACH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GAMMAROACH_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GAMMAROACH_STEP.get(), 1.0F, 1.0F);
        }
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GammaroachServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = GammaroachServant.this.getTarget();
            if (target != null && target.isAlive()) {
                GammaroachServant.this.getNavigation().moveTo(target, 1.0D);
                GammaroachServant.this.lookAt(target, 180, 30);
                if (GammaroachServant.this.distanceTo(target) < 1.5F + target.getBbWidth()) {
                    if (GammaroachServant.this.getAnimation() == NO_ANIMATION) {
                        if (target.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
                            GammaroachServant.this.syncAnimation(GammaroachServant.ANIMATION_RAM);
                        } else {
                            GammaroachServant.this.triggerSpraying();
                        }
                    } else if (GammaroachServant.this.getAnimation() == GammaroachServant.ANIMATION_RAM && GammaroachServant.this.getAnimationTick() > 8 && GammaroachServant.this.getAnimationTick() < 15) {
                        GammaroachServant.this.playSound(ACSoundRegistry.GAMMAROACH_ATTACK.get());
                        target.hurt(damageSources().mobAttack(GammaroachServant.this), (float) GammaroachServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    }
                }
            }
        }
    }
}
