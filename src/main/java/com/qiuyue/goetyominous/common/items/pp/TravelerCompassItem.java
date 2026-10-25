package com.qiuyue.goetyominous.common.items.pp;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import com.qiuyue.goetyominous.common.items.explore.ExpeditionCompassItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class TravelerCompassItem extends ExpeditionCompassItem {
    public TravelerCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean acceptsTraveler(LivingEntity entity) {
        return entity instanceof PiglinTravelerServant;
    }

    @Override
    public boolean canTravelTo(LivingEntity traveler, ServerLevel destination) {
        return destination.dimensionType().piglinSafe()
                || traveler instanceof AbstractPiglinServant piglin && piglin.isImmune();
    }
}
