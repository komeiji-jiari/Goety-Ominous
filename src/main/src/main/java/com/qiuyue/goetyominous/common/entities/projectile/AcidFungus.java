package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.FungusPackHelper;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AcidFungus extends ThrowableItemProjectile implements ItemSupplier {

    public AcidFungus(EntityType<? extends AcidFungus> type, Level level) {
        super(type, level);
    }

    public AcidFungus(Level level, LivingEntity shooter) {
        super(ModEntityTypes.ACID_FUNGUS.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ACID_FUNGUS.get();
    }

    @Override
    protected float getGravity() {
        return 0.05F;
    }

    private float damage = 4.0F;

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_HEAL =
            net.minecraft.network.syncher.SynchedEntityData.defineId(AcidFungus.class,
                    net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HEAL, false);
    }

    public void setHeal(boolean heal) {
        this.entityData.set(DATA_HEAL, heal);
    }

    public boolean isHeal() {
        return this.entityData.get(DATA_HEAL);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (this.isHeal()) {
            return target instanceof LivingEntity living && this.isHealTarget(living);
        }
        return super.canHitEntity(target);
    }

    private boolean isHealTarget(LivingEntity living) {
        if (living == this.getOwner()) return false;
        if (living.getHealth() >= living.getMaxHealth()) return false;
        Entity owner = this.getOwner();
        return owner == null || com.Polarice3.Goety.utils.MobUtil.areAllies(owner, living);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || this.isRemoved() || !this.isHeal()) return;
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.5D), this::isHealTarget)) {
            this.onHit(new EntityHitResult(living));
            break;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) return;
        if (this.isHeal()) return;
        if (result.getEntity() instanceof LivingEntity living) {
            living.hurt(this.damageSources().thrown(this, this.getOwner()), this.damage);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                com.Polarice3.Goety.init.ModSounds.BLAST_FUNGUS_EXPLODE.get(),
                net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);

        Vec3 hitPos = result.getLocation();
        if (result instanceof EntityHitResult entityHit) {
            hitPos = entityHit.getEntity().position();
        }

        AcidPool pool;
        if (this.isHeal()) {
            pool = new HealFungusPool(ModEntityTypes.HEAL_FUNGUS_POOL.get(), this.level());
            pool.setColor(0xFF3B30);
            pool.setWarmupColor(0xFF3B30);
            pool.setRadius(3.0F);
            pool.setDamage(this.damage);
            pool.setDuration(80);
        } else {
            pool = new AcidPool(ModEntityType.ACID_POOL.get(), this.level());
            pool.setColor(0x87CEEB);
            pool.setWarmupColor(0x87CEEB);
            pool.setRadius(3.0F);
            pool.setDamage(this.damage);
            int duration = 80;
            if (this.getOwner() instanceof LivingEntity owner
                    && FungusPackHelper.hasMatchingFungus(owner, ModItems.ACID_FUNGUS.get())) {
                duration = 120;
            }
            pool.setDuration(duration);
        }

        pool.setPos(hitPos);
        if (this.getOwner() instanceof LivingEntity owner) {
            pool.setOwner(owner);
        }
        this.level().addFreshEntity(pool);

        this.discard();
    }
}
