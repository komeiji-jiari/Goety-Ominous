package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormServant;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormServantRidingGoal extends Goal {

    private final GumWormServant entity;
    private float leapRot;

    public GumWormServantRidingGoal(GumWormServant worm) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        return this.entity.isRidingMode();
    }

    @Override
    public void stop() {
        this.entity.setLeaping(false);
        this.entity.setRidingLeapTime(0);
        this.entity.setBiting(false);
    }

    @Override
    public void tick() {
        Player ridingPlayer = this.entity.getRidingPlayer();
        if (ridingPlayer != null) {
            this.entity.getNavigation().stop();
            if (this.entity.getRidingLeapTime() > 0 && this.entity.isValidRider()) {
                float f = Math.max(1.0F, (float) this.entity.getMaxRidingLeapTime());
                this.entity.setLeaping(true);
                float f1 = 1.0F - (float) this.entity.getRidingLeapTime() / f;
                Vec3 leapDelta = new Vec3(0.0D, Math.sin((double) f1 * Math.PI * 1.5D) * 2.0D, 2.0D).yRot((float) (-Math.toRadians(this.leapRot)));
                this.entity.setDeltaMovement(leapDelta);
                this.entity.setYRot(this.leapRot);
                this.entity.setRidingLeapTime(this.entity.getRidingLeapTime() - 1);
            } else {
                this.entity.setLeaping(false);
                Vec3 forwardsVec = new Vec3(this.entity.isValidRider() ? (double) (ridingPlayer.xxa * 2.5F) : 0.0D, 0.0D, 10.0D)
                        .yRot((float) (-Math.toRadians(this.entity.yBodyRot))).add(this.entity.position());
                this.entity.getMoveControl().setWantedPosition(forwardsVec.x, forwardsVec.y, forwardsVec.z, 3.0D);
                this.entity.setTargetDigPitch(this.entity.horizontalCollision ? -45.0F : 0.0F);
                this.leapRot = this.entity.getYRot();
            }
            if (this.entity.isMouthOpen()) {
                this.entity.attackAllAroundMouth((float) this.entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 2.0F);
            }
            this.entity.setBiting(this.entity.isMouthForcedOpen());
        }
    }
}
