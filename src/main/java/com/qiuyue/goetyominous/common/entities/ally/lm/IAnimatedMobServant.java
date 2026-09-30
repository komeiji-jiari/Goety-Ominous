package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IAnimatedMobServant extends IAnimatedMonsterServant {
    public double lastTargetX = this.getX();
    public double lastTargetY = this.getY();
    public double lastTargetZ;

    public IAnimatedMobServant(EntityType entity, Level world) {
        super(entity, world);
        this.lastTargetZ = this.getZ();
    }

    public LivingEntity target() {
        return getTarget();
    }

    public void saveTargetPos(double x, double y, double z) {
        if (this.targetIsNotNull()) {
            this.lastTargetX = x;
            this.lastTargetY = y;
            this.lastTargetZ = z;
        }
    }

    public Vec3 lastTargetPos() {
        return new Vec3(this.lastTargetX, this.lastTargetY, this.lastTargetZ);
    }

    public void advancedDash(LivingEntity livingEntity, float vec, float offset, float Vscale) {
        float f = Mth.cos(livingEntity.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(livingEntity.yBodyRot * ((float) Math.PI / 180F));
        double theta = (livingEntity.yBodyRot) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        Vec3 rollPos = new Vec3(livingEntity.getX() + vec * vecX + f * offset, getY(), livingEntity.getZ() + vec * vecZ + f1 * offset);
        Vec3 sub = position().subtract(rollPos);
        Vec3 finalPos = sub.scale(Vscale);
        setDeltaMovement(finalPos.x, getDeltaMovement().y, finalPos.z);
    }

    public void calculatedDashToPositon(float Multiplier, Vec3 position) {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.setDeltaMovement((position.x - this.getX()) * Multiplier, 0, (position.z - this.getZ()) * Multiplier);
        }
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        ItemEntity itementity = this.spawnAtLocation(stack, 0.0f);
        if (itementity != null) {
            itementity.setGlowingTag(true);
            itementity.setExtendedLifetime();
        }
        return itementity;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.FALL)) return false;
        if (pSource.is(DamageTypes.IN_WALL)) return false;
        return super.hurt(pSource, pAmount);

    }
}
