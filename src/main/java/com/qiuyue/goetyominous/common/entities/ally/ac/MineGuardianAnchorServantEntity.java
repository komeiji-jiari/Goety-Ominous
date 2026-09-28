package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class MineGuardianAnchorServantEntity extends MineGuardianAnchorEntity {

    private static final EntityDataAccessor<Boolean> CHAIN_RELEASED = SynchedEntityData.defineId(MineGuardianAnchorServantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int LANDED_LINGER_TICKS = 60;

    private boolean landed;
    private int landedTicks;

    public MineGuardianAnchorServantEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MineGuardianAnchorServantEntity(MineGuardianServant servant) {
        this(AcEntityRegistry.MINE_GUARDIAN_ANCHOR_SERVANT.get(), servant.level());
        this.linkWithGuardian(servant);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.setPos(servant.position().add(0.0D, 0.5D, 0.0D));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHAIN_RELEASED, false);
    }

    public boolean isChainReleased() {
        return this.entityData.get(CHAIN_RELEASED);
    }

    public void releaseChain() {
        this.entityData.set(CHAIN_RELEASED, true);
        this.landed = false;
        this.landedTicks = 0;
    }

    @Override
    public void tick() {
        if (this.isChainReleased()) {
            this.baseTick();
            if (!this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
            } else {
                this.landed = true;
            }
            this.move(MoverType.SELF, this.getDeltaMovement().scale(0.9F));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 0.9D, 0.9D));
            if (!this.level().isClientSide && this.landed && ++this.landedTicks > LANDED_LINGER_TICKS) {
                this.discard();
            }
            return;
        }
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.getGuardian() instanceof MineGuardianServant servant) {
            if (servant.isChainCut()) {
                this.releaseChain();
                return;
            }
            this.linkWithGuardian(servant);
            LivingEntity attackTarget = servant.getTarget();
            boolean hasTarget = attackTarget != null && attackTarget.isAlive();
            double distance = this.distanceTo(servant);
            int chainLength = servant.getMaxChainLength();
            double distanceGoal = (servant.isInWaterOrBubble() ? (double) chainLength + Math.sin((float) this.tickCount * 0.1F + (float) chainLength * 0.5F) * 0.25D : 5.0D) + (hasTarget ? 5.0D : 0.0D);
            double waterUp = Math.min(servant.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()), 1.0D) * 0.005D;
            if (servant.isInWaterOrBubble() && !hasTarget) {
                double f = this.getX() - Math.sin((float) this.tickCount * 0.025F + (float) chainLength) * 0.5D;
                double f1 = this.getZ() + Math.cos((float) this.tickCount * 0.025F + (float) chainLength) * 0.5D;
                double f2 = this.getY() + distanceGoal;
                Vec3 vec3 = new Vec3(f, f2, f1).subtract(servant.position());
                servant.setDeltaMovement(servant.getDeltaMovement().add(vec3.scale(waterUp)));
            }
            if (distance > distanceGoal) {
                double disRem = Math.min(distance - distanceGoal, 1.0D) * 0.1D;
                Vec3 moveTo = this.getChainFrom(1.0F).subtract(servant.position());
                if (moveTo.length() > 1.0D) {
                    moveTo = moveTo.normalize();
                }
                double damping = hasTarget ? 1.0D : 0.8D;
                servant.setDeltaMovement(servant.getDeltaMovement().multiply(damping, damping, damping).add(moveTo.scale(disRem)));
            }
        }
    }

    @Override
    public Vec3 getChainTo(float partialTicks) {
        if (!this.isChainReleased() && this.getGuardian() instanceof MineGuardianServant servant) {
            return servant.getPosition(partialTicks);
        }
        return super.getChainTo(partialTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(CHAIN_RELEASED, compound.getBoolean("ChainReleased"));
        this.landedTicks = compound.getInt("LandedTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ChainReleased", this.isChainReleased());
        compound.putInt("LandedTicks", this.landedTicks);
    }
}
