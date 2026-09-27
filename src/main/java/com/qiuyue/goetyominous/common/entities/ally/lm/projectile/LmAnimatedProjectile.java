package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class LmAnimatedProjectile extends Entity {

    public static final EntityDataAccessor<Integer> ANIMATIONSTATE =
            SynchedEntityData.defineId(LmAnimatedProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(LmAnimatedProjectile.class, EntityDataSerializers.FLOAT);

    public int AnimationTicks = 0;
    public final ControlledAnim controlledAnim = new ControlledAnim(this.disappearTicks());
    public AnimationState emergeAnimationState = new AnimationState();

    public LmAnimatedProjectile(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public int disappearTicks() {
        return 0;
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public int getAnimationTicks() {
        return this.AnimationTicks;
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATIONSTATE);
    }

    public void setAnimationState(int animation) {
        this.AnimationTicks = 0;
        this.entityData.set(ANIMATIONSTATE, animation);
    }

    public void stopAllAnimationStates() {
        this.emergeAnimationState.stop();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ANIMATIONSTATE, 0);
        this.entityData.define(DAMAGE, 0.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ANIMATIONSTATE.equals(key) && this.level().isClientSide) {
            switch (this.getAnimationState()) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.emergeAnimationState.startIfStopped(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 1) {
            this.AnimationTicks = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimationState() > 0) {
            ++this.AnimationTicks;
        }
    }
}
