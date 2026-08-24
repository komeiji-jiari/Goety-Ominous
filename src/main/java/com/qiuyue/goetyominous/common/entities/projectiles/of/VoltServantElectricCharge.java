package com.qiuyue.goetyominous.common.entities.projectiles.of;

import com.unusualmodding.opposing_force.entity.projectile.ElectricCharge;
import com.unusualmodding.opposing_force.entity.projectile.FrictionlessProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 伏特仆从发射的电球：复刻 OF 原版 ElectricCharge，唯一差别是爆炸换成
 * {@link VoltServantElectricExplosion}（带友军过滤，不误伤主人和其他仆从）。
 * 直击本来就不打伤害（Projectile.onHitEntity 是空方法），伤害全在爆炸的 AoE 里。
 */
public class VoltServantElectricCharge extends ElectricCharge {

    public VoltServantElectricCharge(EntityType<? extends FrictionlessProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void createExplosion(float f) {
        if (!this.level().isClientSide) {
            // 粒子 + 爆炸：和原版一模一样的节奏，只有爆炸实体换成我们的子类
            this.spawnElectricParticles(this, (int) (f + 1.0F + this.random.nextInt((int) f + 1)), 0.25F, 16.0F);
            VoltServantElectricExplosion explosion = new VoltServantElectricExplosion(
                    this.level(), this, this.getX(), this.getY() + 0.0625D, this.getZ(), f);
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }
}
