package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;

public class EntityServantHemolymph extends SpellThrowableProjectile {
    private boolean leftOwner;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EntityServantHemolymph(EntityType type, Level level) {
        super(type, level);
    }

    public EntityServantHemolymph(Level worldIn, WarpedMoscoServant shooter) {
        this(AmEntityRegistry.SERVANT_HEMOLYMPH.get(), worldIn);
        this.setOwner(shooter);
        this.setExtraDamage(AttributesConfig.WarpedMoscoServantDamage.get().floatValue());
        this.setPos(shooter.getX() - (double) (shooter.getBbWidth() + 1.0F) * 0.35D * (double) Mth.sin(shooter.yBodyRot * Mth.DEG_TO_RAD), shooter.getEyeY() + (double) 0.2F, shooter.getZ() + (double) (shooter.getBbWidth() + 1.0F) * 0.35D * (double) Mth.cos(shooter.yBodyRot * Mth.DEG_TO_RAD));
    }

    public EntityServantHemolymph(Level worldIn, LivingEntity shooter, boolean right) {
        this(AmEntityRegistry.SERVANT_HEMOLYMPH.get(), worldIn);
        this.setOwner(shooter);
        this.setExtraDamage(AttributesConfig.WarpedMoscoServantDamage.get().floatValue());
        float rot = shooter.yHeadRot + (right ? 60 : -60);
        this.setPos(shooter.getX() - (double) (shooter.getBbWidth()) * 0.5D * (double) Mth.sin(rot * Mth.DEG_TO_RAD), shooter.getEyeY() - (double) 0.2F, shooter.getZ() + (double) (shooter.getBbWidth()) * 0.5D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
    }

    @OnlyIn(Dist.CLIENT)
    public EntityServantHemolymph(Level worldIn, double x, double y, double z, double dx, double dy, double dz) {
        this(AmEntityRegistry.SERVANT_HEMOLYMPH.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(dx, dy, dz);
    }

    public EntityServantHemolymph(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AmEntityRegistry.SERVANT_HEMOLYMPH.get(), world);
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        if (this.level().isClientSide) {
            float r1 = (random.nextFloat() - 0.5F) * 0.5F;
            float r2 = (random.nextFloat() - 0.5F) * 0.5F;
            float r3 = (random.nextFloat() - 0.5F) * 0.5F;
            this.level().addParticle(AMParticleRegistry.HEMOLYMPH.get(), this.getX() + r1, this.getY() + r2, this.getZ() + r3, r1 * 0.1F, r2 * 0.1F, r3 * 0.1F);
        }
        super.tick();
                if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            LivingEntity living = this.getOwner();
            if (living != null) {
                result.getEntity().hurt(this.damageSources().mobProjectile(this, living), this.getExtraDamage());
            }
                        this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.isSpectator() && target.isAlive() && target.isPickable()) {
            Entity entity = this.getOwner();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(target);
        } else {
            return false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for (Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }
}
