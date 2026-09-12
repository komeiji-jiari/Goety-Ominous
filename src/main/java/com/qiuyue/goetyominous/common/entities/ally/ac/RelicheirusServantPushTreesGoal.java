package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingTreeBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RelicheirusServantPushTreesGoal extends MoveToBlockGoal {
    private static final int MAXIMUM_BLOCKS_PUSHED = 300;
    public static final int MAX_TREE_SPREAD = 12;
    private final RelicheirusServant relicheirus;
    private boolean madeTreeEntity = false;

    public RelicheirusServantPushTreesGoal(RelicheirusServant relicheirus, int range) {
        super(relicheirus, 1.0D, range, 6);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.relicheirus = relicheirus;
    }

    private boolean isAvailable() {
        return !this.relicheirus.isStaying() && !this.relicheirus.isCommanded()
                && this.relicheirus.getControllingPassenger() == null && !this.relicheirus.isImmobile();
    }

    private boolean isFedPrimordialSoup() {
        return this.relicheirus.getPushingTreesFor() > 0;
    }

    private boolean mayPushTrees() {
        return this.isAvailable() && (this.isFedPrimordialSoup() || this.relicheirus.getTarget() == null);
    }

    @Override
    public boolean canUse() {
        return !this.relicheirus.isBaby() && this.mayPushTrees() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !this.madeTreeEntity;
    }

    @Override
    protected int nextStartTick(net.minecraft.world.entity.PathfinderMob mob) {
        return this.isFedPrimordialSoup()
                ? reducedTickDelay(10 + this.relicheirus.getRandom().nextInt(20))
                : super.nextStartTick(mob);
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
        return this.relicheirus.getStandAtTreePos(this.getBottomOfTree(this.relicheirus.level(), this.blockPos));
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = this.getMoveToTarget();
        if (target == null) {
            return;
        }
        if (this.isReachedTarget()) {
            if (this.relicheirus.lockTreePosition(this.blockPos)) {
                if (this.relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    this.relicheirus.setPeckY(this.blockPos.getY());
                    this.relicheirus.syncAnimation(RelicheirusServant.ANIMATION_PUSH_TREE);
                } else if (this.relicheirus.getAnimation() == RelicheirusServant.ANIMATION_PUSH_TREE
                        && this.relicheirus.getAnimationTick() >= 35 && !this.madeTreeEntity) {
                    this.madeTreeEntity = true;
                    this.relicheirus.playSound(ACSoundRegistry.RELICHEIRUS_TOPPLE.get());
                    ArrayList<BlockPos> gathered = new ArrayList<>();
                    this.gatherAttachedBlocks(this.blockPos, this.blockPos, gathered);
                    if (!gathered.isEmpty()) {
                        ArrayList<MovingBlockData> allData = new ArrayList<>();
                        for (BlockPos pos : gathered) {
                            BlockState moveState = this.relicheirus.level().getBlockState(pos);
                            BlockEntity te = this.relicheirus.level().getBlockEntity(pos);
                            BlockPos offset = pos.subtract(this.blockPos);
                            MovingBlockData data = new MovingBlockData(moveState, moveState.getShape(this.relicheirus.level(), pos), offset, te == null ? null : te.saveWithoutMetadata());
                            this.relicheirus.level().removeBlockEntity(pos);
                            allData.add(data);
                        }
                        for (BlockPos pos : gathered) {
                            this.relicheirus.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                        FallingTreeBlockEntity fallingTree = ACEntityRegistry.FALLING_TREE_BLOCK.get().create(this.relicheirus.level());
                        if (fallingTree != null) {
                            fallingTree.moveTo(Vec3.atCenterOf(this.blockPos));
                            fallingTree.setAllBlockData(FallingTreeBlockEntity.createTagFromData(allData));
                            fallingTree.setPlacementCooldown(1);
                            Vec3 vec3 = Vec3.atCenterOf(this.blockPos).subtract(this.relicheirus.position());
                            float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                            fallingTree.setFallDirection(Direction.fromYRot(f));
                            this.relicheirus.level().addFreshEntity(fallingTree);
                        }
                    }
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
        this.madeTreeEntity = false;
        super.stop();
    }

    private BlockPos getBottomOfTree(LevelReader worldIn, BlockPos pos) {
        while (pos.getY() > worldIn.getMinBuildHeight()
                && (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES)
                || worldIn.getBlockState(pos).isAir()
                || worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS))) {
            pos = pos.below();
        }
        return pos;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS)) {
            BlockPos treeTop = new BlockPos(pos);
            while (worldIn.getBlockState(treeTop).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS) && treeTop.getY() < worldIn.getMaxBuildHeight()) {
                treeTop = treeTop.above();
            }
            return worldIn.getBlockState(treeTop).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES);
        }
        return false;
    }

    public void gatherAttachedBlocks(BlockPos origin, BlockPos pos, List<BlockPos> list) {
        if (list.size() < MAXIMUM_BLOCKS_PUSHED && !list.contains(pos)) {
            list.add(pos);
            for (BlockPos blockpos1 : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if (blockpos1.equals(pos)
                        || !(pos.distToCenterSqr(origin.getX(), pos.getY(), origin.getZ()) < (double) MAX_TREE_SPREAD)
                        || !this.isTreePart(blockpos1)) {
                    continue;
                }
                this.gatherAttachedBlocks(origin, blockpos1.immutable(), list);
            }
        }
    }

    public boolean isTreePart(BlockPos pos) {
        BlockState state = this.relicheirus.level().getBlockState(pos);
        if (state.isAir() || state.is(ACTagRegistry.UNMOVEABLE)) {
            return false;
        }
        return state.is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS) || state.is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES);
    }
}
