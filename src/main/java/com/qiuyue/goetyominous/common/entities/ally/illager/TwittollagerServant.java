package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.yellowbrossproductions.illageandspillage.entities.CameraShakeEntity;
import com.yellowbrossproductions.illageandspillage.packet.ParticlePacket;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;

public class TwittollagerServant extends AbstractIllagerServant {
    private static final EntityDataAccessor<Boolean> ANGRY;
    private static final EntityDataAccessor<Boolean> STARING;
    private static final EntityDataAccessor<Boolean> PHONE_DINGED;
    private static final EntityDataAccessor<Boolean> HMM;
    private static final EntityDataAccessor<Boolean> CHECKING_PHONE;
    private static final EntityDataAccessor<Integer> GRRRRRRRRRRRRR_TICKS;
    private static final EntityDataAccessor<Boolean> CAN_CHARGE;
    private static final EntityDataAccessor<Boolean> EXPLODE;
    private int checkPhoneTicks;
    private int waitTime;
    private boolean isAngry;
    private boolean canExplodeInfinitely;

    public TwittollagerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ChargeAtTargetGoal());
        this.goalSelector.addGoal(0, new StareAggressivelyGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, GlowSquid.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Sheep.class, 10, false, false,
                (p_234199_0_) -> p_234199_0_ instanceof Sheep && ((Sheep) p_234199_0_).getColor() == DyeColor.PINK));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, TwittollagerServant.class)).setAlertOthers());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TwittollagerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TwittollagerServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TwittollagerServantDamage.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.TwittollagerServantHealth.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.TwittollagerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.TwittollagerServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.TwittollagerServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.TwittollagerServantFollowRange.get());
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsAngry", isAngry);
        tag.putBoolean("CanExplodeInfinitely", canExplodeInfinitely);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.isAngry = tag.getBoolean("IsAngry");
        this.canExplodeInfinitely = tag.getBoolean("CanExplodeInfinitely");
        this.setAngry(isAngry);
        this.setCanCharge(isAngry);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(STARING, false);
        this.entityData.define(PHONE_DINGED, false);
        this.entityData.define(HMM, false);
        this.entityData.define(CHECKING_PHONE, false);
        this.entityData.define(GRRRRRRRRRRRRR_TICKS, 0);
        this.entityData.define(CAN_CHARGE, false);
        this.entityData.define(EXPLODE, false);
    }

    public void tick() {
        super.tick();
        if (this.getTarget() instanceof GlowSquid && !this.isAngry()) {
            this.checkPhoneTicks = 115;
            if (!this.level().isClientSide) {
                this.setAngry(true);
            }

            this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_ANGRY.get(), 2.0F, 1.0F);
        }

        if (this.getTarget() instanceof Sheep && ((Sheep) this.getTarget()).getColor() == DyeColor.PINK
                && !this.isAngry()) {
            this.checkPhoneTicks = 115;
            if (!this.level().isClientSide) {
                this.setAngry(true);
            }

            this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_ANGRY.get(), 2.0F, 1.0F);
        }

        if (this.getTarget() instanceof Villager && ((Villager) this.getTarget()).getAge() < 0 && !this.isAngry()) {
            this.checkPhoneTicks = 115;
            if (!this.level().isClientSide) {
                this.setAngry(true);
            }

            this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_ANGRY.get(), 2.0F, 1.0F);
        }

        if (this.isStaring()) {
            ++this.waitTime;
            if (this.waitTime > 60 && this.random.nextInt(25) == 0 && !this.hasPhoneDinged() && !this.isAngry()) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_DING.get(), 1.0F, 1.0F);
                this.setPhoneDinged(true);
            }
        }

        if (this.hasPhoneDinged() || this.isAngry()) {
            ++this.checkPhoneTicks;
            if (!this.isAngry()) {
                if (this.checkPhoneTicks == 15) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_HMM.get(), 1.0F, 1.0F);
                    if (!this.level().isClientSide) {
                        this.setHmm(true);
                    }
                }

                if (this.checkPhoneTicks == 35 && !this.level().isClientSide) {
                    this.setCheckingPhone(true);
                }

                if (this.checkPhoneTicks == 115) {
                    if (!this.level().isClientSide) {
                        this.setAngry(true);
                    }

                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_ANGRY.get(), 2.0F, 1.0F);
                }
            }
        }

        if (this.isAngry()) {
            if (!this.level().isClientSide && this.getGRRRRRRRRRR() < 60) {
                this.setGRRRRRRRRRRRRRR(this.getGRRRRRRRRRR() + 1);
            }

            if (this.checkPhoneTicks == 184) {
                this.setCanCharge(true);
                if (!this.level().isClientSide) {
                    this.setCheckingPhone(false);
                }
            }
        }

    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_EXPLOSION) && source.getEntity() instanceof TwittollagerServant) {
            return false;
        } else {
            if (source.getEntity() != null && !this.isAngry()) {
                this.checkPhoneTicks = 115;
                if (!this.level().isClientSide) {
                    this.setAngry(true);
                }

                this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_ANGRY.get(), 2.0F, 1.0F);
            }

            return super.hurt(source, amount);
        }
    }

    public boolean canBeLeader() {
        return false;
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_CELEBRATE.get();
    }

    protected SoundEvent getAmbientSound() {
        return !this.hasPhoneDinged() && !this.isAngry()
                ? IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_AMBIENT.get()
                : null;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return this.isExplode() && !this.canExplodeInfinitely ? null
                : IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_DEATH.get();
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, angry);
    }

    public boolean isStaring() {
        return this.entityData.get(STARING);
    }

    public void setStaring(boolean angry) {
        this.entityData.set(STARING, angry);
    }

    public boolean hasPhoneDinged() {
        return this.entityData.get(PHONE_DINGED);
    }

    public void setPhoneDinged(boolean ding) {
        this.entityData.set(PHONE_DINGED, ding);
    }

    public boolean isHmm() {
        return this.entityData.get(HMM);
    }

    public void setHmm(boolean hmm) {
        this.entityData.set(HMM, hmm);
    }

    public boolean isCheckingPhone() {
        return this.entityData.get(CHECKING_PHONE);
    }

    public void setCheckingPhone(boolean check) {
        this.entityData.set(CHECKING_PHONE, check);
    }

    public int getGRRRRRRRRRR() {
        return this.entityData.get(GRRRRRRRRRRRRR_TICKS);
    }

    public void setGRRRRRRRRRRRRRR(int grrrrr) {
        this.entityData.set(GRRRRRRRRRRRRR_TICKS, grrrrr);
    }

    public boolean canCharge() {
        return this.entityData.get(CAN_CHARGE);
    }

    public void setCanCharge(boolean charge) {
        this.entityData.set(CAN_CHARGE, charge);
    }

    public boolean isExplode() {
        return this.entityData.get(EXPLODE);
    }

    public void setExplode(boolean boom) {
        this.entityData.set(EXPLODE, boom);
    }

    public boolean doHurtTarget(Entity p_70652_1_) {
        return false;
    }

    private void explode() {
        this.setExplode(true);
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_EXPLODE.get(), 6.0F, 1.0F);
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_SCREAM.get(), 6.0F, 1.0F);
        CameraShakeEntity.cameraShake(this.level(), this.position(), 30.0F, 0.4F, 0, 20);
        if (!this.canExplodeInfinitely)
            this.kill();
        this.makeExplodeParticles();
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0F, Level.ExplosionInteraction.NONE);
        }

    }

    public void die(DamageSource p_37847_) {
        if (this.isExplode() && !this.canExplodeInfinitely) {
            this.deathTime = 19;
        }

        super.die(p_37847_);
    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 60) {
            if (!this.isExplode()) {
                super.handleEntityEvent(p_21375_);
            }
        } else {
            super.handleEntityEvent(p_21375_);
        }

    }

    public void makeExplodeParticles() {
        if (!this.level().isClientSide) {
            Iterator<ServerPlayer> var1 = ((ServerLevel) this.level()).players().iterator();

            while (true) {
                ServerPlayer serverPlayer;
                do {
                    if (!var1.hasNext()) {
                        return;
                    }

                    serverPlayer = var1.next();
                } while (!(serverPlayer.distanceToSqr(this) < 4096.0));

                ParticlePacket packet = new ParticlePacket();

                int i;
                double random;
                double d1;
                double d2;
                for (i = 0; i < 250; ++i) {
                    random = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(random, d1, d2));
                }

                for (i = 0; i < 200; ++i) {
                    random = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.POOF, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(random, d1, d2));
                }

                for (i = 0; i < 150; ++i) {
                    random = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.LARGE_SMOKE, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(random, d1, d2));
                }

                for (i = 0; i < 75; ++i) {
                    random = (-0.5 + this.random.nextDouble()) / 2.0;
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() - 2.0 + random, this.getY() + 1.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() + 2.0 + random, this.getY() + 1.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() - 1.0 + random, this.getY() + 2.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() + 1.0 + random, this.getY() + 2.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() + 0.0 + random, this.getY() + 2.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() - 2.0 + random, this.getY() + 5.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() + 2.0 + random, this.getY() + 5.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() - 1.0 + random, this.getY() + 4.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                    packet.queueParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, false,
                            new Vec3(this.getX() + 1.0 + random, this.getY() + 4.0 + random, this.getZ() + random),
                            new Vec3(0.0, 0.14, 0.0));
                }
            }
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.getEffect() == GoetyEffects.CRIPPLED.get() ||
                effectInstance.getEffect() == GoetyEffects.WOUNDED.get()) {
            int newDuration = effectInstance.getDuration() / 4;
            MobEffectInstance modifiedEffect = new MobEffectInstance(
                    effectInstance.getEffect(),
                    newDuration,
                    effectInstance.getAmplifier(),
                    effectInstance.isAmbient(),
                    effectInstance.isVisible(),
                    effectInstance.showIcon());
            return super.addEffect(modifiedEffect, entity);
        }
        return super.addEffect(effectInstance, entity);
    }

    static {
        ANGRY = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        STARING = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        PHONE_DINGED = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        HMM = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        CHECKING_PHONE = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        GRRRRRRRRRRRRR_TICKS = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.INT);
        CAN_CHARGE = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
        EXPLODE = SynchedEntityData.defineId(TwittollagerServant.class, EntityDataSerializers.BOOLEAN);
    }

    class ChargeAtTargetGoal extends Goal {
        public ChargeAtTargetGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return TwittollagerServant.this.getTarget() != null
                    && TwittollagerServant.this.hasLineOfSight(TwittollagerServant.this.getTarget())
                    && TwittollagerServant.this.canCharge();
        }

        public void start() {
            TwittollagerServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_TWITTOLLAGER_CHARGE.get(), 2.0F,
                    1.0F);
        }

        public boolean canContinueToUse() {
            return TwittollagerServant.this.getTarget() != null
                    && TwittollagerServant.this.hasLineOfSight(TwittollagerServant.this.getTarget());
        }

        public void tick() {
            if (TwittollagerServant.this.getTarget() != null) {
                TwittollagerServant.this.getNavigation().moveTo(TwittollagerServant.this.getTarget(), 2.5);
                TwittollagerServant.this.getLookControl().setLookAt(TwittollagerServant.this.getTarget(), 30.0F, 30.0F);
                TwittollagerServant.this.navigation.moveTo(TwittollagerServant.this.getTarget(), 2.5);
                if (TwittollagerServant.this.distanceToSqr(TwittollagerServant.this.getTarget()) < 4.5) {
                    TwittollagerServant.this.explode();
                }
            }

        }

        public void stop() {
            TwittollagerServant.this.getNavigation().stop();
            TwittollagerServant.this.navigation.stop();
        }
    }

    class StareAggressivelyGoal extends Goal {
        public StareAggressivelyGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return TwittollagerServant.this.getTarget() != null
                    && TwittollagerServant.this.distanceToSqr(TwittollagerServant.this.getTarget()) < 150.0
                    && TwittollagerServant.this.hasLineOfSight(TwittollagerServant.this.getTarget())
                    && !TwittollagerServant.this.canCharge();
        }

        public void start() {
            TwittollagerServant.this.setStaring(true);
        }

        public boolean canContinueToUse() {
            return (TwittollagerServant.this.getTarget() != null
                    && TwittollagerServant.this.distanceToSqr(TwittollagerServant.this.getTarget()) < 150.0
                    && TwittollagerServant.this.getTarget().isAlive()
                    && TwittollagerServant.this.hasLineOfSight(TwittollagerServant.this.getTarget())
                    || TwittollagerServant.this.hasPhoneDinged()) && !TwittollagerServant.this.canCharge();
        }

        public void tick() {
            TwittollagerServant.this.getNavigation().stop();
            if (TwittollagerServant.this.checkPhoneTicks >= 30 && TwittollagerServant.this.hasPhoneDinged()) {
                TwittollagerServant.this.getLookControl().setLookAt(TwittollagerServant.this.getX(),
                        TwittollagerServant.this.getY(), TwittollagerServant.this.getZ(), 30.0F, 30.0F);
            } else if (TwittollagerServant.this.getTarget() != null) {
                TwittollagerServant.this.getLookControl().setLookAt(TwittollagerServant.this.getTarget(), 30.0F, 30.0F);
            }

            TwittollagerServant.this.navigation.stop();
        }

        public void stop() {
            TwittollagerServant.this.setStaring(false);
        }
    }
}