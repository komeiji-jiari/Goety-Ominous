package com.qiuyue.goetyominus.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.init.ModMobType;
import com.qiuyue.goetyominus.common.entities.projectile.ScorchFireball;
import com.qiuyue.goetyominus.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Scorch extends Minion {
    public int shootTime;

    public Scorch(EntityType<? extends Scorch> type, Level level) {
        super(type, level);
        this.navigation = new FlyingPathNavigation(this, level);
        this.navigation.setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 6;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OutofBoundsGoal());
        this.goalSelector.addGoal(2, new com.Polarice3.Goety.common.entities.ai.MinionFollowGoal(this, 0.5D, 6.0F, 3.0F, true));
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new com.Polarice3.Goety.common.entities.ai.SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ScorchHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ScorchDamage.get());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.shootTime > 0) {
            --this.shootTime;
        }
        if (!this.level().isClientSide) {
            if (this.isCharging()) {
                if (this.random.nextInt(4) == 0) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            this.getY() + this.random.nextDouble() * this.getBbHeight(),
                            this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            0.0D, 0.0D, 0.0D);
                }
            } else {
                if (this.random.nextInt(8) == 0) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            this.getY() + this.random.nextDouble() * this.getBbHeight(),
                            this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            if (com.Polarice3.Goety.utils.CuriosFinder.hasNetherRobe(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return false;
        if (source.getEntity() instanceof Scorch) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.VEX_AMBIENT; }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { return SoundEvents.VEX_HURT; }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.VEX_DEATH; }

    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.shootTime = compound.getInt("ShootTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ShootTime", this.shootTime);
    }

    class OutofBoundsGoal extends Goal {
        public OutofBoundsGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!Scorch.this.isInWall()) return false;
            return !Scorch.this.getMoveControl().hasWanted();
        }

        @Override
        public boolean canContinueToUse() {
            if (!Scorch.this.isInWall()) return false;
            return !Scorch.this.getMoveControl().hasWanted();
        }

        @Override
        public void start() {
            BlockPos.MutableBlockPos pos = Scorch.this.blockPosition().mutable();
            pos.setY(Scorch.this.level().getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
            Scorch.this.getMoveControl().setWantedPosition(
                    pos.getX(), pos.getY(), pos.getZ(), 1.0D);
        }
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Scorch.this.getTarget();
            if (target == null || !target.isAlive()) return false;
            if (Scorch.this.getMoveControl().hasWanted()) return false;
            return !target.isAlliedTo(Scorch.this);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Scorch.this.getTarget();
            if (target == null || !target.isAlive()) return false;
            return Scorch.this.isCharging();
        }

        @Override
        public void start() {
            LivingEntity target = Scorch.this.getTarget();
            if (target == null) return;
            Scorch.this.setIsCharging(true);
            Vec3 eyePos = target.getEyePosition(1.0F);
            if (Scorch.this.distanceTo(target) > 4.0F) {
                Scorch.this.getMoveControl().setWantedPosition(eyePos.x, eyePos.y, eyePos.z, 1.0D);
            }
        }

        @Override
        public void stop() {
            Scorch.this.setIsCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity target = Scorch.this.getTarget();
            if (target == null) {
                Scorch.this.setIsCharging(false);
                return;
            }

            double dx = target.getX() - Scorch.this.getX();
            double dy = target.getY(0.5D) - Scorch.this.getY(0.5D);
            double dz = target.getZ() - Scorch.this.getZ();

            if (Scorch.this.shootTime == 10) {
                LivingEntity owner = Scorch.this.getTrueOwner();
                boolean useHellBolt = owner != null
                        && com.Polarice3.Goety.utils.CuriosFinder.hasUnholySet(owner);
                if (useHellBolt) {
                    com.Polarice3.Goety.common.entities.projectiles.HellBolt bolt =
                            new com.Polarice3.Goety.common.entities.projectiles.HellBolt(
                                    Scorch.this, dx, dy, dz, Scorch.this.level());
                    bolt.setPos(bolt.getX(), Scorch.this.getY(0.5D), bolt.getZ());
                    Scorch.this.level().addFreshEntity(bolt);
                } else {
                    ModFireball fireball = new ScorchFireball(
                            Scorch.this.level(), Scorch.this, dx, dy, dz);
                    fireball.setPos(fireball.getX(), Scorch.this.getY(0.5D), fireball.getZ());
                    Scorch.this.level().addFreshEntity(fireball);
                }
                Scorch.this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F);
            }

            Scorch.this.setIsCharging(Scorch.this.shootTime <= 10);

            if (Scorch.this.shootTime == 0) {
                Scorch.this.shootTime = 20;
            }

            Vec3 velocity = Scorch.this.getDeltaMovement();
            double vy = velocity.y * 0.6D;

            Vec3 targetEye = target.getEyePosition(1.0F);
            if (Scorch.this.getY() < targetEye.y) {
                vy += Math.max(0.0D, 0.3D - velocity.y * 0.6D);
            } else {
                vy = Math.max(0.0D, vy);
                vy += Math.max(0.0D, 0.3D - velocity.y * 0.6D);
            }

            velocity = new Vec3(velocity.x * 0.6D, vy, velocity.z * 0.6D);

            double horizDistSqr = dx * dx + dz * dz;
            if (horizDistSqr > 9.0D) {
                Vec3 steer = new Vec3(dx, 0.0D, dz).normalize();
                velocity = velocity.add(
                        steer.x * 0.3D - velocity.x * 0.6D,
                        0.0D,
                        steer.z * 0.3D - velocity.z * 0.6D);
            }

            Scorch.this.setDeltaMovement(velocity);

            Scorch.this.setYRot(-((float) Mth.atan2(dx, dz)) * 57.295776F);
            Scorch.this.yBodyRot = Scorch.this.getYRot();
        }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !Scorch.this.getMoveControl().hasWanted()
                    && Scorch.this.random.nextInt(7) == 0
                    && !Scorch.this.isCharging();
        }

        @Override
        public boolean canContinueToUse() { return false; }

        @Override
        public void start() {
            BlockPos pos = Scorch.this.blockPosition();
            for (int i = 0; i < 3; i++) {
                BlockPos target = pos.offset(
                        Scorch.this.random.nextInt(8) - 4,
                        Scorch.this.random.nextInt(6) - 2,
                        Scorch.this.random.nextInt(8) - 4);
                if (Scorch.this.level().isEmptyBlock(target)) {
                    Scorch.this.getMoveControl().setWantedPosition(
                            target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 0.25D);
                    if (Scorch.this.getTarget() == null) {
                        Scorch.this.getLookControl().setLookAt(
                                target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    return;
                }
            }
        }
    }
}
