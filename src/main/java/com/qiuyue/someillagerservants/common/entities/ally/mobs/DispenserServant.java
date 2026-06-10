package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DispenserServant extends MagiServant implements IllagerAttack {
    private final List<IllashooterServant> shooters = new ArrayList<>();
    private static final EntityDataAccessor<Boolean> IN_MOTION;
    private int spawnTicks;

    public DispenserServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.DispenserServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.DispenserServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DispenserServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.DispenserServantFollowRange.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.DispenserServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.DispenserServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.DispenserServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.DispenserServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IN_MOTION, false);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isInMotion()) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_DISPENSER_OPEN.get(), 1.0F, 1.0F);
            this.setInMotion(false);
        }

        return false;
    }

    public void tick() {
        if (this.isAlive() && !this.isInMotion()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            ++this.spawnTicks;
            if (this.spawnTicks > 60 && this.shooters.size() < 5) {
                this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                if (!this.level().isClientSide) {
                    IllashooterServant illashooter = IasEntityRegistry.ILLASHOOTER_SERVANT.get().create(this.level());

                    assert illashooter != null;

                    illashooter.setPos(this.getX(), this.getY(), this.getZ());
                    illashooter.setDeltaMovement(0.0, 0.5, 0.0);

                    if (this.getTrueOwner() != null) {
                        illashooter.setTrueOwner(this);
                    }
                    illashooter.setMagi(this);

                    illashooter.setLimitedLife(20 * (30 + DispenserServant.this.random.nextInt(90)));
                    this.level().addFreshEntity(illashooter);
                    this.shooters.add(illashooter);
                }

                this.spawnTicks = 0;
            }

            this.updateShooterList();
        }

        if (this.onGround()) {
            this.setInMotion(false);
        }

        super.tick();
    }

    public boolean isInMotion() {
        return this.entityData.get(IN_MOTION);
    }

    public void setInMotion(boolean motion) {
        this.entityData.set(IN_MOTION, motion);
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_DISPENSER_DESTROY.get();
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

    public void updateShooterList() {
        if (!this.shooters.isEmpty()) {
            for (int i = 0; i < this.shooters.size(); ++i) {
                IllashooterServant clone = this.shooters.get(i);
                if (!clone.isAlive()) {
                    this.shooters.remove(i);
                    --i;
                }
            }
        }

    }

    static {
        IN_MOTION = SynchedEntityData.defineId(DispenserServant.class, EntityDataSerializers.BOOLEAN);
    }
}
