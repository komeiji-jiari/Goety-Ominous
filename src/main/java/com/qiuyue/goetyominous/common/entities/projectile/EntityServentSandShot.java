package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;

public class EntityServentSandShot extends SpellThrowableProjectile {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityServentSandShot.class, EntityDataSerializers.INT);
    private boolean leftOwner;

    public EntityServentSandShot(EntityType<? extends SpellThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public EntityServentSandShot(Level worldIn, LivingEntity shooter, boolean right) {
        this(AmEntityRegistry.SERVANT_SAND_SHOT.get(), worldIn);
        this.setOwner(shooter);
        float rot = shooter.yHeadRot + (right ? 60 : -60);
        this.setPos(shooter.getX() - (double) shooter.getBbWidth() * 0.5D * (double) Mth.sin(rot * Mth.DEG_TO_RAD), shooter.getEyeY() - 0.2F, shooter.getZ() + (double) shooter.getBbWidth() * 0.5D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
        this.setExtraDamage(2.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public EntityServentSandShot(Level worldIn, double x, double y, double z, double dx, double dy, double dz) {
        this(AmEntityRegistry.SERVANT_SAND_SHOT.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(dx, dy, dz);
    }

    public EntityServentSandShot(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AmEntityRegistry.SERVANT_SAND_SHOT.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        ParticleOptions type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SHOT_SOUL.get() : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SHOT_RED.get() : AMParticleRegistry.GUSTER_SAND_SHOT.get();
        for (int i = 0; i < 3 + this.random.nextInt(6); ++i) {
            double d0 = 0.1D + 0.3D * (double) i;
            this.level().addParticle(type, this.getX() + 0.25F * (this.random.nextFloat() - 0.5F), this.getY() + 0.25F * (this.random.nextFloat() - 0.5F), this.getZ() + 0.25F * (this.random.nextFloat() - 0.5F), this.getDeltaMovement().x * d0, this.getDeltaMovement().y, this.getDeltaMovement().z * d0);
        }
        super.tick();
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.discard();
        } else if (this.isInWaterOrBubble()) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            hitEntity.hurt(this.damageSources().mobProjectile(this, owner), this.getExtraDamage());
        }
        if (owner instanceof Player && hitEntity instanceof LivingEntity) {
            ((LivingEntity) hitEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, false));
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable()) {
            if (entity instanceof ItemEntity) {
                return false;
            }
            Entity owner = this.getOwner();
            if (owner != null) {
                if (MobUtil.areAllies(owner, entity)) {
                    return false;
                }
                if (entity instanceof IOwned owned0 && owner instanceof IOwned owned1 && MobUtil.ownerStack(owned0, owned1)) {
                    return false;
                }
                return this.leftOwner || !owner.isPassengerOfSameVehicle(entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity owner = this.getOwner();
        if (owner != null) {
            for (Entity entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), entityIn -> !entityIn.isSpectator() && entityIn.isPickable())) {
                if (entity.getRootVehicle() == owner.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getColorForVariant(int variant) {
        if (variant == 2) {
            return 0X4E3D33;
        } else if (variant == 1) {
            return 0XC66127;
        } else {
            return 0XF3C389;
        }
    }
}
