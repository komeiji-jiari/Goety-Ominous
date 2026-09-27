package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.IAnimatedBoss.TheObliterator.TheObliteratorUtils;
import net.miauczel.legendary_monsters.sound.ModSounds;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class AnnihilationBomb extends ThrowableProjectile {

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(AnnihilationBomb.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SILENT =
            SynchedEntityData.defineId(AnnihilationBomb.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BULLETS_AMOUNT =
            SynchedEntityData.defineId(AnnihilationBomb.class, EntityDataSerializers.INT);

    public AnnihilationBomb(EntityType<? extends AnnihilationBomb> type, Level world) {
        super(type, world);
    }

    public AnnihilationBomb(EntityType<? extends AnnihilationBomb> type, Level world, LivingEntity thrower,
                            float damage, int bulletsAmount, boolean silent) {
        super(type, thrower, world);
        this.setOwner(thrower);
        this.setDamage(damage);
        this.setBulletsAmount(bulletsAmount);
        this.setSilent1(silent);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(BULLETS_AMOUNT, 0);
        this.entityData.define(SILENT, false);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public boolean isSilent1() {
        return this.entityData.get(SILENT);
    }

    public void setSilent1(boolean silent) {
        this.entityData.set(SILENT, silent);
    }

    public float getBulletAmount() {
        return this.entityData.get(BULLETS_AMOUNT);
    }

    public void setBulletsAmount(int bulletsAmount) {
        this.entityData.set(BULLETS_AMOUNT, bulletsAmount);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner == null || target == owner) {
            return;
        }
        if (target instanceof TamableAnimal animal && animal.getOwner() == owner) {
            return;
        }
        if (target instanceof LivingEntity living && owner instanceof LivingEntity livingOwner) {
            if (MobUtil.areAllies(livingOwner, living)) {
                return;
            }
            living.hurt(ModDamageTypes.causeAnnihilationDamage(livingOwner, livingOwner), this.getDamage());
            TheObliteratorUtils.applyAnnihilationEffect(living, ModEffects.ANNIHILATION.get(), 1, false);
        }
    }

    @Override
    public void onHit(HitResult result) {
        super.onHit(result);
        if (this.isSilent1()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.DIMENSIONAL_BOMB_EXPLODE.get(),
                    this.getSoundSource(), 0.3F, 1.0F, false);
        } else {
            this.playSound(ModSounds.DIMENSIONAL_BOMB_EXPLODE.get(), 1.0F, 1.0F);
        }
        float f3 = (this.random.nextFloat() - 0.5F) * 4.0F;
        float f4 = (this.random.nextFloat() - 0.5F) * 4.0F;
        float f5 = (this.random.nextFloat() - 0.5F) * 4.0F;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.GROUND_ANNIHILATION_NUKE.get(),
                    this.getX() + f3, this.getY() + 2.0D + f4, this.getZ() + f5, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ModParticles.GROUND_ANNIHILATION_NUKE.get(),
                    this.getX() + f3, this.getY() + 2.0D + f4, this.getZ() + f5, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        LivingEntity entity = (LivingEntity) this.getOwner();
        float smallBulletAmount = this.getBulletAmount() * 0.5F;
        for (int i = 0; (float) i < smallBulletAmount; ++i) {
            if (entity != null) {
                float throwAngle = (float) i * (float) Math.PI / (smallBulletAmount / 2.0F);
                double sx = this.getX() + Mth.cos(throwAngle) * 1.0F;
                double sy = this.getY() + this.getBbHeight() * 0.2D;
                double sz = this.getZ() + Mth.sin(throwAngle) * 1.0F;
                double vx = Mth.cos(throwAngle);
                double vy = entity.getRandom().nextFloat() * 0.3F;
                double vz = Mth.sin(throwAngle);
                double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
                SmallAnnihilationBomb projectile = new SmallAnnihilationBomb(entity.level(), entity, 8.0F);
                projectile.moveTo(sx, sy, sz, i * 11.25F, this.getXRot());
                projectile.shoot(vx, vy + v3 * 2.0D, vz, 0.5F, 1.0F);
                entity.level().addFreshEntity(projectile);
            }
        }
        for (int i = 0; (float) i < this.getBulletAmount(); ++i) {
            if (entity != null) {
                float throwAngle = (float) i * (float) Math.PI / (this.getBulletAmount() / 2.0F);
                double sx = this.getX() + Mth.cos(throwAngle) * 1.0F;
                double sy = this.getY() + this.getBbHeight() * 0.2D;
                double sz = this.getZ() + Mth.sin(throwAngle) * 1.0F;
                double vx = Mth.cos(throwAngle);
                double vy = entity.getRandom().nextFloat() * 0.3F;
                double vz = Mth.sin(throwAngle);
                double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
                SmallAnnihilationBomb projectile = new SmallAnnihilationBomb(entity.level(), entity, 8.0F);
                projectile.moveTo(sx, sy, sz, i * 11.25F, this.getXRot());
                projectile.shoot(vx, vy + v3 * 0.2F, vz, 0.7F, 1.0F);
                entity.level().addFreshEntity(projectile);
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.ANNIHILATION_FLAME_STRIKE.get(),
                    this.getX(), this.getY() + 2.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.tickCount % 3 == 0) {
                int i = 0;
                while ((double) i < 0.1) {
                    this.level().addParticle(ModParticles.BIG_ANNIHILATION_FLAME.get(),
                            this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.025D, 0.0D);
                    ++i;
                }
            }
            double dx = this.getX() + 1.5F * (this.random.nextFloat() - 0.5F);
            double dy = this.getY() + 1.5F * (this.random.nextFloat() - 0.5F);
            double dz = this.getZ() + 1.5F * (this.random.nextFloat() - 0.5F);
            float g = 0.7647059F + this.random.nextFloat() * 0.4F;
            this.level().addParticle(new AnnihilationBombTrail.OrbData(0.0F, g, 0.0F, 0.5F, 0.8F, this.getId()),
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
