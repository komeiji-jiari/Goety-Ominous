package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

final class MutantTargeting {
    private MutantTargeting() {
    }

    static boolean isDefendingOwner(LivingEntity servant, LivingEntity target) {
        if (!(servant instanceof IOwned ownedServant) || !(target instanceof IOwned ownedTarget)) {
            return false;
        }
        LivingEntity owner = ownedServant.getTrueOwner();
        if (owner == null || ownedTarget.getTrueOwner() == owner) {
            return false;
        }
        return target instanceof Mob mob && mob.getTarget() == owner;
    }
}
