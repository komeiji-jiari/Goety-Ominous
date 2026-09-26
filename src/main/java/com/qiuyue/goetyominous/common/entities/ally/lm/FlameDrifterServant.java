package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoalMin;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SmallAnnihilationBomb;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.Particle.custom.BigAnnihilationSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.LightningParticle;
import net.miauczel.legendary_monsters.Particle.custom.MovingTrailParticle;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public class FlameDrifterServant extends AbstractFlamebornServant {

    public final int SPIN_COOLDOWN = 80;
    public int spin_cooldown = 80;
    public int nextShootType = 1;
    public double lastX;
    public double lastY;
    public double lastZ;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState leftCannonShootAnimationState = new AnimationState();
    public final AnimationState rightCannonShootAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState spinChargeAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public int deathTime;

    public FlameDrifterServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.lastX = this.getX();
        this.lastY = this.getY();
        this.lastZ = this.getZ();
        this.setPersistenceRequired();
        this.setMaxUpStep(2.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FlameDrifterServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.FlameDrifterServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FlameDrifterServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.FlameDrifterServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FlameDrifterServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.FlameDrifterServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FlameDrifterServantDamage.get());
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    @Override
    public int attackDelayTicksValue() {
        return 3;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 40, 54, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlameDrifterServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && FlameDrifterServant.this.getTarget() != null
                        && FlameDrifterServant.this.getNextShootType() == 1;
            }

            @Override
            public void stop() {
                FlameDrifterServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 6, 0, 76, 76, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlameDrifterServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && FlameDrifterServant.this.getTarget() != null
                        && FlameDrifterServant.this.spin_cooldown <= 0;
            }

            @Override
            public void stop() {
                FlameDrifterServant.this.randomizeAttacks();
                FlameDrifterServant.this.spin_cooldown = SPIN_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 40, 54, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlameDrifterServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && FlameDrifterServant.this.getTarget() != null
                        && FlameDrifterServant.this.getNextShootType() == 2;
            }

            @Override
            public void stop() {
                FlameDrifterServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 5, 0, 53, 53, 12.0F, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlameDrifterServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && FlameDrifterServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                FlameDrifterServant.this.randomizeAttacks();
                super.stop();
            }
        });
    }

    @Override
    public void tick() {
        if (this.spin_cooldown > 0) {
            --this.spin_cooldown;
        }
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
            if (this.isDuringTeleportation() && !this.isInvisible()) {
                this.setInvisible(true);
            }
            if (!this.isDuringTeleportation() && this.isInvisible()) {
                this.setInvisible(false);
            }
        }
        this.UpdateWithAttack();
        super.tick();
    }

    public void randomizeAttacks() {
        this.setRandomNextShootType(2);
    }

    public int getNextShootType() {
        return this.nextShootType;
    }

    public void setRandomNextShootType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.nextShootType = 1;
            case 1 -> this.nextShootType = 2;
        }
    }

    public void backStep(float backstepStrength, float yStrength) {
        float yaw = (float) Math.toRadians(this.getYRot() + 90.0F);
        Vec3 dodgePos = this.getDeltaMovement().add(backstepStrength * Math.cos(yaw), yStrength, backstepStrength * Math.sin(yaw));
        this.setDeltaMovement(dodgePos.x, dodgePos.y, dodgePos.z);
    }

    public void saveLastPosition(LivingEntity entity) {
        this.lastX = entity.getX();
        this.lastY = entity.getY();
        this.lastZ = entity.getZ();
    }

    public void teleportToLastPosition() {
        this.teleport(this.lastX, this.lastY, this.lastZ);
    }

    public boolean isDuringTeleportation() {
        return this.getAttackState() == 5 && this.attackTicks >= 34 && this.attackTicks <= 41;
    }

    public void UpdateWithAttack() {
        if (this.getAttackState() == 2 || this.getAttackState() == 3) {
            if (this.attackTicks == 4) {
                this.playSound(ModSounds.FLAME_DRIFTER_CHARGE_SHOOT.get(), 1.0F, 1.25F);
            }
            float y = (float) (this.getY() + 0.5D);
            float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
            double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            float vec = 0.75F;
            float offset = this.getAttackState() == 2 ? 1.75F : -1.75F;
            double spawnX = this.getX() + vec * vecX + f * offset;
            double spawnZ = this.getZ() + vec * vecZ + f1 * offset;
            if (this.attackTicks == 13 && this.level().isClientSide) {
                for (int k = 0; k < 3; ++k) {
                    float d1 = Mth.sqrt(k);
                    float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                    this.level().addParticle(new MovingTrailParticle.TrailData(0.0F, g, 0.0F, 0.1F, 0.1F),
                            spawnX, y, spawnZ, Mth.sin(k), 0.0D, d1 * 0.01F);
                }
            }
            if (this.attackTicks == 20) {
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.ANNIHILATION_EXPLOSION.get(), spawnX, y, spawnZ, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.OBLITERATOR_ARM_SHOOT.get(), 1.0F, 1.25F);
                this.backStep(-0.75F, 0.0F);
                this.shootAnnihilationBomb(1.0F, (float) spawnX, y, (float) spawnZ, 15);
                this.shootAnnihilationBomb(1.0F, (float) spawnX, y, (float) spawnZ, 30);
            }
        }
        if (this.getAttackState() == 5) {
            if (this.attackTicks == 10) {
                this.saveLastPosition(this);
            }
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.CANNON_SHOOT_1.get(), 2.0F, 0.75F);
                this.calculatedDash(0.55F);
            }
            if (this.attackTicks > 15 && this.attackTicks < 22) {
                if (this.level().isClientSide) {
                    float yaw = (float) Math.toRadians(-this.getYRot() + 180.0F);
                    double theta = this.getYRot() * (Math.PI / 180) + 1.5707963267948966;
                    double vecX = Math.cos(theta);
                    double vecZ = Math.sin(theta);
                    double spawnX = this.getX() + vecX * 1.5D;
                    double spawnZ = this.getZ() + vecZ * 1.5D;
                    double d0 = (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().x;
                    double d1 = (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().y;
                    double d2 = (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().z;
                    double dist = 2.0F + this.random.nextFloat() * 0.2F;
                    this.level().addParticle(new LightningParticle.OrbData(25, 255, 0),
                            this.getX() + d0, this.getY() + 1.0D, this.getZ() + d2, d0 * dist, d1 * dist, d2 * dist);
                    this.level().addParticle(new LightningParticle.OrbData(255, 255, 255),
                            this.getX() + d0, this.getY() + 1.0D, this.getZ() + d2, d0 * dist, d1 * dist, d2 * dist);
                    this.level().addParticle(new Circle.RingData(yaw, 0.0F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                            spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
                }
                float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                float vec = 0.0F;
                float offset = 2.0F;
                float offset2 = -2.0F;
                double dx = this.getX() + vec * vecX + f * offset;
                double dx2 = this.getX() + vec * vecX + f * offset2;
                double dy = this.getY() + 1.5D;
                double dz = this.getZ() + vec * vecZ + f1 * offset;
                double dz2 = this.getZ() + vec * vecZ + f1 * offset2;
                float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                if (this.level().isClientSide) {
                    this.level().addParticle(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 1.0F, this.getId()), dx, dy, dz, 0.0D, 0.0D, 0.0D);
                    this.level().addParticle(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 1.0F, this.getId()), dx2, dy, dz2, 0.0D, 0.0D, 0.0D);
                }
                this.SideAreaAttack(1.0F, 2.0F, 360.0F, 0.0F, 10.0F, 60, true, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.5F);
            }
            if (this.attackTicks == 34) {
                this.teleportToLastPosition();
            }
        }
        if (this.getAttackState() == 6 && this.attackTicks > 20 && this.attackTicks < 50) {
            float a = 0.275F;
            this.basicDash(a, a, 0.0F, false);
            if (this.tickCount % 5 == 0) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.5F);
                float sweepSize = 1.25F;
                this.createSweep(0.0F, 0.0F, 3.0F, this.getRandom().nextInt() * 100 < 50, sweepSize, 0.0F);
                this.createSweep(0.0F, 0.0F, -3.0F, this.getRandom().nextInt() * 100 < 50, sweepSize, 0.0F);
            }
            this.SideAreaAttack(3.0F, 3.0F, 360.0F, 0.0F, 13.0F, 100, true, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.5F);
        }
    }

    public void createSweep(float pos, float posOffset, float yHeight, boolean reverse, float scale, float rot) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + pos * vecX + f * posOffset;
        double z = this.getZ() + pos * vecZ + f1 * posOffset;
        if (this.level().isClientSide) {
            double d1 = this.getY() + this.getBbHeight() / 2.0F + 0.4D;
            float yaw = (float) Math.toRadians(-this.yBodyRot + (reverse ? rot : 180.0F));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (reverse ? -1 : 1) * (float) Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            this.level().addParticle(new BigAnnihilationSweepParticle.SweepData(this.getScale() * scale, yaw, pitch), x, d1, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public void shootAnnihilationBomb(float velocity, float x, float y, float z, int inaccuracy) {
        if (this.targetIsNotNull()) {
            SmallAnnihilationBomb bomb = new SmallAnnihilationBomb(this.level(), this, 8.0F);
            bomb.setPosRaw(x, y, z);
            double d0 = this.target().getX() - x;
            double d1 = this.target().getBoundingBox().minY + this.target().getBbHeight() / 2.0F - bomb.getY();
            double d2 = this.target().getZ() - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            bomb.shoot(d0, d1 + d3 * 0.2D, d2, velocity, inaccuracy - this.level().getDifficulty().getId() * 4);
            bomb.setOwner(this);
            this.level().addFreshEntity(bomb);
        }
    }

    private boolean isFriendlyTo(LivingEntity other) {
        return other == this || MobUtil.areAllies(this, other);
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float damage, int brokenShieldTicks, boolean canlaunch, SoundEvent soundEvent, float pitch) {
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * 57.29577951308232 - 90.0D) % 360.0D);
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            boolean inArc = entityHitDistance <= range
                    && (entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F);
            if (!inArc || this.isFriendlyTo(entityHit)) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * ModConfig.MOB_CONFIG.FlameDrifterDamageMutliplier.get()));
            if (flag) {
                this.playSound(soundEvent, 1.0F, pitch);
                if (canlaunch) {
                    entityHit.push(entityHit.getDeltaMovement().x, 0.5D, entityHit.getDeltaMovement().z);
                }
                if (this.getAttackState() == 5) {
                    entityHit.setSecondsOnFire(3);
                }
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    @Override
    public boolean teleport(double x, double y, double z) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + this.getBbHeight() / 2.0F,
                    this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(x, y, z);
        while (mutableBlockPos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(mutableBlockPos).blocksMotion()) {
            mutableBlockPos.move(Direction.DOWN);
        }
        if (!this.level().getBlockState(mutableBlockPos).blocksMotion()) {
            return false;
        }
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
        if (event.isCanceled()) {
            return false;
        }
        Vec3 oldPos = this.position();
        if (this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            this.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.SHULKER_TELEPORT, 4.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean teleportBoolean(double x, double y, double z, boolean playSound) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        double targetY = y;
        boolean success = false;
        BlockPos pos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(pos)) {
            boolean foundGround = false;
            while (!foundGround && pos.getY() > level.getMinBuildHeight()) {
                BlockPos below = pos.below();
                if (level.getBlockState(below).blocksMotion()) {
                    foundGround = true;
                    continue;
                }
                targetY -= 1.0D;
                pos = below;
            }
            if (foundGround) {
                this.teleportTo(x, targetY, z);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    success = true;
                }
            }
        }
        if (!success) {
            this.teleportTo(oldX, oldY, oldZ);
            return false;
        }
        if (playSound) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("left_cannon_shoot")) {
            return this.leftCannonShootAnimationState;
        }
        if (input.equals("right_cannon_shoot")) {
            return this.rightCannonShootAnimationState;
        }
        if (input.equals("spin_charge")) {
            return this.spinChargeAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        if (input.equals("spin")) {
            return this.spinAnimationState;
        }
        return new AnimationState();
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.leftCannonShootAnimationState.stop();
        this.rightCannonShootAnimationState.stop();
        this.deathAnimationState.stop();
        this.spinChargeAnimationState.stop();
        this.spinAnimationState.stop();
    }

    private void startAnimation(AnimationState state) {
        this.stopAllAnimationStates();
        state.startIfStopped(this.tickCount);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 2 -> this.startAnimation(this.leftCannonShootAnimationState);
                case 3 -> this.startAnimation(this.rightCannonShootAnimationState);
                case 4 -> this.startAnimation(this.deathAnimationState);
                case 5 -> this.startAnimation(this.spinChargeAnimationState);
                case 6 -> this.startAnimation(this.spinAnimationState);
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public void die(DamageSource source) {
        this.deathTime = 0;
        this.stopAllAnimationStates();
        this.setAttackState(4);
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 45) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.THE_WARPED_ONE_HURT2.get();
    }
}
