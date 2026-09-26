package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class MutantBodyRotationControl extends BodyRotationControl {
    protected final Mob mob;
    private int headStableTime;
    private float lastStableYHeadRot;

    public MutantBodyRotationControl(Mob p_24879_) {
        super(p_24879_);
        this.mob = p_24879_;
    }

    public void clientTick() {
        if (this.isMoving()) {
            float f = Mth.wrapDegrees(this.mob.getYRot() - this.mob.yBodyRot);
            this.mob.yBodyRot += f * 0.3F;
            float f1 = Mth.wrapDegrees(this.mob.getYRot() - this.mob.yBodyRot);
            if (Math.abs(f1) > 50.0F) {
                this.mob.yBodyRot += f1 - Mth.sign(f1) * 50.0F;
            }
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
        } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.rotateBodyIfNecessary();
            } else {
                ++this.headStableTime;
                if (this.headStableTime > 10) {
                    this.rotateHeadTowardsFront();
                }
            }
        }
    }

    protected float getBodyHeadRotLimit() {
        return (float)this.mob.getMaxHeadYRot();
    }

    protected void rotateBodyIfNecessary() {
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, this.getBodyHeadRotLimit());
    }

    protected void rotateHeadIfNecessary() {
        this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, this.getBodyHeadRotLimit());
    }

    protected void rotateHeadTowardsFront() {
        int i = this.headStableTime - 10;
        float f = Mth.clamp((float)i / 10.0F, 0.0F, 1.0F);
        float f1 = this.getBodyHeadRotLimit() * (1.0F - f);
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, f1);
    }

    private boolean notCarryingMobPassengers() {
        return !(this.mob.getFirstPassenger() instanceof Mob);
    }

    private boolean isMoving() {
        double d0 = this.mob.getX() - this.mob.xo;
        double d1 = this.mob.getZ() - this.mob.zo;
        return d0 * d0 + d1 * d1 > 2.500000277905201E-7;
    }
}
