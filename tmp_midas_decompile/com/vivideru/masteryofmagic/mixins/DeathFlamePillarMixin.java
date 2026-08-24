/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.util.FirePillar
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.entities.util.FirePillar;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FirePillar.class}, remap=false)
public class DeathFlamePillarMixin {
    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", remap=true), remap=false, require=0)
    private int gmom$darkPillarParticles(ServerLevel level, ParticleOptions original, double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
        FirePillar self = (FirePillar)this;
        if (!self.m_19880_().contains("gmom_death_flame")) {
            return level.m_8767_(original, x, y, z, count, dx, dy, dz, speed);
        }
        return level.m_8767_((ParticleOptions)new DustParticleOptions(new Vector3f(0.03f, 0.03f, 0.05f), 1.35f), x, y, z, 5, 0.2, 0.35, 0.2, 0.012);
    }
}

