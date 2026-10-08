package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.entities.projectiles.BouncyBubble;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.BouncyBubbleSpell;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BouncyBubbleSpell.class)
public class BouncyBubbleSpellKeyOfRlyehMixin {

    @Unique
    private int goetyominous$bubblePotency;
    @Unique
    private int goetyominous$bubbleVelocity;
    @Unique
    private int goetyominous$bubbleRadius;

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "potency", require = 1, remap = false)
    private int goetyominous$capturePotency(int potency) {
        this.goetyominous$bubblePotency = potency;
        return potency;
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "velocity", require = 1, remap = false)
    private int goetyominous$captureVelocity(int velocity) {
        this.goetyominous$bubbleVelocity = velocity;
        return velocity;
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "radius", require = 1, remap = false)
    private int goetyominous$captureRadius(int radius) {
        this.goetyominous$bubbleRadius = radius;
        return radius;
    }

    @Inject(method = "SpellResult", at = @At("RETURN"), remap = false)
    private void goetyominous$keyOfRlyehExtraBubble(ServerLevel worldIn, LivingEntity caster,
                                                    ItemStack staff, SpellStat spellStat, CallbackInfo ci) {
        if (!KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff)) {
            return;
        }

        Vec3 vector3d = caster.getLookAngle();
        BouncyBubble extra = new BouncyBubble(
                caster.getX() + vector3d.x / 2.0D + worldIn.random.nextGaussian(),
                caster.getEyeY() - 0.2D,
                caster.getZ() + vector3d.z / 2.0D + worldIn.random.nextGaussian(),
                vector3d.x, vector3d.y, vector3d.z, worldIn);
        extra.shoot(vector3d);
        extra.setOwner(caster);
        extra.setExtraDamage((float) this.goetyominous$bubblePotency);
        extra.setBoltSpeed(this.goetyominous$bubbleVelocity);
        extra.setSize((float) this.goetyominous$bubbleRadius);
        worldIn.addFreshEntity(extra);
    }
}
