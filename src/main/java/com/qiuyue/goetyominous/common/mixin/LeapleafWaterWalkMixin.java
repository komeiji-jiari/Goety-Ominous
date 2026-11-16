package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leapleaf.class)
public abstract class LeapleafWaterWalkMixin extends Summoned {

    protected LeapleafWaterWalkMixin(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), require = 1)
    private void goetyominous$bottomWalkSetup(CallbackInfo ci) {
        if ((Object) this instanceof Leapkelp) {
            return;
        }
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof FloatGoal);
        this.navigation.setCanFloat(false);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
    }

    @Inject(method = "tick", at = @At("TAIL"), require = 1)
    private void goetyominous$sinkInWater(CallbackInfo ci) {
        if ((Object) this instanceof Leapkelp || this.level().isClientSide) {
            return;
        }
        if (this.isInWater() && !this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.01D, 0.0D));
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
