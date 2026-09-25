package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.IAnimatedBoss.TheObliterator.TheObliteratorUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class AnnihilationExplosion extends Entity implements ISpellEntity {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(AnnihilationExplosion.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> BULLETS_AMOUNT =
            SynchedEntityData.defineId(AnnihilationExplosion.class, EntityDataSerializers.INT);

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 20;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    public AnnihilationExplosion(EntityType<? extends AnnihilationExplosion> type, Level level) {
        super(type, level);
    }

    public AnnihilationExplosion(Level level, double x, double y, double z, float yRot, int warmup,
                                 LivingEntity caster, int lifeTicks, float damage, int bulletAmount) {
        this(LmEntityRegistry.ANNIHILATION_EXPLOSION.get(), level);
        this.warmupDelayTicks = warmup;
        this.setCaster(caster);
        this.setYRot(yRot * 57.295776F);
        this.setBulletsAmount(bulletAmount);
        this.setPos(x, y, z);
        this.lifeTicks = lifeTicks;
        this.setDamage(damage);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(BULLETS_AMOUNT, 0);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public float getBulletAmount() {
        return this.entityData.get(BULLETS_AMOUNT);
    }

    public void setBulletsAmount(int bulletsAmount) {
        this.entityData.set(BULLETS_AMOUNT, bulletsAmount);
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getEntity(this.casterUuid) instanceof LivingEntity living) {
                this.caster = living;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 15) {
                    this.level().addAlwaysVisibleParticle(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                LivingEntity entity = this.getCaster();
                if (entity != null) {
                    for (int i = 0; (float) i < this.getBulletAmount(); ++i) {
                        float throwAngle = (float) i * (float) Math.PI / (this.getBulletAmount() / 2.0F);
                        double sx = this.getX() + Mth.cos(throwAngle) * 1.0F;
                        double sy = this.getY() + this.getBbHeight() * 0.2D;
                        double sz = this.getZ() + Mth.sin(throwAngle) * 1.0F;
                        double vx = Mth.cos(throwAngle);
                        double vy = entity.getRandom().nextFloat() * 0.3F;
                        double vz = Mth.sin(throwAngle);
                        double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
                        SmallAnnihilationBomb projectile = new SmallAnnihilationBomb(entity.level(), entity, 6.0F);
                        projectile.setTurnRate(0.0F);
                        projectile.moveTo(sx, sy, sz, i * 11.25F, this.getXRot());
                        projectile.shoot(vx, vy + v3 * 2.0D, vz, 0.5F, 1.0F);
                        entity.level().addFreshEntity(projectile);
                    }
                }
            }
            if (this.warmupDelayTicks > -8) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    this.damage(livingentity);
                }
            }
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity livingentity = this.getCaster();
        if (!impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == livingentity) {
            return;
        }
        if (livingentity != null && MobUtil.areAllies(livingentity, impactEntity)) {
            return;
        }
        if (impactEntity instanceof TamableAnimal animal && animal.getOwner() != null) {
            return;
        }
        DamageSource damageSource = new DamageSource(
                this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ModDamageTypes.ANNIHILATION), livingentity);
        if (impactEntity.hurt(damageSource, this.getDamage())) {
            TheObliteratorUtils.applyAnnihilationEffect(impactEntity, ModEffects.ANNIHILATION.get(), 1, false);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT,
                        this.getSoundSource(), 0.3F, 1.25F, false);
            }
        } else if (id <= 0) {
            this.lifeTicks = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 25, 0.0F, 1.0F, 0.0F, 1.0F,
                    14.0F, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
