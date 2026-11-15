package com.qiuyue.goetyominous.compat.jade;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum GuzzlerUpgradeProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof Guzzler guzzler) {
            tag.putInt("GuzzlerUpgradeLevel", guzzler.getUpgradeLevel());
            tag.putInt("GuzzlerUpgradePoints", guzzler.getUpgradePoints());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "guzzler_upgrade");
    }
}
