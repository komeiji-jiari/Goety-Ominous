package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IceSpikeEntity extends Entity implements ISpellEntity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 34;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    public IceSpikeEntity(EntityType<? extends IceSpikeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public IceSpikeEntity(Level level, double x, double y, double z, float yRot, int warmupDelay, LivingEntity caster) {
        this(LmEntityRegistry.ICE_SPIKE.get(), level);
        this.warmupDelayTicks = warmupDelay;
        this.setCaster(caster);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
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
    public void tick() {
        if (this.lifeTicks == 32 && !this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.ICE_SPIKE_EMERGE.get(), this.getSoundSource(), 0.5F, this.random.nextFloat() * 0.2F + 0.85F, false);
        }
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks < -10 && this.warmupDelayTicks > -30) {
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                    this.damage(livingEntity);
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
        LivingEntity caster = this.getCaster();
        if (caster == null || !impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }
        if (ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }
        DamageSource damageSource = this.level().damageSources().mobAttack(caster);
        impactEntity.hurt(damageSource, 5.0F);
        if (!this.level().isClientSide) {
            impactEntity.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), 60, 0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        }
        int remaining = this.lifeTicks - 2;
        return remaining <= 0 ? 1.0F : 1.0F - ((float) remaining - partialTicks) / 20.0F;
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
