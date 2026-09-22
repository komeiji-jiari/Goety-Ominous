package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.utils.EntityFinder;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GrottoceratopsSpiritEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID =
            SynchedEntityData.defineId(GrottoceratopsSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> OWNER_ENTITY_ID =      // ★ 给客户端渲染用（UUID 解析不到非玩家主人）
            SynchedEntityData.defineId(GrottoceratopsSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ROTATE_OFFSET =
            SynchedEntityData.defineId(GrottoceratopsSpiritEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(GrottoceratopsSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FADING =
            SynchedEntityData.defineId(GrottoceratopsSpiritEntity.class, EntityDataSerializers.BOOLEAN);

    private static final float MAX_FADE = 10.0F;

    private float fadeIn;
    private float prevFadeIn;
    private int lSteps;
    private double lx, ly, lz, lxr, lxd, lyd, lzd;

    public GrottoceratopsSpiritEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public GrottoceratopsSpiritEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.GROTTOCERATOPS_SPIRIT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(OWNER_ENTITY_ID, -1);
        this.entityData.define(ROTATE_OFFSET, 0.0F);
        this.entityData.define(LIFETIME, 0);
        this.entityData.define(FADING, false);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.getUsingEntity();
        this.prevFadeIn = this.fadeIn;

        if (this.isFading() && this.fadeIn > 0.0F) --this.fadeIn;
        if (!this.isFading() && this.fadeIn < MAX_FADE) ++this.fadeIn;
        if (this.isFading() && this.fadeIn <= 0.0F) {
            this.discard();
            return;
        }

        if (!this.level().isClientSide && this.getLifetime() > 0) {      // ★ 75 秒寿命，到期全体淡出
            this.setLifetime(this.getLifetime() - 1);
            if (this.getLifetime() <= 0) {
                this.setFading(true);
                return;
            }
        }

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.FLAME,
                    this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

        if (owner == null) {
            if (!this.level().isClientSide) {
                this.setFading(true);
            }
        } else {
            this.tickGrottoceratops(owner);
        }

        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double dx = this.getX() + (this.lx - this.getX()) / this.lSteps;
                double dy = this.getY() + (this.ly - this.getY()) / this.lSteps;
                double dz = this.getZ() + (this.lz - this.getZ()) / this.lSteps;
                this.setXRot(this.getXRot() + (float) (this.lxr - this.getXRot()) / this.lSteps);
                --this.lSteps;
                this.setPos(dx, dy, dz);
            } else {
                this.reapplyPosition();
            }
        }
    }

    /** AC DinosaurSpiritEntity.tickGrottoceratops:168-179 原样，只把 Player 换成 LivingEntity、去掉"手持长矛"判定 */
    private void tickGrottoceratops(LivingEntity owner) {
        float rot = this.getRotateOffset() + (float) (owner.tickCount * 5);
        Vec3 orbitBy = new Vec3(0.0D, 1.0D, 2.0D).yRot((float) (-Math.toRadians(rot)));
        Vec3 orbitTarget = owner.position().add(orbitBy).subtract(this.position());
        this.setXRot(10.0F);
        this.setDeltaMovement(orbitTarget.scale(0.25D));
        this.noPhysics = true;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x; this.ly = y; this.lz = z; this.lxr = xr; this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.lxd = x; this.lyd = y; this.lzd = z;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setOwner(LivingEntity owner) {
        this.setOwnerUUID(owner.getUUID());
        this.setOwnerEntityId(owner.getId());
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }

    public void setOwnerEntityId(int id) {
        this.entityData.set(OWNER_ENTITY_ID, id);
    }

    public int getOwnerEntityId() {
        return this.entityData.get(OWNER_ENTITY_ID);
    }

    public boolean isOwnedBy(LivingEntity living) {
        UUID uuid = this.getOwnerUUID();
        return uuid != null && uuid.equals(living.getUUID());
    }

    @Nullable
    public LivingEntity getUsingEntity() {
        if (this.level().isClientSide) {                       // 客户端：主人的实体 id 也在同步字段里
            Entity entity = this.level().getEntity(this.getOwnerEntityId());
            return entity instanceof LivingEntity living ? living : null;
        }
        UUID uuid = this.getOwnerUUID();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(this.level(), uuid);
    }

    public float getRotateOffset() {
        return this.entityData.get(ROTATE_OFFSET);
    }

    public void setRotateOffset(float offset) {
        this.entityData.set(ROTATE_OFFSET, offset);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int ticks) {
        this.entityData.set(LIFETIME, ticks);
    }

    public boolean isFading() {
        return this.entityData.get(FADING);
    }

    public void setFading(boolean fading) {
        this.entityData.set(FADING, fading);
    }

    public float getFadeIn(float partialTicks) {
        return (this.prevFadeIn + (this.fadeIn - this.prevFadeIn) * partialTicks) / MAX_FADE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setOwnerEntityId(tag.getInt("OwnerEntityId"));
        this.setRotateOffset(tag.getFloat("RotateOffset"));
        this.setLifetime(tag.getInt("Lifetime"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            tag.putUUID("OwnerUUID", uuid);
        }
        tag.putInt("OwnerEntityId", this.getOwnerEntityId());
        tag.putFloat("RotateOffset", this.getRotateOffset());
        tag.putInt("Lifetime", this.getLifetime());
    }
}
