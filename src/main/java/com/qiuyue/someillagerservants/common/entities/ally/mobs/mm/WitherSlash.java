package com.qiuyue.someillagerservants.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class WitherSlash extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> SIZE;
    private static final EntityDataAccessor<Float> FIXED_YAW;
    public int textureChange = 0;
    public float damage = 0.0F;
    public float leechAmount = 0.0F;
    public int witherLength = 0;
    public int witherLevel = 0;
    public boolean ignoresInvulTime = true;
    public List<Entity> alreadyHit = Lists.newArrayList();

    public WitherSlash(EntityType<? extends WitherSlash> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public WitherSlash(Level p_37399_, LivingEntity p_37400_, float yRot) {
        super(com.qiuyue.someillagerservants.common.init.mm.MmEntityRegistry.WITHER_SLASH.get(), p_37400_, p_37399_);
        this.setFixedYaw(yRot);
    }

    protected float getGravity() {
        return 0.0F;
    }

    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            try {
                Class<?> handleLoopingSoundInstancesClass = Class.forName("com.alexander.mutantmore.util.HandleLoopingSoundInstances");
                Method addFireSlashAudioMethod = handleLoopingSoundInstancesClass.getDeclaredMethod("addFireSlashAudio",
                        com.alexander.mutantmore.entities.WitherSlash.class, Level.class);
                addFireSlashAudioMethod.setAccessible(true);

                Entity owner = this.getOwner();
                if (owner instanceof LivingEntity) {
                    com.alexander.mutantmore.entities.WitherSlash mutantMoreWitherSlash = new com.alexander.mutantmore.entities.WitherSlash(
                            this.level(),
                            (LivingEntity) owner,
                            this.getFixedYaw());
                    mutantMoreWitherSlash.setPos(this.getX(), this.getY(), this.getZ());
                    mutantMoreWitherSlash.setDeltaMovement(this.getDeltaMovement());

                    addFireSlashAudioMethod.invoke(null, mutantMoreWitherSlash, this.level());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EntityDimensions getDimensions(Pose p_19975_) {
        return super.getDimensions(p_19975_).scale(this.getSize());
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(SIZE, 1.0F);
        this.entityData.define(FIXED_YAW, 0.0F);
    }

    public float getSize() {
        return (Float)this.entityData.get(SIZE);
    }

    public void setSize(float value) {
        this.entityData.set(SIZE, value);
    }

    public float getFixedYaw() {
        return (Float)this.entityData.get(FIXED_YAW);
    }

    public void setFixedYaw(float yaw) {
        if (!this.level().isClientSide) {
            this.entityData.set(FIXED_YAW, yaw);
        }

    }

    public void tick() {
        if (!this.level().isClientSide && this.tickCount > 160) {
            this.discard();
            return;
        }
        Vec3 deltaMovementO = this.getDeltaMovement();
        super.tick();
        this.refreshDimensions();
        this.setDeltaMovement(deltaMovementO);
        Iterator var2 = this.level().getEntities(this, this.getBoundingBox()).iterator();

        while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (!this.level().isClientSide && this.canHitEntity(entity)) {
                Entity target = entity;
                Entity owner = this.getOwner();
                if (!this.alreadyHit.contains(target) && this.canHarm(target)) {
                    if (this.ignoresInvulTime) {
                        target.invulnerableTime = 0;
                    }

                    boolean flag = target.hurt(MMDamageTypes.witherSlashAttack(this.damageSources(), this, owner), this.damage);
                    LivingEntity livingTarget;
                    if (target instanceof LivingEntity) {
                        livingTarget = (LivingEntity)target;
                        if (flag) {
                            if (owner instanceof LivingEntity) {
                                LivingEntity livingOwner = (LivingEntity)owner;
                                MiscUtils.witherLeech(livingOwner, this.leechAmount, new Vec3(livingTarget.getX(), livingTarget.getY(0.75), livingTarget.getZ()));
                            }

                            livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, this.witherLength, this.witherLevel), owner);
                        }

                        flag = true;
                    } else {
                        flag = false;
                    }

                    if (owner instanceof LivingEntity) {
                        livingTarget = (LivingEntity)owner;
                        this.alreadyHit.add(target);
                        this.doEnchantDamageEffects(livingTarget, target);
                    }
                }
            }
        }

        if (this.level().isClientSide) {
            ShakeCameraEvent.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        }

        int particleColour = 5254710;
        double d0 = (double)((float)(particleColour >> 16 & 255) / 255.0F);
        double d1 = (double)((float)(particleColour >> 8 & 255) / 255.0F);
        double d2 = (double)((float)(particleColour & 255) / 255.0F);
        this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(1.0), this.getY(), this.getRandomZ(1.0), d0, d1, d2);
        if (this.tickCount % 5 == 0) {
            ++this.textureChange;
        }

    }

    protected void onHit(HitResult p_37388_) {
        super.onHit(p_37388_);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }

        if (tag.contains("Size")) {
            this.setSize(tag.getFloat("Size"));
        }

        if (tag.contains("LeechAmount")) {
            this.leechAmount = tag.getFloat("LeechAmount");
        }

        if (tag.contains("WitherLength")) {
            this.witherLength = tag.getInt("WitherLength");
        }

        if (tag.contains("WitherLevel")) {
            this.witherLevel = tag.getInt("WitherLevel");
        }

        if (tag.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        }

    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putFloat("Size", this.getSize());
        tag.putFloat("LeechAmount", this.leechAmount);
        tag.putInt("WitherLength", this.witherLength);
        tag.putInt("WitherLevel", this.witherLevel);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
    }

    public boolean isAttackable() {
        return false;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    boolean canHarm(Entity target) {
        Entity owner = this.getOwner();

        if (owner == null) {
            return true;
        }

        if (target == owner) {
            return false;
        }

        if (target instanceof LivingEntity && MobUtil.areAllies(owner, (LivingEntity)target)) {
            return false;
        }

        return true;
    }

    boolean canHit(Entity target) {
        return this.canHarm(target);
    }

    static {
        SIZE = SynchedEntityData.defineId(WitherSlash.class, EntityDataSerializers.FLOAT);
        FIXED_YAW = SynchedEntityData.defineId(WitherSlash.class, EntityDataSerializers.FLOAT);
    }
}