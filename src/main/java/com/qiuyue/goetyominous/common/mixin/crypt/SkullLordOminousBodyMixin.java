package com.qiuyue.goetyominous.common.mixin.crypt;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.qiuyue.goetyominous.common.entities.hostile.OminousSkullLord;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkullLord.class)
public class SkullLordOminousBodyMixin {

    @Redirect(method = "tick", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    private Entity goetyominous$tickBody(EntityType<?> type, Level level) {
        return this.goetyominous$createBody(type, level);
    }

    @Redirect(method = "finalizeSpawn", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    private Entity goetyominous$spawnBody(EntityType<?> type, Level level) {
        return this.goetyominous$createBody(type, level);
    }

    @Redirect(method = "tick", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void goetyominous$replaceSmoke(Level level, ParticleOptions particle,
                                           double x, double y, double z,
                                           double dx, double dy, double dz) {
        if ((Object) this instanceof OminousSkullLord skullLord
                && !skullLord.isDeadOrDying()
                && (particle == ParticleTypes.SMOKE || particle == ParticleTypes.POOF)) {
            return;
        }
        level.addParticle(particle, x, y, z, dx, dy, dz);
    }

    @Unique
    private Entity goetyominous$createBody(EntityType<?> type, Level level) {
        if ((Object) this instanceof OminousSkullLord && type == ModEntityType.BONE_LORD.get()) {
            return ModEntityTypes.OMINOUS_BONE_LORD.get().create(level);
        }
        return type.create(level);
    }
}
