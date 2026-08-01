package com.qiuyue.goetyominous.common.entities.ai;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.util.Mth;

public abstract class PiglinStoreChestGoal<T extends AbstractPiglinServant> extends PiglinChestGoal<T> {
    public PiglinStoreChestGoal(T piglin) {
        super(piglin);
    }

    protected boolean findNearestBlock() {
        if (this.piglin.getDumpChestPos() != null) {
            this.blockPos = this.piglin.getDumpChestPos();
            if (this.blockPos != null) {
                return this.piglin.distanceToSqr((double)((float)this.blockPos.getX() + 0.5F), (double)((float)this.blockPos.getY() + 0.5F), (double)((float)this.blockPos.getZ() + 0.5F)) <= (double)Mth.square(this.searchRange);
            }
        }
        return super.findNearestBlock();
    }

    public boolean canUse() {
        return !this.hasDumpChest() && !this.hasRegularChest() ? false : super.canUse();
    }

    public boolean hasDumpChest() {
        if (this.piglin.getDumpChestPos() != null && this.piglin.getDumpChestLevel() == this.piglin.level().dimension()) {
            boolean flag = true;
            if (this.piglin.getBoundPos() != null) {
                flag = this.piglin.isWithinGuard(this.piglin.getDumpChestPos());
            }
            if (flag) {
                return this.getChest(this.piglin.level(), this.piglin.getDumpChestPos()) != null && !this.isFull(this.getItem(), this.piglin.level(), this.piglin.getDumpChestPos());
            }
        }
        return false;
    }

    public boolean hasRegularChest() {
        if (this.piglin.getChestPos() != null && this.piglin.getChestLevel() == this.piglin.level().dimension()) {
            boolean flag = true;
            if (this.piglin.getBoundPos() != null) {
                flag = this.piglin.isWithinGuard(this.piglin.getChestPos());
            }
            if (flag) {
                return this.getChest(this.piglin.level(), this.piglin.getChestPos()) != null && !this.isFull(this.getItem(), this.piglin.level(), this.piglin.getChestPos());
            }
        }
        return false;
    }
}
