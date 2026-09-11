package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AtlatitanServantNibbleTreesGoal extends MoveToBlockGoal {
    private final AtlatitanServant atlatitan;
    private boolean stopFlag = false;
    private int reachCheckTime = 50;

    public AtlatitanServantNibbleTreesGoal(AtlatitanServant atlatitan, int range) {
        super(atlatitan, 1.0D, range, 16);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.atlatitan = atlatitan;
    }

    private boolean isIdle() {
        return !this.atlatitan.isStaying() && !this.atlatitan.isCommanded()
                && this.atlatitan.getControllingPassenger() == null && !this.atlatitan.isImmobile()
                && this.atlatitan.getTarget() == null;
    }

    @Override
    public boolean canUse() {
        this.verticalSearchStart = this.atlatitan.isBaby() ? 3 : 6;
        return this.isIdle() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.stopFlag && this.isIdle() && super.canContinueToUse();
    }

    @Override
    protected int nextStartTick(net.minecraft.world.entity.PathfinderMob mob) {
        return reducedTickDelay(200 + this.atlatitan.getRandom().nextInt(200));
    }

    @Override
    public double acceptedDistance() {
        return (int) Math.floor(14.0F * this.atlatitan.getScale());
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = this.getMoveToTarget();
        return target != null && this.atlatitan.distanceToSqr(target.getX() + 0.5F, this.atlatitan.getY(), target.getZ() + 0.5F) < this.acceptedDistance();
    }

    @Override
    protected BlockPos getMoveToTarget() {
        return this.atlatitan.getStandAtTreePos(this.blockPos);
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
            this.reachCheckTime = 50 + this.atlatitan.getRandom().nextInt(100);
            if (!this.canReach(target)) {
                this.stopFlag = true;
                this.blockPos = BlockPos.ZERO;
                return;
            }
        }
        if (this.isReachedTarget()) {
            if (!this.atlatitan.lockTreePosition(this.blockPos)) {
                return;
            }
            if (this.atlatitan.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.atlatitan.setEatingPos(this.blockPos);
                this.atlatitan.setAnimation(AtlatitanServant.ANIMATION_EAT_LEAVES);
            } else if (this.atlatitan.getAnimation() == AtlatitanServant.ANIMATION_EAT_LEAVES) {
                if (this.atlatitan.getAnimationTick() >= 35) {
                    this.stopFlag = true;
                    this.blockPos = BlockPos.ZERO;
                    return;
                }
                if (this.atlatitan.getAnimationTick() == 20) {
                    BlockState back = this.atlatitan.level().getBlockState(this.blockPos);
                    this.atlatitan.setLastEatenBlock(back);
                    this.atlatitan.level().destroyBlock(this.blockPos, false, this.atlatitan);
                    this.atlatitan.level().setBlock(this.blockPos, back, 3);
                }
            }
        } else if (this.atlatitan.getNavigation().isDone()) {
            Vec3 vec31 = Vec3.atCenterOf(target);
            this.atlatitan.getMoveControl().setWantedPosition(vec31.x, this.atlatitan.getY(), vec31.z, 1.0D);
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
        if (this.atlatitan.isBaby()) {
            return height <= 2;
        }
        return height > 3 && height < 20;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES) && this.highEnough(worldIn, pos);
    }

    private boolean canReach(BlockPos target) {
        Path path = this.atlatitan.getNavigation().createPath(target, 0);
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
