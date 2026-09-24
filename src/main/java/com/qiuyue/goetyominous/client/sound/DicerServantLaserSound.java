package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class DicerServantLaserSound extends AbstractTickableSoundInstance {

    private final DicerServantLaser laser;

    public DicerServantLaserSound(DicerServantLaser laser) {
        super(OPSoundEvents.DICER_LASER.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.laser = laser;
        this.x = (float) laser.getX();
        this.y = (float) laser.getY();
        this.z = (float) laser.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.laser.isRemoved()) {
            this.stop();
            return;
        }
        this.x = (float) this.laser.getX();
        this.y = (float) this.laser.getY();
        this.z = (float) this.laser.getZ();
        this.pitch = 1.0F;
        this.volume = 4.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.laser.isSilent() && this.laser.isAlive();
    }

    public boolean isSameEntity(DicerServantLaser laser) {
        return this.laser.isAlive() && this.laser.getId() == laser.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
