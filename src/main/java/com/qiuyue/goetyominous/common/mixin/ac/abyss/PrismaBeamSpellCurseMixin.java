package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.PrismaBeamSpell;
import com.qiuyue.goetyominous.utils.ac.CurseOfRlyehHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrismaBeamSpell.class)
public class PrismaBeamSpellCurseMixin {

    @Unique
    private ItemStack goetyominous$curseStaff = ItemStack.EMPTY;

    @Inject(method = "SpellResult", at = @At("HEAD"), remap = false)
    private void goetyominous$captureCurseStaff(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
                                                SpellStat spellStat, CallbackInfo ci) {
        this.goetyominous$curseStaff = staff;
    }

    @Redirect(method = "SpellResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 0), require = 1)
    private boolean goetyominous$curseOnFirstHit(LivingEntity target, DamageSource source, float amount) {
        float modified = CurseOfRlyehHelper.amplifyKeySpellDamage(target, amount);
        boolean hurt = target.hurt(source, modified);
        if (hurt) {
            CurseOfRlyehHelper.applyCurseOnHit(target, this.goetyominous$curseStaff);
        }
        return hurt;
    }

    @Redirect(method = "SpellResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 1), require = 1)
    private boolean goetyominous$curseOnSecondHit(LivingEntity target, DamageSource source, float amount) {
        float modified = CurseOfRlyehHelper.amplifyKeySpellDamage(target, amount);
        boolean hurt = target.hurt(source, modified);
        if (hurt) {
            CurseOfRlyehHelper.applyCurseOnHit(target, this.goetyominous$curseStaff);
        }
        return hurt;
    }
}
