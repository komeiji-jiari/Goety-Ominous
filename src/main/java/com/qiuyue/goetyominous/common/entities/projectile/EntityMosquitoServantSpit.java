package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
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

public class EntityMosquitoServantSpit extends SpellThrowableProjectile {

    public EntityMosquitoServantSpit(EntityType<? extends SpellThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public EntityMosquitoServantSpit(Level worldIn, LivingEntity shooter) {
        this(AmEntityRegistry.MOSQUITO_SERVANT_SPIT.get(), worldIn);
        this.setOwner(shooter);
        this.setPos(shooter.getX() - (double) (shooter.getBbWidth() + 1.0F) * 0.35D * (double) Mth.sin(shooter.yBodyRot * Mth.DEG_TO_RAD), shooter.getEyeY() + (double) 0.2F, shooter.getZ() + (double) (shooter.getBbWidth() + 1.0F) * 0.35D * (double) Mth.cos(shooter.yBodyRot * Mth.DEG_TO_RAD));
        this.setExtraDamage(AttributesConfig.CrimsonMosquitoServantDamage.get().floatValue());
    }

    public EntityMosquitoServantSpit(Level worldIn, LivingEntity shooter, boolean right) {
        this(AmEntityRegistry.MOSQUITO_SERVANT_SPIT.get(), worldIn);
        this.setOwner(shooter);
        float rot = shooter.yHeadRot + (right ? 60 : -60);
        this.setPos(shooter.getX() - (double) (shooter.getBbWidth()) * 0.5D * (double) Mth.sin(rot * Mth.DEG_TO_RAD), shooter.getEyeY() - (double) 0.2F, shooter.getZ() + (double) (shooter.getBbWidth()) * 0.5D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
        this.setExtraDamage(AttributesConfig.CrimsonMosquitoServantDamage.get().floatValue());
    }

    @OnlyIn(Dist.CLIENT)
    public EntityMosquitoServantSpit(Level worldIn, double x, double y, double z, double dx, double dy, double dz) {
        this(AmEntityRegistry.MOSQUITO_SERVANT_SPIT.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(dx, dy, dz);
    }

    public EntityMosquitoServantSpit(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AmEntityRegistry.MOSQUITO_SERVANT_SPIT.get(), world);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        if (!this.level().isClientSide) {
            LivingEntity living = this.getOwner();
            if (living != null) {
                hitEntity.hurt(this.damageSources().mobProjectile(this, living), this.getExtraDamage());
            }
            this.discard();
        }
        if (hitEntity instanceof EntityCrimsonMosquito && !this.level().isClientSide) {
            EntityCrimsonMosquito mosquito = (EntityCrimsonMosquito) hitEntity;
            mosquito.setBloodLevel(mosquito.getBloodLevel() + 1);
        }
        if (hitEntity instanceof CrimsonMosquitoServant && !this.level().isClientSide) {
            CrimsonMosquitoServant mosquito = (CrimsonMosquitoServant) hitEntity;
            mosquito.setBloodLevel(mosquito.getBloodLevel() + 1);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}
