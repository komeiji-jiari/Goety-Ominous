package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class VoltServantElectricChargeSound extends AbstractTickableSoundInstance {

    private final VoltServantElectricCharge charge;

    public VoltServantElectricChargeSound(VoltServantElectricCharge charge) {
        super(OPSoundEvents.ELECTRIC_CHARGE.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.charge = charge;
        this.x = (float) charge.getX();
        this.y = (float) charge.getY();
        this.z = (float) charge.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.charge.isRemoved()) {
            this.stop();
            return;
        }
        this.x = (float) this.charge.getX();
        this.y = (float) this.charge.getY();
        this.z = (float) this.charge.getZ();
        float horizontalDistance = (float) this.charge.getDeltaMovement().horizontalDistance();
        this.pitch = Mth.lerp(Mth.clamp(horizontalDistance, 0.75F, 1.25F), 0.75F, 1.25F);
        this.volume = 2.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.charge.isSilent();
    }

    public boolean isSameEntity(VoltServantElectricCharge charge) {
        return this.charge.isAlive() && this.charge.getId() == charge.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
