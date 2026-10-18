package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IceShockwaveSpawner extends Entity implements ISpellEntity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 9;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    public IceShockwaveSpawner(EntityType<? extends IceShockwaveSpawner> entityType, Level level) {
        super(entityType, level);
    }

    public IceShockwaveSpawner(Level level, double x, double y, double z, float yRot, int warmupDelay,
                               LivingEntity caster) {
        this(LmEntityRegistry.ICE_SHOCKWAVE_SPAWNER.get(), level);
        this.warmupDelayTicks = warmupDelay;
        this.setCaster(caster);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        if (!level.isClientSide) {
            for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                this.playSound(SoundEvents.STONE_BREAK, 1.0F, 0.5F);
                this.damage(livingEntity);
            }
        }
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
        if (this.level().isClientSide) {
            super.tick();
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
            }
            return;
        }
        int standingOnY = Mth.floor(this.getY());
        if (this.lifeTicks == 7) {
            for (int i = 0; i < 1.5; ++i) {
                float angle = (float) i * (float) Math.PI * 2.0F / 1.5F + 4.1887903F;
                this.spawnSpike(this.getX() + (double) Mth.cos(angle) * 1.5D,
                        this.getZ() + (double) Mth.sin(angle) * 1.5D,
                        standingOnY, this.getY() + 1.0D, angle, 0);
            }
        }
        if (this.lifeTicks == 4) {
            for (int i = 0; i < 6; ++i) {
                float angle = (float) i * (float) Math.PI * 2.0F / 6.0F + 1.2566371F;
                this.spawnSpike(this.getX() + (double) Mth.cos(angle) * 2.5D,
                        this.getZ() + (double) Mth.sin(angle) * 2.5D,
                        standingOnY, this.getY() + 1.0D, angle, 0);
            }
        }
        if (this.lifeTicks == 1) {
            for (int i = 0; i < 11; ++i) {
                float angle = (float) i * (float) Math.PI * 2.0F / 11.0F + 0.62831855F;
                this.spawnSpike(this.getX() + (double) Mth.cos(angle) * 3.5D,
                        this.getZ() + (double) Mth.sin(angle) * 3.5D,
                        standingOnY, this.getY() + 1.0D, angle, 2);
            }
        }
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
            this.damage(livingEntity);
        }
        super.tick();
        if (--this.warmupDelayTicks < 0) {
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void spawnSpike(double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean found = false;
        double offset = 0.0D;
        do {
            BlockPos below = blockpos.below();
            BlockState blockstate = this.level().getBlockState(below);
            if (blockstate.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState inside = this.level().getBlockState(blockpos);
                    VoxelShape shape = inside.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        offset = shape.max(Direction.Axis.Y);
                    }
                }
                found = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (found) {
            this.level().addFreshEntity(new IceShockwaveEntity(
                    this.level(), x, (double) blockpos.getY() + offset, z, rotation, delay, this.getCaster(), 17.0F));
        }
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();
        if (caster == null || !impactEntity.isAlive() || impactEntity.isInvulnerable()
                || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }
        if (ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }
        DamageSource damageSource = this.level().damageSources().mobAttack(caster);
        impactEntity.hurt(damageSource, 8.0F);
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
