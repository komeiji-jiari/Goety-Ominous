package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.unusualmodding.opposing_force.entity.projectile.DicerLaser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class DicerServantLaser extends DicerLaser {

    public DicerServantLaser(Level level, LivingEntity caster, double x, double y, double z,
                             float yaw, float pitch, int duration, int damage) {
        super(level, caster, x, y, z, yaw, pitch, duration, damage);
    }

    @Override
    public LaserHitResult raytraceEntities(Level level, Vec3 start, Vec3 end,
                                           boolean ignoreBlocks, boolean ignoreEntities, boolean requireNonAir) {
        LaserHitResult result = new LaserHitResult();
        result.setBlockHit(level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.getBlockHit() != null) {
            Vec3 location = result.getBlockHit().getLocation();
            this.collidePosX = location.x;
            this.collidePosY = location.y;
            this.collidePosZ = location.z;
            this.blockSide = result.getBlockHit().getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.blockSide = null;
        }

        AABB aabb = new AABB(
                Math.min(this.getX(), this.collidePosX),
                Math.min(this.getY(), this.collidePosY),
                Math.min(this.getZ(), this.collidePosZ),
                Math.max(this.getX(), this.collidePosX),
                Math.max(this.getY(), this.collidePosY),
                Math.max(this.getZ(), this.collidePosZ)
        ).inflate(1.0);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity entity : entities) {
            if (entity == this.caster) {
                continue;
            }
            if (this.caster != null && MobUtil.areAllies(this.caster, entity)) {
                continue;
            }

            float f = entity.getPickRadius() + 0.1F;
            AABB box = entity.getBoundingBox().inflate(f, f, f);
            Optional<Vec3> clip = box.clip(start, end);
            if (box.contains(start) || clip.isPresent()) {
                result.addEntityHit(entity);
            }
        }
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.caster != null && this.caster.isAlive() && !this.isRemoved()) {
            this.setPos(this.caster.getX(), this.caster.getY() + 2.45, this.caster.getZ());
        }
    }
}
