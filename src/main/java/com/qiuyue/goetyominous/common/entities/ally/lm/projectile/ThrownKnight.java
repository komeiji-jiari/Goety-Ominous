package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ThrownKnight extends SpellThrowableProjectile {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(ThrownKnight.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ENHANCED =
            SynchedEntityData.defineId(ThrownKnight.class, EntityDataSerializers.BOOLEAN);

    public ThrownKnight(EntityType<? extends ThrownKnight> type, Level level) {
        super(type, level);
    }

    public ThrownKnight(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ,
                        float damage, ResurrectedKnightServant passenger, boolean enhanced) {
        super(LmEntityRegistry.THROWN_KNIGHT.get(), level);
        this.setDamage(damage);
        this.setEnhanced(enhanced);
        this.setOwner(shooter);
        this.moveTo(offsetX, offsetY, offsetZ);
        if (passenger != null) {
            passenger.startRiding(this, true);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(ENHANCED, false);
    }

    public boolean isEnhanced() {
        return this.entityData.get(ENHANCED);
    }

    public void setEnhanced(boolean enhanced) {
        this.entityData.set(ENHANCED, enhanced);
    }

    private float ringRed() {
        return this.isEnhanced() ? 1.0F : 0.25F;
    }

    private float ringGreen() {
        return this.isEnhanced() ? 0.25F : 1.0F;
    }

    private float ringBlue() {
        return this.isEnhanced() ? 0.25F : 0.75F;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    private boolean isAlly(Entity entity) {
        if (entity == this || entity == this.getOwner()) {
            return true;
        }
        LivingEntity owner = this.getOwner();
        return owner != null && entity instanceof LivingEntity living
                && owner.isAlliedTo(living);
    }

    private List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(distanceX, distanceY, distanceZ),
                e -> e != (Entity) this && this.distanceTo(e) <= radius + e.getBbWidth() / 2.0F && e.getY() <= this.getY() + distanceY);
    }

    private void AreaAttack(float range, float height, float arc, float damage) {
        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return;
        }
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180.0D / Math.PI) - 90.0D) % 360.0D);
            float entityAttackingAngle = this.getYRot() % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            boolean inArc = entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc
                    || this.isAlly(entityHit)
                    || entityHit instanceof BeheadedKnightServant
                    || entityHit instanceof ResurrectedKnightServant) {
                continue;
            }
            entityHit.hurt(ModDamageTypes.causeGhostlyDamage(owner, owner), damage);
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            if (this.tickCount % 3 == 0) {
                float yaw = (float) Math.toRadians(-this.getYRot() + 180.0F);
                double theta = this.getYRot() * (Math.PI / 180) + 1.5707963267948966;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                Vec3 vec3 = this.getDeltaMovement();
                double spawnX = this.getX() + vec3.x + vecX * 1.5D;
                double spawnZ = this.getZ() + vec3.z + vecZ * 1.5D;
                this.level().addParticle(new Circle.RingData(this.horizontalCollision ? 0.0F : yaw, this.horizontalCollision ? 90.0F : 0.0F,
                                30, this.ringRed(), this.ringGreen(), this.ringBlue(), 0.8F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                        spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            }
        }
        if (this.isVehicle() && this.getFirstPassenger() instanceof Player player) {
            player.setShiftKeyDown(false);
        }
        super.tick();
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CLOUD;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() == this.getFirstPassenger()) {
            return;
        }
        super.onHitEntity(result);
        LivingEntity owner = this.getOwner();
        if (!this.level().isClientSide && owner != null) {
            Entity entity = result.getEntity();
            if (entity.hurt(this.damageSources().mobAttack(owner), 8.0F) && entity.isAlive()) {
                this.doEnchantDamageEffects(owner, entity);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        this.handleEntityEvent((byte) 1);
        if (result instanceof EntityHitResult) {
            return;
        }
        super.onHit(result);
        this.xPillars(6, 3.0D, 5.0F);
        this.AreaAttack(4.0F, 4.0F, 360.0F, this.getDamage());
        CameraShakeEntity.cameraShake(this.level(), this.position(), 5.0F, 0.1F, 5, 5);
        this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
        this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, this.ringRed(), this.ringGreen(), this.ringBlue(), 1.0F, 100.0F,
                false, Circle.EnumRingBehavior.GROW), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        this.level().addParticle(new Circle.RingData(1.5707964F, 0.0F, 30, this.ringRed(), this.ringGreen(), this.ringBlue(), 1.0F, 100.0F,
                false, Circle.EnumRingBehavior.GROW), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.tickCount = 0;
        } else if (id == 1) {
            this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            double theta = this.getYRot() * (Math.PI / 180) + 1.5707963267948966;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            Vec3 vec3 = this.getDeltaMovement();
            double spawnX = this.getX() + vec3.x + vecX * 1.5D;
            double spawnZ = this.getZ() + vec3.z + vecZ * 1.5D;
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, this.ringRed(), this.ringGreen(), this.ringBlue(), 0.8F, 40.0F,
                    false, Circle.EnumRingBehavior.GROW_THEN_SHRINK), spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            this.level().addParticle(new Circle.RingData(1.5707964F, 0.0F, 30, this.ringRed(), this.ringGreen(), this.ringBlue(), 0.8F, 40.0F,
                    false, Circle.EnumRingBehavior.GROW_THEN_SHRINK), spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void xPillars(int pillar, double delay, float distance) {
        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return;
        }
        for (int i = 0; i < pillar; ++i) {
            float throwAngle = (float) i * (float) Math.PI / (float) (pillar / 2);
            for (int k = 0; (float) k < distance; ++k) {
                double d2 = 1.15D * (k + 1);
                int d3 = (int) (delay * (k + 1));
                this.spawnSoulPillars(this.getX() + Mth.cos(throwAngle) * 1.25D * d2,
                        this.getY(),
                        this.getZ() + Mth.sin(throwAngle) * 1.25D * d2,
                        this.getY() - 2.0D, throwAngle, d3, this.level(), owner);
            }
        }
    }

    private boolean spawnSoulPillars(double x, double y, double z, double lowestYCheck, float yRot,
                                      int warmupDelayTicks, Level world, LivingEntity caster) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos belowPos = blockpos.below();
            BlockState below = world.getBlockState(belowPos);
            if (below.isFaceSturdy(world, belowPos, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState here = world.getBlockState(blockpos);
                    VoxelShape shape = here.getCollisionShape(world, blockpos);
                    if (!shape.isEmpty()) {
                        d0 = shape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while ((double) blockpos.getY() >= lowestYCheck);
        if (flag) {
            world.addFreshEntity(new SoulPillar(world, x, (double) blockpos.getY() + d0, z, yRot,
                    warmupDelayTicks, caster, 20, 8.0F, this.isEnhanced()));
            return true;
        }
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
