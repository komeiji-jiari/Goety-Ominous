package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.miauczel.legendary_monsters.entity.ai.navigation.ModPathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
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

public class ServantPathNavigation extends ModPathNavigation {

    public ServantPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new ServantPathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected void followThePath() {
        Path path = this.path;
        Vec3 pos = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        BlockPos next = path.getNextNodePos();
        double dx = Math.abs(this.mob.getX() - ((double) next.getX() + 0.5));
        double dy = Math.abs(this.mob.getY() - (double) next.getY());
        double dz = Math.abs(this.mob.getZ() - ((double) next.getZ() + 0.5));
        if (dx < (double) this.maxDistanceToWaypoint && dz < (double) this.maxDistanceToWaypoint && dy < 1.0
                || this.canCutCorner(path.getNextNode().type) && this.shouldTargetNextNodeInDirection(pos)) {
            path.advance();
        }
        this.doStuckDetection(pos);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 pos) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        }
        Vec3 nodeCenter = Vec3.atBottomCenterOf(this.path.getNextNodePos());
        if (!pos.closerThan(nodeCenter, 2.0)) {
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
        if (!(nextSqr < currentSqr) && !(currentSqr < 0.5)) {
            return false;
        }
        return toNext.normalize().dot(toCurrent.normalize()) < 0.0;
    }

    private static class ServantPathFinder extends PathFinder {

        ServantPathFinder(NodeEvaluator nodeEvaluator, int maxVisitedNodes) {
            super(nodeEvaluator, maxVisitedNodes);
        }

        @Nullable
        @Override
        public Path findPath(PathNavigationRegion region, Mob mob, Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
            Path path = super.findPath(region, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);
            return path == null ? null : new ServantPath(path);
        }
    }

    private static class ServantPath extends Path {

        ServantPath(Path original) {
            super(copyNodes(original), original.getTarget(), original.canReach());
        }

        @Override
        public Vec3 getEntityPosAtNode(Entity entity, int index) {
            Node node = this.getNode(index);
            double offset = (double) Mth.clamp(entity.getBbWidth() + 1.0F, 0.0F, 1.0F) * 0.5D;
            return new Vec3((double) node.x + offset, (double) node.y, (double) node.z + offset);
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
