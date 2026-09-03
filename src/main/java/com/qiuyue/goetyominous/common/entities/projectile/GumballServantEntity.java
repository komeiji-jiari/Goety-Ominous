package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * 糖球弹丸:忠实移植 Alex's Caves 原版 GumballEntity 的完整弹跳逻辑(碰地反弹、
 * 随机 12 色糖球、可分裂/追踪/爆炸选项),仅在两处加入友伤过滤:
 * <ol>
 *   <li>发射时把 owner 直接设为主人(getTrueOwner),使糖球在仆从自爆阵亡后仍能以主人为源判定友军;</li>
 *   <li>命中判定用 Goety 的 MobUtil.areAllies 兜底,主人的其他仆从/盟友不再被弹跳糖球误伤。
 *       (与 DeepOneMageServantWaterBolt 保持一致)</li>
 * </ol>
 * 另加 15 秒寿命上限,避免停驻的糖球永久残留场景(原版仅靠卸载区块清理)。
 */
public class GumballServantEntity extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> MAXIMUM_BOUNCES = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOUNCES = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TARGETS_ON_BOUNCE = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPLITS_ON_HIT = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EXPLOSIVE = SynchedEntityData.defineId(GumballServantEntity.class, EntityDataSerializers.BOOLEAN);

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float explodeProgress;
    private float prevExplodeProgress;
    private final List<Integer> hitEntityIds = new ArrayList<>();
    private int bounceSoundCooldown = 0;

    public GumballServantEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.setColor(level.random.nextInt(12));
    }

    public GumballServantEntity(Level level, LivingEntity shooter) {
        this(AcEntityRegistry.GUMBALL_SERVANT.get(), level);
        this.setOwner(shooter);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAXIMUM_BOUNCES, 5);
        this.entityData.define(BOUNCES, 0);
        this.entityData.define(COLOR, 0);
        this.entityData.define(DAMAGE, 2.0F);
        this.entityData.define(TARGETS_ON_BOUNCE, false);
        this.entityData.define(SPLITS_ON_HIT, false);
        this.entityData.define(EXPLOSIVE, false);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevExplodeProgress = this.explodeProgress;
        if (!this.level().isClientSide && this.tickCount > 300) {
            this.discard();
            return;
        }
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (this.isExplosive() && this.getBounces() >= this.getMaximumBounces()) {
            if (this.explodeProgress > 20.0F) {
                if (!this.level().isClientSide) {
                    this.level().explode(this.getOwner(), this.getX(), this.getY() + 0.5, this.getZ(), 2.0F, false, Level.ExplosionInteraction.NONE);
                    this.discard();
                }
            } else {
                this.explodeProgress += 1.0F;
            }
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.4F, this.getZ(), 0.0D, 0.1D, 0.0D);
        }
        if (this.bounceSoundCooldown > 0) {
            --this.bounceSoundCooldown;
        }
    }

    public void bounceFromDirection(Direction hitDirection) {
        boolean flag = false;
        if (this.getBounces() > this.getMaximumBounces() - 1 && this.isExplosive()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setBounces(this.getMaximumBounces());
            return;
        }
        if (this.targetsOnBounce()) {
            Entity shooter = this.getOwner();
            LivingEntity nearestBounceTarget = null;
            Vec3 position = this.getEyePosition();
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(position.add(-10.0D, -10.0D, -10.0D), position.add(10.0D, 10.0D, 10.0D)))) {
                if (shooter != null && this.isFriendlyToOwner(entity)) continue;
                if (this.hitEntityIds.contains(entity.getId())) continue;
                if (nearestBounceTarget != null && !(entity.distanceTo(this) < nearestBounceTarget.distanceTo(this))) continue;
                nearestBounceTarget = entity;
            }
            if (nearestBounceTarget != null) {
                flag = true;
                if (!this.level().isClientSide) {
                    this.setBounces(this.getBounces() + 1);
                }
                if (this.getBounces() > this.getMaximumBounces()) {
                    this.discard();
                } else {
                    Vec3 vec3 = nearestBounceTarget.getEyePosition().subtract(this.position()).normalize().scale(0.8F);
                    this.setDeltaMovement(vec3.x, vec3.y, vec3.z);
                }
            }
        }
        if (this.bounceSoundCooldown == 0) {
            this.bounceSoundCooldown = 5;
            this.playSound(ACSoundRegistry.GUMBALL_BOUNCE.get());
        }
        if (!flag) {
            Vec3 deltaMovement = this.getDeltaMovement();
            double x = deltaMovement.x();
            double y = deltaMovement.y();
            double z = deltaMovement.z();
            switch (hitDirection.getAxis()) {
                case X:
                    x = -x * 0.8F;
                    break;
                case Y:
                    y = -y * 0.5;
                    break;
                case Z:
                    z = -z * 0.8F;
                    break;
            }
            if (!this.level().isClientSide) {
                this.setBounces(this.getBounces() + 1);
            }
            if (this.getBounces() > this.getMaximumBounces()) {
                this.discard();
            } else {
                this.setDeltaMovement(x, y, z);
            }
        }
    }

    public int getMaximumBounces() {
        return this.entityData.get(MAXIMUM_BOUNCES);
    }

    public void setMaximumBounces(int bounces) {
        this.entityData.set(MAXIMUM_BOUNCES, bounces);
    }

    public int getBounces() {
        return this.entityData.get(BOUNCES);
    }

    public void setBounces(int bounces) {
        this.entityData.set(BOUNCES, bounces);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public boolean targetsOnBounce() {
        return this.entityData.get(TARGETS_ON_BOUNCE);
    }

    public void setTargetsOnBounce(boolean targets) {
        this.entityData.set(TARGETS_ON_BOUNCE, targets);
    }

    public boolean splitsOnHit() {
        return this.entityData.get(SPLITS_ON_HIT);
    }

    public void setSplitsOnHit(boolean splits) {
        this.entityData.set(SPLITS_ON_HIT, splits);
    }

    public boolean isExplosive() {
        return this.entityData.get(EXPLOSIVE);
    }

    public void setExplosive(boolean explosive) {
        this.entityData.set(EXPLOSIVE, explosive);
    }

    @Override
    protected float getGravity() {
        return 0.08F;
    }

    /**
     * 弹丸命中后是否应视为"主人/盟友一侧":自身、owner、双向 isAlliedTo,
     * 外加 Goety 的 MobUtil.areAllies 兜底(主人的其他仆从、SEHelper 盟友)。
     */
    private boolean isFriendlyToOwner(Entity entity) {
        Entity owner = this.getOwner();
        if (owner == null) {
            return entity.is(this);
        }
        if (entity.is(owner)) {
            return true;
        }
        if (entity.isAlliedTo(owner)) {
            return true;
        }
        if (owner.isAlliedTo(entity)) {
            return true;
        }
        return MobUtil.areAllies(entity, owner);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockState state = this.level().getBlockState(blockHitResult.getBlockPos());
            if (!state.getCollisionShape(this.level(), blockHitResult.getBlockPos()).isEmpty()) {
                this.bounceFromDirection(blockHitResult.getDirection());
            }
        } else if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (!this.isFriendlyToOwner(entity) && !(entity instanceof GumballServantEntity)) {
                this.hitEntityIds.add(entity.getId());
                Vec3 vec3 = entity.getEyePosition().subtract(this.getEyePosition());
                float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                if (!this.splitsOnHit()) {
                    this.bounceFromDirection(Direction.fromYRot(f));
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        float damage = this.getDamage();
        if (!this.isFriendlyToOwner(entity)) {
            DamageSource damageSource = this.damageSources().mobProjectile(this, owner instanceof LivingEntity living ? living : null);
            this.playSound(ACSoundRegistry.GUMBALL_HIT.get());
            entity.hurt(damageSource, damage);
        }
        if (this.splitsOnHit()) {
            for (int i = 0; i < 3; ++i) {
                GumballServantEntity gumball = AcEntityRegistry.GUMBALL_SERVANT.get().create(this.level());
                if (gumball == null) {
                    return;
                }
                gumball.setOwner(owner);
                Vec3 vec3 = this.getDeltaMovement().normalize();
                float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                Vec3 vec31 = new Vec3(0.0D, 0.0D, this.isExplosive() ? 0.7F : 1.5).yRot((float) -Math.toRadians(f + 30.0F - 30.0F * (float) i));
                gumball.setPos(entity.getEyePosition().add(vec31));
                gumball.setDeltaMovement(vec31);
                gumball.setSplitsOnHit(false);
                gumball.setDamage(this.getDamage());
                gumball.setTargetsOnBounce(this.targetsOnBounce());
                gumball.setExplosive(this.isExplosive());
                gumball.setMaximumBounces(this.getMaximumBounces());
                this.level().addFreshEntity(gumball);
            }
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Color", this.getColor());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setColor(compoundTag.getInt("Color"));
    }

    public float getExplodeProgress(float partialTicks) {
        return (this.prevExplodeProgress + (this.explodeProgress - this.prevExplodeProgress) * partialTicks) / 20.0F;
    }
}
