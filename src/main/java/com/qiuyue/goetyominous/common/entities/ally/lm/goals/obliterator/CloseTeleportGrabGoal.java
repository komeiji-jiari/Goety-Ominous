package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoalMin;
import net.minecraft.util.Mth;

public class CloseTeleportGrabGoal extends IAttackGoalMin {
    protected final TheObliteratorServant entity;

    public CloseTeleportGrabGoal(TheObliteratorServant entity, int getattackstate, int attackstate,
                                 int attackendstate, int attackMaxtick, int attackseetick,
                                 float attackrange, float attackrangemin) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick, attackrange,
                attackrangemin);
        this.entity = entity;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        int attackTicks = this.entity.getAttackTicks();
        if (this.entity.succedGrabbing && attackTicks >= 36 && attackTicks <= 39) {
            this.entity.setInvisible(true);
            if (this.entity.targetIsNotNull()) {
                float f = Mth.cos(this.entity.target().yBodyRot * ((float) Math.PI / 180));
                float f1 = Mth.sin(this.entity.target().yBodyRot * ((float) Math.PI / 180));
                double theta = (double) this.entity.target().yBodyRot * (Math.PI / 180);
                double vecX = Math.cos(theta += 1.5707963267948966);
                double vecZ = Math.sin(theta);
                float vec = 2.0F;
                float offset = 0.0F;
                this.entity.teleportTo(this.entity.target().getX() + (double) vec * vecX + (double) (f * offset),
                        this.entity.target().getY() + 1.0,
                        this.entity.target().getZ() + (double) vec * vecZ + (double) (f1 * offset));
            }
        }
        super.tick();
    }

    @Override
    public void stop() {
        if (this.entity.succedGrabbing) {
            if (!this.entity.level().isClientSide) {
                this.entity.setAttackState(18);
            }
            this.entity.succedGrabbing = false;
        } else {
            super.stop();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
