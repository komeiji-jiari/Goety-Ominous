/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.client.ForgeHooksClient
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.client.DarkenedArmorTextures;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import com.vivideru.masteryofmagic.enchantment.LightenedEnchantment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ForgeHooksClient.class})
public abstract class ForgeHooksClientMixin {
    @Inject(method={"getArmorModel"}, at={@At(value="RETURN")}, remap=false)
    private static void gmom_dbg_getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> def, CallbackInfoReturnable<Model> cir) {
    }

    @Inject(method={"getArmorTexture"}, at={@At(value="HEAD")}, remap=false)
    private static void gmom_dbg_getArmorTexture_head(Entity entity, ItemStack stack, String defaultTexture, EquipmentSlot slot, String type, CallbackInfoReturnable<String> cir) {
    }

    @Inject(method={"getArmorTexture"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private static void gmom_darken(Entity entity, ItemStack stack, String defaultTexture, EquipmentSlot slot, String type, CallbackInfoReturnable<String> cir) {
        ResourceLocation base;
        String returned = (String)cir.getReturnValue();
        boolean has = DarkenedEnchantment.has(stack);
        boolean light = LightenedEnchantment.has(stack);
        if (!has || light) {
            return;
        }
        if (returned == null || returned.isEmpty()) {
            return;
        }
        try {
            base = new ResourceLocation(returned);
        }
        catch (Exception e) {
            return;
        }
        ResourceLocation dark = DarkenedArmorTextures.getOrCreate(base);
        cir.setReturnValue((Object)dark.toString());
    }
}

