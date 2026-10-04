package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.SimpleExplosionDamageCalculator;
import com.qiuyue.goetyominous.utils.WindChargeExplosion;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ServantWindCharge extends AbstractWindCharge {
    private float radius = 3.0F;
    private float damage = 1.0F;
    private float knockbackMultiplier = 1.0F;

    public ServantWindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public ServantWindCharge(Level level, LivingEntity owner, double x, double y, double z,
                             float damage, float knockbackMultiplier) {
        super(ModEntityTypes.SERVANT_WIND_CHARGE.get(), level, owner, x, y, z);
        this.damage = damage;
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getKnockbackMultiplier() {
        return this.knockbackMultiplier;
    }

    public void setKnockbackMultiplier(float knockbackMultiplier) {
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) return false;
        if (this.getOwner() instanceof Owned owned) {
            return entity != owned.getTrueOwner() && !owned.isAlliedTo(entity);
        }
        return true;
    }

    @Override
    public boolean shouldAffectEntity(Entity entity) {
        if (this.getOwner() instanceof Owned owned) {
            return entity != owned.getTrueOwner() && !owned.isAlliedTo(entity);
        }
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            Entity hitEntity = result.getEntity();
            LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
            if (owner != null) owner.setLastHurtMob(hitEntity);
            DamageSource source = windChargeDamage(this.level(), this, owner);
            hitEntity.hurt(source, this.damage);
            this.explode(this.position());
        }
    }

    @Override
    protected void explode(Vec3 pos) {
        ExplosionDamageCalculator calculator = this.knockbackMultiplier == 1.0F
                ? EXPLOSION_DAMAGE_CALCULATOR
                : new SimpleExplosionDamageCalculator(true, false, Optional.of(this.knockbackMultiplier),
                BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                        .map(holders -> (HolderSet<Block>) holders));
        if (!this.level().isClientSide) {
            WindChargeExplosion explosion = new WindChargeExplosion(this.level(), this,
                    windChargeDamage(this.level(), this, null), calculator, pos.x, pos.y, pos.z, this.radius,
                    ModParticleTypes.GUST_EMITTER_SMALL.get(), ModParticleTypes.GUST_EMITTER_LARGE.get(),
                    ModSounds.BREEZE_WIND_CHARGE_BURST.get());
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putFloat("Knockback", this.knockbackMultiplier);
        tag.putFloat("Radius", this.radius);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("Knockback")) this.knockbackMultiplier = tag.getFloat("Knockback");
        if (tag.contains("Radius")) this.radius = tag.getFloat("Radius");
    }
}
