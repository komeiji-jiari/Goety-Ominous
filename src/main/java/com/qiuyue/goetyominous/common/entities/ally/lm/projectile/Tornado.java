package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.common.entities.projectiles.WaterHurtingProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class Tornado extends WaterHurtingProjectile {
    private boolean leftOwner;
    private boolean hasHurt;
    public boolean onFire;
    public boolean AdjustedLife;
    public int maxLife = 20;
    public int lifetick;

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(Tornado.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TRANSPARENCY =
            SynchedEntityData.defineId(Tornado.class, EntityDataSerializers.INT);

    public Tornado(EntityType<? extends Tornado> type, Level level) {
        super(type, level);
    }

    public Tornado(EntityType<? extends Tornado> type, double x, double y, double z,
                   double dx, double dy, double dz, Level level, float yRot) {
        super(type, x, y, z, dx, dy, dz, level);
        this.setYRot(yRot);
    }

    public Tornado(LivingEntity owner, double dx, double dy, double dz, Level level, float damage, float yRot, float life) {
        this(LmEntityRegistry.TORNADO.get(), owner.getX(), owner.getY(), owner.getZ(), dx, dy, dz, level, yRot);
        this.setOwner(owner);
        this.setDamage(damage);
        this.setYRot(yRot);
        this.maxLife = (int) life;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(TRANSPARENCY, 0);
    }

    @Nullable
    private LivingEntity livingOwner() {
        return this.getOwner() instanceof LivingEntity living ? living : null;
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
    public void tick() {
        this.baseTick();
        this.DamageAndGrab();

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.75D, this.getBbHeight(), 0.75D), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (entity.isShiftKeyDown()) {
                entity.setShiftKeyDown(false);
            }
        }

        if (this.lifetick >= this.maxLife - 3) {
            for (Entity passenger : this.getPassengers()) {
                passenger.stopRiding();
            }
            this.ejectPassengers();
            this.discard();
        }
        if (this.tickCount > 100 && !this.hasHurt) {
            this.discard();
            this.ejectPassengers();
        }
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        if (!this.level().isClientSide) {
            ++this.lifetick;
            this.setTransparency(this.lifetick);
            if (this.lifetick >= this.maxLife) {
                this.ejectPassengers();
                this.discard();
            }
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }
        this.checkInsideBlocks();

        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        if (!this.hasHurt) {
            this.setDeltaMovement(vec3.add(this.xPower, 0.0D, this.zPower).scale(this.getInertia()));
        }
        this.setPos(d0, d1, d2);

        BlockPos pos = this.blockPosition();
        BlockState inside = this.level().getBlockState(pos);
        BlockState below = this.level().getBlockState(pos.below());
        if (below.getBlock() instanceof FireBlock || inside.getBlock() instanceof FireBlock) {
            this.onFire = true;
        }
        LivingEntity owner = this.livingOwner();
        if (owner != null && !owner.isAlive()) {
            this.discard();
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        if (this.level().isClientSide) {
            this.spawnParticles(this.onFire);
        }
    }

    private void spawnParticles(boolean fire) {
        ParticleOptions particle = fire ? ModParticles.TORNADO_FIRE.get() : ModParticles.TORNADO.get();
        float spawnPercent = 1.5F;
        float maxY = 3.7124999F;
        float y = 0.0F;
        float dY = maxY / (40.0F * spawnPercent);
        double posX = this.getX();
        double posY = this.getY();
        double posZ = this.getZ();
        float a = 0.0F;
        float dA = 4.3982296F / 10.0F;
        while (y < maxY) {
            float radius = y * 0.35F;
            float cosA = Mth.cos(a) * radius;
            float sinA = Mth.sin(a) * radius;
            if ((int) (y / dY) % 2 == 0) {
                this.level().addParticle(particle, posX + (double) cosA, posY + (double) y - (double) maxY * 0.08D,
                        posZ + (double) sinA, 0.0D, 0.0D, 0.0D);
            }
            y += dY;
            a += dA;
        }
    }

    public void grab() {
        LivingEntity owner = this.livingOwner();
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.75D, this.getBbHeight(), 0.75D), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        for (LivingEntity livingentity : entities) {
            if (owner == null || MobUtil.areAllies(owner, livingentity)) {
                continue;
            }
            if (!livingentity.hurt(this.damageSources().mobAttack(owner), 1.0F) || this.hasHurt) {
                continue;
            }
            this.hasHurt = true;
            if (livingentity.isShiftKeyDown()) {
                livingentity.setShiftKeyDown(false);
            }
            if (!this.getPassengers().isEmpty() || this.level().isClientSide) {
                continue;
            }
            livingentity.startRiding(this);
        }
    }

    public void DamageAndGrab() {
        LivingEntity owner = this.livingOwner();
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.75D, this.getBbHeight(), 0.75D), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        for (LivingEntity livingentity : entities) {
            if (livingentity.isShiftKeyDown()) {
                livingentity.setShiftKeyDown(false);
            }
            if (owner == null) {
                continue;
            }
            if (livingentity instanceof Player && this.hasHurt) {
                continue;
            }
            if (livingentity.isPassenger() || livingentity == owner
                    || MobUtil.areAllies(owner, livingentity) || !livingentity.isAlive()) {
                continue;
            }
            this.hasHurt = true;
            if (!livingentity.startRiding(this)) {
                livingentity.setPos(this.getX(), this.getY() + 3.0D, this.getZ());
            }
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.onFire) {
                livingentity.setRemainingFireTicks(60);
            }
            if (!this.AdjustedLife && owner instanceof Mob mob && mob.getTarget() != null) {
                if (!(livingentity instanceof Player player && player.isBlocking())) {
                    livingentity.hurt(this.damageSources().mobProjectile(this, owner), this.getDamage());
                }
            }
            this.AdjustedLife = true;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            Entity entity = result.getEntity();
            LivingEntity owner = this.livingOwner();
            if (owner != null && !MobUtil.areAllies(owner, entity)) {
                entity.hurt(this.damageSources().mobAttack(owner), this.getDamage());
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                this.hasHurt = true;
                entity.startRiding(this, true);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
            this.hasHurt = true;
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, (BlockState) null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult) result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos,
                    GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) {
            return false;
        }
        Entity owner = this.getOwner();
        return owner == null || this.leftOwner || !owner.isPassengerOfSameVehicle(entity);
    }

    @Override
    protected float getInertia() {
        return 0.85F;
    }

    private boolean checkLeftOwner() {
        Entity owner = this.getOwner();
        if (owner != null) {
            for (Entity entity : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
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
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }
}
