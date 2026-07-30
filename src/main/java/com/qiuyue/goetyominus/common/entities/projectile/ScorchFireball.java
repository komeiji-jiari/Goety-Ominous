package com.qiuyue.goetyominus.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ScorchFireball extends ModFireball {
    public ScorchFireball(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(level, shooter, dx, dy, dz);
        this.getEntityData().set(DATA_DANGEROUS, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target.isInvulnerableTo(this.damageSources().fireball(this, this.getOwner()))) {
            if (target instanceof LivingEntity living) {
                float halfDamage = this.getDamage() * 0.5F;
                DamageSource source = ModDamageSource.magicFireball(this, this.getOwner(), this.level());
                living.hurt(source, halfDamage);
                int fiery = this.getFiery();
                if (fiery > 0) {
                    living.setSecondsOnFire(fiery * 5);
                }
            }
        } else {
            super.onHitEntity(result);
        }
    }
}
