package com.qiuyue.someillagerservants.common.entities.ally.ua.goals;

import com.qiuyue.someillagerservants.common.entities.ally.ua.ThrasherServant;
import com.teamabnormals.blueprint.core.api.AdvancedRandomPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ThrasherServantRandomSwimGoal extends RandomSwimmingGoal {
    private final ThrasherServant thrasher;

    public ThrasherServantRandomSwimGoal(ThrasherServant thrasher, double speed, int chance) {
        super(thrasher, speed, chance);
        this.thrasher = thrasher;
    }

    public boolean canUse() {
        if (!this.forceTrigger) {
            if (this.thrasher.getNoActionTime() >= 100) {
                return false;
            }

            if (this.thrasher.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
        }

        Vec3 vec3d = this.getPosition();
        if (vec3d == null) {
            return false;
        } else {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            this.forceTrigger = false;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec3d = AdvancedRandomPos.findRandomTarget(this.mob, 15, 8, !this.thrasher.getPassengers().isEmpty());

        for (int i = 0; vec3d != null && !this.mob.level().getBlockState(BlockPos.containing(vec3d)).isPathfindable(this.mob.level(), BlockPos.containing(vec3d), PathComputationType.WATER) && i++ < 10; vec3d = AdvancedRandomPos.findRandomTarget(this.mob, 10, 8, !this.thrasher.getPassengers().isEmpty())) {
        }

        return vec3d;
    }
}
