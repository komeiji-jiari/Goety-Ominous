package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.Entity;

public final class ServantAllyUtil {

    private ServantAllyUtil() {
    }

    public static boolean areAllied(Entity attacker, Entity target) {
        if (attacker == null || target == null) {
            return false;
        }
        if (MobUtil.areAllies(attacker, target)) {
            return true;
        }
        return attacker instanceof IOwned ownedAttacker
                && target instanceof IOwned ownedTarget
                && MobUtil.ownerStack(ownedAttacker, ownedTarget);
    }
}
