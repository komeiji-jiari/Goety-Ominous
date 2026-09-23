package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class DeepOneStrollGoal extends RandomStrollGoal {

    private final PathfinderMob mob;
    private final IDeepOneWanderer wanderer;

    public DeepOneStrollGoal(PathfinderMob mob, double speed, int interval) {
        super(mob, speed, interval, false);
        this.mob = mob;
        if (!(mob instanceof IDeepOneWanderer deepOne)) {
            throw new IllegalArgumentException("DeepOneStrollGoal requires an IDeepOneWanderer entity");
        }
        this.wanderer = deepOne;
    }

    @Override
    public boolean canUse() {
        return this.canStroll() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canStroll() && super.canContinueToUse();
    }

    private boolean canStroll() {
        if (this.mob.isInWaterOrBubble() || this.wanderer.isTrading()) {
            return false;
        }
        if (this.mob instanceof IServant servant) {
            return !servant.isStaying() && !servant.isCommanded();
        }
        return true;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        BlockPos bound = this.getGuardPos();
        if (bound == null) {
            return super.getPosition();
        }
        int range = Math.max(6, IServant.GUARDING_RANGE / 2);
        for (int i = 0; i < 10; ++i) {
            BlockPos probe = bound.offset(this.mob.getRandom().nextInt(2 * range + 1) - range, 0,
                    this.mob.getRandom().nextInt(2 * range + 1) - range);
            BlockPos ground = this.findGround(probe);
            if (ground != null) {
                return Vec3.atBottomCenterOf(ground);
            }
        }
        return null;
    }

    @Nullable
    private BlockPos findGround(BlockPos probe) {
        int y = probe.getY();
        while (y > this.mob.level().getMinBuildHeight() && this.mob.level().getBlockState(probe.atY(y)).isAir()) {
            --y;
        }
        if (y >= probe.getY()) {
            return null;
        }
        BlockPos ground = probe.atY(y + 1);
        return this.mob.level().getFluidState(ground).isEmpty() ? ground : null;
    }

    @Nullable
    private BlockPos getGuardPos() {
        if (this.mob instanceof IServant servant && servant.isGuardingArea()) {
            return servant.getBoundPos();
        }
        return null;
    }
}
