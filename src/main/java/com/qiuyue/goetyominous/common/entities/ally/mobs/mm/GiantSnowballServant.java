package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.alexander.mutantmore.entities.IceCube;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.init.EntityTypeInit;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            MmScreenShake.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        } else {
            for (Entity entity : this.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), null, this.getBoundingBox().inflate(2.0))) {
                if (entity == this.getOwner() || !this.canHitEntity(entity)) continue;
                IceCube iceCube = EntityTypeInit.ICE_CUBE.get().create(this.level());
                iceCube.moveTo(entity.position());
                iceCube.setTarget(entity);
                this.level().addFreshEntity(iceCube);
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

    protected void defineSynchedData() {
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
