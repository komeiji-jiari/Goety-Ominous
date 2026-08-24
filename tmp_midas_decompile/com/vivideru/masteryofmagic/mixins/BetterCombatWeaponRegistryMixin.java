/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import java.lang.reflect.Method;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"net.bettercombat.logic.WeaponRegistry"}, remap=false)
public abstract class BetterCombatWeaponRegistryMixin {
    @Unique
    private static Method goetyMasteryOfMagic$getAttributesMethod;
    @Unique
    private static boolean goetyMasteryOfMagic$attributesMethodResolved;

    @Inject(method={"getAttributes(Lnet/minecraft/world/item/ItemStack;)Lnet/bettercombat/api/WeaponAttributes;"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private static void goetyMasteryOfMagic$inheritSelectedWandProfile(ItemStack stack, CallbackInfoReturnable<Object> cir) {
        if (!(stack.m_41720_() instanceof MasterStaffItem)) {
            return;
        }
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(stack);
        if (selectedWand.m_41619_()) {
            return;
        }
        try {
            Method method = BetterCombatWeaponRegistryMixin.goetyMasteryOfMagic$getAttributesMethod();
            if (method != null) {
                cir.setReturnValue(method.invoke(null, selectedWand));
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    @Unique
    private static Method goetyMasteryOfMagic$getAttributesMethod() {
        if (!goetyMasteryOfMagic$attributesMethodResolved) {
            goetyMasteryOfMagic$attributesMethodResolved = true;
            try {
                Class<?> registry = Class.forName("net.bettercombat.logic.WeaponRegistry");
                goetyMasteryOfMagic$getAttributesMethod = registry.getMethod("getAttributes", ItemStack.class);
            }
            catch (ReflectiveOperationException ignored) {
                goetyMasteryOfMagic$getAttributesMethod = null;
            }
        }
        return goetyMasteryOfMagic$getAttributesMethod;
    }
}

