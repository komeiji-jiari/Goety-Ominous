package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public final class GrimMonarchNavigation extends GroundPathNavigation {
    public GrimMonarchNavigation(Mob mob, Level level) {
        super(mob, level);
        this.setMaxVisitedNodesMultiplier(2.0F);
        this.setCanFloat(true);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new SurfaceNodes();
        this.nodeEvaluator.setCanFloat(true);
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.position();
    }

    @Override
    protected double getGroundY(Vec3 pos) {
        double liquid = GrimMonarchFluidSurface.column(this.level, pos.x, pos.z, pos.y - 1.0, pos.y);
        return Math.max(liquid, super.getGroundY(pos));
    }

    @Override
    public boolean canCutCorner(BlockPathTypes type) {
        return false;
    }

    private Vec3 pathPoint(int index) {
        Vec3 at = this.path.getEntityPosAtNode(this.mob, index);
        return new Vec3(at.x, this.getGroundY(at), at.z);
    }

    @Override
    protected void followThePath() {
        if (this.path == null || this.path.isDone()) {
            return;
        }
        Vec3 here = this.getTempMobPos();
        GrimMonarchEntity boss = (GrimMonarchEntity) this.mob;
        this.tryShortcut(here, boss);
        this.tryAdvance(here, boss);
        this.doStuckDetection(here);
    }

    private void tryShortcut(Vec3 here, GrimMonarchEntity boss) {
        int first = this.path.getNextNodeIndex();
        int last = Math.min(first + 4, this.path.getNodeCount() - 1);
        for (int index = last; index > first; --index) {
            Vec3 candidate = this.pathPoint(index);
            if (candidate.distanceToSqr(here) > 9.0) {
                continue;
            }
            if (!boss.canWalkRoute(here, candidate)) {
                continue;
            }
            while (this.path.getNextNodeIndex() < index) {
                this.path.advance();
            }
            return;
        }
    }

    private void tryAdvance(Vec3 here, GrimMonarchEntity boss) {
        Vec3 next = this.pathPoint(this.path.getNextNodeIndex());
        double dx = here.x - next.x;
        double dz = here.z - next.z;
        if (dx * dx + dz * dz > 0.4225) {
            return;
        }
        if (Math.abs(here.y - next.y) >= 0.65) {
            return;
        }
        int after = this.path.getNextNodeIndex() + 1;
        if (after < this.path.getNodeCount() && !boss.canWalkRoute(here, this.pathPoint(after))) {
            return;
        }
        this.path.advance();
    }

    private static final class SurfaceNodes extends WalkNodeEvaluator {
        private final Map<Long, Boolean> supported = new HashMap<>();

        @Override
        public void prepare(PathNavigationRegion region, Mob mob) {
            super.prepare(region, mob);
            this.supported.clear();
        }

        @Override
        public void done() {
            this.supported.clear();
            super.done();
        }

        @Override
        public BlockPathTypes getBlockPathType(BlockGetter world, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!world.getFluidState(pos).isEmpty()) {
                return BlockPathTypes.BLOCKED;
            }
            BlockPathTypes type = super.getBlockPathType(world, x, y, z);
            if (!Double.isFinite(GrimMonarchFluidSurface.top(world, pos.below()))) {
                return type;
            }
            if (!world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
                return type;
            }
            if (type == BlockPathTypes.OPEN || type == BlockPathTypes.WALKABLE
                    || type == BlockPathTypes.WATER_BORDER || type == BlockPathTypes.DAMAGE_FIRE) {
                return BlockPathTypes.WALKABLE;
            }
            return type;
        }

        @Override
        protected double getFloorLevel(BlockPos pos) {
            return Math.max(super.getFloorLevel(pos), GrimMonarchFluidSurface.top(this.level, pos.below()));
        }

        @Override
        public Node getStart() {
            Node fallback = super.getStart();
            double surface = GrimMonarchFluidSurface.column(this.level, this.mob.getX(), this.mob.getZ(),
                    this.mob.getY() - 0.2, this.mob.getY() + 1.0);
            int baseY = Double.isFinite(surface) ? Mth.ceil(surface) : fallback.y;
            double offset = startOffset();
            int baseX = Mth.floor(this.mob.getX() - offset);
            int baseZ = Mth.floor(this.mob.getZ() - offset);
            Node best = null;
            double nearest = Double.POSITIVE_INFINITY;
            for (int x = baseX - 1; x <= baseX + 2; ++x) {
                for (int z = baseZ - 1; z <= baseZ + 2; ++z) {
                    BlockPos pos = new BlockPos(x, baseY, z);
                    if (!this.canStartAt(pos)) {
                        continue;
                    }
                    Node node = this.getStartNode(pos);
                    Vec3 center = this.center(node);
                    double distance = center.distanceToSqr(this.mob.position());
                    if (distance >= nearest) {
                        continue;
                    }
                    if (!this.usable(node)) {
                        continue;
                    }
                    if (!((GrimMonarchEntity) this.mob).canWalkRoute(this.mob.position(), center)) {
                        continue;
                    }
                    nearest = distance;
                    best = node;
                }
            }
            return best == null ? fallback : best;
        }

        private double startOffset() {
            return (double) ((int) (this.mob.getBbWidth() + 1.0F)) * 0.5;
        }

        private Vec3 center(Node node) {
            double offset = startOffset();
            double floor = this.getFloorLevel(new BlockPos(node.x, node.y, node.z));
            return new Vec3((double) node.x + offset, floor, (double) node.z + offset);
        }

        private boolean usable(Node node) {
            long key = BlockPos.asLong(node.x, node.y, node.z);
            return this.supported.computeIfAbsent(key, ignored -> {
                Vec3 center = this.center(node);
                return ((GrimMonarchEntity) this.mob).canWalkRoute(center, center);
            });
        }

        @Override
        public int getNeighbors(Node[] result, Node from) {
            int count = super.getNeighbors(result, from);
            Vec3 start = this.center(from);
            int kept = 0;
            for (int index = 0; index < count; ++index) {
                Node next = result[index];
                if (!this.usable(next)) {
                    continue;
                }
                if (!((GrimMonarchEntity) this.mob).canWalkRoute(start, this.center(next))) {
                    continue;
                }
                result[kept++] = next;
            }
            return kept;
        }
    }
}
