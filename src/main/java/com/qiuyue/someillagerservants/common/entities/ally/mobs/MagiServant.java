package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.EntityFinder;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class MagiServant extends Summoned implements IllagerAttack {
    protected static final EntityDataAccessor<Optional<UUID>> MAGI_UUID = SynchedEntityData.defineId(MagiServant.class, EntityDataSerializers.OPTIONAL_UUID);

    public MagiServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAGI_UUID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Magi")) {
            this.setMagiID(compound.getUUID("Magi"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getMagi() != null) {
            compound.putUUID("Magi", this.getMagi().getUUID());
        }
    }

    @Nullable
    public LivingEntity getMagi() {
        UUID uuid = this.getMagiID();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(this.level(), uuid);
    }

    @Nullable
    public UUID getMagiID() {
        return this.entityData.get(MAGI_UUID).orElse(null);
    }

    public void setMagiID(@Nullable UUID p_184754_1_) {
        this.entityData.set(MAGI_UUID, Optional.ofNullable(p_184754_1_));
    }

    public void setMagi(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setMagiID(livingEntity.getUUID());
        }
    }

    @Override
    public int xpReward() {
        return 0;
    }

}