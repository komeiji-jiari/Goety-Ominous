package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RelicheirusServantNibblePewensGoal extends MoveToBlockGoal {
    private final RelicheirusServant relicheirus;
    private boolean stopFlag = false;
    private int reachCheckTime = 50;

    public RelicheirusServantNibblePewensGoal(RelicheirusServant relicheirus, int range) {
        super(relicheirus, 1.0D, range, 6);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.relicheirus = relicheirus;
    }

    private boolean isIdle() {
        return !this.relicheirus.isStaying() && !this.relicheirus.isCommanded()
                && this.relicheirus.getControllingPassenger() == null && !this.relicheirus.isImmobile()
                && this.relicheirus.getTarget() == null;
    }

    @Override
    public boolean canUse() {
        return this.isIdle() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.stopFlag && this.isIdle() && super.canContinueToUse();
    }

    @Override
    protected int nextStartTick(net.minecraft.world.entity.PathfinderMob mob) {
        return reducedTickDelay(220 + this.relicheirus.getRandom().nextInt(500));
    }

    @Override
    public double acceptedDistance() {
        return 4.0D;
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = this.getMoveToTarget();
        return target != null && this.relicheirus.distanceToSqr(target.getX() + 0.5F, this.relicheirus.getY(), target.getZ() + 0.5F) < this.acceptedDistance();
    }

    @Override
    protected BlockPos getMoveToTarget() {
        return this.relicheirus.getStandAtTreePos(this.blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = this.getMoveToTarget();
        if (target == null) {
            return;
        }
        if (this.reachCheckTime > 0) {
            --this.reachCheckTime;
        } else {
            this.reachCheckTime = 50 + this.relicheirus.getRandom().nextInt(100);
            if (!this.canReach(target)) {
                this.stopFlag = true;
                this.blockPos = BlockPos.ZERO;
                return;
            }
        }
        if (this.isReachedTarget()) {
            if (!this.relicheirus.lockTreePosition(this.blockPos)) {
                return;
            }
            if (this.relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.relicheirus.setPeckY(this.blockPos.getY());
                this.relicheirus.syncAnimation(RelicheirusServant.ANIMATION_EAT_TREE);
            } else if (this.relicheirus.getAnimation() == RelicheirusServant.ANIMATION_EAT_TREE) {
                if (this.relicheirus.getAnimationTick() >= 30) {
                    this.stopFlag = true;
                    this.blockPos = BlockPos.ZERO;
                    return;
                }
                if (this.relicheirus.getAnimationTick() % 8 == 0) {
                    BlockState back = this.relicheirus.level().getBlockState(this.blockPos);
                    this.relicheirus.level().destroyBlock(this.blockPos, false, this.relicheirus);
                    this.relicheirus.level().setBlock(this.blockPos, back, 3);
                }
            }
        } else if (this.relicheirus.getNavigation().isDone()) {
            Vec3 vec31 = Vec3.atCenterOf(target);
            Vec3 vec32 = vec31.subtract(this.relicheirus.position());
            if (vec32.length() > 1.0D) {
                vec32 = vec32.normalize();
            }
            Vec3 delta = new Vec3(vec32.x * 0.1F, 0.0D, vec32.z * 0.1F);
            this.relicheirus.setDeltaMovement(this.relicheirus.getDeltaMovement().add(delta));
        }
    }

    @Override
    protected void moveMobToBlock() {
        BlockPos pos = this.getMoveToTarget();
        this.mob.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.speedModifier);
    }

    @Override
    public void stop() {
        this.blockPos = BlockPos.ZERO;
        super.stop();
        this.stopFlag = false;
    }

    private int getHeightOfBlock(LevelReader worldIn, BlockPos pos) {
        int i = 0;
        while (pos.getY() > worldIn.getMinBuildHeight()
                && (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES)
                || worldIn.getBlockState(pos).isAir()
                || worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS))) {
            pos = pos.below();
            ++i;
        }
        return i;
    }

    private boolean highEnough(LevelReader worldIn, BlockPos pos) {
        int height = this.getHeightOfBlock(worldIn, pos);
        if (this.relicheirus.isBaby()) {
            return height <= 1;
        }
        return height > 3 && height < 7;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES) && this.highEnough(worldIn, pos);
    }

    private boolean canReach(BlockPos target) {
        Path path = this.relicheirus.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        }
        Node node = path.getEndNode();
        if (node == null) {
            return false;
        }
        int i = node.x - target.getX();
        int j = node.y - target.getY();
        int k = node.z - target.getZ();
        return i * i + j * j + k * k <= 3.0D;
    }
}
