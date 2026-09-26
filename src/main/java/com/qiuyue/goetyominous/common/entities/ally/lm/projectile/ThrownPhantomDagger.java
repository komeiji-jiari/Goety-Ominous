package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.common.entities.ally.lm.ControlledAnim;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownPhantomDagger extends ServantFlyingProjectile {

    private static final EntityDataAccessor<Integer> RETURN_TICK =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.BOOLEAN);

    public int clientSideReturnTridentTickCount;

    private LivingEntity rEntity;

    private float interia = 1.0F;

    public float uR = 195.0F;
    public float uG = 24.0F;
    public float uB = 30.0F;

    public int lessLifeTicks = 0;

    public ControlledAnim fade = new ControlledAnim(6);

    public ThrownPhantomDagger(EntityType<? extends ThrownPhantomDagger> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RETURN_TICK, 20);
        this.entityData.define(IS_RED, false);
        this.entityData.define(DAMAGE, 0.0F);
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    public float getReturnTick() {
        return (float) (int) this.entityData.get(RETURN_TICK);
    }

    public void setReturnTick(int returnTick) {
        this.entityData.set(RETURN_TICK, returnTick);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getLessLifeTicks() {
        return this.lessLifeTicks;
    }

    public void setLessLifeTicks(int lessLifeTicks) {
        this.lessLifeTicks = lessLifeTicks;
    }

    public LivingEntity returnEntity() {
        return this.rEntity;
    }

    public void setReturnEntity(LivingEntity returnEntity) {
        if (returnEntity != null) {
            this.rEntity = returnEntity;
        }
    }

    @Override
    protected float getInertia() {
        return this.interia;
    }

    public void setInteria(float interia) {
        this.interia = interia;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        double finalTick = (double) ((float) (100 - this.getLessLifeTicks()) + this.getReturnTick());

        if ((double) this.tickCount >= finalTick - 6.0D) {
            this.fade.increaseTimer();
        }

        if ((double) this.tickCount >= finalTick && !this.level().isClientSide) {
            this.discard();
        }

        double dx = this.getX() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        double dy = this.getY() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        double dz = this.getZ() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        float r = (this.getRed() ? this.uR : 57.0F) / 255.0F;
        float g = (this.getRed() ? this.uG : 190.0F) / 255.0F;
        float b = (this.getRed() ? this.uB : 197.0F) / 255.0F;
        if (this.level().isClientSide) {
            this.level().addParticle(new PhantomDaggerTrail.OrbData(r, g, b, 0.0F, 0.25F, this.getId()),
                    dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }

        if ((float) this.tickCount >= this.getReturnTick() && this.returnEntity() != null) {
            this.noPhysics = true;
            Vec3 returnPos = new Vec3(this.returnEntity().getX(),
                    this.returnEntity().getY() + 1.0D, this.returnEntity().getZ());
            Vec3 vec3 = returnPos.subtract(this.position());

            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double) this.interia, this.getZ());
            if (this.level().isClientSide) {
                this.yOld = this.getY();
            }

            double d0 = 0.05D * (double) this.interia;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D)
                    .add(vec3.normalize().scale(d0)));
            ++this.clientSideReturnTridentTickCount;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (owner == null || target == owner) {
            return;
        }
        if (!(owner instanceof LivingEntity livingOwner) || !(target instanceof LivingEntity livingTarget)) {
            return;
        }

        if (livingOwner.isAlliedTo(livingTarget)) {
            return;
        }

        float m = ServantMath.toPercent(livingTarget.getMaxHealth());
        boolean hurt = livingTarget.hurt(LmDamageTypes.ghostly(livingOwner),
                this.getDamage() + m);

        if (hurt) {
            EntityUtil.applyStackingEffect(livingTarget, ModEffects.SOUL_FRACTURE.get(),
                    1, 4, ServantMath.toTicks(10.0F));
            livingOwner.heal(3.0F);
        }
    }
}
