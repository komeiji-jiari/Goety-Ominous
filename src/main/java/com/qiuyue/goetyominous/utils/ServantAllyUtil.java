package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

public final class ServantAllyUtil {

    private ServantAllyUtil() {
    }

    public static LivingEntity getSummonOwner(LivingEntity caster) {
        if (caster instanceof IServant servant && servant.getMasterOwner() != null) {
            return servant.getMasterOwner();
        }
        return caster;
    }

    public static boolean areAllied(Entity attacker, Entity target) {
        if (attacker == null || target == null) {
            return false;
        }
        if (attacker == target) {
            return true;
        }
        if (MobUtil.areAllies(attacker, target)) {
            return true;
        }
        if (attacker instanceof IOwned ownedAttacker && target instanceof IOwned ownedTarget
                && MobUtil.ownerStack(ownedAttacker, ownedTarget)) {
            return true;
        }
        LivingEntity ownerAttacker = getOwner(attacker);
        LivingEntity ownerTarget = getOwner(target);
        return ownerAttacker != null && ownerAttacker == target
                || ownerTarget != null && ownerTarget == attacker
                || ownerAttacker != null && ownerAttacker == ownerTarget;
    }

    private static LivingEntity getOwner(Entity entity) {
        if (entity instanceof IOwned owned && owned.getTrueOwner() != null) {
            return owned.getTrueOwner();
        }
        if (entity instanceof OwnableEntity ownable) {
            return ownable.getOwner();
        }
        return null;
    }
}
