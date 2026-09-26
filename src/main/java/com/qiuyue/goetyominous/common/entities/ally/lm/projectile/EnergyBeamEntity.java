package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergyBeamEntity extends Entity {
    public static final double RADIUS = 30.0;
    public LivingEntity caster;
    public double endPosX;
    public double endPosY;
    public double endPosZ;
    public double collidePosX;
    public double collidePosY;
    public double collidePosZ;
    public double prevCollidePosX;
    public double prevCollidePosY;
    public double prevCollidePosZ;
    public float renderYaw;
    public float renderPitch;
    public ControlledAnim appear = new ControlledAnim(3);
    public ControlledAnim appearVisual = new ControlledAnim(3);
    public ControlledAnim disappear = new ControlledAnim(3);
    public boolean on = true;
    public Direction blockSide = null;
    public float prevYaw;
    public float prevPitch;
    public int NextSound = 0;

    private static final EntityDataAccessor<Float> YAW =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CASTER =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HEAD =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FIRE =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HPDAMAGE =
            SynchedEntityData.defineId(EnergyBeamEntity.class, EntityDataSerializers.FLOAT);

    @OnlyIn(Dist.CLIENT)
    private Vec3[] attractorPos;

    public EnergyBeamEntity(EntityType<? extends EnergyBeamEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
        if (world.isClientSide) {
            this.attractorPos = new Vec3[]{new Vec3(0.0, 0.0, 0.0)};
        }
    }

    public EnergyBeamEntity(EntityType<? extends EnergyBeamEntity> type, Level world, LivingEntity caster, double x, double y, double z,
                            float yaw, float pitch, int duration, float damage, float hpDamage) {
        this(type, world);
        this.caster = caster;
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setDuration(duration);
        this.setPos(x, y, z);
        this.setDamage(damage);
        this.setHpDamage(hpDamage);
        this.calculateEndPos();
        if (!world.isClientSide) {
            this.setCasterID(caster.getId());
        }
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    private void updateWithCaster() {
        if (this.caster != null) {
            double theta = this.caster.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta + 1.5707963267948966);
            double vecZ = Math.sin(theta + 1.5707963267948966);
            double spawnY = this.caster.getY(0.0);
            float radius = 1.0F;
            float angle = (float) Math.PI / 180 * this.caster.yBodyRot;
            double extraX = (double) (radius * Mth.sin((float) (Math.PI + (double) angle))) + 0.5;
            double extraZ = (double) (radius * Mth.cos(angle)) - 0.5 * (double) Mth.sin(angle);
            this.setYaw((float) ((double) (this.caster.yHeadRot + 90.0F) * Math.PI / 180.0));
            this.setPitch((float) ((double) (-this.caster.getXRot()) * Math.PI / 180.0));
            this.setPos(this.caster.getX() + extraX, spawnY + 1.0, this.caster.getZ() + extraZ);
        }
    }

    private void spawnParticlesAlongLine(Level level, Vec3 from, Vec3 to, double step) {
        Vec3 direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double i = 0.0; i < distance; i += step) {
            Vec3 particlePos = from.add(direction.scale(i));
            level.addParticle(ParticleTypes.WAX_OFF, particlePos.x, particlePos.y, particlePos.z, 50.0, 0.0, 50.0);
        }
    }

    private void spawnThickParticlesAlongLine(Level level, Vec3 from, Vec3 to, double step, double radius) {
        Vec3 direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double i = 0.0; i < distance; i += step) {
            Vec3 center = from.add(direction.scale(i));
            for (int j = 0; j < 10; ++j) {
                double angle = Math.PI * 2 * level.random.nextDouble();
                double r = radius * Math.sqrt(level.random.nextDouble());
                double offsetX = r * Math.cos(angle);
                double offsetZ = r * Math.sin(angle);
                level.addParticle(ParticleTypes.END_ROD, center.x + offsetX, center.y, center.z + offsetZ, 0.0, 0.0, 0.0);
            }
        }
    }

    private void spawnUniformParticlesAlongLine(Level level, Vec3 from, Vec3 to, double step, double radius) {
        Vec3 direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double i = 0.0; i < distance; i += step) {
            Vec3 center = from.add(direction.scale(i));
            for (int j = 0; j < 10; ++j) {
                double angle = Math.PI * 2 * level.random.nextDouble();
                double r = radius * Math.sqrt(level.random.nextDouble());
                double offsetX = r * Math.cos(angle);
                double offsetZ = r * Math.sin(angle);
                double offsetY = (level.random.nextDouble() * 2.0 - 1.0) * radius;
                level.addParticle(ModParticles.BEAM.get(), center.x + offsetX, center.y + offsetY, center.z + offsetZ, 0.005, 0.005, 0.005);
            }
        }
    }

    private void spawnSpiralParticlesAlongLine(Level level, Vec3 from, Vec3 to, double step, double radius, double loops) {
        Vec3 direction = to.subtract(from).normalize();
        double distance = from.distanceTo(to);
        for (double i = 0.0; i < distance; i += step) {
            Vec3 center = from.add(direction.scale(i));
            double progress = i / distance;
            double angle = progress * 2.0 * Math.PI * loops;
            double offsetX = radius * Math.cos(angle);
            double offsetY = radius * Math.sin(angle);
            level.addParticle(ModParticles.BEAM.get(), center.x + offsetX, center.y + offsetY, center.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.NextSound == 0 && this.tickCount >= 20) {
            this.NextSound = 10;
        }
        if (this.NextSound <= 0) {
            --this.NextSound;
        }
        if (this.tickCount == this.getDuration()) {
            this.discard();
        }
        if (this.caster != null) {
            this.updateWithCaster();
            this.calculateEndPos();
        }
        if (!this.on && this.appear.getTimer() == 0) {
            this.discard();
        }
        if (this.on && this.tickCount > 20) {
            this.appear.increaseTimer();
        } else {
            this.appear.decreaseTimer();
        }
        if (this.tickCount - 20 > this.getDuration()) {
            this.on = false;
        }
        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.tickCount == 1 && this.level().isClientSide) {
            this.caster = (LivingEntity) this.level().getEntity(this.getCasterID());
        }
        if (this.caster != null) {
            this.renderYaw = (float) (((double) this.caster.yHeadRot + 90.0) * Math.PI / 180.0);
            this.renderPitch = (float) ((double) (-this.caster.getXRot()) * Math.PI / 180.0);
        }
        if (this.caster != null && !this.caster.isAlive()) {
            this.discard();
        }
        if (this.tickCount > 20) {
            Vec3 entityPosition = this.position();
            CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.2F, 0, 2);
            Vec3 start = new Vec3(this.getX(), this.getY(), this.getZ());
            Vec3 end = new Vec3(this.endPosX, this.endPosY, this.endPosZ);
            this.spawnSpiralParticlesAlongLine(this.level(), start, end, 0.2, 0.7, 20.0);
            this.calculateEndPos();
            List<LivingEntity> hit = this.raytraceEntities(this.level(),
                    new Vec3(this.getX(), this.getY(), this.getZ()),
                    new Vec3(this.endPosX, this.endPosY, this.endPosZ), false, true, true).entities;
            if (this.blockSide != null) {
                this.spawnExplosionParticles(5);
                if (!this.level().isClientSide) {
                    for (BlockPos pos : BlockPos.betweenClosed(
                            Mth.floor(this.collidePosX - 2.5), Mth.floor(this.collidePosY - 2.5), Mth.floor(this.collidePosZ - 2.5),
                            Mth.floor(this.collidePosX + 2.5), Mth.floor(this.collidePosY + 2.5), Mth.floor(this.collidePosZ + 2.5))) {
                        BlockState blockState = this.level().getBlockState(pos);
                    }
                    if (this.getFire()) {
                        BlockPos blockPos = BlockPos.containing(this.collidePosX, this.collidePosY, this.collidePosZ);
                    }
                }
            }
            if (!this.level().isClientSide) {
                for (LivingEntity target : hit) {
                    if (this.caster == null || target == this.caster || MobUtil.areAllies(this.caster, target)) {
                        continue;
                    }
                    boolean flag = target.hurt(ModDamageTypes.causeEnergyBeamDamage(this, this.caster),
                            (float) ((double) this.getDamage() + (double) target.getMaxHealth() * 0.01));
                    if (flag && this.caster instanceof Mob mob && mob.getTarget() != null) {
                        float f = (float) ((double) mob.getTarget().getMaxHealth() * 0.0025);
                    }
                    if (this.getFire() && flag) {
                        target.setSecondsOnFire(2);
                    }
                }
            }
        }
        if (this.tickCount - 20 > this.getDuration()) {
            this.on = false;
        }
    }

    private void spawnExplosionParticles(int amount) {
        for (int i = 0; i < amount; ++i) {
            float yaw = (float) ((double) (this.random.nextFloat() * 2.0F) * Math.PI);
            float motionY = this.random.nextFloat() * 0.8F;
            float motionX = 1.5F * Mth.cos(yaw);
            float motionZ = 1.5F * Mth.sin(yaw);
            this.level().addParticle(ModParticles.BEAM.get(), this.collidePosX, this.collidePosY + 0.1, this.collidePosZ,
                    (double) motionX, (double) motionY, (double) motionZ);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(YAW, 0.0F);
        this.entityData.define(PITCH, 0.0F);
        this.entityData.define(DURATION, 0);
        this.entityData.define(CASTER, -1);
        this.entityData.define(HEAD, 0);
        this.entityData.define(FIRE, false);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(HPDAMAGE, 0.0F);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getHpDamage() {
        return this.entityData.get(HPDAMAGE);
    }

    public void setHpDamage(float damage) {
        this.entityData.set(HPDAMAGE, damage);
    }

    public float getYaw() {
        return this.entityData.get(YAW);
    }

    public void setYaw(float yaw) {
        this.entityData.set(YAW, yaw);
    }

    public float getPitch() {
        return this.entityData.get(PITCH);
    }

    public void setPitch(float pitch) {
        this.entityData.set(PITCH, pitch);
    }

    public int getDuration() {
        return this.entityData.get(DURATION);
    }

    public void setDuration(int duration) {
        this.entityData.set(DURATION, duration);
    }

    public int getHead() {
        return this.entityData.get(HEAD);
    }

    public void setHead(int head) {
        this.entityData.set(HEAD, head);
    }

    public int getCasterID() {
        return this.entityData.get(CASTER);
    }

    public void setCasterID(int id) {
        this.entityData.set(CASTER, id);
    }

    public boolean getFire() {
        return this.entityData.get(FIRE);
    }

    public void setFire(boolean fire) {
        this.entityData.set(FIRE, fire);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private void calculateEndPos() {
        if (this.level().isClientSide()) {
            this.endPosX = this.getX() + 30.0 * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosZ = this.getZ() + 30.0 * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosY = this.getY() + 30.0 * Math.sin(this.renderPitch);
        } else {
            this.endPosX = this.getX() + 30.0 * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosZ = this.getZ() + 30.0 * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosY = this.getY() + 30.0 * Math.sin(this.getPitch());
        }
    }

    public LaserbeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to, boolean stopOnLiquid,
                                               boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        LaserbeamHitResult result = new LaserbeamHitResult();
        result.setBlockHit(world.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.blockHit != null) {
            Vec3 hitVec = result.blockHit.getLocation();
            this.collidePosX = hitVec.x;
            this.collidePosY = hitVec.y;
            this.collidePosZ = hitVec.z;
            this.blockSide = result.blockHit.getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.blockSide = null;
        }
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(
                Math.min(this.getX(), this.collidePosX), Math.min(this.getY(), this.collidePosY), Math.min(this.getZ(), this.collidePosZ),
                Math.max(this.getX(), this.collidePosX), Math.max(this.getY(), this.collidePosY), Math.max(this.getZ(), this.collidePosZ))
                .inflate(1.0, 1.0, 1.0));
        for (LivingEntity entity : entities) {
            if (entity == this.caster) {
                continue;
            }
            float pad = entity.getPickRadius() + 0.5F;
            AABB aabb = entity.getBoundingBox().inflate(pad, pad, pad);
            Optional<Vec3> hit = aabb.clip(from, to);
            if (aabb.contains(from)) {
                result.addEntityHit(entity);
                continue;
            }
            if (!hit.isPresent()) {
                continue;
            }
            result.addEntityHit(entity);
        }
        return result;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0;
    }

    public static class LaserbeamHitResult {
        private BlockHitResult blockHit;
        private final List<LivingEntity> entities = new ArrayList<>();

        public BlockHitResult getBlockHit() {
            return this.blockHit;
        }

        public void setBlockHit(HitResult rayTraceResult) {
            if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
                this.blockHit = (BlockHitResult) rayTraceResult;
            }
        }

        public void addEntityHit(LivingEntity entity) {
            this.entities.add(entity);
        }
    }
}
