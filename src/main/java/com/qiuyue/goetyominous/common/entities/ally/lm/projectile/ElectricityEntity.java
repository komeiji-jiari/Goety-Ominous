package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class ElectricityEntity extends Entity {
    public double xPower;
    public double yPower;
    public double zPower;
    private LivingEntity caster;
    private UUID casterUuid;
    private boolean leftOwner;

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(ElectricityEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TRANSPARENCY =
            SynchedEntityData.defineId(ElectricityEntity.class, EntityDataSerializers.INT);

    public int maxLife = 20;
    public int lifetick = 0;

    public ElectricityEntity(EntityType<? extends ElectricityEntity> type, Level level) {
        super(type, level);
    }

    public ElectricityEntity(EntityType<? extends ElectricityEntity> type, double x, double y, double z,
                             double dx, double dy, double dz, Level level, float yRot) {
        this(type, level);
        this.setPosRaw(x, y, z);
        double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d0 != 0.0) {
            this.xPower = dx / d0 * 0.1;
            this.yPower = dy / d0 * 0.1;
            this.zPower = dz / d0 * 0.1;
        }
    }

    public ElectricityEntity(LivingEntity owner, double dx, double dy, double dz, Level level,
                            float damage, float yRot, float life) {
        this(LmEntityRegistry.ELECTRIC_BURST.get(), owner.getX(), owner.getY(), owner.getZ(), dx, dy, dz, level, yRot);
        this.setOwner(owner);
        this.setDamage(damage);
        this.setYRot(yRot);
        this.maxLife = (int) life;
    }

    public ElectricityEntity(EntityType<? extends ElectricityEntity> type, LivingEntity owner, double x, double y, double z,
                             double dx, double dy, double dz, float damage, Level level) {
        this(type, level);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.setOwner(owner);
        this.setDamage(damage);
        this.reapplyPosition();
        double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d0 != 0.0) {
            this.xPower = dx / d0 * 0.5;
            this.yPower = dy / d0 * 0.5;
            this.zPower = dz / d0 * 0.5;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(TRANSPARENCY, 0);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.caster = owner;
        this.casterUuid = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUuid);
            if (entity instanceof LivingEntity living) {
                this.caster = living;
            }
        }
        return this.caster;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getTransparency() {
        return this.entityData.get(TRANSPARENCY);
    }

    public void setTransparency(int transparency) {
        this.entityData.set(TRANSPARENCY, transparency);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }
        d0 *= 64.0;
        return distance < d0 * d0;
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity owner = this.getOwner();
        if (!impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == owner || this.tickCount % 5 != 0) {
            return;
        }
        DamageSource damageSource = new DamageSource(
                this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC), owner);
        if (owner == null) {
            impactEntity.hurt(damageSource, this.getDamage());
            return;
        }
        if (MobUtil.areAllies(owner, impactEntity)) {
            return;
        }
        if (impactEntity instanceof TamableAnimal animal && animal.getOwner() == owner) {
            return;
        }
        impactEntity.hurt(damageSource, this.getDamage());
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.5, 4.0, 0.75))) {
            this.damage(livingentity);
        }
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            double theta = this.getYRot() * (Math.PI / 180);
            double vecX = Math.cos(theta + 1.5707963267948966);
            double vecZ = Math.sin(theta + 1.5707963267948966);
            int numberOfSkulls = 1;
            float angleStep = 30.0F;
            for (int i = 0; i < numberOfSkulls; ++i) {
                float angle = this.getYRot() + (float) (i - numberOfSkulls / 2) * angleStep;
                float rad = (float) Math.toRadians(angle);
                double spawnX = this.getX() + vecX * 1.0;
                double spawnY = this.getY() + 2.0;
                double spawnZ = this.getZ() + vecZ * 1.0;
                this.level().addAlwaysVisibleParticle(ModParticles.LB.get(), spawnX, spawnY, spawnZ, 0.0, 0.0, 0.0);
            }
        }
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        if (!this.level().isClientSide) {
            ++this.lifetick;
            this.setTransparency(this.lifetick);
            if (this.lifetick >= this.maxLife) {
                this.discard();
            }
        }
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }
        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        float f = this.getInertia();
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.setPos(d0, d1, d2);
    }

    protected void onHitEntity(EntityHitResult result) {
    }

    protected void onHitBlock(BlockHitResult result) {
    }

    protected void onHit(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(),
                    GameEvent.Context.of(this, (BlockState) null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult) result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos,
                    GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
    }

    protected boolean canHitEntity(Entity entity) {
        return this.canHit(entity) && !entity.noPhysics;
    }

    protected boolean canHit(Entity entity) {
        if (!entity.canBeHitByProjectile()) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        return owner == null || this.leftOwner || !owner.isPassengerOfSameVehicle(entity);
    }

    protected float getInertia() {
        return 0.85F;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
        if (compound.contains("power", 9)) {
            ListTag listtag = compound.getList("power", 6);
            if (listtag.size() == 3) {
                this.xPower = listtag.getDouble(0);
                this.yPower = listtag.getDouble(1);
                this.zPower = listtag.getDouble(2);
            }
        }
        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
        compound.put("power", this.newDoubleList(this.xPower, this.yPower, this.zPower));
    }

    private boolean checkLeftOwner() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            for (Entity entity : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0),
                    e -> !e.isSpectator() && e.isPickable())) {
                if (entity.getRootVehicle() != owner.getRootVehicle()) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity owner = this.getOwner();
        int i = owner == null ? 0 : owner.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(),
                this.getXRot(), this.getYRot(), this.getType(), i,
                new Vec3(this.xPower, this.yPower, this.zPower), 0.0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
    }
}
