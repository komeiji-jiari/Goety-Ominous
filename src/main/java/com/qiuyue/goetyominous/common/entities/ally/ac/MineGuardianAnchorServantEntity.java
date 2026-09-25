package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class MineGuardianAnchorServantEntity extends MineGuardianAnchorEntity {

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
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getGuardian() instanceof MineGuardianServant servant) {
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
        if (this.getGuardian() instanceof MineGuardianServant servant) {
            return servant.getPosition(partialTicks);
        }
        return super.getChainTo(partialTicks);
    }
}
