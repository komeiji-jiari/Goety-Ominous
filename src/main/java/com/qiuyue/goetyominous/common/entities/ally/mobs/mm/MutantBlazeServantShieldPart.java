package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.alexander.mutantmore.init.ParticleTypeInit;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class MutantBlazeServantShieldPart extends PartEntity<MutantBlazeServant> {
    public final MutantBlazeServant parentMob;
    public int partNumber;

    public MutantBlazeServantShieldPart(MutantBlazeServant parent, int partNumber) {
        super(parent);
        this.parentMob = parent;
        this.partNumber = partNumber;
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.parentMob.shieldsInactive() && !this.parentMob.isShieldBroken(this.partNumber);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.parentMob.shieldsInactive()) {
            return false;
        }
        if (this.parentMob.isShieldBroken(this.partNumber)) {
            return false;
        }
        if (source.getEntity() instanceof LivingEntity attacker
                && com.Polarice3.Goety.utils.MobUtil.areAllies(this.parentMob, attacker)) {
            return false;
        }
        if (!this.level().isClientSide) {
            if (this.parentMob.onShieldPartHit(this.partNumber)) {
                for (int i = 0; i < 20; ++i) {
                    ((ServerLevel) this.level()).sendParticles(ParticleTypeInit.BROKEN_MUTANT_BLAZE_SHIELD.get(),
                            this.getRandomX(0.75D), this.getY(), this.getRandomZ(0.75D), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.parentMob.setShieldBroken(this.partNumber, true);
            }
        }
        return source.getDirectEntity() instanceof AbstractArrow arrow
                && arrow.getPierceLevel() > 0
                && !this.isInvulnerableTo(source);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(1.21875F, 1.875F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }
}
