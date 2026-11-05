package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.UUID;

public class PyromancerFireball extends ModFireball {
    private static final float FALLBACK_DAMAGE = 6.0F;
    private static final float DIRECT_DAMAGE_MULTIPLIER = 1.0F;
    private static final float AOE_RADIUS = 3.0F;
    private static final float AOE_DAMAGE_MULTIPLIER = 1.0F;
    private static final double FIREBALL_POWER = 0.1D;
    private static final float HOMING_TURN = 0.22F;
    private static final float FIRE_RADIUS = 2.5F;
    private static final EntityDataAccessor<Boolean> DATA_HELD = SynchedEntityData.defineId(
            PyromancerFireball.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_POWER_X = SynchedEntityData.defineId(
            PyromancerFireball.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_POWER_Y = SynchedEntityData.defineId(
            PyromancerFireball.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_POWER_Z = SynchedEntityData.defineId(
            PyromancerFireball.class, EntityDataSerializers.FLOAT);

    @Nullable
    private UUID homingTarget;

    public PyromancerFireball(EntityType<? extends ModFireball> type, Level level) {
        super(type, level);
        this.setDamage(FALLBACK_DAMAGE);
    }

    public PyromancerFireball(Level level, LivingEntity owner) {
        this(ModEntityTypes.PYROMANCER_FIREBALL.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        this.setPowers(owner.getLookAngle());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HELD, false);
        this.entityData.define(DATA_POWER_X, 0.0F);
        this.entityData.define(DATA_POWER_Y, 0.0F);
        this.entityData.define(DATA_POWER_Z, 0.0F);
    }

    public boolean isHeld() {
        return this.entityData.get(DATA_HELD);
    }

    public void setHeld(boolean held) {
        this.entityData.set(DATA_HELD, held);
        this.noPhysics = held;
        if (held) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPowers(Vec3.ZERO);
        }
    }

    public void launch(Vec3 target) {
        this.entityData.set(DATA_HELD, false);
        this.noPhysics = false;
        Vec3 direction = target.subtract(this.position());
        if (direction.lengthSqr() < 1.0E-4D) {
            direction = this.getLookAngle();
        }
        direction = direction.normalize();
        this.setDeltaMovement(Vec3.ZERO);
        this.setPowers(direction);
    }

    private void setPowers(Vec3 direction) {
        this.xPower = direction.x * FIREBALL_POWER;
        this.yPower = direction.y * FIREBALL_POWER;
        this.zPower = direction.z * FIREBALL_POWER;
        this.entityData.set(DATA_POWER_X, (float) this.xPower);
        this.entityData.set(DATA_POWER_Y, (float) this.yPower);
        this.entityData.set(DATA_POWER_Z, (float) this.zPower);
    }

    @Override
    public void setOwner(Entity owner) {
        super.setOwner(owner);
        if (!this.level().isClientSide && owner instanceof LivingEntity living) {
            this.setDamage(resolveOwnerDamage(living));
        }
    }

    private static float resolveOwnerDamage(LivingEntity owner) {
        double attackDamage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return attackDamage > 0.0D ? (float) attackDamage * DIRECT_DAMAGE_MULTIPLIER : FALLBACK_DAMAGE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityTypes.PYROMANCER_FIREBALL.get();
    }

    @Override
    public void tick() {
        if (this.isHeld()) {
            this.baseTick();
            if (this.level().isClientSide) {
                this.trailParticles();
            }
            return;
        }
        if (this.level().isClientSide) {
            this.xPower = this.entityData.get(DATA_POWER_X);
            this.yPower = this.entityData.get(DATA_POWER_Y);
            this.zPower = this.entityData.get(DATA_POWER_Z);
        } else {
            this.handleHoming();
        }
        super.tick();
        if (this.level().isClientSide) {
            this.trailParticles();
        }
    }

    public void setHomingTarget(@Nullable LivingEntity target) {
        this.homingTarget = target != null ? target.getUUID() : null;
    }

    private void handleHoming() {
        LivingEntity target = this.getHomingTargetEntity();
        if (target == null) {
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(this.position());
        if (toTarget.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 targetDir = toTarget.normalize();
        Vec3 newDir;
        if (speed < 1.0E-4D) {
            newDir = targetDir;
        } else {
            Vec3 currentDir = motion.scale(1.0D / speed);
            newDir = currentDir.scale(1.0D - HOMING_TURN)
                    .add(targetDir.scale(HOMING_TURN))
                    .normalize();
        }
        this.setPowers(newDir);
    }

    @Nullable
    private LivingEntity getHomingTargetEntity() {
        if (this.homingTarget == null) {
            return null;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.homingTarget);
            if (entity instanceof LivingEntity living && living.isAlive() && living != this.getOwner()) {
                return living;
            }
        }
        this.homingTarget = null;
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.homingTarget != null) {
            tag.putUUID("HomingTarget", this.homingTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.homingTarget = tag.hasUUID("HomingTarget") ? tag.getUUID("HomingTarget") : null;
    }

    private void trailParticles() {
        Vec3 motion = this.getDeltaMovement();
        double prevX = this.getX() - motion.x;
        double prevY = this.getY() - motion.y;
        double prevZ = this.getZ() - motion.z;
        for (int i = 0; i < 4; i++) {
            Vec3 random = this.randomVec3(0.2D);
            this.level().addParticle(ParticleTypes.SMOKE, true,
                    prevX - random.x, prevY + 0.5D - random.y, prevZ - random.z,
                    random.x * 0.5D, random.y * 0.5D, random.z * 0.5D);
        }
        for (int i = 0; i < 2; i++) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, true,
                    prevX + (this.random.nextDouble() - 0.5D) * 0.6D,
                    prevY + 0.5D + (this.random.nextDouble() - 0.5D) * 0.4D,
                    prevZ + (this.random.nextDouble() - 0.5D) * 0.6D,
                    0.0D, 0.02D, 0.0D);
        }
        int count = Mth.clamp((int) Math.ceil(motion.length() * 3.0D), 1, 5);
        for (int i = 0; i < count; i++) {
            double t = count == 1 ? 0.5D : (double) i / (double) count;
            double x = Mth.lerp(t, this.getX() - motion.x, this.getX()) + (this.random.nextDouble() - 0.5D) * 0.6D;
            double y = Mth.lerp(t, this.getY() - motion.y, this.getY()) + this.getBbHeight() * 0.5D
                    + (this.random.nextDouble() - 0.5D) * 0.6D;
            double z = Mth.lerp(t, this.getZ() - motion.z, this.getZ()) + (this.random.nextDouble() - 0.5D) * 0.6D;
            this.level().addParticle(ModParticleTypes.BIG_FIRE.get(), true, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        if (this.random.nextInt(4) == 0) {
            this.level().addParticle(ModParticleTypes.FLY.get(), true,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    (this.random.nextDouble() - 0.5D) * 0.08D, 0.03D, (this.random.nextDouble() - 0.5D) * 0.08D);
        }
        if (this.random.nextInt(3) == 0) {
            this.level().addParticle(com.qiuyue.goetyominous.common.init.ModParticleTypes.BOUNCING_SPARK.get(), true,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.4D,
                    this.getY() + 0.3D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D,
                    (this.random.nextDouble() - 0.5D) * 0.3D, -0.05D, (this.random.nextDouble() - 0.5D) * 0.3D);
        }
    }

    private Vec3 randomVec3(double scale) {
        return new Vec3((this.random.nextDouble() * 2.0D - 1.0D) * scale,
                (this.random.nextDouble() * 2.0D - 1.0D) * scale,
                (this.random.nextDouble() * 2.0D - 1.0D) * scale);
    }

    @Override
    protected void onHit(HitResult result) {
        Vec3 pos = result.getLocation();
        super.onHit(result);
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            this.impactBurst(serverLevel, pos);
            this.createImpactFire(serverLevel, pos);
            this.applyAoe(serverLevel, pos);
        }
    }

    private void createImpactFire(ServerLevel level, Vec3 pos) {
        if (!this.canIgnite(level)) {
            return;
        }
        int radius = Mth.ceil(FIRE_RADIUS);
        BlockPos center = BlockPos.containing(pos);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > FIRE_RADIUS * FIRE_RADIUS) {
                        continue;
                    }
                    BlockPos firePos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(firePos).isAir()) {
                        continue;
                    }
                    BlockPos below = firePos.below();
                    if (!level.getBlockState(below).isSolidRender(level, below)) {
                        continue;
                    }
                    if (level.random.nextInt(3) != 0) {
                        continue;
                    }
                    level.setBlockAndUpdate(firePos, BaseFireBlock.getState(level, firePos));
                }
            }
        }
    }

    private boolean canIgnite(ServerLevel level) {
        if (!this.isDangerous()) {
            return false;
        }
        Entity owner = this.getOwner();
        if (owner instanceof Player || (owner instanceof IOwned owned && owned.getMasterOwner() instanceof Player)) {
            return SpellConfig.FireballGriefing.get();
        }
        return ForgeEventFactory.getMobGriefingEvent(level, owner);
    }

    private void applyAoe(ServerLevel level, Vec3 pos) {
        float baseDamage = this.getDamage() * AOE_DAMAGE_MULTIPLIER;
        DamageSource source = ModDamageSource.magicFireball(this, this.getOwner(), level);
        new SpellExplosion(level, this, source, pos.x, pos.y, pos.z, AOE_RADIUS, 0.0F) {
            @Override
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z,
                                    double seen, float actualDamage) {
                float damage = (float) (baseDamage * (seen * seen + seen) * 0.5D);
                if (damage > 0.0F) {
                    super.explodeHurt(target, damageSource, x, y, z, seen, damage);
                }
            }
        };
    }

    private void impactBurst(ServerLevel level, Vec3 pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 4.0F,
                (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
        level.sendParticles(new ShockwaveParticleOption(1.0F, 0.55F, 0.2F, 1.8F, 3.6F, 1, true),
                x, y + 0.05D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(new CircleExplodeParticleOption(1.0F, 0.55F, 0.2F, 4.0F, 1),
                x, y + 0.05D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(new CircleExplodeParticleOption(1.0F, 0.75F, 0.35F, 5.5F, 2),
                x, y + 0.05D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.SMOKE,
                x, y + 0.5D, z, 100, 1.6D, 1.0D, 1.6D, 0.08D);
        level.sendParticles(ParticleTypes.LARGE_SMOKE,
                x, y + 0.5D, z, 50, 1.4D, 0.8D, 1.4D, 0.04D);
        level.sendParticles(ParticleTypes.LAVA,
                x, y + 0.5D, z, 30, 1.5D, 0.1D, 1.5D, 1.0D);
        DustCloudParticleOption darkCloud = new DustCloudParticleOption(
                new Vector3f(Vec3.fromRGB24(8021604).toVector3f()), 1.2F);
        DustCloudParticleOption warmCloud = new DustCloudParticleOption(
                new Vector3f(Vec3.fromRGB24(15508116).toVector3f()), 1.2F);
        for (int i = 0; i < 2; i++) {
            ServerParticleUtil.circularParticles(level, darkCloud, x, y + 0.25D, z,
                    0.0D, 0.14D, 0.0D, AOE_RADIUS / 2.0F);
        }
        ServerParticleUtil.circularParticles(level, warmCloud, x, y + 0.25D, z,
                0.0D, 0.14D, 0.0D, AOE_RADIUS / 2.0F);
        ServerParticleUtil.circularParticles(level, darkCloud, x, y + 1.0D, z,
                0.0D, 0.12D, 0.0D, AOE_RADIUS / 4.0F);
        ServerParticleUtil.circularParticles(level, warmCloud, x, y + 1.0D, z,
                0.0D, 0.12D, 0.0D, AOE_RADIUS / 4.0F);
        level.sendParticles(ModParticleTypes.BIG_FIRE.get(),
                x, y + 0.5D, z, 60, 1.2D, 0.8D, 1.2D, 0.12D);
        level.sendParticles(ModParticleTypes.BIG_FIRE_DROP.get(),
                x, y + 0.5D, z, 24, 1.0D, 0.6D, 1.0D, 0.15D);
        level.sendParticles(com.qiuyue.goetyominous.common.init.ModParticleTypes.BOUNCING_SPARK.get(),
                x, y + 0.5D, z, 100, 1.2D, 0.7D, 1.2D, 0.55D);
        level.sendParticles(com.qiuyue.goetyominous.common.init.ModParticleTypes.BOUNCING_SPARK.get(),
                x, y + 0.3D, z, 80, 1.8D, 0.4D, 1.8D, 0.95D);
        ServerParticleUtil.circularParticles(level, ModParticleTypes.BIG_FIRE_GROUND.get(),
                x, y + 0.15D, z, 0.0D, 0.05D, 0.0D, 2.5F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
