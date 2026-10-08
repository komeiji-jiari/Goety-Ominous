package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.WaterJetSpell;
import com.Polarice3.Goety.utils.MathHelper;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterJetSpell.class)
public class WaterJetSpellKeyOfRlyehMixin {

    @Unique
    private ItemStack goetyominous$waterJetStaff = ItemStack.EMPTY;

    @Inject(method = "SpellResult", at = @At("HEAD"), remap = false)
    private void goetyominous$captureStaff(ServerLevel worldIn, LivingEntity caster,
                                           ItemStack staff, SpellStat spellStat, CallbackInfo ci) {
        this.goetyominous$waterJetStaff = staff;
    }

    @ModifyArg(method = "SpellResult",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
            require = 1)
    private MobEffectInstance goetyominous$keyOfRlyehSappedLevel(MobEffectInstance original) {
        if (!KeyOfRlyehMixinHelper.isKeyOfRlyeh(this.goetyominous$waterJetStaff)) {
            return original;
        }
        return new MobEffectInstance(GoetyEffects.SAPPED.get(),
                MathHelper.secondsToTicks(5), 1);
    }
}
