package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.LivingEntity;

public class BHSynergyStateGoal extends KnightStateGoal {

    public BHSynergyStateGoal(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                              int attackendstate, int attackfinaltick, int attackseetick) {
        super(entity, getattackstate, attackstate, attackendstate, attackfinaltick, attackseetick);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < 30
                || this.entity.attackTicks > 60 && this.entity.attackTicks < 72
                || this.entity.attackTicks > 100 && this.entity.attackTicks < 135) {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
                this.entity.lookAt(target, 30.0F, 30.0F);
            }
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }
}
