package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoalMin;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationExplosion;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationFlameStrike;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EntityThrown;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SmallAnnihilationBomb;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.BigAnnihilationSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.GiantAnnihilationSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.MovingTrailParticle;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;

public class AnnihilationPursuerServant extends IAnimatedMiniBossServant {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState stompComboAnimationState = new AnimationState();
    public final AnimationState stompComboEndAnimationState = new AnimationState();
    public final AnimationState stompComboTeleportEndAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState awakenAnimationState = new AnimationState();
    public final AnimationState singleSlashFromAnimationState = new AnimationState();
    public final AnimationState singleSlashFromParryAnimationState = new AnimationState();
    public final AnimationState singleSlashFromFailAnimationState = new AnimationState();
    public final AnimationState singleSlashCutAnimationState = new AnimationState();
    public final AnimationState singleSlashDoubleAnimationState = new AnimationState();
    public final AnimationState singleSlashFailAnimationState = new AnimationState();
    public final AnimationState grabPreAnimationState = new AnimationState();
    public final AnimationState grabSuccessAnimationState = new AnimationState();
    public final AnimationState grabFailAnimationState = new AnimationState();
    public final AnimationState teleportChaseAnimationState = new AnimationState();
    public final AnimationState teleportSlamAnimationState = new AnimationState();
    public final AnimationState teleportChaseNextAnimationState = new AnimationState();
    public final AnimationState buckshotAnimationState = new AnimationState();
    public final AnimationState buckshotEndAnimationState = new AnimationState();
    public final AnimationState buckshotTPAnimationState = new AnimationState();

    public final int TELEPORT_SLAM_COOLDOWN = 100;
    public final int SHIELD_STUN_COOLDOWN = 100;
    public final int STAB_FINISHER_COOLDOWN = 200;
    public final int BUCKSHOT_COOLDOWN = 40;
    public final int STOMP_COMBO_COOLDOWN = 100;
    public int stomp_combo_cooldown;
    public int teleport_slam_cooldown;
    public int shield_stun_cooldown;
    public int stab_finisher_cooldown;
    public int buckshot_cooldown;
    public double lastX;
    public double lastY;
    public double lastZ;
    public boolean hasHit;
    public boolean succedGrabbing;
    public int deathTime;

    public AnnihilationPursuerServant(EntityType<? extends AnnihilationPursuerServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AnnihilationPursuerServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.AnnihilationPursuerServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.AnnihilationPursuerServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.AnnihilationPursuerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.AnnihilationPursuerServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.AnnihilationPursuerServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AnnihilationPursuerServantDamage.get());
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
        return 2;
    }

    @Override
    protected boolean isCarryImmune() {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        if (this.isSleep()) {
            return false;
        }
        return super.addEffect(pEffectInstance, pEntity);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isVehicle() && this.getAttackState() != 14
                && this.getAttackState() != 15 && this.getAttackState() != 19) {
            this.ejectPassengers();
        }
        if (this.isVehicle() && this.getFirstPassenger() != null) {
            this.getFirstPassenger().setShiftKeyDown(false);
        }
        if (this.teleport_slam_cooldown > 0) {
            --this.teleport_slam_cooldown;
        }
        if (this.stomp_combo_cooldown > 0) {
            --this.stomp_combo_cooldown;
        }
        if (this.shield_stun_cooldown > 0) {
            --this.shield_stun_cooldown;
        }
        if (this.stab_finisher_cooldown > 0) {
            --this.stab_finisher_cooldown;
        }
        if (this.buckshot_cooldown > 0) {
            --this.buckshot_cooldown;
        }
        if (this.level().isClientSide) {
            if (this.isDuringTeleportation() && !this.isInvisible()) {
                this.setInvisible(true);
            }
            if (!this.isDuringTeleportation() && this.isInvisible()) {
                this.setInvisible(false);
            }
        }
        this.UpdateWithAttack();
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        super.tick();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 21, 0, MathUtils.toTicks(1.42F), MathUtils.toTicks(1.42F), 15.0F, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 32.0F
                        && AnnihilationPursuerServant.this.getTarget() != null
                        && AnnihilationPursuerServant.this.buckshot_cooldown <= 0;
            }

            @Override
            public void stop() {
                AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.getRandom().nextInt() * 100 < 50 ? 22 : 23);
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 22, 22, 0, MathUtils.toTicks(1.08F), 10) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.buckshot_cooldown = BUCKSHOT_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 23, 23, 0, MathUtils.toTicks(1.33F), MathUtils.toTicks(1.33F)) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.buckshot_cooldown = BUCKSHOT_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 13, 0, 70, 25, 13.0F, 3.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && AnnihilationPursuerServant.this.getTarget() != null
                        && AnnihilationPursuerServant.this.teleport_slam_cooldown <= 0;
            }

            @Override
            public void stop() {
                AnnihilationPursuerServant.this.teleport_slam_cooldown = TELEPORT_SLAM_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 18, 0, 21, 25, 20.0F, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 32.0F
                        && AnnihilationPursuerServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.getRandom().nextInt() * 100 < 50
                        && AnnihilationPursuerServant.this.stab_finisher_cooldown <= 0 ? 19 : 0);
                AnnihilationPursuerServant.this.attackCooldown = 0;
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 19, 19, 0, 38, 35) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.succedGrabbing ? 15 : 16);
                AnnihilationPursuerServant.this.attackTicks = 0;
                AnnihilationPursuerServant.this.attackCooldown = 0;
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 8, 10, 29, 29, 7.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && AnnihilationPursuerServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                if (AnnihilationPursuerServant.this.hasHit && AnnihilationPursuerServant.this.shield_stun_cooldown <= 0) {
                    AnnihilationPursuerServant.this.hasHit = false;
                    AnnihilationPursuerServant.this.setAttackState(9);
                } else {
                    super.stop();
                }
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 9, 9, 0, 48, 25) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.shield_stun_cooldown = SHIELD_STUN_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 10, 10, 0, 19, 0));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 5, 12, 28, 28, 7.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && AnnihilationPursuerServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                if (AnnihilationPursuerServant.this.hasHit) {
                    AnnihilationPursuerServant.this.hasHit = false;
                    AnnihilationPursuerServant.this.setAttackState(11);
                } else {
                    AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.stab_finisher_cooldown <= 0
                            ? (AnnihilationPursuerServant.this.getRandom().nextInt() * 100 < 50 ? 12 : 14) : 12);
                }
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 11, 11, 0, 37, 30));
        this.goalSelector.addGoal(0, new IStateGoal(this, 12, 12, 0, 21, 0));
        this.goalSelector.addGoal(0, new IStateGoal(this, 14, 14, 0, 39, 30) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.succedGrabbing ? 15 : 16);
                AnnihilationPursuerServant.this.attackTicks = 0;
                AnnihilationPursuerServant.this.attackCooldown = 0;
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 15, 15, 0, 112, 0) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.stab_finisher_cooldown = STAB_FINISHER_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 16, 16, 0, 23, 0) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.stab_finisher_cooldown = STAB_FINISHER_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 46, 34, 7.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AnnihilationPursuerServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && AnnihilationPursuerServant.this.getTarget() != null
                        && AnnihilationPursuerServant.this.stomp_combo_cooldown <= 0;
            }

            @Override
            public void stop() {
                AnnihilationPursuerServant.this.setAttackState(AnnihilationPursuerServant.this.getRandom().nextInt() * 100 < 50 ? 3 : 4);
                AnnihilationPursuerServant.this.attackCooldown = 0;
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 3, 3, 0, 22, 0) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.stomp_combo_cooldown = STOMP_COMBO_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 4, 4, 0, 28, 0) {
            @Override
            public void stop() {
                AnnihilationPursuerServant.this.stomp_combo_cooldown = STOMP_COMBO_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IStateGoal(this, 6, 6, 0, 0, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });
        this.goalSelector.addGoal(0, new IAttackGoal(this, 6, 7, 0, 30, 0, 10.0F));
        this.goalSelector.addGoal(1, new IStateGoal(this, 17, 17, 0, 85, 0));
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 6 : 0);
    }

    public boolean isSleep() {
        return this.getAttackState() == 6 || this.getAttackState() == 7;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    public boolean isDuringTeleportation() {
        return this.getAttackState() == 4 && this.attackTicks > 8 && this.attackTicks < 13
                || this.getAttackState() == 13 && this.attackTicks > 12 && this.attackTicks < 18
                || this.getAttackState() == 18 && this.attackTicks > 8 && this.attackTicks < 14
                || this.getAttackState() == 19 && this.attackTicks > 8 && this.attackTicks < 14
                || this.getAttackState() == 14 && this.attackTicks > 12 && this.attackTicks < 18
                || this.getAttackState() == 23 && this.attackTicks > 10 && this.attackTicks < MathUtils.toTicks(0.83F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player
                && countServants(player) >= MobsConfig.AnnihilationPursuerServantLimit.get()) {
            return null;
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    private static int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof AnnihilationPursuerServant servant && servant.getTrueOwner() == player) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setPersistenceRequired();
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
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("is_Sleep", this.isSleep());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setSleep(compound.getBoolean("is_Sleep"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public void dismountTo(double pX, double pY, double pZ) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float vec = 3.0F;
        float offset = 2.0F;
        super.dismountTo(this.getX() + vec * vecX + f * offset, this.getY() + 5.0D, this.getZ() + vec * vecZ + f1 * offset);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity pPassenger) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float vec = 2.0F;
        float offset = 1.0F;
        return new Vec3(this.getX() + vec * vecX + f * offset, this.getY() + 4.0D, this.getZ() + vec * vecZ + f1 * offset);
    }

    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
            double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            float vec = 2.25F;
            float offset = 0.25F;
            pCallback.accept(pPassenger, this.getX() + vec * vecX + f * offset, this.getY() + 3.0D, this.getZ() + vec * vecZ + f1 * offset);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.FALL)) {
            return false;
        }
        if (pSource.is(DamageTypeTags.IS_PROJECTILE) && !this.isSleep()) {
            this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            this.teleportRandomly(10.0D);
            return false;
        }
        if (this.isDuringTeleportation() && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || this.isSleep()) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    private boolean isFriendlyTo(LivingEntity other) {
        return other == this || other instanceof AnnihilationPursuerServant || MobUtil.areAllies(this, other);
    }

    public void UpdateWithAttack() {
        float sweepSize = 2.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        float bigSweepAdditionalY = 1.0F;
        float dmgred = 1.5F;
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float slashRange = 5.0F;

        if (this.getAttackState() == 7 && this.attackTicks == 1) {
            CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.075F, 0, 20);
        }
        if (this.getAttackState() == 2) {
            for (int i = 20; i <= 26; i += 2) {
                if (this.attackTicks != i) {
                    continue;
                }
                this.flameRadagonShockwave(0.2F, i - 18, 1.0F, 2, 0.0F, -1.5F, 6.0F);
            }
            if (this.attackTicks == 20) {
                if (this.level().isClientSide) {
                    this.spawnCircleParticle(1.5F, -1.0F, 30.0F, true, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, Circle.EnumRingBehavior.GROW, 35);
                }
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 0, 10);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 1.0F, 1.0F);
                this.SideAreaAttack(3.5F, 3.0F, 180.0F, -90.0F, 0.0F, 18.0F, 70, false, false, SoundEvents.EMPTY, 1.0F);
            }
            if (this.attackTicks == 25) {
                this.calculatedDash(0.15F);
            }
            if (this.attackTicks == 28) {
                this.createSweep(2.0F, -1.0F, 3.0F, 0.4D, true, 1.25F, sweepRot, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 1.25F);
                this.SideAreaAttack(4.0F, 4.0F, 180.0F, 0.0F, 0.0F, 18.0F - dmgred, 100, false, false, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == 33) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 36) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 0.8F);
                this.SideAreaAttack(5.0F, 5.0F, 220.0F, -90.0F, 0.0F, 18.0F, 120, false, false, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
        if (this.getAttackState() == 4 && this.attackTicks == 8) {
            this.teleport(this.getX() - 4.0F * vecX, this.getY(), this.getZ() - 4.0F * vecZ);
        }
        if (this.getAttackState() == 5) {
            if (this.attackTicks == 23) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, false);
                this.calculatedDash(0.2F);
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 0.8F);
            }
            if (this.attackTicks == 26) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
                this.nextSideAreaAttack(slashRange, 5.0F, 220.0F, 0.0F, 18.0F - dmgred, 120, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
        if (this.getAttackState() == 11) {
            if (this.attackTicks == 10) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.calculatedDash(0.2F);
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 0.8F);
            }
            if (this.attackTicks == 13) {
                this.SideAreaAttack(slashRange, 5.0F, 220.0F, 0.0F, 0.0F, 18.0F - dmgred, 120, false, false, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
        if (this.getAttackState() == 8) {
            if (this.attackTicks == 23) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.calculatedDash(0.2F);
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 0.8F);
            }
            if (this.attackTicks == 26) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
                this.nextSideAreaAttack(slashRange, 5.0F, 220.0F, 0.0F, 18.0F - dmgred, 120, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
        if (this.getAttackState() == 9) {
            if (this.attackTicks == 22) {
                this.calculatedDash(0.35F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 2.0F, 1.0F);
            }
            if (this.attackTicks == 25) {
                this.SideAreaAttack(4.25F, 5.0F, 210.0F, 90.0F, 0.0F, 18.0F - dmgred, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
            }
        }
        if (this.getAttackState() == 13) {
            if (this.attackTicks == 13) {
                this.teleport(this.lastX, this.lastY, this.lastZ);
            }
            if (this.attackTicks == 10 && this.targetIsNotNull()) {
                float f2 = Mth.cos(this.target().yBodyRot * ((float) Math.PI / 180));
                float f3 = Mth.sin(this.target().yBodyRot * ((float) Math.PI / 180));
                double theta1 = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
                double vecX1 = Math.cos(theta1);
                double vecZ1 = Math.sin(theta1);
                this.saveTeleportPositions(this.target().getX() + 3.0F * vecX1, this.getY(), this.target().getZ() + 3.0F * vecZ1);
            }
            if (this.attackTicks == 30) {
                double d1 = this.getY() + this.getBbHeight() / 2.0F + 0.3D;
                float yaw = (float) Math.toRadians(-this.yBodyRot + 90.0F);
                float pitch = (float) Math.toRadians(-this.getXRot() + 180.0F);
                if (this.level().isClientSide) {
                    this.level().addParticle(new GiantAnnihilationSweepParticle.SweepData(this.getScale() * 2.0F, yaw, pitch),
                            this.getX(), d1, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.HEAVY_SWING.get(), 2.0F, 0.8F);
            }
            if (this.attackTicks == 33) {
                this.StraightLineAreaAttack(-0.5F, 2.0F, 5.5F, 120, 17.0F, true);
                this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0F, 1.0F);
                this.spawnExplosions(8.0F, 2, 2.0D, 3.0D, 0);
            }
        }
        if (this.getAttackState() == 18) {
            if (this.attackTicks == 3 && this.targetIsNotNull()) {
                Vec3 distance = new Vec3(this.target().position().x + this.position().x, this.target().getY(),
                        this.target().position().z + this.position().z).scale(0.5D);
                this.saveTeleportPositions(distance.x, this.target().getY(), distance.z);
            }
            if (this.attackTicks == 8) {
                this.teleport(this.lastX, this.lastY, this.lastZ);
            }
        }
        if (this.getAttackState() == 14) {
            if (this.attackTicks == 13 && this.targetIsNotNull()) {
                float ft = Mth.cos(this.target().yHeadRot * ((float) Math.PI / 180));
                float f1t = Mth.sin(this.target().yHeadRot * ((float) Math.PI / 180));
                double thetat = this.target().yHeadRot * (Math.PI / 180) + 1.5707963267948966;
                double vecXt = Math.cos(thetat);
                double vecZt = Math.sin(thetat);
                this.teleport(this.target().getX() + 5.0F * vecXt + ft * 3.0F, this.target().getY(),
                        this.target().getZ() + 5.0F * vecZt + f1t * 3.0F);
            }
            if (this.attackTicks == 36) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 39) {
                this.SideGrab(4.5F, 4.0F, 225.0F, 90.0F, 5.0F, 100, SoundEvents.PLAYER_ATTACK_SWEEP, 0.8F);
            }
        }
        if (this.getAttackState() == 19) {
            if (this.attackTicks == 8 && this.targetIsNotNull()) {
                float ft = Mth.cos(this.target().yHeadRot * ((float) Math.PI / 180));
                float f1t = Mth.sin(this.target().yHeadRot * ((float) Math.PI / 180));
                double thetat = this.target().yHeadRot * (Math.PI / 180) + 1.5707963267948966;
                double vecXt = Math.cos(thetat);
                double vecZt = Math.sin(thetat);
                this.teleport(this.target().getX() + 4.0F * vecXt, this.target().getY(), this.target().getZ() + 4.0F * vecZt);
            }
            if (this.attackTicks == 35) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 38) {
                this.SideGrab(4.5F, 4.0F, 225.0F, 90.0F, 5.0F, 100, SoundEvents.PLAYER_ATTACK_SWEEP, 0.8F);
            }
        }
        if (this.getAttackState() == 15) {
            if (this.attackTicks == 17) {
                this.playSound(ModSounds.HEAVY_STAB.get());
            }
            if ((this.attackTicks == 21 || this.attackTicks == 45) && this.isVehicle() && this.getFirstPassenger() != null) {
                this.getFirstPassenger().hurt(ModDamageTypes.causeAnnihilationDamage(this, this),
                        (float) (8.0D * ModConfig.MOB_CONFIG.AnnihilationPursuerDamageMutliplier.get()));
            }
            if (this.attackTicks == 21 && this.level().isClientSide) {
                this.level().addParticle(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + 2.5F * vecX + f * 1.5F,
                        this.getY() + 5.0D, this.getZ() + 2.5F * vecZ + f1 * 1.5F, 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks == 45) {
                this.playSound(ModSounds.STAB_HIT.get());
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + 2.75F * vecX - f,
                            this.getY() + 4.0D, this.getZ() + 2.75F * vecZ - f1, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 82) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 2.0F, 1.0F);
            }
            if (this.attackTicks == 84 && this.getFirstPassenger() instanceof LivingEntity passenger && !this.level().isClientSide) {
                this.throwAnGravityEntity(1.0F, this.getX() + 15.0F * vecX, this.getY() + 2.0D, this.getZ() + 15.0F * vecZ,
                        this.getX() + 2.25F * vecX + f * 0.25F, this.getY() + 3.0D, this.getZ() + 2.25F * vecZ + f1 * 0.25F, 1.0F, passenger);
            }
        }
        if (this.getAttackState() == 21) {
            float y = (float) (this.getY() + 3.0D);
            if (this.attackTicks == 17 && this.level().isClientSide) {
                for (int k = 0; k < 3; ++k) {
                    float d1 = Mth.sqrt(k);
                    float g = 0.7647059F + this.random.nextFloat() * 0.4F;
                    this.level().addParticle(new MovingTrailParticle.TrailData(0.0F, g, 0.0F, 0.1F, 0.1F),
                            this.getX() + 2.0F * vecX - f * 0.5F, y, this.getZ() + 2.0F * vecZ - f1 * 0.5F,
                            Mth.sin(k), 0.0D, d1 * 0.01F);
                }
            }
            if (this.attackTicks == 18) {
                this.playSound(ModSounds.DIMENSIONAL_BOMB_EXPLODE_SMALL.get(), 1.0F, 1.0F);
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX() + 2.0F * vecX, y, this.getZ() + 2.0F * vecZ, 0.0D, 0.0D, 0.0D);
                }
                this.shootAnnihilationBomb(1.0F, (float) this.getX(), y, (float) this.getZ(), 15);
                this.shootAnnihilationBomb(1.0F, (float) this.getX(), y, (float) this.getZ(), 0);
                this.shootAnnihilationBomb(1.0F, (float) this.getX(), y, (float) this.getZ(), 30);
            }
        }
        if (this.getAttackState() == 23 && this.targetIsNotNull() && this.attackTicks == 10) {
            this.teleportRandomly(this.target(), 7.0F, 10.0F);
        }
    }

    public void saveTeleportPositions(double x, double y, double z) {
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
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

    public void createSweep(float pos, float posOffset, float yHeight, double additionalY, boolean reverse, float scale, float rot, boolean small) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + pos * vecX + f * posOffset;
        double z = this.getZ() + pos * vecZ + f1 * posOffset;
        if (this.level().isClientSide) {
            double d1 = this.getY() + this.getBbHeight() / 2.0F + additionalY;
            float yaw = (float) Math.toRadians(-this.yBodyRot + (reverse ? rot : 180.0F));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (reverse ? -1 : 1) * (float) Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            if (small) {
                this.level().addParticle(new BigAnnihilationSweepParticle.SweepData(this.getScale() * scale, yaw, pitch), x, d1, z, 0.0D, 0.0D, 0.0D);
            } else {
                this.level().addParticle(new GiantAnnihilationSweepParticle.SweepData(this.getScale() * scale, yaw, pitch), x, d1, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void throwAnGravityEntity(float velocity, double destX, double destY, double destZ, double x, double y, double z, float damage, LivingEntity passenger) {
        EntityThrown thrown = new EntityThrown(this.level(), this, x, y, z, damage, passenger);
        thrown.setPosRaw(x, y, z);
        double d0 = destX - x;
        double d1 = destY + 0.5D - thrown.getY();
        double d2 = destZ - z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        thrown.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 14 - this.level().getDifficulty().getId() * 4);
        thrown.setOwner(this);
        this.level().addFreshEntity(thrown);
    }

    public void spawnExplosions(float damage, int bulletamount, double amount, double range, int tickDelay) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double endPosX = this.getX() + range * vecX;
        double endPosZ = this.getZ() + range * vecZ;
        double d3 = Mth.floor(this.getY());
        double d1 = this.getY() + 1.0D;
        float f2 = (float) Mth.atan2(endPosZ - this.getZ(), endPosX - this.getX());
        for (int l = 0; l < amount; ++l) {
            double d2 = 4.0D * (l + 1);
            this.spawnEnergyExplosions(this.getX() + Mth.cos(f2) * d2, this.getZ() + Mth.sin(f2) * d2, d3, d1, f2, tickDelay, damage, bulletamount);
        }
    }

    private void spawnEnergyExplosions(double x, double z, double minY, double maxY, float rotation, int delay, float damage, int bulletamount) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (!blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                continue;
            }
            if (!this.level().isEmptyBlock(blockpos)) {
                VoxelShape voxelshape = this.level().getBlockState(blockpos).getCollisionShape(this.level(), blockpos);
                if (!voxelshape.isEmpty()) {
                    d0 = voxelshape.max(Direction.Axis.Y);
                }
            }
            flag = true;
            break;
        } while ((blockpos = blockpos.below()).getY() >= Mth.floor(minY) - 1);
        if (flag) {
            this.level().addFreshEntity(new AnnihilationExplosion(this.level(), x, blockpos.getY() + d0, z, rotation, delay, this, 20, damage, bulletamount));
        }
    }

    public void teleportRandomly(LivingEntity entity, float range, float iteractions) {
        Vec3 entityPos = entity.position();
        Level level = this.level();
        for (int i = 0; (float) i < iteractions; ++i) {
            double x = entityPos.x() + (this.getRandom().nextDouble() - 0.5D) * range;
            double z = entityPos.z() + (this.getRandom().nextDouble() - 0.5D) * range;
            double y = entityPos.y();
            BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
            if (level.isEmptyBlock(pos) && !level.getBlockState(pos.below()).isAir()) {
                this.teleport(x, y, z);
                return;
            }
        }
    }

    private void teleportRandomly(double area) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + this.getBbHeight() / 2.0F,
                    this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        Vec3 entityPos = this.position();
        Level level = this.level();
        for (int i = 0; i < 10; ++i) {
            double x = entityPos.x() + (this.getRandom().nextDouble() - 0.5D) * area;
            double z = entityPos.z() + (this.getRandom().nextDouble() - 0.5D) * area;
            double y = entityPos.y();
            BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
            if (!level.isEmptyBlock(pos) || level.getBlockState(pos.below()).isAir()) {
                continue;
            }
            this.teleportTo(x, y, z);
            return;
        }
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset, float damage, int brokenShieldTicks, boolean canStun, boolean canlaunch, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966;
        double forwardX = Math.cos(theta) * forwardOffset;
        double forwardZ = Math.sin(theta) * forwardOffset;
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0D) % 360.0D);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            boolean inArc = entityHitDistance <= range
                    && (entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F)
                    || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc || this.isFriendlyTo(entityHit)) {
                continue;
            }
            DamageSource damageSource = this.getAttackState() == 15
                    ? ModDamageTypes.causeAnnihilationDamage(this, this) : this.damageSources().mobAttack(this);
            if (entityHit.hurt(damageSource, (float) (damage * ModConfig.MOB_CONFIG.AnnihilationPursuerDamageMutliplier.get()))) {
                EntityUtil.cancelBuffs(entityHit);
                entityHit.invulnerableTime = 0;
                this.hasHit = true;
                if (canlaunch) {
                    this.launch(entityHit, true);
                }
                this.playSound(soundEvent, 1.0F, pitch);
                if (canStun) {
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 40, 1));
                }
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    public void nextSideAreaAttack(float range, float height, float arc, float boxOffset, float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
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
            boolean inArc = entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc || this.isFriendlyTo(entityHit)) {
                continue;
            }
            hitAny = true;
            if (entityHit.hurt(this.damageSources().mobAttack(this), (float) (damage * ModConfig.MOB_CONFIG.AnnihilationPursuerDamageMutliplier.get()))) {
                entityHit.setShiftKeyDown(false);
                this.playSound(soundEvent, 1.0F, pitch);
                this.hasHit = true;
            } else {
                this.hasHit = false;
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
        if (!hitAny) {
            this.hasHit = false;
        }
    }

    public void SideGrab(float range, float height, float arc, float boxOffset, float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
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
            boolean inArc = entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc || this.isFriendlyTo(entityHit)) {
                continue;
            }
            hitAny = true;
            boolean entityHitisTarget = entityHit == this.target();
            DamageSource damageSource = this.damageSources().mobAttack(this);
            if (entityHit.isDamageSourceBlocked(damageSource)) {
                this.succedGrabbing = false;
            }
            if (entityHit.hurt(damageSource, (float) (damage * ModConfig.MOB_CONFIG.AnnihilationPursuerDamageMutliplier.get()))) {
                this.playSound(soundEvent, 1.0F, pitch);
                if (entityHitisTarget && entityHit.startRiding(this, true)) {
                    entityHit.setShiftKeyDown(false);
                    this.succedGrabbing = true;
                } else {
                    this.succedGrabbing = false;
                }
            } else {
                this.succedGrabbing = false;
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
        if (!hitAny) {
            this.succedGrabbing = false;
        }
    }

    private void StraightLineAreaAttack(float boxWidth, float yHeight, float range, int brokenShieldTicks, float damage, boolean launch) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = range * Math.cos(rad);
        double zRange = range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate(boxWidth, yHeight, boxWidth).expandTowards(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isFriendlyTo(entityHit)) {
                continue;
            }
            if (entityHit.hurt(this.damageSources().mobAttack(this), (float) (damage * ModConfig.MOB_CONFIG.AnnihilationPursuerDamageMutliplier.get())) && launch) {
                EntityUtil.cancelBuffs(entityHit);
                this.launch(entityHit, true);
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    private void flameRadagonShockwave(float spreadarc, int distance, float vec, int delay, float pos, float offset, float damage) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta1 = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta1);
        double vecZ = Math.sin(theta1);
        double x = this.getX() + pos * vecX + f * offset;
        double z = this.getZ() + pos * vecZ + f1 * offset;
        double facingAngle = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double spread = Math.PI * spreadarc;
        int arcLen = Mth.ceil(distance * spread);
        for (int i = 0; i < arcLen; ++i) {
            double theta = (i / (arcLen - 1.0D) - 0.5D) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = x + vx * distance + vec * Math.cos((this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = z + vz * distance + vec * Math.sin((this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            this.spawnFlames(Mth.floor(px) + 0.5D, Mth.floor(pz) + 0.5D, this.getY() - 5.0D, this.getY() + 3.0D, (float) theta, delay, damage);
        }
    }

    private void spawnFlames(double x, double z, double minY, double maxY, float rotation, int delay, float damage) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (!blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                continue;
            }
            if (!this.level().isEmptyBlock(blockpos)) {
                VoxelShape voxelshape = this.level().getBlockState(blockpos).getCollisionShape(this.level(), blockpos);
                if (!voxelshape.isEmpty()) {
                    d0 = voxelshape.max(Direction.Axis.Y);
                }
            }
            flag = true;
            break;
        } while ((blockpos = blockpos.below()).getY() >= Mth.floor(minY) - 1);
        if (flag) {
            this.level().addFreshEntity(new AnnihilationFlameStrike(this.level(), x, blockpos.getY() + d0, z, rotation, delay, this, 20, damage));
        }
    }

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
        Vec3 vec3 = this.position();
        if (this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.SHULKER_TELEPORT, 4.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    public boolean teleportBoolean(double x, double y, double z, boolean p_20988_) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(blockpos)) {
            boolean flag1 = false;
            while (!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                if (level.getBlockState(blockpos1).blocksMotion()) {
                    flag1 = true;
                    continue;
                }
                d3 -= 1.0D;
                blockpos = blockpos1;
            }
            if (flag1) {
                this.teleportTo(x, d3, z);
                EntityUtil.applyServerTeleport(this);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            this.teleportTo(d0, d1, d2);
            EntityUtil.applyServerTeleport(this);
            return false;
        }
        if (p_20988_) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("stomp_combo")) {
            return this.stompComboAnimationState;
        }
        if (input.equals("stomp_combo_end")) {
            return this.stompComboEndAnimationState;
        }
        if (input.equals("stomp_combo_teleport_end")) {
            return this.stompComboTeleportEndAnimationState;
        }
        if (input.equals("single_slash")) {
            return this.singleSlashCutAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        if (input.equals("sleep")) {
            return this.sleepAnimationState;
        }
        if (input.equals("awaken")) {
            return this.awakenAnimationState;
        }
        if (input.equals("single_slash_from")) {
            return this.singleSlashFromAnimationState;
        }
        if (input.equals("single_slash_from_parry")) {
            return this.singleSlashFromParryAnimationState;
        }
        if (input.equals("single_slash_from_fail")) {
            return this.singleSlashFromFailAnimationState;
        }
        if (input.equals("single_slash_double")) {
            return this.singleSlashDoubleAnimationState;
        }
        if (input.equals("single_slash_fail")) {
            return this.singleSlashFailAnimationState;
        }
        if (input.equals("teleport_slam")) {
            return this.teleportSlamAnimationState;
        }
        if (input.equals("grab_pre")) {
            return this.grabPreAnimationState;
        }
        if (input.equals("grab_success")) {
            return this.grabSuccessAnimationState;
        }
        if (input.equals("grab_fail")) {
            return this.grabFailAnimationState;
        }
        if (input.equals("teleport_chase")) {
            return this.teleportChaseAnimationState;
        }
        if (input.equals("teleport_chase_next")) {
            return this.teleportChaseNextAnimationState;
        }
        if (input.equals("buckshot")) {
            return this.buckshotAnimationState;
        }
        if (input.equals("buckshot_end")) {
            return this.buckshotEndAnimationState;
        }
        if (input.equals("buckshot_tp")) {
            return this.buckshotTPAnimationState;
        }
        return new AnimationState();
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.stompComboAnimationState.stop();
        this.stompComboEndAnimationState.stop();
        this.stompComboTeleportEndAnimationState.stop();
        this.deathAnimationState.stop();
        this.sleepAnimationState.stop();
        this.awakenAnimationState.stop();
        this.singleSlashFromAnimationState.stop();
        this.singleSlashFromParryAnimationState.stop();
        this.singleSlashFromFailAnimationState.stop();
        this.singleSlashCutAnimationState.stop();
        this.singleSlashDoubleAnimationState.stop();
        this.singleSlashFailAnimationState.stop();
        this.grabPreAnimationState.stop();
        this.grabSuccessAnimationState.stop();
        this.grabFailAnimationState.stop();
        this.teleportChaseAnimationState.stop();
        this.teleportSlamAnimationState.stop();
        this.teleportChaseNextAnimationState.stop();
        this.buckshotAnimationState.stop();
        this.buckshotEndAnimationState.stop();
        this.buckshotTPAnimationState.stop();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> this.startAnimation(this.idleAnimationState);
                case 2 -> this.startAnimation(this.stompComboAnimationState);
                case 3 -> this.startAnimation(this.stompComboEndAnimationState);
                case 4 -> this.startAnimation(this.stompComboTeleportEndAnimationState);
                case 5 -> this.startAnimation(this.singleSlashCutAnimationState);
                case 6 -> this.startAnimation(this.sleepAnimationState);
                case 7 -> this.startAnimation(this.awakenAnimationState);
                case 8 -> this.startAnimation(this.singleSlashFromAnimationState);
                case 9 -> this.startAnimation(this.singleSlashFromParryAnimationState);
                case 10 -> this.startAnimation(this.singleSlashFromFailAnimationState);
                case 11 -> this.startAnimation(this.singleSlashDoubleAnimationState);
                case 12 -> this.startAnimation(this.singleSlashFailAnimationState);
                case 13 -> this.startAnimation(this.teleportSlamAnimationState);
                case 14 -> this.startAnimation(this.grabPreAnimationState);
                case 15 -> this.startAnimation(this.grabSuccessAnimationState);
                case 16 -> this.startAnimation(this.grabFailAnimationState);
                case 17 -> this.startAnimation(this.deathAnimationState);
                case 18 -> this.startAnimation(this.teleportChaseAnimationState);
                case 19 -> this.startAnimation(this.teleportChaseNextAnimationState);
                case 21 -> this.startAnimation(this.buckshotAnimationState);
                case 22 -> this.startAnimation(this.buckshotEndAnimationState);
                case 23 -> this.startAnimation(this.buckshotTPAnimationState);
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void startAnimation(AnimationState state) {
        this.stopAllAnimationStates();
        state.startIfStopped(this.tickCount);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        this.deathTime = 0;
        this.stopAllAnimationStates();
        this.setAttackState(17);
        super.die(pDamageSource);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 85) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

}
