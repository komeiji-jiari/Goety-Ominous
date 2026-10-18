package com.qiuyue.goetyominous.client.sound.of;

import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class FrowzyServantPantingSound extends AbstractTickableSoundInstance {

    private final FrowzyServant frowzy;

    public FrowzyServantPantingSound(FrowzyServant frowzy) {
        super(OPSoundEvents.FROWZY_RUN.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.frowzy = frowzy;
        this.x = (float) frowzy.getX();
        this.y = (float) frowzy.getY();
        this.z = (float) frowzy.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.frowzy.isRemoved()) {
            this.stop();
            return;
        }
        this.x = (float) this.frowzy.getX();
        this.y = (float) this.frowzy.getY();
        this.z = (float) this.frowzy.getZ();
        this.pitch = this.frowzy.getVoicePitch();
        this.volume = 1.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.frowzy.isSilent() && this.frowzy.isAlive() && this.frowzy.isAggressive();
    }

    public boolean isSameEntity(FrowzyServant frowzy) {
        return this.frowzy.isAlive() && this.frowzy.getId() == frowzy.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
