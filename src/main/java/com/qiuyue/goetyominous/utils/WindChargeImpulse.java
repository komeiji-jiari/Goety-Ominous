package com.qiuyue.goetyominous.utils;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface WindChargeImpulse {
    void setIgnoreFallDamageFromCurrentImpulse(boolean ignore);

    boolean getIgnoreFallDamageFromCurrentExplosion();

    void onExplosionHitImpulse(@Nullable Entity source);

    void onMaceSmashImpact();

    void setSpawnExtraParticlesOnFall(boolean spawn);

    boolean getSpawnExtraParticlesOnFall();

    static void onExplosionHit(Entity entity, @Nullable Entity source) {
        if (entity instanceof WindChargeImpulse impulse) {
            impulse.onExplosionHitImpulse(source);
        }
    }
}
