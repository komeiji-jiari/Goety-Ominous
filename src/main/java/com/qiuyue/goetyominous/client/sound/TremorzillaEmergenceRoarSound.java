package com.qiuyue.goetyominous.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class TremorzillaEmergenceRoarSound extends AbstractTickableSoundInstance {

    private static final int FADE_IN_TICKS = 4;
    private static final int HOLD_TICKS = 56;
    private static final int FADE_OUT_TICKS = 20;
    private static final int LIFETIME = HOLD_TICKS + FADE_OUT_TICKS;
    private static final float VOLUME = 8.0F;

    private int age;

    private TremorzillaEmergenceRoarSound(TremorzillaServant servant) {
        super(ACSoundRegistry.TREMORZILLA_ROAR.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.x = servant.getX();
        this.y = servant.getEyeY();
        this.z = servant.getZ();
        this.volume = VOLUME / (float) FADE_IN_TICKS;
    }

    public static void play(TremorzillaServant servant) {
        Minecraft.getInstance().getSoundManager().play(new TremorzillaEmergenceRoarSound(servant));
    }

    @Override
    public void tick() {
        ++this.age;
        if (this.age > LIFETIME) {
            this.stop();
            return;
        }
        float ramp;
        if (this.age <= FADE_IN_TICKS) {
            ramp = (float) this.age / (float) FADE_IN_TICKS;
        } else if (this.age <= HOLD_TICKS) {
            ramp = 1.0F;
        } else {
            ramp = 1.0F - (float) (this.age - HOLD_TICKS) / (float) FADE_OUT_TICKS;
        }
        this.volume = VOLUME * Mth.clamp(ramp, 0.0F, 1.0F);
    }
}
