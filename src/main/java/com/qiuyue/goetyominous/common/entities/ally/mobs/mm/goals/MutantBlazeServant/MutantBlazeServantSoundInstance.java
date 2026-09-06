package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class MutantBlazeServantSoundInstance extends AbstractTickableSoundInstance {
    protected final MutantBlazeServant mutantBlaze;
    public float minPitch;
    public float maxVolume;
    public float maxPitch;
    public boolean speedAffectsSound;
    public float alterableVolume;
    public float alterablePitch;

    public MutantBlazeServantSoundInstance(MutantBlazeServant blaze, SoundEvent sound, SoundSource source,
                                           float minPitch, float maxVolume, float maxPitch, boolean speedAffectsSound,
                                           float alterableVolume, float alterablePitch) {
        super(sound, source, SoundInstance.createUnseededRandom());
        this.mutantBlaze = blaze;
        this.minPitch = minPitch;
        this.maxVolume = maxVolume;
        this.maxPitch = maxPitch;
        this.speedAffectsSound = speedAffectsSound;
        this.alterableVolume = alterableVolume;
        this.alterablePitch = alterablePitch;
        this.x = blaze.getX();
        this.y = blaze.getY();
        this.z = blaze.getZ();
        this.relative = true;
        this.attenuation = Attenuation.NONE;
        this.volume = speedAffectsSound ? 0.0F : alterableVolume;
        this.pitch = alterablePitch;
    }

    @Override
    public void tick() {
        if (this.mutantBlaze.isRemoved()) {
            this.stop();
            return;
        }
        this.x = this.mutantBlaze.getX();
        this.y = this.mutantBlaze.getY();
        this.z = this.mutantBlaze.getZ();
        if (!this.mutantBlaze.shouldHeat()) {
            if (this.pitch > 0.0F) this.pitch -= 0.025F;
            if (this.volume > 0.0F) this.volume -= 0.05F;
        } else {
            if (this.pitch < this.alterablePitch) {
                this.pitch += 0.025F;
            } else if (this.pitch > this.alterablePitch) {
                this.pitch = this.alterablePitch;
            }
            if (this.volume < this.alterableVolume) {
                this.volume += 0.05F;
            } else if (this.volume > this.alterableVolume) {
                this.volume = this.alterableVolume;
            }
        }
    }

    @Override
    public boolean isLooping() {
        return true;
    }

    @Override
    public boolean isRelative() {
        return !this.mutantBlaze.isSilent();
    }
}
