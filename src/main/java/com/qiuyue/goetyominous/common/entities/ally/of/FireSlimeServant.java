package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FireSlimeServant extends SlimeServant {
    private static final int FIXED_SIZE = 1;
    private static final float FIXED_WIDTH = 0.6875F;
    private static final float FIXED_HEIGHT = 0.6875F;

    public FireSlimeServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FireSlimeServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FireSlimeServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FireSlimeServantAttackDamage.get());
    }

    @Override
    public int getSize() {
        return FIXED_SIZE;
    }

    @Override
    public void setSize(int size, boolean heal) {
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(FIXED_WIDTH, FIXED_HEIGHT);
    }

    @Override
    public @Nullable EntityType<?> getVariant(@NotNull Level level, @NotNull BlockPos blockPos) {
        return null;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.FireSlimeServantLimit.get();
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public void tick() {
        if (this.tickCount % 4 == 0 && this.isAlive()) {
            float f = this.random.nextFloat() * ((float) Math.PI * 2.0F);
            float f1 = this.random.nextFloat() * 0.5F + 0.5F;
            float f2 = Mth.sin(f) * 0.75F * f1;
            float f3 = Mth.cos(f) * 0.75F * f1;
            this.level().addParticle(ParticleTypes.FLAME,
                    this.getX() + (double) f2, this.getY() + 0.3D, this.getZ() + (double) f3,
                    0.0D, 0.0D, 0.0D);
        }
        if (this.hasLifespan() && this.getLifespan() <= 40 && this.getLifespan() > 1) {
            this.getNavigation().stop();
            if (this.getTarget() != null) {
                this.setTarget(null);
            }
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(0.0D, vec3.y, 0.0D);
            if (this.tickCount % 2 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(0.5D) - d1 * 10.0D, this.getZ(),
                        d0, d1, d2);
            }
        }
        super.tick();
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        if (this.isAlive()
                && this.distanceToSqr(target) < 1.1D
                && this.hasLineOfSight(target)
                && target.hurt(this.getServantAttack(), this.getAttackDamage())) {
            this.playSound(OPSoundEvents.FIRE_SLIME_ATTACK.get(), 1.0F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.random.nextBoolean()) {
                target.setSecondsOnFire(3);
            }
            this.doEnchantDamageEffects(this, target);
        }
    }

    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    public void shootFromOwner(double x, double y, double z, float scale) {
        Vec3 vec3 = new Vec3(x, y, z).normalize()
                .add(this.random.nextGaussian() * 0.008D,
                        this.random.nextGaussian() * 0.008D,
                        this.random.nextGaussian() * 0.008D)
                .scale((double) scale);
        this.setDeltaMovement(vec3);
        float horizontalDistanceSqr = (float) vec3.horizontalDistanceSqr();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 55.0D));
        this.setXRot((float) (Mth.atan2(vec3.y, (double) horizontalDistanceSqr) * 55.0D));
        this.xRotO = this.getXRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.getYRot();
        this.yRotO = this.getYRot();
    }

    @Override
    public void dismiss() {
        this.playSound(OPSoundEvents.FIRE_SLIME_POP.get(), this.getSoundVolume(),
                (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        super.dismiss();
    }

    @Override
    public void jumpInFluid(@NotNull FluidType type) {
        if (type == ForgeMod.LAVA_TYPE.get()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, 0.011F, vec3.z);
            this.hasImpulse = true;
        } else {
            super.jumpInFluid(type);
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public int getMaxFallDistance() {
        return 10;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @Override
    protected int getJumpDelay() {
        return this.random.nextInt(20) + 14;
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected boolean spawnCustomParticles() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.FIRE_SLIME_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.FIRE_SLIME_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.FIRE_SLIME_DEATH.get();
    }

    @Override
    protected @NotNull SoundEvent getSquishSound() {
        return OPSoundEvents.FIRE_SLIME_SQUISH.get();
    }

    @Override
    protected @NotNull SoundEvent getJumpSound() {
        return OPSoundEvents.FIRE_SLIME_JUMP.get();
    }

    @Override
    protected @NotNull SoundEvent getAttackSound() {
        return OPSoundEvents.FIRE_SLIME_ATTACK.get();
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SLIME_BLOCK)) {
            return InteractionResult.PASS;
        }
        if (stack.is(Items.SLIME_BALL) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.heal(2.0F);
            this.playSound(this.getSquishSound(), 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }
}
