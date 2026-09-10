package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FrostBallEntity extends AbstractHurtingProjectile {
    private static final int MAX_LIFETIME = 160;
    private static final EntityDataAccessor<Integer> DATA_ANIMATION =
            SynchedEntityData.defineId(FrostBallEntity.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = new HashMap<>();
    static {
        for (int i = 0; i < 8; i++) {
            TEXTURE_BY_TYPE.put(i, new ResourceLocation(GoetyOminous.MOD_ID,
                    "textures/entity/frost/frost_" + i + ".png"));
        }
    }

    private final Set<Integer> piercedEntities = new HashSet<>();

    public FrostBallEntity(EntityType<? extends FrostBallEntity> type, Level level) {
        super(type, level);
    }

    public FrostBallEntity(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(ModEntityTypes.FROST_BALL.get(), shooter, dx, dy, dz, level);
    }

    public FrostBallEntity(Level level, double x, double y, double z, double dx, double dy, double dz) {
        super(ModEntityTypes.FROST_BALL.get(), x, y, z, dx, dy, dz, level);
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANIMATION, 0);
    }

    public int getAnimation() { return this.entityData.get(DATA_ANIMATION); }
    public void setAnimation(int type) { this.entityData.set(DATA_ANIMATION, type); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Animation", this.getAnimation());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAnimation(tag.getInt("Animation"));
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    public void tick() {
        if (this.isOnFire()) {
            this.clearFire();
        }
        super.tick();
        if (this.getAnimation() < 7) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        if (!this.level().isClientSide && this.tickCount >= MAX_LIFETIME) {
            this.level().broadcastEntityEvent(this, (byte) 102);
            this.playSound(SoundEvents.SNOW_BREAK, 1.0F, 1.0F);
            this.discard();
        }
    }

    private boolean ownerHasFrostRobes() {
        Entity owner = this.getOwner();
        if (owner instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            LivingEntity master = owned.getMasterOwner();
            return master != null && CuriosFinder.hasFrostRobes(master);
        }
        return owner instanceof LivingEntity living && CuriosFinder.hasFrostRobes(living);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        boolean pierce = this.ownerHasFrostRobes();
        Entity target = result.getEntity();
        if (pierce && !this.piercedEntities.add(target.getId())) {
            return;
        }
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            float damage = 4.0F;
            int duration = 0;
            boolean hurt;
            if (owner instanceof Mob mob) {
                damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                hurt = target.hurt(ModDamageSource.indirectFreeze(target, this), damage);
            } else if (owner instanceof Player) {
                hurt = target.hurt(ModDamageSource.indirectFreeze(target, this), damage);
            } else if (owner != null) {
                hurt = target.hurt(ModDamageSource.indirectFreeze(target, this), damage);
            } else {
                hurt = target.hurt(this.damageSources().freeze(), damage);
            }
            if (hurt && target instanceof LivingEntity living) {
                int extraAmp = pierce ? 1 : 0;
                if (!living.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300 + duration, extraAmp));
                } else if (this.random.nextFloat() <= 0.01F) {
                    EffectsUtil.amplifyEffect(living, MobEffects.MOVEMENT_SLOWDOWN, 300 + duration);
                } else {
                    EffectsUtil.resetDuration(living, MobEffects.MOVEMENT_SLOWDOWN, 300 + duration);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        boolean pierce = this.ownerHasFrostRobes();
        super.onHit(result);
        if (this.level().isClientSide) {
            return;
        }
        if (pierce && result instanceof EntityHitResult) {
            return;
        }
        this.level().broadcastEntityEvent(this, (byte) 102);
        this.playSound(SoundEvents.SNOW_BREAK, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 102) {
            for (int i = 0; i < 16; ++i) {
                this.level().addParticle(ParticleTypes.SNOWFLAKE,
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0, 0);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
