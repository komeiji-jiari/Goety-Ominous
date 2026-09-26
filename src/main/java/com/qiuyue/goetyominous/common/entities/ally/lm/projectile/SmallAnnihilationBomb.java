package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class SmallAnnihilationBomb extends ThrowableProjectile {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(SmallAnnihilationBomb.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TURN_RATE =
            SynchedEntityData.defineId(SmallAnnihilationBomb.class, EntityDataSerializers.FLOAT);

    public SmallAnnihilationBomb(EntityType<? extends SmallAnnihilationBomb> type, Level level) {
        super(type, level);
    }

    public SmallAnnihilationBomb(Level level, LivingEntity thrower, float damage) {
        super(LmEntityRegistry.SMALL_ANNIHILATION_BOMB.get(), thrower, level);
        this.setOwner(thrower);
        this.setDamage(damage);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(TURN_RATE, 0.075F);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getTurnRate() {
        return this.entityData.get(TURN_RATE);
    }

    public void setTurnRate(float turnRate) {
        this.entityData.set(TURN_RATE, turnRate);
    }

    public boolean isTurning() {
        return this.getTurnRate() == 0.0F;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        if (!(this.getOwner() instanceof LivingEntity owner) || !(target instanceof LivingEntity living)) {
            return;
        }
        if (target == owner || MobUtil.areAllies(owner, living)) {
            return;
        }
        living.hurt(ModDamageTypes.causeAnnihilationDamage(owner, owner),
                (float) ((this.getDamage() + MathUtils.entityBasedHpDamage(living, 3.0F))
                        * ModConfig.MOB_CONFIG.FlameDrifterDamageMutliplier.get()));
    }

    @Override
    public void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.ANNIHILATION_FLAME_STRIKE.get(),
                    this.getX(), this.getY() + 2.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.discard();
        }
        this.playSound(ModSounds.DIMENSIONAL_BOMB_EXPLODE_SMALL.get(), 0.1F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.level().addParticle(ModParticles.BIG_ANNIHILATION_FLAME.get(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        if (this.isTurning()) {
            Vec3 motion = this.getDeltaMovement();
            Vec3 newMotion = motion.yRot(this.getTurnRate());
            this.setDeltaMovement(newMotion);
            this.setYRot((float) (Mth.atan2(newMotion.z, newMotion.x) * (180.0D / Math.PI)) - 90.0F);
            this.setXRot((float) (Mth.atan2(newMotion.y, Math.sqrt(newMotion.x * newMotion.x + newMotion.z * newMotion.z)) * (180.0D / Math.PI)));
        }
        if (this.level().isClientSide) {
            double dx = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
            double dy = this.getY() + 1.5F * (this.random.nextFloat() - 0.5F);
            double dz = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
            float g = 0.7647059F + this.random.nextFloat() * 0.4F;
            this.level().addParticle(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.2F, 0.5F, this.getId()),
                    dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.tickCount = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getGravity() {
        return 0.03F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
