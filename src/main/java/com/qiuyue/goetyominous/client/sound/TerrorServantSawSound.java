package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class TerrorServantSawSound extends AbstractTickableSoundInstance {

    private final TerrorServant terror;

    public TerrorServantSawSound(TerrorServant terror) {
        super(OPSoundEvents.TERROR_SAW.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.terror = terror;
        this.x = (float) terror.getX();
        this.y = (float) terror.getY();
        this.z = (float) terror.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.terror.isRemoved()) {
            this.stop();
            return;
        }
        this.x = (float) this.terror.getX();
        this.y = (float) this.terror.getY();
        this.z = (float) this.terror.getZ();
        this.pitch = this.terror.getVoicePitch();
        this.volume = 2.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.terror.isSilent() && this.terror.isAlive();
    }

    public boolean isSameEntity(TerrorServant terror) {
        return this.terror.isAlive() && this.terror.getId() == terror.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
