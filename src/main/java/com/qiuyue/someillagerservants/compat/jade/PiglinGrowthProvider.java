package com.qiuyue.someillagerservants.compat.jade;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.PiglinServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum PiglinGrowthProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof PiglinServant piglin && piglin.isBaby()) {
            tag.putInt("GrowingTime", -piglin.getAge());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(SomeIllagerServants.MOD_ID, "piglin_growth");
    }
}