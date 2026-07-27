package com.qiuyue.goetyominus.compat.jade;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum PiglinBreedProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof AbstractPiglinServant piglin) {
            int breedCool = piglin.getBreedCool();
            if (breedCool > 0) {
                tag.putInt("BreedingCD", breedCool);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "piglin_breed");
    }
}
