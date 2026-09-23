package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class SkyvernServantLoopSound extends AbstractTickableSoundInstance {

    private static final float MAX_SPEED = 0.5F;
    private static final float MIN_PITCH = 0.75F;
    private static final float MAX_PITCH = 1.5F;
    private static final float MIN_VOLUME = 0.2F;
    private static final float MAX_VOLUME = 1.0F;

    private final SkyvernServant skyvern;

    public SkyvernServantLoopSound(SkyvernServant skyvern) {
        super(OPSoundEvents.SKYVERN_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.skyvern = skyvern;
        this.x = (float) skyvern.getX();
        this.y = (float) skyvern.getY();
        this.z = (float) skyvern.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public void tick() {
        if (this.skyvern.isRemoved()) {
            this.stop();
            return;
        }
        this.x = (float) this.skyvern.getX();
        this.y = (float) this.skyvern.getY();
        this.z = (float) this.skyvern.getZ();
        float speed = Mth.clamp((float) this.skyvern.getDeltaMovement().horizontalDistance() / MAX_SPEED, 0.0F, 1.0F);
        if (this.skyvern.isAlive()) {
            this.pitch = Mth.lerp(speed, MIN_PITCH, MAX_PITCH);
            this.volume = Mth.lerp(speed, MIN_VOLUME, MAX_VOLUME);
        } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
        }
    }

    @Override
    public boolean canPlaySound() {
        return !this.skyvern.isSilent() && this.skyvern.isAlive();
    }

    public boolean isSameEntity(SkyvernServant skyvern) {
        return this.skyvern.isAlive() && this.skyvern.getId() == skyvern.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
