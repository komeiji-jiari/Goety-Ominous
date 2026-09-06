package com.qiuyue.goetyominous.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.CandicornServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

/**
 * 糖果独角兽仆从冲刺持续循环音:自 Alex's Caves CandicornSound 原样复制,仅把 CandicornEntity
 * 改成 CandicornServant。冲刺(isCharging)期间跟随实体并随移动速度淡入淡出(静止 moveFade 上升音量归零),
 * 停止冲刺即 self-stop。
 */
public class CandicornServantChargeSound extends AbstractTickableSoundInstance {

    private final CandicornServant servant;

    private float moveFade = 0;

    public CandicornServantChargeSound(CandicornServant servant) {
        super(ACSoundRegistry.CANDICORN_CHARGE_LOOP.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
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
        return this.servant.isAlive() && !this.servant.isSilent() && this.servant.isCharging();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.servant.isAlive() && this.servant.isCharging()) {
            this.x = this.servant.getX();
            this.y = this.servant.getY();
            this.z = this.servant.getZ();
            float f = (float) this.servant.getDeltaMovement().length();
            this.pitch = 1.0F;
            if (f <= 0.01F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            } else {
                this.moveFade = Math.max(0F, this.moveFade - 0.05F);
            }
            this.volume = 1.0F - this.moveFade;
        } else {
            this.stop();
        }
    }

    public boolean isSameEntity(CandicornServant servant) {
        return this.servant.isAlive() && this.servant.getId() == servant.getId();
    }

    public void stopSound() {
        this.stop();
    }
}
