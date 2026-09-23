package com.qiuyue.goetyominous.common.magic.utils;

import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class ContinuousControllerSpell<T extends ContinuousControllerSpell.BaseController>
        extends EverChargeSpell {

    protected abstract Class<T> getControllerType();

    @Nullable
    protected abstract T createController(ServerLevel world, LivingEntity caster,
                                          ItemStack staff, SpellStat spellStat);

    protected Object controllerKey(LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        return "";
    }

    protected double searchRange() {
        return 50.0D;
    }

    protected void onCastStart(ServerLevel world, LivingEntity caster,
                               ItemStack staff, SpellStat spellStat) {
    }

    protected void onCastTick(ServerLevel world, LivingEntity caster,
                              ItemStack staff, SpellStat spellStat) {
    }

    @Override
    public final void SpellResult(ServerLevel worldIn, LivingEntity caster,
                                  ItemStack staff, SpellStat spellStat) {
        Object key = controllerKey(caster, staff, spellStat);
        UUID casterUUID = caster.getUUID();

        List<T> sameKey = worldIn.getEntitiesOfClass(
                getControllerType(),
                caster.getBoundingBox().inflate(searchRange()),
                c -> c.isOwnedBy(casterUUID) && Objects.equals(c.getControllerKey(), key)
        );
        if (!sameKey.isEmpty()) {
            sameKey.get(0).refresh();
            onCastTick(worldIn, caster, staff, spellStat);
            return;
        }

        worldIn.getEntitiesOfClass(
                getControllerType(),
                caster.getBoundingBox().inflate(searchRange()),
                c -> c.isOwnedBy(casterUUID)
        ).forEach(Entity::discard);

        onCastStart(worldIn, caster, staff, spellStat);
        T controller = createController(worldIn, caster, staff, spellStat);
        if (controller != null) {
            controller.bindCaster(casterUUID);
            controller.setPos(caster.position());
            worldIn.addFreshEntity(controller);
        }
    }
    public abstract static class BaseController extends Entity {

        private static final String TAG_CASTER = "Caster";
        private static final String TAG_REFRESH = "LastRefresh";

        protected UUID casterUUID;
        protected int lastRefreshTick;

        public BaseController(EntityType<?> type, Level level) {
            super(type, level);
        }

        protected int getTimeout() {
            return 10;
        }

        protected abstract void onTick(ServerLevel level, LivingEntity caster);

        protected void onRefresh() {
        }

        protected void onDespawn(ServerLevel level, @Nullable LivingEntity caster) {
        }

        protected boolean shouldDespawn(ServerLevel level, LivingEntity caster) {
            return this.tickCount - lastRefreshTick > getTimeout();
        }

        protected boolean followCaster() {
            return true;
        }

        public abstract Object getControllerKey();

        protected void clientTick() {
        }

        public final void bindCaster(UUID uuid) {
            this.casterUUID = uuid;
            this.lastRefreshTick = this.tickCount;
        }

        public final boolean isOwnedBy(UUID uuid) {
            return Objects.equals(this.casterUUID, uuid);
        }

        public final void refresh() {
            this.lastRefreshTick = this.tickCount;
            this.onRefresh();
        }

        @Nullable
        public final LivingEntity getCaster(ServerLevel level) {
            if (casterUUID == null) {
                return null;
            }
            Entity e = level.getEntity(casterUUID);
            return e instanceof LivingEntity living ? living : null;
        }

        @Override
        public final void tick() {
            if (this.level().isClientSide) {
                this.clientTick();
                return;
            }
            if (!(this.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            LivingEntity caster = getCaster(serverLevel);
            if (caster == null || !caster.isAlive()) {
                onDespawn(serverLevel, caster);
                this.discard();
                return;
            }

            if (followCaster()) {
                this.setPos(caster.position());
            }

            if (shouldDespawn(serverLevel, caster)) {
                onDespawn(serverLevel, caster);
                this.discard();
                return;
            }

            onTick(serverLevel, caster);
        }

        @Override
        protected void defineSynchedData() {
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {
            if (tag.hasUUID(TAG_CASTER)) {
                this.casterUUID = tag.getUUID(TAG_CASTER);
            }
            this.lastRefreshTick = tag.getInt(TAG_REFRESH);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            if (casterUUID != null) {
                tag.putUUID(TAG_CASTER, casterUUID);
            }
            tag.putInt(TAG_REFRESH, lastRefreshTick);
        }

        @Override
        public Packet<ClientGamePacketListener> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this);
        }

        @Override
        public boolean shouldRenderAtSqrDistance(double d) {
            return false;
        }

        @Override
        public boolean shouldRender(double x, double y, double z) {
            return false;
        }
    }
}