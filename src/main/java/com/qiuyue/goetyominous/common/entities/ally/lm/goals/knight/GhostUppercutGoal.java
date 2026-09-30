package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.LivingEntity;

public class GhostUppercutGoal extends KnightAttackGoal {

    public GhostUppercutGoal(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                             int attackendstate, int attackMaxtick, int attackseetick,
                             float attackrange) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick, attackrange);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < 19 || this.entity.attackTicks >= 35 && this.entity.attackTicks < 45) {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
                this.entity.lookAt(target, 30.0F, 30.0F);
            }
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }
}
