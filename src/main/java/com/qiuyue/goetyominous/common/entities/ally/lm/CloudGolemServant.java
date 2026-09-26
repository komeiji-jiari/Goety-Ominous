package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoalMin;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.LaserGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.MultipleHitGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.CloudEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ElectricityEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EnergyBeamEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.LightningBoltEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.Tornado;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.item.ModItems;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.ParticleUtils;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CloudGolemServant extends IAnimatedMiniBossServant {

    public static final EntityDataAccessor<Boolean> GAVE_CHANCE =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TEXTURE_VARIANT1 =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TEXTURE_VARIANT2 =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BREAK =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> PTICKS =
            SynchedEntityData.defineId(CloudGolemServant.class, EntityDataSerializers.INT);

    public float LayerBrightness;
    public float oLayerBrightness;
    public float LayerTicks;

    private static final int SLEEP_STANDBY_DELAY = 20;
    private static final int AWAKE_TICKS = 20;

    private int stunCooldown = 0;
    private int standbyTicks = 0;

    public int tornado = 0;
    public int pullCooldown = 160;
    public int cooldownTicksThunder = 0;
    public int cooldownStompLeft = 0;
    public int chargeCooldown = 0;
    public boolean hasSwitched = false;
    public int safetyShouldLaserTornadoSwitchCooldown = 60;
    public int GolemInvulnerabilityTime = 0;

    private int blockhitCooldown = 0;
    private int electricBurstCooldown = 0;
    public int laserCooldown = 300;

    public final int BIG_SMASH_COOLDOWN = 30;
    public int bigsmashCooldown = 0;
    public int bigsmash2Cooldown = 0;
    public int flySmashCooldown = 0;
    public int cloudSwarmCooldown = 0;

    public final int CLOUD_SWARM_SUMMON_COOLDOWN = 40;
    public final int FLY_SMASH_COOLDOWN = 0;
    public final int ELECTRIC_BURST_COOLDOWN = 0;
    public final int TORNADO_SHOOT_COOLDOWN = 40;
    public final int LIGHTNING_STRIKE_COOLDOWN = 0;
    public final int BLOCK_HIT_COOLDOWN = 60;

    public boolean shouldDoExtraDashes = true;
    public boolean particle1 = false;
    public int attackLock = 0;
    private boolean DiedOnce = false;
    private int ArrowDamageCooldown = 0;

    public int cloudGolemDeathTime;
    public int DamageCap = 6;

    public boolean shouldLaserAfterTornado = false;

    public final AnimationState DeathAnimationState = new AnimationState();
    public int DeathAnimationTimeout = 40;
    public final AnimationState BlockHitStunAState = new AnimationState();
    public final AnimationState PreFractureFallAnimationState = new AnimationState();
    public final AnimationState FractureLandAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState awakeAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState attackarm1AnimationState = new AnimationState();
    public final AnimationState attackarm2AnimationState = new AnimationState();
    public final AnimationState attackarmsAnimationState = new AnimationState();
    public final AnimationState lightningSummonAnimationState = new AnimationState();
    public final AnimationState p2AState = new AnimationState();
    public final AnimationState landAnimationState = new AnimationState();
    public final AnimationState fallAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState runpreAnimationState = new AnimationState();
    public final AnimationState postRunAnimationState = new AnimationState();
    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState chargepreAnimationState = new AnimationState();
    public final AnimationState chargeEndAnimationState = new AnimationState();
    public final AnimationState chargeAggresiveEndAnimationState = new AnimationState();
    public final AnimationState ExplodeAnimationState = new AnimationState();
    public final AnimationState cloudSummonAnimationState = new AnimationState();
    public final AnimationState mhitAnimationState = new AnimationState();
    public final AnimationState flipAnimationState = new AnimationState();
    public final AnimationState cloudSummonBigAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState bhdbAnimationState = new AnimationState();
    public final AnimationState bhAnimationState = new AnimationState();
    public final AnimationState laserAnimationState = new AnimationState();
    public final AnimationState laser2AnimationState = new AnimationState();
    public final AnimationState stompLeftAState = new AnimationState();
    public final AnimationState stompAState = new AnimationState();
    public final AnimationState respawnAState = new AnimationState();
    public final AnimationState flyAState = new AnimationState();
    public final AnimationState blockAState = new AnimationState();

    public CloudGolemServant(EntityType<? extends IAnimatedMiniBossServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100;
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_VARIANT, 0);
        this.entityData.define(BREAK, 0);
        this.entityData.define(GAVE_CHANCE, false);
        this.entityData.define(PTICKS, 0);
        this.entityData.define(TEXTURE_VARIANT2, 0);
        this.entityData.define(TEXTURE_VARIANT1, 0);
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int value) {
        this.entityData.set(TEXTURE_VARIANT, value);
    }

    public boolean getGaveChance() {
        return this.entityData.get(GAVE_CHANCE);
    }

    public void setGaveChance(boolean value) {
        this.entityData.set(GAVE_CHANCE, value);
    }

    public int getPticks() {
        return this.entityData.get(PTICKS);
    }

    public void setPticks(int value) {
        this.entityData.set(PTICKS, value);
    }

    public int getTextureVariant2() {
        return this.entityData.get(TEXTURE_VARIANT2);
    }

    public void setTextureVariant2(int value) {
        this.entityData.set(TEXTURE_VARIANT2, value);
    }

    public int getTextureVariant1() {
        return this.entityData.get(TEXTURE_VARIANT1);
    }

    public void setTextureVariant1(int value) {
        this.entityData.set(TEXTURE_VARIANT1, value);
    }

    public boolean isBroken() {
        return this.entityData.get(BREAK) >= 3;
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public boolean isAngry() {
        return this.getTextureVariant() == 1 || this.getTextureVariant() == 3;
    }

    public boolean isSleep() {
        return this.getAttackState() == 1 || this.getAttackState() == 2;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 1 : 0);
    }

    private boolean isStandby() {
        return this.isStaying() && !this.isCommanded() && this.getTarget() == null;
    }

    public boolean canLaser() {
        LivingEntity target = this.getTarget();
        return this.laserCooldown <= 0 && target != null && this.distanceTo(target) >= 4.0F;
    }

    public boolean shouldGiveHitChance() {
        return this.getTarget() != null && ModConfig.MOB_CONFIG.CloudGolemLosesConsciousness.get();
    }

    public boolean shouldChargeOnceMore() {
        return this.shouldDoExtraDashes && this.getCrackiness() == Crackiness.HIGH
                && ModConfig.MOB_CONFIG.CloudGolemCanCharge2Times.get();
    }

    public boolean setInLaserMode(boolean isLaserMode) {
        return isLaserMode;
    }

    public boolean getShouldLaserAfterTornado() {
        return this.shouldLaserAfterTornado;
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return this.getAttackState() != 9;
    }

    public boolean canBreak() {
        return this.getAttackState() == 18 || this.getAttackState() == 19;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CloudGolemServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CloudGolemServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CloudGolemServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.CloudGolemServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CloudGolemServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.CloudGolemServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CloudGolemServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CloudGolemServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CloudGolemServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CloudGolemServantDamage.get());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CGA.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.CGH.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CGD.get();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotionEffectInstance) {
        return true;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public double damageCap() {
        return ModConfig.MOB_CONFIG.CloudGolemDamageCap.get();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 17, 15, 30, 110, 18.0F, 4.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && Math.random() < 0.35D
                        && CloudGolemServant.this.flySmashCooldown <= 0
                        && CloudGolemServant.this.getTarget() != null
                        && CloudGolemServant.this.getAttackState() != 27
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado
                        && !CloudGolemServant.this.getTarget().isPassenger()
                        && !CloudGolemServant.this.canLaser();
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.flySmashCooldown = 0;
                CloudGolemServant.this.setNoGravity(false);
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 27, 28, 20, 16, 15.0F, 4.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.chargeCooldown <= 0
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado
                        && !CloudGolemServant.this.canLaser()
                        && CloudGolemServant.this.getTarget() != null
                        && !CloudGolemServant.this.getTarget().isPassenger();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 30, 27, 28, 20, 100) {
            @Override
            public void start() {
                super.start();
                CloudGolemServant.this.shouldDoExtraDashes = false;
            }

            @Override
            public boolean canUse() {
                LivingEntity target = CloudGolemServant.this.getTarget();
                return super.canUse() && CloudGolemServant.this.shouldChargeOnceMore()
                        && target != null && CloudGolemServant.this.distanceTo(target) >= 5.0F;
            }
        });

        this.goalSelector.addGoal(1, new LaserGoal(this, 0, 14, 33, 107, 107, 15.0F, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && CloudGolemServant.this.laserCooldown <= 0
                        && CloudGolemServant.this.shouldGiveHitChance()
                        && CloudGolemServant.this.getTarget() != null
                        && CloudGolemServant.this.isAngry()
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.getAttackState() != 27
                        && CloudGolemServant.this.attackLock == 0;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.shouldLaserAfterTornado = false;
                CloudGolemServant.this.setNoGravity(false);
                CloudGolemServant.this.laserCooldown = 500;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 33, 33, 32, 100, 100));

        this.goalSelector.addGoal(0, new IStateGoal(this, 32, 32, 0, 73, 0) {
            @Override
            public void start() {
                super.start();
                CloudGolemServant.this.setPticks(0);
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 28, 28, 30, 25, 0) {
            @Override
            public void tick() {
                if (this.entity.onGround()) {
                    Vec3 vector3d = this.entity.getDeltaMovement();
                    float f = this.entity.getYRot() * ((float) Math.PI / 180);
                    Vec3 vector3d1 = new Vec3(-Mth.sin(f), this.entity.getDeltaMovement().y, Mth.cos(f))
                            .scale(0.75D).add(vector3d.scale(0.9D));
                    this.entity.setDeltaMovement(vector3d1.x, this.entity.getDeltaMovement().y, vector3d1.z);
                }
            }
        });

        this.goalSelector.addGoal(0, new IAttackGoal(this, 28, 30, 0, 30, 45, 3.0F) {
            @Override
            public void start() {
                this.entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.chargeCooldown = 220;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 30, 30, 0, 30, 40) {
            @Override
            public void start() {
                super.start();
                this.entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.chargeCooldown = 220;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 15, 15, 16, 100, 100));
        this.goalSelector.addGoal(0, new IStateGoal(this, 21, 21, 0, 50, 0));

        this.goalSelector.addGoal(0, new IStateGoal(this, 16, 16, 0, 30, 0) {
            @Override
            public void start() {
                super.start();
                ParticleUtils.controlledSmashParticles(CloudGolemServant.this, 0.0F, 0.0F, 0.0F, 7.5F, 3.0F);
                CloudGolemServant.this.setPticks(0);
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.bigsmash2Cooldown = 80;
            }
        });

        this.goalSelector.addGoal(1, new LaserGoal(this, 0, 14, 15, 107, 107, 15.0F, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && CloudGolemServant.this.laserCooldown <= 0
                        && !CloudGolemServant.this.shouldGiveHitChance()
                        && CloudGolemServant.this.getTarget() != null
                        && CloudGolemServant.this.isAngry()
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.getAttackState() != 27
                        && CloudGolemServant.this.attackLock == 0;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.shouldLaserAfterTornado = false;
                CloudGolemServant.this.setNoGravity(false);
                CloudGolemServant.this.laserCooldown = 500;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 15, 15, 16, 100, 100));

        this.goalSelector.addGoal(0, new IStateGoal(this, 16, 16, 0, 30, 0) {
            @Override
            public void start() {
                super.start();
                CloudGolemServant.this.setPticks(0);
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 26, 26, 0, 45, 20, false));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 18, 0, 37, 37, 5.0F) {
            @Override
            public void start() {
                CloudGolemServant.this.setPticks(0);
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && !CloudGolemServant.this.canLaser()
                        && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 14.0F
                        && CloudGolemServant.this.electricBurstCooldown <= 0
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.electricBurstCooldown = 60;
            }
        });

        this.goalSelector.addGoal(1, new MultipleHitGoal(this, 0, 19, 0, 62, 12, 25, 32, 0, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !CloudGolemServant.this.canLaser()
                        && CloudGolemServant.this.isAngry()
                        && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 20.0F
                        && CloudGolemServant.this.electricBurstCooldown <= 0
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.electricBurstCooldown = 0;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 23, 0, 60, 60, 13.0F, 6.0F) {
            @Override
            public void start() {
                CloudGolemServant.this.setPticks(0);
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && !CloudGolemServant.this.canLaser()
                        && CloudGolemServant.this.tornado <= 0
                        && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && CloudGolemServant.this.getTarget() != null
                        && !CloudGolemServant.this.getTarget().isPassenger()
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.tornado = 40;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 31, 0, 100, 100, 16.0F) {
            @Override
            public void start() {
                CloudGolemServant.this.setPticks(0);
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.getAttackState() != 25
                        && CloudGolemServant.this.attackLock == 0
                        && CloudGolemServant.this.cooldownTicksThunder <= 0
                        && CloudGolemServant.this.isAngry()
                        && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && !CloudGolemServant.this.canLaser()
                        && !CloudGolemServant.this.shouldLaserAfterTornado;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.cooldownTicksThunder = 0;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 20, 0, 50, 80, 18.0F, 7.0F) {
            @Override
            public void start() {
                CloudGolemServant.this.setPticks(0);
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.getAttackState() != 25
                        && !CloudGolemServant.this.canLaser()
                        && !CloudGolemServant.this.shouldLaserAfterTornado
                        && CloudGolemServant.this.attackLock == 0
                        && CloudGolemServant.this.cooldownTicksThunder <= 0
                        && !CloudGolemServant.this.isAngry();
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.cooldownTicksThunder = 0;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 25, 0, 56, 56, 12.0F, 5.0F) {
            @Override
            public void start() {
                CloudGolemServant.this.setPticks(0);
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && CloudGolemServant.this.isAngry()
                        && !CloudGolemServant.this.shouldLaserAfterTornado
                        && CloudGolemServant.this.getAttackState() != 27
                        && CloudGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && !CloudGolemServant.this.canLaser()
                        && CloudGolemServant.this.attackLock == 0
                        && CloudGolemServant.this.cooldownStompLeft <= 0;
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.cooldownStompLeft = 30;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 9, 0, 60, 60, 7.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !CloudGolemServant.this.isAngry()
                        && CloudGolemServant.this.getAttackState() != 13
                        && CloudGolemServant.this.cloudSwarmCooldown <= 0
                        && CloudGolemServant.this.attackLock == 0
                        && !CloudGolemServant.this.shouldLaserAfterTornado
                        && !CloudGolemServant.this.canLaser();
            }

            @Override
            public void stop() {
                super.stop();
                CloudGolemServant.this.cloudSwarmCooldown = 40;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 1, 1, 0, 0, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });

        this.goalSelector.addGoal(0, new IAttackGoal(this, 1, 2, 0, 20, 50, 8.0F));
    }

    @Override
    public void tick() {
        if (this.shouldLaserAfterTornado && this.getAttackState() != 14 && this.getAttackState() != 23
                && this.safetyShouldLaserTornadoSwitchCooldown > 0) {
            --this.safetyShouldLaserTornadoSwitchCooldown;
        }
        if (this.safetyShouldLaserTornadoSwitchCooldown <= 0) {
            this.shouldLaserAfterTornado = false;
            this.safetyShouldLaserTornadoSwitchCooldown = 60;
        }
        if (this.ArrowDamageCooldown > 0) {
            --this.ArrowDamageCooldown;
        }
        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null) {
            this.DamageCap = currentTarget instanceof Player ? 6 : 27;
        }
        if (this.getAttackState() == 0 && this.attackLock == 1 && !this.isAngry()) {
            this.setAttackState(21);
        }
        if (!this.level().isClientSide) {
            if (this.isStandby() && this.getAttackState() == 0 && this.attackLock == 0) {
                if (++this.standbyTicks >= SLEEP_STANDBY_DELAY) {
                    this.setSleep(true);
                }
            } else {
                this.standbyTicks = 0;
            }
        }
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 1 && !this.isStandby()) {
                this.setAttackState(2);
            } else if (this.getAttackState() == 2 && this.attackTicks >= AWAKE_TICKS) {
                this.setSleep(false);
            }
        }
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
            this.LayerTicks += 1.0F;
            this.LayerBrightness += (0.0F - this.LayerBrightness) * 0.8F;
        }
        if (this.isBroken() && !this.isAngry()) {
            this.setTextureVariant(2);
        }
        if (this.isBroken() && this.isAngry()) {
            this.setTextureVariant(3);
        }
        if (this.getCrackiness() == Crackiness.MEDIUM && !this.isAngry() && this.attackLock == 0) {
            this.attackLock = 1;
        }
        if (this.getAttackState() != 9) {
            this.particle1 = false;
            this.setTextureVariant1(0);
        }
        if (this.getAttackState() != 11) {
            this.setTextureVariant2(0);
        }
        if (this.getAttackState() != 13) {
            this.setDiscardFriction(false);
        }
        this.updateWithAttack();
        if (this.laserCooldown > 0 && this.isAngry()) {
            --this.laserCooldown;
        }
        if (this.chargeCooldown > 0) {
            --this.chargeCooldown;
        }
        if (this.cloudSwarmCooldown > 0) {
            --this.cloudSwarmCooldown;
        }
        if (this.GolemInvulnerabilityTime > 0) {
            --this.GolemInvulnerabilityTime;
        }
        if (this.cooldownStompLeft > 0) {
            --this.cooldownStompLeft;
        }
        if (this.cooldownTicksThunder > 0) {
            --this.cooldownTicksThunder;
        }
        if (this.getPticks() >= 0) {
            this.setPticks(this.getPticks() + 1);
        }
        if (this.electricBurstCooldown > 0) {
            --this.electricBurstCooldown;
        }
        if (this.getAttackState() != 27 && this.getAttackState() != 28 && this.getAttackState() != 30) {
            this.shouldDoExtraDashes = true;
        }
        if (this.tornado > 0) {
            --this.tornado;
        }
        if (this.blockhitCooldown > 0) {
            --this.blockhitCooldown;
        }
        if (this.pullCooldown > 0) {
            --this.pullCooldown;
        }
        if (this.flySmashCooldown > 0) {
            --this.flySmashCooldown;
        }
        if (this.bigsmashCooldown > 0) {
            --this.bigsmashCooldown;
        }
        if (this.bigsmash2Cooldown > 0) {
            --this.bigsmash2Cooldown;
        }
        if (this.isAngry() && this.stunCooldown > 0) {
            --this.stunCooldown;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAttackState() == 28 && this.attackTicks >= 0 && this.level().isClientSide) {
            float g = (float) Math.toRadians(-this.getYRot() + 180.0F);
            double theta = this.getYRot() * (Math.PI / 180);
            theta += 1.5707963267948966D;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            double spawnX = this.getX() + vecX * 1.5D;
            double spawnZ = this.getZ() + vecZ * 1.5D;
            this.level().addParticle(new Circle.RingData(g, 0.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                    spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
        }
        if (this.getAttackState() != 14 || this.getAttackState() != 17) {
            this.setNoGravity(false);
        }
        if (this.level().isClientSide) {
            if (this.isAngry()) {
                this.level().addParticle(ModParticles.BEAM.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0D, 0.025D, 0.0D);
            } else {
                this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0D, 0.025D, 0.0D);
            }
        }
        this.attackParticle();
        if (this.getTextureVariant1() == 1) {
            this.Sphereparticle(0.55F, 0.0F, 1.5F);
        }
        if (this.horizontalCollision && this.isInWall()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.1D, 0.0D, 0.1D));
        }
    }

    public void attackParticle() {
        if (this.getAttackState() == 16 && this.getPticks() == 2 && this.level().isClientSide) {
            for (int i = 0; i < 360; ++i) {
                BlockState block = this.level().getBlockState(this.blockPosition().below());
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                        this.getX() + Math.cos(i), this.getY(), this.getZ() + Math.sin(i),
                        Math.cos(i) * 0.9D, 0.0D, Math.sin(i) * 0.9D);
            }
            for (int i = 0; i < 360; ++i) {
                BlockState block = this.level().getBlockState(this.blockPosition().below());
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                        this.getX() + Math.cos(i), this.getY(), this.getZ() + Math.sin(i),
                        Math.cos(i) * 1.3D, 0.0D, Math.sin(i) * 1.3D);
            }
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 85.0F, false, Circle.EnumRingBehavior.GROW),
                    this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        if (this.getAttackState() == 25) {
            if (this.getPticks() == 25) {
                this.attackParticleRing(0.0F, -1.0F, 20.0F);
            }
            if (this.getPticks() == 41) {
                this.attackParticleRing(0.0F, 1.0F, 20.0F);
            }
        }
        if (this.getAttackState() == 23) {
            if (this.getPticks() == 21) {
                this.attackParticleRing(0.0F, -1.0F, 20.0F);
            }
            if (this.getPticks() == 37) {
                this.attackParticleRing(0.0F, 1.0F, 20.0F);
            }
        }
        if (this.getAttackState() == 18 && this.getPticks() == 19) {
            this.attackParticleRing(1.5F, 1.0F, 20.0F);
        }
        if (this.getAttackState() == 19 && this.getPticks() == 19) {
            this.attackParticleRing(1.5F, 1.0F, 20.0F);
        }
    }

    private void attackParticleRing(float radius, float side, float life) {
        if (!this.level().isClientSide) {
            return;
        }
        float angle = (float) Math.PI / 180 * this.yBodyRot;
        double extraX = radius * Mth.sin((float) (Math.PI + angle)) + side * 0.5D;
        double extraZ = radius * Mth.cos(angle) - side * 0.5D * Mth.sin(angle);
        this.ParticleBlock(1, this.getX() + extraX, this.getY(), this.getZ() + extraZ);
        this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, 1.0F, 1.0F, 1.0F, 1.0F, life, false, Circle.EnumRingBehavior.GROW),
                this.getX() + extraX, this.getY() + 0.1D, this.getZ() + extraZ, 0.0D, 0.0D, 0.0D);
    }

    private void Sphereparticle(float height, float vec, float size) {
        if (!this.level().isClientSide || this.tickCount % 2 != 0) {
            return;
        }
        double d0 = this.getX();
        double d1 = this.getY() + height;
        double d2 = this.getZ();
        double theta = (double) this.yBodyRot * (Math.PI / 180);
        theta += 1.5707963267948966D;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        for (float i = -size; i <= size; i += 1.0F) {
            for (float j = -size; j <= size; j += 1.0F) {
                for (float k = -size; k <= size; k += 1.0F) {
                    double d3 = j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d4 = i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d5 = k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d6 = Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / 0.5D + this.random.nextGaussian() * 0.05D;
                    this.level().addParticle(ParticleTypes.CLOUD, d0 + (double) vec * vecX, d1, d2 + (double) vec * vecZ,
                            d3 / d6, d4 / d6, d5 / d6);
                    if (i == -size || i == size || j == -size || j == size) {
                        continue;
                    }
                    k += size * 2.0F - 1.0F;
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.GolemInvulnerabilityTime > 0 && ModConfig.MOB_CONFIG.CloudGolemInvulnerabilityTime.get()) {
            return false;
        }
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.FALL) || source.is(DamageTypes.LIGHTNING_BOLT)) {
            return false;
        }
        if (this.entityData.get(BREAK) == 3) {
            this.entityData.set(BREAK, this.entityData.get(BREAK) + 1);
            this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 2.0F, 1.0F);
        }
        if (source.getEntity() instanceof Player player) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.getItem() instanceof AxeItem && player.getAttackStrengthScale(0.5F) >= 1.0F && this.canBreak()) {
                this.entityData.set(BREAK, this.entityData.get(BREAK) + 1);
                if (!this.isBroken()) {
                    float radius = 1.0F;
                    float angle = (float) Math.PI / 180 * this.yBodyRot;
                    double extraX = radius * Mth.sin((float) (Math.PI + angle)) + 0.5D;
                    double extraZ = radius * Mth.cos(angle) - 0.5D * Mth.sin(angle);
                    this.ParticleBlock(1, this.getX() + extraX, this.getY() + 1.0D, this.getZ() + extraZ);
                    this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 2.0F, 1.0F);
                }
            }
        }
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
            return false;
        }
        if (source.is(DamageTypes.FALL)) {
            return false;
        }
        if (source.getDirectEntity() instanceof AbstractArrow && this.isAngry()) {
            if (this.ArrowDamageCooldown > 0) {
                return false;
            }
            if (ModConfig.MOB_CONFIG.CloudGolemProjectileImmunityTimer.get()) {
                this.ArrowDamageCooldown = 30;
            }
        }
        if (source.is(DamageTypes.EXPLOSION)) {
            return false;
        }
        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            return false;
        }
        if (source.is(DamageTypes.PLAYER_EXPLOSION)) {
            return false;
        }
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(DamageTypeTags.BYPASSES_ARMOR)
                && this.canBreak() && !this.isBroken() && amount > 5.0F) {
            amount = 4.0F;
        }
        if ((this.isSleep() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) || this.getAttackState() == 21) {
            return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt) {
            this.GolemInvulnerabilityTime = 10;
        }
        return hurt;
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
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            ItemStack itemStack = new ItemStack(ModItems.AIR_RUNE.get());
            FlyingItem flyingItem = new FlyingItem(
                    com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                    this.level(),
                    this.getX(),
                    this.getY(),
                    this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(itemStack);
            flyingItem.setParticle(ParticleTypes.HAPPY_VILLAGER);
            flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
            this.level().addFreshEntity(flyingItem);
        }
        this.setAttackState(8);
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.cloudGolemDeathTime;
        if (this.level() instanceof ServerLevel && this.cloudGolemDeathTime > 1 && !this.isSilent()) {
            this.setNoAi(true);
        }
        if (this.cloudGolemDeathTime == 40 && this.level() instanceof ServerLevel) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();
        if (isOwner && this.isAngry() && itemstack.is(ModItems.AIR_RUNE.get())) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.setTextureVariant(0);
            this.attackLock = 0;
            this.setHealth(this.getMaxHealth());
            this.playSound(SoundEvents.BEACON_DEACTIVATE, 2.0F, 0.8F);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getRandomX(1.0D), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0D),
                            0, d0, d1, d2, 0.5F);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DiedOnce", this.DiedOnce);
        compound.putInt("TextureVariant", this.getTextureVariant());
        compound.putBoolean("gaveChance", this.getGaveChance());
        compound.putBoolean("is_Sleep", this.isSleep());
        compound.putInt("break", this.entityData.get(BREAK));
        compound.putInt("StunCooldown", this.stunCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(BREAK, compound.getInt("break"));
        this.setSleep(compound.getBoolean("is_Sleep"));
        this.stunCooldown = compound.getInt("StunCooldown");
        this.setGaveChance(compound.getBoolean("gaveChance"));
        this.DiedOnce = compound.getBoolean("DiedOnce");
        this.setTextureVariant(compound.getInt("TextureVariant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player
                && countServants(player) >= MobsConfig.CloudGolemServantLimit.get()) {
            return null;
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private static int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof CloudGolemServant servant && servant.getTrueOwner() == player) {
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

    public AnimationState getAnimationState(String input) {
        if (input.equals("sleep")) {
            return this.sleepAnimationState;
        }
        if (input.equals("awake")) {
            return this.awakeAnimationState;
        }
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("attackarmright")) {
            return this.attackarm1AnimationState;
        }
        if (input.equals("attackarmleft")) {
            return this.attackarm2AnimationState;
        }
        if (input.equals("attackarms")) {
            return this.attackarmsAnimationState;
        }
        if (input.equals("attacklightning")) {
            return this.lightningSummonAnimationState;
        }
        if (input.equals("cloudattack")) {
            return this.cloudSummonAnimationState;
        }
        if (input.equals("cloudattackbig")) {
            return this.cloudSummonBigAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        if (input.equals("explode")) {
            return this.ExplodeAnimationState;
        }
        if (input.equals("resp")) {
            return this.respawnAState;
        }
        if (input.equals("flip")) {
            return this.flipAnimationState;
        }
        if (input.equals("p2")) {
            return this.p2AState;
        }
        if (input.equals("fly")) {
            return this.flyAState;
        }
        if (input.equals("laser")) {
            return this.laserAnimationState;
        }
        if (input.equals("laser2")) {
            return this.laser2AnimationState;
        }
        if (input.equals("blockhitdb")) {
            return this.bhdbAnimationState;
        }
        if (input.equals("blockhit")) {
            return this.bhAnimationState;
        }
        if (input.equals("land")) {
            return this.landAnimationState;
        }
        if (input.equals("fall")) {
            return this.fallAnimationState;
        }
        if (input.equals("pull")) {
            return this.runAnimationState;
        }
        if (input.equals("pullpre")) {
            return this.runpreAnimationState;
        }
        if (input.equals("postpull")) {
            return this.postRunAnimationState;
        }
        if (input.equals("stompleft")) {
            return this.stompLeftAState;
        }
        if (input.equals("stomp")) {
            return this.stompAState;
        }
        if (input.equals("block")) {
            return this.blockAState;
        }
        if (input.equals("precharge")) {
            return this.chargepreAnimationState;
        }
        if (input.equals("charge")) {
            return this.chargeAnimationState;
        }
        if (input.equals("endcharge")) {
            return this.chargeEndAnimationState;
        }
        if (input.equals("aendcharge")) {
            return this.chargeAggresiveEndAnimationState;
        }
        if (input.equals("mhit")) {
            return this.mhitAnimationState;
        }
        if (input.equals("fractureland")) {
            return this.FractureLandAnimationState;
        }
        if (input.equals("prefracturefall")) {
            return this.PreFractureFallAnimationState;
        }
        if (input.equals("blockhitstun")) {
            return this.BlockHitStunAState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0:
                    if (this.attackTicks != 90 && !this.sleepAnimationState.isStarted()) {
                        break;
                    }
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.sleepAnimationState.startIfStopped(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.awakeAnimationState.startIfStopped(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.attackarm1AnimationState.startIfStopped(this.tickCount);
                    break;
                case 4:
                    this.stopAllAnimationStates();
                    this.attackarm2AnimationState.startIfStopped(this.tickCount);
                    break;
                case 5:
                    this.stopAllAnimationStates();
                    this.lightningSummonAnimationState.startIfStopped(this.tickCount);
                    break;
                case 6:
                    this.stopAllAnimationStates();
                    this.attackarmsAnimationState.startIfStopped(this.tickCount);
                    break;
                case 7:
                    this.stopAllAnimationStates();
                    this.cloudSummonAnimationState.startIfStopped(this.tickCount);
                    break;
                case 8:
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                    break;
                case 9:
                    this.stopAllAnimationStates();
                    this.ExplodeAnimationState.startIfStopped(this.tickCount);
                    break;
                case 10:
                    this.stopAllAnimationStates();
                    this.runpreAnimationState.startIfStopped(this.tickCount);
                    break;
                case 11:
                    this.stopAllAnimationStates();
                    this.runAnimationState.startIfStopped(this.tickCount);
                    break;
                case 12:
                    this.stopAllAnimationStates();
                    this.postRunAnimationState.startIfStopped(this.tickCount);
                    break;
                case 13:
                    this.attackTicks = 0;
                    break;
                case 14:
                    this.stopAllAnimationStates();
                    this.laserAnimationState.startIfStopped(this.tickCount);
                    break;
                case 15:
                    this.stopAllAnimationStates();
                    this.fallAnimationState.startIfStopped(this.tickCount);
                    break;
                case 16:
                    this.stopAllAnimationStates();
                    this.landAnimationState.startIfStopped(this.tickCount);
                    break;
                case 17:
                    this.stopAllAnimationStates();
                    this.flyAState.startIfStopped(this.tickCount);
                    break;
                case 18:
                    this.stopAllAnimationStates();
                    this.bhAnimationState.startIfStopped(this.tickCount);
                    break;
                case 19:
                    this.stopAllAnimationStates();
                    this.bhdbAnimationState.startIfStopped(this.tickCount);
                    break;
                case 20:
                    this.stopAllAnimationStates();
                    this.cloudSummonBigAnimationState.startIfStopped(this.tickCount);
                    break;
                case 21:
                    this.stopAllAnimationStates();
                    this.p2AState.startIfStopped(this.tickCount);
                    break;
                case 22:
                    this.stopAllAnimationStates();
                    this.laser2AnimationState.startIfStopped(this.tickCount);
                    break;
                case 23:
                    this.setPticks(0);
                    this.stopAllAnimationStates();
                    this.stompAState.startIfStopped(this.tickCount);
                    break;
                case 24:
                    this.stopAllAnimationStates();
                    this.flipAnimationState.startIfStopped(this.tickCount);
                    break;
                case 25:
                    this.stopAllAnimationStates();
                    this.stompLeftAState.startIfStopped(this.tickCount);
                    break;
                case 26:
                    this.stopAllAnimationStates();
                    this.blockAState.startIfStopped(this.tickCount);
                    break;
                case 27:
                    this.setPticks(0);
                    this.stopAllAnimationStates();
                    this.chargepreAnimationState.startIfStopped(this.tickCount);
                    break;
                case 28:
                    this.stopAllAnimationStates();
                    this.chargeAnimationState.startIfStopped(this.tickCount);
                    break;
                case 29:
                    this.stopAllAnimationStates();
                    this.chargeEndAnimationState.startIfStopped(this.tickCount);
                    break;
                case 30:
                    this.stopAllAnimationStates();
                    this.chargeAggresiveEndAnimationState.startIfStopped(this.tickCount);
                    break;
                case 31:
                    this.stopAllAnimationStates();
                    this.mhitAnimationState.startIfStopped(this.tickCount);
                    break;
                case 32:
                    this.stopAllAnimationStates();
                    this.FractureLandAnimationState.startIfStopped(this.tickCount);
                    break;
                case 33:
                    this.stopAllAnimationStates();
                    this.PreFractureFallAnimationState.startIfStopped(this.tickCount);
                    break;
                case 34:
                    this.stopAllAnimationStates();
                    this.BlockHitStunAState.startIfStopped(this.tickCount);
                    break;
                default:
                    break;
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.BlockHitStunAState.stop();
        this.sleepAnimationState.stop();
        this.PreFractureFallAnimationState.stop();
        this.FractureLandAnimationState.stop();
        this.awakeAnimationState.stop();
        this.attackarmsAnimationState.stop();
        this.lightningSummonAnimationState.stop();
        this.attackarm1AnimationState.stop();
        this.attackarm2AnimationState.stop();
        this.mhitAnimationState.stop();
        this.chargepreAnimationState.stop();
        this.chargeAnimationState.stop();
        this.chargeEndAnimationState.stop();
        this.chargeAggresiveEndAnimationState.stop();
        this.blockAState.stop();
        this.deathAnimationState.stop();
        this.stompLeftAState.stop();
        this.flipAnimationState.stop();
        this.respawnAState.stop();
        this.laserAnimationState.stop();
        this.flyAState.stop();
        this.stompAState.stop();
        this.laser2AnimationState.stop();
        this.p2AState.stop();
        this.cloudSummonBigAnimationState.stop();
        this.bhAnimationState.stop();
        this.bhdbAnimationState.stop();
        this.fallAnimationState.stop();
        this.landAnimationState.stop();
        this.postRunAnimationState.stop();
        this.runAnimationState.stop();
        this.runpreAnimationState.stop();
        this.ExplodeAnimationState.stop();
        this.cloudSummonAnimationState.stop();
    }

    public void launchAOE() {
        double knockbackRadius = 5.0D;
        List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox().inflate(knockbackRadius),
                e -> e instanceof LivingEntity && e != this);
        for (Entity target : nearbyEntities) {
            if (MobUtil.areAllies(this, target)) {
                continue;
            }
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            double knockbackStrength = 0.5D + 0.25D * (knockbackRadius - distance);
            if (target.onGround()) {
                target.push(dx / distance * knockbackStrength, 0.3D, dz / distance * knockbackStrength);
                continue;
            }
            double knockbackStrength2 = 1.0D + 0.5D * (knockbackRadius - distance);
            target.push(dx / distance * knockbackStrength2, 0.4D, dz / distance * knockbackStrength2);
        }
    }

    private void launchMini(LivingEntity entity, boolean huge) {
        double deltaX = entity.getX() - this.getX();
        double deltaZ = entity.getZ() - this.getZ();
        double distanceSquared = Math.max(deltaX * deltaX + deltaZ * deltaZ, 0.001D);
        float multiplier = huge ? 1.2F : 0.5F;
        entity.push(deltaX / distanceSquared * multiplier, huge ? 0.3D : 0.2D, deltaZ / distanceSquared * multiplier);
    }

    private void AreaAttack(float range, float height, float arc, float damage, int shieldbreakticks, boolean stun,
                            float knockback, boolean BNknockback, float Lstrenght, boolean launch) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX())
                    * 57.29577951308232D - 90.0D) % 360.0D);
            float entityAttackingAngle = this.yBodyRot % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if ((entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F)
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F) {
                if (MobUtil.areAllies(this, entityHit) || entityHit instanceof CloudGolemServant || entityHit == this) {
                    continue;
                }
                boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) (damage * ModConfig.MOB_CONFIG.CloudGolemDamageMutliplier.get()));
                if (hurt) {
                    EntityUtil.cancelBuffs(entityHit);
                }
                if ((this.getAttackState() == 4 || this.getAttackState() == 3) && this.attackTicks == 20) {
                    if (BNknockback && !entityHit.isBlocking()) {
                        double knockbackRadius = 5.0D;
                        double dx = entityHit.getX() - this.getX();
                        double dz = entityHit.getZ() - this.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double knockbackStrength = knockback + 0.5D * (knockbackRadius - distance);
                        entityHit.push(dx / distance * knockbackStrength, 0.4D, dz / distance * knockbackStrength);
                    }
                    CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
                }
                if (this.getAttackState() == 4) {
                    this.launchMini(entityHit, true);
                }
                if (this.getAttackState() == 30 && hurt) {
                    this.playSound(SoundEvents.ANVIL_PLACE, 2.0F, 1.0F);
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 55, 0));
                }
                if (this.getAttackState() == 9) {
                    this.launchAOE();
                }
                if (this.getAttackState() == 16) {
                    this.launchAOE();
                }
                if (this.getAttackState() == 21) {
                    this.launchAOE();
                }
                if (this.getAttackState() == 12) {
                    this.launchAOE();
                }
                if (this.getAttackState() == 3) {
                    this.launchMini(entityHit, true);
                }
                if (this.getAttackState() == 6) {
                    this.launch(entityHit, true);
                }
                if (this.getAttackState() == 31) {
                    this.launch(entityHit, true);
                }
                if (this.getAttackState() == 18) {
                    this.launch(entityHit, true);
                }
                if (this.getAttackState() == 19) {
                    this.launch(entityHit, true);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && shieldbreakticks > 0) {
                    disableShield(entityHit, shieldbreakticks);
                }
            }
        }
    }

    public void updateWithAttack() {
        if (this.getAttackState() == 33) {
            if (this.onGround()) {
                this.setAttackState(32);
            } else {
                this.Particle(30, this.getX(), this.getY(), this.getZ());
            }
        }
        if (this.getAttackState() == 32) {
            this.setPos(this.getX(), Mth.floor(this.getY()), this.getZ());
            if (this.attackTicks == 1) {
                this.playSound(ModSounds.CGD.get(), 1.0F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(4.0F, 4.0F, 130.0F, 16.0F + a, 0, false, 0.5F, false, 0.25F, true);
            }
        }
        if (this.getAttackState() == 31) {
            if (this.attackTicks == 20) {
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(4.0F, 4.0F, 130.0F, 16.0F + a, 0, false, 0.5F, false, 0.25F, true);
                ParticleUtils.controlledSmashParticles(this, 1.5F, 0.0F, 0.0F, 1.5F, 1.0F);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 10.0F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                this.thunderLine(0.0F);
            }
            if (this.attackTicks == 37) {
                ParticleUtils.controlledSmashParticles(this, 1.5F, 0.0F, 0.0F, 1.5F, 1.0F);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 10.0F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(4.0F, 4.0F, 130.0F, 16.0F + a, 0, false, 0.5F, false, 0.25F, true);
                this.thunderLine(0.3F);
            }
            if (this.attackTicks == 68) {
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 1.5F, 1.0F);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 10.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(5.0F, 4.0F, 130.0F, 18.0F + a, 0, false, 0.5F, false, 0.25F, true);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                switch (this.random.nextInt(3)) {
                    case 0:
                        this.spawnDoubleThunder();
                        break;
                    case 1:
                        this.thunderLine(0.0F);
                        break;
                    case 2:
                        this.thunderLine(0.2025F);
                        break;
                    default:
                        break;
                }
            }
        }
        if (this.getAttackState() == 28) {
            Vec3 vector3d = this.getDeltaMovement();
            float f = this.getYRot() * ((float) Math.PI / 180);
            Vec3 vector3d1 = new Vec3(-Mth.sin(f), this.getDeltaMovement().y, Mth.cos(f))
                    .scale(0.45D).add(vector3d.scale(0.6D));
            this.setDeltaMovement(vector3d1.x, this.getDeltaMovement().y, vector3d1.z);
            if (this.horizontalCollision) {
                this.setAttackState(30);
            }
        }
        if (this.getAttackState() == 30 && this.attackTicks == 3) {
            LivingEntity target = this.getTarget();
            int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
            if (target != null) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3.0F, 0.25F);
            }
            this.AreaAttack(5.0F, 4.0F, 230.0F, 22.0F + a, 70, false, 0.5F, false, 0.25F, true);
        }
        if (this.getAttackState() == 25) {
            if (this.attackTicks == 41) {
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 0.5F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
                this.spawnElectricRing(9, 20.0F, 7.0F, Mth.floor(this.getY()));
            }
            if (this.attackTicks == 25) {
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 0.5F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
                this.spawnElectricRing(7, 30.0F, 7.0F, this.getY(0.15D));
            }
        }
        if (this.getAttackState() == 24) {
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 3.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(5.0F, 4.0F, 90.0F, 16.0F + a, 40, false, 0.5F, false, 0.25F, true);
            }
            if (this.attackTicks == 17) {
                this.dash(2.0F, 1.6F, 0.0F);
            }
            if (this.attackTicks == 25) {
                this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 3.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.04D) : 0;
                this.AreaAttack(5.0F, 4.0F, 90.0F, 18.0F + a, 40, false, 0.5F, false, 0.25F, true);
            }
        }
        if (this.getAttackState() == 23) {
            if (this.getPticks() == 23) {
                float radius = 1.0F;
                float angle = (float) Math.PI / 180 * this.yBodyRot;
                double extraX = radius * Mth.sin((float) (Math.PI + angle)) + 0.5D;
                double extraZ = radius * Mth.cos(angle) - 0.5D * Mth.sin(angle);
                this.ParticleBlock(1, this.getX() + extraX, this.getY(), this.getZ() + extraZ);
            }
            if (this.attackTicks == 23 && this.getTarget() != null) {
                this.spawnTornadoes(1, 30.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
            }
            if (this.attackTicks == 38 && this.getTarget() != null) {
                this.spawnTornadoes(1, 30.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
            }
            if (this.attackTicks == 48) {
                switch (this.random.nextInt(2)) {
                    case 0:
                        this.shouldLaserAfterTornado = false;
                        break;
                    case 1:
                        LivingEntity target = this.getTarget();
                        if (!this.isAngry() || target == null) {
                            break;
                        }
                        this.shouldLaserAfterTornado = target.isPassenger()
                                && target.getVehicle() instanceof Tornado && this.laserCooldown <= 0;
                        break;
                    default:
                        break;
                }
            }
        }
        if (this.getAttackState() == 18 && this.attackTicks == 19) {
            CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
            ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 1.5F, 1.0F);
            this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
            LivingEntity target = this.getTarget();
            int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
            this.AreaAttack(5.6F, 4.0F, 130.0F, 19.5F + a, 50, false, 0.5F, false, 0.25F, true);
        }
        if (this.getAttackState() == 21 && this.attackTicks == 25) {
            if (this.getTarget() != null) {
                this.spawnTornadoes(4, 90.0F);
            }
            this.summonBolt(1.0F, 0.0F, 0.0F);
            this.setTextureVariant(1);
            this.playSound(SoundEvents.TRIDENT_THUNDER, 3.0F, 1.0F);
            LivingEntity target = this.getTarget();
            int a = target != null ? (int) (target.getMaxHealth() * 0.06D) : 0;
            this.AreaAttack(6.0F, 4.0F, 180.0F, 14.0F + a, 60, false, 0.5F, false, 0.25F, true);
            this.attackLock = 0;
        }
        if (this.getAttackState() == 20 && this.attackTicks >= 25 && this.attackTicks <= 35) {
            float progress = 2.0F * this.attackTicks - 46.0F;
            this.summonControlledCloud((float) this.getX(), (float) this.getY(), (float) this.getZ(), progress);
        }
        if (this.getAttackState() == 19) {
            if (this.attackTicks == 19) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 1.5F, 1.0F);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.03D) : 0;
                this.AreaAttack(5.6F, 3.0F, 180.0F, 18.0F + a, 0, false, 0.5F, false, 0.25F, true);
            }
            if (this.attackTicks == 35) {
                this.dash(1.3F, 0.9F, 4.0F);
            }
            if (this.attackTicks == 38) {
                double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966D;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                double offset = this.getTarget() != null ? 1.0D : 0.0D;
                for (int i = 0; i < 5; ++i) {
                    float angle = this.yBodyRot + (i - 5 / 2) * 26.0F;
                    float rad = (float) Math.toRadians(angle);
                    ElectricityEntity electricity = new ElectricityEntity(this, -Math.sin(rad), 0.0D, Math.cos(rad),
                            this.level(), 6.0F, angle, 35.0F);
                    electricity.setPos(this.getX() + vecX * offset, Mth.floor(this.getY()), this.getZ() + vecZ * offset);
                    this.level().addFreshEntity(electricity);
                }
            }
            if (this.attackTicks == 40) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
                this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 1.0F, 1.0F);
                LivingEntity target = this.getTarget();
                int a = target != null ? (int) (target.getMaxHealth() * 0.05D) : 0;
                this.AreaAttack(5.0F, 4.0F, 180.0F, 19.0F + a, 60, false, 0.5F, false, 0.25F, true);
            }
        }
        if (this.getAttackState() == 17) {
            if (this.attackTicks == 30) {
                LivingEntity target = this.getTarget();
                this.setNoGravity(false);
                if (target != null) {
                    double distanceToTarget = this.distanceTo(target);
                    float k = (float) (distanceToTarget * 0.02D);
                    this.getLookControl().setLookAt(target, 60.0F, 30.0F);
                    Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY(),
                            target.getZ() - this.getZ()).normalize();
                    if (distanceToTarget < 12.0D) {
                        if (target instanceof Player) {
                            this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 1.5D, -0.6D + k, vec3.z * 1.5D));
                        } else {
                            this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 2.0D, vec3.y - (0.2D + k), vec3.z * 2.0D));
                        }
                    } else if (target instanceof Player) {
                        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 2.0D, -0.6D + k, vec3.z * 2.0D));
                    } else {
                        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 2.0D, vec3.y - (0.2D + k), vec3.z * 2.0D));
                    }
                }
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.0D, 0.0D));
            }
            if (this.attackTicks == 11) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.6D, 0.0D));
            }
            if (this.attackTicks > 11 && this.attackTicks < 30) {
                this.yHeadRot = this.yBodyRot;
                this.setNoGravity(true);
            }
        }
        if (this.getAttackState() == 15) {
            if (this.onGround()) {
                ParticleUtils.controlledSmashParticles(this, 0.0F, 0.0F, 0.0F, 7.5F, 3.0F);
                this.setAttackState(16);
            } else {
                this.Particle(30, this.getX(), this.getY(), this.getZ());
            }
        }
        if (this.getAttackState() == 14) {
            if (this.attackTicks == 1) {
                this.setInLaserMode(false);
                this.spawnBolt((float) this.getX(), (float) this.getY(), (float) this.getZ());
            }
            if (this.attackTicks == 11) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.6D, 0.0D));
            }
            if (this.attackTicks == 20) {
                this.playSound(ModSounds.BEAM_CHARGE.get(), 5.0F, 1.0F);
            }
            if (this.attackTicks > 11 && this.attackTicks < 104) {
                this.setNoGravity(true);
            }
            if (this.attackTicks == 39) {
                this.setInLaserMode(true);
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                this.playSound(ModSounds.BEAM_GO.get(), 5.0F, 1.0F);
            }
            if (this.attackTicks == 19) {
                double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966D;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                double spawnX = this.getX() + vecX * 1.0D;
                double spawnY = this.getY(0.0D);
                double spawnZ = this.getZ() + vecZ * 1.0D;
                EnergyBeamEntity energyBeam = new EnergyBeamEntity(LmEntityRegistry.ENERGY_BEAM.get(), this.level(), this,
                        spawnX, spawnY + 1.0D, spawnZ,
                        (float) ((this.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-this.getXRot() * Math.PI / 180),
                        75, 5.0F, 5.0F);
                this.level().addFreshEntity(energyBeam);
            }
            if (this.attackTicks == 105) {
                this.setInLaserMode(false);
                LivingEntity target = this.getTarget();
                this.setNoGravity(false);
                if (target != null) {
                    this.getLookControl().setLookAt(target, 60.0F, 30.0F);
                    Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY(),
                            target.getZ() - this.getZ()).normalize();
                    this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 2.0D, -0.5D, vec3.z * 2.0D));
                }
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.0D, 0.0D));
            }
        }
        if (this.getAttackState() == 13) {
            if (this.attackTicks == 1) {
                this.setDiscardFriction(true);
            }
            if (this.attackTicks == 40) {
                this.attackLock = 0;
                this.summonBolt(1.0F, 0.0F, 0.0F);
                this.setTextureVariant(1);
            }
        }
        if (this.getAttackState() == 16 && this.attackTicks == 3) {
            CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
            this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
            LivingEntity target = this.getTarget();
            int a = target != null ? (int) (target.getMaxHealth() * 0.08D) : 0;
            this.AreaAttack(6.5F, 5.0F, 360.0F, 25.0F + a, 140, false, 0.5F, false, 0.25F, true);
        }
        if (this.getAttackState() == 11) {
            this.AreaAttack(5.0F, 4.0F, 360.0F, 5.0F, 0, false, 0.5F, false, 0.25F, false);
            this.setTextureVariant2(1);
        }
        if (this.getAttackState() == 9) {
            int damageCloud = 8;
            float cloudDamageRange = 2.0F;
            if (this.attackTicks == 24) {
                if (this.getTarget() != null) {
                    for (int k = 0; k < 6; ++k) {
                        float f2 = k * (float) Math.PI * 2.0F / 6.0F + 1.2566371F;
                        this.spawnCloud(this.getX() + Mth.cos(f2) * 2.5D, this.getZ() + Mth.sin(f2) * 2.5D, 7, cloudDamageRange);
                    }
                }
                this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 3.0F, 0.769F);
                this.particle1 = true;
                this.setTextureVariant1(1);
            }
            if (this.attackTicks == 25) {
                for (int k = 0; k < 9; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 9.0F + 0.62831855F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 3.5D, this.getZ() + Mth.sin(f2) * 3.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 28) {
                for (int k = 0; k < 12; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 12.0F + 0.41887903F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 4.5D, this.getZ() + Mth.sin(f2) * 4.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 31) {
                for (int k = 0; k < 15; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 15.0F + 0.31415927F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 5.5D, this.getZ() + Mth.sin(f2) * 5.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 34) {
                for (int k = 0; k < 18; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 18.0F + 0.25132743F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 6.5D, this.getZ() + Mth.sin(f2) * 6.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 37) {
                for (int k = 0; k < 21; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 21.0F + 0.20943952F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 8.5D, this.getZ() + Mth.sin(f2) * 8.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 40) {
                for (int k = 0; k < 24; ++k) {
                    float f2 = k * (float) Math.PI * 2.0F / 24.0F + 0.17951958F;
                    this.spawnCloud(this.getX() + Mth.cos(f2) * 7.5D, this.getZ() + Mth.sin(f2) * 7.5D, damageCloud, cloudDamageRange);
                }
            }
            if (this.attackTicks == 29) {
                this.particle1 = false;
                this.setTextureVariant1(0);
            }
        }
        if (this.getAttackState() == 5 && this.attackTicks == 13 && this.getTarget() != null) {
            this.spawnLightningBolts();
        }
    }

    private void spawnCloud(double x, double z, int damage, float attackRange) {
        CloudEntity cloud = LmEntityRegistry.CLOUD.get().create(this.level());
        if (cloud == null) {
            return;
        }
        cloud.setPos(x, this.getY() + 8.0D, z);
        cloud.setOwner(this);
        cloud.setParticleOptimalization(true);
        cloud.setDamage(damage);
        cloud.setAttackRange(attackRange);
        this.level().addFreshEntity(cloud);
    }

    private void spawnTornadoes(int count, float angleStep) {
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966D;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        for (int i = 0; i < count; ++i) {
            float angle = this.yBodyRot + (i - count / 2) * angleStep;
            float rad = (float) Math.toRadians(angle);
            Tornado tornado = new Tornado(this, -Math.sin(rad), 0.0D, Math.cos(rad), this.level(), 7.0F, angle, 120.0F);
            tornado.setPos(this.getX() + vecX * 1.0D, this.getY(0.15D), this.getZ() + vecZ * 1.0D);
            this.level().addFreshEntity(tornado);
        }
    }

    private void spawnElectricRing(int count, float angleStep, float damage, double y) {
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966D;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        for (int i = 0; i < count; ++i) {
            float angle = this.yBodyRot + (i - count / 2) * angleStep;
            float rad = (float) Math.toRadians(angle);
            ElectricityEntity electricity = new ElectricityEntity(this, -Math.sin(rad), 0.0D, Math.cos(rad),
                    this.level(), damage, angle, 45.0F);
            electricity.setPos(this.getX() + vecX * 1.0D, y, this.getZ() + vecZ * 1.0D);
            this.level().addFreshEntity(electricity);
        }
    }

    private void thunderLine(float spread) {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        double minY = Math.min(target.getY(), this.getY());
        double maxY = Math.max(target.getY(), this.getY()) + 1.0D;
        float angle = (float) Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX());
        for (int l = 0; l < 16; ++l) {
            double distance = 1.25D * (l + 1);
            if (spread == 0.0F) {
                this.spawnThunder(this.getX() + Mth.cos(angle) * distance, this.getZ() + Mth.sin(angle) * distance,
                        minY, maxY, angle, l);
            } else {
                for (float a : new float[]{angle - spread, angle, angle + spread}) {
                    this.spawnThunder(this.getX() + Mth.cos(a) * distance, this.getZ() + Mth.sin(a) * distance,
                            minY, maxY, a, l);
                }
            }
        }
    }

    private void spawnLightningBolts() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        float angle = (float) Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX());
        float offsetX = Mth.cos(this.getYRot() * ((float) Math.PI / 180)) * 1.1F;
        float offsetZ = Mth.sin(this.getYRot() * ((float) Math.PI / 180)) * 1.1F;
        for (int l = 0; l < 10; ++l) {
            double distance = 1.25D * (l + 1);
            this.strikeLightning(this.getX() + offsetX + Mth.cos(angle) * distance, this.getZ() + offsetZ + Mth.sin(angle) * distance);
            this.strikeLightning(this.getX() - offsetX + Mth.cos(angle) * distance, this.getZ() - offsetZ + Mth.sin(angle) * distance);
        }
    }

    private void strikeLightning(double x, double z) {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos blockpos = new BlockPos((int) x, (int) this.getY(), (int) z);
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                lightningBolt.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.getYRot(), 0.0F);
                serverLevel.addFreshEntity(lightningBolt);
            }
        }
    }

    private void spawnThunder(double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos below = blockpos.below();
            BlockState blockstate = this.level().getBlockState(below);
            if (blockstate.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState state = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = state.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
        } while ((blockpos = blockpos.below()).getY() >= Mth.floor(minY) - 1);
        if (flag) {
            this.level().addFreshEntity(new LightningBoltEntity(this.level(), x, blockpos.getY() + d0, z,
                    rotation, delay, this, 20, 7.0F));
        }
    }

    public void ParticleBlock(int precent, double x, double y, double z) {
        for (int i = 0; i < 360; ++i) {
            if (i % precent != 0 || !this.level().isClientSide) {
                continue;
            }
            BlockState block = this.level().getBlockState(this.blockPosition().below());
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                    x + Math.cos(i), y, z + Math.sin(i), Math.cos(i) * 0.25D, 0.0D, Math.sin(i) * 0.25D);
        }
    }

    public void Particle(int precent, double x, double y, double z) {
        for (int i = 0; i < 360; ++i) {
            if (i % precent != 0 || !this.level().isClientSide) {
                continue;
            }
            this.level().addParticle(ParticleTypes.CLOUD, x, y, z, Math.cos(i) * 0.25D, 0.0D, Math.sin(i) * 0.25D);
        }
    }

    private void dash(float a1, float a2, float minD) {
        if (this.onGround()) {
            LivingEntity target = this.getTarget();
            if (target != null && this.distanceToSqr(target.getX(), target.getY(), target.getZ()) > minD) {
                Vec3 vector3d = this.getDeltaMovement();
                float f = this.getYRot() * ((float) Math.PI / 180);
                Vec3 vector3d1 = new Vec3(-Mth.sin(f), this.getDeltaMovement().y, Mth.cos(f))
                        .scale(a1).add(vector3d.scale(a2));
                this.setDeltaMovement(vector3d1.x, this.getDeltaMovement().y, vector3d1.z);
            }
        }
    }

    private void spawnDoubleThunder() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        double minY = Math.min(target.getY(), this.getY());
        double maxY = Math.max(target.getY(), this.getY()) + 2.0D;
        float angle = (float) Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX());
        float offsetX = Mth.cos(this.getYRot() * ((float) Math.PI / 180)) * 1.5F;
        float offsetZ = Mth.sin(this.getYRot() * ((float) Math.PI / 180)) * 1.5F;
        for (int l = 0; l < 16; ++l) {
            double distance = 1.25D * (l + 1);
            int delay = (int) (1.25F * l);
            this.spawnThunder(this.getX() + offsetX + Mth.cos(angle) * distance,
                    this.getZ() + offsetZ + Mth.sin(angle) * distance, minY, maxY, angle, delay);
            this.spawnThunder(this.getX() - offsetX + Mth.cos(angle) * distance,
                    this.getZ() - offsetZ + Mth.sin(angle) * distance, minY, maxY, angle, delay);
        }
    }

    private void summonBolt(float size, float direction, float offset) {
        float cosAngle = (float) Math.cos(this.yBodyRot * ((float) Math.PI / 180));
        float sinAngle = (float) Math.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.0D;
        double directionX = Math.cos(theta);
        double directionZ = Math.sin(theta);
        for (int i = 0; i < 80 + this.random.nextInt(12); ++i) {
            double deltaX = this.getDeltaMovement().x * 0.07D;
            double deltaY = this.getDeltaMovement().y * 0.07D;
            double deltaZ = this.getDeltaMovement().z * 0.07D;
            float angle = (float) Math.PI / 180 * this.yBodyRot + i;
            double extraX = size * Math.sin((float) (Math.PI + angle));
            double extraY = 0.3F;
            double extraZ = size * Math.cos(angle);
            BlockPos pos = new BlockPos(Mth.floor(this.getX() + direction * directionX + extraX),
                    Mth.floor(this.getY()), Mth.floor(this.getZ() + direction * directionZ + extraZ));
            BlockState block = this.level().getBlockState(pos);
            if (block.getRenderShape() == RenderShape.INVISIBLE) {
                continue;
            }
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                    this.getX() + direction * directionX + extraX + cosAngle * offset,
                    this.getY() + extraY,
                    this.getZ() + direction * directionZ + extraZ + sinAngle * offset,
                    deltaX, deltaY, deltaZ);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                lightningBolt.moveTo(
                        this.getX() + direction * directionX + cosAngle * offset,
                        this.getY() + 0.2F,
                        this.getZ() + direction * directionZ + sinAngle * offset,
                        this.getYRot(), 0.0F);
                serverLevel.addFreshEntity(lightningBolt);
            }
        }
    }

    private void summonControlledCloud(float x, float y, float z, float progress) {
        LivingEntity target = this.getTarget();
        if (target == null || target.isDeadOrDying()) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        CloudEntity cloud = LmEntityRegistry.CLOUD.get().create(this.level());
        if (cloud == null) {
            return;
        }
        cloud.setDamage(8);
        cloud.setPos(this.getX() + dx / distance * progress,
                target.getBoundingBox().maxY + 2.0D,
                this.getZ() + dz / distance * progress);
        cloud.setOwner(this);
        this.level().addFreshEntity(cloud);
    }

    private void spawnBolt(float x, float y, float z) {
        LivingEntity target = this.getTarget();
        if (target == null || target.isDeadOrDying()) {
            return;
        }
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(this.level());
        if (lightningBolt != null) {
            lightningBolt.setPos(x, y, z);
            this.level().addFreshEntity(lightningBolt);
        }
    }

    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.65F),
        HIGH(0.25F);

        private static final List<Crackiness> BY_DAMAGE;
        public final float fraction;

        Crackiness(float pFraction) {
            this.fraction = pFraction;
        }

        public static Crackiness byFraction(float pFraction) {
            for (Crackiness crackiness : BY_DAMAGE) {
                if (pFraction < crackiness.fraction) {
                    return crackiness;
                }
            }
            return NONE;
        }

        static {
            BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble(c -> c.fraction)).toList();
        }
    }
}
