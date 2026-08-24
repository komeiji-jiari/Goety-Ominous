package com.qiuyue.goetyominous.common.entities.ally.ac;

import net.minecraft.nbt.CompoundTag;
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

public class HullbreakerServantPartEntity extends PartEntity<HullbreakerServant> {

    private final Entity connectedTo;
    private final EntityDimensions size;

    public HullbreakerServantPartEntity(HullbreakerServant parent, Entity connectedTo, float sizeXZ, float sizeY) {
        super(parent);
        this.blocksBuilding = true;
        this.connectedTo = connectedTo;
        this.size = EntityDimensions.fixed(sizeXZ, sizeY);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        HullbreakerServant parent = this.getParent();
        if (parent == null) {
            return InteractionResult.PASS;
        } else {
            this.playSound(SoundEvents.ITEM_BREAK);
            return parent.interact(player, hand);
        }
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        HullbreakerServant parent = this.getParent();
        return parent != null && parent.canBeCollidedWith();
    }

    @Override
    public boolean isPickable() {
        HullbreakerServant parent = this.getParent();
        return parent != null && parent.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        HullbreakerServant parent = this.getParent();
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.35F;
        }
        return !this.isInvulnerableTo(source) && !this.level().isClientSide && parent != null && parent.hurt(source, amount);
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
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
        return this.getBoundingBox().inflate(2.0F, 0.5F, 2.0F);
    }

    public float calculateAnimationAngle(float partialTicks, boolean pitch) {
        HullbreakerServant parent = this.getParent();
        float parentRot = parent == null ? 0.0F : parent.yBodyRotO + (parent.yBodyRot - parent.yBodyRotO) * partialTicks;
        Vec3 connection = this.connectedTo.getPosition(partialTicks).add(0.0D, this.connectedTo.getBbHeight() * 0.5F, 0.0D);
        Vec3 center = this.centeredPosition(partialTicks);
        Vec3 offset = connection.subtract(center).normalize();
        Vec3 back = center.add(offset.scale(-1.0F * this.getBbWidth()));
        double d0 = connection.x - back.x;
        double d1 = connection.y - back.y;
        double d2 = connection.z - back.z;
        if (pitch) {
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            return Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 180.0F / (float) Math.PI))) * 0.35F;
        } else {
            return (float) (Mth.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F - parentRot;
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public void setPosCenteredY(Vec3 pos) {
        this.setPos(pos.x, pos.y - this.getBbHeight() * 0.5F, pos.z);
    }

    public Vec3 centeredPosition() {
        return this.position().add(0.0D, this.getBbHeight() * 0.5F, 0.0D);
    }

    public Vec3 centeredPosition(float partialTicks) {
        return this.getPosition(partialTicks).add(0.0D, this.getBbHeight() * 0.5F, 0.0D);
    }
}
