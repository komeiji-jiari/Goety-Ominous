package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SingleStackItem.class)
public class SingleStackItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void goetyominous$croneHatInfo(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
        if (stack.is(ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety:crone_hat")))) {
            tooltip.add(Component.translatable("info.goetyominous.crone_hat").withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("info.goetyominous.crone_hat_cast").withStyle(ChatFormatting.BLUE));
        }
    }
}
