package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DredenServant extends AbstractDredenEntity {

    public DredenServant(EntityType<? extends AbstractDredenEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public java.util.function.Predicate<net.minecraft.world.entity.Entity> summonPredicate() {
        return entity -> entity instanceof DredenServant
                || entity instanceof com.Polarice3.Goety.common.entities.ally.undead.WraithServant
                || entity instanceof com.Polarice3.Goety.common.entities.ally.undead.BorderWraithServant
                || entity instanceof com.Polarice3.Goety.common.entities.ally.undead.MuckWraithServant;
    }

    @Override
    public int getSummonLimit(net.minecraft.world.entity.LivingEntity owner) {
        return com.Polarice3.Goety.config.SpellConfig.WraithLimit.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new Summoned.WanderGoal(this, 1.0D, 0.0F));
    }
}
