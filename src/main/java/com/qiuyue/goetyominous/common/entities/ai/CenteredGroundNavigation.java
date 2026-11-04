package com.qiuyue.goetyominous.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CenteredGroundNavigation extends GroundPathNavigation {
    public CenteredGroundNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new CenteredPathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected void followThePath() {
        Vec3 pos = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        BlockPos next = this.path.getNextNodePos();
        double dx = Math.abs(this.mob.getX() - ((double) next.getX() + 0.5D));
        double dy = Math.abs(this.mob.getY() - (double) next.getY());
        double dz = Math.abs(this.mob.getZ() - ((double) next.getZ() + 0.5D));
        boolean atNode = dx <= (double) this.maxDistanceToWaypoint && dz <= (double) this.maxDistanceToWaypoint && dy < 1.0D;
        if (atNode || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(pos)) {
            this.path.advance();
        }

        this.doStuckDetection(pos);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 pos) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        }
        Vec3 nodeCenter = Vec3.atBottomCenterOf(this.path.getNextNodePos());
        if (!pos.closerThan(nodeCenter, 2.0D)) {
            return false;
        }
        if (this.canMoveDirectly(pos, this.path.getNextEntityPos(this.mob))) {
            return true;
        }
        Vec3 nextCenter = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
        Vec3 toCurrent = nodeCenter.subtract(pos);
        Vec3 toNext = nextCenter.subtract(pos);
        double currentSqr = toCurrent.lengthSqr();
        double nextSqr = toNext.lengthSqr();
        if (!(nextSqr < currentSqr) && !(currentSqr < 0.5D)) {
            return false;
        }
        return toNext.normalize().dot(toCurrent.normalize()) < 0.0D;
    }

    static class CenteredPathFinder extends PathFinder {
        CenteredPathFinder(NodeEvaluator nodeEvaluator, int maxVisitedNodes) {
            super(nodeEvaluator, maxVisitedNodes);
        }

        @Nullable
        @Override
        public Path findPath(PathNavigationRegion region, Mob mob, Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
            Path path = super.findPath(region, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
            return path == null ? null : new CenteredPath(path);
        }
    }

    static class CenteredPath extends Path {
        CenteredPath(Path original) {
            super(copyNodes(original), original.getTarget(), original.canReach());
        }

        @Override
        public Vec3 getEntityPosAtNode(Entity entity, int index) {
            Node node = this.getNode(index);
            return new Vec3((double) node.x + 0.5D, (double) node.y, (double) node.z + 0.5D);
        }

        private static List<Node> copyNodes(Path original) {
            List<Node> nodes = new ArrayList<>(original.getNodeCount());
            for (int i = 0; i < original.getNodeCount(); ++i) {
                nodes.add(original.getNode(i));
            }
            return nodes;
        }
    }
}
