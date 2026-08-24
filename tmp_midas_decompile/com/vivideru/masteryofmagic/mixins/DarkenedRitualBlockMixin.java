/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity
 *  com.Polarice3.Goety.common.crafting.RitualRecipe
 *  com.Polarice3.Goety.common.ritual.EnchantItemRitual
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.EnchantItemRitual;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EnchantItemRitual.class}, remap=false)
public abstract class DarkenedRitualBlockMixin {
    @Inject(method={"isValid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lcom/Polarice3/Goety/common/blocks/entities/DarkAltarBlockEntity;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void gmom_blockDarkened(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem, List<?> remainingAdditionalIngredients, CallbackInfoReturnable<Boolean> cir) {
        EnchantItemRitual ritual = (EnchantItemRitual)this;
        RitualRecipe recipe = ritual.recipe;
        if (!(recipe.getEnchantment() instanceof DarkenedEnchantment)) {
            return;
        }
        if (!(activationItem.m_41720_() instanceof ArmorItem)) {
            cir.setReturnValue((Object)false);
            return;
        }
        if (DarkenedEnchantment.has(activationItem)) {
            cir.setReturnValue((Object)false);
            return;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)activationItem.m_41720_());
        if (id != null && "goety".equals(id.m_135827_())) {
            cir.setReturnValue((Object)false);
        }
    }
}

