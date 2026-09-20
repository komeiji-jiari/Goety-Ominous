package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.config.MobsConfig;
import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CorrodentServantTailEntity extends ACMultipartEntity<CorrodentServant> {

    private EntityDimensions size;
    public float scale = 1;

    public CorrodentServantTailEntity(CorrodentServant parent) {
        super(parent);
        this.size = EntityDimensions.fixed(0.9F, 0.9F);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0D, 1.0D, 1.0D);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        CorrodentServant parent = this.getParent();
        if (parent != null && MobsConfig.OwnerAttackCancel.get()
                && parent.getTrueOwner() instanceof Player owner && source.getEntity() == owner) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    public void setToTransformation(Vec3 offset, float xRot, float yRot) {
        Vec3 transformed = offset.xRot((float) (-xRot * (Math.PI / 180F))).yRot((float) (-yRot * (Math.PI / 180F)));
        Vec3 offseted = transformed.add(getParent().position().add(0, getParent().getBbHeight() * 0.5F, 0));
        this.setPos(offseted.x, offseted.y - this.getBbHeight() * 0.5F, offseted.z);
    }
}
