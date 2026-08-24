/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.PlanetShapeAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class PlanetShapePlayerMixin
implements PlanetShapeAccess {
    @Unique
    private static final EntityDataAccessor<String> GMOM_PLANET_SHAPE = SynchedEntityData.m_135353_(Player.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);

    @Inject(method={"defineSynchedData"}, at={@At(value="TAIL")})
    private void gmom$definePlanetShape(CallbackInfo ci) {
        ((Player)this).m_20088_().m_135372_(GMOM_PLANET_SHAPE, (Object)"");
    }

    @Override
    public String gmom$getPlanetShape() {
        return (String)((Player)this).m_20088_().m_135370_(GMOM_PLANET_SHAPE);
    }

    @Override
    public void gmom$setPlanetShape(String id) {
        ((Player)this).m_20088_().m_135381_(GMOM_PLANET_SHAPE, (Object)(id == null ? "" : id));
    }

    @Override
    public boolean gmom$isPlanetShapeData(EntityDataAccessor<?> key) {
        return key.equals(GMOM_PLANET_SHAPE);
    }
}

