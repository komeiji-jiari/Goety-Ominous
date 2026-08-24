package com.qiuyue.goetyominous.common.entities.projectiles.of;

import com.Polarice3.Goety.utils.MobUtil;
import com.unusualmodding.opposing_force.entity.projectile.DicerLaser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Dicer 仆从的激光，直接继承 OF 原版的 DicerLaser。
 * <p>
 * 好处：父类的便捷构造器 {@code (Level, LivingEntity, ...)} 内部用的是
 * {@code OPEntities.DICER_LASER} 这个实体类型，所以渲染器自动是 OF 的
 * DicerLaserRenderer，外观/音效/粒子 100% 是敌对版 Dicer 的激光，
 * 不需要再走 goety 的 CorruptedBeam 那一套。
 * <p>
 * 唯一改动：重写 raytraceEntities，在收集命中实体时把"友军"过滤掉。
 * 原版 OF 激光伤害循环里用的是 Minecraft 的 {@code isAlliedTo}（队伍系统），
 * 对 goety 仆从不生效，会连主人/其他仆从一起烧；这里改用 Goety 的
 * {@code MobUtil.areAllies} 做阵营判定，从源头排除友军。
 */
public class DicerServantLaser extends DicerLaser {

    /**
     * 和父类便捷构造器签名一致：指定施法者、起点坐标、朝向和伤害。
     * 父类内部会用 OPEntities.DICER_LASER 作为实体类型。
     */
    public DicerServantLaser(Level level, LivingEntity caster, double x, double y, double z,
                             float yaw, float pitch, int duration, int damage) {
        super(level, caster, x, y, z, yaw, pitch, duration, damage);
    }

    /**
     * 复刻父类 raytraceEntities 的全部逻辑（方块遮挡、AABB 判定、命中收集），
     * 只额外插入一行：友军用 MobUtil.areAllies 直接跳过。
     */
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
            // ★ 关键改动：友军（主人、其他仆从、中立阵营）不进入命中列表
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

    /**
     * 原版的 updateWithDicer 是 private 方法，且只在 caster 是 OF 的 Dicer 时被调用；
     * 我们的施法者是 DicerServant，不会触发，所以这里手动让激光始终贴着施法者胸口射出，
     * 避免施法者被击退/移动时激光飘在原处。
     */
    @Override
    public void tick() {
        super.tick();
        if (this.caster != null && this.caster.isAlive() && !this.isRemoved()) {
            this.setPos(this.caster.getX(), this.caster.getY() + 2.45, this.caster.getZ());
        }
    }
}
