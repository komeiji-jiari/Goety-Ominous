package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.yellowbrossproductions.illageandspillage.entities.CameraShakeEntity;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.packet.PacketHandler;
import com.yellowbrossproductions.illageandspillage.packet.ParticlePacket;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Iterator;

public class KaboomerServant extends MagiServant implements IllagerAttack {
    private static final EntityDataAccessor<Integer> ATTACKSTAGE;
    private int spawnTicks;
    private int oldSwell;
    private int swell;
    private final int maxSwell = 30;

    public KaboomerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public boolean isAttackable() {
        return false;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.KaboomerServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.KaboomerServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.KaboomerServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.KaboomerServantFollowRange.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.KaboomerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.KaboomerServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.KaboomerServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.KaboomerServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKSTAGE, 0);
    }

    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        return false;
    }

    public void tick() {
        if (this.isAlive()) {
            ++this.spawnTicks;
            if (this.getAttackStage() == 0) {
                this.setAttackStage(1);
            }

            this.setYRot(this.getYHeadRot());
            this.yBodyRot = this.getYRot();
            if (this.spawnTicks == 30) {
                this.setAttackStage(2);
            }

            if (this.spawnTicks == 70) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_KABOOMER_HISS.get(), 2.0F, 1.0F);
                this.setAttackStage(3);
            }

            if (this.spawnTicks > 100) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_KABOOMER_EXPLODE.get(), 3.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.6F, 0, 20);
                this.makeExplodeParticles();
                this.explode();
                this.kill();
            }

            if (this.getAttackStage() == 3) {
                this.makeHissParticles();
                this.oldSwell = this.swell++;
                if (this.swell >= this.maxSwell) {
                    this.swell = this.maxSwell;
                }
            }

            this.getNavigation().stop();
            if (this.getTarget() != null && this.getAttackStage() >= 2) {
                this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
            }

            this.navigation.stop();
        }

        super.tick();
    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public double getPassengersRidingOffset() {
        return 3.2;
    }

    public void teleport(@Nullable Entity entity) {
        if (entity != null) {
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 3.0F, 1.0F);
            this.setPos(entity.getX(), entity.getY(), entity.getZ());
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
                double d0;
                double d1;
                double d2;
                for (i = 0; i < 250; ++i) {
                    d0 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(d0, d1, d2));
                }

                for (i = 0; i < 200; ++i) {
                    d0 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.POOF, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(d0, d1, d2));
                }

                for (i = 0; i < 150; ++i) {
                    d0 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.LARGE_SMOKE, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(d0, d1, d2));
                }

                ServerPlayer finalServerPlayer = serverPlayer;
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> finalServerPlayer), packet);
            }
        }
    }

    public void makeHissParticles() {
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

                for (int i = 0; i < 20; ++i) {
                    double d0 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    double d1 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    double d2 = (-0.5 + this.random.nextGaussian()) / 2.0;
                    packet.queueParticle(ParticleTypes.ELECTRIC_SPARK, false,
                            new Vec3(this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0)),
                            new Vec3(d0, d1, d2));
                }

                ServerPlayer finalServerPlayer = serverPlayer;
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> finalServerPlayer), packet);
            }
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.RAVAGER_STEP;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(this.getStepSound(), 1.0F, 1.0F);
    }

    public boolean hurt(DamageSource source, float amount) {
        return (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL))
                && super.hurt(source, amount);
    }

    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
        if (this.level().isClientSide) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), d0, d1,
                    d2);
        }

        this.deathTime = 19;
    }

    private void explode() {
        if (!this.level().isClientSide) {
            double range = 4.0;
            ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY(), this.getZ(), 5.0F, false,
                    Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX() + range, this.getY(), this.getZ() + range, 5.0F,
                    false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX() + range, this.getY(), this.getZ() - range, 5.0F,
                    false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX() - range, this.getY(), this.getZ() + range, 5.0F,
                    false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX() - range, this.getY(), this.getZ() - range, 5.0F,
                    false, Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY() + range, this.getZ(), 5.0F, false,
                    Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
            ExplosionUtil.lootExplode(this.level(), this, this.getX(), this.getY() - range, this.getZ(), 5.0F, false,
                    Explosion.BlockInteraction.KEEP, LootingExplosion.Mode.REGULAR);
        }
    }

    public int getAttackStage() {
        return this.entityData.get(ATTACKSTAGE);
    }

    public void setAttackStage(int attackStage) {
        this.entityData.set(ATTACKSTAGE, attackStage);
    }

    static {
        ATTACKSTAGE = SynchedEntityData.defineId(KaboomerServant.class, EntityDataSerializers.INT);
    }

    class AttackGoal extends Goal {
        AttackGoal() {
        }

        public boolean canUse() {
            return KaboomerServant.this.getAttackStage() > 0;
        }

        public void tick() {
        }
    }
}