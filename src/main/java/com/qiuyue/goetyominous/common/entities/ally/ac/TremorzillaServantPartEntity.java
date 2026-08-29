package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.MultipartEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

/**
 * 特雷莫兹拉仆从的多段尾巴 part 实体。
 * 移植自 AC 原版 TremorzillaPartEntity,父实体改为 TremorzillaServant。
 * 攻击消息复用 AC 的 MultipartEntityMessage —— 其 handler 对任意 isMultipartEntity() 实体通用。
 */
public class TremorzillaServantPartEntity extends PartEntity<TremorzillaServant> {
    private final Entity connectedTo;
    private EntityDimensions size;
    public float scale = 1.0F;

    public TremorzillaServantPartEntity(TremorzillaServant parent, Entity connectedTo, float sizeXZ, float sizeY) {
        super(parent);
        this.blocksBuilding = true;
        this.connectedTo = connectedTo;
        this.size = EntityDimensions.scalable(sizeXZ, sizeY);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        TremorzillaServant parent = this.getParent();
        return parent == null ? this.size : this.size.scale(parent.getScale());
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        TremorzillaServant parent = this.getParent();
        if (parent == null) {
            return InteractionResult.PASS;
        }
        this.playSound(SoundEvents.ITEM_BREAK);
        if (player.level().isClientSide) {
            AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), player.getId(), 0, 0.0));
        }
        return parent.interact(player, hand);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(ACDamageTypes.ACID) || damageSource.getEntity() != null && this.getParent().isPassengerOfSameVehicle(damageSource.getEntity());
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        TremorzillaServant parent = this.getParent();
        return parent != null && parent.canBeCollidedWith();
    }

    @Override
    public boolean isPickable() {
        TremorzillaServant parent = this.getParent();
        return parent != null && parent.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        TremorzillaServant parent = this.getParent();
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.35F;
        }
        if (!this.isInvulnerableTo(source) && parent != null && source.getEntity() != null && !parent.isAlliedTo(source.getEntity()) && source.getEntity().level().isClientSide) {
            AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), source.getEntity().getId(), 1, amount));
        }
        return false;
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2.0, 0.5, 2.0);
    }

    public float calculateAnimationAngle(float partialTicks, boolean pitch) {
        TremorzillaServant parent = this.getParent();
        float parentRot = 0.0F;
        Vec3 connection = this.connectedTo.getPosition(partialTicks).add(0.0, this.connectedTo.getBbHeight() * 0.5F, 0.0);
        if (this.connectedTo == parent && parent != null) {
            connection = connection.add(0.0, parent.isTremorzillaSwimming() ? 0.0 : -4.0F * parent.getScale() - parent.getLegSolverBodyOffset(), 0.0);
        }
        if (parent != null) {
            parentRot = -(parent.yBodyRotO + (parent.yBodyRot - parent.yBodyRotO) * partialTicks) - 90.0F;
        }
        Vec3 center = this.centeredPosition(partialTicks);
        Vec3 offset = connection.subtract(center).normalize();
        Vec3 back = center.add(offset.scale(-1.0F * this.getBbWidth()));
        double d0 = connection.x - back.x;
        double d1 = connection.y - back.y;
        double d2 = connection.z - back.z;
        if (pitch) {
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            return Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 180.0 / Math.PI)));
        }
        return (float) (Mth.atan2(d2, d0) * 57.2957763671875) + parentRot;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public void setPosCenteredY(Vec3 pos) {
        this.setPos(pos.x, pos.y - this.getBbHeight() * 0.5F, pos.z);
    }

    public Vec3 centeredPosition() {
        return this.position().add(0.0, this.getBbHeight() * 0.5F, 0.0);
    }

    public Vec3 centeredPosition(float partialTicks) {
        return this.getPosition(partialTicks).add(0.0, this.getBbHeight() * 0.5F, 0.0);
    }
}
