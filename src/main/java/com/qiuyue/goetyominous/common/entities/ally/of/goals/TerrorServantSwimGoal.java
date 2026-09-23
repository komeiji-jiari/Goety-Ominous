package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TerrorServantSwimGoal extends Summoned.WanderGoal<TerrorServant> {
    private final TerrorServant terror;

    public TerrorServantSwimGoal(TerrorServant terror, double speedModifier) {
        super(terror, speedModifier, 20, 1.0F);
        this.terror = terror;
    }

    @Override
    public boolean canUse() {
        return this.terror.isInWater() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.terror.isInWater() && super.canContinueToUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (((IServant) this.terror).isGuardingArea()) {
            return this.boundWaterPos();
        }
        return BehaviorUtils.getRandomSwimmablePos(this.terror, 10, 7);
    }

    @Nullable
    private Vec3 boundWaterPos() {
        BlockPos boundPos = ((IServant) this.terror).getBoundPos();
        if (boundPos == null) {
            return null;
        }
        int range = IServant.GUARDING_RANGE / 2;
        for (int i = 0; i < 10; ++i) {
            BlockPos pos = boundPos.offset(
                    this.terror.getRandom().nextIntBetweenInclusive(-range, range),
                    this.terror.getRandom().nextIntBetweenInclusive(-range, range),
                    this.terror.getRandom().nextIntBetweenInclusive(-range, range));
            if (GoalUtils.isWater(this.terror, pos)) {
                return Vec3.atBottomCenterOf(pos);
            }
        }
        return null;
    }
}
