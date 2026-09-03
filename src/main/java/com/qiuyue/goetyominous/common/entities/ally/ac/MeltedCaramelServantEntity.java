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

/**
 * 焦糖液(融化焦糖):继承 AC 的 MeltedCaramelEntity,外观与黏着行为与原版完全一致,
 * 只把"谁能豁免黏着"从 AC 的 instanceof CaramelCubeEntity 换成"本仆从所属主人阵营免疫"。
 *
 * AC 原版逻辑:MeltedCaramelEntity#slowEntities 每 tick 给包围盒内所有 LivingEntity 施加黏着,
 * 唯一豁免是 CaramelCubeEntity(它免疫自己;焦糖本身无队伍,isAlliedTo 恒假)。
 * 仆从化后若照搬该逻辑,浇出这块焦糖的仆从、其主人本人、主人名下其它仆从都会被自己的焦糖减速。
 * 因此这里记录浇出焦糖的仆从之主(UUID,同步数据里客户端也可读),isAlliedTo 按主人类别判定,做到"只黏敌人"。
 */
public class MeltedCaramelServantEntity extends MeltedCaramelEntity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(MeltedCaramelServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public MeltedCaramelServantEntity(EntityType<? extends MeltedCaramelServantEntity> entityType, Level level) {
        super(entityType, level);
    }

    // 客户端生成(Forge PlayMessages 自定义工厂走这条构造)。
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
            // 主人本人不黏。
            if (living.getUUID().equals(master)) {
                return true;
            }
            // 主人名下其它召唤物/仆从(同一条 IOwned 归属链)不黏。
            if (living instanceof IOwned owned) {
                LivingEntity otherMaster = owned.getTrueOwner();
                if (otherMaster != null && master.equals(otherMaster.getUUID())) {
                    return true;
                }
            }
            // 主人驯养的动物不黏。
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
