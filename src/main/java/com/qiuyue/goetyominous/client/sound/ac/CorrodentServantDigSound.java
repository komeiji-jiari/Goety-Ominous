package com.qiuyue.goetyominous.client.sound.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.CorrodentServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class CorrodentServantDigSound extends AbstractTickableSoundInstance {

    private final CorrodentServant servant;

    private float moveFade = 0;

    public CorrodentServantDigSound(CorrodentServant servant) {
        super(ACSoundRegistry.CORRODENT_DIG_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.servant = servant;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = servant.getX();
        this.y = servant.getY();
        this.z = servant.getZ();
        this.delay = 0;
    }

    @Override
    public boolean canPlaySound() {
        return this.servant.isAlive() && !this.servant.isSilent() && this.servant.isDigging();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.servant.isAlive() && this.servant.isDigging()) {
            this.x = this.servant.getX();
            this.y = this.servant.getY();
            this.z = this.servant.getZ();
            float f = (float) this.servant.getDeltaMovement().length();
            this.pitch = 1.0F;
            if (f <= 0.01F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            } else {
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            this.volume = 1.0F - this.moveFade;
        } else {
            this.stop();
        }
    }

    public boolean isSameEntity(CorrodentServant servant) {
        return this.servant.isAlive() && this.servant.getId() == servant.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
