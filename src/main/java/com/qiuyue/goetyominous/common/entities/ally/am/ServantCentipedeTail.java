package com.qiuyue.goetyominous.common.entities.ally.am;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

public class ServantCentipedeTail extends ServantCentipedeBody {
    public ServantCentipedeTail(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public ServantCentipedeTail(EntityType type, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(type, parent, radius, angleYaw, offsetY);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}