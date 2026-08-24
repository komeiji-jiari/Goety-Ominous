/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 */
package com.vivideru.masteryofmagic.client.sound;

import com.vivideru.masteryofmagic.entity.PhilosopherWindSlashEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class PhilosopherWindSlashSoundInstance
extends AbstractTickableSoundInstance {
    private final PhilosopherWindSlashEntity slash;

    public PhilosopherWindSlashSoundInstance(PhilosopherWindSlashEntity slash) {
        super((SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_PHILOSOPHER_SLASH_LOOP.get(), SoundSource.HOSTILE, RandomSource.m_216327_());
        this.slash = slash;
        this.f_119578_ = true;
        this.f_119579_ = 0;
        this.f_119573_ = 0.35f;
        this.f_119574_ = 1.0f;
        this.f_119580_ = SoundInstance.Attenuation.LINEAR;
        this.f_119582_ = false;
        this.f_119575_ = slash.m_20185_();
        this.f_119576_ = slash.m_20186_();
        this.f_119577_ = slash.m_20189_();
    }

    public void m_7788_() {
        if (this.slash.m_213877_() || !this.slash.m_6084_()) {
            this.m_119609_();
            return;
        }
        this.f_119575_ = this.slash.m_20185_();
        this.f_119576_ = this.slash.m_20186_();
        this.f_119577_ = this.slash.m_20189_();
    }
}

