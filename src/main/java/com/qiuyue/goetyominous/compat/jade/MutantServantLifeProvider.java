package com.qiuyue.goetyominous.compat.jade;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mm.AbstractMutantServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum MutantServantLifeProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof AbstractMutantServant servant) {
            int maxLives = servant.getMaxLives();
            if (maxLives <= 0) {
                return;
            }
            tag.putInt("RemainingLives", servant.getRemainingLives());
            tag.putInt("MaxLives", maxLives);
            tag.putInt("LifeRecoveryProgress", servant.getLifeRecoveryProgress());
            tag.putInt("LifeRecoveryTicks", servant.getLifeRecoveryTicks());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "mutant_servant_life");
    }
}
