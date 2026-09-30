package com.qiuyue.goetyominous.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.events.TremorzillaBreathHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class TremorzillaBreathLoopSound extends AbstractTickableSoundInstance {

    private final int casterId;

    public TremorzillaBreathLoopSound(int casterId, Entity caster) {
        super(ACSoundRegistry.TREMORZILLA_BEAM_LOOP.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.casterId = casterId;
        this.x = (float) caster.getX();
        this.y = (float) caster.getY();
        this.z = (float) caster.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 8.0F;
        this.pitch = 1.0F;
    }

    public int getCasterId() {
        return this.casterId;
    }

    @Override
    public void tick() {
        Entity caster = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(this.casterId);
        if (caster == null || !caster.isAlive() || !TremorzillaBreathHandler.hasClientBeam(this.casterId)) {
            this.stop();
            return;
        }
        this.x = (float) caster.getX();
        this.y = (float) caster.getY();
        this.z = (float) caster.getZ();
        this.pitch = 1.0F;
        this.volume = 8.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.isStopped();
    }

    public void stopSound() {
        this.stop();
    }
}
