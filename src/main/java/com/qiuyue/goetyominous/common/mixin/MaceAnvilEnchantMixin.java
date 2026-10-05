package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.MaceItem;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class MaceAnvilEnchantMixin {
    @Redirect(method = "createResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"), require = 1)
    private boolean goetyominous$maceBookEnchant(Enchantment enchantment, ItemStack stack) {
        if (enchantment.canEnchant(stack)) {
            return true;
        }
        return stack.getItem() instanceof MaceItem
                && (enchantment == Enchantments.SMITE
                || enchantment == Enchantments.BANE_OF_ARTHROPODS
                || enchantment == Enchantments.FIRE_ASPECT);
    }
}
