package com.qiuyue.goetyominus.compat.jade;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum PiglinEvolutionProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof AbstractPiglinServant piglin && !piglin.isBaby()) {
            tag.putInt("MeleeDamageDealt", piglin.getMeleeDamageDealt());
            tag.putInt("RangedDamageDealt", piglin.getRangedDamageDealt());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "piglin_evolution");
    }
}
