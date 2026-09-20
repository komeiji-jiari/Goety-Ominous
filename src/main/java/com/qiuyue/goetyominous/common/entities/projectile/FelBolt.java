package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;
import java.util.Map;

public class FelBolt extends SpellHurtingProjectile {

    private static final EntityDataAccessor<Integer> DATA_TYPE_ID =
            SynchedEntityData.defineId(FelBolt.class, EntityDataSerializers.INT);

    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), map -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/projectile/fel_bolt_1.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/projectile/fel_bolt_2.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/projectile/fel_bolt_3.png"));
    });

    private final Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    public FelBolt(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    public FelBolt(double x, double y, double z, double vx, double vy, double vz, Level level) {
        super(ModEntityTypes.FEL_BOLT.get(), x, y, z, vx, vy, vz, level);
    }

    public FelBolt(LivingEntity shooter, double vx, double vy, double vz, Level level) {
        super(ModEntityTypes.FEL_BOLT.get(), shooter, vx, vy, vz, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimation(compound.getInt("Animation"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Animation", this.getAnimation());
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    public void rotateToMatchMovement() {
        this.updateRotation();
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int type) {
        this.entityData.set(DATA_TYPE_ID, type);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            float baseDamage = SpellConfig.SoulBoltDamage.get().floatValue() * WandUtil.damageMultiply();
            Entity target = result.getEntity();
            Entity owner = this.getOwner();
            boolean flag;
            if (owner instanceof LivingEntity living) {
                if (living instanceof Mob mob
                        && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null
                        && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0.0D) {
                    baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                }
                baseDamage += this.getExtraDamage();
                flag = target.hurt(target.damageSources().indirectMagic(this, living), baseDamage);
                if (flag && target.isAlive()) {
                    this.doEnchantDamageEffects(living, target);
                }
            } else {
                flag = target.hurt(target.damageSources().magic(), baseDamage);
            }

            if (flag && target instanceof LivingEntity livingTarget) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
                MobEffect effect = MobEffects.POISON;
                LivingEntity livingOwner = com.Polarice3.Goety.utils.MobUtil.getOwner(this);
                if (livingOwner != null && com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneRobe(livingOwner)) {
                    effect = GoetyEffects.ACID_VENOM.get();
                }
                livingTarget.addEffect(new MobEffectInstance(effect, 100, 0));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        this.playSound(ModSounds.POISON_BOLT_IMPACT.get());
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            for (int i = 0; i < 32; ++i) {
                double d0 = this.getX() + this.level().random.nextDouble();
                double d1 = this.getY() + this.level().random.nextDouble();
                double d2 = this.getZ() + this.level().random.nextDouble();
                double[] colors = MathHelper.rgbParticle(0xA64BFF);
                serverLevel.sendParticles((SimpleParticleType) ModParticleTypes.CULT_SPELL.get(),
                        d0, d1, d2, 0, colors[0], colors[1], colors[2], 1.0D);
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimation() < 2) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }

        Entity owner = this.getOwner();
        if (this.tickCount >= MathHelper.secondsToTicks(10)) {
            this.discard();
        }

        if (this.level().isClientSide
                || (owner == null || !owner.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            Vec3 delta = this.getDeltaMovement();
            double d0 = this.getX() - delta.x;
            double d1 = this.getY() - delta.y;
            double d2 = this.getZ() - delta.z;
            this.level().addParticle(ModParticleTypes.SUMMON_TRAIL.get(),
                    d0 + this.level().random.nextDouble() / 4.0D * (double) this.level().random.nextInt(-1, 1),
                    d1 + 0.15D,
                    d2 + this.level().random.nextDouble() / 4.0D * (double) this.level().random.nextInt(-1, 1),
                    0.0D, 0.0D, 0.0D);
        }

        Vec3 trailAt = this.position().add(0.0D, (double) (this.getBbHeight() / 2.0F), 0.0D);
        if (this.trailPointer == -1) {
            Arrays.fill(this.trailPositions, trailAt);
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale((double) partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
