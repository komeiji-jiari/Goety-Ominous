package com.qiuyue.goetyominous.compat.jade;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MutantServantLifeComponent implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int maxLives = data.getInt("MaxLives");
        if (maxLives <= 0) {
            return;
        }
        int remaining = data.getInt("RemainingLives");
        tooltip.add(Component.translatable("jade.goetyominous.remaining_lives", remaining, maxLives));
        if (remaining >= maxLives) {
            return;
        }
        int total = data.getInt("LifeRecoveryTicks");
        int progress = Math.min(data.getInt("LifeRecoveryProgress"), total);
        int seconds = (int)Math.ceil((total - progress) / 20.0D);
        tooltip.add(Component.translatable("jade.goetyominous.life_recovery", seconds));
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "mutant_servant_life");
    }
}
