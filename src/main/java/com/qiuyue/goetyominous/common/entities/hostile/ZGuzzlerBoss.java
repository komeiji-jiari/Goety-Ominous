package com.qiuyue.goetyominous.common.entities.hostile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ZGuzzlerBoss extends GuzzlerBoss {

    public ZGuzzlerBoss(EntityType<? extends GuzzlerBoss> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public float getVoicePitch() {
        return 0.55F;
    }
}
