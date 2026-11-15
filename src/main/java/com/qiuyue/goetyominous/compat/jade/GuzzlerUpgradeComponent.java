package com.qiuyue.goetyominous.compat.jade;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GuzzlerUpgradeComponent implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Guzzler)) {
            return;
        }
        CompoundTag data = accessor.getServerData();
        int level = data.getInt("GuzzlerUpgradeLevel");
        int points = data.getInt("GuzzlerUpgradePoints");
        if (level <= 0 && points <= 0) {
            return;
        }
        int required = Guzzler.getRequiredPoints(level);
        int shown = level >= Guzzler.MAX_UPGRADE_LEVEL ? required : points;
        tooltip.add(Component.translatable("jade.goetyominous.guzzler_upgrade", level, shown, required));
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "guzzler_upgrade");
    }
}
