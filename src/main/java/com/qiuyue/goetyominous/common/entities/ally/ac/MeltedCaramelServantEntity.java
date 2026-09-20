package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.IOwned;
import com.github.alexmodguy.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MeltedCaramelServantEntity extends MeltedCaramelEntity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(MeltedCaramelServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public MeltedCaramelServantEntity(EntityType<? extends MeltedCaramelServantEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MeltedCaramelServantEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType<MeltedCaramelServantEntity>) AcEntityRegistry.MELTED_CARAMEL_SERVANT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setOwnerMaster(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerMaster() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        UUID master = this.getOwnerMaster();
        if (master != null && entity instanceof LivingEntity living) {
            if (living.getUUID().equals(master)) {
                return true;
            }
            if (living instanceof IOwned owned) {
                LivingEntity otherMaster = owned.getTrueOwner();
                if (otherMaster != null && master.equals(otherMaster.getUUID())) {
                    return true;
                }
            }
            if (living instanceof OwnableEntity tameable) {
                UUID petOwner = tameable.getOwnerUUID();
                if (petOwner != null && master.equals(petOwner)) {
                    return true;
                }
            }
        }
        return super.isAlliedTo(entity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("OwnerMasterUUIDMost") && compoundTag.contains("OwnerMasterUUIDLeast")) {
            this.setOwnerMaster(new UUID(compoundTag.getLong("OwnerMasterUUIDMost"), compoundTag.getLong("OwnerMasterUUIDLeast")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        UUID master = this.getOwnerMaster();
        if (master != null) {
            compoundTag.putLong("OwnerMasterUUIDMost", master.getMostSignificantBits());
            compoundTag.putLong("OwnerMasterUUIDLeast", master.getLeastSignificantBits());
        }
    }
}
