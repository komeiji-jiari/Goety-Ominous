package com.qiuyue.someillagerservants.common.mixin;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MutantMoreItemMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void someillagerservants$increaseStackSize(CallbackInfoReturnable<Integer> cir) {
        String itemId = ForgeRegistries.ITEMS.getKey((Item) (Object) this).toString();
        if ("mutantmore:formula_y".equals(itemId) || "mutantmore:compound_z".equals(itemId)) {
            cir.setReturnValue(3);
        }
    }
}
