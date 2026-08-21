package com.qiuyue.goetyominous.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class NucleeperServantSirenSound extends AbstractTickableSoundInstance {

    private final NucleeperServant nucleeper;

    public NucleeperServantSirenSound(NucleeperServant nucleeper) {
        super(ACSoundRegistry.NUCLEAR_SIREN.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.nucleeper = nucleeper;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
        this.x = nucleeper.getX();
        this.y = nucleeper.getY();
        this.z = nucleeper.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !this.nucleeper.isSilent() && this.nucleeper.isTriggered();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.nucleeper.isAlive() && this.nucleeper.isTriggered()) {
            this.x = this.nucleeper.getX();
            this.y = this.nucleeper.getY();
            this.z = this.nucleeper.getZ();
            this.volume = 0.1F + this.nucleeper.getCloseProgress(1.0F) * 0.9F;
        } else {
            this.stop();
        }
    }

    public boolean isSameEntity(NucleeperServant nucleeper) {
        return this.nucleeper.getId() == nucleeper.getId() && this.nucleeper.isAlive();
    }

    public void stopSound() {
        this.stop();
    }
}
