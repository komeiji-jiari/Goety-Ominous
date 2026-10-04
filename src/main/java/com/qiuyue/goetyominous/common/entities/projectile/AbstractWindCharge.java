package com.qiuyue.goetyominous.common.entities.projectile;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.SimpleExplosionDamageCalculator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {
    public static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR =
            new SimpleExplosionDamageCalculator(true, false, Optional.empty(),
                    BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                            .map(holders -> (HolderSet<Block>) holders));
    public static final double JUMP_SCALE = 0.25D;
    public static final ResourceKey<DamageType> WIND_CHARGE_DAMAGE_TYPE =
            ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GoetyOminous.MOD_ID, "wind_charge"));

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, Level level, Entity owner,
                              double x, double y, double z) {
        super(type, level);
        this.setPos(x, y, z);
        this.setOwner(owner);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.0F;
    }

    protected AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, double x, double y, double z,
                                 Vec3 movement, Level level) {
        super(type, level);
        this.setPos(x, y, z);
        this.setDeltaMovement(movement);
    }

    @Override
    public AABB makeBoundingBox() {
        float width = this.getType().getDimensions().width / 2.0F;
        float height = this.getType().getDimensions().height;
        return new AABB(this.getX() - (double) width, this.getY() - 0.15D, this.getZ() - (double) width,
                this.getX() + (double) width, this.getY() - 0.15D + (double) height, this.getZ() + (double) width);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof AbstractWindCharge) && super.canCollideWith(entity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return !(entity instanceof AbstractWindCharge)
                && entity.getType() != EntityType.END_CRYSTAL
                && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) return;
        Entity hitEntity = result.getEntity();
        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        if (owner != null) owner.setLastHurtMob(hitEntity);
        DamageSource source = windChargeDamage(this.level(), this, owner);
        hitEntity.hurt(source, 1.0F);
        this.explode(this.position());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal()).multiply(0.25D, 0.25D, 0.25D);
            this.explode(result.getLocation().add(normal));
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
            this.explode(this.position());
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    public void push(double x, double y, double z) {
    }

    public boolean shouldAffectEntity(Entity entity) {
        return true;
    }

    private boolean grantsFallDamageImmunity;

    public boolean grantsFallDamageImmunity() {
        return this.grantsFallDamageImmunity;
    }

    public void setGrantsFallDamageImmunity(boolean grants) {
        this.grantsFallDamageImmunity = grants;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("GrantsFallDamageImmunity", this.grantsFallDamageImmunity);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("GrantsFallDamageImmunity")) {
            this.grantsFallDamageImmunity = tag.getBoolean("GrantsFallDamageImmunity");
        }
    }

    protected abstract void explode(Vec3 pos);

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public static DamageSource windChargeDamage(Level level, Entity directEntity, @Nullable Entity causingEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(WIND_CHARGE_DAMAGE_TYPE), directEntity, causingEntity);
    }
}
