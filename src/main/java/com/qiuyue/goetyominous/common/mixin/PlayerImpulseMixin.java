package com.qiuyue.goetyominous.common.mixin;

import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge;
import com.qiuyue.goetyominous.utils.WindChargeImpulse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerImpulseMixin implements WindChargeImpulse {
    @Unique private Vec3 goetyominous$currentImpulseImpactPos;
    @Unique private Entity goetyominous$currentExplosionCause;
    @Unique private boolean goetyominous$ignoreFallDamageFromCurrentExplosion;
    @Unique private int goetyominous$currentImpulseContextResetGraceTime;
    @Unique private boolean goetyominous$spawnExtraParticlesOnFall;

    @Override
    public void onExplosionHitImpulse(@Nullable Entity source) {
        Player self = (Player) (Object) this;
        this.goetyominous$currentImpulseImpactPos = self.position();
        this.goetyominous$currentExplosionCause = source;
        this.setIgnoreFallDamageFromCurrentImpulse(source instanceof AbstractWindCharge windCharge
                && windCharge.grantsFallDamageImmunity());
    }

    @Override
    public void setIgnoreFallDamageFromCurrentImpulse(boolean ignore) {
        this.goetyominous$ignoreFallDamageFromCurrentExplosion = ignore;
        this.goetyominous$currentImpulseContextResetGraceTime = ignore ? 40 : 0;
    }

    @Override
    public boolean getIgnoreFallDamageFromCurrentExplosion() {
        return this.goetyominous$ignoreFallDamageFromCurrentExplosion;
    }

    @Unique
    private void goetyominous$decrementGrace() {
        if (this.goetyominous$currentImpulseContextResetGraceTime == 0) {
            this.goetyominous$resetImpulseContext();
        }
    }

    @Unique
    private void goetyominous$resetImpulseContext() {
        this.goetyominous$currentImpulseContextResetGraceTime = 0;
        this.goetyominous$currentExplosionCause = null;
        this.goetyominous$currentImpulseImpactPos = null;
        this.goetyominous$ignoreFallDamageFromCurrentExplosion = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void goetyominous$tickImpulse(CallbackInfo ci) {
        if (this.goetyominous$currentImpulseContextResetGraceTime > 0) {
            --this.goetyominous$currentImpulseContextResetGraceTime;
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float goetyominous$impulseFallDamage(float fallDistance) {
        if (this.goetyominous$ignoreFallDamageFromCurrentExplosion && this.goetyominous$currentImpulseImpactPos != null) {
            Player self = (Player) (Object) this;
            double impactY = this.goetyominous$currentImpulseImpactPos.y;
            this.goetyominous$decrementGrace();
            if (impactY < self.getY()) {
                return 0.0F;
            }
            return Math.min(fallDistance, (float) (impactY - self.getY()));
        }
        return fallDistance;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void goetyominous$readImpulse(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("current_explosion_impact_pos", 9)) {
            Vec3.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("current_explosion_impact_pos"))
                    .resultOrPartial(error -> {}).ifPresent(pos -> this.goetyominous$currentImpulseImpactPos = pos);
        }
        this.goetyominous$ignoreFallDamageFromCurrentExplosion = tag.getBoolean("ignore_fall_damage_from_current_explosion");
        this.goetyominous$currentImpulseContextResetGraceTime = tag.getInt("current_impulse_context_reset_grace_time");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void goetyominous$writeImpulse(CompoundTag tag, CallbackInfo ci) {
        if (this.goetyominous$currentImpulseImpactPos != null) {
            Vec3.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.goetyominous$currentImpulseImpactPos)
                    .resultOrPartial(error -> {}).ifPresent(data -> tag.put("current_explosion_impact_pos", data));
        }
        tag.putBoolean("ignore_fall_damage_from_current_explosion", this.goetyominous$ignoreFallDamageFromCurrentExplosion);
        tag.putInt("current_impulse_context_reset_grace_time", this.goetyominous$currentImpulseContextResetGraceTime);
    }

    @Override
    public void onMaceSmashImpact() {
        Player self = (Player) (Object) this;
        if (this.goetyominous$ignoreFallDamageFromCurrentExplosion && this.goetyominous$currentImpulseImpactPos != null) {
            if (this.goetyominous$currentImpulseImpactPos.y > self.getY()) {
                this.goetyominous$currentImpulseImpactPos = self.position();
            }
        } else {
            this.goetyominous$currentImpulseImpactPos = self.position();
        }
        this.setIgnoreFallDamageFromCurrentImpulse(true);
    }

    @Override
    public void setSpawnExtraParticlesOnFall(boolean spawn) {
        this.goetyominous$spawnExtraParticlesOnFall = spawn;
    }

    @Override
    public boolean getSpawnExtraParticlesOnFall() {
        return this.goetyominous$spawnExtraParticlesOnFall;
    }
}
