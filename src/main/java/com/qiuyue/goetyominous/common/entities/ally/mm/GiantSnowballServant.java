package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.entities.IceCube;
import com.qiuyue.goetyominous.compat.mm.MmBossIceCubeController;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.init.EntityTypeInit;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.utils.BossUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class GiantSnowballServant extends ThrowableProjectile {
    public GiantSnowballServant(EntityType<? extends GiantSnowballServant> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public GiantSnowballServant(Level p_37399_, LivingEntity p_37400_) {
        super(MmEntityRegistry.GIANT_SNOWBALL_SERVANT.get(), p_37400_, p_37399_);
    }

    public GiantSnowballServant(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
        super(MmEntityRegistry.GIANT_SNOWBALL_SERVANT.get(), p_37395_, p_37396_, p_37397_, p_37394_);
    }

    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            MmScreenShake.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        } else {
            LivingEntity root = this.getOwner() instanceof IOwned ownedOwner
                    ? ownedOwner.getMasterOwner()
                    : this.getOwner() instanceof LivingEntity living ? living : null;
            for (Entity entity : this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), null, this.getBoundingBox().inflate(2.0))) {
                if (entity == this.getOwner() || !this.canHitEntity(entity)) continue;
                if (root != null && (entity == root
                        || entity instanceof IOwned owned && owned.getMasterOwner() == root
                        || entity instanceof OwnableEntity ownable && ownable.getOwner() == root
                        || MobUtil.areAllies(root, entity))) {
                    continue;
                }
                if (this.extraDamage > 0.0F) {
                    entity.hurt(this.damageSources().indirectMagic(this,
                            this.getOwner() instanceof LivingEntity attacker ? attacker : null), this.extraDamage);
                }
                IceCube iceCube = EntityTypeInit.ICE_CUBE.get().create(this.level());
                iceCube.moveTo(entity.position());
                iceCube.setTarget(entity);
                this.level().addFreshEntity(iceCube);
                if (BossUtil.isBoss(entity)) {
                    MmBossIceCubeController.track((ServerLevel) this.level(), iceCube);
                }
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.discard();
                if (this.getOwner() == null || !(this.getOwner() instanceof MutantFrozenZombieServant zombie) || zombie.getTarget() == null || entity != zombie.getTarget()) continue;
                zombie.wantsToJump = true;
            }
        }
    }

    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 4) {
            for (int i = 0; i < 80; ++i) {
                this.level().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }

    boolean canHit(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_FROZEN_ZOMBIE_GIANT_SNOWBALL_CANT_HIT, this, target, this.getOwner(), entity -> entity instanceof MutantFrozenZombieServant)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantFrozenZombieServant));
    }

    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private static final EntityDataAccessor<Float> SIZE_SCALE =
            SynchedEntityData.defineId(GiantSnowballServant.class, EntityDataSerializers.FLOAT);

    private float extraDamage;

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getSizeScale());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SIZE_SCALE.equals(key)) {
            this.refreshDimensions();
        }
    }

    public void setSizeScale(float scale) {
        this.entityData.set(SIZE_SCALE, scale);
        this.refreshDimensions();
    }

    public float getSizeScale() {
        return this.entityData.get(SIZE_SCALE);
    }

    public void setExtraDamage(float damage) {
        this.extraDamage = damage;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE_SCALE, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("SizeScale", this.getSizeScale());
        tag.putFloat("ExtraDamage", this.extraDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SizeScale")) {
            this.setSizeScale(tag.getFloat("SizeScale"));
        }
        this.extraDamage = tag.getFloat("ExtraDamage");
    }
}
