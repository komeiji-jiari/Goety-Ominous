package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractMiredEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MiredServant extends AbstractMiredEntity {

    public MiredServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
    }
}
