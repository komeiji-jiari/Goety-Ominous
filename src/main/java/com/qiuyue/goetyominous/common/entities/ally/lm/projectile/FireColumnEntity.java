package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class FireColumnEntity extends Entity {

    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(FireColumnEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(FireColumnEntity.class, EntityDataSerializers.INT);

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 60;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;
    public float activateProgress;
    public float prevactivateProgress;
    public int attackCooldown;
    public int attackTicks;

    public FireColumnEntity(EntityType<? extends FireColumnEntity> type, Level level) {
        super(type, level);
    }

    public FireColumnEntity(Level worldIn, double x, double y, double z, float rotation, int warmup, LivingEntity casterIn) {
        this(LmEntityRegistry.FIRE_PILLAR.get(), worldIn);
        this.warmupDelayTicks = warmup;
        this.setCaster(casterIn);
        this.setYRot(rotation * 57.295776F);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(ATTACK_STATE, 0);
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUuid);
            if (entity instanceof LivingEntity livingEntity) {
                this.caster = livingEntity;
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
        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2))) {
            this.damage(livingentity);
        }
        if (this.tickCount % 5 == 0) {
            for (int i = 0; (float) i < 0.5F; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                double y = this.getY() + 0.5 + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }
        }
        this.level().addParticle(ModParticles.FLAME.get(), this.getX(), this.getY(), this.getZ(), 0.0, 1.0, 0.0);
        super.tick();
        if (this.getAttackState() == 0) {
            this.setAttackState(1);
        }
        if (this.getAttackState() > 0) {
            ++this.attackTicks;
        }
        if (this.attackCooldown > 0) {
            --this.attackCooldown;
        }
        this.prevactivateProgress = this.activateProgress;
        if (this.isActivate() && this.activateProgress > 0.0F) {
            --this.activateProgress;
        }
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (!this.isActivate() && this.activateProgress < 10.0F) {
                    ++this.activateProgress;
                }
                if (this.lifeTicks == 6) {
                    this.setActivate(true);
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -10 && this.isActivate()) {
                this.setActivate(false);
            }
            if (this.warmupDelayTicks < -10 && this.warmupDelayTicks > -30) {
                for (int i = 0; i < 10; ++i) {
                    double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                    double y = this.getY() + 0.5 + (double) (this.random.nextFloat() * this.getBbHeight());
                    double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                    double motx = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                    double moty = 0.5F + this.random.nextFloat() * 2.0F;
                    double motz = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                    this.level().addParticle(ParticleTypes.FLAME, x, y, z, motx, moty, motz);
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

    public boolean isActivate() {
        return this.entityData.get(ATTACK);
    }

    public void setActivate(boolean activate) {
        this.entityData.set(ATTACK, activate);
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();
        if (!impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }
        if (caster == null) {
            DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK), this);
            impactEntity.hurt(damageSource, 10.0F);
            impactEntity.setRemainingFireTicks(80);
            return;
        }
        if (caster.isAlliedTo(impactEntity) || ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }
        DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK), caster);
        impactEntity.hurt(damageSource, 10.0F);
        impactEntity.setRemainingFireTicks(80);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setAttackState(1);
    }

    public void setAttackState(int input) {
        this.attackTicks = 0;
        this.entityData.set(ATTACK_STATE, input);
    }

    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }
}
