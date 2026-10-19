package com.qiuyue.goetyominous.common.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

import java.util.Arrays;

public class MovementBodyRotationControl extends BodyRotationControl {

    private static final int HISTORY_SIZE = 20;
    private static final int RECENT_TICKS = 4;
    private static final float BODY_FOLLOW = 0.5F;
    private static final float MAX_BODY_STEP = 12.0F;
    private static final double RECENT_MOVE_SQR = 0.0025D;
    private static final double TELEPORT_SQR = 4.0D;

    private final Mob mob;
    private final double[] historyX = new double[HISTORY_SIZE];
    private final double[] historyZ = new double[HISTORY_SIZE];
    private int cursor;
    private int filled;
    private int headStableTime;
    private float lastStableYHeadRot;

    public MovementBodyRotationControl(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void clientTick() {
        double x = this.mob.getX();
        double z = this.mob.getZ();
        int last = (this.cursor - 1 + HISTORY_SIZE) % HISTORY_SIZE;
        if (this.filled > 0) {
            double jumpX = x - this.historyX[last];
            double jumpZ = z - this.historyZ[last];
            if (jumpX * jumpX + jumpZ * jumpZ > TELEPORT_SQR) {
                Arrays.fill(this.historyX, x);
                Arrays.fill(this.historyZ, z);
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
            }
        }
        this.historyX[this.cursor] = x;
        this.historyZ[this.cursor] = z;
        this.cursor = (this.cursor + 1) % HISTORY_SIZE;
        if (this.filled < HISTORY_SIZE) {
            ++this.filled;
        }
        double recentX = this.deltaX(RECENT_TICKS);
        double recentZ = this.deltaZ(RECENT_TICKS);
        if (recentX * recentX + recentZ * recentZ > RECENT_MOVE_SQR) {
            float movementYaw = (float) (Mth.atan2(recentZ, recentX) * (180.0D / Math.PI)) - 90.0F;
            float delta = Mth.wrapDegrees(movementYaw - this.mob.yBodyRot);
            this.mob.yBodyRot += Mth.clamp(delta * BODY_FOLLOW, -MAX_BODY_STEP, MAX_BODY_STEP);
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, this.mob.getMaxHeadYRot());
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
        } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, this.mob.getMaxHeadYRot());
            } else {
                ++this.headStableTime;
                if (this.headStableTime > 10) {
                    int i = this.headStableTime - 10;
                    float f = Mth.clamp((float) i / 10.0F, 0.0F, 1.0F);
                    float limit = this.mob.getMaxHeadYRot() * (1.0F - f);
                    this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, limit);
                }
            }
        }
    }

    private double deltaX(int ticksAgo) {
        if (this.filled <= ticksAgo) {
            return 0.0D;
        }
        int index = (this.cursor - 1 - ticksAgo + HISTORY_SIZE * 2) % HISTORY_SIZE;
        return this.mob.getX() - this.historyX[index];
    }

    private double deltaZ(int ticksAgo) {
        if (this.filled <= ticksAgo) {
            return 0.0D;
        }
        int index = (this.cursor - 1 - ticksAgo + HISTORY_SIZE * 2) % HISTORY_SIZE;
        return this.mob.getZ() - this.historyZ[index];
    }

    private boolean notCarryingMobPassengers() {
        return !(this.mob.getFirstPassenger() instanceof Mob);
    }
}
