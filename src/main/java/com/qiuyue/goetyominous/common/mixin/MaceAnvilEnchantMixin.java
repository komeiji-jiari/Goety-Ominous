package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.MaceItem;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MaceAnvilEnchantMixin {

    @Inject(method = "createResult", at = @At("HEAD"))
    private void goetyominous$beginMaceAnvil(CallbackInfo ci) {
        MaceItem.beginAnvilEnchantCheck();
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void goetyominous$endMaceAnvil(CallbackInfo ci) {
        MaceItem.endAnvilEnchantCheck();
    }
}
